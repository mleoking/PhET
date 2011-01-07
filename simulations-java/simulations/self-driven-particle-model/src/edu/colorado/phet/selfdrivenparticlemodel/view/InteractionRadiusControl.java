// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

public class InteractionRadiusControl extends ModelSlider {
    public InteractionRadiusControl( final ParticleModel model ) {
        super( "Interaction radius", "", 0, 200, model.getRadius(), new DecimalFormat( "0.00" ) );
        setModelTicks( new double[]{0, 100, 200} );
        addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                model.setRadius( getValue() );
            }
        } );
    }
}
