// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionmatcher.model;

import lombok.Data;

import java.awt.Color;

/**
 * Graphical representation for a fraction as a shape.  Used for generating levels.
 *
 * @author Sam Reid
 */
public @Data class GraphicalRepresentation {
    public final ShapeType shapeType;
    public final Color color;
    public final FillType fillType;
}