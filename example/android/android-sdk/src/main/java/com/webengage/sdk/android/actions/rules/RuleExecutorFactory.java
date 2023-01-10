package com.webengage.sdk.android.actions.rules;


public class RuleExecutorFactory {
    static RuleExecutor ruleExecutor = null;
    static RuleExecutor noOp = null;

    public static RuleExecutor getRuleExecutor() {
        if (ruleExecutor == null) {
            synchronized (RuleExecutorFactory.class) {
                if (ruleExecutor == null) {
                    ruleExecutor = new RuleExecutorImpl();
                }
            }
        }
        return ruleExecutor;
    }

    public static RuleExecutor getNoOpRuleExecutor() {
        if (noOp == null) {
            noOp = new RuleExecutorNoOpImpl();
        }

        return noOp;
    }
}
