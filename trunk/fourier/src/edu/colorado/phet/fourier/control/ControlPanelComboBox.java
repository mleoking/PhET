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
import java.util.Enumeration;
import java.util.Hashtable;

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
    private Hashtable _choices;

    public ControlPanelComboBox( String label, Hashtable choices ) {

        assert ( label != null );
        assert ( choices != null );

        // Label
        _label = new JLabel( label );

        // Choices 
        _choices = choices;

        // Combo box
        _comboBox = new JComboBox();
        Enumeration enum = _choices.keys();
        while ( enum.hasMoreElements() ) {
            _comboBox.addItem( enum.nextElement() );
        }

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
    
    public void setChoices( Hashtable choices ) {
        assert( choices != null );
        _choices = choices;
        _comboBox.removeAllItems();
        Enumeration enum = _choices.keys();
        while ( enum.hasMoreElements() ) {
            _comboBox.addItem( enum.nextElement() );
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
    
    public void setSelectedIndex( int index ) {
        _comboBox.setSelectedIndex( index );
    }
    
    public int getSelectedIndex() {
        return _comboBox.getSelectedIndex();
    }
}
