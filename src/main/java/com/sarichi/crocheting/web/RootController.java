package com.sarichi.crocheting.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectRoot() {
        return "redirect:/crocheting/";
    }

    @GetMapping({ "/rifa", "/rifa/" })
    public String redirectRifa() {
        return "redirect:/rifa/index.html";
    }

    @GetMapping({ "/rifa/panel", "/rifa/panel/" })
    public String redirectRifaPanel() {
        return "redirect:/rifa/panel.html";
    }

    @GetMapping({ "/qa", "/qa/", "/qa/rifa-informe", "/qa/rifa-informe/" })
    public String redirectQa() {
        return "redirect:/qa/rifa-informe/index.html";
    }
}
