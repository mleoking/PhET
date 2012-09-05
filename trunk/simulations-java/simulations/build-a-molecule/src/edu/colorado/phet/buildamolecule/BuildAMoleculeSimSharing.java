// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

public class BuildAMoleculeSimSharing {
    public enum UserComponent implements IUserComponent {
        atom
    }

    public enum ParameterKey implements IParameterKey {
        atomId, atomElement,

        atomWasInKitArea,
        atomDroppedInKitArea,
        atomDroppedOverCollectionBox,
        atomSuccessfullyDroppedInCollectionBox,

        bondOccurs,
        bondAtomA,
        bondAtomB,
        bondDirection,

        bondMoleculeDestroyedA,
        bondMoleculeDestroyedB,
        bondMoleculeCreated,

        moleculeStructureDestroyedA,
        moleculeStructureDestroyedB,
        moleculeStructureCreated,
        moleculeId,

        collectionBoxMolecularFormula,
        collectionBoxCommonName,
        collectionBoxCID,
        collectionBoxQuantity,
        collectionBoxCapacity

    }

    public enum ModelComponent implements IModelComponent {
        atom,
        molecule,
        collectionBox
    }

    public enum ModelAction implements IModelAction {
        atomDropped,
        collectionDropInformation,
        bondAttempt,
        bonding,

        moleculePutInCollectionBox,
        collectionBoxFilled
    }

}
