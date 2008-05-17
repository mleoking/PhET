/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.dialog;

import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersApplication;

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

    private final GlaciersApplication _app;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner, GlaciersApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        setModal( false );

        _app = app;

        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        JLabel label = new JLabel( "- developer controls go here -" );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( label, row, column );

        return panel;
    }
}
