package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.text.DecimalFormat;

import edu.colorado.phet.quantumwaveinterference.QWIResources;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
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
        init( QWIResources.getString( "atom.radius" ), new DecimalFormat( "0.00" ),
              dgModel.getRadiusModelFrame(),
              dgModel.getRadiusViewFrame(),
              new CoordinateFrame( 0, getNumSliderValues( dgModel.getRadiusViewFrame() ) - 1 ) );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
            }
        } );
        update();
        CoordinateFrame sliderCoordinateFrame = getSliderFrame();
        System.out.println( "Converting slider values:" );
        for( double i = sliderCoordinateFrame.getMin(); i <= sliderCoordinateFrame.getMax(); i++ ) {
            double v = transform( i, sliderCoordinateFrame, getViewFrame() );
            double m = transform( i, sliderCoordinateFrame, getModelFrame() );
            System.out.println( "slider value = " + i + ", view=" + v + ", fraction of lattice=" + m + ", cells on lattice=" + modelFractionToNumCells( m ) + " chopped=" + (int)modelFractionToNumCells( m ) );
        }

        System.out.println( "Converting view values:" );
        for( double i = getViewFrame().getMin(); i <= getViewFrame().getMax(); i += 0.05 ) {
            double m = transform( i, getViewFrame(), getModelFrame() );
            double s = transform( i, getViewFrame(), getSliderFrame() );
            System.out.println( "view value = " + i + ", slider value=" + s + ", fraction of lattice=" + m + ", cells on lattice=" + modelFractionToNumCells( m ) + ", chopped=" + (int)modelFractionToNumCells( m ) );
        }
    }

    public static double modelFractionToNumCells( double modelFraction ) {
//        return (int)( modelFraction * 45 );
        return ( modelFraction * 45.0 );
    }

}
