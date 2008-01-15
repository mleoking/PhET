/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * ViewControlPanel is the "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends JPanel {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Color BACKGROUND_COLOR = GlaciersConstants.INNER_PANEL_BACKGROUND_COLOR;
    private static final Color TITLE_COLOR = GlaciersConstants.INNER_PANEL_TITLE_COLOR;
    private static final Color CONTROL_COLOR = GlaciersConstants.INNER_PANEL_CONTROL_COLOR;
    private static final Font TITLE_FONT = GlaciersConstants.CONTROL_PANEL_TITLE_FONT;
    private static final Font CONTROL_FONT = GlaciersConstants.CONTROL_PANEL_CONTROL_FONT;

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private JCheckBox _equilibriumLineCheckBox;
    private JCheckBox _iceFlowCheckBox;
    private JCheckBox _snowfallCheckBox;
    private JCheckBox _coordinatesCheckBox;
    private JCheckBox _ageOfIceCheckBox;
    
    private ArrayList _listeners; // list of ViewControlPanelListener
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ViewControlPanel() {
        super();
        
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
        
        _snowfallCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_SNOWFALL );
        _snowfallCheckBox.setFont( CONTROL_FONT );
        _snowfallCheckBox.setForeground( CONTROL_COLOR );
        _snowfallCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySnowfallChanged();
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
        
        _ageOfIceCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_AGE_OF_ICE );
        _ageOfIceCheckBox.setFont( CONTROL_FONT );
        _ageOfIceCheckBox.setForeground( CONTROL_COLOR );
        _ageOfIceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyAgeOfIceChanged();
            }
        });
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_VIEW );
        titledBorder.setTitleFont( TITLE_FONT );
        titledBorder.setTitleColor( TITLE_COLOR );
        titledBorder.setBorder( BorderFactory.createLineBorder( TITLE_COLOR, 1 ) );
        Border compoundBorder = BorderFactory.createCompoundBorder( emptyBorder, titledBorder );
        setBorder( compoundBorder );
        
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int column = 0;
        layout.setAnchor( GridBagConstraints.WEST );
        layout.addComponent( _equilibriumLineCheckBox, row++, column );
        layout.addComponent( _iceFlowCheckBox, row++, column );
        layout.addComponent( _snowfallCheckBox, row++, column );
        layout.addComponent( _coordinatesCheckBox, row++, column );
        layout.addComponent( _ageOfIceCheckBox, row++, column );
        
        SwingUtils.setBackgroundDeep( this, BACKGROUND_COLOR, null /* excludedClasses */, false /* processContentsOfExcludedContainers */ );
    }
    
    //----------------------------------------------------------------------------
    // Setters and getters
    //----------------------------------------------------------------------------
    
    public void setEquilibriumLineSelected( boolean b ) {
        _equilibriumLineCheckBox.setSelected( b );
    }
    
    public boolean isEquilibriumSelected() {
        return _equilibriumLineCheckBox.isSelected();
    }
    
    public void setIceFlowSelected( boolean b ) {
        _iceFlowCheckBox.setSelected( b );
    }
    
    public boolean isIceFlowSelected() {
        return _iceFlowCheckBox.isSelected();
    }
    
    public void setSnowfallSelected( boolean b ) {
        _snowfallCheckBox.setSelected( b );
    }
    
    public boolean isSnowfallSelected() {
        return _snowfallCheckBox.isSelected();
    }
    
    public void setCoordinatesSelected( boolean b ) {
        _coordinatesCheckBox.setSelected( b );
    }
    
    public boolean isCoordinatesSelected() {
        return _coordinatesCheckBox.isSelected();
    }
    
    public void setAgeOfIceSelected( boolean b ) {
        _ageOfIceCheckBox.setSelected( b );
    }
    
    public boolean isAgeOfIceSelected() {
        return _ageOfIceCheckBox.isSelected();
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
        public void snowfallChanged( boolean b );
        public void coordinatesChanged( boolean b );
        public void ageOfIceChanged( boolean b );
    }
    
    public static class ViewControlPanelAdapter implements ViewControlPanelListener {
        public void equilibriumLineChanged( boolean b ) {};
        public void iceFlowChanged( boolean b ) {};
        public void snowfallChanged( boolean b ) {};
        public void coordinatesChanged( boolean b ) {};
        public void ageOfIceChanged( boolean b ) {};
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
        boolean b = isEquilibriumSelected();
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
    
    private void notifySnowfallChanged() {
        boolean b = isSnowfallSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).snowfallChanged( b );
        }
    }

    private void notifyCoordinatesChanged() {
        boolean b = isCoordinatesSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).coordinatesChanged( b );
        }
    }

    private void notifyAgeOfIceChanged() {
        boolean b = isAgeOfIceSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            ( (ViewControlPanelListener) i.next() ).ageOfIceChanged( b );
        }
    }
}
