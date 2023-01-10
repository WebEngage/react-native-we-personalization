package com.webengage.sdk.android.actions.rules;

import com.webengage.sdk.android.actions.rules.ruleEngine.Function;
import com.webengage.sdk.android.actions.rules.ruleEngine.Operator;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;
import java.util.Map;

class RuleExecutorNoOpImpl extends RuleExecutor {
    @Override
    protected void setEventCriteriaMap(Map<String, List<EventCriteria>> map) {

    }

    @Override
    protected List<EventCriteria> getEventCriteriasForEvent(String eventName) {
        return null;
    }

    @Override
    protected List<String> evaluateRulesByCategory(WebEngageConstant.RuleCategory ruleCategory) {
        return null;
    }

    @Override
    public boolean evaluateRule(String id, WebEngageConstant.RuleCategory ruleCategory) {
        return false;
    }

    @Override
    public boolean evaluateRule(Rule rule, WebEngageConstant.RuleCategory ruleCategory) {
        return false;
    }

    @Override
    protected void setCompetingIds(List<String> competingIds) {

    }

    @Override
    protected void setRuleMap(Map<String, Rule> ruleMap) {

    }

    @Override
    protected void reset() {

    }

    @Override
    protected List<String> filterRenderingIds(List<String> evaluatedIds, WebEngageConstant.RuleCategory ruleCategory) {
        return null;
    }

    @Override
    public Object evaluateInfixRule(String infixRule) {
        return null;
    }

    @Override
    public Object evaluatePostfixRule(List<String> postFixRule) {
        return null;
    }

    @Override
    public Function getFunction(String identifier) {
        return null;
    }

    @Override
    public Operator getOperator(String identifier) {
        return null;
    }
}
