package com.sarichi.crocheting.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/api/web/";
    }
}
