// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.io.Serializable;

import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements Serializable {

    private final GravityAndOrbitsModuleState moduleState;
    private final long timestamp;
    private final Dimension frameSize;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication gravityAndOrbitsApplication ) {
        moduleState = new GravityAndOrbitsModuleState( gravityAndOrbitsApplication.getIntro() );
        timestamp = System.currentTimeMillis();
        frameSize = new Dimension( gravityAndOrbitsApplication.getPhetFrame().getWidth(), gravityAndOrbitsApplication.getPhetFrame().getHeight() );
    }

    public void apply( GravityAndOrbitsApplication application ) {
//        System.out.println( "round trip time: " + ( System.currentTimeMillis() - timestamp ) );
        moduleState.apply( application.getIntro() );
        if ( application.getPhetFrame().getSize().width != frameSize.width || application.getPhetFrame().getSize().height != frameSize.height ) {
            application.getPhetFrame().setSize( frameSize );
        }
    }

    @Override
    public String toString() {
        return "GravityAndOrbitsApplicationState{" +
               "moduleState=" + moduleState +
               ", timestamp=" + timestamp +
               '}';
    }
}
