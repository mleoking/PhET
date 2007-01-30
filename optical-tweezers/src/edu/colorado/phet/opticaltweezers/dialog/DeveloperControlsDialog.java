/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.opticaltweezers.OTConstants;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DeveloperControlsDialog extends JDialog {
    
    // Title labels
    private static class TitleLabel extends JLabel {
        public TitleLabel( String title ) {
            super( title );
            setForeground( TITLE_COLOR );
            setFont( TITLE_FONT );
        }
    }
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets DEFAULT_INSETS = new Insets( 3, 3, 3, 3 );
    
    private static final Color TITLE_COLOR = Color.RED;
    private static final Font TITLE_FONT = new Font( OTConstants.DEFAULT_FONT_NAME, Font.BOLD, 14 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
   
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperControlsDialog( Frame owner ) {
        super( owner, "Developer Controls" );
        setResizable( false );
        
        JPanel inputPanel = createInputPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {
        
        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        {
           layout.addComponent( new JLabel( "controls go here" ), row, 0 );
        }
        
        return panel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------
    
    private class EventListener extends MouseAdapter implements ChangeListener {
        
        public EventListener() {}

        public void stateChanged( ChangeEvent event ) {
            Object source = event.getSource();
            
//            else {
//                throw new UnsupportedOperationException( "unsupported ChangeEvent source: " + source );
//            }
        }
        
        public void mouseClicked( MouseEvent event ) {
            Object source = event.getSource();
//            else {
//                throw new UnsupportedOperationException( "unsupported MouseEvent source: " + source );
//            }
        }
    }
}
