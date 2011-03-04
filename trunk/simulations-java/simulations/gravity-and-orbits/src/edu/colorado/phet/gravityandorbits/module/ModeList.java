// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.gravityandorbits.GAOStrings;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.view.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

import static edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils.multiScaleToWidth;
import static edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication.RESOURCES;

/**
 * GravityAndOrbitsModeList enumerates and declares the possible modes in the GravityAndOrbitsModule, such as "Sun & Earth" mode.
 *
 * @author Sam Reid
 */
public class ModeList extends ArrayList<GravityAndOrbitsMode> {

    private ModeListParameter p;

    public ModeList( final ModeListParameter p, final BodyPrototype sun, final BodyPrototype earth, final BodyPrototype moon, final BodyPrototype spaceStation, final double sunModesZoom ) {
        this.p = p;
        Function2<BodyNode, Property<Boolean>, PNode> readoutInEarthMasses = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new EarthMassReadoutNode( bodyNode, visible );
            }
        };

        //Create the modes.
        Line2D.Double initialMeasuringTapeLocationSunModes = new Line2D.Double( 0, -earth.x / 6, earth.x, -earth.x / 6 );
        int SEC_PER_YEAR = 365 * 24 * 60 * 60;
        final double SUN_MODES_VELOCITY_SCALE = 4.48E6;
        add( new GravityAndOrbitsMode( GAOStrings.SUN_AND_PLANET,
                                       VectorNode.FORCE_SCALE * 120,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT,
                                       days,
                                       createIconImage( true, true, false, false, sun, earth, moon, spaceStation ),
                                       SEC_PER_YEAR,
                                       SUN_MODES_VELOCITY_SCALE,
                                       readoutInEarthMasses,
                                       initialMeasuringTapeLocationSunModes,
                                       sunModesZoom,
                                       new ImmutableVector2D( 0, 0 ),
                                       earth.x / 2,
                                       new Point2D.Double( 0, 0 ),
                                       p ) {{
            addBody( createSun( getMaxPathLength(), sun ) );
            addBody( createPlanet( 0, earth.vy, getMaxPathLength(), earth ) );
        }} );
        add( new GravityAndOrbitsMode( GAOStrings.SUN_PLANET_AND_MOON,
                                       VectorNode.FORCE_SCALE * 120,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT,
                                       days,
                                       createIconImage( true, true, true, false, sun, earth, moon, spaceStation ),
                                       SEC_PER_YEAR,
                                       SUN_MODES_VELOCITY_SCALE,
                                       readoutInEarthMasses,
                                       initialMeasuringTapeLocationSunModes,
                                       sunModesZoom,
                                       new ImmutableVector2D( 0, 0 ),
                                       earth.x / 2,
                                       new Point2D.Double( 0, 0 ),
                                       p ) {{
            addBody( createSun( getMaxPathLength(), sun ) );
            addBody( createPlanet( 0, earth.vy, getMaxPathLength(), earth ) );
            addBody( createMoon( moon.vx, earth.vy,
                                 false,//no room for the slider
                                 getMaxPathLength(),
                                 false, moon ) );//so it doesn't intersect with earth mass readout
        }} );
        int SEC_PER_MOON_ORBIT = 28 * 24 * 60 * 60;
        add( new GravityAndOrbitsMode( GAOStrings.PLANET_AND_MOON,
                                       VectorNode.FORCE_SCALE * 45,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT / 3,
                                       days,
                                       createIconImage( false, true, true, false, sun, earth, moon, spaceStation ),
                                       SEC_PER_MOON_ORBIT,
                                       SUN_MODES_VELOCITY_SCALE * 0.06,
                                       readoutInEarthMasses,
                                       new Line2D.Double( earth.x, -moon.y / 4, moon.x + moon.y, -moon.y / 4 ),
                                       400,
                                       new ImmutableVector2D( earth.x, 0 ),
                                       moon.y / 2,
                                       new Point2D.Double( earth.x, 0 ),
                                       p ) {{
            // Add in some initial -x velocity to offset the earth-moon barycenter drift
            //This value was computed by sampling the total momentum in GravityAndOrbitsModel for this mode
            ImmutableVector2D sampledSystemMomentum = new ImmutableVector2D( 7.421397422188586E25, -1.080211713202125E22 );
            ImmutableVector2D velocityOffset = sampledSystemMomentum.getScaledInstance( -1 / ( earth.mass + moon.mass ) );
            //scale so it is a similar size to other modes
            addBody( createPlanet( velocityOffset.getX(), velocityOffset.getY(), getMaxPathLength(), earth ) );
            addBody( createMoon( moon.vx, 0, true, getMaxPathLength(), true, moon ) );
        }} );
        Function2<BodyNode, Property<Boolean>, PNode> spaceStationMassReadoutFactory = new Function2<BodyNode, Property<Boolean>, PNode>() {
            public PNode apply( BodyNode bodyNode, Property<Boolean> visible ) {
                return new SpaceStationMassReadoutNode( bodyNode, visible );
            }
        };
        add( new GravityAndOrbitsMode( GAOStrings.PLANET_AND_SPACE_STATION,
                                       VectorNode.FORCE_SCALE * 3E13,
                                       false,
                                       GravityAndOrbitsClock.DEFAULT_DT * 9E-4,
                                       minutes,
                                       createIconImage( false, true, false, true, sun, earth, moon, spaceStation ),
                                       5400,
                                       SUN_MODES_VELOCITY_SCALE / 10000,
                                       spaceStationMassReadoutFactory,
                                       new Line2D.Double( earth.x, -earth.radius / 6, spaceStation.x, -earth.radius / 6 ),
                                       21600,
                                       new ImmutableVector2D( earth.x, 0 ),
                                       ( spaceStation.x - earth.x ) * 15,
                                       new Point2D.Double( earth.x, 0 ),
                                       p ) {{
            addBody( createPlanet( 0, 0, getMaxPathLength(), earth ) );
            addBody( createSpaceStation( getMaxPathLength(), spaceStation ) );
        }} );
    }

    //Creates an image that can be used for the mode icon, showing the nodes of each body in the mode.
    private Image createIconImage( final boolean sun, final boolean earth, final boolean moon, final boolean spaceStation, final BodyPrototype sunSpec, final BodyPrototype earthSpec, final BodyPrototype moonSpec, final BodyPrototype spaceStationSpec ) {
        return new PNode() {
            {
                int inset = 5;//distance between icons
                addChild( new PhetPPath( new Rectangle2D.Double( 20, 0, 1, 1 ), new Color( 0, 0, 0, 0 ) ) );
                addIcon( inset, new PImage( new BodyRenderer.SphereRenderer( Color.yellow, Color.white, 30 ).toImage() ), sun );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "earth_satellite.gif" ), 30 ) ), earth );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "moon.png" ), 30 ) ), moon );
                addIcon( inset, new PImage( multiScaleToWidth( RESOURCES.getImage( "space-station.png" ), 30 ) ), spaceStation );
            }

            private void addIcon( int inset, PNode icon, boolean visible ) {
                addChild( icon );
                icon.setOffset( getFullBounds().getMaxX() + inset + icon.getFullBounds().getWidth() / 2, 0 );
                icon.setVisible( visible );
            }
        }.toImage();
    }

    private Body createSpaceStation( int maxPathLength, BodyPrototype body ) {
        return new Body( GAOStrings.SATELLITE, body.x, 0, body.radius * 2000, 0,
                         body.vy, body.mass, Color.gray, Color.white,
                         getImageRenderer( "space-station.png" ), p.scaleProperty, -Math.PI / 4, true, maxPathLength, true,
                         body.mass, GAOStrings.SPACE_STATION, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    private Body createMoon( double vx, double vy, boolean massSettable, int maxPathLength, final boolean massReadoutBelow, BodyPrototype body ) {
        return new Body( GAOStrings.MOON, body.x, body.y, body.radius * 2, vx, vy, body.mass, Color.magenta, Color.white,
                         //putting this number too large makes a kink or curly-q in the moon trajectory, which should be avoided
                         getRenderer( "moon.png", body.mass ), p.scaleProperty, -3 * Math.PI / 4, massSettable, maxPathLength,
                         massReadoutBelow, body.mass, GAOStrings.OUR_MOON, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    private Body createPlanet( double vx, double vy, int maxPathLength, BodyPrototype body ) {
        return new Body( GAOStrings.PLANET, body.x, 0, body.radius * 2, vx, vy, body.mass, Color.gray, Color.lightGray,
                         getRenderer( "earth_satellite.gif", body.mass ), p.scaleProperty, -Math.PI / 4, true,
                         maxPathLength, true, body.mass, GAOStrings.EARTH, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    private Body createSun( int maxPathLength, BodyPrototype body ) {
        return new Body( GAOStrings.SUN, 0, 0, body.radius * 2, 0, 0, body.mass, Color.yellow, Color.white,
                         SUN_RENDERER, p.scaleProperty, -Math.PI / 4, true, maxPathLength, true, body.mass, GAOStrings.OUR_SUN, p.clockPausedProperty, p.stepping, p.rewinding );
    }

    public static Function2<Body, Double, BodyRenderer> getImageRenderer( final String image ) {
        return new Function2<Body, Double, BodyRenderer>() {
            public BodyRenderer apply( Body body, Double viewDiameter ) {
                return new BodyRenderer.ImageRenderer( body, viewDiameter, image );
            }
        };
    }

    public static Function2<Body, Double, BodyRenderer> getRenderer( final String image, final double targetMass ) {//the mass for which to use the image
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
        }
    };

    private static final Function1<Double, String> days = new Function1<Double, String>() {
        public String apply( Double time ) {
            int value = (int) ( time / GravityAndOrbitsClock.SECONDS_PER_DAY );
            String units = ( value == 1 ) ? GAOStrings.EARTH_DAY : GAOStrings.EARTH_DAYS;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };
    private static final Function1<Double, String> minutes = new Function1<Double, String>() {
        final double SECONDS_PER_MINUTE = 60;

        public String apply( Double time ) {
            int value = (int) ( time / SECONDS_PER_MINUTE );
            String units = ( value == 1 ) ? GAOStrings.EARTH_MINUTE : GAOStrings.EARTH_MINUTES;
            return MessageFormat.format( GAOStrings.PATTERN_VALUE_UNITS, value, units );
        }
    };
}