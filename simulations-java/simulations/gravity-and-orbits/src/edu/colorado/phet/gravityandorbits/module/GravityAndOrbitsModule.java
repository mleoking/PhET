/* Copyright 2010, University of Colorado */

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.piccolophet.PiccoloModule;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.*;
import edu.umd.cs.piccolo.PNode;

/**
 * The GravityAndOrbitsModule has a set of "modes", one mode for each configuration of bodies (eg, Sun + Planet).
 * Each mode has its own model, canvas, clock, etc, which are used in place of this Module's data.
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

    private static final double SUN_RADIUS = 6.955E8;
    private static final double SUN_MASS = 1.989E30;

    private static final double EARTH_RADIUS = 6.371E6;
    public static final double EARTH_MASS = 5.9736E24;
    public static final double EARTH_PERIHELION = 147098290E3;
    private static final double EARTH_ORBITAL_SPEED_AT_PERIHELION = 30300;

    private static final double MOON_MASS = 7.3477E22;
    private static final double MOON_RADIUS = 1737.1E3;
    private static final double MOON_EARTH_SPEED = 1.01E3;
    private static final double MOON_SPEED = MOON_EARTH_SPEED;
    public static final double MOON_PERIGEE = 391370E3;
    private static final double MOON_X = EARTH_PERIHELION;
    private static final double MOON_Y = MOON_PERIGEE;

    //see http://en.wikipedia.org/wiki/International_Space_Station
    private static final double SPACE_STATION_RADIUS = 109;
    public static final double SPACE_STATION_MASS = 369914;
    private static final double SPACE_STATION_SPEED = 7706;
    private static final double SPACE_STATION_PERIGEE = 347000;

    private static final Function1<Double, String> days = new Function1<Double, String>() {
        public String apply( Double time ) {
            int value = (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_DAY );
            String units = ( value == 1 ) ? GAOStrings.EARTH_DAY : GAOStrings.EARTH_DAYS;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };
    private static final Function1<Double, String> minutes = new Function1<Double, String>() {
        public String apply( Double time ) {
            int value = (int) ( time / GravityAndOrbitsDefaults.SECONDS_PER_MINUTE );
            String units = ( value == 1 ) ? GAOStrings.EARTH_MINUTE : GAOStrings.EARTH_MINUTES;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };
    private final int SEC_PER_YEAR = 365 * 24 * 60 * 60;

    public static Function2<Body, Double, BodyRenderer> getImageRenderer( final String image ) {
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.ImageRenderer( body, viewDiameter, image );
            }
        };
    }

    public static Function2<Body, Double, BodyRenderer> getRenderer( final String image,
                                                                     final double targetMass ) {//the mass for which to use the image
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.SwitchableBodyRenderer( body, targetMass, new BodyRenderer.ImageRenderer( body, viewDiameter, image ),
                                                                new BodyRenderer.SphereRenderer( body, viewDiameter ) );
            }
        };
    }

    private final Function2<Body, Double, BodyRenderer> SUN_RENDERER = new Function2<Body, Double, BodyRenderer>() {
        public BodyRenderer apply( Body body, Double viewDiameter ) {
            return new BodyRenderer.SphereRenderer( body, viewDiameter );
//            return new BodyRenderer.ImageRenderer( body, viewDiameter, "sun.png" );
        }
    };
    private final int SEC_PER_MOON_ORBIT = 28 * 24 * 60 * 60;
    private final int SEC_PER_SPACE_STATION_ORBIT = 90 * 60;
    public static final double SUN_MODES_VELOCITY_SCALE = 200 / 4.466E-5;
    private final Line2D.Double initialMeasuringTapeLocationSunModes = new Line2D.Double( 0, -EARTH_PERIHELION / 6, EARTH_PERIHELION, -EARTH_PERIHELION / 6 );
    private final ArrayList<GravityAndOrbitsMode> modes = new ArrayList<GravityAndOrbitsMode>() {{
        Function2<BodyNode, Property<Boolean>, PNode> readoutInEarthMasses = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new EarthMassReadoutNode( bodyNode, visible );
            }
        };
        add( new GravityAndOrbitsMode( "Sun & Planet", VectorNode.FORCE_SCALE * 100 * 1.2, false, GravityAndOrbitsDefaults.DEFAULT_DT, days,
                                       createIconImage( true, true, false, false ), SEC_PER_YEAR, clockPausedProperty, SUN_MODES_VELOCITY_SCALE, readoutInEarthMasses,
                                       initialMeasuringTapeLocationSunModes, 1.25, new ImmutableVector2D( 0, 0 ),
                                       gravityEnabledProperty ) {
            {
                final Body sun = createSun( getMaxPathLength() );
                addBody( sun );
                addBody( createPlanet( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION, getMaxPathLength(), 650 ) );
            }
        } );
        add( new GravityAndOrbitsMode( "Sun, Planet & Moon", VectorNode.FORCE_SCALE * 100 * 1.2, false, GravityAndOrbitsDefaults.DEFAULT_DT, days,
                                       createIconImage( true, true, true, false ), SEC_PER_YEAR, clockPausedProperty, SUN_MODES_VELOCITY_SCALE, readoutInEarthMasses,
                                       initialMeasuringTapeLocationSunModes, 1.25, new ImmutableVector2D( 0, 0 ),
                                       gravityEnabledProperty ) {
            {
                final Body sun = createSun( getMaxPathLength() );
                addBody( sun );
                final Body earth = createPlanet( sun, 0, EARTH_ORBITAL_SPEED_AT_PERIHELION, getMaxPathLength(), 650 );
                addBody( earth );
                final Body moon = createMoon( earth, MOON_SPEED, EARTH_ORBITAL_SPEED_AT_PERIHELION,
                                              false,//no room for the slider
                                              getMaxPathLength(), 17, 1000, 25,
                                              false );//so it doesn't intersect with earth mass readout
                addBody( moon );
            }
        } );
        add( new GravityAndOrbitsMode( "Planet & Moon", VectorNode.FORCE_SCALE * 100 / 2 * 0.9, false, GravityAndOrbitsDefaults.DEFAULT_DT / 3, days,
                                       createIconImage( false, true, true, false ), SEC_PER_MOON_ORBIT, clockPausedProperty, SUN_MODES_VELOCITY_SCALE / 100 * 6, readoutInEarthMasses,
                                       new Line2D.Double( EARTH_PERIHELION, -MOON_PERIGEE / 4, EARTH_PERIHELION + MOON_PERIGEE, -MOON_PERIGEE / 4 ), 400,
                                       new ImmutableVector2D( EARTH_PERIHELION, 0 ),
                                       gravityEnabledProperty ) {
            // Add in some initial -x velocity to offset the earth-moon barycenter drift
            //This value was computed by sampling the total momentum in GravityAndOrbitsModel for this mode
            ImmutableVector2D sampledSystemMomentum = new ImmutableVector2D( 7.421397422188586E25, -1.080211713202125E22 );

            final Body earth;

            {
                ImmutableVector2D velocityOffset = sampledSystemMomentum.getScaledInstance( -1 / ( EARTH_MASS + MOON_MASS ) );
                earth = createPlanet( null, velocityOffset.getX(), velocityOffset.getY(), getMaxPathLength(),
                                      650.0 / 400.0 / 1.25 * 10 );//scale so it is a similar size to other modes

                addBody( earth );
                addBody( createMoon( earth, MOON_SPEED, 0, true, getMaxPathLength(), 1, 1000 / 400 / 1.25 * 10, 1, true ) );
            }
        } );
        Function2<BodyNode, Property<Boolean>, PNode> spaceStationMassReadoutFactory = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new SpaceStationMassReadoutNode( bodyNode, visible );
            }
        };
        add( new GravityAndOrbitsMode( "Planet & Space Station", VectorNode.FORCE_SCALE * 10000 * 1000 * 10000 * 100 * 3, false,
                                       GravityAndOrbitsDefaults.DEFAULT_DT / 10000 * 9, minutes,
                                       createIconImage( false, true, false, true ), SEC_PER_SPACE_STATION_ORBIT, clockPausedProperty,
                                       SUN_MODES_VELOCITY_SCALE / 10000, spaceStationMassReadoutFactory,
                                       new Line2D.Double( EARTH_PERIHELION, -EARTH_RADIUS / 6, EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, -EARTH_RADIUS / 6 ),
                                       400 * 54, new ImmutableVector2D( EARTH_PERIHELION, 0 ),
                                       gravityEnabledProperty ) {
            final Body earth = createPlanet( null, 0, 0, getMaxPathLength(), 650.0 / 400.0 / 1.25 * 10 * 1.5 * 1.5 / 54 );

            {
                addBody( earth );
                addBody( createSpaceStation( earth, getMaxPathLength() ) );
            }
        } );
    }};

    private Image createIconImage( final boolean sun, final boolean earth, final boolean moon, final boolean spaceStation ) {
        return new PNode() {
            {
                int inset = 20;//distance between icons
                addChild( new PhetPPath( new Rectangle2D.Double( 20, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) );
                addIcon( inset, createSun( 0 ).createRenderer( 30 ), sun );
                addIcon( inset, createPlanet( null, 0, 0, 0, 650 ).createRenderer( 25 ), earth );
                addIcon( inset, createMoon( null, 0, 0, true, 0, 17, 1000, 1, true ).createRenderer( 20 ), moon );
                addIcon( inset, createSpaceStation( null, 0 ).createRenderer( 30 ), spaceStation );
            }

            private void addIcon( int inset, PNode sunIcon, boolean sun ) {
                addChild( sunIcon );
                sunIcon.setOffset( getFullBounds().getMaxX() + inset + sunIcon.getFullBounds().getWidth() / 2, 0 );
                sunIcon.setVisible( sun );
            }
        }.toImage();
    }

    private Body createSpaceStation( Body earth, int maxPathLength ) {
        return new Body( earth, GAOStrings.SATELLITE, EARTH_PERIHELION + SPACE_STATION_PERIGEE + EARTH_RADIUS, 0, SPACE_STATION_RADIUS * 2 * 1000, 0,
                         SPACE_STATION_SPEED, SPACE_STATION_MASS, Color.gray, Color.white, 25000 / 80.0 / 54, 1000 * 1.6 / 80 * 2 / 54,
                         getImageRenderer( "space-station.png" ), scaleProperty, -Math.PI / 4, true, maxPathLength, 1, true,
                         SPACE_STATION_MASS, GAOStrings.SPACE_STATION, clockPausedProperty );
    }

    private final Property<GravityAndOrbitsMode> modeProperty = new Property<GravityAndOrbitsMode>( modes.get( 0 ) );

    private Body createMoon( Body earth, double vx, double vy, boolean massSettable, int maxPathLength, final double cartoonOffsetScale,
                             final double cartoonDiameterScaleFactor, double cartoonForceVectorScale, final boolean massReadoutBelow ) {
        return new Body( earth, GAOStrings.MOON, MOON_X, MOON_Y, MOON_RADIUS * 2, vx, vy, MOON_MASS, Color.magenta, Color.white,
                         cartoonDiameterScaleFactor, cartoonOffsetScale,//putting this number too large makes a kink or curly-q in the moon trajectory, which should be avoided
                         getRenderer( "moon.png", MOON_MASS ), scaleProperty, -3 * Math.PI / 4, massSettable, maxPathLength,
                         cartoonForceVectorScale, massReadoutBelow, MOON_MASS, GAOStrings.OUR_MOON, clockPausedProperty );
    }

    private Body createPlanet( Body sun, double vx, double vy, int maxPathLength, final double cartoonDiameterScaleFactor ) {
        return new Body( sun, GAOStrings.PLANET, EARTH_PERIHELION, 0, EARTH_RADIUS * 2, vx, vy, EARTH_MASS, Color.gray, Color.lightGray,
                         cartoonDiameterScaleFactor, 1, getRenderer( "earth_satellite.gif", EARTH_MASS ), scaleProperty, -Math.PI / 4, true,
                         maxPathLength, 1, true, EARTH_MASS, GAOStrings.EARTH, clockPausedProperty );
    }

    private Body createSun( int maxPathLength ) {
        return new Body( null, GAOStrings.SUN, 0, 0, SUN_RADIUS * 2, 0, 0, SUN_MASS, Color.yellow, Color.white, 50, 1,
                         SUN_RENDERER, scaleProperty, -Math.PI / 4, true, maxPathLength, 1, true, SUN_MASS, GAOStrings.OUR_SUN, clockPausedProperty );
    }

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