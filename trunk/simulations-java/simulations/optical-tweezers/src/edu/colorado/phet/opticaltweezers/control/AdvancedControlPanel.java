/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.FluidControlDialog;
import edu.colorado.phet.opticaltweezers.model.Fluid;

/**
 * AdvancedControlPanel contains miscellaneous "advanced" controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedControlPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    private Frame _parentFrame;
    private FluidControlDialog _fluidControlDialog;
    
    private JButton _advancedButton;
    private Box _panel;
    private JCheckBox _fluidControlsCheckBox;
    private JCheckBox _momemtumChangeCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param fluid
     * @param parentFrame
     */
    public AdvancedControlPanel( Font titleFont, Font controlFont, Fluid fluid, Frame parentFrame ) {
        super();
        
        _fluid = fluid;
        _parentFrame = parentFrame;
        _fluidControlDialog = null;
        
        _advancedButton = new JButton( OTResources.getString( "label.showAdvanced" ) );
        _advancedButton.setFont( titleFont );
        _advancedButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleAdvancedButton();
            }
        } );

        _fluidControlsCheckBox = new JCheckBox( OTResources.getString( "label.controlFluidFlow" ) );
        _fluidControlsCheckBox.setFont( controlFont );
        _fluidControlsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidControlsCheckBox();
            }
        } );
        
        _momemtumChangeCheckBox = new JCheckBox( OTResources.getString( "label.showMomentumChange" ) );
        _momemtumChangeCheckBox.setFont( controlFont );
        _momemtumChangeCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleMomentumChangeCheckBox();
            }
        } );
        
        _panel = new Box( BoxLayout.Y_AXIS );
        _panel.add( _fluidControlsCheckBox );
//XXX        _panel.add( _momemtumChangeCheckBox );
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 0 );
        int row = 0;
        layout.addComponent( _advancedButton, row++, 1 );
        layout.addComponent( _panel, row++, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _panel.setVisible( false );
        _fluidControlsCheckBox.setSelected( false );
        _momemtumChangeCheckBox.setSelected( false );
        
        //XXX not implemented
        _momemtumChangeCheckBox.setForeground( Color.RED );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setAdvancedVisible( boolean b ) {
        if ( b ^ _panel.isVisible() ) {
            handleAdvancedButton();
        }
    }
    
    public boolean isAdvancedVisible() {
        return _advancedButton.isVisible();
    }
    
    public void setFluidControlSelected( boolean b ) {
        _fluidControlsCheckBox.setSelected( b );
        handleFluidControlsCheckBox();
    }
    
    public boolean isFluidControlsSelected() {
        return _fluidControlsCheckBox.isSelected();
    }
    
    public void setMomentumChangeSelected( boolean b ) {
        _momemtumChangeCheckBox.setSelected( b );
        handleMomentumChangeCheckBox();
    }
    
    public boolean isMomentumChangeSelected() {
        return _momemtumChangeCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------
    
    private void handleAdvancedButton() {
        _panel.setVisible( !_panel.isVisible() );
        if ( _panel.isVisible() ) {
            _advancedButton.setText( OTResources.getString( "label.hideAdvanced" ) );
        }
        else {
            _advancedButton.setText( OTResources.getString( "label.showAdvanced" ) );
        }
    }

    private void handleFluidControlsCheckBox() {

        final boolean selected = _fluidControlsCheckBox.isSelected();
        
        if ( !selected ) {
            if ( _fluidControlDialog != null ) {
                _fluidControlDialog.dispose();
                _fluidControlDialog = null;
            }
        }
        else {
            _fluidControlDialog = new FluidControlDialog( _parentFrame, OTConstants.CONTROL_PANEL_CONTROL_FONT, _fluid );
            _fluidControlDialog.addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                public void windowClosing( WindowEvent e ) {
                    _fluidControlDialog.dispose();
                }

                // called by JDialog.dispose
                public void windowClosed( WindowEvent e ) {
                    _fluidControlDialog = null;
                    _fluidControlsCheckBox.setSelected( false );
                }
            } );
            // Position at the left-center of the main frame
            Point p = _parentFrame.getLocationOnScreen();
            _fluidControlDialog.setLocation( (int) p.getX() + 10, (int) p.getY() + ( ( _parentFrame.getHeight() - _fluidControlDialog.getHeight() ) / 2 ) );
            _fluidControlDialog.show();
        }
    }
    
    private void handleMomentumChangeCheckBox() {
        final boolean selected = _momemtumChangeCheckBox.isSelected();
        //XXX
    }
}
