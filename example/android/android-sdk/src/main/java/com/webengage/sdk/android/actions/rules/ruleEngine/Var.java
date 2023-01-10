package com.webengage.sdk.android.actions.rules.ruleEngine;


import java.util.concurrent.atomic.AtomicReference;

public class Var {
    private String identifier;
    private AtomicReference<Object> value=new AtomicReference<Object>();
    public Var(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier(){
        return this.identifier;
    }

    public void setValue(Object value){
        this.value.set(value);
    }

    public Object getValue(){
        return this.value.get();
    }
}

