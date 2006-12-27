package edu.colorado.phet.ec2.elements.energy;

import edu.colorado.phet.ec2.elements.car.Car;

/**
 * User: Sam Reid
 * Date: Jul 25, 2003
 * Time: 1:28:23 PM
 * Copyright (c) Jul 25, 2003 by Sam Reid
 */
public interface EnergyObserver {
    public void energyChanged( Car c, double ke, double pe, double friction );
}
