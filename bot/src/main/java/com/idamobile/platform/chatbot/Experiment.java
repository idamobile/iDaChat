package com.idamobile.platform.chatbot;

import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.client.WsEndpointClientImpl;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Experiment {
    public static final String SERVICE_NAME = "WsEndpointServiceFacade";
    public static final String ENDPOINT_NAME = "WsEndpointServiceFacadeHttpSoap12Endpoint";
    public static final String SERVICE_URL = "http://dev.idamob.ru/light/services/WsEndpointServiceFacade?wsdl";

    public static void main(String[] args) {
        WsEndpointClient client = new WsEndpointClientImpl(SERVICE_URL, SERVICE_NAME, ENDPOINT_NAME);

//        WsGetNewsResponseDTO news = client.getNewsFeed(new WsGetNewsRequestDTO(1));
//        log.info("{}", news);
//
//        WsGetNearestLocationResponseDTO loc = client.getNearestLocation(new WsGetNearestLocationRequestDTO(55.4, 37.5));
//        log.info("{}", loc);

        Arrays.asList(client.getBanners().getBanners()).stream().forEach(b -> {
            System.out.println(b);
        });

    }
}
