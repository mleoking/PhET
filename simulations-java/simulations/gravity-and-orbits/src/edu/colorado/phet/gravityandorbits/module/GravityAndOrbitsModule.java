/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private Property<Boolean> showGravityForceProperty = new Property<Boolean>( false );
    private Property<Boolean> showPathProperty = new Property<Boolean>( false );
    private Property<Boolean> showVelocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassProperty = new Property<Boolean>( false );
    private Property<Boolean> toScaleProperty = new Property<Boolean>( false );

    private static final double SUN_RADIUS = 6.955E8;
    public static final double SUN_MASS = 1.989E30;

    public static final double FAKE_SUN_MASS = 2E29;
    public static final double FAKE_SUN_RADIUS = 6.955E8;

    public static final double PLANET_RADIUS = 6.371E6;
    public static final double PLANET_ORBIT_RADIUS = 1.6E11;
    public static final double PLANET_ORBITAL_SPEED = 0.9E4;

    public static final double FAKE_MOON_RADIUS = 1737.1E3;
    public static final double FAKE_MOON_MASS = 1E25;
    public static final double FAKE_MOON_INITIAL_X = 1.4E11;
    public static final double FAKE_MOON_ORBITAL_SPEED = 0.397E4;

    private static final double EARTH_RADIUS = 6.371E6;
    private static final double EARTH_ORBIT_RADIUS = 149668992000.0;
    private static final double EARTH_ORBITAL_SPEED = -29.78E3;

    public static final double G = 6.67428E-11;
    public static final double PLANET_MASS = 1E28;
    public static final double EARTH_MASS = 1E28;

    public static double MOON_MASS = 7.3477E22;
    private static final double MOON_RADIUS = 1737.1E3;
    private final double MOON_ORBITAL_SPEED = EARTH_ORBITAL_SPEED + 1.022E3;
    private final double MOON_INITIAL_X = EARTH_ORBIT_RADIUS + 384399E3;

    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        add( new GravityAndOrbitsMode( "My Sun & Planet" ) {{
            addBody( new Body( "Sun", 0, 0, FAKE_SUN_RADIUS * 2, 0, -0.045E4, FAKE_SUN_MASS, Color.yellow, Color.white, GravityAndOrbitsCanvas.SUN_SIZER, true ) );
            addBody( new Body( "Planet", PLANET_ORBIT_RADIUS, 0, PLANET_RADIUS * 2, 0, PLANET_ORBITAL_SPEED, PLANET_MASS, Color.magenta, Color.white, GravityAndOrbitsCanvas.PLANET_SIZER, true ) );
            addBody( new Body( "Moon", FAKE_MOON_INITIAL_X, 0, FAKE_MOON_RADIUS * 2, 0, FAKE_MOON_ORBITAL_SPEED, FAKE_MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.MOON_SIZER, false ) );
        }} );
        add( new GravityAndOrbitsMode( "Sun, Earth & Moon" ) {{
            addBody( new Body( "Sun", 0, 0, SUN_RADIUS * 2, 0, -0.045E4, SUN_MASS, Color.yellow, Color.white, GravityAndOrbitsCanvas.SUN_SIZER, false ) );
            addBody( new Body( "Earth", EARTH_ORBIT_RADIUS, 0, EARTH_RADIUS * 2, 0, EARTH_ORBITAL_SPEED, EARTH_MASS, Color.blue, Color.white, GravityAndOrbitsCanvas.PLANET_SIZER, false ) );
            addBody( new Body( "Moon", MOON_INITIAL_X, 0, MOON_RADIUS * 2, 0, -MOON_ORBITAL_SPEED, MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.MOON_SIZER, false ) );
        }} );
        add( new GravityAndOrbitsMode( "My Planet & Space Station" ) );
        add( new GravityAndOrbitsMode( "Earth & Space Station" ) );
    }};
    private Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public GravityAndOrbitsModule( final PhetFrame phetFrame, String[] commandLineArgs ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE
               + ": " + Arrays.asList( commandLineArgs )//For simsharing
                ,
               new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ) );

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
                updateClocks();
            }
        }, false );
        updateClocks();

        setClockControlPanel( null );//clock panel appears in the canvas

        // Help
        if ( hasHelp() ) {
            //XXX add help items
        }

        // Set initial state
        reset();
    }

    private GravityAndOrbitsMode getMode() {
        return modeProperty.getValue();
    }

    private void updateClocks() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.setRunning( mode == getMode() );
        }
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void reset() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.reset();
        }
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

    public Property<Boolean> getToScaleProperty() {
        return toScaleProperty;
    }

    public void resetAll() {
        showGravityForceProperty.reset();
        showPathProperty.reset();
        showVelocityProperty.reset();
        showMassProperty.reset();
        toScaleProperty.reset();
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
}