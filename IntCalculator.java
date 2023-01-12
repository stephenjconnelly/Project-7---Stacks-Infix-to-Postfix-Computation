import java.util.Arrays;
import java.util.HashMap;

/**
 * Command line calculator that works with only integers.
 * The command line argument must be put in quotes, as in:
 * java IntCalculator "(2 x 3) ^ 2"
 * @author Stephen Connelly Sjc2235
 * @version 1.0 October 12, 2022
 */

public class IntCalculator {
    public static final HashMap<Character, Integer>
        binaryOperatorPrecedenceMap = new HashMap<>(),
        unaryOperatorPrecedenceMap = new HashMap<>();
    /**
     * userExpression is the infix expression entered by the user.
     * infixExpression is similar to the user expression, except that each
     * negation minus sign has been replaced with a ~ to avoid ambiguity im
     * the conversion and evaluation algorithms.
     */
    private String infixExpression, userExpression, postfixExpression;
    private Error error;

    /**
     * Operators and their precedences. Operators with higher precedences are
     * evaluated first.
     */
    static {
        unaryOperatorPrecedenceMap.put('~', 4);
        binaryOperatorPrecedenceMap.put('^', 3);
        binaryOperatorPrecedenceMap.put('x', 2);
        binaryOperatorPrecedenceMap.put('/', 2);
        binaryOperatorPrecedenceMap.put('%', 2);
        binaryOperatorPrecedenceMap.put('+', 1);
        binaryOperatorPrecedenceMap.put('-', 1);
    }

    /**
     * Creates an instance of an integer calculator.
     * @param expression the infix expression supplied by the user
     */
    public IntCalculator(String expression) {
        setExpression(expression);
    }

    /**
     * Converts the infix expression stored in the instance variable
     * 'infixExpression' into postfix, storing the result in instance variable
     * 'postfixExpression'. Each symbol in the postfix expression is
     * separated by a space.
     * @return a string containing the postfix expression
     * @throws StackException if an error occurs when calling a method on the
     * stack. This should not happen. The throws clause is there so that you
     * don't need any try-catch blocks in the body of this method.
     */

    public String infixToPostfix() throws StackException, MyStack.StackException {
        MyStack<Character> sta = new MyArrayList<>();
        StringBuilder postfix = new StringBuilder();
        for(int j = 0; j<infixExpression.length(); j++){
            if(infixExpression.charAt(j) == '^'){
                if(!isLeftAssociative(infixExpression.charAt(j))){
                    while(sta.isEmpty() == false && precedence(infixExpression.charAt(j)) < precedence(sta.peek())){
                        postfix.append(sta.pop());
                        postfix.append(' ');
                    }
                }else{
                    while(sta.isEmpty() == false && precedence(infixExpression.charAt(j)) <= precedence(sta.peek())){
                        postfix.append(sta.pop());
                        postfix.append(' ');
                    }
                }
                sta.push(infixExpression.charAt(j));
            }else
            if(isWhiteSpace(infixExpression.charAt(j)) == true){
                continue;
            }else if(isDigit(infixExpression.charAt(j)) == true){
                postfix.append(infixExpression.charAt(j));
                while(j < infixExpression.length()-1 && isDigit(infixExpression.charAt(j+1))){
                    postfix.append(infixExpression.charAt(j+1));
                    j++;
                }
               if(infixExpression.length() != 1){
                   postfix.append(' ');
               }
            }else if(infixExpression.charAt(j) == '('){//open
                char c = infixExpression.charAt(j);
                sta.push(infixExpression.charAt(j));
            }else if(infixExpression.charAt(j) == ')'){//close
                while(sta.isEmpty() == false && sta.peek() != '('){
                    postfix.append(sta.pop());
                    postfix.append(' ');
                }
                if(sta.isEmpty() == false ){
                    for(int i = 0; i<sta.size(); i++){
                        if(sta.peek() == '('){
                            sta.pop();
                        }
                    }
                }
            }else if(isUnaryOperator(infixExpression.charAt(j)) == true){
                sta.push(infixExpression.charAt(j));
            }else if(isBinaryOperator(infixExpression.charAt(j)) || infixExpression.charAt(j) == '^'){
                while(sta.isEmpty() == false && precedence(infixExpression.charAt(j)) <= precedence(sta.peek())){
                    postfix.append(sta.pop());
                    postfix.append(' ');
                }
                sta.push(infixExpression.charAt(j));
            }else if(isUnencodedUnaryOperator(infixExpression.charAt(j))){
                sta.push(infixExpression.charAt(j));
            }
        }
        while(sta.isEmpty() == false){
            if(!isWhiteSpace(postfix.charAt(postfix.length()-1))){
                postfix.append(' ');
            }
            postfix.append(sta.pop());
            if(sta.size() > 1){
                postfix.append(' ');
            }
        }
        postfixExpression = postfix.toString();
        return this.postfixExpression;
    }

