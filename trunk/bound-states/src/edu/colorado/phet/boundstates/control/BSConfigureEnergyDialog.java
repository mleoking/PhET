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
    
    private SliderControl _widthSlider;
    private SliderControl _depthSlider;
    private SliderControl _offsetSlider;
    private SliderControl _centerSlider;
    private SliderControl _spacingSlider;
    private JButton _closeButton;
    private EventListener _eventListener;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public BSConfigureEnergyDialog( Frame parent ) {
        super( parent );
        setModal( false );
        setResizable( false );
        setTitle( SimStrings.get( "BSConfigureEnergyDialog.title" ) );
        
        _eventListener = new EventListener();
        addWindowListener( _eventListener );
        
        createUI( parent );
    }
    
    /**
     * Clients should call this before releasing references to this object.
     */
    public void cleanup() {
        //XXX
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
        _widthSlider = new SliderControl( 1, 0.1, 10, 10-0.1, 1, 1, widthFormat, SLIDER_INSETS );
        
        String depthFormat = SimStrings.get( "label.wellDepth" ) + " {0} " + energyUnits;
        _depthSlider = new SliderControl( 1, 0.1, 10, 10-0.1, 1, 1, depthFormat, SLIDER_INSETS );
        
        String offsetFormat = SimStrings.get( "label.wellOffset" ) + " {0} " + energyUnits;
        _offsetSlider = new SliderControl( 1, 0.1, 10, 10-0.1, 1, 1, offsetFormat, SLIDER_INSETS );
        
        String centerFormat = SimStrings.get( "label.wellCenter" ) + " {0} " + positionUnits;
        _centerSlider = new SliderControl( 1, 0.1, 10, 10-0.1, 1, 1, centerFormat, SLIDER_INSETS );
        
        String spacingFormat = SimStrings.get( "label.wellSpacing" ) + " {0} " + positionUnits;
        _spacingSlider = new SliderControl( 1, 0.1, 10, 10-0.1, 1, 1, spacingFormat, SLIDER_INSETS );
        
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
        layout.addComponent( _centerSlider, row, col );
        row++;
        layout.addComponent( _spacingSlider, row, col );
        row++;

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
        //XXX
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /*
     * Dispatches events to the appropriate handler method.
     */
    private class EventListener extends WindowAdapter implements ActionListener, ChangeListener {

        public void windowClosing( WindowEvent event ) {
            handleClose();
        }
        
        public void actionPerformed( ActionEvent event ) {
            if ( event.getSource() == _closeButton ) {
                handleClose();
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
            else if ( event.getSource() == _centerSlider ) {
                handleCenterChange();
            }
            else if ( event.getSource() == _spacingSlider ) {
                handleSpacingChange();
            }
            else {
                throw new IllegalArgumentException( "unexpected event: " + event );
            }
        }
    }
    
    /*
     * Handles the "Close" button.
     */
    private void handleClose() {
        cleanup();
        dispose();
    }
    
    private void handleWidthChange() {
        //XXX
    }
    
    private void handleDepthChange() {
        //XXX
    }
    
    private void handleOffsetChange() {
        //XXX
    }
    
    private void handleCenterChange() {
        //XXX
    }
    
    private void handleSpacingChange() {
        //XXX
    }

}
