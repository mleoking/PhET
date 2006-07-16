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

import edu.colorado.phet.simlauncher.Catalog;
import edu.colorado.phet.simlauncher.Simulation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * CheckAllForUpdateAction
 * <p/>
 * Causes all installed simulations to check for updates
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CheckAllForUpdateAction extends AbstractAction {
    private JDialog waitDlg;
    private Component component;

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------
    public CheckAllForUpdateAction( Component component ) {
        this.component = component;
    }

    public void actionPerformed( ActionEvent e ) {
        List installedSims = Catalog.instance().getInstalledSimulations();
        Simulation simulation = null;
//        showWaitDialog();

        for( int i = 0; i < installedSims.size(); i++ ) {
            simulation = (Simulation)installedSims.get( i );
            if( !simulation.isCurrent() ) {
                simulation.setUpdateAvailable( true );
            }
        }

//        hideWaitDialog();
    }


    private void showWaitDialog() {
        JFrame frame = (JFrame)SwingUtilities.getRoot( component );
        waitDlg = new JDialog( frame, "Checking for updates...", true );
        JLabel message = new JLabel( "Please wait while the installed simulations are checked for available updates." );
        JPanel contentPane = (JPanel)waitDlg.getContentPane();
        contentPane.setLayout( new GridBagLayout() );
        GridBagConstraints gbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE,
                                                         1, 1, 1, 1,
                                                         GridBagConstraints.CENTER,
                                                         GridBagConstraints.NONE,
                                                         new Insets( 10, 20, 10, 20 ),
                                                         0, 0 );
        contentPane.add( message, gbc );
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate( true );
        contentPane.add( progressBar, gbc );

        waitDlg.pack();
        waitDlg.setLocationRelativeTo( frame );
        waitDlg.setDefaultCloseOperation( JDialog.DO_NOTHING_ON_CLOSE );
        waitDlg.setVisible( true );
    }

    private void hideWaitDialog() {
        waitDlg.setVisible( false );
    }

}
