package eu.nimble.service.catalogue.impl;


import eu.nimble.service.catalogue.federation.CatalogueServiceClient;
import eu.nimble.service.catalogue.federation.ClientFactory;
import eu.nimble.service.catalogue.federation.CoreFunctions;
import eu.nimble.utility.config.CatalogueServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@Controller
@RequestMapping(value="/delegate")
public class DelegateController {


    //DELEGATE WRAPPER FOR SOME SERVİCES..


    @Autowired
    private CatalogueController catalogueController;

    @Autowired
    private CatalogueLineController catalogueLineController;

    @Autowired
    private CoreFunctions core;

    @Autowired
    private CatalogueServiceConfig config;



    public CatalogueServiceClient clientGenerator(String instanceid){
        String url=core.getEndpointFromInstanceId(instanceid);
        return ClientFactory.getClientFactoryInstance().createClient(CatalogueServiceClient.class,url);
    }



    @RequestMapping(value = "/catalogue/{partyId}/default",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity delegateGetDefaultCatalogue(@PathVariable String partyId,
                                                      @RequestParam(value="targetInstanceId" ,required = true)String targetInstanceId,
                                                      @RequestParam(value = "initiatorInstanceId", required = true) String initiatorInstanceId,
                                                      @RequestHeader(value="Authorization", required=true) String bearerToken ) throws Exception{
        if(config.getInstanceid().equals(targetInstanceId))
            return this.catalogueController.getDefaultCatalogue(partyId,bearerToken);
        else
            return ClientFactory.getClientFactoryInstance().createResponseEntity(clientGenerator(targetInstanceId).clientGetDefaultCatalogue(partyId,initiatorInstanceId,targetInstanceId,bearerToken));

    }

    @RequestMapping(value = "/catalogue/{standard}/{uuid}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity delegateGetCatalogue(@PathVariable String standard,
                                               @PathVariable String uuid,
                                               @RequestParam(value="targetInstanceId" ,required = true)String targetInstanceId,
                                               @RequestParam(value = "initiatorInstanceId", required = true) String initiatorInstanceId,
                                               @RequestHeader(value="Authorization", required=true) String bearerToken) throws Exception{


        if(config.getInstanceid().equals(targetInstanceId))
            return this.catalogueController.getCatalogue(standard,uuid,bearerToken);
        else
            return ClientFactory.getClientFactoryInstance().createResponseEntity(clientGenerator(targetInstanceId).clientGetCatalogue(standard,uuid,initiatorInstanceId,targetInstanceId,bearerToken));
    }


    @RequestMapping(value = "/catalogue/{catalogueUuid}/catalogueline/{lineId}",
            produces = {"application/json"},
            method = RequestMethod.GET)
    public ResponseEntity delegateGetCatalogueLine(@PathVariable String catalogueUuid,
                                                   @PathVariable String lineId,
                                                   @RequestParam(value="targetInstanceId" ,required = true)String targetInstanceId,
                                                   @RequestParam(value = "initiatorInstanceId", required = true) String initiatorInstanceId,
                                                   @RequestHeader(value="Authorization", required=true) String bearerToken) throws Exception{


        if(config.getInstanceid().equals(targetInstanceId))
            return this.catalogueLineController.getCatalogueLine(catalogueUuid,lineId,bearerToken);
        else{
            return ClientFactory.getClientFactoryInstance().createResponseEntity(clientGenerator(targetInstanceId).clientGetCatalogueLine(catalogueUuid,lineId,initiatorInstanceId,targetInstanceId,bearerToken));

        }

    }




}
