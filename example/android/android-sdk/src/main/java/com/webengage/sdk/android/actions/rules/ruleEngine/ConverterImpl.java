package com.webengage.sdk.android.actions.rules.ruleEngine;


class ConverterImpl implements Converter {
    @Override
    public Object convertStringToObject(String input) {
        if (input.equalsIgnoreCase("null")) {
            return null;
        } else if (input.equalsIgnoreCase("true") || input.equalsIgnoreCase("false")) {
            return Boolean.valueOf(input);
        } else {
            try {
                return Long.valueOf(input);
            } catch (NumberFormatException e1) {
                try {
                    return Double.valueOf(input);
                } catch (NumberFormatException e2) {
                    if (!input.isEmpty()) {
                        if (input.charAt(0) == '"' && input.charAt(input.length() - 1) == '"')
                            input = input.substring(1, input.length() - 1);
                    }
                    return input;
                }
            }
        }
    }
}
