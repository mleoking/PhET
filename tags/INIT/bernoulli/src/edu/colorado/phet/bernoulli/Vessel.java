package edu.colorado.phet.bernoulli;

import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.bernoulli.meter.Barometer;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Sep 25, 2003
 * Time: 11:05:46 AM
 * To change this template use Options | File Templates.
 */
public abstract class Vessel extends SimpleObservable {
    abstract public boolean waterContainsPoint( double x, double y );

    abstract public double getPressure( double x, double y );

}
