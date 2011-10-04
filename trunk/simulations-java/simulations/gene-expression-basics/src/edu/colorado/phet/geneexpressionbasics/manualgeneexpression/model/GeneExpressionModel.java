// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.manualgeneexpression.model;

import java.util.List;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Base class for the models used in this simulation.  All models must extend
 * from a common class so that the sam biomolecules can be used within each.
 *
 * @author John Blanco
 */
public abstract class GeneExpressionModel {

    /**
     * Get the DNA molecule.
     *
     * @return - DNA molecule, null if none exists.
     */
    public abstract DnaMolecule getDnaMolecule();

    /**
     * Add a mobile biomolecule to the model.  The model must send out the
     * appropriate notifications.
     *
     * @param mobileBiomolecule
     */
    public abstract void addMobileBiomolecule( final MobileBiomolecule mobileBiomolecule );

    /**
     * Add the specified messenger RNA strand to the model.  The model must
     * send out the appropriate notifications.
     *
     * @param messengerRna
     */
    public abstract void addMessengerRna( final MessengerRna messengerRna );

    /**
     * Get a list of all messenger RNA strands that are currently in
     * existence.
     *
     * @return
     */
    public abstract List<MessengerRna> getMessengerRnaList();
}

