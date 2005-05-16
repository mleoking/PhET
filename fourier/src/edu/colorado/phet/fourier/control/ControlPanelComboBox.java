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
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.fourier.util.EasyGridBagLayout;


/**
 * ControlPanelComboBox
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ControlPanelComboBox extends JPanel {

    private JComboBox _comboBox;
    private Hashtable _choices;

    public ControlPanelComboBox( String label, Hashtable choices ) {

        assert ( label != null );
        assert ( choices != null );

        // Label
        JLabel jlabel = new JLabel( label );

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
        layout.addAnchoredComponent( jlabel, 0, 0, GridBagConstraints.EAST );
        layout.addAnchoredComponent( _comboBox, 0, 1, GridBagConstraints.WEST );
    }
    
    public JComboBox getComboBox() {
        return _comboBox;
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
}
