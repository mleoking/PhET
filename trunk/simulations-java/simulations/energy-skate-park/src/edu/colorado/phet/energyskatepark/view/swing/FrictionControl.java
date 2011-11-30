// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;

/**
 * User: Sam Reid
 * Date: May 26, 2006
 * Time: 7:55:58 AM
 */

public class FrictionControl extends VerticalLayoutPanel {
    private final ModelSlider modelSlider;

    public FrictionControl( final AbstractEnergySkateParkModule module ) {
        modelSlider = new ModelSlider( EnergySkateParkResources.getString( "controls.friction" ), "", 0, 0.01, 0.0, new DecimalFormat( "0.000" ), new DecimalFormat( "0.000" ) );
        modelSlider.setModelTicks( new double[] { 0, 0.005, 0.01 } );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                module.setCoefficientOfFriction( modelSlider.getValue() );
            }
        } );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                if ( module.getEnergySkateParkModel().getNumBodies() > 0 ) {
                    modelSlider.setValue( module.getEnergySkateParkModel().getBody( 0 ).getFrictionCoefficient() );
                }
            }
        } );

        modelSlider.setTextFieldVisible( false );
        setIgnoreKeyEvents( true );
        modelSlider.setExtremumLabels( new JLabel( EnergySkateParkResources.getString( "controls.gravity.none" ) ), new JLabel( EnergySkateParkResources.getString( "controls.gravity.lots" ) ) );
        addFullWidth( modelSlider );
    }

    private void setIgnoreKeyEvents( boolean ignoreKeyEvents ) {
        setFocusable( !ignoreKeyEvents );
    }

    public ModelSlider getModelSlider() {
        return modelSlider;
    }
}
