/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.view;

import java.awt.Graphics2D;

import org.jfree.ui.Drawable;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPaintContext;


/**
 * DrawableNode is a Piccolo node that draws any org.jfree.ui.Drawable 
 * (including, JFreeChart).
 * <p>
 * Sample usage:
 * <br>
 * <code>
 *     JFreeChart chart = new JFreeChart(...);
 *     DrawableNode node = new DrawableNode( chart );
 *     node.setBounds( 0, 0, 500, 250 ); // determines the size of the chart
 *     PCanvas canvas = new PCanvas();
 *     canvas.getLayer().addChild( node );
 * </code>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DrawableNode extends PNode {

    private Drawable _drawable;
    
    /**
     * Sole constructor.
     * The drawable provided to the constructor will be used as 
     * the graphical representation of the node.
     * 
     * @param drawable the drawable
     */
    public DrawableNode( Drawable drawable ) {
        super();
        _drawable = drawable;
    }
    
    /**
     * Gets the drawable.
     * 
     * @return the drawable
     */
    public Drawable getDrawable() {
        return _drawable;
    }
    
    /*
     * Renders the drawable, using this node's reference bounds to
     * determine the rendering size.
     * 
     * @see edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
     */
    protected void paint( PPaintContext paintContext ) {
        Graphics2D g2 = paintContext.getGraphics();
        _drawable.draw( g2, getBoundsReference() );
    }
}
