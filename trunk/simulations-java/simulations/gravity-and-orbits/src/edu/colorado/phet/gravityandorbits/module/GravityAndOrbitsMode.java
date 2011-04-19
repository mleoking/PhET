// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

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

//REVIEW Very confusing that this is unrelated to class ModeList.Mode.  What is the relationship? Is something misnamed?

/**
 * A GravityAndOrbitsMode behaves like a module, it has its own model, control panel, canvas, and remembers its state when you leave and come back.
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
    private final String name;
    private final GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;//REVIEW javadoc says that a mode has its own canvas, so explain why this isn't final.
    private final double forceScale;
    public final Property<Boolean> active;
    private final Function1<Double, String> timeFormatter;
    private final Image iconImage;
    private final double defaultOrbitalPeriod;//REVIEW doc
    private final double dt;
    private final double velocityScale; //REVIEW doc
    private final Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory;//REVIEW doc
    private final Property<Boolean> deviatedFromEarthValuesProperty = new Property<Boolean>( false ); //REVIEW doc
    private double rewindClockTime;
    //the play area only takes up the left side of the canvas; the control panel is on the right side
    private static final double PLAY_AREA_WIDTH = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60; //REVIEW not robust, this does not account for variable control panel size due to i18n
    private static final double PLAY_AREA_HEIGHT = GravityAndOrbitsCanvas.STAGE_SIZE.height;
    private final double gridSpacing;//in meters
    private final Point2D.Double gridCenter;

    public final Property<ModelViewTransform> modelViewTransformProperty;
    public final Property<Boolean> rewinding;
    public final Property<Double> timeSpeedScaleProperty;
    public final Property<ImmutableVector2D> measuringTapeStartPoint;
    public final Property<ImmutableVector2D> measuringTapeEndPoint;
    public final double defaultZoomScale;//REVIEW not used
    public final Property<Double> zoomLevel = new Property<Double>( 1.0 );//additional scale factor on top of defaultZoomScale
    public final ModeListParameterList p;

    //REVIEW this constructor is worth documenting, many non-obvious parameters
    public GravityAndOrbitsMode( final String name,//mode name, currently used only for debugging, i18n not required
                                 double forceScale, boolean active, double dt, Function1<Double, String> timeFormatter, Image iconImage,
                                 double defaultOrbitalPeriod,//for determining the length of the path
                                 double velocityScale, Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory,
                                 Line2D.Double initialMeasuringTapeLocation, final double defaultZoomScale, final ImmutableVector2D zoomOffset,
                                 double gridSpacing, Point2D.Double gridCenter,
                                 final ModeListParameterList p ) {
        this.dt = dt;
        this.p = p;
        this.defaultZoomScale = defaultZoomScale;
        this.name = name;
        this.forceScale = forceScale;
        this.iconImage = iconImage;
        this.defaultOrbitalPeriod = defaultOrbitalPeriod;
        this.velocityScale = velocityScale;
        this.gridSpacing = gridSpacing;
        this.gridCenter = gridCenter;
        this.rewinding = p.rewinding;
        this.timeSpeedScaleProperty = p.timeSpeedScaleProperty;
        this.active = new Property<Boolean>( active );
        this.timeFormatter = timeFormatter;
        this.massReadoutFactory = massReadoutFactory;

        modelViewTransformProperty = new Property<ModelViewTransform>( createTransform( defaultZoomScale, zoomOffset ) );
        zoomLevel.addObserver( new SimpleObserver() {
            public void update() {
                modelViewTransformProperty.setValue( createTransform( defaultZoomScale, zoomOffset ) );
            }
        } );
        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( dt, p.stepping, timeSpeedScaleProperty ), p.gravityEnabledProperty );

        this.rewindClockTime = 0;
        //REVIEW have you addressed this?
        //TODO: this block looks nonsensical--why would we want to set the rewind clock time each time the clock is paused?
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void clockPaused( ClockEvent clockEvent ) {
                rewindClockTime = clockEvent.getSimulationTime();
            }
        } );

        new RichSimpleObserver() {
            public void update() {
                model.getClock().setRunning( !p.clockPausedProperty.getValue() && GravityAndOrbitsMode.this.active.getValue() );
            }
        }.observe( p.clockPausedProperty, this.active );
        measuringTapeStartPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( initialMeasuringTapeLocation.getP1() ) );
        measuringTapeEndPoint = new Property<ImmutableVector2D>( new ImmutableVector2D( initialMeasuringTapeLocation.getP2() ) );
    }

    //REVIEW doc. create transform for what?
    private ModelViewTransform createTransform( double defaultZoomScale, ImmutableVector2D zoomOffset ) {
        Rectangle2D.Double targetRectangle = getTargetRectangle( defaultZoomScale * zoomLevel.getValue(), zoomOffset );
        final double x = targetRectangle.getMinX();
        final double y = targetRectangle.getMinY();
        final double w = targetRectangle.getMaxX() - x;
        final double h = targetRectangle.getMaxY() - y;
        return createRectangleInvertedYMapping( new Rectangle2D.Double( x, y, w, h ), new Rectangle2D.Double( 0, 0, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT ) );
    }

    //REVIEW doc
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
                deviatedFromEarthValuesProperty.setValue( true );
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
        deviatedFromEarthValuesProperty.reset();
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
                        modeProperty.setValue( GravityAndOrbitsMode.this ); //Make it so clicking on the icon also activates the mode
                    }
                } );
            }} );
        }};
    }

    public Function1<Double, String> getTimeFormatter() {
        return timeFormatter;
    }

    //REVIEW doc
    public void resetBodies() {
        model.resetBodies();
        deviatedFromEarthValuesProperty.setValue( false );
    }

    public double getVelocityScale() {
        return velocityScale;
    }

    public Function2<BodyNode, Property<Boolean>, PNode> getMassReadoutFactory() {
        return massReadoutFactory;
    }

    //Restore the last set of initial conditions that were set while the sim was paused.
    public void rewind() {
        rewinding.setValue( true );
        getClock().setSimulationTime( rewindClockTime );
        for ( Body body : model.getBodies() ) {
            body.rewind();
        }
        rewinding.setValue( false );
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public Point2D.Double getGridCenter() {
        return gridCenter;
    }
}