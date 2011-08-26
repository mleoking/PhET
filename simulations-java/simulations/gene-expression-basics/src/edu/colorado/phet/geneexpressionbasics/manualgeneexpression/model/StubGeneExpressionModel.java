// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

/**
 * Gene expression model that doesn't do anything.  This is needed in cases
 * where we want to create biomolecules without needing a full blown model, such
 * as on control panels.
 *
 * @author John Blanco
 */
public class StubGeneExpressionModel extends GeneExpressionModel {
    @Override public DnaMolecule getDnaMolecule() {
        return null;
    }
}
