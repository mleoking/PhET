package edu.colorado.phet.nuclearphysics2.util;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.Arrow;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * This class creates a Piccolo PPath object that is a double-ended arrow, i.e.
 * it has arrowheads at both ends.
 *
 * @author John Blanco
 */
public class DoubleArrowNode extends PPath {

    /**
     * Constructor.
     *
     * @param tailLocation
     * @param tipLocation
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     */
    public DoubleArrowNode( Point2D tailLocation, Point2D tipLocation,
                      double headHeight, double headWidth, double tailWidth ) {
        super();
        
        // Find the midpoint between the two given points.
        Point2D midpoint = midPoint(tailLocation, tipLocation);
        
        // Create two single-ended arrows, one from the midpoint to the tail
        // and one from the midpoint to the tip.
        Arrow midToTip  = new Arrow( midpoint, tipLocation, headHeight, headWidth, tailWidth );
        Arrow midToTail = new Arrow( midpoint, tailLocation, headHeight, headWidth, tailWidth );
        GeneralPath overallShape = midToTip.getShape();
        overallShape.append( midToTail.getShape(), false );
        
        setPathTo( overallShape );
    }

    /**
     * Constructor.
     *
     * @param tailLocation
     * @param tipLocation
     * @param headHeight
     * @param headWidth
     * @param tailWidth
     * @param fractionalHeadHeight
     * @param scaleTailToo
     */
    public DoubleArrowNode( Point2D tailLocation, Point2D tipLocation,
                      double headHeight, double headWidth, double tailWidth,
                      double fractionalHeadHeight, boolean scaleTailToo ) {
        super();
        
        // Find the midpoint between the two given points.
        Point2D midpoint = midPoint(tailLocation, tipLocation);
        
        // Create two single-ended arrows, one from the midpoint to the tail
        // and one from the midpoint to the tip.
        Arrow midToTip  = new Arrow( midpoint, tipLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        Arrow midToTail = new Arrow( midpoint, tailLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        GeneralPath overallShape = midToTip.getShape();
        overallShape.append( midToTail.getShape(), false );
        
        setPathTo( overallShape );
    }
    
    private Point2D midPoint(Point2D point1, Point2D point2){ 
        
        return (new Point2D.Double( point1.getX() +( (point2.getX()-point1.getX())/2 ), point1.getY() +( (point2.getY()-point1.getY())/2 ) ));
    }

}
