/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;
import edu.colorado.phet.naturalselection.model.NaturalSelectionModel;
import edu.colorado.phet.naturalselection.module.NaturalSelectionModule;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------

    private NaturalSelectionApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, final NaturalSelectionApplication app ) {
        super( owner );
        setTitle( "Developer Controls" );
        //super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        _app = app;


        JPanel mainPanel = new JPanel();

        EasyGridBagLayout layout = new EasyGridBagLayout( mainPanel );

        mainPanel.setLayout( layout );

        JButton frenzyButton = new JButton( "Start Frenzy" );
        frenzyButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                NaturalSelectionModule module = (NaturalSelectionModule) app.getActiveModule();
                NaturalSelectionModel model = module.getMyModel();
                model.startFrenzy();
            }
        } );
        layout.addComponent( frenzyButton, 0, 0 );

        JButton killAllButton = new JButton( "Kill bunnies" );
        killAllButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent actionEvent ) {
                ( (NaturalSelectionModule) app.getActiveModule() ).getMyModel().killAllBunnies();
            }
        } );
        layout.addComponent( killAllButton, 1, 0 );

        layout.addComponent( new DeveloperSettingsPanel( app ), 2, 0 );

        setContentPane( mainPanel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

}