    /**
     * Evaluates the postfix expression and returns the integer value of the
     * expression. All operations are performed with integers.
     * @return the integer value that results after evaluating the postfix
     * expression
     * @throws StackException if an error occurs when calling a method on the
     * stack. This should not happen. The throws clause is there so that you
     * don't need any try-catch blocks in the body of this method.
     * @throws IllegalArgumentException if an attempt to divide or mod by zero
     * is encountered. The message of the exception reads:
     * "Cannot evaluate expression, division by zero."
     * An IllegalArgumentException si also thrown if the user attempts to
     * compute 0^0. The message of the exception reads:
     * "Cannot evaluate expression, 0^0 is undefined."
     */
    public int evaluatePostfix()
            throws StackException, IllegalArgumentException, MyStack.StackException {
        MyStack<Integer> sta = new MyArrayList<>();
        for(int j = 0; j<postfixExpression.length(); j++) {
            if (isDigit(postfixExpression.charAt(j)) == true) {
                String stringValue = String.valueOf(postfixExpression.charAt(j));
            while(j < postfixExpression.length()-1 && isDigit(postfixExpression.charAt(j+1))){
                j++;
                stringValue+= String.valueOf(postfixExpression.charAt(j));
                }
                sta.push(Integer.valueOf(stringValue));
            }else if (isUnaryOperator(postfixExpression.charAt(j)) == true) {
                if (sta.isEmpty() == false){
                    sta.push(-sta.pop());
                }
            } else if (isBinaryOperator(postfixExpression.charAt(j)) == true || postfixExpression.charAt(j) == '^') {//close
                int a = sta.pop();
                int b = sta.pop();
                if(postfixExpression.charAt(j) == 'x'){
                    int c = a*b;
                    sta.push(c);
                }else if(postfixExpression.charAt(j) == '+'){
                    int c = a+b;
                    sta.push(c);
                }else if(postfixExpression.charAt(j) == '-'){
                    int c = b-a;
                    sta.push(c);
                }else if(postfixExpression.charAt(j) == '^'){
                    if(a ==0) {
                        throw new ArithmeticException(
                                "Cannot evaluate expression, 0^0 is undefined.");
                    }
                    int c = (int) Math.pow(b, a);
                    sta.push(c);
                } else if(postfixExpression.charAt(j) == '%') {
                    if(a==0){
                        throw new ArithmeticException(
                                "Cannot evaluate expression, division by zero.");
                    }
                    int c  = b%a;
                    sta.push(c);

                }else{//illegal argument exception that checks if a is 0
                    if(a==0) {
                        throw new ArithmeticException(
                                "Cannot evaluate expression, division by zero.");
                    }
                    int c = b/a;
                    sta.push(c);
                }
            }
        }
        return sta.pop();
    }

    /**
     * Sets the expression instance variables after parsing the characters in
     * input expression. If an invalid symbol is discovered, the internal
     * Error object is set accordingly.
     * @param expression the infix expression supplied by the user
     */
    public void setExpression(String expression) {
        error = null;
        boolean leading = true;
        StringBuilder internalBuilder = new StringBuilder(),
                      externalBuilder = new StringBuilder();
        for (int i = 0, len = expression.length(); i < len; i++) {
            char symbol = expression.charAt(i);
            if (isWhiteSpace(symbol)) {
                internalBuilder.append(symbol);
                externalBuilder.append(symbol);
                continue;
            }
            if (!isValid(symbol)) {
                if (error == null) {
                    error = new Error(getErrorHeader(i) + "Unexpected symbol '"
                            + symbol + "' found at position " + (i + 1) + ".",
                            0, i);
                }
            }
            char newSymbol = symbol;
            if (leading && symbol == '-') {
                newSymbol = '~';
            }
            leading =  (symbol == '(' || isUnencodedUnaryOperator(symbol)
                                      || isBinaryOperator(symbol));
            internalBuilder.append(newSymbol);
            externalBuilder.append(symbol);
        }
        this.infixExpression = internalBuilder.toString();
        this.userExpression = externalBuilder.toString();
    }

