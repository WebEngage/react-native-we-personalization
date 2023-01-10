package com.webengage.sdk.android.actions.rules.ruleEngine;

public abstract class Operator {
    private String operIdentifier = null;
    int precedence = 0;
    boolean isLeftToRight = true;

    public Operator(String operIdentifier, int precedence, boolean isLeftToRight) {
        this.operIdentifier = operIdentifier;
        this.precedence = precedence;
        this.isLeftToRight = isLeftToRight;
    }

    public Operator(String operIdentifier, int precedence) {
        this(operIdentifier, precedence, true);
    }


    public String getOperatorIdentifier() {
        return this.operIdentifier;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public boolean isLeftToRightAssociative() {
        return this.isLeftToRight;
    }

    public abstract Object onEvaluation(Object leftOperand,Object rightOperand);


}