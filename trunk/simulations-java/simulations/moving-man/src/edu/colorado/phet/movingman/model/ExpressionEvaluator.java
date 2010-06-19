package edu.colorado.phet.movingman.model;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Sam Reid
 */
public class ExpressionEvaluator {
    private Interpreter interpreter = new Interpreter();
    private String expression;

    public ExpressionEvaluator(String expression) {
        this.expression = expression;
    }

    public double evaluate(double time) {
        String timeString = "(" + time + ")";

        String equation = expression.replaceAll("cos", "Math.cos");
        equation = equation.replaceAll("sin", "Math.sin");
        equation = equation.replaceAll("pi", "Math.PI");
        equation = equation.replaceAll("log", "Math.log");
        equation = equation.replaceAll("pow", "Math.pow");

        double x = 0;
        try {
            Object value = interpreter.eval("t=" + timeString + "; y=" + equation);
            x = ((Number) value).doubleValue();
        }
        catch (EvalError evalError) {
            evalError.printStackTrace();
        }
        return x;
    }

    public static void main(String[] args) {
        System.out.println("new ExpressionEvaluator().evaluate(0.1) = " + new ExpressionEvaluator("sin(t)").evaluate(0.1));
    }
}
