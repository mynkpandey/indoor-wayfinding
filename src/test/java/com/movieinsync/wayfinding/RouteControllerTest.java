package com.movieinsync.wayfinding;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieinsync.wayfinding.model.RouteRequest;
import com.movieinsync.wayfinding.service.RouteService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RouteService routeService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    void shouldAcceptValidRouteRequest() throws Exception {

        when(routeService.findRoute(any(RouteRequest.class)))
                .thenReturn(Map.of(
                        "status", "SUCCESS",
                        "totalDistance", 75.0
                ));

        RouteRequest request = new RouteRequest();

        request.setStart("N1");
        request.setDestination("N6");
        request.setWheelchairAccessible(false);

        mockMvc.perform(
                post("/api/routes")
                        .with(
                                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .httpBasic("mynk", "mynk@23")
                        )
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value("SUCCESS"));
    }


    @Test
    void shouldRejectUnauthenticatedRequest() throws Exception {

        RouteRequest request = new RouteRequest();

        request.setStart("N1");
        request.setDestination("N6");

        mockMvc.perform(
                post("/api/routes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isUnauthorized());
    }


    @Test
    void shouldRejectInvalidRequest() throws Exception {

        RouteRequest request = new RouteRequest();

        request.setStart("");
        request.setDestination("");

        mockMvc.perform(
                post("/api/routes")
                        .with(
                                org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors
                                        .httpBasic("mynk", "mynk@23")
                        )
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest());
    }
}