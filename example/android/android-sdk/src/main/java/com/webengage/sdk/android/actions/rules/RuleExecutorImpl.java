package com.webengage.sdk.android.actions.rules;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.actions.database.DataContainer;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.rules.ruleEngine.Evaluator;
import com.webengage.sdk.android.actions.rules.ruleEngine.Expression;
import com.webengage.sdk.android.actions.rules.ruleEngine.Function;
import com.webengage.sdk.android.actions.rules.ruleEngine.Operator;
import com.webengage.sdk.android.utils.DataType;
import com.webengage.sdk.android.utils.RollingDate;
import com.webengage.sdk.android.utils.WebEngageConstant;
import com.webengage.sdk.android.utils.WebEngageUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RuleExecutorImpl extends RuleExecutor {
    private static final Object lock = new Object();
    private List<String> competingIds;
    private Map<String, Rule> ruleMap = null;
    private Map<String, List<EventCriteria>> eventCriteriaMap;

    RuleExecutorImpl() {
        competingIds = new ArrayList<String>();
        ruleMap = new LinkedHashMap<String, Rule>();
        eventCriteriaMap = new HashMap<String, List<EventCriteria>>();
        registerOperatorAndFunctions();
    }

    @Override
    protected void setRuleMap(Map<String, Rule> ruleMap) {
        synchronized (lock) {
            this.ruleMap.clear();
            this.competingIds.clear();
            if (ruleMap != null) {
                this.ruleMap.putAll(ruleMap);
                List<String> copy = new ArrayList<String>();
                copy.addAll(ruleMap.keySet());
                this.competingIds.addAll(copy);
            }
        }
    }

    @Override
    protected void setEventCriteriaMap(Map<String, List<EventCriteria>> map) {
        synchronized (lock) {
            this.eventCriteriaMap.clear();
            if (map != null) {
                this.eventCriteriaMap.putAll(map);
            }
        }
    }

    @Override
    protected List<EventCriteria> getEventCriteriasForEvent(String eventName) {
        synchronized (lock) {
            return this.eventCriteriaMap.get(eventName);
        }
    }

    @Override
    protected List<String> filterRenderingIds(List<String> evaluatedIds, WebEngageConstant.RuleCategory ruleCategory) {
        List<String> filteredIds = new ArrayList<String>();
        if (evaluatedIds != null && evaluatedIds.size() > 0) {
            synchronized (lock) {
                for (String id : evaluatedIds) {
                    WebEngageConstant.RuleCategory qualifyingCategory = ruleMap.get(id).getQualifyingCategory();
                    if (qualifyingCategory.compareTo(ruleCategory) <= 0) {
                        filteredIds.add(id);
                    }
                }
            }
        }
        return filteredIds;
    }

    @Override
    public void setCompetingIds(List<String> competingIds) {
        synchronized (lock) {
            this.competingIds.clear();
            if (competingIds != null) {
                this.competingIds.addAll(competingIds);
            }
        }
    }

    @Override
    protected void reset() {
        synchronized (lock) {
            this.competingIds.clear();
            if (ruleMap != null) {
                List<String> copy = new ArrayList<String>();
                copy.addAll(ruleMap.keySet());
                this.competingIds.addAll(copy);
            }

        }
    }

    @Override
    protected List<String> evaluateRulesByCategory(final WebEngageConstant.RuleCategory ruleCategory) {
        List<String> evaluatedIds = new ArrayList<String>();
        synchronized (lock) {
            for (String id : competingIds) {
                boolean result = this.evaluateRule(id, ruleCategory);
                if (result) {
                    evaluatedIds.add(id);
                }
            }
        }
        return evaluatedIds;
    }


    @Override
    public boolean evaluateRule(final String id, final WebEngageConstant.RuleCategory ruleCategory) {
        Boolean result = false;
        try {
            switch (ruleCategory) {
                case SESSION_RULE:
                    result = (Boolean) ruleMap.get(id).sessionRule().evaluate();
                    break;
                case PAGE_RULE:
                    result = (Boolean) ruleMap.get(id).pageRule().evaluate();
                    break;
                case EVENT_RULE:
                    result = (Boolean) ruleMap.get(id).eventRule().evaluate();
                    break;
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while evaluating rule for experiment by category", e);
        }

        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

    @Override
    public boolean evaluateRule(Rule rule, WebEngageConstant.RuleCategory ruleCategory) {
        Boolean result = false;
        try {
            switch (ruleCategory) {
                case CUSTOM_RULE:
                    result = (Boolean) rule.customRule().evaluate();
                    break;
            }
        } catch (Exception e) {
            Logger.e(WebEngageConstant.TAG, "Exception while evaluating rule for custom rule category", e);
        }

        if (result != null) {
            return result;
        } else {
            return false;
        }
    }

    @Override
    public Object evaluateInfixRule(String infixRule) {
        Expression expression = new Expression(infixRule);
        try {
            return expression.evaluate();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object evaluatePostfixRule(List<String> postFixRule) {
        Expression expression = new Expression(postFixRule);
        try {
            return expression.evaluate();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Function getFunction(String identifier) {
        return Evaluator.get().getFunction(identifier);
    }

    @Override
    public Operator getOperator(String identifier) {
        return Evaluator.get().getOperator(identifier);
    }

    private void registerOperatorAndFunctions() {

        Evaluator.get().registerOperator(new Operator("->", Integer.MIN_VALUE + 1) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (rightValue instanceof List && leftValue instanceof List) {
                    ((List) leftValue).addAll((List) rightValue);
                    return leftValue;
                } else if (rightValue instanceof List) {
                    ((List) rightValue).add(0, leftValue);
                    return rightValue;
                } else if (leftValue instanceof List) {
                    ((List) leftValue).add(rightValue);
                    return leftValue;
                } else {
                    List<Object> list = new ArrayList<Object>();
                    list.add(leftValue);
                    list.add(rightValue);
                    return list;
                }
            }
        });

        Evaluator.get().registerOperator(new Operator("$we_between", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null) {
                    return false;
                } else if (rightValue instanceof List) {
                    List<Object> list = (List<Object>) rightValue;
                    if (list.size() < 2) {
                        return false;
                    }
                    double value = ((Number) leftValue).doubleValue();
                    if (list.get(0) != null && list.get(1) != null) {
                        double lowerBound = ((Number) list.get(0)).doubleValue();
                        double uppperBound = ((Number) list.get(1)).doubleValue();
                        return (value >= lowerBound) && (value <= uppperBound);
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        });
        Evaluator.get().registerOperator(new Operator("$we_in", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    return false;
                }
                if (rightValue instanceof List) {
                    List<Object> list = (List<Object>) rightValue;
                    for (Object object : list) {
                        if ((Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_in").onEvaluation(leftValue, object)) {
                            return true;
                        }
                    }
                } else {
                    return RuleExecutorFactory.getRuleExecutor().getOperator("==").onEvaluation(leftValue, rightValue);
                }
                return false;
            }
        });

        Evaluator.get().registerOperator(new Operator("$we_nin", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                Boolean result = (Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_in").onEvaluation(leftValue, rightValue);
                return !result;
            }
        });

        Evaluator.get().registerOperator(new Operator("$we_contains_all", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                boolean contains = false;

                if (leftValue instanceof List) {
                    if (rightValue instanceof List) {
                        List<Object> list = (List<Object>) rightValue;
                        for (Object object : list) {
                            if ((Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_contains_all").onEvaluation(leftValue, object)) {
                                contains = true;
                                continue;
                            }
                            contains = false;
                            break;
                        }
                        return contains;
                    } else {
                        return RuleExecutorFactory.getRuleExecutor().getOperator("$we_in").onEvaluation(rightValue, leftValue);
                    }
                } else {
                    if (rightValue instanceof List) {
                        return false;
                    } else {
                        return RuleExecutorFactory.getRuleExecutor().getOperator("==").onEvaluation(leftValue, rightValue);
                    }
                }

            }
        });

        Evaluator.get().registerOperator(new Operator("$we_contains_any", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                boolean contains = false;
                if (leftValue instanceof List) {
                    if (rightValue instanceof List) {
                        List<Object> list = (List<Object>) rightValue;
                        for (Object object : list) {
                            if ((Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_contains_any").onEvaluation(leftValue, object)) {
                                contains = true;
                                break;
                            }
                        }
                        return contains;
                    } else {
                        return RuleExecutorFactory.getRuleExecutor().getOperator("$we_in").onEvaluation(rightValue, leftValue);
                    }
                } else {
                    if (rightValue instanceof List) {
                        List<Object> list = (List<Object>) rightValue;
                        for (Object object : list) {
                            if ((Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_contains_any").onEvaluation(leftValue, object)) {
                                contains = true;
                                break;
                            }
                        }
                        return contains;
                    } else {
                        return RuleExecutorFactory.getRuleExecutor().getOperator("==").onEvaluation(leftValue, rightValue);
                    }
                }


            }
        });

        Evaluator.get().registerOperator(new Operator("$we_exclude_all", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                return !((Boolean) RuleExecutorFactory.getRuleExecutor().getOperator("$we_contains_any").onEvaluation(leftValue, rightValue));
            }
        });

        Evaluator.get().registerFunction(new Function("$we_matches") {
            @Override
            public Object onEvaluation(List<Object> parameters) {
                if (parameters.size() <= 1) {
                    return false;
                }

                if (parameters.get(0) != null) {
                    String valueToMatch = parameters.get(0).toString();
                    if (parameters.get(1) instanceof List) {
                        List<Object> values = (List<Object>) parameters.get(1);
                        for (Object object : values) {
                            List<Object> list = new ArrayList<Object>();
                            list.add(valueToMatch);
                            list.add(object);
                            if ((Boolean) RuleExecutorFactory.getRuleExecutor().getFunction("$we_matches").onEvaluation(list)) {
                                return true;
                            }
                        }

                    } else {
                        if (parameters.get(1) != null) {
                            String regex = parameters.get(1).toString();
                            Pattern pattern = Pattern.compile(regex);
                            Matcher matcher = pattern.matcher(valueToMatch);
                            return matcher.find();
                        }
                    }
                }
                return false;

            }
        });

        Evaluator.get().registerFunction(new Function("$we_getData") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                Object result = DataHolder.get().getData(arguments);
                if (result != null && result instanceof Date) {
                    return ((Date) result).getTime();
                }
                return result;

            }
        });

        Evaluator.get().registerFunction(new Function("$we_escapeRegex") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments.size() == 0 || arguments.size() == 0) {
                    return null;
                }
                return WebEngageUtils.escapeRegex(arguments.get(0).toString());
            }
        });

        Evaluator.get().registerFunction(new Function("$we_exists") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null) {
                    return false;
                }
                return arguments.get(0) != null;

            }
        });

        Evaluator.get().registerFunction(new Function("$we_default") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments.size() <= 1) {
                    return null;
                }
                return arguments.get(0) == null ? arguments.get(1) : arguments.get(0);


            }
        });


        Evaluator.get().registerFunction(new Function("$we_event_criteria") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                List<Object> list = new ArrayList<Object>();
                list.add(DataContainer.EVENT_CRITERIA.toString());
                list.add(arguments.get(0));
                list.add("val");
                return RuleExecutorFactory.getRuleExecutor().getFunction("$we_getData").onEvaluation(list);
            }
        });


        Evaluator.get().registerFunction(new Function(EventCriteria.SUM) {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                Number result = 0;
                Number newValue = (Number) arguments.get(0);
                Map<String, Object> eventCriteriaValues = (Map<String, Object>) arguments.get(1);
                if (eventCriteriaValues != null) {
                    Number oldValue = (Number) eventCriteriaValues.get("val");
                    if (oldValue == null) {
                        eventCriteriaValues.put("val", newValue);
                        return eventCriteriaValues;
                    }
                    result = oldValue.doubleValue() + newValue.doubleValue();
                    eventCriteriaValues.put("val", result);
                    return eventCriteriaValues;
                } else {
                    eventCriteriaValues = new HashMap<String, Object>();
                    eventCriteriaValues.put("val", newValue);
                    return eventCriteriaValues;
                }

            }
        });

        Evaluator.get().registerFunction(new Function(EventCriteria.AVG) {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                Number result = 0;
                Number newValue = (Number) arguments.get(0);
                Map<String, Object> eventCriteriaValues = (Map<String, Object>) arguments.get(1);
                if (eventCriteriaValues != null) {
                    Number oldValue = (Number) eventCriteriaValues.get("val");
                    Number count = (Number) eventCriteriaValues.get("count");
                    if (oldValue == null || count == null) {
                        eventCriteriaValues.put("val", newValue);
                        eventCriteriaValues.put("count", 1l);
                        return eventCriteriaValues;
                    }
                    result = (oldValue.doubleValue() * count.longValue() + newValue.doubleValue()) / (count.longValue() + 1);
                    eventCriteriaValues.put("val", result);
                    eventCriteriaValues.put("count", count.longValue() + 1);
                    return eventCriteriaValues;
                } else {
                    eventCriteriaValues = new HashMap<String, Object>();
                    result = newValue;
                    eventCriteriaValues.put("val", result);
                    eventCriteriaValues.put("count", 1l);
                    return eventCriteriaValues;
                }


            }
        });

        Evaluator.get().registerFunction(new Function(EventCriteria.COUNT) {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                Number result = 0;
                Map<String, Object> eventCriteriaValues = (Map<String, Object>) arguments.get(1);
                if (eventCriteriaValues != null) {
                    Number oldValue = (Number) eventCriteriaValues.get("val");
                    if (oldValue == null) {
                        eventCriteriaValues.put("val", 1l);
                        return eventCriteriaValues;
                    }
                    result = oldValue.longValue() + 1;
                    eventCriteriaValues.put("val", result);
                    return eventCriteriaValues;
                } else {
                    eventCriteriaValues = new HashMap<String, Object>();
                    eventCriteriaValues.put("val", 1l);
                    return eventCriteriaValues;
                }

            }
        });


        Evaluator.get().registerFunction(new Function(EventCriteria.MIN) {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                Object newValue = arguments.get(0);
                Map<String, Object> eventCriteriaValues = (Map<String, Object>) arguments.get(1);
                if (eventCriteriaValues != null) {
                    if (newValue instanceof Date) {
                        Object oldValue = eventCriteriaValues.get("val");
                        if (!(oldValue instanceof Date)) {
                            try {
                                oldValue = DataType.convert(oldValue, DataType.DATE, true);
                            } catch (Exception e) {
                                eventCriteriaValues.put("val", newValue);
                                return eventCriteriaValues;
                            }
                        }
                        if (((Date) newValue).getTime() < ((Date) oldValue).getTime()) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        } else {
                            return eventCriteriaValues;
                        }
                    } else if (newValue instanceof Number) {
                        Number oldValue = (Number) eventCriteriaValues.get("val");
                        if (oldValue == null) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        }
                        if (((Number) newValue).doubleValue() < oldValue.doubleValue()) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        } else {
                            return eventCriteriaValues;
                        }
                    }
                } else {
                    eventCriteriaValues = new HashMap<String, Object>();
                    eventCriteriaValues.put("val", newValue);
                    return eventCriteriaValues;
                }
                return null;
            }
        });

        Evaluator.get().registerFunction(new Function(EventCriteria.MAX) {

            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                Object newValue = arguments.get(0);
                Map<String, Object> eventCriteriaValues = (Map<String, Object>) arguments.get(1);
                if (eventCriteriaValues != null) {
                    if (newValue instanceof Date) {
                        Object oldValue = eventCriteriaValues.get("val");
                        if (!(oldValue instanceof Date)) {
                            try {
                                oldValue = DataType.convert(oldValue, DataType.DATE, true);
                            } catch (Exception e) {
                                eventCriteriaValues.put("val", newValue);
                                return eventCriteriaValues;
                            }
                        }
                        if (((Date) newValue).getTime() > ((Date) oldValue).getTime()) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        } else {
                            return eventCriteriaValues;
                        }
                    } else if (newValue instanceof Number) {
                        Number oldValue = (Number) eventCriteriaValues.get("val");
                        if (oldValue == null) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        }
                        if (((Number) newValue).doubleValue() > oldValue.doubleValue()) {
                            eventCriteriaValues.put("val", newValue);
                            return eventCriteriaValues;
                        } else {
                            return eventCriteriaValues;
                        }
                    }
                } else {
                    eventCriteriaValues = new HashMap<String, Object>();
                    eventCriteriaValues.put("val", newValue);
                    return eventCriteriaValues;
                }
                return null;
            }
        });


        Evaluator.get().registerFunction(new Function("$we_date") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0) {
                    return null;
                }
                try {
                    return ((Date) DataType.convert(arguments.get(0), DataType.DATE, false)).getTime();
                } catch (Exception e) {
                    return null;
                }
            }
        });

        Evaluator.get().registerFunction(new Function("$we_ms") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments.size() == 0 || arguments.size() < 2) {
                    return null;
                }
                Integer value = (Integer) arguments.get(0);
                String unit = (String) arguments.get(1);
                return TimeUnit.MILLISECONDS.convert(value, TimeUnit.valueOf(unit.toUpperCase()));
            }
        });


        Evaluator.get().registerFunction(new Function("$we_now") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                return new Date().getTime();
            }
        });


        Evaluator.get().registerFunction(new Function("$we_boolean") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments.size() < 2) {
                    return false;
                }
                return RuleExecutorFactory.getRuleExecutor().getOperator("==").onEvaluation(arguments.get(0), arguments.get(1));
            }
        });

        Evaluator.get().registerFunction(new Function("$we_ref_date") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() < 2) {
                    return null;
                }
                if (arguments.get(0) != null && arguments.get(1) != null) {
                    RollingDate rollingDate = new RollingDate((long) arguments.get(0), (String) arguments.get(1));
                    Date date = rollingDate.getTime();
                    if (date != null) {
                        return date.getTime();
                    }
                }
                return null;
            }
        });
        /**
         * You are not expected to understand this.
         */
        Evaluator.get().registerFunction(new Function("$we_getResolvedData") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments == null || arguments.size() == 0 || arguments.get(0) == null) {
                    return null;
                }
                List<Object> path = arguments;

                String container = (String) path.get(0);
                if (container != null) {
                    if ("user".equals(container)) {
                        if (path.size() > 1) {
                            String category = (String) path.get(1);
                            if (WebEngageConstant.SYSTEM.equals(category)) {
                                List<Object> list = new ArrayList<Object>();
                                list.add(DataContainer.USER.toString());
                                if (path.size() > 2) {
                                    //list.addAll(pa)
                                    list.addAll(path.subList(2, path.size()));
                                    return DataHolder.get().getData(list);
                                } else {
                                    Map<String, Object> systemAttr = (Map<String, Object>) DataHolder.get().getData(list);
                                    if (systemAttr != null && systemAttr.size() > 0) {
                                        return systemAttr;
                                    }
                                }
                            } else if (WebEngageConstant.CUSTOM.equals(category)) {
                                List<Object> list = new ArrayList<Object>();
                                list.add(DataContainer.ATTR.toString());
                                if (path.size() > 2) {
                                    list.addAll(path.subList(2, path.size()));
                                    return DataHolder.get().getData(list);
                                } else {
                                    Map<String, Object> customAttr = (Map<String, Object>) DataHolder.get().getData(list);
                                    if (customAttr != null && customAttr.size() > 0) {
                                        return customAttr;
                                    }

                                }
                            }
                        } else {
                            //return all user's system and custom attribute
                            Map<String, Object> result = new HashMap<String, Object>();
                            Map<String, Object> systemAttr = (Map<String, Object>) DataHolder.get().getData(DataContainer.USER.toString());
                            Map<String, Object> customAttr = (Map<String, Object>) DataHolder.get().getData(DataContainer.ATTR.toString());
                            if ((systemAttr == null || systemAttr.isEmpty()) && (customAttr == null || customAttr.isEmpty())) {
                                return null;
                            }
                            if (systemAttr != null && !systemAttr.isEmpty()) {
                                result.put(WebEngageConstant.SYSTEM, systemAttr);
                            }
                            if (customAttr != null && !customAttr.isEmpty()) {
                                result.put(WebEngageConstant.CUSTOM, customAttr);
                            }
                            return result;

                        }
                    } else if ("screen".equals(container)) {
                        List<Object> list = new ArrayList<Object>();
                        list.add(DataContainer.PAGE.toString());
                        if (path.size() > 1) {
                            list.addAll(path.subList(1, path.size()));
                        }
                        return DataHolder.get().getData(list);
                    } else if ("event".equals(container)) {
                        if (path.size() <= 2) {
                            return null;
                        }
                        if (path.size() > 3) {
                            List<Object> list = new ArrayList<Object>();
                            list.add(DataContainer.LATEST_EVENT.toString());
                            if (WebEngageConstant.SYSTEM.equals(path.get(1))) {
                                list.add("we_" + path.get(2));
                            } else if (WebEngageConstant.CUSTOM.equals(path.get(1))) {
                                list.add(path.get(2));
                            }
                            if (WebEngageConstant.SYSTEM.equals(path.get(3))) {
                                list.add("we_wk_sys");
                            }
                            if (path.size() > 4) {
                                list.addAll(path.subList(4, path.size()));
                                return DataHolder.get().getData(list);
                            } else {
                                //size is 4
                                if (WebEngageConstant.SYSTEM.equals(path.get(3))) {
                                    Map<String, Object> systemAttr = (Map<String, Object>) DataHolder.get().getData(list);
                                    if (systemAttr == null || systemAttr.isEmpty()) {
                                        return null;
                                    }
                                    return systemAttr;
                                } else if (WebEngageConstant.CUSTOM.equals(path.get(3))) {
                                    Map<String, Object> eventAttributes = (Map<String, Object>) DataHolder.get().getData(list);
                                    Map<String, Object> customAttributes = new HashMap<String, Object>();
                                    if (eventAttributes == null) {
                                        return null;
                                    }
                                    for (Map.Entry<String, Object> entry : eventAttributes.entrySet()) {
                                        if (!"we_wk_sys".equals(entry.getKey())) {
                                            customAttributes.put(entry.getKey(), entry.getValue());
                                        }
                                    }
                                    return customAttributes.isEmpty() ? null : customAttributes;
                                }
                            }

                        } else {
                            //size is 3
                            List<Object> list = new ArrayList<Object>();
                            list.add(DataContainer.LATEST_EVENT.toString());
                            if (WebEngageConstant.SYSTEM.equals(path.get(1))) {
                                list.add("we_" + path.get(2));
                            } else if (WebEngageConstant.CUSTOM.equals(path.get(1))) {
                                list.add(path.get(2));
                            }
                            Map<String, Object> result = new HashMap<String, Object>();
                            Map<String, Object> eventAttributes = (Map<String, Object>) DataHolder.get().getData(list);
                            if (eventAttributes == null || eventAttributes.isEmpty()) {
                                return null;
                            }
                            Map<String, Object> systemAttributes = (Map<String, Object>) eventAttributes.get("we_wk_sys");
                            if (systemAttributes != null && !systemAttributes.isEmpty()) {
                                result.put(WebEngageConstant.SYSTEM, systemAttributes);
                            }
                            Map<String, Object> customAttr = new HashMap<String, Object>();
                            for (Map.Entry<String, Object> entry : eventAttributes.entrySet()) {
                                if (!"we_wk_sys".equals(entry.getKey())) {
                                    customAttr.put(entry.getKey(), entry.getValue());
                                }
                            }
                            if (!customAttr.isEmpty()) {
                                result.put(WebEngageConstant.CUSTOM, customAttr);
                            }
                            if (result.isEmpty()) {
                                return null;
                            }
                            return result;

                        }
                    }
                }
                return null;

            }
        });


    }

}
