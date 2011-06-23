// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * @author Sam Reid
 */
public class Gene {
    private Rectangle2D.Double rect;
    private Paint color;

    public Gene( Rectangle2D.Double rect, Paint color ) {
        this.rect = rect;
        this.color = color;
    }

    public Rectangle2D.Double getRect() {
        return rect;
    }

    public Paint getColor() {
        return color;
    }
}
