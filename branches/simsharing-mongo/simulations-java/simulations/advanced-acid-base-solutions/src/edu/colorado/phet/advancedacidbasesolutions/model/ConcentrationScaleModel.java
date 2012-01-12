
// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.model;

/**
 * Model for mapping a concentration value to a font size.
 * Used for scaling reaction equations and equilibrium expressions.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationScaleModel {

    // concentration log10 range
    private static final double C_MIN = -16;
    private static final double C_MAX = 2;
    private static final double C_LENGTH = C_MAX - C_MIN;
    
    // font point size range
    private static final double T_MIN = 3;
    private static final double T_MAX = 72;
    private static final double T_LENGTH = T_MAX - T_MIN;
    
    private ConcentrationScaleModel() {}

    /**
     * Given a concentration in mol/L, get a font size in points.
     */
    public static double getFontSize( double concentration ) {
        double T = T_MIN;
        if ( concentration > 0 ) {
            final double C = Math.log10( concentration );
            final double D = ( T_LENGTH - C_LENGTH ) / ( ( C_MAX * C_MAX ) - ( C_MIN * C_MIN ) - ( 2 * C_LENGTH * C_MIN ) );
            final double B = 1 - ( 2 * D * C_MIN );
            final double A = T_MIN - ( B * C_MIN ) - ( D * C_MIN * C_MIN );
            T = A + ( B * C ) + ( D * C * C );
        }
        return T;
    }
    
    public static void main( String[] args ) {
        double concentration = 0;
        final double maxConcentration = Math.pow( 10, C_MAX );
        while ( concentration <= maxConcentration ) {
            System.out.println( concentration + " -> " + getFontSize( concentration ) );
            concentration += 0.1;
        }
    }
}
