package ru.avkurbatov_home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.avkurbatov_home.aspect.ControllerLogs;

/**
 * Controller for home page
 * */
@Controller
@ControllerLogs
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "home";
    }

}