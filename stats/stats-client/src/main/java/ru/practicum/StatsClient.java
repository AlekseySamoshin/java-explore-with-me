package ru.practicum;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class StatsClient {
    private static final String URL = "http://stats-server:9090";
    private final RestTemplate restTemplate = new RestTemplate();

    private final WebClient webClient = WebClient.create(URL);

    public EndpointHitDto saveHit(EndpointHitDto endpointHitDto) {
        return webClient.post()
                .uri("/hit")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Mono.just(endpointHitDto), EndpointHitDto.class)
                .retrieve()
                .bodyToMono(EndpointHitDto.class)
                .block();
    }

    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        return List.of(webClient.get()
                .uri(uriWithParams -> uriWithParams.path("/stats")
                        .queryParam("start", start)
                        .queryParam("end", end)
                        .queryParam("uris", uris)
                        .queryParam("unique", unique)
                        .build())
                .retrieve()
                .bodyToMono(ViewStatsDto[].class)
                .block());
    }
}