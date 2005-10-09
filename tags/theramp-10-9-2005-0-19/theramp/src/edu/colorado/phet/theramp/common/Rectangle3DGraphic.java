/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.DoubleGeneralPath;

import java.awt.*;

/**
 * User: Sam Reid
 * Date: Feb 12, 2005
 * Time: 9:34:31 AM
 * Copyright (c) Feb 12, 2005 by Sam Reid
 */

public class Rectangle3DGraphic extends CompositePhetGraphic {
    private Rectangle rectangle;
    private PhetShapeGraphic faceGraphic;
    private PhetShapeGraphic sideGraphic;
    private PhetShapeGraphic topGraphic;
    private int dx;
    private int dy;

    public Rectangle3DGraphic( Component component, Rectangle rectangle, Paint face, Stroke stroke, Paint top, Paint right, int dx, int dy, Paint borderPaint ) {
        super( component );
        this.rectangle = rectangle;
        this.dx = dx;
        this.dy = dy;
        faceGraphic = new PhetShapeGraphic( component, rectangle, face, stroke, borderPaint );
        topGraphic = new PhetShapeGraphic( component, null, top, stroke, borderPaint );
        sideGraphic = new PhetShapeGraphic( component, null, right, stroke, borderPaint );

        addGraphic( sideGraphic );
        addGraphic( topGraphic );
        addGraphic( faceGraphic );
        update();
    }

    public void setFacePaint( Paint face ) {
        faceGraphic.setPaint( face );
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle( Rectangle rectangle ) {
        this.rectangle = rectangle;
        update();
    }

    private void update() {
        if( rectangle != null ) {
            Point startA = new Point( rectangle.x, dy < 0 ? rectangle.y : rectangle.y + rectangle.height );
            DoubleGeneralPath topPath = new DoubleGeneralPath( startA );
            topPath.lineToRelative( dx, dy );
            topPath.lineToRelative( rectangle.width, 0 );
            topPath.lineToRelative( -dx, -dy );
            topPath.lineTo( startA );
            topGraphic.setShape( topPath.getGeneralPath() );

            Point startB = new Point( dx < 0 ? rectangle.x : rectangle.x + rectangle.width, rectangle.y );
            DoubleGeneralPath rightPath = new DoubleGeneralPath( startB );
            rightPath.lineToRelative( dx, dy );
            rightPath.lineToRelative( 0, rectangle.height );
            rightPath.lineToRelative( -dx, -dy );
            rightPath.lineTo( startB );
            sideGraphic.setShape( rightPath.getGeneralPath() );

            faceGraphic.setShape( rectangle );
            setBoundsDirty();
            autorepaint();
        }
    }

    public void setDx( int dx ) {
        this.dx = dx;
        update();
    }

    public void setDy( int dy ) {
        this.dy = dy;
        update();
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}
