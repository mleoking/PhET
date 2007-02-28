package edu.colorado.phet.qm.davissongermer;

import edu.colorado.phet.common.view.ModelSlider;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;

/**
 * User: Sam Reid
 * Date: Jul 18, 2006
 * Time: 12:33:52 AM
 * Copyright (c) Jul 18, 2006 by Sam Reid
 */

public class RadiusModelSlider extends ModelSlider {

    public RadiusModelSlider( final DGModel dgModel, final double scale ) {
        super( QWIStrings.getString( "atom.radius" ), QWIStrings.getString( "nm" ), 0.05, 0.25, dgModel.getFractionalRadius(), new DecimalFormat( "0.00" ) );
        getSlider().setSnapToTicks( true );
        setModelTicks( new double[]{0.05, ( 0.5 + 0.25 ) / 2.0, 0.25} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                System.out.println( MessageFormat.format( QWIStrings.getResourceBundle().getString( "getvalue.scale.0" ), new Object[]{new Double( getValue() / scale )} ) );

                dgModel.setFractionalRadius( getValue() / scale );
            }
        } );
    }
}
