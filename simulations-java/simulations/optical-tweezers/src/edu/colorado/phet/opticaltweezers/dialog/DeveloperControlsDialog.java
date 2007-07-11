/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.opticaltweezers.OpticalTweezersApplication;
import edu.colorado.phet.opticaltweezers.control.ColorControl;

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

    private OpticalTweezersApplication _app;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperControlsDialog( Frame owner, OpticalTweezersApplication app ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        
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
        
        Color controlPanelBackground = _app.getControlPanelBackground();
        ColorControl controlPanelColorControl = new ColorControl( "control panel background color: ", controlPanelBackground ) {
            protected void setColor( Color color ) {
                super.setColor( color );
                _app.setControlPanelBackground( color );
            }
        };
        
        Color selectedTabColor = _app.getSelectedTabColor();
        ColorControl selectedTabColorControl = new ColorControl( "selected module tab color: ", selectedTabColor ) {
            protected void setColor( Color color ) {
                super.setColor( color );
                _app.setSelectedTabColor( color );
            }
        };
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( controlPanelColorControl, row++, column );
        layout.addComponent( selectedTabColorControl, row++, column );

        return panel;
    }
}
