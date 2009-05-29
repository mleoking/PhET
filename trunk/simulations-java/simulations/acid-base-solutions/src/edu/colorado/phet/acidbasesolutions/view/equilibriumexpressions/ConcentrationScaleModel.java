package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;


public class ConcentrationScaleModel {

    private static final double C0 = -16;
    private static final double C1 = 4;
    private static final double T0 = 1;
    private static final double T1 = 72; 
    private static final double dC = C1 - C0;
    private static final double dT = T1 - T0;
    private static final double m0 = 2; //XXX vestigial?
    private static final double m1 = 4; //XXX vestigial?
    
    private ConcentrationScaleModel() {}
    
    public static double getFontSize( double concentration ) {
        final double C = Math.log10( concentration );
        final double D = (dT - dC) / ( ( C1 * C1 ) - ( C0 * C0 ) - ( 2 * dC * C0) );
        final double B = 1 - ( 2 * D * C0 );
        final double A = T0 - ( B * C0 ) - ( D * C0 * C0 ); 
        final double T = A +  ( B * C ) + ( D * C * C );
        return T; //XXX units?
    }
}
