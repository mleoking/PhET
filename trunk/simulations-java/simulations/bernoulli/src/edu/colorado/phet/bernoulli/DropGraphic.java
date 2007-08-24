package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.common.bernoulli.bernoulli.graphics.transform.TransformListener;
import edu.colorado.phet.common.bernoulli.bernoulli.math.PhetVector;
import edu.colorado.phet.common.bernoulli.bernoulli.simpleobserver.SimpleObserver;
import edu.colorado.phet.common.bernoulli.model.ModelElement;
import edu.colorado.phet.common.bernoulli.view.graphics.Graphic;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 9:55:44 PM
 */
public class DropGraphic implements Graphic, SimpleObserver, TransformListener {
    Drop drop;
    private ModelViewTransform2d transform;
    private RepaintManager rm;
    private BernoulliApplication application;
    private Point point;
    private int radius;
    private int brighterRadius;

    public DropGraphic( Drop drop, ModelViewTransform2d transform, RepaintManager rm, BernoulliApplication application ) {
        this.drop = drop;
        this.transform = transform;
        this.rm = rm;
        this.application = application;
        radius = transform.modelToViewDifferentialX( drop.getRadius() );
        brighterRadius = radius + 4;
        drop.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                DropGraphic.this.update();
            }
        } );
        update();
        transform.addTransformListener( this );
    }

    Color brighter = new Color( 120, 60, 255 );

    public void paint( Graphics2D g ) {
        if( point == null ) {
            return;
        }
        g.setColor( brighter );
        g.fillOval( point.x - brighterRadius / 2, point.y - brighterRadius / 2, brighterRadius, brighterRadius );
        g.setColor( Color.blue );
        g.fillOval( point.x - radius / 2, point.y - radius / 2, radius, radius );
    }

    public void update() {
        PhetVector pv = drop.getPosition();
        this.point = transform.modelToView( pv.getX(), pv.getY() );
        this.rm.update();
        if( point.y > transform.getViewBounds().height + transform.getViewBounds().y ) {
            application.removeDropAndGraphic( this );
        }
        double groundHeight = 0;
        if( pv.getY() < groundHeight ) {
            application.removeDropAndGraphic( this );
        }
    }

    public void transformChanged( ModelViewTransform2d mvt ) {
        radius = transform.modelToViewDifferentialX( drop.getRadius() );
        brighterRadius = radius + 4;
        update();
    }

    public ModelElement getDrop() {
        return drop;
    }
}
