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
import edu.umd.cs.piccolo.PNode;

/**
 * MiscControlPanel contains miscellaneous controls.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MiscControlPanel extends JPanel implements Observer {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private PNode _rulerNode;
    private Fluid _fluid;
    private Frame _parentFrame;
    private FluidControlDialog _fluidControlsDialog;
    
    private JCheckBox _rulerCheckBox;
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
    public MiscControlPanel( Font titleFont, Font controlFont, Frame parentFrame, PNode rulerNode, Fluid fluid ) {
        super();
        
        _parentFrame = parentFrame;
        
        _rulerNode = rulerNode;
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _fluidControlsDialog = null;
        
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        _rulerCheckBox.setFont( controlFont );
        _rulerCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        });
        Icon rulerIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_RULER ) );
        JLabel rulerLabel = new JLabel( rulerIcon );
        Box rulerPanel = new Box( BoxLayout.X_AXIS );
        rulerPanel.add( _rulerCheckBox );
        rulerPanel.add( Box.createHorizontalStrut( 5 ) );
        rulerPanel.add( rulerLabel );
        
        // Fluid-vacuum choice
        {
            JLabel label = new JLabel( OTResources.getString( "label.beadIsIn" ) );
            
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

            ButtonGroup buttonGroup = new ButtonGroup();
            buttonGroup.add( _fluidRadioButton );
            buttonGroup.add( _vacuumRadioButton );
            
            _fluidVacuumPanel = new Box( BoxLayout.X_AXIS );
            _fluidVacuumPanel.add( label );
            _fluidVacuumPanel.add( _fluidRadioButton );
            _fluidVacuumPanel.add( _vacuumRadioButton );
        }
        
        _fluidControlsCheckBox = new JCheckBox( OTResources.getString( "label.showFluidControls" ) );
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
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( rulerPanel, row++, 0 );
        layout.addComponent( _fluidVacuumPanel, row++, 0 );
        layout.addComponent( _fluidControlsCheckBox, row++, 0 );
        //XXX feature disabled for AAPT
//        layout.addComponent( _momemtumChangeCheckBox, row++, 0 );
        setLayout( new BorderLayout() );
        add( innerPanel, BorderLayout.WEST );
        
        // Default state
        if ( _fluid.isEnabled() ) {
            _fluidRadioButton.setSelected( true );
        }
        else {
            _vacuumRadioButton.setSelected( true );
        }
        _fluidControlsCheckBox.setSelected( false );
        _momemtumChangeCheckBox.setSelected( false );
        _rulerCheckBox.setSelected( false );
        
        //XXX not implemented
        _momemtumChangeCheckBox.setForeground( Color.RED );
    }
    
    public void cleanup() {
        _fluid.deleteObserver( this );
        closeFluidControlsDialog();
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setRulerSelected( boolean b ) {
        _rulerCheckBox.setSelected( b );
        handleRulerCheckBox();
    }
    
    public boolean isRulerSelected() {
        return _rulerCheckBox.isSelected();
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
    
    public void setFluidControlsSelected( boolean b ) {
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
    
    private void handleRulerCheckBox() {
        final boolean selected = _rulerCheckBox.isSelected();
        _rulerNode.setVisible( selected );
    }
    
    private void handleFluidOrVacuumChoice() {
        
        _fluid.deleteObserver( this );
        _fluid.setEnabled( _fluidRadioButton.isSelected() );
        _fluid.addObserver( this );
        
        _fluidControlsCheckBox.setEnabled( _fluidRadioButton.isSelected() );
        
        if ( _fluidRadioButton.isSelected() ) {
            if ( _fluidControlsCheckBox.isSelected() ) {
                openFluidControlsDialog();
            }
        }
        else {
            closeFluidControlsDialog();
        }
    }

    private void handleFluidControlsCheckBox() {
        final boolean selected = _fluidRadioButton.isSelected() && _fluidControlsCheckBox.isSelected();
        if ( selected ) {
            openFluidControlsDialog();
        }
        else {
            closeFluidControlsDialog();
        }
    }
    
    private void handleMomentumChangeCheckBox() {
        final boolean selected = _momemtumChangeCheckBox.isSelected();
        //XXX
    }

    //----------------------------------------------------------------------------
    // Fluid controls dialog
    //----------------------------------------------------------------------------
    
    private void openFluidControlsDialog() {
        
        closeFluidControlsDialog();
        
        _fluidControlsDialog = new FluidControlDialog( _parentFrame, OTConstants.CONTROL_PANEL_CONTROL_FONT, _fluid );
        _fluidControlsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                _fluidControlsDialog.dispose();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _fluidControlsDialog = null;
                if ( _fluidRadioButton.isSelected() ) {
                    _fluidControlsCheckBox.setSelected( false );
                }
            }
        } );
        
        // Position at the lower-left of the main frame
        Point p = _parentFrame.getLocationOnScreen();
        _fluidControlsDialog.setLocation( (int) p.getX() + 10, (int) p.getY() + ( _parentFrame.getHeight() - _fluidControlsDialog.getHeight() - 60 ) );
        _fluidControlsDialog.show();
    }
    
    private void closeFluidControlsDialog() {
        if ( _fluidControlsDialog != null ) {
            _fluidControlsDialog.dispose();
            _fluidControlsDialog = null;
        }
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
