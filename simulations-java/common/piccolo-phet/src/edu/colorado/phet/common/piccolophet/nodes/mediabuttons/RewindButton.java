package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;

/**
 * Created by: Sam
 * Sep 12, 2008 at 7:13:02 AM
 */
public class RewindButton extends DefaultIconButton {

    public RewindButton( int buttonHeight ) {
        super( buttonHeight, new ButtonIconSet( buttonHeight, buttonHeight ).createRewindIconShape() );
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        PlayPauseButton playPauseButton = new PlayPauseButton( 75 );
        testFrame.addNode( playPauseButton );

        RewindButton button = new RewindButton( 50 );
        button.setOffset( playPauseButton.getFullBounds().getMaxX(), playPauseButton.getFullBounds().getCenterY() - button.getFullBounds().getHeight() / 2 );
        testFrame.addNode( button );
        testFrame.setVisible( true );
    }
}