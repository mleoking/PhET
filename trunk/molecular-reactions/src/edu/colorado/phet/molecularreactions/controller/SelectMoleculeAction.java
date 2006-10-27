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
import edu.colorado.phet.common.util.PhetUtilities;
import edu.colorado.phet.molecularreactions.model.*;

import java.util.List;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.*;

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

        // Un-select the currently selected molecule, if there is one
        SimpleMolecule molecule = model.getMoleculeBeingTracked();
        if( molecule != null ) {
            molecule.setSelectionStatus( Selectable.NOT_SELECTED );
        }
        if( listener != null ) {
            model.removeSelectedMoleculeTrackerListener( listener );
        }

        final JDialog dlg = new Prompt();
        Point frameLocation = PhetUtilities.getPhetFrame().getLocation();
        Dimension frameSize = PhetUtilities.getPhetFrame().getSize();
        dlg.setLocation( (int)(frameLocation.getX() + frameSize.getWidth() - 300),
                         (int)(frameLocation.getY() + frameSize.getHeight() / 2 - 200 ));
        dlg.setVisible( true );

        // Listen to the molecules being tracked
        listener = new SelectedMoleculeTracker.Listener() {
            public void moleculeBeingTrackedChanged( SimpleMolecule newTrackedMolecule,
                                                     SimpleMolecule prevTrackedMolecule ) {
                dlg.setVisible( false );
                clock.start();

            }

            public void closestMoleculeChanged( SimpleMolecule newClosestMolecule, SimpleMolecule prevClosestMolecule ) {
                // noop
            }
        };
        model.addSelectedMoleculeTrackerListener( listener );

    }

    private static class Prompt extends JDialog {
        public Prompt( ) throws HeadlessException {
            super( PhetUtilities.getPhetFrame(), false );
            JPanel msgPanel = new JPanel( new GridBagLayout() );
            GridBagConstraints gbc = new GridBagConstraints( 0,0,1,1,1,1,
                                                             GridBagConstraints.CENTER,
                                                             GridBagConstraints.NONE,
                                                             new Insets( 15, 15, 15, 15 ),
                                                             0,0);
            msgPanel.add( new JLabel( "<html>Click on the molecule you wish to track.<br> " +
                                              "The simulation will resume running when you do."),
                          gbc );
            setContentPane( msgPanel );
            pack();
        }
    }
}
