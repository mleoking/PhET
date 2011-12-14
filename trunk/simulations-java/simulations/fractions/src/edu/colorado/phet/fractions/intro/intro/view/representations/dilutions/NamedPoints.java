// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.intro.view.representations.dilutions;

import java.awt.geom.Point2D;
import java.util.HashMap;

import edu.umd.cs.piccolo.PNode;

/**
 * A map used to identify "points of interest" in a node.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class NamedPoints extends HashMap<String, Point2D> {

    private final PNode node;

    public NamedPoints( PNode node ) {
        this.node = node;
    }

    // Adds a named point in the untransformed coordinate frame of the image.
    public void addOffset( String name, Point2D point ) {
        put( name, point );
    }

    // Gets a named point in the transformed coordinate frame of the image.
    public Point2D getOffset( String name ) {
        return node.getTransform().transform( get( name ), null );
    }
}
