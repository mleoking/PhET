/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;


public class PlayPauseButton extends AbstractMediaButton {

    private double dx;
    private double dy;
    private boolean playing;
    private PPath iconNode;

    public PlayPauseButton( int buttonHeight ) {
        super( buttonHeight );

        dx = buttonHeight / 6;
        dy = buttonHeight / 6;
        iconNode = new PhetPPath( createPlayIconShape(), Color.BLACK, new BasicStroke( 1 ), Color.LIGHT_GRAY );
        addChild( iconNode );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mouseReleased( PInputEvent event ) {
                setPlaying( !isPlaying() );
                updateShape();
            }
        } );
    }

    private void setPlaying( boolean b ) {
        this.playing = b;
        updateShape();
    }

    private void updateShape() {
        iconNode.setPathTo( isPlaying() ? createPauseIconShape() : createPlayIconShape() );
    }

    private boolean isPlaying() {
        return playing;
    }

    private Shape createPlayIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = getButtonDimension().width;
        double height = getButtonDimension().height;
        shape.moveTo( width / 2 + dx+dx/4, height / 2 );
        shape.lineToRelative( -dx * 2, dy );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( 2 * dx, dy );
        return shape.getGeneralPath();
    }

    private Shape createPauseIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = getButtonDimension().width;
        double height = getButtonDimension().height;

        double buttonSpacing=dx/4;
        double buttonWidth=dx*0.8;
        shape.moveTo( width / 2 - buttonSpacing, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( buttonWidth, 0 );

        shape.moveTo( width / 2 + buttonWidth + buttonSpacing, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( buttonWidth, 0 );
        return shape.getGeneralPath();
    }

    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame( "Button Test" );
        testFrame.addNode( new PlayPauseButton( 75 ) );
        testFrame.setVisible( true );
    }
}
