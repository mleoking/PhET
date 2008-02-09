/* Copyright 2004, Sam Reid */
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 25, 2005
 * Time: 12:39:43 AM
 * Copyright (c) Aug 25, 2005 by Sam Reid
 */

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
