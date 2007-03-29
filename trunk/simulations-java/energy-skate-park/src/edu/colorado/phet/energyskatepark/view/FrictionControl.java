/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 7:55:58 AM
 * Copyright (c) May 26, 2006 by Sam Reid
 */

public class FrictionControl extends VerticalLayoutPanel {
    private ModelSlider modelSlider;

    public FrictionControl( final EnergySkateParkModule module ) {

        modelSlider = new ModelSlider( EnergySkateParkStrings.getString( "coefficient.of.friction" ), "", 0, 0.01, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
//        final ModelSlider modelSlider = new ModelSlider( "Coefficient of Friction", "", 0, 1.0, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
        modelSlider.setModelTicks( new double[]{0, 0.005, 0.01} );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setCoefficientOfFriction( modelSlider.getValue() );
            }
        } );
        module.getClock().addClockListener( new ClockAdapter() {
            public void clockTicked( ClockEvent event ) {
                if( module.getEnergySkateParkModel().numBodies() > 0 ) {
                    modelSlider.setValue( module.getEnergySkateParkModel().bodyAt( 0 ).getFrictionCoefficient() );
                }
            }
        } );
        modelSlider.setTextFieldVisible( false );
        setIgnoreKeyEvents( true );
        modelSlider.setExtremumLabels( new JLabel( EnergySkateParkStrings.getString( "none" ) ), new JLabel( EnergySkateParkStrings.getString( "lots" ) ) );
        addFullWidth( modelSlider );

    }

    private void setIgnoreKeyEvents( boolean ignoreKeyEvents ) {
        setFocusable( !ignoreKeyEvents );
    }

    public ModelSlider getModelSlider() {
        return modelSlider;
    }
}
