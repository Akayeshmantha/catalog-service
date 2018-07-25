package eu.nimble.service.catalogue.federation;


import feign.Headers;
import feign.Param;
import feign.RequestLine;
import feign.Response;


@Headers("Content-Type: application/json")
public interface CatalogueServiceClient {

    @RequestLine("GET delegate/catalogue/{standard}/{uuid}?initiatorInstanceId={initiatorInstanceId}&targetInstanceId={targetInstanceId}")
    @Headers("Authorization: {bearerToken}")
    public Response clientGetCatalogue(
            @Param("standard") String standard,
            @Param("uuid") String uuid,
            @Param("initiatorInstanceId") String initiatorInstanceId,
            @Param("targetInstanceId") String targetInstanceId,
            @Param("bearerToken") String bearerToken) ;




    @RequestLine("GET delegate/catalogue/{catalogueUuid}/catalogueline/{lineId}?initiatorInstanceId={initiatorInstanceId}&targetInstanceId={targetInstanceId}")
    @Headers("Authorization: {bearerToken}")
    public Response clientGetCatalogueLine(
            @Param("catalogueUuid") String catalogueUuid,
            @Param("lineId") String lineId,
            @Param("initiatorInstanceId") String initiatorInstanceId,
            @Param("targetInstanceId") String targetInstanceId,
            @Param("bearerToken") String bearerToken) ;




    @RequestLine("GET delegate/catalogue/{partyId}/default?initiatorInstanceId={initiatorInstanceId}&targetInstanceId={targetInstanceId}")
    @Headers("Authorization: {bearerToken}")
    public Response clientGetDefaultCatalogue(
                                                     @Param("partyId") String partyId,
                                                     @Param("initiatorInstanceId") String initiatorInstanceId,
                                                     @Param("targetInstanceId") String targetInstanceId,
                                                     @Param("bearerToken") String bearerToken) ;




}





