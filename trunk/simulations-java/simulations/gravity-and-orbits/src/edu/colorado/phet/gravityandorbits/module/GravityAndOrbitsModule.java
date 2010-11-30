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
import edu.colorado.phet.gravityandorbits.controlpanel.GOCheckBox;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.ImageBody;
import edu.colorado.phet.gravityandorbits.model.SphereBody;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.colorado.phet.gravityandorbits.view.VectorNode;

/**
 * Module template.
 */
public class GravityAndOrbitsModule extends PiccoloModule {

    public static final double G = 6.67428E-11;

    private Property<Boolean> showGravityForceProperty = new Property<Boolean>( false );
    private Property<Boolean> showPathProperty = new Property<Boolean>( false );
    private Property<Boolean> showVelocityProperty = new Property<Boolean>( false );
    private Property<Boolean> showMassProperty = new Property<Boolean>( false );

    private static final double SUN_RADIUS = 6.955E8;
    private static final double SUN_MASS = 1.989E30;

    private static final double FAKE_SUN_MASS = 2E29;
    private static final double FAKE_SUN_RADIUS = 6.955E8;

    public static final double PLANET_MASS = 1E28;
    private static final double PLANET_RADIUS = 6.371E6;
    private static final double PLANET_ORBIT_RADIUS = 1.6E11;
    private static final double PLANET_ORBITAL_SPEED = 0.9E4;

    private static final double FAKE_MOON_RADIUS = 1737.1E3;
    private static final double FAKE_MOON_MASS = 1E25;
    private static final double FAKE_MOON_INITIAL_X = 1.4E11;
    private static final double FAKE_MOON_ORBITAL_SPEED = 0.397E4;

    private static final double EARTH_RADIUS = 6.371E6;
    private static final double EARTH_MASS = 5.9736E24;
    private static final double EARTH_PERIHELION = 147098290E3;
    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30300;

    private static final double MOON_MASS = 7.3477E22;
    private static final double MOON_RADIUS = 1737.1E3;
    private static final double MOON_ORBITAL_SPEED = EARTH_ORBITAL_SPEED_AT_PERIHELION + 1.022E3 * 1.04;
    private static final double MOON_PERIGEE = 364397E3;
    private static final double MOON_INITIAL_X = EARTH_PERIHELION + MOON_PERIGEE;

    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        add( new GravityAndOrbitsMode( "My Sun & Planet", VectorNode.FORCE_SCALE, true ) {
            private GravityAndOrbitsMode mode;

            {
                addBody( new SphereBody( "Sun", 0, 0, FAKE_SUN_RADIUS * 2, 0, -0.045E4, FAKE_SUN_MASS, Color.yellow, Color.white, GravityAndOrbitsCanvas.SUN_SIZER, true ) );
                addBody( new SphereBody( "Planet", PLANET_ORBIT_RADIUS, 0, PLANET_RADIUS * 2, 0, PLANET_ORBITAL_SPEED, PLANET_MASS, Color.magenta, Color.white, GravityAndOrbitsCanvas.PLANET_SIZER, true ) );
                addBody( new SphereBody( "Moon", FAKE_MOON_INITIAL_X, 0, FAKE_MOON_RADIUS * 2, 0, FAKE_MOON_ORBITAL_SPEED, FAKE_MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.MOON_SIZER, false ) );
                mode = this;
            }

            @Override
            public JComponent newComponent( final Property<GravityAndOrbitsMode> modeProperty ) {
                return new JPanel() {{
                    setLayout( new BorderLayout() );
                    setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
                    add( createRadioButton( modeProperty ), BorderLayout.WEST );
                    final GOCheckBox moonCheckBox = new GOCheckBox( "Moon", getMoonProperty() );
                    add( moonCheckBox, BorderLayout.EAST );

                    // moon checkbox only visible when this mode is active
                    mode.addModeActiveListener( new SimpleObserver() {
                        public void update() {
                            moonCheckBox.setVisible( mode.isActive() );
                        }
                    } );
                }};
            }
        } );
        add( new GravityAndOrbitsMode( "Sun, Earth & Moon", VectorNode.FORCE_SCALE * 100, false ) {{
            addBody( new SphereBody( "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, SUN_MASS, Color.yellow, Color.white, GravityAndOrbitsCanvas.SUN_SIZER, false ) );
            addBody( new SphereBody( "Earth", EARTH_PERIHELION, 0, EARTH_RADIUS * 2, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION, EARTH_MASS, Color.blue, Color.white, GravityAndOrbitsCanvas.PLANET_SIZER, false ) );
            addBody( new SphereBody( "Moon", MOON_INITIAL_X, 0, MOON_RADIUS * 2, 0, MOON_ORBITAL_SPEED, MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.MOON_SIZER, false ) );
        }} );
        add( new GravityAndOrbitsMode( "My Planet & Space Station", VectorNode.FORCE_SCALE, false ) {{
            addBody( new SphereBody( "Planet", PLANET_ORBIT_RADIUS, 0, PLANET_RADIUS * 2, 0, PLANET_ORBITAL_SPEED, PLANET_MASS, Color.magenta, Color.white, GravityAndOrbitsCanvas.PLANET_SIZER, true ) );
            addBody( new ImageBody( "Moon", MOON_INITIAL_X, 0, MOON_RADIUS * 2, 0, MOON_ORBITAL_SPEED, MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.MOON_SIZER, false ) );
        }} );
        add( new GravityAndOrbitsMode( "Earth & Space Station", VectorNode.FORCE_SCALE * 100, false ) );
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
                updateActiveModule();
                modeProperty.getValue().startZoom();
            }
        }, false );
        updateActiveModule();

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

    private void updateActiveModule() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.setActive( mode == getMode() );
        }
    }

    //----------------------------------------------------------------------------
    // Module overrides
    //----------------------------------------------------------------------------

    /**
     * Resets the module.
     */
    public void resetAll() {
        for ( GravityAndOrbitsMode mode : modes ) {
            mode.reset();
        }
        showGravityForceProperty.reset();
        showPathProperty.reset();
        showVelocityProperty.reset();
        showMassProperty.reset();
        modeProperty.reset();
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
}