/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.opticaltweezers.control.FluidControlPanel;
import edu.colorado.phet.opticaltweezers.model.Fluid;

/**
 * FluidControlDialog is a nonmodal dialog that display the fluid controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FluidControlDialog extends JDialog {

    private FluidControlPanel _fluidControlPanel;
    private JButton _closeButton;
    
    /**
     * Constructor.
     * 
     * @param owner parent of this dialog
     * @param font font to used for all controls
     * @param fluid model that we'll be controlling
     */
    public FluidControlDialog( Frame owner, Font font, Fluid fluid ) {
        super( owner );
        assert( owner != null );
        setResizable( false );
        setModal( false );
        setTitle( SimStrings.get( "title.fluidControlDialog" ) );
        
        _fluidControlPanel = new FluidControlPanel( fluid, font ); 
        
        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.setFont( font );
        _closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                dispose();
            }
        } );
        JPanel actionsPanel = new JPanel();
        actionsPanel.add( _closeButton );
        
        JPanel mainPanel = new VerticalLayoutPanel();
        mainPanel.add( _fluidControlPanel );
        mainPanel.add( new JSeparator() );
        mainPanel.add( actionsPanel );
        
        getContentPane().add( mainPanel );
        pack();
    }
    
    /**
     * Performs cleanup before disposing of the dialog.
     */
    public void dispose() {
        _fluidControlPanel.cleanup(); // deletes observer relationship with model
        super.dispose();
    }
}
