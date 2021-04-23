package com.mettyoung.presentation;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

public abstract class AbstractParsingInterpreter implements Interpreter {

    public abstract String getCommand();

    public abstract int getNumberOfArguments();

    public abstract boolean interpret(String[] args);

    @Override
    public boolean supports(String line) {
        var words = StringUtils.split(line, ' ');
        var argumentSize = words.length - 1;
        return argumentSize == getNumberOfArguments() && getCommand().equalsIgnoreCase(words[0]);
    }

    @Override
    public boolean interpret(String line) {
        var words = StringUtils.split(line, ' ');
        return interpret(Arrays.copyOfRange(words, 1, words.length));
    }

    //region Helper
    protected String reconstructOriginalCommand(String[] args) {
        return getCommand() + " " + StringUtils.join(args, " ");
    }
    //endregion
}
