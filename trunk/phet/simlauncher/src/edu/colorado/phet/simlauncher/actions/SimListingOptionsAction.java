/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.actions;

import edu.colorado.phet.simlauncher.Options;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * SimListingOptionsAction
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SimListingOptionsAction extends AbstractAction {
    private Component parent;

    public SimListingOptionsAction( Component parent ) {
        super( "Simulation information..." );
        this.parent = parent;
    }

    public void actionPerformed( ActionEvent e ) {

        // Installed simulations
        JLabel installedSimsSectionLabel = new JLabel( "Installed Simulations" );
        final JCheckBoxMenuItem iconOptionCB_A = new JCheckBoxMenuItem( "Show thumbnails" );
        iconOptionCB_A.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                Options.instance().setShowInstalledThumbnailsNoUpdate( iconOptionCB_A.isSelected() );
            }
        } );
        iconOptionCB_A.setSelected( Options.instance().isShowInstalledThumbnails() );

        JCheckBoxMenuItem abstractCB_A = new JCheckBoxMenuItem( "Show description" );

        // Unistalled simulations
        JLabel uninstalledSimsSectionLabel = new JLabel( "Uninstalled Simulations" );
        final JCheckBoxMenuItem iconOptionCB_B = new JCheckBoxMenuItem( "Show thumbnails" );
        iconOptionCB_B.addActionListener( new AbstractAction() {
            public void actionPerformed( ActionEvent e ) {
                Options.instance().setShowUninstalledThumbnailsNoUpdate( iconOptionCB_B.isSelected() );
            }
        } );
        iconOptionCB_B.setSelected( Options.instance().isShowCatalogThumbnails() );

        JCheckBoxMenuItem abstractCB_B = new JCheckBoxMenuItem( "Show description" );

        // Lay out the panel for the dialog
        JPanel optionsPane = new JPanel( new GridBagLayout() );
        Insets sectionHeaderInsets = new Insets( 5, 0, 0, 0 );
        Insets optionInsets = new Insets( 0, 10, 0, 0 );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.NORTHWEST,
                                                         GridBagConstraints.NONE,
                                                         sectionHeaderInsets,
                                                         0, 0 );

        optionsPane.add( installedSimsSectionLabel, gbc );
        gbc.insets = optionInsets;
        optionsPane.add( iconOptionCB_A, gbc );
        optionsPane.add( abstractCB_A, gbc );
        gbc.insets = sectionHeaderInsets;
        optionsPane.add( uninstalledSimsSectionLabel, gbc );
        gbc.insets = optionInsets;
        optionsPane.add( iconOptionCB_B, gbc );
        optionsPane.add( abstractCB_B, gbc );

//        int option = JOptionPane.showOptionDialog( parent,
//                                                   optionsPane,
//                                                   "Simulation Listing Options",
//                                                   JOptionPane.OK_CANCEL_OPTION,
//                                                   JOptionPane.QUESTION_MESSAGE,
//                                                   null, null,
//                                                   JOptionPane.OK_OPTION );
//        if( option == JOptionPane.OK_OPTION ) {
//            Options.instance().notifyListeners();
//        }
    }
}