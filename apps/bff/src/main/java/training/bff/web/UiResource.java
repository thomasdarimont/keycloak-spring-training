package training.bff.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
class UiResource {

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
