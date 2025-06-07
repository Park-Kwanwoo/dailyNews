package dev.park.dailynews.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SPAController implements ErrorController {

    @GetMapping("/error")
    public String forward() {
        return "forward:index.html";
    }
}
