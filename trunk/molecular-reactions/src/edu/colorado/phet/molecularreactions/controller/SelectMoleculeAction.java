/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.controller;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.molecularreactions.model.MRModel;
import edu.colorado.phet.molecularreactions.model.Selectable;
import edu.colorado.phet.molecularreactions.model.SelectedMoleculeTracker;
import edu.colorado.phet.molecularreactions.model.SimpleMolecule;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * SelectMoleculeAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SelectMoleculeAction extends AbstractAction {
    private IClock clock;
    private MRModel model;
    private SelectedMoleculeTracker.Listener listener;

    public SelectMoleculeAction( IClock clock, MRModel model ) {
        this.clock = clock;
        this.model = model;
    }

    public void actionPerformed( ActionEvent e ) {
        clock.pause();
        SimpleMolecule molecule = model.getMoleculeBeingTracked();
        if( molecule != null ) {
            molecule.setSelectionStatus( Selectable.NOT_SELECTED );
        }

        if( listener != null ) {
            model.removeSelectedMoleculeTrackerListener( listener );
        }

        listener = new SelectedMoleculeTracker.Listener() {
            public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                     SimpleMolecule prevTrackedMolecule ) {
                clock.start();
            }

            public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
                // noop
            }
        };
        model.addSelectedMoleculeTrackerListener( listener );

    }
}
