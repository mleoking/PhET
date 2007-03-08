/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.DynamicListenerController;
import edu.colorado.phet.common.util.DynamicListenerControllerFactory;

import java.util.Collection;

public class MoleculeSelectionTracker extends SimpleObservable {
    private final DynamicListenerController listenerController = DynamicListenerControllerFactory.newController( MoleculeSelectionListener.class );

    private MRModel mrModel;

    public MoleculeSelectionTracker( MRModule module ) {
        SelectedMoleculeListener moleculeListener = new SelectedMoleculeListener();

        mrModel = module.getMRModel();

        mrModel.addListener( moleculeListener );
        mrModel.addSelectedMoleculeTrackerListener( moleculeListener );
    }

    public SimpleMolecule getBoundMolecule() {
        SimpleMolecule boundMolecule = null;

        if( getSelectedMolecule() != null && getNearestToSelectedMolecule() != null ) {
            if( getSelectedMolecule().isPartOfComposite() ) {
                boundMolecule = getSelectedMolecule();
            }
            else if( getNearestToSelectedMolecule().isPartOfComposite() ) {
                boundMolecule = getNearestToSelectedMolecule();
            }
        }

        return boundMolecule;
    }

    public SimpleMolecule getFreeMolecule() {
        SimpleMolecule freeMolecule = null;

        if( getSelectedMolecule() != null && getNearestToSelectedMolecule() != null ) {
            if( getSelectedMolecule().isPartOfComposite() ) {
                if( !getNearestToSelectedMolecule().isPartOfComposite() ) {
                    freeMolecule = getNearestToSelectedMolecule();
                }
            }
            else if( getNearestToSelectedMolecule().isPartOfComposite() ) {
                if( !getSelectedMolecule().isPartOfComposite() ) {
                    freeMolecule = getSelectedMolecule();
                }
            }
        }

        return freeMolecule;
    }

    public SimpleMolecule getSelectedMolecule() {
        return mrModel.getMoleculeBeingTracked();
    }

    public SimpleMolecule getNearestToSelectedMolecule() {
        return mrModel.getNearestToMoleculeBeingTracked();
    }

    public boolean isTracking() {
        if (getFreeMolecule() != null && getBoundMolecule() != null) {
            if (getSelectedMolecule() != null && getNearestToSelectedMolecule() != null) {
                return true;
            }
        }

        return false;
    }

    private MoleculeSelectionListener getSelectionListenerController() {
        return (MoleculeSelectionListener)listenerController;
    }

    public void addSelectionStateListener( MoleculeSelectionListener listener ) {
        listenerController.addListener( listener );
    }

    public void removeSelectionStateListener( MoleculeSelectionListener listener ) {
        listenerController.removeListener( listener );
    }

    public Collection getAllSelectionStateListeners( MoleculeSelectionListener listener ) {
        return listenerController.getAllListeners();
    }

    // This class keeps track of the selected, and nearest to selected molecules.
    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            getSelectionListenerController().notifySelectionChanged( prevTrackedMolecule, newTrackedMolecule );

            getController().update();
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            getSelectionListenerController().notifyNearestToSelectionChanged( prevClosestMolecule, newClosestMolecule );

            getController().update();
        }

        public void notifyEnergyProfileChanged( EnergyProfile newProfile ) {
            getSelectionListenerController().notifyEnergyProfileChanged( newProfile );

            getController().update();
        }
    }

    public interface MoleculeSelectionListener {
        void notifySelectionChanged( SimpleMolecule oldSelection, SimpleMolecule newSelection );

        void notifyNearestToSelectionChanged( SimpleMolecule oldNearest, SimpleMolecule newNearest );

        void notifyEnergyProfileChanged( EnergyProfile newProfile );
    }
}
