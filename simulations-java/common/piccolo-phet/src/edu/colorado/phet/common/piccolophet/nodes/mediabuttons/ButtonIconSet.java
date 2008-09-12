package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

/**
 * Factory for creating shapes to use in a media button set.
 */
public class ButtonIconSet {
    private double dx;
    private double dy;
    private int buttonWidth;
    private int buttonHeight;

    public ButtonIconSet( int buttonWidth, int buttonHeight ) {
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        dx = buttonHeight / 6;
        dy = buttonHeight / 6;
    }

    public Shape createPlayIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = buttonWidth;
        double height = buttonHeight;
        shape.moveTo( width / 2 + dx + dx / 4, height / 2 );
        shape.lineToRelative( -dx * 2, dy );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( 2 * dx, dy );
        return shape.getGeneralPath();
    }

    public Shape createPauseIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = buttonWidth;
        double height = buttonHeight;

        double buttonSpacing = dx / 4;
        double buttonWidth = dx * 0.8;
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

    public Shape createStepIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = buttonWidth;
        double height = buttonHeight;

        double buttonSpacing = dx / 5;
        double buttonWidth = dx * 0.8;
        shape.moveTo( width / 2 - buttonSpacing, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth * 0.8, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( buttonWidth * 0.8, 0 );

        shape.moveTo( width / 2 + buttonSpacing, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( buttonWidth * 1.5, -dy );
        shape.lineToRelative( -buttonWidth * 1.5, -dy );
//        shape.lineToRelative( buttonWidth, 0 );
        return shape.getGeneralPath();
    }
}
