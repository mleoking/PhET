/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.view;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;
import edu.colorado.phet.common.view.phetgraphics.*;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.forces1d.Force1DModule;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Dec 16, 2004
 * Time: 5:57:05 PM
 * Copyright (c) Dec 16, 2004 by Sam Reid
 */

public class FreeBodyDiagram extends GraphicLayerSet {
    private Force1DModule module;
    private PhetGraphic background;
    private AxesGraphic axes;
    private Rectangle rect;

    public FreeBodyDiagram( Component component, Force1DModule module ) {
        super( component );
        this.module = module;
        rect = new Rectangle( 100, 100, 400, 400 );

        background = new PhetShapeGraphic( component, rect, Color.white, new BasicStroke( 1.0f ), Color.black );
        addGraphic( background );
        axes = new AxesGraphic( component );
        addGraphic( axes );
    }

    public void addForceArrow( ForceArrow forceArrow ) {
        addGraphic( forceArrow );
    }

    private Point2D getCenter() {
        return RectangleUtils.getCenter( rect );
    }

    public static class ForceArrow extends CompositePhetGraphic {
        private PhetShapeGraphic shapeGraphic;
        private PhetShadowTextGraphic textGraphic;
        private FreeBodyDiagram fbd;

        public ForceArrow( Component component, FreeBodyDiagram fbd, Color color, String name, Vector2D.Double v ) {
            super( component );
            this.fbd = fbd;
            shapeGraphic = new PhetShapeGraphic( component, null, color, new BasicStroke( 2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND ), Color.black );
            addGraphic( shapeGraphic );
            Font font = new Font( "Lucida Sans", Font.BOLD, 18 );
            textGraphic = new PhetShadowTextGraphic( component, name, font, 0, 0, color, 1, 1, Color.black );
            addGraphic( textGraphic );
            setVector( v );
        }

        public void setVector( AbstractVector2D v ) {
            Arrow arrow = new Arrow( fbd.getCenter(), v.getDestination( fbd.getCenter() ), 30, 30, 10, 0.5, true );
            Shape sh = arrow.getShape();
            shapeGraphic.setShape( sh );

            Rectangle b = sh.getBounds();
            Point ctr = RectangleUtils.getCenter( b );
            this.textGraphic.setLocation( b.x + b.width + 5, ctr.y - textGraphic.getHeight() / 2 );
        }
    }

    public class AxesGraphic extends CompositePhetGraphic {

        public AxesGraphic( Component component ) {
            super( component );
            Line2D.Double xLine = new Line2D.Double( rect.x, rect.y + rect.height / 2, rect.x + rect.width, rect.y + rect.height / 2 );
            Line2D.Double yLine = new Line2D.Double( rect.x + rect.width / 2, rect.y, rect.x + rect.width / 2, rect.y + rect.height );
            Stroke stroke = new BasicStroke( 2.0f );
            Color color = Color.black;
            PhetShapeGraphic xAxis = new PhetShapeGraphic( component, xLine, stroke, color );
            PhetShapeGraphic yAxis = new PhetShapeGraphic( component, yLine, stroke, color );
            addGraphic( xAxis );
            addGraphic( yAxis );


            Font font = new Font( "Lucida Sans", Font.BOLD, 18 );
            PhetTextGraphic xLabel = new PhetTextGraphic( component, font, "Fx", Color.black, 0, 0 );
            PhetTextGraphic yLabel = new PhetTextGraphic( component, font, "Fy", Color.black, 0, 0 );
            addGraphic( xLabel );
            addGraphic( yLabel );

            xLabel.setLocation( (int)xLine.getX2() - xLabel.getWidth(), (int)xLine.getY2() );
            yLabel.setLocation( (int)yLine.getX1() + 3, (int)yLine.getY1() );
        }
    }
}
