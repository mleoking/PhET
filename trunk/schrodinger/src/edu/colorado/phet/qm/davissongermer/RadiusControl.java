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

//    double[] modelValues = new double[]{
//            0.011111111111111112,
//            0.02592592592592592,
//            0.03580246913580246,
//            0.0499,
//            0.05555555555555555};
//
//    protected double determineModelValue() {
//        int sliderValue = getSlider().getValue();
//        return modelValues[sliderValue];
//    }

    public void setModelValue( double modelValue ) {
        dgModel.setFractionalRadius( modelValue );
    }

//    private boolean closeEnough( double a, double b ) {
//        double epsilon = 0.001;
//        return Math.abs( a - b ) <= epsilon;
//    }

    private static int getNumSliderValues( CoordinateFrame viewFrame ) {
        return 5;
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
