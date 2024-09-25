package training.apiapp.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static java.util.Map.entry;

@RestController
class GreetingResource {

    @GetMapping("/api/greetings/me")
    public ResponseEntity<?> greeting(Authentication auth) {
        var payload = Map.ofEntries( //
                entry("message", "Hello " + auth.getName()), //
                entry("user", auth.getName()), //
                entry("roles", auth.getAuthorities()), //
                entry("time", Instant.now()) //
        );
        LoggerFactory.getLogger(getClass()).info("Greeting request: {}", payload);
        return ResponseEntity.ok(payload);
    }
}
