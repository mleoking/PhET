// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

import static java.lang.System.currentTimeMillis;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements Serializable {
    private GravityAndOrbitsModuleState introState;
    private GravityAndOrbitsModuleState toScaleState;
    private long timestamp;
    private Dimension frameSize;
    private SerializableBufferedImage thumbnail;
    private int activeModule;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication app, ImageFactory imageFactory ) {
        final PhetFrame frame = app.getPhetFrame();
        introState = new GravityAndOrbitsModuleState( app.getIntro() );
        toScaleState = new GravityAndOrbitsModuleState( app.getToScale() );
        timestamp = currentTimeMillis();
        frameSize = new Dimension( frame.getWidth(), frame.getHeight() );
        thumbnail = new SerializableBufferedImage( imageFactory.getThumbnail( frame, 200 ) );
        activeModule = app.indexOf( app.getActiveModule() );
    }

    public GravityAndOrbitsApplicationState() {
    }

    public SerializableBufferedImage getThumbnail() {
        return thumbnail;
    }

    public void apply( GravityAndOrbitsApplication application ) {
//        System.out.println( "round trip time: " + ( System.currentTimeMillis() - timestamp ) );
        application.setActiveModule( activeModule );
        introState.apply( application.getIntro() );
        toScaleState.apply( application.getToScale() );
        if ( application.getPhetFrame().getSize().width != frameSize.width || application.getPhetFrame().getSize().height != frameSize.height ) {
            application.getPhetFrame().setSize( frameSize );
        }
    }

    @Override
    public String toString() {
        return "GravityAndOrbitsApplicationState{" +
               "moduleState=" + introState +
               ", timestamp=" + timestamp +
               '}';
    }
}