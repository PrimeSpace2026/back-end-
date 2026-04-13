package primespace.demo.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final DataSource dataSource;

    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/api/wake")
    public ResponseEntity<Map<String, String>> wake() {
        return ResponseEntity.ok(Map.of("status", "awake"));
    }

    @GetMapping("/api/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    @GetMapping("/api/debug-db")
    public ResponseEntity<Map<String, String>> debugDb() {
        Map<String, String> info = new HashMap<>();
        try {
            var conn = dataSource.getConnection();
            info.put("status", "connected");
            info.put("url", conn.getMetaData().getURL());
            info.put("user", conn.getMetaData().getUserName());
            conn.close();
        } catch (Exception e) {
            info.put("status", "error");
            info.put("error", e.getMessage());
            if (e.getCause() != null) {
                info.put("cause", e.getCause().getMessage());
            }
        }
        info.put("db_url_env", System.getenv("DB_URL") != null ? "SET (" + System.getenv("DB_URL").substring(0, Math.min(30, System.getenv("DB_URL").length())) + "...)" : "NOT SET");
        info.put("db_user_env", System.getenv("DB_USER") != null ? System.getenv("DB_USER") : "NOT SET");
        info.put("db_pass_env", System.getenv("DB_PASS") != null ? "SET (length=" + System.getenv("DB_PASS").length() + ")" : "NOT SET");
        return ResponseEntity.ok(info);
    }

    @GetMapping("/api/debug-tables")
    public ResponseEntity<?> debugTables() {
        List<Map<String, String>> tables = new ArrayList<>();
        try (var conn = dataSource.getConnection();
             var rs = conn.getMetaData().getTables(null, "public", "%", new String[]{"TABLE"})) {
            while (rs.next()) {
                Map<String, String> t = new HashMap<>();
                t.put("name", rs.getString("TABLE_NAME"));
                tables.add(t);
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
        return ResponseEntity.ok(tables);
    }
}
