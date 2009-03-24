/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control;

import java.awt.*;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.opticaltweezers.OTConstants;
import edu.colorado.phet.opticaltweezers.OTResources;
import edu.colorado.phet.opticaltweezers.dialog.FluidControlsDialog;
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
    private FluidControlsDialog _fluidControlsDialog;
    private Point _fluidControlsDialogOffset;  // default offset from upper-left corner of parent frame
    private Point _fluidControlsDialogLocation;
    
    private JCheckBox _rulerCheckBox;
    private Box _fluidVacuumPanel;
    private JRadioButton _fluidRadioButton, _vacuumRadioButton;
    private JCheckBox _fluidControlsCheckBox;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param titleFont
     * @param controlFont
     * @param parentFrame
     * @param fluidControlsDialogOffset
     * @param miscroscopeSlide
     */
    public MiscControlPanel( Font titleFont, Font controlFont, Frame parentFrame, Point fluidControlsDialogOffset, PNode rulerNode, Fluid fluid ) {
        super();
        
        _parentFrame = parentFrame;
        
        _rulerNode = rulerNode;
        
        _fluid = fluid;
        _fluid.addObserver( this );
        
        _fluidControlsDialog = null;
        _fluidControlsDialogOffset = new Point( fluidControlsDialogOffset );
        _fluidControlsDialogLocation = null;
        
        _rulerCheckBox = new JCheckBox( OTResources.getString( "label.showRuler" ) );
        _rulerCheckBox.setFont( controlFont );
        _rulerCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleRulerCheckBox();
            }
        });
        Icon rulerIcon = new ImageIcon( OTResources.getImage( OTConstants.IMAGE_RULER ) );
        JLabel rulerLabel = new JLabel( rulerIcon );
        rulerLabel.addMouseListener( new MouseAdapter() {
            // clicking on the ruler toggles the state of the ruler check box
            public void mouseReleased(MouseEvent e) {
                _rulerCheckBox.setSelected( !_rulerCheckBox.isSelected() );
                handleRulerCheckBox();
            }
        });
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
        
        // Layout
        JPanel innerPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( innerPanel );
        innerPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        layout.setInsets( OTConstants.SUB_PANEL_INSETS );
        layout.setMinimumWidth( 0, 20 );
        int row = 0;
        layout.addComponent( rulerPanel, row++, 0 );
        layout.addComponent( _fluidVacuumPanel, row++, 0 );
        layout.addComponent( _fluidControlsCheckBox, row++, 0 );
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
        _rulerCheckBox.setSelected( false );
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
    
    public boolean isFluidSelected() {
        return _fluidRadioButton.isSelected();
    }
    
    public void setVacuumSelected( boolean b ) {
        _fluidRadioButton.setSelected( !b );
        _vacuumRadioButton.setSelected( b );
        handleFluidOrVacuumChoice();
    }
    
    public boolean isVacuumSelected() {
        return _vacuumRadioButton.isSelected();
    }
    
    public void setFluidControlsSelected( boolean b ) {
        _fluidControlsCheckBox.setSelected( b );
        handleFluidControlsCheckBox();
    }
    
    public boolean isFluidControlsSelected() {
        return _fluidControlsCheckBox.isSelected();
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
            if ( _parentFrame.isVisible() ) {
                openFluidControlsDialog();
            }
        }
        else {
            closeFluidControlsDialog();
        }
    }
    
    //----------------------------------------------------------------------------
    // Fluid controls dialog
    //----------------------------------------------------------------------------
    
    private void openFluidControlsDialog() {
        
        closeFluidControlsDialog();
        
        _fluidControlsDialog = new FluidControlsDialog( _parentFrame, OTConstants.CONTROL_PANEL_CONTROL_FONT, _fluid );
        _fluidControlsDialog.addWindowListener( new WindowAdapter() {

            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                closeFluidControlsDialog();
            }

            // called by JDialog.dispose
            public void windowClosed( WindowEvent e ) {
                _fluidControlsDialog = null;
                if ( _fluidRadioButton.isSelected() ) {
                    _fluidControlsCheckBox.setSelected( false );
                }
            }
        } );
        
        if ( _fluidControlsDialogLocation == null ) {
            // initial placement is at the lower-left of the main frame
            Point p = _parentFrame.getLocationOnScreen();
            int x = (int)( p.getX() + _fluidControlsDialogOffset.getX() );
            int y = (int)( p.getY() + _fluidControlsDialogOffset.getY() );
            _fluidControlsDialog.setLocation( x, y );
        }
        else {
            _fluidControlsDialog.setLocation( _fluidControlsDialogLocation );
        }
        _fluidControlsDialog.show();
    }
    
    private void closeFluidControlsDialog() {
        if ( _fluidControlsDialog != null ) {
            _fluidControlsDialogLocation = _fluidControlsDialog.getLocation();
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
