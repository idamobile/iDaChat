package com.idamobile.platform.chatbot;

import com.idamobile.platform.light.core.ws.client.WsEndpointClient;
import com.idamobile.platform.light.core.ws.client.WsEndpointClientImpl;
import com.idamobile.platform.light.core.ws.dto.WsCurrencyRateDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Slf4j
public class Experiment {
    public static final String SERVICE_NAME = "WsEndpointServiceFacade";
    public static final String ENDPOINT_NAME = "WsEndpointServiceFacadeHttpSoap12Endpoint";
    public static final String SERVICE_URL = "http://dev.idamob.ru/light/services/WsEndpointServiceFacade?wsdl";

    public static void main(String[] args) {
        WsEndpointClient client = new WsEndpointClientImpl(SERVICE_URL, SERVICE_NAME, ENDPOINT_NAME);

        Map<String, List<WsCurrencyRateDTO>> index = new HashMap<>();
        Arrays.stream(client.getCurrencyRates().getRates()).forEach(rate -> {
            if (!index.containsKey(rate.getType())) {
                index.put(rate.getType(), new LinkedList<>());
            }
            index.get(rate.getType()).add(rate);
        });

        log.info("{}", Arrays.asList(client.getCurrencyRates().getRates()));



    }
}
