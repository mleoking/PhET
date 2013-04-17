// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildamolecule;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

public class BuildAMoleculeSimSharing {
    public enum UserComponent implements IUserComponent {
        atom,
        breakApartButton,
        scissorsButton,
        jmol3DButton,
        makeMoleculesTab,
        collectMultipleTab,
        largerMoleculesTab,
        resetCollection,
        soundOn,
        soundOff,
        refillKit,
        resetCurrentTab
    }

    public enum ParameterKey implements IParameterKey {
        atomId, atomElement, atomIds,

        atomWasInKitArea,
        atomDroppedInKitArea,
        atomDroppedOverCollectionBox,
        atomSuccessfullyDroppedInCollectionBox,

        collectionBoxFormulasUnderDroppedMolecule,
        collectionBoxFormulaDroppedInto,
        collectionBoxDropFailure,

        bonds,
        bondOccurs,
        bondAtomA,
        bondAtomB,
        bondMoleculeIdA,
        bondMoleculeIdB,
        bondDirection,

        bondMoleculeDestroyedA,
        bondMoleculeDestroyedB,
        bondMoleculeCreated,

        moleculesCreated,
        moleculeDestroyed,
        moleculeStructureDestroyedA,
        moleculeStructureDestroyedB,
        moleculeStructureCreated,
        moleculeId,
        moleculeRepulsed, // boolean
        moleculesRepulsed, // list of molecules if applicable
        moleculeSerial2,
        moleculeGeneralFormula,
        moleculeIsCompleteMolecule,

        completeMoleculeMolecularFormula,
        completeMoleculeCommonName,
        completeMoleculeCID,
        completeMoleculeSerial2,
        collectionBoxQuantity,
        collectionBoxCapacity,

        kitIndex
    }

    public enum ModelComponent implements IModelComponent {
        atom,
        molecule,
        collectionBox,
        kit
    }

    public enum ModelAction implements IModelAction {
        atomAddedIntoPlay,

        kitChanged,

        moleculeStatusAfterDrop,

        atomDropped,
        collectionDropInformation,
        bondAttempt,
        bonding,
        bondBroken, // just one bond broken, probably with scissors
        moleculeBroken, // all bonds at once
        moleculeRecycled,
        moleculeRemovedMisc,

        moleculePutInCollectionBox,
        collectionBoxFilled,

        // lower-level events
        moleculeAdded,
        moleculeRemoved
    }

}
