package com.webengage.sdk.android.actions.rules;

import com.webengage.sdk.android.actions.rules.ruleEngine.Function;
import com.webengage.sdk.android.actions.rules.ruleEngine.Operator;
import com.webengage.sdk.android.utils.WebEngageConstant;

import java.util.List;
import java.util.Map;

public abstract class RuleExecutor {

    protected abstract void setEventCriteriaMap(Map<String, List<EventCriteria>> map);

    protected abstract List<EventCriteria> getEventCriteriasForEvent(String eventName);

    protected abstract List<String> evaluateRulesByCategory(WebEngageConstant.RuleCategory ruleCategory);

    public abstract boolean evaluateRule(String id, WebEngageConstant.RuleCategory ruleCategory);

    public abstract boolean evaluateRule(Rule rule, WebEngageConstant.RuleCategory ruleCategory);

    protected abstract void setCompetingIds(List<String> competingIds);

    protected abstract void setRuleMap(Map<String, Rule> ruleMap);

    protected abstract void reset();

    protected abstract List<String> filterRenderingIds(List<String> evaluatedIds, WebEngageConstant.RuleCategory ruleCategory);

    public abstract Object evaluateInfixRule(String infixRule);

    public abstract Object evaluatePostfixRule(List<String> postFixRule);

    public abstract Function getFunction(String identifier);

    public abstract Operator getOperator(String identifier);


}
