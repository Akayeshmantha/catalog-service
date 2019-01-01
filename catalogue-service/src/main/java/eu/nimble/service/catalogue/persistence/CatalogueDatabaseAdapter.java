package eu.nimble.service.catalogue.persistence;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.nimble.service.catalogue.persistence.util.PartyTypePersistenceUtil;
import eu.nimble.service.catalogue.util.SpringBridge;
import eu.nimble.service.model.ubl.commonaggregatecomponents.PartyType;
import eu.nimble.service.model.ubl.commonaggregatecomponents.QualityIndicatorType;
import eu.nimble.service.model.ubl.commonbasiccomponents.QuantityType;
import eu.nimble.utility.JsonSerializationUtility;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DataModelUtility;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by suat on 07-Aug-18.
 */
public class CatalogueDatabaseAdapter {
    private static final Logger logger = LoggerFactory.getLogger(CatalogueDatabaseAdapter.class);

    public static void syncPartyInUBLDB(String partyId, String bearerToken) {
        PartyType catalogueParty = PartyTypePersistenceUtil.getPartyById(partyId);
        PartyType identityParty;
        try {
            identityParty = SpringBridge.getInstance().getIdentityClientTyped().getParty(bearerToken, partyId);
            identityParty = checkPartyIntegrity(identityParty);
        } catch (IOException e) {
            String msg = String.format("Failed to get party with id: %s", partyId);
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        if(catalogueParty == null) {
            SpringBridge.getInstance().getGenericJPARepository().persistEntity(identityParty);

        } else {
            DataModelUtility.nullifyPartyFieldsExceptHjid(catalogueParty);
            DataModelUtility.copyPartyExceptHjid(catalogueParty, identityParty);
            SpringBridge.getInstance().getGenericJPARepository().updateEntity(catalogueParty);
        }
    }

    public static void syncTrustScores(String partyId, String accessToken) {
        PartyType trustParty;
        InputStream partyStream;
        String partyStr;

        try {
            partyStream = SpringBridge.getInstance().getTrustClient().obtainPartyTrustValues(partyId, accessToken).body().asInputStream();
            partyStr = IOUtils.toString(partyStream);
        } catch (IOException e) {
            logger.error("Failed to obtain party with id: {} from trust service", partyId, e);
            return;
        }

        try {
            trustParty = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readValue(partyStr, PartyType.class);
        }
        catch (Exception e){
            logger.error("Failed to deserialize party with id: {}, serialization: {}", partyId, partyStr, e);
            return;
        }

        PartyType catalogueParty = PartyTypePersistenceUtil.getPartyById(trustParty.getID());
        if(catalogueParty == null) {
            logger.warn("No party available in UBLDB with id: {}", partyId);
            return;
        }

        if(catalogueParty.getQualityIndicator() == null) {
            catalogueParty.setQualityIndicator(new ArrayList<>());
        }

        for(QualityIndicatorType qualityIndicator : trustParty.getQualityIndicator()) {
            if(qualityIndicator.getQuantity() == null) {
                continue;
            }

            // update existing indicators
            boolean indicatorExists = false;
            for(QualityIndicatorType qualityIndicatorExisting : catalogueParty.getQualityIndicator()) {
                if(qualityIndicator.getQualityParameter() != null && qualityIndicatorExisting.getQualityParameter() != null &&
                        qualityIndicator.getQualityParameter().contentEquals(qualityIndicatorExisting.getQualityParameter())) {
                    if(qualityIndicatorExisting.getQuantity() == null) {
                        qualityIndicatorExisting.setQuantity(new QuantityType());
                    }
                    qualityIndicatorExisting.getQuantity().setValue(qualityIndicator.getQuantity().getValue());
                    indicatorExists = true;
                    break;
                }
            }
            // create new indicator
            if(!indicatorExists) {
                QualityIndicatorType indicator = new QualityIndicatorType();
                indicator.setQualityParameter(qualityIndicator.getQualityParameter());
                QuantityType quantity = new QuantityType();
                quantity.setValue(qualityIndicator.getQuantity().getValue());
                indicator.setQuantity(quantity);
                catalogueParty.getQualityIndicator().add(indicator);
            }
        }

        SpringBridge.getInstance().getGenericJPARepository().updateEntity(catalogueParty);
    }

    public static PartyType syncPartyInUBLDB(PartyType party) {
        if (party == null) {
            return null;
        }
        PartyType catalogueParty = PartyTypePersistenceUtil.getPartyById(party.getID());
        if(catalogueParty != null) {
            return catalogueParty;
        } else {
            party = checkPartyIntegrity(party);
            SpringBridge.getInstance().getGenericJPARepository().persistEntity(party);
            return party;
        }
    }

    private static PartyType checkPartyIntegrity(PartyType party) {
        party = removePartyHjids(party);
        // TODO do not store any other party information in ubldb than ID. Todo that the user interface and
        // other places relying on the party information (stored in ubldb) should be checked/updated
        return party;
    }

    private static PartyType removePartyHjids(PartyType party) {
        try {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JSONObject object = new JSONObject(objectMapper.writeValueAsString(party));
        JsonSerializationUtility.removeHjidFields(object);
        party = objectMapper.readValue(object.toString(), PartyType.class);
        return party;
        } catch (IOException e) {
            String msg = String.format("Failed to remove hjid fields from the party with id: %s", party.getID());
            logger.error(msg, e);
            throw new RuntimeException(msg, e);
        }
    }
}
