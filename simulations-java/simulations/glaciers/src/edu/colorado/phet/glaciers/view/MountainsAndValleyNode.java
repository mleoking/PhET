/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

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
    
    public MountainsAndValleyNode() {
        super( GlaciersImages.MOUNTAINS );
        setPickable( false );
        setChildrenPickable( false );
    }
}
