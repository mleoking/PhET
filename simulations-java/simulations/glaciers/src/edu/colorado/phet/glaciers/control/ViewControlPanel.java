/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JCheckBox;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * ViewControlPanel is the "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends AbstractSubPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.SUBPANEL_BACKGROUND_COLOR;
    private static final String TITLE_STRING = GlaciersStrings.TITLE_VIEW_CONTROLS;
    private static final Color TITLE_COLOR = GlaciersConstants.SUBPANEL_TITLE_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.SUBPANEL_TITLE_FONT;
    private static final Color CONTROL_COLOR = GlaciersConstants.SUBPANEL_CONTROL_COLOR;
    private static final Font CONTROL_FONT = GlaciersConstants.SUBPANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JCheckBox _equilibriumLineCheckBox;
    private JCheckBox _iceFlowCheckBox;
    private JCheckBox _coordinatesCheckBox;
    
    private ArrayList _listeners; // list of ViewControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ViewControlPanel() {
        super( TITLE_STRING, TITLE_COLOR, TITLE_FONT );
        
        _listeners = new ArrayList();
        
        _equilibriumLineCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_EQUILIBRIUM_LINE );
        _equilibriumLineCheckBox.setFont( CONTROL_FONT );
        _equilibriumLineCheckBox.setForeground( CONTROL_COLOR );
        _equilibriumLineCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyEquilibriumLineChanged();
            }
        });
        
        _iceFlowCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_ICE_FLOW );
        _iceFlowCheckBox.setFont( CONTROL_FONT );
        _iceFlowCheckBox.setForeground( CONTROL_COLOR );
        _iceFlowCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyIceFlowChanged();
            }
        });
        
        _coordinatesCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_COORDINATES );
        _coordinatesCheckBox.setFont( CONTROL_FONT );
        _coordinatesCheckBox.setForeground( CONTROL_COLOR );
        _coordinatesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyCoordinatesChanged();
            }
        });
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _equilibriumLineCheckBox, row++, column );
        layout.addComponent( _iceFlowCheckBox, row++, column );
        layout.addComponent( _coordinatesCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* excludedClasses */, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineSelected( boolean selected ) {
        if ( selected != isEquilibriumLineSelected() ) {
            _equilibriumLineCheckBox.setSelected( selected );
            notifyEquilibriumLineChanged();
        }
    }
    
    public boolean isEquilibriumLineSelected() {
        return _equilibriumLineCheckBox.isSelected();
    }
    
    public void setIceFlowSelected( boolean selected ) {
        if ( selected != isIceFlowSelected() ) {
            _iceFlowCheckBox.setSelected( selected );
            notifyIceFlowChanged();
        }
    }
    
    public boolean isIceFlowSelected() {
        return _iceFlowCheckBox.isSelected();
    }
    
    public void setCoordinatesSelected( boolean selected ) {
        if ( selected != isCoordinatesSelected() ) {
            _coordinatesCheckBox.setSelected( selected );
            notifyCoordinatesChanged();
        }
    }
    
    public boolean isCoordinatesSelected() {
        return _coordinatesCheckBox.isSelected();
    }
    
    //----------------------------------------------------------------------------
    // Listener
    //----------------------------------------------------------------------------
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public interface ViewControlPanelListener {
        public void equilibriumLineChanged( boolean b );
        public void iceFlowChanged( boolean b );
        public void coordinatesChanged( boolean b );
    }
    
    public static class ViewControlPanelAdapter implements ViewControlPanelListener {
        public void equilibriumLineChanged( boolean b ) {};
        public void iceFlowChanged( boolean b ) {};
        public void coordinatesChanged( boolean b ) {};
    }
    
    public void addViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeViewControlPanelListener( ViewControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    //----------------------------------------------------------------------------
    // Notification
    //----------------------------------------------------------------------------
    
    private void notifyEquilibriumLineChanged() {
        boolean b = isEquilibriumLineSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).equilibriumLineChanged( b );
        }
    }

    private void notifyIceFlowChanged() {
        boolean b = isIceFlowSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).iceFlowChanged( b );
        }
    }
    
    private void notifyCoordinatesChanged() {
        boolean b = isCoordinatesSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).coordinatesChanged( b );
        }
    }
}
