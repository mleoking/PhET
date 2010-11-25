package edu.colorado.phet.gravityandorbits.module;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
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

    //TODO: instead of passing in the module, how about passing in a minimal required interface?
    public GravityAndOrbitsMode( String name, double forceScale ) {
        this.name = name;
        this.forceScale = forceScale;

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
}
