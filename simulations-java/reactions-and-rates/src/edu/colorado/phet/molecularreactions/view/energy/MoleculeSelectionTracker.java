/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.view.EnergyMoleculeGraphic;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.util.SimpleObservable;

public class MoleculeSelectionTracker extends SimpleObservable {

    private final MoleculeSeparationPane.MolecularPaneState molecularPaneState;
    private final MRModule module;


    public MoleculeSelectionTracker( MoleculeSeparationPane.MolecularPaneState molecularPaneState, MRModule module ) {
        this.molecularPaneState = molecularPaneState;
        this.module = module;

        MRModel model = module.getMRModel();

        SelectedMoleculeListener moleculeListener = new SelectedMoleculeListener();

        model.addListener( moleculeListener );
        model.addSelectedMoleculeTrackerListener(
                moleculeListener
        );
    }

    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {

        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( molecularPaneState.selectedMolecule != null ) {
                molecularPaneState.selectedMolecule.removeObserver( getController() );
            }

            molecularPaneState.selectedMolecule = newTrackedMolecule;
            if( molecularPaneState.selectedMoleculeGraphic != null
                && molecularPaneState.moleculeLayer.getChildrenReference().contains( molecularPaneState.selectedMoleculeGraphic ) ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.selectedMoleculeGraphic );
            }

            if( newTrackedMolecule != null ) {
                molecularPaneState.selectedMoleculeGraphic = new EnergyMoleculeGraphic( newTrackedMolecule.getFullMolecule(),
                                                                     module.getMRModel().getEnergyProfile() );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.selectedMoleculeGraphic );
                newTrackedMolecule.addObserver( getController() );
                molecularPaneState.moleculePaneAxisNode.setVisible( true );
            }
            else {
                molecularPaneState.moleculePaneAxisNode.setVisible( false );

            }
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule,
                                            SimpleMolecule prevClosestMolecule ) {
            if( molecularPaneState.nearestToSelectedMolecule != null ) {
                molecularPaneState.nearestToSelectedMolecule.removeObserver( getController() );
            }

            molecularPaneState.nearestToSelectedMolecule = newClosestMolecule;
            if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
            }
            molecularPaneState.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( newClosestMolecule.getFullMolecule(),
                                                                          module.getMRModel().getEnergyProfile() );
            molecularPaneState.moleculeLayer.addChild( molecularPaneState.nearestToSelectedMoleculeGraphic );

            newClosestMolecule.addObserver( getController() );

            getController().update();
        }


        public void notifyEnergyProfileChanged( EnergyProfile profile ) {
            if( molecularPaneState.selectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.selectedMoleculeGraphic );
                molecularPaneState.selectedMoleculeGraphic = new EnergyMoleculeGraphic( molecularPaneState.selectedMolecule.getFullMolecule(),
                                                                     profile );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.selectedMoleculeGraphic );
            }
            if( molecularPaneState.nearestToSelectedMoleculeGraphic != null ) {
                molecularPaneState.moleculeLayer.removeChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
                molecularPaneState.nearestToSelectedMoleculeGraphic = new EnergyMoleculeGraphic( molecularPaneState.nearestToSelectedMolecule.getFullMolecule(),
                                                                              profile );
                molecularPaneState.moleculeLayer.addChild( molecularPaneState.nearestToSelectedMoleculeGraphic );
            }
        }
    }
}
