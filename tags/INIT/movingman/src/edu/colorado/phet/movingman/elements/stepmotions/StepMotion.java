package edu.colorado.phet.movingman.elements.stepmotions;

import edu.colorado.phet.movingman.elements.Man;

/**
 * User: Sam Reid
 * Date: Jul 15, 2003
 * Time: 3:37:59 PM
 * Copyright (c) Jul 15, 2003 by Sam Reid
 */
public interface StepMotion {
    public double stepInTime(Man man, double dt);
//    void setInitialPosition(double x);
}
