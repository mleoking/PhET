/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.semiconductor.common.photons;

import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Mar 2, 2004
 * Time: 1:29:55 AM
 * Copyright (c) Mar 2, 2004 by Sam Reid
 */
public class SinCurveShape {
    double phase;
    double frequency;
    private int numSections;
    private double amplitude;
    private DoubleGeneralPath path;

    public SinCurveShape( double dist, double phase, double frequency, int numSections, double amplitude, boolean fixedTip ) {
        //tip is at x=0, y=val
        this.phase = phase;
        this.frequency = frequency;
        this.numSections = numSections;
        this.amplitude = amplitude;
        if( fixedTip ) {
            path = null;
        }
        else {
            path = new DoubleGeneralPath( new PhetVector( 0, 0 ) );
        }
        for( int i = 1; i < numSections; i++ ) {
            double x = i * dist / numSections;
            double val = amplitude * Math.sin( x * frequency + phase );
            if( path == null ) {
                path = new DoubleGeneralPath( x, val );
            }
            else {
                path.lineTo( x, val );
            }
        }
    }

    public Shape getShape() {
        return path.getGeneralPath();
    }
}
