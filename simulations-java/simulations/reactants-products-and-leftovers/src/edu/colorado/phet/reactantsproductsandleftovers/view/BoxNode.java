package edu.colorado.phet.reactantsproductsandleftovers.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.reactantsproductsandleftovers.RPALConstants;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Box that shows the substances involved at some stage of a reaction.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BoxNode extends PPath {
    
    private static final Stroke STROKE = new BasicStroke( 1f );
    private static final Color STROKE_PAINT = Color.BLACK;
    private static final Color FILL_PAINT = RPALConstants.BEFORE_AFTER_BOX_COLOR;
    
    public BoxNode( PDimension size ) {
        super();
        setPathTo( new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() ));
        setPaint( FILL_PAINT );
        setStrokePaint( STROKE_PAINT );
        setStroke( STROKE );
    }
    
    public Point2D getRandomPoint( double xMargin, double yMargin ) {
        double xRange = getFullBoundsReference().getWidth() - ( 2 * xMargin );
        double yRange = getFullBoundsReference().getHeight() - ( 2 * yMargin );
        double x = xMargin + ( Math.random() * xRange );
        double y = yMargin + ( Math.random() * yRange );
        return new Point2D.Double( x, y );
    }
}
