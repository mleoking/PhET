// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

/**
 * ProvisionalBondPostReaction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProvisionalBondPostReaction extends ProvisionalBond {

    /**
     * @param sm1
     * @param sm2
     * @param maxBondLength
     * @param model
     * @param pe            Potential energy to be in the spring when its compressed
     */
    public ProvisionalBondPostReaction( SimpleMolecule sm1, SimpleMolecule sm2, double maxBondLength, MRModel model, double pe ) {
        super( sm1, sm2, maxBondLength, model, pe, true );
    }
}
