package com.sarichi.crocheting.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/api/web/";
    }

    @GetMapping({ "/rifa", "/rifa/" })
    public String redirectRifa() {
        return "redirect:/rifa/index.html";
    }

    @GetMapping({ "/qa", "/qa/", "/qa/rifa-informe", "/qa/rifa-informe/" })
    public String redirectQa() {
        return "redirect:/qa/rifa-informe/index.html";
    }
}
