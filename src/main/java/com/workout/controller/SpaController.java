package com.workout.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // API (/api) でも 静的ファイル (.js, .css など) でもない
    // "/workout" や "/workout/12" といったリクエストを全て index.html にフォワードする
    @GetMapping({"/workout", "/workout/**"})
    public String forward() {
        return "forward:/index.html";
    }
}