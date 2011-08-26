// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;

/**
 * This class is the model representation of a gene on a DNA molecule.  Each
 * gene consists of a regulatory region and a transcribed region.  In real life,
 * a gene is just a collection of base pairs on the DNA strand.  This class
 * essentially says where on the strand the gene exists.
 *
 * @author John Blanco
 * @author Sam Reid
 */
public class Gene {

    private static final double REG_REGION_PROPORTION = 0.25;

    private final DnaMolecule dnaMolecule;
    private final Color regulatoryRegionColor;
    private final Color transcribedRegionColor;
    private final IntegerRange regulatoryRegion;
    private final IntegerRange transcribedRegion;

    /**
     * Constructor.
     *
     * @param dnaMolecule            The DNA molecule within which this gene
     *                               exists.
     * @param regulatoryRegion       The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param regulatoryRegionColor
     * @param transcribedRegion      The range, in terms of base pairs on the
     *                               DNA strand, where this region exists.
     * @param transcribedRegionColor
     */
    public Gene( DnaMolecule dnaMolecule, IntegerRange regulatoryRegion, Color regulatoryRegionColor,
                 IntegerRange transcribedRegion, Color transcribedRegionColor ) {
        this.dnaMolecule = dnaMolecule;
        this.regulatoryRegion = regulatoryRegion;
        this.regulatoryRegionColor = regulatoryRegionColor;
        this.transcribedRegion = transcribedRegion;
        this.transcribedRegionColor = transcribedRegionColor;
    }

    public Color getRegulatoryRegionColor() {
        return regulatoryRegionColor;
    }

    public Color getTranscribedRegionColor() {
        return transcribedRegionColor;
    }

    public double getCenterX() {
        double startX = dnaMolecule.getBasePairXOffsetByIndex( regulatoryRegion.getMin() );
        double endX = dnaMolecule.getBasePairXOffsetByIndex( transcribedRegion.getMax() );
        return startX + ( endX - startX ) / 2;
    }

    public IntegerRange getRegulatoryRegion() {
        return regulatoryRegion;
    }

    public IntegerRange getTranscribedRegion() {
        return transcribedRegion;
    }
}
