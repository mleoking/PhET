package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;

/**
 * Button for moving one step backwards in time.
 * 
 * @author John Blanco
 */
public class StepBackButton extends DefaultIconButton {

    public StepBackButton( int buttonHeight ) {
        super( buttonHeight, new ButtonIconSet( buttonHeight, buttonHeight ).createStepBackIconShape() );
    }


    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        StepBackButton button = new StepBackButton( 50 );
        button.setOffset(50, 50);
        testFrame.addNode( button );

        PlayPauseButton playPauseButton = new PlayPauseButton( 75 );
        playPauseButton.setOffset(button.getFullBounds().getMaxX(), button.getFullBounds().getCenterY() - playPauseButton.getFullBounds().getHeight() / 2);
        testFrame.addNode( playPauseButton );

        testFrame.setVisible( true );
    }

}
