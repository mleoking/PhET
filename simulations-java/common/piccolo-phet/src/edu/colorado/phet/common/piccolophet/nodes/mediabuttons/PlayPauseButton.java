/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.image.RescaleOp;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.test.PiccoloTestFrame;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;


public class PlayPauseButton extends AbstractMediaButton {

    private double dx;
    private double dy;
    
    public PlayPauseButton( int buttonHeight ) {
        super( buttonHeight );
        
        dx = buttonHeight / 6;
        dy = buttonHeight / 6;
        PPath playButton = new PhetPPath( createPlayIconShape(), Color.BLACK, new BasicStroke(1), Color.LIGHT_GRAY );
        addChild( playButton );

    }
    
    private Shape createPlayIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = getButtonDimension().width;
        double height = getButtonDimension().height;
        shape.moveTo( width / 2 + dx, height / 2 );
        shape.lineToRelative( -dx * 2, dy );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( 2 * dx, dy );
        return shape.getGeneralPath();
    }
    
    private Shape createPauseIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = getButtonDimension().width;
        double height = getButtonDimension().height;
        shape.moveTo( width / 2 - dx / 3, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -dx, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( dx, 0 );
        shape.moveTo( width / 2 + 1.33 * dx, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -dx, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( dx, 0 );
        return shape.getGeneralPath();
    }
    
    public static void main( String[] args ) {
        PiccoloTestFrame testFrame = new PiccoloTestFrame("Button Test");
        testFrame.addNode( new PlayPauseButton(75) );
        testFrame.setVisible( true );
    }
}
