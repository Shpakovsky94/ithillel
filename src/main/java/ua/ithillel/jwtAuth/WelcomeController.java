package ua.ithillel.jwtAuth;

import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody
public class WelcomeController {

    @GetMapping()
    public ResponseEntity<String> welcome() {
        return ResponseEntity.ok("<h1>welcome me</h1>");
    }
  @GetMapping()
    public ResponseEntity<String> test(HttpRequest httpRequest) {
      System.out.print(httpRequest.toString());
        return ResponseEntity.ok("<h1>welcome me</h1>");
    }
}
