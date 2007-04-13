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

import java.awt.GridBagConstraints;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;

/**
 * RadioButtonWithIcon adds an icon to the right of a JRadioButton. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class RadioButtonWithIcon extends JPanel {

    private JRadioButton _radioButton;
    
    /**
     * Constructor.
     * @param label
     * @param icon
     */
    public RadioButtonWithIcon( String label, Icon icon ) {
        super();
        _radioButton = new JRadioButton( label );
        JLabel iconLabel = new JLabel( icon );
        
        // Opacity
        setOpaque( false );
        _radioButton.setOpaque( false );
        iconLabel.setOpaque( false );
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int col = 0;
        layout.addComponent( _radioButton, row, col++ );
        layout.addComponent( iconLabel, row, col );
    }
    
    /**
     * Gets the JRadioButton.
     * @return JRadioButton
     */
    public JRadioButton getRadioButton() {
        return _radioButton;
    }
}
