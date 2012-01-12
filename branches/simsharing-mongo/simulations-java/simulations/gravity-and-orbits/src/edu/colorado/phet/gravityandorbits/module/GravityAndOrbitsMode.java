// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.module;

import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.Function1;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.controlpanel.GAORadioButton;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.BodyNode;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform.createRectangleInvertedYMapping;

/**
 * A GravityAndOrbitsMode behaves like a module, it has its own model, control panel, canvas, and remembers its state when you leave and come back.  It is created with defaults from ModeList.Mode.
 * <p/>
 * The sim was designed this way so that objects are replaced instead of mutated.
 * For instance, when switching from Mode 1 to Mode 2, instead of removing Mode 1 bodies from the model, storing their state, and replacing with the Mode 2 bodies,
 * this paradigm just replaces the entire model instance.
 * <p/>
 * The advantage of this approach is that model states, canvas states and control panels are always correct, and it is impossible to end up with a bug in which you have
 * a mixture of components from multiple modes.
 *
 * @author Sam Reid
 */
public abstract class GravityAndOrbitsMode {
    private final GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;//Not final because it must be set lazily, after we can get a reference to the parent module
    private final double forceScale;
    public final Property<Boolean> active;
    private final Function1<Double, String> timeFormatter;
    private final Image iconImage;
    private final double defaultOrbitalPeriod;//Precomputed value for the orbital period under default conditions (i.e. no other changes), for purposes of determining the path length (about 2 orbits)
    private final double dt;
    private final double velocityVectorScale; //How much to scale (shrink or grow) the velocity vectors; a mapping from meters/second to stage coordinates
    public final Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory;//Function that creates a PNode to readout the mass for the specified bodynode (with the specified visibility flag)
    private final Property<Boolean> deviatedFromDefaults = new Property<Boolean>( false ); //Flag to indicate whether any value has deviated from the original value (which was originally used for showing a reset button, but not anymore)
    private double rewindClockTime;
    //the play area only takes up the left side of the canvas; the control panel is on the right side
    private static final double PLAY_AREA_WIDTH = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60; //TODO: not robust, this does not account for variable control panel size due to i18n
    private static final double PLAY_AREA_HEIGHT = GravityAndOrbitsCanvas.STAGE_SIZE.height;
    private final double gridSpacing;//in meters
    private final Point2D.Double gridCenter;

    public final Property<ModelViewTransform> transform;
    public final Property<Boolean> rewinding;
    public final Property<Double> timeSpeedScaleProperty;
    public final Property<ImmutableVector2D> measuringTapeStartPoint;
    public final Property<ImmutableVector2D> measuringTapeEndPoint;
    public final Property<Double> zoomLevel = new Property<Double>( 1.0 );//additional scale factor on top of defaultZoomScale
    public final ModeListParameterList p;

    //Create a new GravityAndOrbitsMode that shares ModeListParameterList values with other modes
    public GravityAndOrbitsMode( double forceScale, boolean active, double dt, Function1<Double, String> timeFormatter, Image iconImage, double defaultOrbitalPeriod, double velocityVectorScale,
                                 Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory, Line2D.Double initialMeasuringTapeLocation, final double defaultZoomScale,
                                 final ImmutableVector2D zoomOffset, double gridSpacing, Point2D.Double gridCenter, final ModeListParameterList p ) {
        this.dt = dt;
        this.p = p;
        this.forceScale = forceScale;
        this.iconImage = iconImage;
        this.defaultOrbitalPeriod = defaultOrbitalPeriod;
        this.velocityVectorScale = velocityVectorScale;
        this.gridSpacing = gridSpacing;
        this.gridCenter = gridCenter;
        this.rewinding = p.rewinding;
        this.timeSpeedScaleProperty = p.timeSpeedScale;
        this.active = new Property<Boolean>( active );
        this.timeFormatter = timeFormatter;
        this.massReadoutFactory = massReadoutFactory;

        transform = new Property<ModelViewTransform>( createTransform( defaultZoomScale, zoomOffset ) );
        zoomLevel.addObserver( new SimpleObserver() {
            public void update() {
                transform.set( createTransform( defaultZoomScale, zoomOffset ) );
            }
        } );
        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( dt, p.stepping, timeSpeedScaleProperty ), p.gravityEnabled );

