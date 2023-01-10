package com.webengage.sdk.android.actions.rules;

import com.webengage.sdk.android.actions.rules.ruleEngine.Expression;
import com.webengage.sdk.android.utils.WebEngageConstant;

public class Rule {
    private Expression sessionRule = null;
    private Expression pageRule = null;
    private Expression eventRule = null;
    private Expression customRule = null;

    public Rule(String sessionRule, String pageRule, String eventRule) {
        this.sessionRule = new Expression(sessionRule);
        this.pageRule = new Expression(pageRule);
        this.eventRule = new Expression(eventRule);
    }

    public Rule(String customRule) {
        this.customRule = new Expression(customRule);
    }

    public Expression sessionRule() {
        return this.sessionRule;
    }

    public Expression pageRule() {
        return this.pageRule;
    }

    public Expression eventRule() {
        return this.eventRule;
    }

    public Expression customRule() {
        return this.customRule;
    }

    public WebEngageConstant.RuleCategory getQualifyingCategory() {
        if (eventRule().toString().equalsIgnoreCase("true")) {
            return WebEngageConstant.RuleCategory.PAGE_RULE;
        }
        return WebEngageConstant.RuleCategory.EVENT_RULE;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Rule) {
            Rule rule = (Rule) o;
            return rule.sessionRule.equals(this.sessionRule) && rule.pageRule.equals(this.pageRule) && rule.eventRule.equals(this.eventRule);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (this.sessionRule.toString()+this.pageRule.toString()+this.eventRule.toString()).hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        sb.append(this.getClass().getName() + " Object { " + NEW_LINE);
        sb.append(" Session Rule : " + sessionRule() + NEW_LINE);
        sb.append(" Page Rule : " + pageRule() + NEW_LINE);
        sb.append(" Event Rule : " + eventRule() + NEW_LINE);
        sb.append("}");
        return sb.toString();
    }
}

