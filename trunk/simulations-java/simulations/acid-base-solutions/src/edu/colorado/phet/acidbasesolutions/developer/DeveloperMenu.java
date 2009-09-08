/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.acidbasesolutions.AcidBaseSolutionsApplication;
import edu.colorado.phet.common.piccolophet.PhetTabbedPane;
import edu.colorado.phet.common.piccolophet.TabbedPanePropertiesDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    private final AcidBaseSolutionsApplication app;
    private JCheckBoxMenuItem tabPropertiesItem;
    
    private JDialog tabPropertiesDialog;

    public DeveloperMenu( AcidBaseSolutionsApplication app ) {
        super( "Developer" );

        this.app = app;

        if ( app.getTabbedModulePane() instanceof PhetTabbedPane ) {
            tabPropertiesItem = new JCheckBoxMenuItem( "Tabbed Pane properties..." );
            add( tabPropertiesItem );
            tabPropertiesItem.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    handleTabProperties();
                }
            } );
        }
    }

    private void handleTabProperties() {
        if ( tabPropertiesItem.isSelected() ) {
            Frame owner = app.getPhetFrame();
            tabPropertiesDialog = new TabbedPanePropertiesDialog( owner, (PhetTabbedPane)app.getTabbedModulePane() );
            tabPropertiesDialog.setVisible( true );
            tabPropertiesDialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    tabPropertiesItem.setSelected( false );
                    tabPropertiesDialog = null;
                }
            } );
        }
        else {
            tabPropertiesDialog.dispose();
        }
    }
}
