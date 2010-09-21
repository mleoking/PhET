/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.view.tools;

import java.awt.Image;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.TracerFlag;
import edu.colorado.phet.glaciers.view.ModelViewTransform;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * TracerFlagNode is the visual representation of a tracer flag.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TracerFlagNode extends AbstractToolNode {

    private TracerFlag _tracerFlag;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public TracerFlagNode( TracerFlag tracerFlag, ModelViewTransform mvt, TrashCanDelegate trashCan ) {
        super( tracerFlag, mvt, trashCan );
        _tracerFlag = tracerFlag;
        PImage imageNode = new PImage( GlaciersImages.TRACER_FLAG );
        addChild( imageNode );
        imageNode.setOffset( 0, -imageNode.getFullBoundsReference().getHeight() ); // lower left corner
    }
    
    //----------------------------------------------------------------------------
    // AbstractToolNode overrides
    //----------------------------------------------------------------------------
    
    protected void startDrag() {
        _tracerFlag.startDrag();
        _tracerFlag.setOrientation( 0 );
    }
    
    protected void updateOrientation() {
        setRotation( _tracerFlag.getOrientation() );
    }
    
    //----------------------------------------------------------------------------
    // Utilities
    //----------------------------------------------------------------------------
    
    public static Image createImage() {
        return GlaciersImages.TRACER_FLAG;
    }
}
