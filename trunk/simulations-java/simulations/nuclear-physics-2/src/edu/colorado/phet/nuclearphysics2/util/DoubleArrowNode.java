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

    //------------------------------------------------------------------------
    // Class data
    //------------------------------------------------------------------------
    
    // The points that describe the location of the tip and tail of the arrow.
    Point2D m_tipLocation;
    Point2D m_tailLocation;
    
    // The two "sub-arrows" that comprise this arrow node.
    Arrow m_midToTip;
    Arrow m_midToTail;
    
    // Overall shape of the two combined arrows that comprise this double-ended
    // arrow.
    GeneralPath m_overallShape;
    
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
        m_midToTip  = new Arrow( midpoint, tipLocation, headHeight, headWidth, tailWidth );
        m_midToTail = new Arrow( midpoint, tailLocation, headHeight, headWidth, tailWidth );
        m_overallShape = m_midToTip.getShape();
        m_overallShape.append( m_midToTail.getShape(), false );       
        setPathTo( m_overallShape );
        
        // Save the tip and tail points for later use.
        m_tailLocation = tailLocation;
        m_tipLocation = tipLocation;
    }
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

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
        m_midToTip  = new Arrow( midpoint, tipLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        m_midToTail = new Arrow( midpoint, tailLocation, headHeight, headWidth, tailWidth, fractionalHeadHeight, scaleTailToo );
        m_overallShape = m_midToTip.getShape();
        m_overallShape.append( m_midToTail.getShape(), false );       
        setPathTo( m_overallShape );        
    }

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    /**
     * Method to set a new tip location, effectively moving the arrow as a
     * result.
     */
    public void setTipLocation(Point2D newTipLocation){
        m_tipLocation = newTipLocation;
        
        Point2D midpoint = midPoint(m_tailLocation, m_tipLocation);
        
        m_midToTip.setTipAndTailLocations( m_tipLocation, midpoint );
        m_midToTail.setTipAndTailLocations( m_tailLocation, midpoint );
     
        m_overallShape = m_midToTip.getShape();
        m_overallShape.append( m_midToTail.getShape(), false );       
        setPathTo( m_overallShape );
    }

    /**
     * Method to set a new tail location, effectively moving the arrow as a
     * result.
     */
    public void setTailLocation(Point2D newTailLocation){
        m_tailLocation = newTailLocation;
        
        Point2D midpoint = midPoint(m_tailLocation, m_tipLocation);
        
        m_midToTip.setTipAndTailLocations( m_tipLocation, midpoint );
        m_midToTail.setTipAndTailLocations( m_tailLocation, midpoint );
     
        m_overallShape = m_midToTip.getShape();
        m_overallShape.append( m_midToTail.getShape(), false );       
        setPathTo( m_overallShape );
    }

    /**
     * Method to set new locations for both the tail and the tip of the arrow.
     * This method can be used to translate or rotate the arrow.
     */
    public void setTipAndTailLocations(Point2D newTipLocation, Point2D newTailLocation){
        m_tailLocation = newTailLocation;
        m_tipLocation = newTipLocation;
        
        Point2D midpoint = midPoint(m_tailLocation, m_tipLocation);
        
        m_midToTip.setTipAndTailLocations( m_tipLocation, midpoint );
        m_midToTail.setTipAndTailLocations( m_tailLocation, midpoint );
     
        m_overallShape = m_midToTip.getShape();
        m_overallShape.append( m_midToTail.getShape(), false );       
        setPathTo( m_overallShape );
    }
    
    public double getHeadHeight()
    {
        return m_midToTail.getHeadHeight();
    }

    public double getHeadWidth()
    {
        return m_midToTail.getHeadWidth();
    }
    
    public double getTailWidth()
    {
        return m_midToTail.getTailWidth();
    }
    
    private Point2D midPoint(Point2D point1, Point2D point2){ 
        
        return (new Point2D.Double( point1.getX() +( (point2.getX()-point1.getX())/2 ), point1.getY() +( (point2.getY()-point1.getY())/2 ) ));
    }
    
}
