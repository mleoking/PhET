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

public class MoleculeSelectionState extends SimpleObservable {
    private final DynamicListenerController listenerController = DynamicListenerControllerFactory.newController( MoleculeSelectionStateListener.class );

    private volatile SimpleMolecule boundMolecule;
    private volatile SimpleMolecule freeMolecule;

    private volatile SimpleMolecule selectedMolecule;
    private volatile SimpleMolecule nearestToSelectedMolecule;

    public MoleculeSelectionState( MRModule module ) {
        MRModel model = module.getMRModel();

        SelectedMoleculeListener moleculeListener = new SelectedMoleculeListener();

        model.addListener( moleculeListener );
        model.addSelectedMoleculeTrackerListener(
                moleculeListener
        );

        addObserver(new FreeBoundTrackingObserver() );
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
        selectedMolecule = null;
        nearestToSelectedMolecule = null;
    }

    public boolean isMoleculeSelected() {
        return selectedMolecule != null;
    }

    private MoleculeSelectionStateListener getSelectionListenerController() {
        return (MoleculeSelectionStateListener)listenerController;
    }

    public void addSelectionStateListener(MoleculeSelectionStateListener listener) {
        listenerController.addListener( listener );
    }

    public void removeSelectionStateListener(MoleculeSelectionStateListener listener) {
        listenerController.removeListener( listener );
    }

    public Collection getAllSelectionStateListeners(MoleculeSelectionStateListener listener) {
        return listenerController.getAllListeners();
    }

    // This class keeps track of the free and bound molecule.
    private class FreeBoundTrackingObserver implements SimpleObserver {
        public void update() {
            SimpleMolecule oldFreeMolecule  = freeMolecule;
            SimpleMolecule oldBoundMolecule = boundMolecule;

            boundMolecule = null;
            freeMolecule  = null;

            if (getSelectedMolecule() != null && getNearestToSelectedMolecule() != null) {
                if( getSelectedMolecule().isPartOfComposite() ) {
                    boundMolecule = getSelectedMolecule();
                    if( !getNearestToSelectedMolecule().isPartOfComposite() ) {
                        freeMolecule = getNearestToSelectedMolecule();
                    }
                }
                else if( getNearestToSelectedMolecule().isPartOfComposite() ) {
                    boundMolecule = getNearestToSelectedMolecule();

                    if( !getSelectedMolecule().isPartOfComposite() ) {
                        freeMolecule = getSelectedMolecule();
                    }
                }
            }

            if (oldFreeMolecule != freeMolecule) {
                getSelectionListenerController().notifyFreeMoleculeChanged( oldFreeMolecule, freeMolecule );
            }
            if (oldBoundMolecule != boundMolecule) {
                getSelectionListenerController().notifyBoundMoleculeChanged( oldBoundMolecule, boundMolecule );
            }
        }
    }

    private class SelectedMoleculeListener extends MRModel.ModelListenerAdapter implements SelectedMoleculeTracker.Listener {
        public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                 SimpleMolecule prevTrackedMolecule ) {
            if( selectedMolecule != null ) {
                selectedMolecule.removeObserver( getController() );

                getSelectionListenerController().notifyMoleculeUnselected( selectedMolecule );
            }

            selectedMolecule = newTrackedMolecule;

            if ( newTrackedMolecule != null ) {                
                newTrackedMolecule.addObserver( getController() );
            }

            getSelectionListenerController().notifyMoleculeSelected( selectedMolecule );
        }

        public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
            if( nearestToSelectedMolecule != null ) {
                nearestToSelectedMolecule.removeObserver( getController() );
            }

            nearestToSelectedMolecule = newClosestMolecule;

            newClosestMolecule.addObserver( getController() );

            getSelectionListenerController().notifyNearestToSelectedChanged( prevClosestMolecule, newClosestMolecule );
        }


        public void notifyEnergyProfileChanged( EnergyProfile newProfile ) {
            getSelectionListenerController().notifyEnergyProfileChanged( newProfile );
        }
    }



    public interface MoleculeSelectionStateListener {
        void notifyMoleculeSelected(SimpleMolecule selectedMolecule);

        void notifyMoleculeUnselected(SimpleMolecule selectedMolecule);

        void notifyNearestToSelectedChanged(SimpleMolecule oldNearest, SimpleMolecule newNearest);

        void notifyFreeMoleculeChanged(SimpleMolecule oldFreeMolecule, SimpleMolecule newFreeMolecule);

        void notifyBoundMoleculeChanged(SimpleMolecule oldBoundMolecule, SimpleMolecule newBoundMolecule);

        void notifyEnergyProfileChanged(EnergyProfile newProfile);
    }
}
