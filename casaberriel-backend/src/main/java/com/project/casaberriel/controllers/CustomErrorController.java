package com.project.casaberriel.controllers;


import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // You can add custom error handling logic here
        return "error"; // Return a custom error view
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
