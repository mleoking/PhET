package edu.colorado.phet.common.piccolophet.nodes.mediabuttons;

import java.awt.*;
import java.awt.geom.AffineTransform;

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
        moveInSquare( shape, buttonWidth );

        shape.moveTo( width / 2 + buttonWidth + buttonSpacing, height / 2 - dy );
        moveInSquare( shape, buttonWidth );
        return shape.getGeneralPath();
    }

    private void moveInSquare( DoubleGeneralPath shape, double buttonWidth ) {
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( buttonWidth, 0 );
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
    
    public Shape createStepBackIconShape() {
    	// To create this shape, just flip the shape from the regular step
    	// icon.  This ensures that they are always consistent mirror images
    	// of one another.  Note that translation is also needed, since the
    	// icon shape is not centered about (0,0).
    	Shape stepForwardIconShape = createStepIconShape();
    	double xTranslation = stepForwardIconShape.getBounds2D().getMaxX() + 
    		(buttonWidth - stepForwardIconShape.getBounds2D().getMaxX());
    	AffineTransform tx = new AffineTransform(-1, 0, 0, 1, xTranslation, 1);
    	Shape flippedTranslatedShape = tx.createTransformedShape(stepForwardIconShape);
    	System.out.println("4: " + flippedTranslatedShape.getBounds2D());
    	return (flippedTranslatedShape);
    }

    public Shape createRewindIconShape() {
        DoubleGeneralPath shape = new DoubleGeneralPath();
        double width = buttonWidth;
        double height = buttonHeight;

        double buttonSpacing = dx / 5;
        double buttonWidth = dx * 0.8;
        shape.moveTo( width / 2 - buttonSpacing - buttonWidth / 1.5, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth * 0.8, 0 );
        shape.lineToRelative( 0, -2 * dy );
        shape.lineToRelative( buttonWidth * 0.8, 0 );

        shape.moveTo( width / 2 + buttonSpacing + buttonWidth - buttonWidth / 1.5, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth * 1.5, -dy );
        shape.lineToRelative( buttonWidth * 1.5, -dy );

        shape.moveTo( width / 2 + buttonSpacing + buttonWidth - buttonWidth / 2 + buttonWidth + buttonSpacing * 1.43, height / 2 - dy );
        shape.lineToRelative( 0, 2 * dy );
        shape.lineToRelative( -buttonWidth * 1.5, -dy );
        shape.lineToRelative( buttonWidth * 1.5, -dy );
        return shape.getGeneralPath();
    }
}
