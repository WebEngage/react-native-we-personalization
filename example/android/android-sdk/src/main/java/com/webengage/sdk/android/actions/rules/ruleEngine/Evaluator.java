package com.webengage.sdk.android.actions.rules.ruleEngine;


import android.util.JsonReader;

import com.webengage.sdk.android.Logger;
import com.webengage.sdk.android.actions.database.DataHolder;
import com.webengage.sdk.android.actions.rules.RuleExecutorFactory;
import com.webengage.sdk.android.utils.NetworkUtils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Evaluator {
    private static volatile Evaluator instance = null;
    private final Map<String, Operator> operators;
    private final Map<String, Function> functions;
    private final Map<String, Var> variables;
    private Converter converter;

    private Evaluator() {
        operators = new HashMap<String, Operator>();
        functions = new HashMap<String, Function>();
        variables = new HashMap<String, Var>();
        this.converter = new ConverterImpl();
        registerBaseOperatorsAndFunctions();
    }

    public static Evaluator get() {
        if (instance == null) {
            synchronized (Evaluator.class) {
                if (instance == null) {
                    instance = new Evaluator();
                }
            }
        }
        return instance;
    }

    public boolean isOperator(String operatorIdentifier) {
        synchronized (this.operators) {
            return this.operators.containsKey(operatorIdentifier);
        }
    }

    public boolean isFunction(String functionIdentifier) {
        synchronized (this.functions) {
            return this.functions.containsKey(functionIdentifier);
        }
    }


    public boolean isVariable(String varIdentifier) {
        synchronized (this.variables) {
            return this.variables.containsKey(varIdentifier);
        }
    }

    public void registerOperator(Operator operator) {
        synchronized (this.operators) {
            operators.put(operator.getOperatorIdentifier(), operator);
        }
    }

    public void registerFunction(Function function) {
        synchronized (this.functions) {
            functions.put(function.getFunctionIdentifier(), function);
        }
    }

    public void registerConverter(Converter converter) {
        this.converter = converter;
    }

    public void registerVar(Var var) {
        synchronized (this.variables) {
            variables.put(var.getIdentifier(), var);
        }
    }

    public Var getVar(String identifier) {
        synchronized (this.variables) {
            return variables.get(identifier);
        }
    }

    public Operator getOperator(String identifier) {
        synchronized (this.operators) {
            return operators.get(identifier);
        }
    }

    public Function getFunction(String identifier) {
        synchronized (this.functions) {
            return functions.get(identifier);
        }
    }

    protected List<String> compile(String expression) {
        return compile(expression, false);
    }

    protected List<String> compile(String expression, boolean useLegacy) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }
        List<String> postFixExpression = new ArrayList<String>();
        InfixTokenizer tokenizer = new InfixTokenizer(expression, useLegacy);
        ArrayDeque<String> stack = new ArrayDeque<String>();
        String token = "";
        String prevToken = "";
        while (tokenizer.hasNext()) {
            token = tokenizer.next();
            if (token.isEmpty()) {
                continue;
            }
            if (isOperator(token) || isFunction(token)) {
                while (!stack.isEmpty() && isInputLowerPrecedence(token, stack.peek())) {
                    postFixExpression.add(stack.pop());
                }
                stack.push(token);
            } else if (token.equalsIgnoreCase("(") || token.equalsIgnoreCase("[")) {
                stack.push(token);
            } else if (token.equalsIgnoreCase(")") || token.equalsIgnoreCase("]")) {
                if (prevToken.equalsIgnoreCase("(") || prevToken.equalsIgnoreCase("[")) {
                    postFixExpression.add("null");
                }
                while (!stack.isEmpty() && (!stack.peek().equalsIgnoreCase("(") && !stack.peek().equalsIgnoreCase("["))) {
                    postFixExpression.add(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
                if (!stack.isEmpty() && isFunction(stack.peek())) {
                    postFixExpression.add(stack.pop());
                }
            } else {
                token = token.trim();
                if (!token.isEmpty()) {
                    postFixExpression.add(token);
                }
            }
            prevToken = token;
        }
        while (!stack.isEmpty()) {
            postFixExpression.add(stack.pop());
        }
        return postFixExpression;
    }

    protected Object evaluate(List<String> postFixExpression) {
        return evaluate(postFixExpression, false);
    }

    protected Object evaluate(List<String> postFixExpression, boolean useLegacy) {
        if (postFixExpression == null) {
            return null;
        }
        Stack<Object> stack = new Stack<Object>();
        Object leftOperand = null;
        Object rightOperand = null;

        for (String token : postFixExpression) {
            if (token.equalsIgnoreCase(" ") || token.isEmpty()) {
                continue;
            }
            if (isVariable(token)) {
                stack.push(variables.get(token).getValue());
            } else if (isOperator(token)) {
                rightOperand = stack.pop();
                leftOperand = stack.pop();
                stack.push(operators.get(token).onEvaluation(leftOperand, rightOperand));
            } else if (isFunction(token)) {
                Object value = stack.pop();
                if (value instanceof List) {
                    stack.push(functions.get(token).onEvaluation((List<Object>) value));
                } else {
                    List<Object> list = new ArrayList<Object>();
                    list.add(value);
                    stack.push(functions.get(token).onEvaluation(list));
                }
            } else {
                if(useLegacy) {
                    stack.push(converter.convertStringToObject(removeBackSlash(token)));
                } else {
                    stack.push(converter.convertStringToObject(token));
                }
            }

        }
        return stack.pop();
    }


    public boolean isInputLowerPrecedence(String inputStr, String stackTop) {
        if (isOperator(inputStr)) {
            if (isOperator(stackTop)) {
                Operator stTop = operators.get(stackTop);
                Operator input = operators.get(inputStr);
                if (stTop.getPrecedence() == input.getPrecedence()) {
                    if (input.isLeftToRightAssociative()) {
                        return true;
                    }
                } else {
                    if (input.getPrecedence() < stTop.getPrecedence()) {
                        return true;
                    }
                }
            } else if (isFunction(stackTop)) {
                return true;
            }
        }

        return false;

    }

    public String removeBackSlash(String token) {
        String result = "";
        int count = 0;
        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);
            if (ch == '\\') {
                count++;
                continue;
            }
            count >>= 1;
            while (count != 0) {
                result += "\\";
                count--;
            }
            result += ch;
        }
        count >>= 1;
        while (count != 0) {
            result += "\\";
            count--;
        }
        return result;
    }

    public boolean isValidKeyword(String str) {
        return (Evaluator.get().isOperator(str) || Evaluator.get().isFunction(str) || Evaluator.get().isVariable(str) || str.equals("[") || str.equals("]") || str.equals("(") || str.equals(")"));
    }

    private void registerBaseOperatorsAndFunctions() {
        registerFunction(new Function("!") {
            @Override
            public Object onEvaluation(List<Object> arguments) {
                if (arguments.size() == 0 || arguments.get(0) == null) {
                    return false;
                }
                return !((Boolean) arguments.get(0));
            }
        });
        registerOperator(new Operator(",", Integer.MIN_VALUE) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                List<Object> list = new ArrayList<Object>();
                if (leftValue instanceof Collection) {
                    list.addAll((Collection<?>) leftValue);
                } else {
                    list.add(leftValue);
                }

                if (rightValue instanceof Collection) {
                    list.addAll((Collection) rightValue);
                } else {
                    list.add(rightValue);
                }
                return list;

            }
        });
        registerOperator(new Operator(">=", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                return Double.compare(((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue()) >= 0;
            }
        });

        registerOperator(new Operator("<=", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }

                return Double.compare(((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue()) <= 0;
            }
        });
        registerOperator(new Operator(">", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }

                return Double.compare(((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue()) > 0;
            }
        });

        registerOperator(new Operator("<", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }

                return Double.compare(((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue()) < 0;
            }
        });
        registerOperator(new Operator("&&", 10) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                if (!leftValue.getClass().getSimpleName().equalsIgnoreCase(rightValue.getClass().getSimpleName())) {
                    return false;
                }
                return (Boolean) leftValue && (Boolean) rightValue;
            }
        });
        registerOperator(new Operator("||", 9) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                if (!leftValue.getClass().getSimpleName().equalsIgnoreCase(rightValue.getClass().getSimpleName())) {
                    return false;
                }
                return (Boolean) leftValue || (Boolean) rightValue;
            }
        });
        registerOperator(new Operator("==", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    return true;
                } else if (leftValue == null || rightValue == null) {
                    return false;
                } else if (leftValue instanceof Number && rightValue instanceof Number) {
                    return Double.compare(((Number) leftValue).doubleValue(), ((Number) rightValue).doubleValue()) == 0;
                } else if (!leftValue.getClass().getSimpleName().equals(rightValue.getClass().getSimpleName())) {
                    return false;
                }
                return leftValue.toString().equals(rightValue.toString());
            }
        });

        registerOperator(new Operator("!=", 100) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                return !(Boolean) Evaluator.get().getOperator("==").onEvaluation(leftValue, rightValue);
            }
        });

        registerOperator(new Operator("+", 101) {
            @Override
            public Object onEvaluation(Object leftValue, Object rightValue) {
                if (leftValue == null && rightValue == null) {
                    return null;
                } else if (leftValue == null) {
                    if (rightValue instanceof String) {
                        return rightValue.toString();
                    } else {
                        return null;// throw Exception
                    }
                } else if (rightValue == null) {
                    if (leftValue instanceof String) {
                        return leftValue.toString();
                    } else {
                        return null;
                    }
                } else {
                    if (leftValue instanceof Number && rightValue instanceof Number) {
                        return ((Number) leftValue).doubleValue() + ((Number) rightValue).doubleValue();
                    } else {
                        return leftValue.toString() + rightValue.toString();
                    }
                }
            }
        });

        registerOperator(new Operator("-", 101) {
            @Override
            public Object onEvaluation(Object leftOperand, Object rightOperand) {
                if (leftOperand == null || rightOperand == null) {
                    return null;
                }
                return ((Number) leftOperand).doubleValue() - ((Number) rightOperand).doubleValue();
            }
        });

        registerOperator(new Operator("*", 102) {
            @Override
            public Object onEvaluation(Object leftOperand, Object rightOperand) {
                if (leftOperand == null || rightOperand == null) {
                    return null;
                }
                return ((Number) leftOperand).doubleValue() * ((Number) rightOperand).doubleValue();
            }
        });
        registerOperator(new Operator("/", 102) {
            @Override
            public Object onEvaluation(Object leftOperand, Object rightOperand) {
                if (leftOperand == null || rightOperand == null) {
                    return null;
                }
                return ((Number) leftOperand).doubleValue() / ((Number) rightOperand).doubleValue();
            }
        });

        registerOperator(new Operator("^", 103) {
            @Override
            public Object onEvaluation(Object leftOperand, Object rightOperand) {
                if (leftOperand == null || rightOperand == null) {
                    return null;
                }
                return Math.pow(((Number) leftOperand).doubleValue(), ((Number) rightOperand).doubleValue());
            }
        });
    }

}
