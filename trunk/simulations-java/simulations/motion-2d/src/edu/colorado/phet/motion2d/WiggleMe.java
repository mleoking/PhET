package edu.colorado.phet.motion2d;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetShapeGraphic;

/**
 * User: Sam Reid
 * Date: Jul 12, 2004
 * Time: 11:47:26 AM
 */
public class WiggleMe extends CompositePhetGraphic implements ModelElement {
    private Sine sine;
    private double time = 0;
    private Point2D.Double current;
    private Font font = new PhetFont( 16, true );
    private AbstractVector2D oscillationVector;
    private Point2D startPt;
    private PhetShadowTextGraphic textGraphic;
    private PhetShapeGraphic arrowGraphic;

    public WiggleMe( Component parent, Point2D startPt, AbstractVector2D oscillationDir, double amplitude, double frequency, String text ) {
        super( parent );
        setVisible( false );
        this.startPt = startPt;
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

    public int getWidth() {
        return (int) ( textGraphic.getBounds().width + .5 );
    }

    public void stepInTime( double dt ) {
        if ( !isVisible() ) {
            return;
        }
        this.time += dt;
        double value = sine.valueAtTime( time );
        Point2D at = oscillationVector.getScaledInstance( value ).getDestination( startPt );
        current.setLocation( at );
        if ( textGraphic != null ) {
            Rectangle r1 = textGraphic.getBounds();
            textGraphic.setLocation( (int) at.getX(), (int) at.getY() );
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

    public static class Sine {
        private double frequency;
        private double amplitude;
        private double phase = 0;

        public Sine( double amplitude, double frequency ) {
            this.frequency = frequency;
            this.amplitude = amplitude;
        }

        public double valueAtTime( double time ) {
            double value = 0.0;
            if ( frequency != 0 ) {
                value = Math.sin( frequency * time * Math.PI * 2 - phase ) * amplitude;
            }
            else {
                value = 0;
            }
            return value;
        }
    }


}
