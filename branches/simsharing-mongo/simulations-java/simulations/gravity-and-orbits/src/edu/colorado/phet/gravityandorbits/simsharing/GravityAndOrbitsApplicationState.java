// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.awt.Dimension;

import edu.colorado.phet.common.phetcommon.simsharing.state.ImageFactory;
import edu.colorado.phet.common.phetcommon.simsharing.state.SerializableBufferedImage;
import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;
import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.gravityandorbits.GravityAndOrbitsApplication;

import static java.lang.System.currentTimeMillis;

/**
 * Serializable state for simsharing, stores the top level application, including which module is selected.
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsApplicationState implements IProguardKeepClass, SimState {
    private GravityAndOrbitsModuleState introState;
    private GravityAndOrbitsModuleState toScaleState;
    private long timestamp;
    private int frameWidth;
    private int frameHeight;
    private int activeModule;
    private SerializableBufferedImage thumbnail;
    private int index;

    public GravityAndOrbitsApplicationState( GravityAndOrbitsApplication app, ImageFactory imageFactory, int index ) {
        this.index = index;
        final PhetFrame frame = app.getPhetFrame();
        introState = new GravityAndOrbitsModuleState( app.getIntro() );
        toScaleState = new GravityAndOrbitsModuleState( app.getToScale() );
        timestamp = currentTimeMillis();
        frameWidth = frame.getWidth();
        frameHeight = frame.getHeight();
        thumbnail = new SerializableBufferedImage( imageFactory.getThumbnail( frame, 200 ) );
        activeModule = app.indexOf( app.getActiveModule() );
    }

    public void apply( GravityAndOrbitsApplication application ) {
        application.setActiveModule( activeModule );
        introState.apply( application.getIntro() );
        toScaleState.apply( application.getToScale() );
        if ( application.getPhetFrame().getSize().width != frameWidth || application.getPhetFrame().getSize().height != frameHeight ) {
            application.getPhetFrame().setSize( new Dimension( frameWidth, frameHeight ) );
        }
    }

    @Override public String toString() {
        return "GravityAndOrbitsApplicationState{" +
               "introState=" + introState +
               ", toScaleState=" + toScaleState +
               ", timestamp=" + timestamp +
               ", frameWidth=" + frameWidth +
               ", frameHeight=" + frameHeight +
               ", activeModule=" + activeModule +
               ", thumbnail=" + thumbnail +
               '}';
    }

    public long getTime() {
        return timestamp;
    }

    public SerializableBufferedImage getImage() {
        return thumbnail;
    }

    public int getIndex() {
        return index;
    }
}