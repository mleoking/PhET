/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.control;

import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * ControlPanelComboBox
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ControlPanelComboBox extends JPanel {

    private JLabel _label;
    private JComboBox _comboBox;

    public ControlPanelComboBox( String label, ArrayList choices ) {

        assert ( label != null );
        assert ( choices != null );

        // Label
        _label = new JLabel( label );

        // Combo box
        _comboBox = new JComboBox();
        
        // Choices 
        setChoices( choices );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.addAnchoredComponent( _label, 0, 0, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _comboBox, 0, 1, GridBagConstraints.WEST );
    }
    
    public JComboBox getComboBox() {
        return _comboBox;
    }
    
    public void setEnabled( boolean enabled ) {
        _label.setEnabled( enabled );
        _comboBox.setEnabled( enabled );
    }
    
    public boolean isEnabled() {
        return _comboBox.isEnabled();
    }
    
    public void setChoices( ArrayList choices ) {
        assert( choices != null );
        _comboBox.removeAllItems();
        for ( int i = 0; i < choices.size(); i++ ) {
            _comboBox.addItem( choices.get( i ) );
        }
    }
    
    public void addActionListener( ActionListener listener ) {
        _comboBox.addActionListener( listener );
    }
 
    public void removeActionListener( ActionListener listener ) {
        _comboBox.removeActionListener( listener );
    }
    
    public void setSelectedItem( Object item ) {
        _comboBox.setSelectedItem( item );
    }
    
    public Object getSelectedItem() {
        return _comboBox.getSelectedItem();
    }
    
    public int getSelectedKey() {
        int key = -1;
        Object item = _comboBox.getSelectedItem();
        if ( item instanceof Choice ) {
            key = ((Choice)item).getKey();
        }
        return key;
    }
    
    public static class Choice {

        private int _key;
        private Object _value;
        
        public Choice( int key, Object value ) {
            _key = key;
            _value = value;
        }
        
        public int getKey() {
            return _key;
        }
        
        public Object getValue() {
            return _value;
        }
        
        public String toString() {
            return _value.toString();
        }
    }
}
