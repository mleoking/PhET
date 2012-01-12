// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.phscale.view.graph;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.phscale.PHScaleConstants;
import edu.colorado.phet.phscale.PHScaleStrings;

/**
 * ScaleControlPanel contains the controls for switching the graph's scale.
 * The scale can be either log or linear.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ScaleControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font CONTROL_FONT = PHScaleConstants.CONTROL_FONT;
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final ArrayList _listeners;
    private final JRadioButton _logRadioButton;
    private final JRadioButton _linearRadioButton;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ScaleControlPanel() {
        super();
        setOpaque( false );

        _listeners = new ArrayList();
        
        _logRadioButton = new JRadioButton( PHScaleStrings.RADIO_BUTTON_LOGARITHMIC_SCALE );
        _logRadioButton.setOpaque( false );
        _logRadioButton.setFont( CONTROL_FONT );
        _logRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySelectionChanged();
            }
        } );
        
        _linearRadioButton = new JRadioButton( PHScaleStrings.RADIO_BUTTON_LINEAR_SCALE );
        _linearRadioButton.setOpaque( false );
        _linearRadioButton.setFont( CONTROL_FONT );
        _linearRadioButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySelectionChanged();
            }
        } );
        
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( _logRadioButton );
        buttonGroup.add( _linearRadioButton );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( _logRadioButton, row++, col );
        layout.addComponent( _linearRadioButton, row++, col );
        
        _logRadioButton.setSelected( true );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public boolean isLogSelected() {
        return _logRadioButton.isSelected();
    }
    
    public void setLogSelected( boolean selected ) {
        if ( selected != isLogSelected() ) {
            _logRadioButton.setSelected( selected );
            notifySelectionChanged();
        }
    }
    
    public boolean isLinearSelected() {
        return !isLogSelected();
    }
    
    public void setLinearSelected( boolean selected ) {
        setLogSelected( !selected );
    }
    
    //----------------------------------------------------------------------------
    // Listener interface
    //----------------------------------------------------------------------------
    
    public interface ScaleControlPanelListener {
        public void selectionChanged();
    }
    
    public void addScaleControlPanelListener( ScaleControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeScaleControlPanelListener( ScaleControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifySelectionChanged() {
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ScaleControlPanelListener) i.next() ).selectionChanged();
        }
    }
}