    /**
     * Returns the precedence of the given operator.
     * @param operator the operator of which to find the precedence
     * @return the precedence of the given operator
     */

    public static int precedence(char operator) {
        Integer val = binaryOperatorPrecedenceMap.get(operator);
        if (val == null) {
            val = unaryOperatorPrecedenceMap.get(operator);
        }
        return val != null ? val : -1;
    }

    /**
     * Returns true if the symbol is valid; false otherwise.
     * @param symbol the symbol to check
     * @return true if the symbol is valid
     */
    public static boolean isValid(char symbol) {
        return isBinaryOperator(symbol) ||
               isUnaryOperator(symbol) ||
               isDigit(symbol) ||
               isParenthesis(symbol);
    }

    /**
     * Returns true if the symbol is an operator; false otherwise.
     * @param symbol the symbol to check
     * @return true if the symbol is an operator
     */
    public static boolean isOperator(char symbol) {
        return binaryOperatorPrecedenceMap.containsKey(symbol)
                || unaryOperatorPrecedenceMap.containsKey(symbol);
    }

    /**
     * Returns true if the symbol is a binary operator; false otherwise.
     * @param symbol the symbol to check
     * @return true if the symbol is a binary operator
     */
    public static boolean isBinaryOperator(char symbol) {
        return binaryOperatorPrecedenceMap.containsKey(symbol);
    }

    /**
     * Returns true if the symbol is an unencoded unary operator -; false
     * otherwise.
     * @param symbol the symbol to check
     * @return true if the symbol is an unencoded unary operator
     */
    public static boolean isUnencodedUnaryOperator(char symbol) {
        return symbol == '-';
    }

    /**
     * Returns true if the symbol is a unary operator; false otherwise. ~ is
     * the only unary operator.
     * @param symbol the character to evaluate
     * @return true if the symbol is a unary operator
     */
    public static boolean isUnaryOperator(char symbol) {
        return unaryOperatorPrecedenceMap.containsKey(symbol);
    }

    /**
     * Returns true if the symbol is a digit 0 through 9; false otherwise.
     * @param symbol the character to evaluate
     * @return true if the symbol is a digit
     */
    public static boolean isDigit(char symbol) {
        return symbol >= 48 && symbol <= 57;
    }

    /**
     * Returns true if the symbol is whitespace; false otherwise. Whitespace
     * characters include space, tab, and new line characters.
     * @param symbol the character to evaluate
     * @return true if the symbol is a whitespace character
     */
    public static boolean isWhiteSpace(char symbol) {
        return symbol == ' ' || symbol == '\t' || symbol == '\n';
    }

    /**
     * Returns true if the symbol is an opening or closing parenthesis; false
     * otherwise.
     * @param symbol the character to evaluate
     * @return true if the symbol is an opening or closing parenthesis
     */
    public static boolean isParenthesis(char symbol) {
        return symbol == '(' || symbol == ')';
    }

    /**
     * Returns true if the symbol is left associative; false otherwise.
     * @param symbol the character to evaluate
     * @return true if the symbol is left associative
     */
    public static boolean isLeftAssociative(char symbol) {
        // Only ^ is right associative. All other symbols are left associative.
        return symbol != '^';
    }

    /**
     * Returns a String of spaces followed by a caret and a space, so that the
     * caret points to the first erroneous character in the
     * expression.
     * @param numSpaces the number of spaces before the caret
     * @return a String of spaces followed by a caret and a space
     */
    public static String getErrorHeader(int numSpaces) {
        char[] charArray = new char[numSpaces];
        Arrays.fill(charArray, ' ');
        return new String(charArray) + "^ ";
    }

