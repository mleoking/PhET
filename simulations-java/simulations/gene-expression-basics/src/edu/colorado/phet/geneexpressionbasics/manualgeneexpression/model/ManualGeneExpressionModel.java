// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

import static edu.colorado.phet.common.phetcommon.math.MathUtil.clamp;

/**
 * Primary model for the manual gene expression tab.
 * <p/>
 * Dimensions in this model (and all sub-elements of the model) are in nano-
 * meters, i.e. 10E-9 meters.
 * <p/>
 * The point (0,0) in model space is at the leftmost edge of the DNA strand,
 * and at the vertical center of the strand.
 *
 * @author John Blanco
 */
public class ManualGeneExpressionModel {

    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------

    // Clock that drives all time-dependent behavior in this model.
    private final ConstantDtClock clock = new ConstantDtClock( 30.0 );

    // DNA strand, which is where the genes reside, where the polymerase does
    // its transcription, and where a lot of the action takes place.
    private final DnaMolecule dnaStrand = new DnaMolecule();

    // The gene that the user is focusing on, other gene activity is
    // suspended.  Start with the 0th gene in the DNA (leftmost).
    public final Property<Gene> activeGene = new Property<Gene>( dnaStrand.getGenes().get( 0 ) );

    // Properties that keep track of whether the first or last gene is
    // currently active, which means that the user is viewing it.
    public final ObservableProperty<Boolean> isFirstGeneActive = activeGene.valueEquals( dnaStrand.getGenes().get( 0 ) );
    public final ObservableProperty<Boolean> isLastGeneActive = activeGene.valueEquals( dnaStrand.getLastGene() );

    // List of RNA polymerase in the model.
    public final ObservableList<MobileBiomolecule> mobileBiomoleculeList = new ObservableList<MobileBiomolecule>();

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------

    //------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------

    public ConstantDtClock getClock() {
        return clock;
    }

    public DnaMolecule getDnaMolecule() {
        return dnaStrand;
    }

    public void previousGene() {
        switchToGeneRelative( -1 );
    }

    public void nextGene() {
        switchToGeneRelative( +1 );
    }

    private void switchToGeneRelative( int i ) {
        final ArrayList<Gene> genes = dnaStrand.getGenes();
        int index = clamp( 0, genes.indexOf( activeGene.get() ) + i, genes.size() - 1 );
        activeGene.set( genes.get( index ) );
    }

    public List<MobileBiomolecule> getMobileBiomoleculeList() {
        return mobileBiomoleculeList;
    }

    public void addMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.add( mobileBiomolecule );
    }

    public void removeMobileBiomolecule( MobileBiomolecule mobileBiomolecule ) {
        mobileBiomoleculeList.remove( mobileBiomolecule );
    }
}
