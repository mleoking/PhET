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
import edu.colorado.phet.glaciers.GlaciersStrings;

/**
 * ViewControlPanel is the "View" control panel.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ViewControlPanel extends JPanel {
    
    private static final Color BACKGROUND_COLOR = new Color( 82, 126, 90 ); // green
    private static final Color TITLE_COLOR = Color.WHITE;
    private static final Color CONTROL_COLOR = Color.WHITE;

    private JCheckBox _equilibriumLineCheckBox;
    private JCheckBox _iceFlowCheckBox;
    private JCheckBox _snowfallCheckBox;
    private JCheckBox _coordinatesCheckBox;
    private JCheckBox _ageOfIceCheckBox;
    
    private ArrayList _listeners;
    
    public ViewControlPanel( Font titleFont, Font controlFont ) {
        super();
        
        _listeners = new ArrayList();
        
        _equilibriumLineCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_EQUILIBRIUM_LINE );
        _equilibriumLineCheckBox.setFont( controlFont );
        _equilibriumLineCheckBox.setForeground( CONTROL_COLOR );
        _equilibriumLineCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyEquilibriumLineChanged();
            }
        });
        
        _iceFlowCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_ICE_FLOW );
        _iceFlowCheckBox.setFont( controlFont );
        _iceFlowCheckBox.setForeground( CONTROL_COLOR );
        _iceFlowCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyIceFlowChanged();
            }
        });
        
        _snowfallCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_SNOWFALL );
        _snowfallCheckBox.setFont( controlFont );
        _snowfallCheckBox.setForeground( CONTROL_COLOR );
        _snowfallCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifySnowfallChanged();
            }
        });
        
        _coordinatesCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_COORDINATES );
        _coordinatesCheckBox.setFont( controlFont );
        _coordinatesCheckBox.setForeground( CONTROL_COLOR );
        _coordinatesCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyCoordinatesChanged();
            }
        });
        
        _ageOfIceCheckBox = new JCheckBox( GlaciersStrings.CHECK_BOX_AGE_OF_ICE );
        _ageOfIceCheckBox.setFont( controlFont );
        _ageOfIceCheckBox.setForeground( CONTROL_COLOR );
        _ageOfIceCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                notifyAgeOfIceChanged();
            }
        });
        
        Border emptyBorder = BorderFactory.createEmptyBorder( 3, 3, 3, 3 );
        TitledBorder titledBorder = new TitledBorder( GlaciersStrings.TITLE_VIEW );
        titledBorder.setTitleFont( titleFont );
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
    
    /**
     * Interface implemented by all listeners who are interested in events related to this control panel.
     */
    public static interface ViewControlPanelListener {
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
    
    public void addListener( ViewControlPanelListener listener ) {
        _listeners.add( listener );
    }
    
    public void removeListener( ViewControlPanelListener listener ) {
        _listeners.remove( listener );
    }
    
    private void notifyEquilibriumLineChanged() {
        boolean b = isEquilibriumSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ViewControlPanelListener ) {
                ( (ViewControlPanelListener) o ).equilibriumLineChanged( b );
            }
        }
    }
    
    private void notifyIceFlowChanged() {
        boolean b = isIceFlowSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ViewControlPanelListener ) {
                ( (ViewControlPanelListener) o ).iceFlowChanged( b );
            }
        }
    }
    
    private void notifySnowfallChanged() {
        boolean b = isSnowfallSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ViewControlPanelListener ) {
                ( (ViewControlPanelListener) o ).snowfallChanged( b );
            }
        }
    }
    
    private void notifyCoordinatesChanged() {
        boolean b = isCoordinatesSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ViewControlPanelListener ) {
                ( (ViewControlPanelListener) o ).coordinatesChanged( b );
            }
        }
    }
    
    private void notifyAgeOfIceChanged() {
        boolean b = isAgeOfIceSelected();
        Iterator i = _listeners.iterator();
        while ( i.hasNext() ) {
            Object o = i.next();
            if ( o instanceof ViewControlPanelListener ) {
                ( (ViewControlPanelListener) o ).ageOfIceChanged( b );
            }
        }
    }
}
