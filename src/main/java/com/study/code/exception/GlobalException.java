package com.study.code.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author weim
 * @date
 */
@ControllerAdvice
public class GlobalException {

    @ExceptionHandler(Exception.class)
    public String dealException(Exception e, Model model) {

        model.addAttribute("msg", e.getMessage());

        return "error";
    }

}