    /**
     * Returns true if instance variable 'infixExpression' is valid; false
     * otherwise. At this point, all characters in the expression are known to
     * be valid, but the expression itself may not be well-formed.
     * @return true if the infix expression is valid
     * @throws StackException if an error occurs when calling a method on the
     * stack. This should not happen. The throws clause is there so that you
     * don't need any try-catch blocks in the body of this method.
     */
    public boolean containsValidExpression() throws StackException, MyStack.StackException {
        if (error != null) {
            return false;
        }
        MyStack<Symbol> stack = new MyArrayList<>();
        StringBuilder numBuilder = new StringBuilder();
        boolean leading = true,
                operandFound = false,
                binaryOperatorFound = false;
        int len = infixExpression.length();
        for (int i = 0; i < len; i++) {
            char symbol = infixExpression.charAt(i);
            if (isWhiteSpace(symbol)) {
                if (numBuilder.length() > 0) {
                    operandFound = true;
                    numBuilder = new StringBuilder();
                }
                continue;
            }
            boolean isBinaryOperator = isBinaryOperator(symbol),
                    isOperand = isDigit(symbol);
            if (isOperand) {
                if (operandFound && !binaryOperatorFound) {
                    error = new Error(getErrorHeader(i - 1)
                            + "Expected operator at position " + i + ".",
                            0, i - 1);
                    return false;
                }
                numBuilder.append(symbol);
                binaryOperatorFound = false;
            } else if (isBinaryOperator) {
                binaryOperatorFound = true;
                operandFound = false;
            }
            if (symbol == ')' || isBinaryOperator) {
                if (leading) {
                    error = new Error(getErrorHeader(i)
                            + "Expected operand, but found '" + symbol
                            + "' at position " + (i + 1) + ".", 0, i);
                    return false;
                }
            } else if (!isOperand && !leading) {
                error = new Error(getErrorHeader(i)
                        + "Expected operator, but found '" + symbol
                        + "' found at position " + (i + 1) + ".", 0, i);
                return false;
            }
            if (symbol == '(') {
                stack.push(new Symbol(symbol, 0, i));
            } else if (symbol == ')') {
                if (stack.isEmpty()) {
                    error = new Error(getErrorHeader(i)
                            + "Unmatched ')' found at position "
                            + (i + 1) + ".", 0, i);
                    return false;
                }
                stack.pop();
                binaryOperatorFound = false;
                operandFound = true;
            }
            leading = isBinaryOperator || isUnaryOperator(symbol) ||
                      symbol == '(';
        }
        if (leading) {
            error = new Error(getErrorHeader(len)
                    + "Missing operand at position " + (len + 1) + ".", 0,
                    len);
            return false;
        }
        if (!stack.isEmpty()) {
            Symbol stackTop = stack.pop();
            error = new Error(getErrorHeader(stackTop.position)
                    + "Unmatched '(' found at position "
                    + (stackTop.position + 1) + ".", 0,stackTop.position);
            return false;
        }
        return true;
    }

    /**
     * Returns the internal error message, if one exists.
     * @return the internal error message
     */
    public String getErrorMessage() {
        return error == null ? "No errors found." : error.message;
    }

    /**
     * Returns the infix expression supplied by the user.
     * @return the infix expression supplied by the user
     */
    public String getExpression() {
        return userExpression;
    }

    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder();
        for (String arg : args) {
            builder.append(arg);
        }
        String input = builder.toString().trim();
        if (input.length() == 0) {
            System.err.println("Usage: java IntCalculator <expression>");
            System.exit(1);
        }
        IntCalculator calc = new IntCalculator(input);

        try {
            if (calc.containsValidExpression()) {
                String postfix = calc.infixToPostfix();
                System.out.println("Postfix expression: " + postfix);
                System.out.println("Evaluation:         "
                        + calc.evaluatePostfix());
            } else {
                System.err.println(calc.getExpression());
                System.err.println(calc.getErrorMessage());
            }
        } catch (Exception e) {
            System.err.println("Error:              " + e.getMessage());
        }
    }
}

