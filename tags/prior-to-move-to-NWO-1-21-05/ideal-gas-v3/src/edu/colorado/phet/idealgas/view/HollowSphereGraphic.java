// Box2DGraphic

/*
 * User: Ron LeMaster
 * Date: Oct 18, 2002
 * Time: 10:55:17 AM
 */
package edu.colorado.phet.idealgas.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.idealgas.model.HollowSphere;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class HollowSphereGraphic extends PhetShapeGraphic implements SimpleObserver {

    private static Stroke s_defaultStroke = new BasicStroke( 2.0F );
    private static Color s_defaultColor = Color.GREEN;
    private static float s_sphereOpacity = 0.1f;

    private Ellipse2D.Double rep;
    private HollowSphere sphere;

    public HollowSphereGraphic( Component component, HollowSphere sphere ) {
        super( component, null, s_defaultColor, s_defaultStroke, s_defaultColor );
        this.sphere = sphere;
        sphere.addObserver( this );
        rep = new Ellipse2D.Double();
        setShape( rep );
        update();
    }

    public void update() {
        //    public void update( Observable o, Object arg ) {
        //        this.setPosition( sphere );
        rep.setFrameFromCenter( sphere.getPosition().getX(), sphere.getPosition().getY(),
                                sphere.getPosition().getX() + sphere.getRadius(),
                                sphere.getPosition().getY() + sphere.getRadius() );
        setBoundsDirty();
        repaint();
    }

    public void paint( Graphics2D g ) {
        saveGraphicsState( g );

        g.setColor( s_defaultColor );
        g.setStroke( s_defaultStroke );
        GraphicsUtil.setAntiAliasingOn( g );
        g.draw( rep );
        GraphicsUtil.setAlpha( g, s_sphereOpacity );
        g.fill( rep );

        if( sphere.contactPt != null ) {
            GraphicsUtil.setAlpha( g, 1 );
            g.setColor( Color.red );
            g.fillArc( (int)sphere.contactPt.getX() - 1, (int)sphere.contactPt.getY() - 1, 2, 2, 0, 360 );
        }

        restoreGraphicsState();
    }
}
