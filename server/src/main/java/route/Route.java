package route;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import api.User;

@CrossOrigin
@RestController
public class Route {
    @GetMapping("/")
    public String home() {
        return "Welcome to my application!";
    }

    @GetMapping("/status")
    public String status() {
        return "Application is running!";
    }

    @PostMapping("/user")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        // 处理逻辑
        return ResponseEntity.ok("{\"message\":\"Success\"}");
    }
}
