// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reactionsandrates.view.energy;

import edu.colorado.phet.common.phetcommon.util.DynamicListenerController;
import edu.colorado.phet.common.phetcommon.util.DynamicListenerControllerFactory;
import edu.colorado.phet.reactionsandrates.model.EnergyProfile;
import edu.colorado.phet.reactionsandrates.model.MRModel;
import edu.colorado.phet.reactionsandrates.model.SelectedMoleculeTracker;
import edu.colorado.phet.reactionsandrates.model.SimpleMolecule;
import edu.colorado.phet.reactionsandrates.modules.MRModule;

import java.util.Collection;

public class MoleculeSelectionTracker {
    private final DynamicListenerController listenerController = DynamicListenerControllerFactory.newController( MoleculeSelectionListener.class );

    private MRModel mrModel;
    private SelectedMoleculeListener moleculeListener;

    public MoleculeSelectionTracker( MRModule module ) {
        moleculeListener = new SelectedMoleculeListener();

        mrModel = module.getMRModel();

        mrModel.addListener( moleculeListener );
        mrModel.addSelectedMoleculeTrackerListener( moleculeListener );
    }

    public void terminate() {
        mrModel.removeListener( moleculeListener );
        mrModel.removeSelectedMoleculeTrackerListener( moleculeListener );
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
        if( getFreeMolecule() != null && getBoundMolecule() != null ) {
            if( getSelectedMolecule() != null && getNearestToSelectedMolecule() != null ) {
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

    public Collection getAllSelectionStateListeners() {
        return listenerController.getAllListeners();
    }

    public void reset() {
    }

    // This class keeps track of the selected, and nearest to selected molecules.
    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            getSelectionListenerController().notifySelectedChanged( prevTrackedMolecule, newTrackedMolecule );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            getSelectionListenerController().notifyClosestChanged( prevClosestMolecule, newClosestMolecule );
        }

        public void notifyEnergyProfileChanged( EnergyProfile newProfile ) {
            getSelectionListenerController().notifyEnergyProfileChanged( newProfile );
        }
    }

    public interface MoleculeSelectionListener {
        void notifySelectedChanged( SimpleMolecule oldSelection, SimpleMolecule newSelection );

        void notifyClosestChanged( SimpleMolecule oldNearest, SimpleMolecule newNearest );

        void notifyEnergyProfileChanged( EnergyProfile newProfile );
    }
}
