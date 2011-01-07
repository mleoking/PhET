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
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.controlpanel.GAORadioButton;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.BodyNode;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolo.PNode;

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
    private GravityAndOrbitsCanvas canvas;
    private final double forceScale;
    private final Property<Boolean> active;
    private final Function1<Double, String> timeFormatter;
    private final Image iconImage;
    private final double defaultOrbitalPeriod;
    private final double dt;
    private final double velocityScale;
    private final Line2D.Double initialMeasuringTapeLocation;
    private final Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory;
    private final Property<Boolean> deviatedFromEarthValuesProperty = new Property<Boolean>( false );
    private double zoomScale;
    private ImmutableVector2D zoomOffset;
    private double rewindClockTime;
    private Property<ModelViewTransform> modelViewTransformProperty;

    //the play area only takes up the left side of the canvas; the control panel is on the right side
    private static final double PLAY_AREA_WIDTH = GravityAndOrbitsCanvas.STAGE_SIZE.width * 0.60;
    private static final double PLAY_AREA_HEIGHT = GravityAndOrbitsCanvas.STAGE_SIZE.height;
    private double gridSpacing;//in meters
    private Point2D.Double gridCenter;

    public GravityAndOrbitsMode( final String name,//mode name, currently used only for debugging, i18n not required
                                 double forceScale, boolean active, double dt, Function1<Double, String> timeFormatter, Image iconImage,
                                 double defaultOrbitalPeriod,//for determining the length of the path
                                 final Property<Boolean> clockPaused, double velocityScale, Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory,
                                 Line2D.Double initialMeasuringTapeLocation, double zoomScale, ImmutableVector2D zoomOffset,
                                 Property<Boolean> gravityEnabledProperty, double gridSpacing, Point2D.Double gridCenter ) {
        this.dt = dt;
        this.name = name;
        this.forceScale = forceScale;
        this.iconImage = iconImage;
        this.defaultOrbitalPeriod = defaultOrbitalPeriod;
        this.velocityScale = velocityScale;
        this.initialMeasuringTapeLocation = initialMeasuringTapeLocation;
        this.zoomScale = zoomScale;
        this.zoomOffset = zoomOffset;
        this.gridSpacing = gridSpacing;
        this.gridCenter = gridCenter;
        this.active = new Property<Boolean>( active );
        this.timeFormatter = timeFormatter;
        this.massReadoutFactory = massReadoutFactory;

        Rectangle2D.Double targetRectangle = getTargetRectangle( zoomScale, zoomOffset );
        final double x = targetRectangle.getMinX();
        final double y = targetRectangle.getMinY();
        final double w = targetRectangle.getMaxX() - x;
        final double h = targetRectangle.getMaxY() - y;
        modelViewTransformProperty = new Property<ModelViewTransform>( createTransform( new Rectangle2D.Double( x, y, w, h ) ) );

        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( dt ), gravityEnabledProperty );

        this.rewindClockTime = 0;
        getClock().addClockListener( new ClockAdapter() {
            @Override
            public void clockPaused( ClockEvent clockEvent ) {
                rewindClockTime = clockEvent.getSimulationTime();
            }
        } );

        SimpleObserver updateClockRunning = new SimpleObserver() {
            public void update() {
                final boolean running = !clockPaused.getValue() && isActive();
                model.getClock().setRunning( running );
            }
        };
        clockPaused.addObserver( updateClockRunning );
        this.active.addObserver( updateClockRunning );
    }

    public static ModelViewTransform createTransform( Rectangle2D modelRectangle ) {
        return ModelViewTransform.createRectangleInvertedYMapping( modelRectangle, new Rectangle2D.Double( 0, 0, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT ) );
    }

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

    public String getName() {
        return name;
    }

    public GravityAndOrbitsModel getModel() {
        return model;
    }

    public void reset() {
        model.getClock().resetSimulationTime();// reset the clock
        model.resetAll();
        deviatedFromEarthValuesProperty.reset();
    }

    public JComponent getCanvas() {
        return canvas;
    }

    public void init( GravityAndOrbitsModule module ) {
        canvas = new GravityAndOrbitsCanvas( model, module, this, forceScale );
    }

    public JComponent newComponent( Property<GravityAndOrbitsMode> modeProperty ) {
        return createRadioButtonWithIcons( modeProperty );
    }

    protected JComponent createRadioButtonWithIcons( final Property<GravityAndOrbitsMode> modeProperty ) {
        return new JPanel() {{
            setOpaque( false );//TODO: is this a mac problem?
            setBackground( GravityAndOrbitsControlPanel.BACKGROUND );
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

    public void setActive( boolean active ) {
        this.active.setValue( active );
    }

    public boolean isActive() {
        return active.getValue();
    }

    public Property<ModelViewTransform> getModelViewTransformProperty() {
        return modelViewTransformProperty;
    }

    public Function1<Double, String> getTimeFormatter() {
        return timeFormatter;
    }

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

    public Property<Boolean> getDeviatedFromEarthValuesProperty() {
        return deviatedFromEarthValuesProperty;
    }

    public Line2D.Double getInitialMeasuringTapeLocation() {
        return initialMeasuringTapeLocation;
    }

    //Restore the last set of initial conditions that were set while the sim was paused.
    public void rewind() {
        getClock().setSimulationTime( rewindClockTime );
        for ( Body body : model.getBodies() ) {
            body.rewind();
        }
    }

    public double getGridSpacing() {
        return gridSpacing;
    }

    public Point2D.Double getGridCenter() {
        return gridCenter;
    }
}
