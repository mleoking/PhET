/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.model;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.mri.model.Electromagnet;

import java.awt.geom.Point2D;

/**
 * GradientElectromagnet
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class GradientElectromagnet extends Electromagnet {
    private Gradient gradient;
//    private Point2D position;
//    private double width;
//    private double fieldOffset;


    public GradientElectromagnet( Point2D position, double width, double height, IClock clock, Gradient gradient ) {
        super( position, width, height, clock );
        this.gradient = gradient;
    }

    public double getFieldStrengthAt( double x ){
        return gradient.getValueAt( x );
    }

    public void setGradient( Gradient gradient ) {
        this.gradient = gradient;
    }

    //--------------------------------------------------------------------------------------------------
    // Gradient specifications
    //--------------------------------------------------------------------------------------------------
    public static interface Gradient {
        double getValueAt( double x );
    }

    public static class LinearGradient implements Gradient {
        private double m,b;

        public LinearGradient( double m, double b ) {
            this.m = m;
            this.b = b;
        }

        public double getValueAt( double x ) {
            return m * x + b;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of ModelElement
    //--------------------------------------------------------------------------------------------------

    public void stepInTime( double dt ) {

    }
}
