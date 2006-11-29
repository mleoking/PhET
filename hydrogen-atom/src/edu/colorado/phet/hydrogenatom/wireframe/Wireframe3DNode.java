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
    
    public Wireframe3DNode( Wireframe3D wireframe ) {
        super();
        
        _wireframe = wireframe;
        
        //XXX HACK to get the bounds to refresh
        {
            PPath boundsPath = new PPath();
            double x = _wireframe.getXMin();
            double y = _wireframe.getYMin();
            double w = _wireframe.getXMax() - x;
            double h = _wireframe.getYMax() - y;
            boundsPath.setPathTo( new Rectangle2D.Double( x, y, w, h ) );
            boundsPath.setStroke( null );
            boundsPath.setPaint( new Color( 0, 0, 0, 0 ) );
            addChild( boundsPath );
        }
    }
    
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        _wireframe.paint( paintContext.getGraphics() );
    }
}
