// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import java.text.DecimalFormat;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.selfdrivenparticlemodel.model.ParticleModel;

public class NumberSliderPanel extends HorizontalLayoutPanel {
    private ModelSlider modelSlider;

    public NumberSliderPanel( final IParticleApp particleApp ) {
        this( particleApp, 0, 1000, 10, null );
    }

    public NumberSliderPanel( final IParticleApp particleApp, int min, int max, int increment, int[] ticks ) {
        modelSlider = new ModelSlider( "Number of Particles", "", min, max, particleApp.getParticleModel().numParticles(), new DecimalFormat( "#" ) );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                double value = modelSlider.getValue();
                int val = (int) value;
                particleApp.setNumberParticles( val );
            }
        } );
        add( modelSlider );
        if ( ticks != null ) {
            double[] ticky = new double[ticks.length];
            for ( int i = 0; i < ticky.length; i++ ) {
                ticky[i] = ticks[i];

            }

            modelSlider.setModelTicks( ticky );
        }
        particleApp.getParticleModel().addListener( new ParticleModel.Adapter() {
            public void particleCountChanged() {
                modelSlider.setValue( particleApp.getParticleModel().numParticles() );
            }
        } );
    }

    public void setMaxNumber( int max ) {
        modelSlider.setMaximum( max );
    }

    public ModelSlider getModelSlider() {
        return modelSlider;
    }
}
