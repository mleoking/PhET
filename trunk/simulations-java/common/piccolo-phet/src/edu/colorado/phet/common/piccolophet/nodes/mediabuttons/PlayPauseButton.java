/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;


public class PlayPauseButton extends IconButton {
    private boolean playing;
    private ButtonIconSet buttonIconSet;
    private ArrayList listeners = new ArrayList();
    private ShadowPText pauseLabel;

    public PlayPauseButton( int buttonHeight ) {
        super( buttonHeight );

        buttonIconSet = new ButtonIconSet( buttonHeight, buttonHeight );

        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                if ( isEnabled() ) {
                    setPlaying( !isPlaying() );
                    update();
                    notifyListener();
                }
            }
        } );

        pauseLabel = new ShadowPText( "Paused" );   // TODO: Make this translatable.
        pauseLabel.setTextPaint( Color.RED );
        pauseLabel.setFont( new PhetFont( PhetFont.getDefaultFontSize(), true ) );
        //Wendy recommends to interview without pause label first, 9-18-2008
//        addChild( pauseLabel );

        setPlaying( true );
    }

    public void setPlaying( boolean b ) {
        this.playing = b;
        update();
        updateImage();
    }

    private void update() {
        super.setIconPath( isPlaying() ? buttonIconSet.createPauseIconShape() : buttonIconSet.createPlayIconShape() );
        PDimension buttonDimension = getButtonDimension();
        pauseLabel.setScale( 1 );
        pauseLabel.setScale( buttonDimension.width / pauseLabel.getFullBounds().width );
        pauseLabel.setOffset( ( buttonDimension.width / 2 ) - ( pauseLabel.getFullBounds().width / 2 ), 0 );
        pauseLabel.setVisible( !isPlaying() );
    }

    public boolean isPlaying() {
        return playing;
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        testFrame.addNode( new PlayPauseButton( 75 ) );
        testFrame.setVisible( true );
    }

    public static interface Listener {
        void playbackStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).playbackStateChanged();
        }
    }
}
