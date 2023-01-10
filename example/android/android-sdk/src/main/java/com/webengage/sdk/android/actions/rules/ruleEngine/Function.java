package com.webengage.sdk.android.actions.rules.ruleEngine;

import java.util.List;

public abstract class Function {
    private String funcIdentifier = null;

    private int precedence = Integer.MAX_VALUE;

    public Function(String funcIdentifier) {
        this.funcIdentifier = funcIdentifier;
    }

    public String getFunctionIdentifier() {
        return this.funcIdentifier;
    }

    public int getPrecedence() {
        return precedence;
    }

    public abstract Object onEvaluation(List<Object> arguments);
}