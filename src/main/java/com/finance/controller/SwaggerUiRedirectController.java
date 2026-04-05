package com.finance.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

/**
 * Plan-friendly entry: {@code /api/docs} → default SpringDoc UI at {@code /swagger-ui/index.html}.
 */
@Controller
public class SwaggerUiRedirectController {

    @GetMapping({"/api/docs", "/api/docs/"})
    public RedirectView redirectApiDocs() {
        return new RedirectView("/swagger-ui/index.html");
    }
}
