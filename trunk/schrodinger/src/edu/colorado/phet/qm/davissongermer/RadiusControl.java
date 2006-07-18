package edu.colorado.phet.qm.davissongermer;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
 * Copyright (c) Jul 18, 2006 by Sam Reid
 */

public class RadiusControl extends ConstrainedSliderControl {
    private DGModel dgModel;

    private static double viewMin = 0.05;
    private static double viewMax = 0.25;
    private static double scaleTx = 1 / 4.5;

    private static int getNumSliderValues() {
        return RadiusControl.getNumSliderValues( RadiusControl.viewMin, RadiusControl.viewMax );
    }

    public double getModelValue() {
        return dgModel.getFractionalRadius();
    }

    public void setModelValue( double modelValue ) {
        dgModel.setFractionalRadius( modelValue );
    }

    private static int getNumSliderValues( double viewMin, double viewMax ) {
        return 4;
//        double viewIncrement = 0.1;
//        return (int)( Math.round( ( viewMax - viewMin ) / viewIncrement ) + 1 );
    }

    public RadiusControl( DGModel dgModel ) {
        this.dgModel = dgModel;
        init( "Atom Radius", new DecimalFormat( "0.00" ),
              new CoordinateFrame( RadiusControl.viewMin * RadiusControl.scaleTx, RadiusControl.viewMax * RadiusControl.scaleTx ),
              new CoordinateFrame( RadiusControl.viewMin, RadiusControl.viewMax ),
              new CoordinateFrame( 0, RadiusControl.getNumSliderValues() ) );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
            }
        } );
        update();
    }


}
