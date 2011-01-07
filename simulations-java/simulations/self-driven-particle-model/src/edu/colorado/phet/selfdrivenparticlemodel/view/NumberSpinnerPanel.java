// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class NumberSpinnerPanel extends HorizontalLayoutPanel {
    private JSpinner numberSpinner;

    public NumberSpinnerPanel( final IParticleApp particleApp ) {
        this( particleApp, 0, 1000, 10 );
    }

    public NumberSpinnerPanel( final IParticleApp particleApp, int min, int max, int increment ) {
        add( new JLabel( "Number of Particles" ) );
        numberSpinner = new JSpinner( new SpinnerNumberModel( particleApp.getParticleModel().numParticles(), min, max, increment ) );
        numberSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                Integer value = (Integer)numberSpinner.getValue();
                particleApp.setNumberParticles( value.intValue() );
            }
        } );
        add( numberSpinner );
    }

    public void addChangeListener( ChangeListener changeListener ) {
        numberSpinner.addChangeListener( changeListener );
    }
}
