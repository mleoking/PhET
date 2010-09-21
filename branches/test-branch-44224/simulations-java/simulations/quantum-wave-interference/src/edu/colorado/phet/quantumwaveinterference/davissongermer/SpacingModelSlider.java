package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:32:20 AM
 */

public class SpacingModelSlider extends ModelSlider {
    public SpacingModelSlider( final DGModel dgModel, final double scale ) {
        super( QWIResources.getString( "atom.separation.d" ), QWIResources.getString( "nm" ), 0.4, 1.2, dgModel.getFractionalSpacing() * scale, new DecimalFormat( "0.0" ) );
        getSlider().setSnapToTicks( true );
        setModelTicks( new double[]{0.4, ( 1.2 + 0.4 ) / 2, 1.2} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double spacing = getValue() / scale;
                System.out.println( "spacing = " + spacing );
                dgModel.setFractionalSpacing( spacing );
            }
        } );
    }
}
