/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.enums.AtomicModel;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * AtomicModelSelector is the control for selecting an atomic model.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class AtomicModelSelector extends PhetPNode {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JLabel _atomicModelLabel;
    private JLabel _classicalLabel;
    private JLabel _quantumLabel;
    
    private JToggleButton _billiardBallButton;
    private JToggleButton _plumPuddingButton;
    private JToggleButton _solarSystemButton;
    private JToggleButton _bohrButton;
    private JToggleButton _deBroglieButton;
    private JToggleButton _schrodingerButton;
    
    private EventListenerList _listenerList;
    
    private AtomicModel _selectedModel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public AtomicModelSelector( PSwingCanvas canvas ) {
        
        _listenerList = new EventListenerList();
        
        // Labels
        _atomicModelLabel = new JLabel( SimStrings.get( "label.atomicModel" ) );
        _classicalLabel = new JLabel( SimStrings.get( "label.classical" ) );
        _quantumLabel = new JLabel( SimStrings.get( "label.quantum" ) );
        
        // Buttons
        _billiardBallButton = new JToggleButton( SimStrings.get( "label.billardBall" ) );
        _plumPuddingButton = new JToggleButton( SimStrings.get( "label.plumPudding" ) );
        _solarSystemButton = new JToggleButton( SimStrings.get( "label.solarSystem" ) );
        _bohrButton = new JToggleButton( SimStrings.get( "label.bohr" ) );
        _deBroglieButton = new JToggleButton( SimStrings.get( "label.deBroglie" ) );
        _schrodingerButton = new JToggleButton( SimStrings.get( "label.schrodinger" ) );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _billiardBallButton );
        buttonGroup.add( _plumPuddingButton );
        buttonGroup.add( _solarSystemButton );
        buttonGroup.add( _bohrButton );
        buttonGroup.add( _deBroglieButton );
        buttonGroup.add( _schrodingerButton );
        
        // Action handlers
        _billiardBallButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _billiardBallButton.isSelected() ) {
                    _selectedModel = AtomicModel.BILLIARD_BALL;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }
        });
        _plumPuddingButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _plumPuddingButton.isSelected() ) {
                    _selectedModel = AtomicModel.PLUM_PUDDING;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }    
        });
        _solarSystemButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _solarSystemButton.isSelected() ) {
                    _selectedModel = AtomicModel.SOLAR_SYSTEM;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }    
        });
        _bohrButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _bohrButton.isSelected() ) {
                    _selectedModel = AtomicModel.BOHR;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }    
        });
        _deBroglieButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _deBroglieButton.isSelected() ) {
                    _selectedModel = AtomicModel.DEBROGLIE;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }    
        });
        _schrodingerButton.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( _schrodingerButton.isSelected() ) {
                    _selectedModel = AtomicModel.SCHRODINGER;
                    fireChangeEvent( new ChangeEvent( this ) );
                }
            }    
        });
        
        // Layout
        JPanel buttonPanel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( buttonPanel );
        buttonPanel.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _classicalLabel, row, 0 );
        layout.addComponent( _quantumLabel, row, 5, 1, 1, GridBagConstraints.EAST );
        row++;
        layout.addComponent( _billiardBallButton, row, col++ );
        layout.addComponent( _plumPuddingButton, row, col++ );
        layout.addComponent( _solarSystemButton, row, col++ );
        layout.addComponent( _bohrButton, row, col++ );
        layout.addComponent( _deBroglieButton, row, col++ );
        layout.addComponent( _schrodingerButton, row, col++ );
        
        JPanel titlePanel = new JPanel();
        titlePanel.add( _atomicModelLabel );
        
        JPanel outerPanel = new JPanel();
        outerPanel.setBorder( HAConstants.CONTROL_PANEL_BORDER );
        outerPanel.setLayout( new BorderLayout() );
        outerPanel.add( titlePanel, BorderLayout.NORTH );
        outerPanel.add( buttonPanel, BorderLayout.CENTER );
        
        // Piccolo wrapper for panel
        PSwing pswing = new PSwing( canvas, outerPanel );
        addChild( pswing );
        
        // Fonts
        _atomicModelLabel.setFont( HAConstants.TITLE_FONT );
        _classicalLabel.setFont( HAConstants.CONTROL_FONT );
        _quantumLabel.setFont( HAConstants.CONTROL_FONT );
        _billiardBallButton.setFont( HAConstants.CONTROL_FONT );
        _plumPuddingButton.setFont( HAConstants.CONTROL_FONT );
        _solarSystemButton.setFont( HAConstants.CONTROL_FONT );
        _bohrButton.setFont( HAConstants.CONTROL_FONT );
        _deBroglieButton.setFont( HAConstants.CONTROL_FONT );
        _schrodingerButton.setFont( HAConstants.CONTROL_FONT );
        
        // Opacity
        outerPanel.setOpaque( true );
        titlePanel.setOpaque( false );
        buttonPanel.setOpaque( false );
        _atomicModelLabel.setOpaque( false );
        _classicalLabel.setOpaque( false );
        _quantumLabel.setOpaque( false );
        _billiardBallButton.setOpaque( false );
        _plumPuddingButton.setOpaque( false );
        _solarSystemButton.setOpaque( false );
        _bohrButton.setOpaque( false );
        _deBroglieButton.setOpaque( false );
        _schrodingerButton.setOpaque( false );
        
        // Colors
        outerPanel.setBackground( HAConstants.ATOMIC_MODEL_CONTROL_BACKGROUND );
        _atomicModelLabel.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _classicalLabel.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _quantumLabel.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _billiardBallButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _plumPuddingButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _solarSystemButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _bohrButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _deBroglieButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        _schrodingerButton.setForeground( HAConstants.ATOMIC_MODEL_FOREGROUND );
        
        // Default state
        setSelection( AtomicModel.BILLIARD_BALL );
    }
    
    //----------------------------------------------------------------------------
    // Mutators
    //----------------------------------------------------------------------------
    
    public void setSelection( AtomicModel model ) {
        _selectedModel = model;
        if ( model == AtomicModel.BILLIARD_BALL ) {
            _billiardBallButton.setSelected( true );
        }
        else if ( model == AtomicModel.PLUM_PUDDING ) {
            _plumPuddingButton.setSelected( true );
        }
        else if ( model == AtomicModel.SOLAR_SYSTEM ) {
            _solarSystemButton.setSelected( true );
        }
        else if ( model == AtomicModel.BOHR ) {
            _bohrButton.setSelected( true );
        }
        else if ( model == AtomicModel.DEBROGLIE ) {
            _deBroglieButton.setSelected( true );
        }
        else if ( model == AtomicModel.SCHRODINGER ) {
            _schrodingerButton.setSelected( true );
        }
        fireChangeEvent( new ChangeEvent( this ) );
    }
    
    public AtomicModel getSelection() {
        return _selectedModel;
    }
    
    //----------------------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------------------

    /**
     * Adds a ChangeListener.
     *
     * @param listener the listener
     */
    public void addChangeListener( ChangeListener listener ) {
        _listenerList.add( ChangeListener.class, listener );
    }

    /**
     * Removes a ChangeListener.
     *
     * @param listener the listener
     */
    public void removeChangeListener( ChangeListener listener ) {
        _listenerList.remove( ChangeListener.class, listener );
    }

    /**
     * Fires a ChangeEvent.
     *
     * @param event the event
     */
    private void fireChangeEvent( ChangeEvent event ) {
        Object[] listeners = _listenerList.getListenerList();
        for( int i = 0; i < listeners.length; i += 2 ) {
            if( listeners[i] == ChangeListener.class ) {
                ( (ChangeListener)listeners[i + 1] ).stateChanged( event );
            }
        }
    }
}
