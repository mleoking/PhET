// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.control;

import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.nodes.Piccolo3DCanvas;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.common.piccolophet.nodes.slider.HSliderNode;
import edu.colorado.phet.lwjglphet.utils.GLSwingForwardingClock;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.colorado.phet.platetectonics.PlateTectonicsResources.Strings;
import edu.colorado.phet.platetectonics.PlateTectonicsSimSharing.UserComponents;
import edu.colorado.phet.platetectonics.model.TectonicsClock;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * Time control for the motion tab. When in manual mode, only displays a time readout. When in auto mode, displays the play/pause button,
 * step button, and a slider to control speed.
 */
public class TectonicsTimeControl extends PiccoloClockControlPanel {

    // this property is handled in the Swing EDT
    private Property<Double> speedProperty = new Property<Double>( 1.0 );

    public TectonicsTimeControl( final TectonicsClock clock, final Property<Boolean> isAutoMode ) {
        super( new GLSwingForwardingClock( clock ) );

        setRewindButtonVisible( false );
        setTimeDisplayVisible( true );
        setUnits( Strings.MILLION_YEARS );
        setTimeFormat( new DecimalFormat( "0" ) );
        setTimeColumns( 4 );

        double min = 0.1;
        double max = 10;

        Piccolo3DCanvas timeSliderCanvas = new Piccolo3DCanvas( new HSliderNode( UserComponents.timeSpeedSlider,
                                                                                 min, max, 5, 100,
                                                                                 speedProperty, new Property<Boolean>( true ) ) {{
            addLabel( min, new PText( Strings.TIME_SLOW ) );
            addLabel( max, new PText( Strings.TIME_FAST ) );

            speedProperty.addObserver( new SimpleObserver() {
                public void update() {
                    // our clock is running in the LWJGL thread, so we need to wrap it
                    LWJGLUtils.invoke( new Runnable() {
                        public void run() {
                            clock.setTimeMultiplier( speedProperty.get() );
                        }
                    } );
                }
            } );

            isAutoMode.addObserver( new SimpleObserver() {
                public void update() {
                    setVisible( isAutoMode.get() );
                }
            } );
        }} );

        addBetweenTimeDisplayAndButtons( timeSliderCanvas );

        isAutoMode.addObserver( new SimpleObserver() {
            public void update() {
                final boolean isAuto = isAutoMode.get();
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        getPlayPauseButton().setTransparency( isAuto ? 1 : 0 );
                        getPlayPauseButton().setPickable( isAuto );
                        getStepButton().setTransparency( isAuto ? 1 : 0 );
                        getStepButton().setPickable( isAuto );
                        repaint();
                    }
                } );
            }
        } );
    }

    public void resetAll() {
        // speed property is accessed in the Swing EDT, and our resetAll() runs in the LWJGL thread
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                speedProperty.reset();
            }
        } );
    }
}
