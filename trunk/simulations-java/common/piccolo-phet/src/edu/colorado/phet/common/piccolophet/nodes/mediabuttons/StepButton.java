package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.util.ArrayList;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Created by: Sam
 * Sep 12, 2008 at 7:13:02 AM
 */
public class StepButton extends AbstractMediaButton {
    private PhetPPath iconNode;
    private ArrayList listeners = new ArrayList();

    public StepButton( int buttonHeight ) {
        super( buttonHeight );
        ButtonIconSet buttonIconSet = new ButtonIconSet( buttonHeight, buttonHeight );
        iconNode = new PhetPPath( buttonIconSet.createStepIconShape(), Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                if ( isEnabled() ) {
                    notifyListener();
                }
            }
        } );
    }

    protected void updateImage() {
        super.updateImage();
        iconNode.setPaint( isEnabled() ? Color.black : Color.gray );
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

    public static interface Listener {
        void buttonPressed();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListener() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            ( (Listener) listeners.get( i ) ).buttonPressed();
        }
    }
}
