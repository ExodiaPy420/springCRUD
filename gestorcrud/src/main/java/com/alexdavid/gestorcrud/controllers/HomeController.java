package com.alexdavid.gestorcrud.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

///////////////////////////////////////////////////
//mapeamos la página principal que vamos a tener//
/////////////////////////////////////////////////
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

}
