package edu.colorado.phet.quantumwaveinterference.davissongermer;

import java.text.DecimalFormat;

import edu.colorado.phet.quantumwaveinterference.QWIResources;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:35:09 AM
 */

public class SpacingControl extends ConstrainedSliderControl {
    private DGModel dgModel;

    private static int getNumSliderValues( CoordinateFrame spacingViewFrame ) {
        return getNumSliderValues( spacingViewFrame.getMin(), spacingViewFrame.getMax() );
    }

    public double getModelValue() {
        return dgModel.getFractionalSpacing();
    }

    public void setModelValue( double modelValue ) {
        dgModel.setFractionalSpacing( modelValue );
    }

    private static int getNumSliderValues( double viewMin, double viewMax ) {
        double viewIncrement = 0.1;
        return (int)( Math.round( ( viewMax - viewMin ) / viewIncrement ) + 1 );
    }

    public SpacingControl( DGModel dgModel ) {
        this.dgModel = dgModel;
        init( QWIResources.getString( "atom.separation.d" ), new DecimalFormat( "0.0" ),
              dgModel.getSpacingModelFrame(),
              dgModel.getSpacingViewFrame(),
              new CoordinateFrame( 0, getNumSliderValues( dgModel.getSpacingViewFrame() ) - 1 ) );
        dgModel.addListener( new DGModel.Listener() {
            public void potentialChanged() {
                update();
            }
        } );
    }

}
