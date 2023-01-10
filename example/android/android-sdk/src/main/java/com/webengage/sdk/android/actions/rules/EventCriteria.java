package com.webengage.sdk.android.actions.rules;


import com.webengage.sdk.android.EventPayload;
import com.webengage.sdk.android.actions.rules.ruleEngine.Expression;

public class EventCriteria {

    private String id;
    private String function;
    private String attribute;
    private String attributeCategory;
    private Expression expression;

    public static final String SUM = "SUM";
    public static final String AVG = "AVG";
    public static final String MIN = "MIN";
    public static final String MAX = "MAX";
    public static final String COUNT = "COUNT";

    private EventCriteria(Builder builder) {
        this.id = builder.id;
        this.function = builder.function;
        this.attribute = builder.attribute;
        this.attributeCategory = builder.attributeCategory;
        this.expression = builder.expression;
    }

    public String getId() {
        return id;
    }

    public String getFunction() {
        return function;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getAttributeCategory(){
        return attributeCategory;
    }

    public Expression getExpression() {
        return expression;
    }

    public Builder getCurrentState() {
        Builder builder = new Builder()
                .setAttribute(this.attribute)
                .setAttributeCategory(this.attributeCategory)
                .setExpression(this.expression)
                .setFunction(this.function)
                .setId(this.id);
        return builder;
    }

    public static class Builder {
        private String id;
        private String function;
        private String attribute;
        private String attributeCategory;
        private Expression expression;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setFunction(String function) {
            this.function = function;
            return this;
        }

        public Builder setAttribute(String attribute) {
            this.attribute = attribute;
            return this;
        }

        public Builder setAttributeCategory(String attributeCategory){
            this.attributeCategory = attributeCategory;
            return this;
        }

        public Builder setExpression(Expression expression) {
            this.expression = expression;
            return this;
        }

        public EventCriteria build() {
            return new EventCriteria(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if(o == null){
            return false;
        }
        EventCriteria eventCriteria = (EventCriteria)o;
        return eventCriteria.getId().equals(this.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String NEW_LINE = System.getProperty("line.separator");
        sb.append(this.getClass().getName() + " Object { " + NEW_LINE);
        sb.append(" Id : " + getId() + NEW_LINE);
        sb.append(" Function : " + getFunction().toString() + NEW_LINE);
        sb.append(" Attribute : " + this.getAttribute() + NEW_LINE);
        sb.append(" Attribute Category : " + this.getAttributeCategory() + NEW_LINE);
        sb.append(" Rule : "+this.getExpression().toString());
        sb.append("}");
        return sb.toString();
    }
}
