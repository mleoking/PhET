// Copyright 2002-2011, University of Colorado

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
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


/**
 * FourierComboBox combines a JComboBox and JLabel into one panel that 
 * can be treated like a JComboBox.  It provides methods that make it
 * easier to set & get selection choices.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class FourierComboBox extends JPanel {

    private JLabel _label;
    private JComboBox _comboBox;

    public FourierComboBox( String label, ArrayList choices ) {

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
        layout.setInsets( FourierAbstractControlPanel.DEFAULT_INSETS );
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
    
    public void addItemListener( ItemListener listener ) {
        _comboBox.addItemListener( listener );
    }
 
    public void removeItemListener( ItemListener listener ) {
        _comboBox.removeItemListener( listener );
    }
    
    public void setSelectedItem( Object item ) {
        _comboBox.setSelectedItem( item );
    }
    
    public Object getSelectedItem() {
        return _comboBox.getSelectedItem();
    }
    
    public void setSelectedIndex( int index ) {
        _comboBox.setSelectedIndex( index );
    }
    
    public int getSelectedIndex() {
        return _comboBox.getSelectedIndex();
    }
    
    public void setSelectedKey( Object key ) {
        int numberOfItems = _comboBox.getItemCount();
        for ( int i = 0; i < numberOfItems; i++ ) {
            Object item = _comboBox.getItemAt( i );
            if ( item instanceof Choice ) {
                if ( key == ((Choice)item).getKey() ) {
                    setSelectedItem( item );
                    break;
                }
            }
        }
    }
    
    public Object getSelectedKey() {
        Object key = null;
        Object item = _comboBox.getSelectedItem();
        if ( item instanceof Choice ) {
            key = ((Choice)item).getKey();
        }
        return key;
    }
    
    public static class Choice {

        private Object _key;
        private Object _value;
        
        public Choice( Object key, Object value ) {
            _key = key;
            _value = value;
        }
        
        public Object getKey() {
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
