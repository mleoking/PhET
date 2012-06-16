package edu.colorado.phet.fractionsintro.matchinggame.model;

import lombok.Data;

import java.awt.Color;

/**
 * Graphical representation for a fraction as a shape.  Used for generating levels.
 *
 * @author Sam Reid
 */
@Data class GraphicalRepresentation {
    public final ShapeType shapeType;
    public final Color color;
    public final FillType fillType;
}