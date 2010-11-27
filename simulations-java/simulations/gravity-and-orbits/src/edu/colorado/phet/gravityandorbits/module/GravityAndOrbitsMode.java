package edu.colorado.phet.gravityandorbits.module;

import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.IsSelectedProperty;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.gravityandorbits.controlpanel.GORadioButton;
import edu.colorado.phet.gravityandorbits.model.Body;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsClock;
import edu.colorado.phet.gravityandorbits.model.GravityAndOrbitsModel;
import edu.colorado.phet.gravityandorbits.view.GravityAndOrbitsCanvas;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsMode {
    private String name;
    private GravityAndOrbitsModel model;
    private Property<Boolean> moonProperty = new Property<Boolean>( false );
    private GravityAndOrbitsCanvas canvas;
    private double forceScale;
    private Property<Boolean> active;
    private ArrayList<SimpleObserver> modeActiveListeners = new ArrayList<SimpleObserver>();
    private final Property<Boolean> clockRunningProperty;

    //TODO: instead of passing in the module, how about passing in a minimal required interface?
    public GravityAndOrbitsMode( String name, double forceScale, boolean active ) {
        this.name = name;
        this.forceScale = forceScale;
        this.active = new Property<Boolean>( active );

        model = new GravityAndOrbitsModel( new GravityAndOrbitsClock( GravityAndOrbitsDefaults.CLOCK_FRAME_RATE, GravityAndOrbitsDefaults.CLOCK_DT ), moonProperty );

        getMoonProperty().addObserver( new SimpleObserver() {
            public void update() {
                if ( getMoonProperty().getValue() ) {
                    for ( Body body : model.getBodies() ) {
                        body.resetAll();
                    }
                }
            }
        } );

        //Clock control panel
        clockRunningProperty = new Property<Boolean>( false ) {{
            final SimpleObserver updateClock = new SimpleObserver() {
                public void update() {
                    model.getClock().setRunning( isActive() && getValue() );
                }
            };
            //This assumes that this code is the only place that changes whether the clock is running
            //If another place called clock.start() or stop(), then this property wouldn't get a callback
            addObserver( updateClock );
            addModeActiveListener( updateClock );
        }};
    }

    public GravityAndOrbitsClock getClock() {
        return model.getClock();
    }

    public void addBody( Body body ) {
        model.addBody( body );
    }

    public String getName() {
        return name;
    }

    public GravityAndOrbitsModel getModel() {
        return model;
    }

    public void reset() {
        model.getClock().resetSimulationTime();// reset the clock
        moonProperty.reset();
        model.resetAll();
    }

    public Property<Boolean> getMoonProperty() {
        return moonProperty;
    }

    public void setRunning( boolean running ) {
        model.getClock().setRunning( running );
    }

    public JComponent getCanvas() {
        return canvas;
    }

    public void init( GravityAndOrbitsModule module ) {
        canvas = new GravityAndOrbitsCanvas( model, module, this, forceScale );
    }

    public JComponent newComponent( Property<GravityAndOrbitsMode> modeProperty ) {
        return createRadioButton( modeProperty );
    }

    protected GORadioButton createRadioButton( Property<GravityAndOrbitsMode> modeProperty ) {
        return new GORadioButton( getName(), new IsSelectedProperty<GravityAndOrbitsMode>( this, modeProperty ) );
    }

    public void setActive( boolean active ) {
        this.active.setValue( active );
    }

    public void addModeActiveListener( SimpleObserver simpleObserver ) {
        active.addObserver( simpleObserver );
    }

    public boolean isActive() {
        return active.getValue();
    }

    public Property<Boolean> getClockRunningProperty() {
        return clockRunningProperty;
    }
}
