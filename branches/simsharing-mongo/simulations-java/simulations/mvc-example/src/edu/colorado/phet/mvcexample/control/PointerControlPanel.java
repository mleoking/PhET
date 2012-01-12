// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import java.awt.Color;
import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;

/**
 * PointerControlPanel is a control panel for pointers.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PointerControlPanel extends JPanel {
    
    private PositionControl _positionControl;
    private OrientationControl _orientationControl;
    
    public PointerControlPanel( String title, Color titleColor ) {
        super();
        
        // Title
        JLabel titleLabel = new JLabel( title );
        titleLabel.setFont( new PhetFont( 14, true /* bold */ ) );
        titleLabel.setForeground( titleColor );
        
        // Position control (display only)
        _positionControl = new PositionControl();
        
        // Orientation control
        _orientationControl = new OrientationControl();
        
        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        this.setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        layout.setFill( GridBagConstraints.HORIZONTAL );
        int row = 0;
        int column = 0;
        layout.addComponent( titleLabel, row++, column );
        layout.addComponent( _positionControl, row++, column );
        layout.addComponent( _orientationControl, row++, column );
    }
    
    public PositionControl getPositionControl() {
        return _positionControl;
    }
    
    public OrientationControl getOrientationControl() {
        return _orientationControl;
    }
}
