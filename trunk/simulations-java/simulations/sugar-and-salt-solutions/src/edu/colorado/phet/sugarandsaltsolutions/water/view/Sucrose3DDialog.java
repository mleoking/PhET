// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.water.view;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JFrame;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.jmolphet.JmolDialog;
import edu.colorado.phet.common.jmolphet.Molecule;
import edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources;

import static edu.colorado.phet.sugarandsaltsolutions.SugarAndSaltSolutionsResources.RESOURCES;

/**
 * Creates and displays the JMolDialog, and minimizes and restores it when the user switches tabs
 *
 * @author Sam Reid
 */
public class Sucrose3DDialog {

    //The JmolDialog which shows the sucrose in 3d
    JmolDialog jmolDialog = null;

    //Flag to indicate whether the JMolDialog should be shown when the user switches to this tab
    private boolean showJMolDialogOnActivate;

    //Parent for the dialog
    private JFrame parent;

    public Sucrose3DDialog( JFrame parent ) {
        this.parent = parent;
    }

    //Shows the 3d sucrose view in a dialog, creating the dialog lazily if necessary
    public void showDialog() {
        if ( jmolDialog == null ) {
            jmolDialog = JmolDialog.displayMolecule3D( parent, new Molecule() {
                public String getDisplayName() {
                    return SugarAndSaltSolutionsResources.Strings.SUGAR;
                }

                public String getData() {
                    return loadSucroseStructure();
                }

                public void fixJmolColors( JmolViewer viewer ) {
                }
            }, "Space fill", "Ball and stick", "Loading..." );
        }
        else {
            jmolDialog.setVisible( true );
        }
    }


    //Called when the user switches to the water tab from another tab.  Remembers if the JMolDialog was showing and restores it if so
    public void moduleActivated() {
        if ( jmolDialog != null ) {
            jmolDialog.setVisible( showJMolDialogOnActivate );
        }
    }

    //Called when the user switches to another tab.  Stores the state of the jmol dialog so that it can be restored when the user comes back to this tab
    public void moduleDeactivated() {
        showJMolDialogOnActivate = jmolDialog != null && jmolDialog.isVisible();
        if ( jmolDialog != null ) {
            jmolDialog.setVisible( false );
        }
    }

    //Load the 3d structure of sucrose
    private static String loadSucroseStructure() {
        try {
            BufferedReader structureReader = new BufferedReader( new InputStreamReader( RESOURCES.getResourceAsStream( "sucrose.pdb" ) ) );
            String s = "";
            String line = structureReader.readLine();
            while ( line != null ) {
                s = s + line + "\n";
                line = structureReader.readLine();
            }
            return s;
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public void reset() {

        //When the module is reset, close the jmol dialog if it is open
        if ( jmolDialog != null ) {
            jmolDialog.setVisible( false );
            jmolDialog = null;//Set it to null so that when it is opened again it will be in the startup location instead of saved location
        }
    }
}
