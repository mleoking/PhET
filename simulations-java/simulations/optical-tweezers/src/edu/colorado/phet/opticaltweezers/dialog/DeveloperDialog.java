/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.control.BeadMotionControlPanel;
import edu.colorado.phet.opticaltweezers.model.Bead;

/**
 * Developer controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperDialog extends JDialog {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    private static final Font CONTROL_FONT = new JLabel().getFont();
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperDialog( Frame owner, Bead bead ) {
        super( owner );
        setTitle( "Developer Controls");
        
        JPanel inputPanel = createInputPanel( bead );
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        setResizable( false );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel( Bead bead ) {
        
        JPanel beadMotionPanel = new BeadMotionControlPanel( TITLE_FONT, CONTROL_FONT, bead );
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( beadMotionPanel, row++, column );
        
        return panel;
    }
}
