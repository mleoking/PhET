/** Sam Reid*/
package edu.colorado.phet.cck3.common;

import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.fastpaint.FastPaint;
import edu.colorado.phet.common.view.fastpaint.FastPaintShapeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.shapes.Arrow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 24, 2004
 * Time: 4:05:31 PM
 * Copyright (c) Jun 24, 2004 by Sam Reid
 */
public class WiggleMe implements Graphic, ModelElement {
    Sine sine;
    double time = 0;
    Point2D.Double current;
    Font font = new Font( "Sans Serif", Font.BOLD, 16 );
    private AbstractVector2D oscillationVector;
    private Point2D startPt;
    MultiLineShadowTextGraphic textGraphic;
    private boolean init = false;
    private Component parent;
    private String text;
//    private Arrow arrow;
    FastPaintShapeGraphic arrowGraphic;
    boolean visible = false;

    public WiggleMe( Component parent, Point2D startPt, AbstractVector2D oscillationDir, double amplitude, double frequency, String text ) {
        this.parent = parent;
        this.text = text;
        sine = new Sine( 1, frequency );
        this.startPt = startPt;
        this.oscillationVector = oscillationDir.getInstanceOfMagnitude( amplitude );
        current = new Point2D.Double( startPt.getX(), startPt.getY() );
        arrowGraphic = new FastPaintShapeGraphic( new Area(), Color.black, parent );
    }

    public void paint( Graphics2D g ) {
        if( !visible || time == 0.0 ) {
            return;
        }
        if( !init ) {
            init( g );
            init = true;
        }
        arrowGraphic.paint( g );
        textGraphic.paint( g );
    }

    private void init( Graphics2D g ) {
        textGraphic = new MultiLineShadowTextGraphic( text, 0, 0, g.getFontRenderContext(), g.getFontMetrics( font ) );
        textGraphic.setForegroundColor( Color.blue );
        textGraphic.setShadowColor( Color.black );
        stepInTime( 0 );
    }

    public void stepInTime( double dt ) {
        if( !visible ) {
            return;
        }
        this.time += dt;
        double value = sine.valueAtTime( time );
        Point2D at = oscillationVector.getScaledInstance( value ).getDestination( startPt );
        current.setLocation( at );
        if( textGraphic != null ) {
            Rectangle r1 = textGraphic.getBounds();
            textGraphic.setLocation( at.getX(), at.getY() );
            Rectangle r2 = textGraphic.getBounds();
            r1 = RectangleUtils.expand( r1, 5, 5 );
            r2 = RectangleUtils.expand( r2, 5, 5 );

            Point2D.Double arrowTail = new Point2D.Double( current.getX() +
                                                           SwingUtilities.computeStringWidth( textGraphic.getFontMetrics(), text ) + 15,
                                                           (int)current.getY() + textGraphic.getFontMetrics().getHeight() / 2 );
            Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );

            Arrow a = new Arrow( arrowTail, arrowTip, 8, 8, 4, 100, false );
            arrowGraphic.setShape( a.getShape() );
            FastPaint.fastRepaint( parent, r1, r2 );
        }
    }

    public Rectangle2D getBounds2D() {
        return textGraphic.getBounds().createUnion( arrowGraphic.getBounds() );
    }

    public Rectangle getBounds() {
        Rectangle2D b = getBounds2D();
        return new Rectangle( (int)b.getX(), (int)b.getY(), (int)b.getWidth(), (int)b.getHeight() );
    }

    public void setCenter( Point2D pt ) {
        this.startPt = pt;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }
}
