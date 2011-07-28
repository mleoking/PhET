// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Paint;
import java.awt.geom.Rectangle2D;

/**
 * This class is the model representation of a gene on a DNA molecule.  Each
 * gene consists of a regulatory region and a transcribed region.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class Gene {
    private static final double REG_REGION_PROPORTION = 0.25;
    private Rectangle2D.Double rect;
    private Paint color;

    public Gene( Rectangle2D.Double rect, Paint color ) {
        this.rect = rect;
        this.color = color;
    }

    public Rectangle2D.Double getTranscribedRegionRect() {
        double width = ( 1 - REG_REGION_PROPORTION ) * rect.getWidth();
        return new Rectangle2D.Double( rect.getMaxX() - width, rect.getY(), width, rect.getHeight() );
    }

    public Rectangle2D.Double getRegulatoryRegionRect() {
        double width = REG_REGION_PROPORTION * rect.getWidth();
        return new Rectangle2D.Double( rect.getX(), rect.getY(), width, rect.getHeight() );
    }

    public Paint getColor() {
        return color;
    }
}
