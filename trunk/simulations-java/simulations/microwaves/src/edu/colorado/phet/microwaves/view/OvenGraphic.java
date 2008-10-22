/**
 * Class: OvenGraphic
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Aug 27, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Observable;

import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.microwaves.coreadditions.TxObservingGraphic;
import edu.colorado.phet.microwaves.coreadditions.collision.Box2D;

public class OvenGraphic extends TxObservingGraphic {
    private Box2D box;
    private Stroke stroke = new BasicStroke( 3.0f );
    private RoundRectangle2D.Double innerBox = new RoundRectangle2D.Double();
    private RoundRectangle2D.Double outerBox = new RoundRectangle2D.Double();

    public OvenGraphic( Box2D box, ModelViewTransform2D tx ) {
        super( tx );
        this.box = box;
        update( null, null );
    }

    public void paint( Graphics2D g ) {
        g.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
//        g.setColor( Color.BLACK );
//        g.setStroke( stroke );
//        g.draw( innerBox );
//        g.setColor( new Color( 100, 100, 100 ) );
//        g.fill( innerBox );

        g.setColor( new Color( 200, 200, 200 ) );
//        g.setColor( new Color( 100, 100, 100 ) );
        g.fill( outerBox );
        g.setColor( Color.BLACK );
        g.draw( outerBox );

        g.setColor( Color.BLACK );
        g.setStroke( stroke );
        g.draw( innerBox );
        g.setColor( new Color( 100, 100, 100 ) );
//        g.setColor( new Color( 200, 200, 200 ) );
        g.fill( innerBox );
    }

    public void update( Observable o, Object arg ) {
        int maxX = (int) Math.max( box.getMinX(), box.getMaxX() );
        int minX = (int) Math.min( box.getMinX(), box.getMaxX() );

        int minY = (int) Math.min( box.getMinY(), box.getMaxY() );
        int maxY = (int) Math.max( box.getMinY(), box.getMaxY() );
        innerBox.setRoundRect( minX, minY,
                               Math.abs( maxX - minX ),
                               maxY - minY,
                               10, 10 );
        outerBox.setRoundRect( minX - 10, minY - 8,
                               maxX - minX + 20,
                               Math.abs( maxY - minY ) + 16,
                               10, 10 );
    }
}
