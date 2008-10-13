package edu.colorado.phet.quantumwaveinterference.davissongermer;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.quantumwaveinterference.QWIResources;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:33:52 AM
 */

public class RadiusModelSlider extends ModelSlider {

    public RadiusModelSlider( final DGModel dgModel, final double scale ) {
        super( QWIResources.getString( "atom.radius" ), QWIResources.getString( "nm" ), 0.05, 0.25, dgModel.getFractionalRadius(), new DecimalFormat( "0.00" ) );
        getSlider().setSnapToTicks( true );
        setModelTicks( new double[]{0.05, ( 0.5 + 0.25 ) / 2.0, 0.25} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( MessageFormat.format( QWIResources.getString( "getvalue.scale.0" ), new Object[]{new Double( getValue() / scale )} ) );

                dgModel.setFractionalRadius( getValue() / scale );
            }
        } );
    }
}
