package training.apiapp.api;

import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

import static java.util.Map.entry;

@RestController
class AdminResource {

    @GetMapping("/api/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> admin(Authentication auth) {
        var payload = Map.ofEntries( //
                entry("message", "Admin " + auth.getName()), //
                entry("user", auth.getName()), //
                entry("roles", auth.getAuthorities()), //
                entry("time", Instant.now()) //
        );
        LoggerFactory.getLogger(getClass()).info("Admin request: {}", payload);
        return ResponseEntity.ok(payload);
    }
}
