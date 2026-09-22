package com.movieinsync.wayfinding.controller;

import com.movieinsync.wayfinding.model.RouteRequest;
import com.movieinsync.wayfinding.service.RouteService;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public Map<String, Object> findRoute(
            @RequestBody RouteRequest request) {

        return routeService.findRoute(request);
    }
}