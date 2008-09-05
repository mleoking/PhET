/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.DoubleArrowNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This node represents a double-headed arrow that can be grabbed by the user
 * and moved in order to resize something in the view.
 *
 * @author John Blanco
 */
public class ResizeArrowNode extends PNode{
    
    private static final Color NON_HIGHLIGHTED_FILL_COLOR = Color.ORANGE; 
    private static final Color HIGHLIGHTED_FILL_COLOR = Color.YELLOW; 

    DoubleArrowNode m_adjusterArrow;
    
    /**
     * This constructor allows the user to specify the size and orientation
     * of the resizing arrow.  The arrow that is created will be centered
     * around 0,0 so that the user can position it without having to worry
     * about offsetting it.
     * 
     * @param size - In terms of canvas coordinates. 
     * @param angle - In radians.  A value of 0 means a horizontal arrow.
     */
    public ResizeArrowNode( double size, double angle ){
        
        m_adjusterArrow = new DoubleArrowNode(new Point2D.Double(-size/2, 0), new Point2D.Double(size/2, 0), size/4,
                size/2, size/4);
        m_adjusterArrow.rotate( angle );
        m_adjusterArrow.setPaint( NON_HIGHLIGHTED_FILL_COLOR );
        addChild( m_adjusterArrow );
        
        m_adjusterArrow.setPickable( true );
        
        m_adjusterArrow.addInputEventListener( new CursorHandler() );
    }
}
