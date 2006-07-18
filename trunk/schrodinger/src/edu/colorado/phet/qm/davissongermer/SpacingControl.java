package edu.colorado.phet.qm.davissongermer;

import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
 * Copyright (c) Jul 18, 2006 by Sam Reid
 */

public class SpacingControl extends ConstrainedSliderControl {
    private DGModel dgModel;

    private static double viewMin = 0.4;
    private static double viewMax = 1.2;
    private static double scaleTx = 10.0 * 1.0 / 45;

    private static int getNumSliderValues() {
        return getNumSliderValues( viewMin, viewMax );
    }

    public double getModelValue() {
        return dgModel.getFractionalSpacing();
    }

    public void setModelValue( double modelValue ) {
        dgModel.setFractionalSpacing( modelValue );
    }

    private static int getNumSliderValues( double viewMin, double viewMax ) {
        double viewIncrement = 0.1;
        return (int)( Math.round( ( viewMax - viewMin ) / viewIncrement ) );
    }

    public SpacingControl( DGModel dgModel ) {
        this.dgModel = dgModel;
        init( "Atom Separation (D)", new DecimalFormat( "0.0" ), new ConstrainedSliderControl.CoordinateFrame( viewMin * scaleTx, viewMax * scaleTx ),
              new ConstrainedSliderControl.CoordinateFrame( viewMin, viewMax ),
              new ConstrainedSliderControl.CoordinateFrame( 0, getNumSliderValues() ) );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
            }
        } );
    }

    private void changeValue() {
        double spacing = determineModelValue();
        dgModel.setFractionalSpacing( spacing );
    }


}
