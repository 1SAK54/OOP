package ru.nsu.vorona.Task_1_1_3;

/**
 * Парсер для преобразования строкового представления выражения в дерево объектов Expression.
 */
public class ExpressionParser {

    /**
     * Парсит строку и создаёт соответствующее выражение.
     *
     * @param str строка с математическим выражением
     * @return объект Expression, представляющий выражение
     * @throws IllegalArgumentException если формат строки некорректен
     */
    public static Expression parse(String str) {
        str = str.trim();
        if (str.startsWith("(") && str.endsWith(")")) {
            if (isBalanced(str.substring(1, str.length() - 1))) {
                str = str.substring(1, str.length() - 1).trim();
            }
        }

        if (isNumber(str)) {
            return new Number(Double.parseDouble(str));
        }
        if (isVariable(str)) {
            return new Variable(str);
        }

        int opPos = findMainOperator(str);
        if (opPos == -1) {
            throw new IllegalArgumentException("Invalid expression: " + str);
        }

        String leftPart = str.substring(0, opPos).trim();
        String rightPart = str.substring(opPos + 1).trim();
        char operator = str.charAt(opPos);

        Expression leftExpr = parse(leftPart);
        Expression rightExpr = parse(rightPart);

        switch (operator) {
            case '+':
                return new Add(leftExpr, rightExpr);
            case '-':
                return new Sub(leftExpr, rightExpr);
            case '*':
                return new Mul(leftExpr, rightExpr);
            case '/':
                return new Div(leftExpr, rightExpr);
            default:
                throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }

    private static boolean isBalanced(String s) {
        int level = 0;
        for (char c : s.toCharArray()) {
            if (c == '(') level++;
            if (c == ')') level--;
            if (level < 0) return false;
        }
        return level == 0;
    }

    /**
     * Проверяет, является ли строка числом.
     *
     * @param s проверяемая строка
     * @return true, если строка представляет число
     */
    private static boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Проверяет, является ли строка переменной.
     *
     * @param s проверяемая строка
     * @return true, если строка представляет переменную
     */
    private static boolean isVariable(String s) {
        return s.matches("[a-zA-Z]+");
    }

    /**
     * Находит позицию главного оператора в выражении.
     *
     * @param expr строка с выражением
     * @return индекс главного оператора или -1, если не найден
     */
    private static int findMainOperator(String expr) {
        int level = 0;

        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') level++;
            else if (c == ')') level--;
            else if (level == 0 && (c == '+' || c == '-')) {
                return i;
            }
        }

        level = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') level++;
            else if (c == ')') level--;
            else if (level == 0 && (c == '*' || c == '/')) {
                return i;
            }
        }
        
        return -1;
    }
}
