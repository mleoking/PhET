// Box2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.graphics;

import edu.colorado.phet.physics.body.Particle;
import edu.colorado.phet.physics.collision.CollidableBody;
import edu.colorado.phet.idealgas.physics.body.HollowSphere;
import edu.colorado.phet.graphics.ShapeGraphic;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Observable;

public class HollowSphereGraphic extends ShapeGraphic {

    private Ellipse2D.Float rep = new Ellipse2D.Float();

    public HollowSphereGraphic() {
        super( s_defaultColor, s_defaultStroke );
        init();
    }

    private void init() {
        this.setFill( s_defaultColor );
        this.setOpacity( 0.1f );
        this.setRep( rep );
    }

    public void update( Observable o, Object arg ) {
        this.setPosition( (CollidableBody)o );
    }

    protected void setPosition( Particle body ) {
        HollowSphere sphere = (HollowSphere)body;
        double x = body.getPosition().getX();
        double y = body.getPosition().getY();
        rep.setFrameFromCenter( x, y, x + sphere.getRadius(), y + sphere.getRadius() );
    }

    //
    // Static fields and methods
    //
    private static Stroke s_defaultStroke = new BasicStroke( 2.0F );
    private static Color s_defaultColor = Color.GREEN;
}
