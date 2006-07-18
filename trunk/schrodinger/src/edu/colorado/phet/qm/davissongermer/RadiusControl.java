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

    public double getModelValue() {
        return dgModel.getFractionalRadius();
    }

    public void setModelValue( double modelValue ) {
        dgModel.setFractionalRadius( modelValue );
    }

    private static int getNumSliderValues( CoordinateFrame viewFrame ) {
        double viewIncrement = 0.05;
        return (int)( Math.round( ( viewFrame.getRange() ) / viewIncrement ) + 1 );
    }

    public RadiusControl( DGModel dgModel ) {
        this.dgModel = dgModel;
        init( "Atom Radius", new DecimalFormat( "0.00" ),
              dgModel.getRadiusModelFrame(),
              dgModel.getRadiusViewFrame(),
              new CoordinateFrame( 0, getNumSliderValues( dgModel.getRadiusViewFrame() ) - 1 ) );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
            }
        } );
        update();

    }


}
