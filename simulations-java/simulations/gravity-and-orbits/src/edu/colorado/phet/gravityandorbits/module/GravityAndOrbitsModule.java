/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.ImageBody;
import edu.colorado.phet.gravityandorbits.model.SphereBody;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.colorado.phet.gravityandorbits.view.Scale;
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

    private static final double EARTH_RADIUS = 6.371E6;
    public static final double EARTH_MASS = 5.9736E24;
    private static final double EARTH_PERIHELION = 147098290E3;
    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30300;

    private static final double MOON_MASS = 7.3477E22;
    private static final double MOON_RADIUS = 1737.1E3;
    private static final double MOON_EARTH_SPEED = 1.01E3;
    private static final double MOON_SPEED = MOON_EARTH_SPEED;
    private static final double MOON_PERIGEE = 391370E3;
    private static final double MOON_X = EARTH_PERIHELION;
    private static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    private static final double SPACE_STATION_RADIUS = 109;
    private static final double SPACE_STATION_MASS = 369914;
    private static final double SPACE_STATION_SPEED = 7706;
    private static final double SPACE_STATION_PERIGEE = 347000;

    private static final Function1<Double, String> days = new Function1<Double, String>() {
        public String apply( Double time ) {
            return (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_DAY ) + " Earth Days";
        }
    };
    private static final Function1<Double, String> minutes = new Function1<Double, String>() {
        public String apply( Double time ) {
            return (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_MINUTE ) + " Earth Minutes";
        }
    };

    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        Camera camera = new Camera();
        add( new GravityAndOrbitsMode( "Sun & Planet", VectorNode.FORCE_SCALE * 100, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT, days ) {
            {
                final SphereBody sun = createSun();
                addBody( sun );
                addBody( createEarth( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION ) );
            }

            @Override
            public double getZoomScale() {
                return 1.25;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return new ImmutableVector2D( 0, 0 );
            }
        } );
        add( new GravityAndOrbitsMode( "Sun, Planet & Moon", VectorNode.FORCE_SCALE * 100, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT, days ) {
            {
                final SphereBody sun = createSun();
                addBody( sun );
                final SphereBody earth = createEarth( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION );
                addBody( earth );
                final SphereBody moon = createMoon( earth, MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION );
                addBody( moon );
            }

            @Override
            public double getZoomScale() {
                return 1.25;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return new ImmutableVector2D( 0, 0 );
            }
        } );
        add( new GravityAndOrbitsMode( "Planet & Moon", VectorNode.FORCE_SCALE * 100, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT, days ) {
            final SphereBody earth = createEarth( null, 0, 0 );

            {
                addBody( earth );
                addBody( createMoon( earth, MOON_SPEED, 0 ) );
            }

            @Override
            public double getZoomScale() {
                return 5;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return earth.getPosition();
            }
        } );
        add( new GravityAndOrbitsMode( "Planet & Space Station", VectorNode.FORCE_SCALE * 100, false, camera, GravityAndOrbitsDefaults.DEFAULT_DT / 10000, minutes ) {
            final SphereBody earth = createEarth( null, 0, 0 );

            {
                addBody( earth );
                addBody( new ImageBody( earth, "Space Station", EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, 0, SPACE_STATION_RADIUS * 2 * 1000, 0, SPACE_STATION_SPEED, SPACE_STATION_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.REAL_SIZER, 25000, 1000 * 1.6 ) );
            }

            @Override
            public double getZoomScale() {
                return 5;
            }

            @Override
            public ImmutableVector2D getZoomOffset() {
                return earth.getPosition();
            }
        } );
    }};

    private SphereBody createMoon( Body earth, double vx, double vy ) {
        return new SphereBody( earth, "Moon", MOON_X, -MOON_Y, MOON_RADIUS * 2, vx, vy, MOON_MASS, Color.gray, Color.white, GravityAndOrbitsCanvas.REAL_SIZER, 1000, 40 );
    }

    private SphereBody createEarth( Body sun, double vx, double vy ) {
        return new SphereBody( sun, "Earth", EARTH_PERIHELION, 0, EARTH_RADIUS * 2, vx, vy, EARTH_MASS, Color.blue, Color.white, GravityAndOrbitsCanvas.REAL_SIZER, 1000, 1 );
    }

    private SphereBody createSun() {
        return new SphereBody( null, "Sun", 0, 0, SUN_RADIUS * 2, 0, 0, SUN_MASS, Color.yellow, Color.white, GravityAndOrbitsCanvas.REAL_SIZER, 50, 1 );
    }

    private Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );
    private Property<Scale> scaleProperty = new Property<Scale>( Scale.CARTOON );

    public ArrayList<GravityAndOrbitsMode> getModes() {
        return new ArrayList<GravityAndOrbitsMode>( modes );
    }

    public GravityAndOrbitsModule( final PhetFrame phetFrame, String[] commandLineArgs ) {
        super( GravityAndOrbitsStrings.TITLE_EXAMPLE_MODULE
               + ": " + Arrays.asList( commandLineArgs )//For simsharing
                ,
               new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.DEFAULT_DT ) );//TODO: I don't think this clock is used since each mode has its own clock; perhaps this just runs the active tab?

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

    public Property<Scale> getScaleProperty() {
        return scaleProperty;
    }
}