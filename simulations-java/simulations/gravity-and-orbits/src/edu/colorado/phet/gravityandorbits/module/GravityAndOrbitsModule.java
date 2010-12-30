/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * The GravityAndOrbitsModule has a set of "modes", one mode for each configuration of bodies (eg, Sun + Planet).
 * Each mode has its own model, canvas, clock, etc, which are used in place of this Module's data.
 * The module contains information that is shared across all modes, such as whether certain features are shown (such as
 * showing the gravitational force).
 *
 * @author Sam Reid
 * @author Jon Olson
 * @author Chris Malley
 * @author John Blanco
 * @see edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    public static final double G = 6.67428E-11;

    /*
     * Properties that are common to all "modes" should live here.
     */
    private final Property<Boolean> showGravityForceProperty = new Property<Boolean>( false );
    private final Property<Boolean> showPathProperty = new Property<Boolean>( false );
    private final Property<Boolean> showVelocityProperty = new Property<Boolean>( false );
    private final Property<Boolean> showMassProperty = new Property<Boolean>( false );
    private final Property<Boolean> clockPausedProperty = new Property<Boolean>( true );
    private final Property<Boolean> measuringTapeVisibleProperty = new Property<Boolean>( false );
    private final Property<Scale> scaleProperty = new Property<Scale>( Scale.CARTOON );
    private final Property<Boolean> gravityEnabledProperty = new Property<Boolean>( true );

    private final ArrayList<GravityAndOrbitsMode> modes = new GravityAndOrbitsModeList( clockPausedProperty, gravityEnabledProperty, scaleProperty );
    private final Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    public GravityAndOrbitsModule( final PhetFrame phetFrame, String[] commandLineArgs ) {
        super( "Gravity and Orbits" + ": " + Arrays.asList( commandLineArgs ),//For simsharing
               new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.DEFAULT_DT ) );//TODO: I don't think this clock is used since each mode has its own clock; perhaps this just runs the active tab?
        getModulePanel().setLogoPanel( null );
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.init( this );
        }

        setSimulationPanel( getMode().getCanvas() );

        // Switch the entire canvas on mode switches
        modeProperty.addObserver( new SimpleObserver() {
            public void update() {
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        setSimulationPanel( getMode().getCanvas() );
                        phetFrame.invalidate();
                        phetFrame.validate();
                        phetFrame.doLayout();
                    }
                } );
                updateActiveModule();
            }
        } );
        setClockControlPanel( null );//clock panel appears in the canvas

        reset();
    }

    private GravityAndOrbitsMode getMode() {
        return modeProperty.getValue();
    }

    private void updateActiveModule() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.setActive( mode == getMode() );
        }
    }

    /**
     * Resets the module.
     */
    public void reset() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.reset();
        }
        showGravityForceProperty.reset();
        showPathProperty.reset();
        showVelocityProperty.reset();
        showMassProperty.reset();
        scaleProperty.reset();
        modeProperty.reset();
        measuringTapeVisibleProperty.reset();
        clockPausedProperty.reset();
        gravityEnabledProperty.reset();
    }

    public Property<Boolean> getShowGravityForceProperty() {
        return showGravityForceProperty;
    }

    public Property<Boolean> getShowPathProperty() {
        return showPathProperty;
    }

    public Property<Boolean> getShowVelocityProperty() {
        return showVelocityProperty;
    }

    public Property<Boolean> getShowMassProperty() {
        return showMassProperty;
    }

    public Property<GravityAndOrbitsMode> getModeProperty() {
        return modeProperty;
    }

    public void setTeacherMode( boolean b ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().teacherMode = b;
        }
    }

    public void addModelSteppedListener( SimpleObserver simpleObserver ) {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.getModel().addModelSteppedListener( simpleObserver );
        }
    }

    public Property<Scale> getScaleProperty() {
        return scaleProperty;
    }

    public Property<Boolean> getClockPausedProperty() {
        return clockPausedProperty;
    }

    public Property<Boolean> getMeasuringTapeVisibleProperty() {
        return measuringTapeVisibleProperty;
    }

    public boolean isGravityEnabled() {
        return gravityEnabledProperty.getValue();
    }

    public Property<Boolean> getGravityEnabledProperty() {
        return gravityEnabledProperty;
    }
}