package com.workout.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaController {

    // API (/api/**) でも、静的ファイル (.js, .css, .pngなど) でもないパスに来たら
    // 全て index.html を返す
    @GetMapping(value = "{path:[^\\.]*}")
    public String redirect() {
        return "forward:/index.html";
    }

    // 2階層以上のパス (/workout/details など) にも対応
    @GetMapping(value = "/**/{path:[^\\.]*}")
    public String redirectDeep() {
        return "forward:/index.html";
    }
}