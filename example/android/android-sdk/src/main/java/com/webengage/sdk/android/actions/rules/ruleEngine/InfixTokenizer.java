package com.webengage.sdk.android.actions.rules.ruleEngine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class InfixTokenizer implements Iterator<String> {
    private String expression = null;
    private int pos = 0;

    private char ch = 0;
    private String token = "";
    private int length = 0, i = 0, j = 0;

    private List<Integer> points;
    private boolean useLegacy = false;


    public InfixTokenizer(String expression) {
        this(expression, false);
    }

    public InfixTokenizer(String expression, boolean useLegacy) {
        this.expression = expression.trim();
        this.length = this.expression.length();
        points = new ArrayList<Integer>();
        this.useLegacy = useLegacy;
        if (this.useLegacy) {
            tokenizeExpression();
        }

    }

    @Override
    public boolean hasNext() {
        if (this.useLegacy) {
            return (pos < points.size());
        } else {
            return i < this.expression.length();
        }
    }

    @Override
    public String next() {
        if (this.useLegacy) {
            token = this.expression.substring(points.get(pos++), points.get(pos++));
            return token;
        } else {
            return nextToken();
        }
    }

    @Override
    public void remove() {

    }


    public String nextToken() {
        StringBuilder sb = new StringBuilder();
        boolean escaped = false;
        boolean isInsideQuotes = false;
        while (i < expression.length()) {
            ch = expression.charAt(i);
            if (isInsideQuotes) {
                switch (ch) {
                    case 't':
                        if (escaped) {
                            sb.append('\t');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 'b':
                        if (escaped) {
                            sb.append('\b');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 'n':
                        if (escaped) {
                            sb.append('\n');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 'r':
                        if (escaped) {
                            sb.append('\r');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case 'f':
                        if (escaped) {
                            sb.append('\f');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case '\'':
                        if (escaped) {
                            sb.append('\'');
                            escaped = false;
                        } else {
                            sb.append(ch);
                        }
                        break;
                    case '"':
                        if (escaped) {
                            sb.append(ch);
                            escaped = false;
                        } else {
                            sb.append(ch);
                            isInsideQuotes = false;
                            i++;
                            return sb.toString();
                        }
                        break;
                    case '\\':
                        escaped = !escaped;
                        if (!escaped) {
                            sb.append(ch);
                        }

                        break;
                    default:
                        if (escaped) {
                            escaped = false;
                            sb.append(ch);
                        } else {
                            sb.append(ch);
                        }
                        break;

                }
            } else {
                if (!Character.isJavaIdentifierStart(ch)) {
                    switch (ch) {
                        case '\"':
                            isInsideQuotes = true;
                            sb.append(ch);
                            break;
                        case ' ':
                            break;
                        case '|':
                            if (ch == justNextChar(i)) {
                                i = i + 2;
                                return "||";
                            } else {
                                i = i + 1;
                                return "|";
                            }
                        case '&':
                            if (ch == justNextChar(i)) {
                                i = i + 2;
                                return "&&";
                            } else {
                                i = i + 1;
                                return "&";
                            }
                        case '^':
                            i += 1;
                            return "^";
                        case '=':
                            if (ch == justNextChar(i)) {
                                i += 2;
                                return "==";
                            } else {
                                i += 1;
                                return "=";
                            }
                        case '!':
                            if ('=' == justNextChar(i)) {
                                i += 2;
                                return "!=";
                            } else {
                                i += 1;
                                return "!";
                            }
                        case '<':
                            if ('=' == justNextChar(i)) {
                                i += 2;
                                return "<=";
                            } else if ('<' == justNextChar(i)) {
                                i += 2;
                                return "<<";
                            } else {
                                i += 1;
                                return "<";
                            }
                        case '>':
                            if ('=' == justNextChar(i)) {
                                i += 2;
                                return ">=";
                            } else if ('>' == justNextChar(i)) {
                                i += 2;
                                return ">>";
                            } else {
                                i += 1;
                                return ">";
                            }
                        case '+':
                            i += 1;
                            return ch + "";
                        case '-':
                            if ('>' == justNextChar(i)) {
                                i += 2;
                                return "->";
                            }
                        case '~':
                        case '*':
                        case '%':
                        case '/':
                        case '(':
                        case ')':
                        case '[':
                        case ']':
                        case ',':
                            i += 1;
                            return ch + "";
                        default:
                            if ((Character.isDigit(ch) || (ch == '.' && Character.isDigit(nextChar(i))))) {
                                boolean isDotFound = false;
                                while (i < expression.length()) {
                                    ch = expression.charAt(i);
                                    if (Character.isDigit(ch)) {
                                        sb.append(ch);
                                    } else if (ch == '.') {
                                        if (!isDotFound) {
                                            sb.append(ch);
                                            isDotFound = true;
                                        } else {
                                            break;
                                        }
                                    } else {
                                        break;
                                    }
                                    i++;
                                }
                                return sb.toString();
                            } else {
                                i += 1;
                                return ch + "";
                            }
                    }
                } else {
                    while (i < expression.length()) {
                        ch = expression.charAt(i);
                        if (!Character.isJavaIdentifierPart(ch)) {
                            break;
                        } else {
                            sb.append(ch);
                        }
                        i++;
                    }
                    return sb.toString();
                }
            }
            i++;
        }
        return sb.toString();
    }


    int nextChar(int pos) {
        int j = pos + 1;
        while (j < expression.length()) {
            if (expression.charAt(j) != ' ') {
                return expression.charAt(j);
            }
            j++;
        }
        return -1;
    }

    private int justNextChar(int pos) {
        return pos + 1 < expression.length() ? expression.charAt(pos + 1) : -1;
    }


    private void tokenizeExpression() {
        StringBuilder sb = new StringBuilder();
        int maxTokenIndex = -1;
        int tokenLength = 0;
        boolean isInsideQuotes = false;
        int backSlashState = -1;
        for (j = 0; j < this.expression.length(); j++) {
            sb.setLength(0);
            maxTokenIndex = -1;
            tokenLength = 0;
            for (i = j; i < this.expression.length(); i++) {
                ch = this.expression.charAt(i);
                sb.append(ch);
                if (ch == '\"' && backSlashState == -1 && isInsideQuotes) {
                    isInsideQuotes = false;
                    j = i;
                    break;
                } else if (ch == '\"' && backSlashState == 1) {
                    backSlashState = -1;
                } else if (sb.toString().equals("\"") && backSlashState == -1 && !isInsideQuotes) {
                    isInsideQuotes = true;
                } else if (ch == '\\' && isInsideQuotes) {
                    backSlashState *= -1;
                } else {
                    if (!isInsideQuotes && Evaluator.get().isValidKeyword(sb.toString())) {
                        tokenLength = sb.length();
                        maxTokenIndex = i;
                    }
                }
            }
            if (maxTokenIndex != -1) {
                if (points.size() == 0) {
                    points.add(0);
                    points.add(maxTokenIndex - tokenLength + 1);
                }
                if (points.size() > 0 && points.get(points.size() - 1) != maxTokenIndex - tokenLength + 1) {
                    points.add(points.get(points.size() - 1));
                    points.add(maxTokenIndex - tokenLength + 1);
                }
                points.add(maxTokenIndex - tokenLength + 1);
                points.add(maxTokenIndex + 1);
                j = maxTokenIndex;
            }
        }
        if (points.size() == 0) {
            points.add(0);
            points.add(length);
        }
        if (points.size() > 0 && points.get(points.size() - 1) != length) {
            points.add(points.get(points.size() - 1));
            points.add(length);
        }

    }


}


