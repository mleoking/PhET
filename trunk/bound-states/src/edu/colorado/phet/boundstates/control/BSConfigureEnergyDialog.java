/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.control;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.boundstates.model.BSAbstractWell;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;


/**
 * BSConfigureEnergyDialog
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSConfigureEnergyDialog extends JDialog implements Observer {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Insets SLIDER_INSETS = new Insets( 0, 0, 0, 0 );
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private BSAbstractWell _well;
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _spacingSlider;
    private JButton _closeButton;
    private EventListener _eventListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSConfigureEnergyDialog( Frame parent, BSAbstractWell well ) {
        super( parent );
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSConfigureEnergyDialog.title" ) );
        
        _well = well;
        _well.addObserver( this );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        createUI( parent );
        updateControls();
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        if ( _well != null ) {
            _well.deleteObserver( this );
            _well = null;
        }
    }
    
    //----------------------------------------------------------------------------
    // Private initializers
    //----------------------------------------------------------------------------

    /*
     * Creates the user interface for the dialog.
     * 
     * @param parent the parent Frame
     */
    private void createUI( Frame parent ) {
        
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();

        JPanel bottomPanel = new JPanel( new BorderLayout() );
        bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
        bottomPanel.add( actionsPanel, BorderLayout.CENTER );
        
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
        mainPanel.add( inputPanel, BorderLayout.CENTER );
        mainPanel.add( bottomPanel, BorderLayout.SOUTH );

        getContentPane().add( mainPanel );
        pack();
    }

    /*
     * Creates the dialog's input panel.
     * 
     * @return the input panel
     */
    private JPanel createInputPanel() {
        
        String positionUnits = SimStrings.get( "units.position" );
        String energyUnits = SimStrings.get( "units.energy" );
        
        String widthFormat = SimStrings.get( "label.wellWidth" ) + " {0} " + positionUnits;
        _widthSlider = new SliderControl( 1, 0.1, 3, 3-0.1, 1, 1, widthFormat, SLIDER_INSETS );
        
        String depthFormat = SimStrings.get( "label.wellDepth" ) + " {0} " + energyUnits;
        _depthSlider = new SliderControl( 1, -10, 0, 10, 1, 1, depthFormat, SLIDER_INSETS );
        
        String offsetFormat = SimStrings.get( "label.wellOffset" ) + " {0} " + energyUnits;
        _offsetSlider = new SliderControl( 1, -15, 5, 20, 1, 1, offsetFormat, SLIDER_INSETS );
        
        String spacingFormat = SimStrings.get( "label.wellSpacing" ) + " {0} " + positionUnits;
        _spacingSlider = new SliderControl( 1, 0.1, 3, 3-0.1, 1, 1, spacingFormat, SLIDER_INSETS );
        
        JPanel inputPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( inputPanel );
        inputPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _widthSlider, row, col );
        row++;
        layout.addComponent( _depthSlider, row, col );
        row++;
        layout.addComponent( _offsetSlider, row, col );
        row++;
        layout.addComponent( _spacingSlider, row, col );
        row++;

        // Interction
        _widthSlider.addChangeListener( _eventListener );
        _depthSlider.addChangeListener( _eventListener );
        _offsetSlider.addChangeListener( _eventListener );
        _spacingSlider.addChangeListener( _eventListener );
        
        return inputPanel;
    }

    /*
     * Creates the dialog's actions panel, consisting of a Close button.
     * 
     * @return the actions panel
     */
    private JPanel createActionsPanel() {

        _closeButton = new JButton( SimStrings.get( "button.close" ) );
        _closeButton.addActionListener( _eventListener );

        JPanel buttonPanel = new JPanel( new GridLayout( 1, 1 ) );
        buttonPanel.add( _closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( buttonPanel );

        return actionPanel;
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Synchronizes the view with the model.
     */
    public void update( Observable o, Object arg ) {
        updateControls();
    }
    
    private void updateControls() {
        
        // Sync values
        _widthSlider.setValue( _well.getWidth() );
        _depthSlider.setValue( _well.getDepth() );
        _offsetSlider.setValue( _well.getOffset() );
        _spacingSlider.setValue( _well.getSpacing() );
        
        // Sync ranges
        _widthSlider.setMaximum( _spacingSlider.getValue() - 0.1 );//XXX
        _spacingSlider.setMinimum( _widthSlider.getValue() + 0.1 );//XXX
        
        // Visibility
        //XXX hide the width slider if well is Coulomb
        _spacingSlider.setVisible( _well.getNumberOfWells() > 1 );
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener extends WindowAdapter implements ActionListener, ChangeListener {

        public void windowClosing( WindowEvent event ) {
            handleCloseAction();
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _closeButton ) {
                handleCloseAction();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }

        public void stateChanged( ChangeEvent event ) {
            if ( event.getSource() == _widthSlider ) {
                handleWidthChange();
            }
            else if ( event.getSource() == _depthSlider ) {
                handleDepthChange();
            }
            else if ( event.getSource() == _offsetSlider ) {
                handleOffsetChange();
            }
            else if ( event.getSource() == _spacingSlider ) {
                handleSpacingChange();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    private void handleCloseAction() {
        cleanup();
        dispose();
    }
    
    private void handleWidthChange() {
        final double width = _widthSlider.getValue();
        _well.setWidth( width );
    }
    
    private void handleDepthChange() {
        final double depth = _depthSlider.getValue();
        _well.setDepth( depth );
    }
    
    private void handleOffsetChange() {
        final double offset = _offsetSlider.getValue();
        _well.setOffset( offset );
    }
    
    private void handleSpacingChange() {
        final double spacing = _spacingSlider.getValue();
        _well.setSpacing( spacing );
    }

}
