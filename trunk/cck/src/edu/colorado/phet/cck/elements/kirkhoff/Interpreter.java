package edu.colorado.phet.cck.elements.kirkhoff;

/**
 * User: Sam Reid
 * Date: Nov 12, 2003
 * Time: 12:38:20 PM
 * Copyright (c) Nov 12, 2003 by Sam Reid
 */
public interface Interpreter {
    public double getCurrent(int componentIndex);

    public double getVoltage(int componentIndex);

    public boolean isValidSolution();
}
