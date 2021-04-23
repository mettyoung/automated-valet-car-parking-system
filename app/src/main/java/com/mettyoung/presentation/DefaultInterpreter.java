package com.mettyoung.presentation;

public class DefaultInterpreter implements Interpreter {

    @Override
    public boolean supports(String line) {
        return true;
    }

    @Override
    public boolean interpret(String line) {
        System.out.println(line + " -> Invalid Command");
        return true;
    }
}
