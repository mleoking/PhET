// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.geneexpressionbasics.common.model.attachmentstatemachines;

import edu.colorado.phet.geneexpressionbasics.common.model.MobileBiomolecule;

/**
 * Generic attachment state machine - just implements basic behavior.
 * <p/>
 * This class exists mainly for testing and for quick implementation of
 * biomolecules.  The code analyzer may show that it is unused, but it should
 * be kept around anyway for testing and prototyping of changes.
 *
 * @author John Blanco
 */
public class GenericAttachmentStateMachine extends AttachmentStateMachine {

    public GenericAttachmentStateMachine( MobileBiomolecule biomolecule ) {
        super( biomolecule );
    }
}
