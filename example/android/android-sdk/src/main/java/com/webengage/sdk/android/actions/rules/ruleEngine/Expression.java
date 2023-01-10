package com.webengage.sdk.android.actions.rules.ruleEngine;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.actions.database.DataHolder;

import java.util.List;


public class Expression {
    private String expression = null;
    private List<String> postFixExpression = null;

    public Expression(String expression) {
        this.expression = expression;
        this.postFixExpression = Evaluator.get().compile(this.expression, DataHolder.get().useLegacyRuleCompiler());

    }

    public Expression(List<String> postFixExpression) {
        this.postFixExpression = postFixExpression;
    }

    public List<String> getPostFixExpression() {
        return this.postFixExpression;
    }


    public Object evaluate() {
        boolean useLegacy = DataHolder.get().useLegacyRuleCompiler();
        if (this.postFixExpression == null) {
            this.postFixExpression = Evaluator.get().compile(this.expression, useLegacy);
        }
        try {
            return Evaluator.get().evaluate(this.postFixExpression, useLegacy);
        } catch (Exception e) {
            return null;
        }

    }

    public String toString() {
        return this.expression;
    }
}
