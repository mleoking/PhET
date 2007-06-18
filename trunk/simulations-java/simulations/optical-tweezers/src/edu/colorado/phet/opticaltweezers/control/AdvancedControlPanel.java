/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.FluidControlDialog;
import edu.colorado.phet.opticaltweezers.model.Fluid;
import edu.colorado.phet.opticaltweezers.model.MicroscopeSlide;

/**
 * AdvancedControlPanel contains miscellaneous "advanced" controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AdvancedControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private Fluid _fluid;
    private Frame _parentFrame;
    private FluidControlDialog _fluidControlDialog;
    
    private JButton _showHideButton;
    private JPanel _mainPanel;
    private Box _fluidVacuumPanel;
    private JRadioButton _fluidRadioButton, _vacuumRadioButton;
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
     * @param parentFrame
     * @param miscroscopeSlide
     */
    public AdvancedControlPanel( Font titleFont, Font controlFont, Frame parentFrame, Fluid fluid ) {
        super();
        
        _parentFrame = parentFrame;
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _fluidControlDialog = null;
        
        _showHideButton = new JButton( OTResources.getString( "label.showAdvanced" ) );
        _showHideButton.setFont( titleFont );
        _showHideButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleShowHideButton();
            }
        } );
        
        _fluidRadioButton = new JRadioButton( OTResources.getString( "choice.fluid" ) );
        _fluidRadioButton.setFont( controlFont );
        _fluidRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidOrVacuumChoice();
            }
        } );
        
        _vacuumRadioButton = new JRadioButton( OTResources.getString( "choice.vacuum" ) );
        _vacuumRadioButton.setFont( controlFont );
        _vacuumRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleFluidOrVacuumChoice();
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
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _fluidRadioButton );
        buttonGroup.add( _vacuumRadioButton );
        _fluidVacuumPanel = new Box( BoxLayout.X_AXIS );
        _fluidVacuumPanel.add( _fluidRadioButton );
        _fluidVacuumPanel.add( _vacuumRadioButton );
        
        _mainPanel = new JPanel();
        {
            EasyGridBagLayout layout = new EasyGridBagLayout( _mainPanel );
            _mainPanel.setLayout( layout );
            layout.setAnchor( GridBagConstraints.WEST );
            layout.setFill( GridBagConstraints.HORIZONTAL );
            layout.setMinimumWidth( 0, 20 );
            int row = 0;
            layout.addComponent( _fluidVacuumPanel, row++, 0 );
            layout.addComponent( _fluidControlsCheckBox, row++, 0 );
            layout.addComponent( _momemtumChangeCheckBox, row++, 0 );
        }
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 0 );
        int row = 0;
        layout.addComponent( _showHideButton, row++, 1 );
        layout.addComponent( _mainPanel, row++, 1 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        _mainPanel.setVisible( false );
        if ( _fluid.isEnabled() ) {
            _fluidRadioButton.setSelected( true );
        }
        else {
            _vacuumRadioButton.setSelected( true );
        }
        _fluidControlsCheckBox.setSelected( false );
        _momemtumChangeCheckBox.setSelected( false );
        
        //XXX not implemented
        _momemtumChangeCheckBox.setForeground( Color.RED );
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setAdvancedVisible( boolean b ) {
        if ( b ^ _mainPanel.isVisible() ) {
            handleShowHideButton();
        }
    }
    
    public boolean isAdvancedVisible() {
        return _showHideButton.isVisible();
    }
    
    public void setFluidVacuumPanelVisible( boolean b ) {
        _fluidVacuumPanel.setVisible( b );
    }
    
    public void setFluidSelected( boolean b ) {
        _fluidRadioButton.setSelected( b );
        _vacuumRadioButton.setSelected( !b );
        handleFluidOrVacuumChoice();
    }
    
    public void setVacuumSelected( boolean b ) {
        _fluidRadioButton.setSelected( !b );
        _vacuumRadioButton.setSelected( b );
        handleFluidOrVacuumChoice();
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
    
    private void handleShowHideButton() {
        _mainPanel.setVisible( !_mainPanel.isVisible() );
        if ( _mainPanel.isVisible() ) {
            _showHideButton.setText( OTResources.getString( "label.hideAdvanced" ) );
        }
        else {
            _showHideButton.setText( OTResources.getString( "label.showAdvanced" ) );
        }
    }
    
    private void handleFluidOrVacuumChoice() {
        _fluid.deleteObserver( this );
        _fluid.setEnabled( _fluidRadioButton.isSelected() );
        _fluid.addObserver( this );
        
        _fluidControlsCheckBox.setEnabled( _fluidRadioButton.isSelected() );
        
        handleFluidControlsCheckBox();
    }

    private void handleFluidControlsCheckBox() {

        final boolean selected = _fluidRadioButton.isSelected() && _fluidControlsCheckBox.isSelected();
        
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
                    _fluidControlsCheckBox.setSelected( !( _fluidControlsCheckBox.isSelected() && _fluidRadioButton.isSelected() ) );
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

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    public void update( Observable o, Object arg ) {
        if ( o == _fluid ) {
            if ( arg == Fluid.PROPERTY_ENABLED ) {
                setFluidSelected( _fluid.isEnabled() );
            }
        }
    }
}
