// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet.nodes.conductivitytester;

import java.awt.geom.Point2D;
import java.util.EventListener;

import edu.umd.cs.piccolo.util.PDimension;

/**
 * Interface implemented by all conductivity testers, used by ConductivityTesterNode.
 * All locations are in model coordinates.
 *
 * @author Sam Reid
 */
public interface IConductivityTester {

    boolean isVisible();

    /**
     * Gets a reference to the location of the tester, in model coordinates.
     * Clients should take care not to modify this point, or pass it to methods
     * that might modify the point.
     *
     * @return
     */
    Point2D getLocationReference();

    /**
     * Sets the location of the positive probe.
     * This is in the same coordinate frame as (not relative to) getLocation.
     *
     * @param x
     * @param y
     */
    void setPositiveProbeLocation( double x, double y );

    Point2D getPositiveProbeLocationReference();

    /**
     * Sets the location of the negative probe.
     * This is in the same coordinate frame as (not relative to) getLocation.
     *
     * @param x
     * @param y
     */
    void setNegativeProbeLocation( double x, double y );

    Point2D getNegativeProbeLocationReference();

    /**
     * Gets the dimensions of the probe, in model coordinates.
     *
     * @return
     */
    PDimension getProbeSizeReference();

    /**
     * Gets the brightness of the tester, a number between 0 and 1 inclusive.
     * 0 indicates no conductivity (open circuit)
     * 1 indicates maximum conductivity that the device can display
     *
     * @return 0-1
     */
    double getBrightness();

    void addConductivityTesterChangeListener( ConductivityTesterChangeListener conductivityTesterChangeListener );

    public interface ConductivityTesterChangeListener extends EventListener {

        public void brightnessChanged();

        public void positiveProbeLocationChanged();

        public void negativeProbeLocationChanged();
    }
}