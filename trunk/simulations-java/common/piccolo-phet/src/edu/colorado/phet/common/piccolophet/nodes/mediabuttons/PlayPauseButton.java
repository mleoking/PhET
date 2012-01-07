// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler;
import edu.colorado.phet.common.piccolophet.event.ButtonEventHandler.ButtonEventAdapter;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.ParameterKeys.isPlaying;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.SimSharingConstants.User.UserComponents.playPauseButton;

public class PlayPauseButton extends IconButton {

    private boolean playing;
    private ButtonIconSet buttonIconSet;
    private ArrayList listeners = new ArrayList();

    public PlayPauseButton( int buttonHeight ) {
        super( buttonHeight );
        buttonIconSet = new ButtonIconSet( buttonHeight, buttonHeight );

        // this handler ensures that the button won't fire unless the mouse is released while inside the button
        ButtonEventHandler handler = new ButtonEventHandler();
        addInputEventListener( handler );
        handler.addButtonEventListener( new ButtonEventAdapter() {
            public void fire() {
                if ( isEnabled() ) {

                    SimSharingManager.sendUserEvent( playPauseButton, pressed, new Parameter( isPlaying, !isPlaying() ) );

                    setPlaying( !isPlaying() );
                    update();
                    notifyListeners();
                }
            }
        } );

        setPlaying( true );
    }

    public void setPlaying( boolean b ) {
        this.playing = b;
        update();
        updateImage();
    }

    public boolean isPlaying() {
        return playing;
    }

    private void update() {
        setIconPath( isPlaying() ? buttonIconSet.createPauseIconShape() : buttonIconSet.createPlayIconShape() );
    }

    public static interface Listener {
        void playbackStateChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void removeListener( Listener listener ) {
        listeners.remove( listener );
    }

    private void notifyListeners() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).playbackStateChanged();
        }
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        testFrame.addNode( new PlayPauseButton( 75 ) );
        testFrame.setVisible( true );
    }
}
