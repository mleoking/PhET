// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

//import static edu.colorado.phet.simsharing.ThumbnailGenerator.toImage;
import static java.lang.System.currentTimeMillis;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements Serializable {

    private final GravityAndOrbitsModuleState introState;
    private final GravityAndOrbitsModuleState toScaleState;
    private final long timestamp;
    private final Dimension frameSize;
    private final SerializableBufferedImage thumbnail;
    private final int activeModule;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication gravityAndOrbitsApplication ) {
        final PhetFrame frame = gravityAndOrbitsApplication.getPhetFrame();
        introState = new GravityAndOrbitsModuleState( gravityAndOrbitsApplication.getIntro() );
        toScaleState = new GravityAndOrbitsModuleState( gravityAndOrbitsApplication.getToScale() );
        timestamp = currentTimeMillis();
        frameSize = new Dimension( frame.getWidth(), frame.getHeight() );
        thumbnail = null;
//        thumbnail = new SerializableBufferedImage( BufferedImageUtils.multiScaleToWidth( toImage( frame ), 320 ) );
        activeModule = gravityAndOrbitsApplication.indexOf( gravityAndOrbitsApplication.getActiveModule() );
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
