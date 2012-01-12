// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.waveinterference.util.WIStrings;
import edu.colorado.phet.waveinterference.view.PressureWaveGraphic;

/**
 * User: Sam Reid
 * Date: Aug 23, 2006
 * Time: 10:57:35 PM
 */

public class ShowMarkersControl extends JPanel {
    private JCheckBox jCheckBox;
    private SoundWaveGraphic soundWaveGraphic;

    public ShowMarkersControl( final SoundWaveGraphic soundWaveGraphic, final PressureWaveGraphic pressureWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        jCheckBox = new JCheckBox( WIStrings.getString( "controls.show-markers" ), pressureWaveGraphic.getMarkersVisible() );
        jCheckBox.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                pressureWaveGraphic.setMarkersVisible( jCheckBox.isSelected() );
            }
        } );
        add( jCheckBox );
        pressureWaveGraphic.addListener( new PressureWaveGraphic.Listener() {
            public void markerVisibilityChanged() {
                jCheckBox.setSelected( pressureWaveGraphic.getMarkersVisible() );
                updateEnabled();
            }
        } );
        soundWaveGraphic.addListener( new SoundWaveGraphic.Listener() {
            public void viewChanged() {
            }

            public void viewTypeChanged() {
                updateEnabled();
            }

        } );
        updateEnabled();
    }

    private void updateEnabled() {
        jCheckBox.setEnabled( soundWaveGraphic.isParticleVisible() );
    }
}
