package eu.nimble.service.catalogue.federation;


import eu.nimble.utility.config.CatalogueServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CoreFunctions {

    @Autowired
    private CatalogueServiceConfig config;

    public String getEndpointFromInstanceId(String instanceId){

        String coreUrl=config.getCoreEndpointUrl();

        /*
            TODO:
            A consumer in order to get the instance endpoint from the core url.
        */
        return "http://localhost:8081";
    }


    public boolean isValidToken(String instanceId, String bearerToken){

        String endpoint=this.getEndpointFromInstanceId(instanceId);
        /*
            TODO:
            A consumer in order to validate the token from instance endpoint.
        */
        return true;
    }

}
