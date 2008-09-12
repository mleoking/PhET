/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;


public class PlayPauseButton extends AbstractMediaButton {
    private boolean playing;
    private PPath iconNode;
    private ButtonIconSet buttonIconSet;
    private ArrayList listeners = new ArrayList();
    
    public PlayPauseButton( int buttonHeight ) {
        super( buttonHeight );

        buttonIconSet = new ButtonIconSet( buttonHeight, buttonHeight );
        iconNode = new PhetPPath( buttonIconSet.createPlayIconShape(), Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                setPlaying( !isPlaying() );
                updateShape();
                notifyListener();
            }
        } );
        setPlaying( true );
    }

    private void setPlaying( boolean b ) {
        this.playing = b;
        updateShape();
    }

    private void updateShape() {
        iconNode.setPathTo( isPlaying() ? buttonIconSet.createPauseIconShape() : buttonIconSet.createPlayIconShape() );
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
