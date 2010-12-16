package edu.colorado.phet.gravityandorbits.module;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function1;
import edu.colorado.phet.common.phetcommon.util.Function2;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.gravityandorbits.controlpanel.GORadioButton;
import edu.colorado.phet.gravityandorbits.controlpanel.GravityAndOrbitsControlPanel;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.BodyNode;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;
import edu.umd.cs.piccolo.PNode;

/**
 * A GravityAndOrbitsMode behaves like a module, it has its own model, control panel, canvas, and remembers its state when you leave and come back.
 *
 * @author Sam Reid
 */
public abstract class GravityAndOrbitsMode {
    private final String name;
    private final GravityAndOrbitsModel model;
    private GravityAndOrbitsCanvas canvas;
    private final double forceScale;
    private final Camera camera;
    private final Property<Boolean> active;
    private final Function1<Double, String> timeFormatter;
    private final Image iconImage;
    private final double defaultOrbitalPeriod;
    private final double dt;
    private final double velocityScale;
    private final Line2D.Double initialMeasuringTapeLocation;
    private final Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory;
    private final Property<Boolean> deviatedFromEarthSystemProperty = new Property<Boolean>( false );
    private double zoomScale;
    private ImmutableVector2D zoomOffset;

    public GravityAndOrbitsMode( final String name, double forceScale, boolean active, Camera camera, double dt, Function1<Double, String> timeFormatter, Image iconImage,
                                 double defaultOrbitalPeriod,//for determining the length of the path
                                 final Property<Boolean> simPaused, double velocityScale, Function2<BodyNode, Property<Boolean>, PNode> massReadoutFactory,
                                 Line2D.Double initialMeasuringTapeLocation, double zoomScale, ImmutableVector2D zoomOffset ) {
        this.dt = dt;
        this.name = name;
        this.forceScale = forceScale;
        this.camera = camera;
        this.iconImage = iconImage;
        this.defaultOrbitalPeriod = defaultOrbitalPeriod;
        this.velocityScale = velocityScale;
        this.initialMeasuringTapeLocation = initialMeasuringTapeLocation;
        this.zoomScale = zoomScale;
        this.zoomOffset = zoomOffset;
        this.active = new Property<Boolean>( active );
        this.timeFormatter = timeFormatter;
        this.massReadoutFactory = massReadoutFactory;
        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, dt ) );

        SimpleObserver updateClockRunning = new SimpleObserver() {
            public void update() {
                final boolean running = !simPaused.getValue() && isActive();
                model.getClock().setRunning( running );
            }
        };
        simPaused.addObserver( updateClockRunning );
        this.active.addObserver( updateClockRunning );
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
                deviatedFromEarthSystemProperty.setValue( true );
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
        deviatedFromEarthSystemProperty.reset();
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
            add( new GORadioButton<GravityAndOrbitsMode>( null, modeProperty, GravityAndOrbitsMode.this ) );
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
        return camera.getModelViewTransformProperty();
    }

    //Zoom from the original zoom (default for all views) to the correct zoom for this mode
    public void startZoom() {
        camera.zoomTo( zoomScale, zoomOffset );
    }

    public Function1<Double, String> getTimeFormatter() {
        return timeFormatter;
    }

    public void resetBodies() {
        model.resetBodies();
        deviatedFromEarthSystemProperty.setValue( false );
    }

    public double getVelocityScale() {
        return velocityScale;
    }

    public Function2<BodyNode, Property<Boolean>, PNode> getMassReadoutFactory() {
        return massReadoutFactory;
    }

    public Property<Boolean> getDeviatedFromEarthSystemProperty() {
        return deviatedFromEarthSystemProperty;
    }

    public Line2D.Double getInitialMeasuringTapeLocation() {
        return initialMeasuringTapeLocation;
    }

    //Restore the last set of initial conditions that were set while the sim was paused.
    public void rewind() {
        for ( Body body : model.getBodies() ) {
            body.rewind();
        }
    }
}
