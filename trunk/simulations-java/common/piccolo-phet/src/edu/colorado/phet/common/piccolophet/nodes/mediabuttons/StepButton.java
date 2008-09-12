package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;

/**
 * Created by: Sam
 * Sep 12, 2008 at 7:13:02 AM
 */
public class StepButton extends AbstractMediaButton {
    private PhetPPath iconNode;

    public StepButton( int buttonHeight ) {
        super( buttonHeight );
        ButtonIconSet buttonIconSet = new ButtonIconSet( buttonHeight, buttonHeight );
        iconNode = new PhetPPath( buttonIconSet.createStepIconShape(), Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        PlayPauseButton playPauseButton = new PlayPauseButton( 75 );
        testFrame.addNode( playPauseButton );

        StepButton button = new StepButton( 50 );
        button.setOffset( playPauseButton.getFullBounds().getMaxX(), playPauseButton.getFullBounds().getCenterY() - button.getFullBounds().getHeight() / 2 );
        testFrame.addNode( button );
        testFrame.setVisible( true );
    }
}
