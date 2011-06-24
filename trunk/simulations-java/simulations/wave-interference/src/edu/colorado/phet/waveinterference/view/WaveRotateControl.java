// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.waveinterference.util.WIStrings;

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

        rotate.getSlider().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
//                super.mouseReleased(mouseEvent);    //To change body of overridden methods use File | Settings | File Templates.
                 System.out.println("MR: rotate.getSlider().getValueIsAdjusting() = " + rotate.getSlider().getValueIsAdjusting());
                if (!rotate.getSlider().getValueIsAdjusting()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            double halfway = (rotate.getMaximumModelValue() + rotate.getMinimumModelValue()) / 2;
                            if (rotate.getValue() < halfway) {
                                rotate.setValue(rotate.getMinimumModelValue()+0.1);
                                rotate.setValue(rotate.getMinimumModelValue());
                            } else {
                                rotate.setValue(rotate.getMaximumModelValue()-0.1);
                                rotate.setValue(rotate.getMaximumModelValue());
                            }
                        }
                    });
                }
            }
        });
        rotate.getSlider().addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                System.out.println("rotate.getSlider().getValueIsAdjusting() = " + rotate.getSlider().getValueIsAdjusting());
                if (!rotate.getSlider().getValueIsAdjusting()) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            double halfway = (rotate.getMaximumModelValue() + rotate.getMinimumModelValue()) / 2;
                            if (rotate.getValue() < halfway) {
                                rotate.setValue(rotate.getMinimumModelValue());
                            } else {
                                rotate.setValue(rotate.getMaximumModelValue());
                            }
                        }
                    });
                }
            }
        });
        rotationWaveGraphic.addListener( new RotationWaveGraphic.Listener() {
            public void rotationChanged() {
                rotate.setValue( rotationWaveGraphic.getRotation() );
            }
        } );
        add( rotate );
    }
}