        //When the user pauses the clock, assume they will change some other parameters as well, and set a new rewind point
        this.rewindClockTime = 0;
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void clockPaused( ClockEvent clockEvent ) {
                rewindClockTime = clockEvent.getSimulationTime();
            }
        } );

        new RichSimpleObserver() {
            public void update() {
                model.getClock().setRunning( p.playButtonPressed.get() && GravityAndOrbitsMode.this.active.get() );
            }
        }.observe( p.playButtonPressed, this.active );
        measuringTapeStartPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( initialMeasuringTapeLocation.getP1() ) );
        measuringTapeEndPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( initialMeasuringTapeLocation.getP2() ) );
    }

    //Create the transform from model coordinates to stage coordinates
    private ModelViewTransform createTransform( double defaultZoomScale, ImmutableVector2D zoomOffset ) {
        Rectangle2D.Double targetRectangle = getTargetRectangle( defaultZoomScale * zoomLevel.get(), zoomOffset );
        final double x = targetRectangle.getMinX();
        final double y = targetRectangle.getMinY();
        final double w = targetRectangle.getMaxX() - x;
        final double h = targetRectangle.getMaxY() - y;
        return createRectangleInvertedYMapping( new Rectangle2D.Double( x, y, w, h ), new Rectangle2D.Double( 0, 0, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT ) );
    }

    //Find the rectangle that should be viewed in the model
    public static Rectangle2D.Double getTargetRectangle( double targetScale, ImmutableVector2D targetCenterModelPoint ) {
        double z = targetScale * 1.5E-9;
        double modelWidth = PLAY_AREA_WIDTH / z;
        double modelHeight = PLAY_AREA_HEIGHT / z;
        return new Rectangle2D.Double( -modelWidth / 2 + targetCenterModelPoint.getX(), -modelHeight / 2 + targetCenterModelPoint.getY(), modelWidth, modelHeight );
    }

    /*
     * Gets the number of points that should be used to draw a trace, should be enough so that two periods for the default orbit are visible.
     */
    public int getMaxPathLength() {
        double numberOfPathPeriods = 1.5;//couldn't use 2 as requested because it caused an awkward looking behavior for the lunar orbit
        return (int) ( Math.ceil( numberOfPathPeriods * defaultOrbitalPeriod / dt ) );
    }

    public GravityAndOrbitsClock getClock() {
        return model.getClock();
    }

    public void addBody( Body body ) {
        model.addBody( body );
        final SimpleObserver updater = new SimpleObserver() {
            public void update() {
                deviatedFromDefaults.set( true );
            }
        };
        body.getMassProperty().addObserver( updater, false );
        final VoidFunction0 update = new VoidFunction0() {
            public void apply() {
                updater.update();
            }
        };
        body.addUserModifiedPositionListener( update );
        body.addUserModifiedVelocityListener( update );
    }

    public GravityAndOrbitsModel getModel() {
        return model;
    }

    public void reset() {
        model.getClock().resetSimulationTime();// reset the clock
        model.resetAll();
        deviatedFromDefaults.reset();
        measuringTapeStartPoint.reset();
        measuringTapeEndPoint.reset();
        zoomLevel.reset();
        //TODO: any other properties to reset here?
    }

    public JComponent getCanvas() {
        return canvas;
    }

    public void init( GravityAndOrbitsModule module ) {
        canvas = new GravityAndOrbitsCanvas( model, module, this, forceScale );
    }

    //Create a control for the specified mode
    public JComponent newControl( final Property<GravityAndOrbitsMode> modeProperty ) {
        return new JPanel() {{
            setForeground( GravityAndOrbitsControlPanel.FOREGROUND );
            add( new GAORadioButton<GravityAndOrbitsMode>( null, modeProperty, GravityAndOrbitsMode.this ) );
            add( new JLabel( new ImageIcon( iconImage ) ) {{
                addMouseListener( new MouseAdapter() {
                    @Override
                    public void mouseReleased( MouseEvent e ) {
                        modeProperty.set( GravityAndOrbitsMode.this ); //Make it so clicking on the icon also activates the mode
                    }
                } );
            }} );
        }};
    }

    public Function1<Double, String> getTimeFormatter() {
        return timeFormatter;
    }

    //Return the bodies to their original states when the user presses "reset" (not "reset all")
    public void resetMode() {
        model.resetBodies();
        deviatedFromDefaults.set( false );
        getClock().setSimulationTime( 0.0 );//Same as pressing "clear" in the FloatingClockControlNode
    }

    public double getVelocityVectorScale() {
        return velocityVectorScale;
    }

    //Restore the last set of initial conditions that were set while the sim was paused.
    public void rewind() {
        rewinding.set( true );
        getClock().setSimulationTime( rewindClockTime );
        for ( Body body : model.getBodies() ) {
            body.rewind();
        }
        rewinding.set( false );
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public Point2D.Double getGridCenter() {
        return gridCenter;
    }
}