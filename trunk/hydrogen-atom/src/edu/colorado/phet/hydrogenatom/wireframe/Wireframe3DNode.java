/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.wireframe;

import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Wireframe3DNode is a Piccolo node that draws a Wireframe3D model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Wireframe3DNode extends PNode {

    private Wireframe3D _wireframe;
    private PPath _boundsNode;
    private Rectangle2D _boundsRect;
    
    public Wireframe3DNode( Wireframe3D wireframe ) {
        super();
        
        _wireframe = wireframe;
        
        //XXX HACK to get the bounds to refresh
        {
            _boundsNode = new PPath();
            _boundsNode.setStroke( null );
            _boundsNode.setPaint( new Color( 0, 0, 0, 0 ) );
            addChild( _boundsNode );
            _boundsRect = new Rectangle2D.Double();
            updateBounds();
        }
    }
    
    public void setWireframe( Wireframe3D wireframe ) {
        // Don't check for ==, Wireframe3D is mutable!
        _wireframe = wireframe;
        updateBounds();
        repaint();
    }
    
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        _wireframe.paint( paintContext.getGraphics() );
    }
    
    private void updateBounds() {
        double fudge = 10; //HACK bounds aren't quite right, causes visual artifacts
        double x = _wireframe.getXMin() - fudge;
        double y = _wireframe.getYMin() - fudge;
        double w = _wireframe.getXMax() - x + fudge;
        double h = _wireframe.getYMax() - y + fudge;
        _boundsRect.setRect( x, y, w, h );
        _boundsNode.setPathTo( _boundsRect );
    }
}
