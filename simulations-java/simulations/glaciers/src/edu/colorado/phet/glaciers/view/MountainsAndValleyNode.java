/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * MountainsNode is a background image of the mountains and valley.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MountainsAndValleyNode extends PImage {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public MountainsAndValleyNode( ModelViewTransform mvt ) {
        super( GlaciersImages.MOUNTAINS );
        setPickable( false );
        setChildrenPickable( false );
        
        Point2D offset = mvt.modelToView( -5054 /* m */, 6433 /* m */); //XXX dependent on image, determined via trial & error
        setOffset( offset.getX(), offset.getY() );
    }
}
