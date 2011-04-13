// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

import static java.lang.System.currentTimeMillis;

//REVIEW class doc

/**
 * Serializable state for simsharing
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements IProguardKeepClass {
    private GravityAndOrbitsModuleState introState;
    private GravityAndOrbitsModuleState toScaleState;
    private long timestamp;
    private int frameWidth;
    private int frameHeight;
    private byte[] thumbnail;
    private int activeModule;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication app, ImageFactory imageFactory ) {
        final PhetFrame frame = app.getPhetFrame();
        introState = new GravityAndOrbitsModuleState( app.getIntro() );
        toScaleState = new GravityAndOrbitsModuleState( app.getToScale() );
        timestamp = currentTimeMillis();
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();
        thumbnail = new SerializableBufferedImage( imageFactory.getThumbnail( frame, 200 ) ).getByteImage();
        activeModule = app.indexOf( app.getActiveModule() );
    }

    public byte[] getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail( byte[] thumbnail ) {
        this.thumbnail = thumbnail;
    }

    public GravityAndOrbitsApplicationState() {
    }

//    public SerializableBufferedImage getThumbnail() {
//        return null;
//    }

    public void apply( GravityAndOrbitsApplication application ) {
//        System.out.println( "round trip time: " + ( System.currentTimeMillis() - timestamp ) );
        application.setActiveModule( activeModule );
        introState.apply( application.getIntro() );
        toScaleState.apply( application.getToScale() );
        if ( application.getPhetFrame().getSize().width != frameWidth || application.getPhetFrame().getSize().height != frameHeight ) {
            application.getPhetFrame().setSize( new Dimension( frameWidth, frameHeight ) );
        }
    }

    @Override
    public String toString() {
        return "GravityAndOrbitsApplicationState{" +
//               "moduleState=" + introState +
               ", timestamp=" + timestamp +
               '}';
    }

    public GravityAndOrbitsModuleState getIntroState() {
        return introState;
    }

    public void setIntroState( GravityAndOrbitsModuleState introState ) {
        this.introState = introState;
    }

    public GravityAndOrbitsModuleState getToScaleState() {
        return toScaleState;
    }

    public void setToScaleState( GravityAndOrbitsModuleState toScaleState ) {
        this.toScaleState = toScaleState;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( long timestamp ) {
        this.timestamp = timestamp;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameWidth( int frameWidth ) {
        this.frameWidth = frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public void setFrameHeight( int frameHeight ) {
        this.frameHeight = frameHeight;
    }

    public int getActiveModule() {
        return activeModule;
    }

    public void setActiveModule( int activeModule ) {
        this.activeModule = activeModule;
    }
}