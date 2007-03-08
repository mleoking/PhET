/* Copyright 2007, University of Colorado */
package edu.colorado.phet.molecularreactions.view.energy;

import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;
import edu.colorado.phet.molecularreactions.model.EnergyProfile;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.modules.MRModule;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.util.DynamicListenerController;
import edu.colorado.phet.common.util.DynamicListenerControllerFactory;

import java.util.Collection;

public class MoleculeSelectionTracker extends SimpleObservable {
    private final DynamicListenerController listenerController = DynamicListenerControllerFactory.newController( MoleculeSelectionListener.class );

    private volatile SimpleMolecule boundMolecule;
    private volatile SimpleMolecule freeMolecule;

    private volatile SimpleMolecule selectedMolecule;
    private volatile SimpleMolecule nearestToSelectedMolecule;

    public MoleculeSelectionTracker( MRModule module ) {
        SelectedMoleculeListener moleculeListener = new SelectedMoleculeListener();

        MRModel model = module.getMRModel();

        model.addListener( moleculeListener );
        model.addSelectedMoleculeTrackerListener( moleculeListener );

        addObserver( new FreeBoundTrackingObserver() );
    }

    public SimpleMolecule getBoundMolecule() {
        return boundMolecule;
    }

    public SimpleMolecule getFreeMolecule() {
        return freeMolecule;
    }

    public SimpleMolecule getSelectedMolecule() {
        return selectedMolecule;
    }

    public SimpleMolecule getNearestToSelectedMolecule() {
        return nearestToSelectedMolecule;
    }

    public void reset() {
        setFreeAndBoundMolecules( null, null );
        setSelectedMolecule( null );
        setNearestToSelectedMolecule( null );
    }

    public boolean isMoleculeSelected() {
        return selectedMolecule != null;
    }

    public boolean isTracking() {
        if (freeMolecule != null && boundMolecule != null) {
            if (selectedMolecule != null && nearestToSelectedMolecule != null) {
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

    private void setFreeAndBoundMolecules( SimpleMolecule newFreeMolecule, SimpleMolecule newBoundMolecule ) {
        if( newFreeMolecule != freeMolecule ) {
            SimpleMolecule oldFreeMolecule = freeMolecule;

            freeMolecule = newFreeMolecule;

            getSelectionListenerController().notifyFreeMoleculeChanged( oldFreeMolecule, freeMolecule );
        }
        if( newBoundMolecule != boundMolecule ) {
            SimpleMolecule oldBoundMolecule = boundMolecule;

            boundMolecule = newBoundMolecule;

            getSelectionListenerController().notifyBoundMoleculeChanged( oldBoundMolecule, boundMolecule );
        }
    }

    private void setNearestToSelectedMolecule( SimpleMolecule newMolecule ) {
        if( nearestToSelectedMolecule != newMolecule ) {
            if( nearestToSelectedMolecule != null ) {
                nearestToSelectedMolecule.removeObserver( getController() );
            }

            nearestToSelectedMolecule = newMolecule;

            if( newMolecule != null ) {
                newMolecule.addObserver( getController() );
            }

            getSelectionListenerController().notifyNearestToSelectionChanged( nearestToSelectedMolecule, newMolecule );
        }
    }

    private void setSelectedMolecule( SimpleMolecule newMolecule ) {
        if( selectedMolecule != newMolecule ) {
            if( selectedMolecule != null ) {
                selectedMolecule.removeObserver( getController() );
            }

            selectedMolecule = newMolecule;

            if( newMolecule != null ) {
                newMolecule.addObserver( getController() );
            }

            getSelectionListenerController().notifySelectionChanged( selectedMolecule, newMolecule );
        }
    }

    // This class keeps track of the free and bound molecule.
    private class FreeBoundTrackingObserver implements SimpleObserver {
        public void update() {
            SimpleMolecule newBoundMolecule = null;
            SimpleMolecule newFreeMolecule = null;

            if( getSelectedMolecule() != null && getNearestToSelectedMolecule() != null ) {
                if( getSelectedMolecule().isPartOfComposite() ) {
                    newBoundMolecule = getSelectedMolecule();
                    if( !getNearestToSelectedMolecule().isPartOfComposite() ) {
                        newFreeMolecule = getNearestToSelectedMolecule();
                    }
                }
                else if( getNearestToSelectedMolecule().isPartOfComposite() ) {
                    newBoundMolecule = getNearestToSelectedMolecule();

                    if( !getSelectedMolecule().isPartOfComposite() ) {
                        newFreeMolecule = getSelectedMolecule();
                    }
                }
            }

            setFreeAndBoundMolecules( newFreeMolecule, newBoundMolecule );
        }
    }

    // This class keeps track of the selected, and nearest to selected molecules.
    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            setSelectedMolecule( newTrackedMolecule );

            getController().update();
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            setNearestToSelectedMolecule( newClosestMolecule );

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

        void notifyFreeMoleculeChanged( SimpleMolecule oldFreeMolecule, SimpleMolecule newFreeMolecule );

        void notifyBoundMoleculeChanged( SimpleMolecule oldBoundMolecule, SimpleMolecule newBoundMolecule );

        void notifyEnergyProfileChanged( EnergyProfile newProfile );
    }
}
