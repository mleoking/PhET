/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Aug 4, 2005
 * Time: 6:38:01 PM
 * Copyright (c) Aug 4, 2005 by Sam Reid
 */

public class AppliedForceControl extends PNode {
    private RampModule module;
    private RampPanel rampPanel;

    public AppliedForceControl( final RampModule module, RampPanel rampPanel ) {
        this.module = module;
        this.rampPanel = rampPanel;
        double maxValue = 3000;
        final ModelSlider modelSlider = new ModelSlider( "Applied Force", "Newtons", -maxValue, maxValue, 0, new DecimalFormat( "0.00" ) );
        modelSlider.setModelTicks( new double[]{-maxValue, 0, maxValue} );
        PSwing pSwing = new PSwing( rampPanel, modelSlider );
        addChild( pSwing );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setAppliedForce( modelSlider.getValue() );
            }
        } );
        module.getRampPhysicalModel().addListener( new RampPhysicalModel.Listener() {
            public void appliedForceChanged() {
                modelSlider.setValue( module.getRampPhysicalModel().getAppliedForceScalar());
            }

            public void zeroPointChanged() {
            }

            public void stepFinished() {
            }
        } );
    }
}
