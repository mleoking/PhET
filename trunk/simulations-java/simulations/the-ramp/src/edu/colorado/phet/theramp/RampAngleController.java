// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.theramp;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;

/**
 * User: Sam Reid
 * Date: Oct 7, 2005
 * Time: 9:00:54 PM
 */

public class RampAngleController {
    private RampModule rampModule;
    private ModelSlider modelSlider;

    public RampAngleController( final RampModule rampModule ) {
        this.rampModule = rampModule;
        this.modelSlider = new ModelSlider( TheRampStrings.getString( "property.ramp-angle" ), TheRampStrings.getString( "units.degrees" ), 0, 90, rampModule.getRampAngle() );
        rampModule.getRampPhysicalModel().getRamp().addObserver( new SimpleObserver() {
            public void update() {
                double angle = rampModule.getRampAngle() / 2 / Math.PI * 360;
                modelSlider.setValue( angle );
            }
        } );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double val = modelSlider.getValue();
                rampModule.getRampPhysicalModel().getRamp().setAngle( val * Math.PI * 2 / 360 );
            }
        } );
        modelSlider.setModelTicks( new double[] { 0, 30, 60, 90 } );
    }

    public JComponent getComponent() {
        return modelSlider;
    }
}
