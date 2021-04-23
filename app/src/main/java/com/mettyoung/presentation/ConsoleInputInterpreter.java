package com.mettyoung.presentation;

import java.util.ArrayList;
import java.util.List;

public class ConsoleInputInterpreter {
    private final List<Interpreter> interpreters = new ArrayList<>();

    public ConsoleInputInterpreter addInterpreter(Interpreter interpreter) {
        interpreters.add(interpreter);
        return this;
    }

    public void interpret(String line) {
        for (var interpreter : interpreters) {
            if (interpreter.supports(line) && interpreter.interpret(line)) {
                return;
            }
        }
    }
}
