// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.dilutions.view;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.colorado.phet.common.piccolophet.PhetPNode;

/**
 * Provides the ability to name "points of interest" in the node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NamedPointsNode extends PhetPNode {

    private final HashMap<String, Point2D> offsets;

    public NamedPointsNode() {
        offsets = new HashMap<String, Point2D>();
    }

    // Adds a named point in the untransformed coordinate frame of the image.
    public void addOffset( String name, Point2D point ) {
        offsets.put( name, point );
    }

    // Gets a named point in the transformed coordinate frame of the image.
    public Point2D getOffset( String name ) {
        return getTransform().transform( offsets.get( name ), null );
    }
}
