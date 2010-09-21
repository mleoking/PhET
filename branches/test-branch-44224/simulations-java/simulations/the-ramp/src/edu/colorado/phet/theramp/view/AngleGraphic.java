/*  */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: May 8, 2005
 * Time: 1:51:39 PM
 */

public class AngleGraphic extends PNode {
    private SurfaceGraphic surfaceGraphic;
    private PPath phetShapeGraphic;
    private HTMLNode label;

    public AngleGraphic( SurfaceGraphic surfaceGraphic ) {
        super();
        this.surfaceGraphic = surfaceGraphic;
        phetShapeGraphic = new PPath( null );
        phetShapeGraphic.setStroke( new BasicStroke( 2 ) );
        phetShapeGraphic.setStrokePaint( Color.black );
//        phetShapeGraphic = new PhetShapeGraphic( getComponent(), null, new BasicStroke( 2 ), Color.black );
        label = new HTMLNode( "test" );
        label.setFont( new PhetFont( 14 ) );
//        label = new ShadowHTMLGraphic( getComponent(), "test", new Font( PhetDefaultFont.LUCIDA_SANS, 0, 14 ), Color.black, 1, 1, Color.gray );
        addChild( phetShapeGraphic );
        addChild( label );
        update();
    }

    public void update() {
        Point origin = surfaceGraphic.getViewLocation( 0 );
//        if( getRampWorld() != null ) {
//            origin = getRampWorld().convertToWorld( origin );
//        }
        Point twoMetersOver = getGroundLocationView( 5 );

        int squareWidth = ( twoMetersOver.x - origin.x ) * 2;

        Rectangle2D ellipseBounds = new Rectangle2D.Double();
        ellipseBounds.setFrameFromCenter( origin, new Point2D.Double( origin.x + squareWidth / 2, origin.y + squareWidth / 2 ) );

        double extent = surfaceGraphic.getSurface().getAngle() * 180 / Math.PI;
        extent = Math.max( extent, 0.00001 );
        Arc2D.Double arc = new Arc2D.Double( ellipseBounds, 0, extent, Arc2D.OPEN );
        phetShapeGraphic.setPathTo( arc );

        //todo PICCOLO
        label.setOffset( arc.getBounds().getMaxX(), arc.getBounds().getY() + arc.getBounds().getHeight() / 2 + 20 );
//        label.setLocation( RectangleUtils.getRightCenter( phetShapeGraphic.getBoundsInAncestor( getRampWorld() ) ) );
//        label.setLocation( label.getLocation().x, label.getLocation().y + surfaceGraphic.getImageHeight() + 5 );
        label.setHTML( "" + getAngleMessage() );

    }

    private RampWorld getRampWorld() {
        PNode parent = getParent();
        while( parent != null ) {
            if( parent instanceof RampWorld ) {
                return (RampWorld)parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    private String getAngleMessage() {
        double angle = surfaceGraphic.getSurface().getAngle() * 180 / Math.PI;
        String text = "<html>" + new DecimalFormat( "0.0" ).format( angle ) + "<sup>o</sup></html>";
        return text;
    }

    private Point getGroundLocationView( double dist ) {
        Point2D modelOrigion = surfaceGraphic.getSurface().getOrigin();
        Point2D.Double pt = new Point2D.Double( modelOrigion.getX() + dist, modelOrigion.getY() );
        return surfaceGraphic.getViewLocation( pt );
    }
}
