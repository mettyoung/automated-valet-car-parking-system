package com.mettyoung.presentation;

public interface Interpreter {
    boolean supports(String line);
    boolean interpret(String line);
}
