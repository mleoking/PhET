/*  */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Mar 26, 2006
 * Time: 5:18:38 PM
 */

public class WaveRotateControl extends HorizontalLayoutPanel {
    public WaveRotateControl( final RotationWaveGraphic rotationWaveGraphic ) {
        final ModelSlider rotate = new ModelSlider( WIStrings.getString( "controls.rotate-view" ), WIStrings.getString( "units.radians" ), 0, Math.PI / 2, rotationWaveGraphic.getRotation() );
        rotate.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                rotationWaveGraphic.setViewAngle( rotate.getValue() );
            }
        } );
        rotate.setPaintLabels( true );
        Hashtable modelLabels = new Hashtable();
        modelLabels.put( new Double( rotate.getMaximumModelValue() ), new JLabel( WIStrings.getString( "controls.side" ) ) );
        modelLabels.put( new Double( rotate.getMinimumModelValue() ), new JLabel( WIStrings.getString( "controls.top" ) ) );
        rotate.setPaintTicks( false );
        rotate.setTextFieldVisible( false );
        rotate.setModelLabels( modelLabels );

        rotate.getSlider().addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if( !rotate.getSlider().getValueIsAdjusting() ) {
                    SwingUtilities.invokeLater( new Runnable() {
                        public void run() {
                            double halfway = ( rotate.getMaximumModelValue() + rotate.getMinimumModelValue() ) / 2;
                            if( rotate.getValue() < halfway ) {
                                rotate.setValue( rotate.getMinimumModelValue() );
                            }
                            else {
                                rotate.setValue( rotate.getMaximumModelValue() );
                            }
                        }
                    } );
                }
            }
        } );
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                rotate.setValue( rotationWaveGraphic.getRotation() );
            }
        } );
        add( rotate );
    }
}
