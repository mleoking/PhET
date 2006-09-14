/** Sam Reid*/
package edu.colorado.phet.cck3.phetgraphics_cck;

import edu.colorado.phet.cck3.common.Sine;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.model.ModelElement;
import edu.colorado.phet.common_cck.view.graphics.shapes.Arrow;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jul 12, 2004
 * Time: 11:47:26 AM
 * Copyright (c) Jul 12, 2004 by Sam Reid
 */
public class WiggleMe extends CCKCompositePhetGraphic implements ModelElement {
    private Sine sine;
    private double time = 0;
    private Point2D.Double current;
    private Font font = new Font( "Sans Serif", Font.BOLD, 16 );
    private AbstractVector2D oscillationVector;
    private Point2D startPt;
    private PhetShadowTextGraphic textGraphic;
    private String text;
    private PhetShapeGraphic arrowGraphic;

    public WiggleMe( Component parent, Point2D startPt, AbstractVector2D oscillationDir, double amplitude, double frequency, String text ) {
        super( parent );
        setVisible( false );
        this.startPt = startPt;
        this.text = text;
        sine = new Sine( 1, frequency );
        oscillationVector = oscillationDir.getInstanceOfMagnitude( amplitude );
        current = new Point2D.Double( startPt.getX(), startPt.getY() );
        arrowGraphic = new PhetShapeGraphic( parent, new Area(), Color.black );
        textGraphic = new PhetShadowTextGraphic( getComponent(), text, font, 0, 0, Color.blue, 1, 1, Color.black );
        stepInTime( 0 );
        addGraphic( arrowGraphic );
        addGraphic( textGraphic );
        setVisible( true );
    }

    public void stepInTime( double dt ) {
        if( !isVisible() ) {
            return;
        }
        this.time += dt;
        double value = sine.valueAtTime( time );
        Point2D at = oscillationVector.getScaledInstance( value ).getDestination( startPt );
        current.setLocation( at );
        if( textGraphic != null ) {
            Rectangle r1 = textGraphic.getBounds();
            textGraphic.setPosition( (int)at.getX(), (int)at.getY() );
            Rectangle r2 = textGraphic.getBounds();
            r1 = RectangleUtils.expand( r1, 5, 5 );
            r2 = RectangleUtils.expand( r2, 5, 5 );

            Point2D arrowTail = RectangleUtils.getRightCenter( textGraphic.getBounds() );
            Point2D.Double arrowTip = new Point2D.Double( arrowTail.getX() + 15, arrowTail.getY() + 12 );

            Arrow a = new Arrow( arrowTail, arrowTip, 8, 8, 4, 100, false );
            arrowGraphic.setShape( a.getShape() );
        }
        super.setBoundsDirty();
    }

    public void setCenter( Point center ) {
        this.startPt = center;
    }
}
