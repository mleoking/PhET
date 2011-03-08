// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

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
        thumbnail = new SerializableBufferedImage( BufferedImageUtils.multiScaleToWidth( toImage( frame ), 320 ) );
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

    public static BufferedImage toImage( JFrame frame ) {
        BufferedImage image = new BufferedImage( frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2 = image.createGraphics();
        frame.getContentPane().paint( g2 );
        g2.dispose();
        return image;
    }
}
