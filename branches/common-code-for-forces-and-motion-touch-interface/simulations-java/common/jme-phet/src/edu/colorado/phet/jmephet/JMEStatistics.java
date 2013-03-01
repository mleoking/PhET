// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.jmephet;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;

import com.jme3.app.StatsView;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial.CullHint;

/**
 * Adds the ability to show statistics to any JME application, on a particular container node
 * <p/>
 * Significantly rewritten and reworked from the SimpleApplication JME3 code.
 */
public class JMEStatistics {

    protected float secondCounter = 0.0f;
    protected BitmapText fpsText;
    protected BitmapFont guiFont;
    protected StatsView statsView;
    private boolean showFps;

    private PhetJMEApplication application;

    public void initialize( PhetJMEApplication application, Node containerNode ) {
        this.application = application;

        // load FPS
        guiFont = application.getAssetManager().loadFont( "Interface/Fonts/Default.fnt" );
        fpsText = new BitmapText( guiFont, false );
        fpsText.setLocalTranslation( 0, fpsText.getLineHeight(), 0 );
        fpsText.setText( "Frames per second" );
        containerNode.attachChild( fpsText );

        // load stats
        statsView = new StatsView( "Statistics View", application.getAssetManager(), application.getRenderer().getStatistics() );
        statsView.setLocalTranslation( 0, fpsText.getLineHeight(), 0 ); // move it up so it appears above fps text
        containerNode.attachChild( statsView );

        // defaults
        setDisplayFps( false );
        setDisplayStatView( false );

        // update on each frame
        application.addUpdateObserver( new SimpleObserver() {
            public void update() {
                updateView();
            }
        } );
    }

    public void updateView() {
        if ( showFps ) {
            secondCounter += application.getTimer().getTimePerFrame();
            int fps = (int) application.getTimer().getFrameRate();
            if ( secondCounter >= 1.0f ) {
                fpsText.setText( "Frames per second: " + fps );
                secondCounter = 0.0f;
            }
        }
    }

    public void setDisplayFps( boolean show ) {
        showFps = show;
        fpsText.setCullHint( show ? CullHint.Never : CullHint.Always );
    }

    public void setDisplayStatView( boolean show ) {
        statsView.setEnabled( show );
        statsView.setCullHint( show ? CullHint.Never : CullHint.Always );
    }

}
