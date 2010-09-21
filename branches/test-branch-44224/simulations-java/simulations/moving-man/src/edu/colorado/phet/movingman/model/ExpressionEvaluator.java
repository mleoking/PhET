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

    public double evaluate(double time) throws EvalError {
        //none of this is internationalized because it is beanshell code that must be executed.
        String timeString = "(" + time + ")";

        String equation = expression.replaceAll("cos", "Math.cos");
        equation = equation.replaceAll("sin", "Math.sin");
        equation = equation.replaceAll("pi", "Math.PI");
        equation = equation.replaceAll("log", "Math.log");
        equation = equation.replaceAll("pow", "Math.pow");

        Object value = interpreter.eval("t=" + timeString + "; y=" + equation);
        return ((Number) value).doubleValue();
    }

    public static void main(String[] args) throws EvalError {
        System.out.println("new ExpressionEvaluator().evaluate(0.1) = " + new ExpressionEvaluator("sin(t)").evaluate(0.1));
    }
}
