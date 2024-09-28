package com.pixapp.pixapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FrontendController {
    @RequestMapping(value = {"/{path:^(?!api|static|css|js|img).*}/**"})
    public String forward() {
        return "forward:/index.html";
    }

    @RequestMapping("/error")
    public String handleError() {
        // Можешь вернуть кастомную страницу ошибок
        return "error";
    }
}