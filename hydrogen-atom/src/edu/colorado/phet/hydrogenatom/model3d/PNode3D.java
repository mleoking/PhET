/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.model3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;


public class PNode3D extends PNode {

    private Wireframe3D _wireframe;
    private Stroke _stroke;
    
    public PNode3D( Wireframe3D wireframe, Stroke stroke ) {
        super();
        
        _wireframe = wireframe;
        _stroke = stroke;
        
        //HACK to get the bounds to refresh
        PPath boundsPath = new PPath();
        boundsPath.setPathTo( new Rectangle2D.Double( -500, -500, 1000, 1000 ) );//XXX size to wireframe!
        boundsPath.setStroke( null );
        boundsPath.setPaint( new Color( 0, 0, 0, 0 ) );
        addChild( boundsPath );
    }
    
    protected void paint( PPaintContext paintContext ) {
        super.paint( paintContext );
        
        Graphics2D g2 = paintContext.getGraphics();
        
        Object saveAntialiasValue = g2.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        Stroke saveStroke = g2.getStroke();
        
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setStroke( _stroke );
        
        _wireframe.paint( paintContext.getGraphics() );
        
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, saveAntialiasValue );
        g2.setStroke( saveStroke );
    }
}
