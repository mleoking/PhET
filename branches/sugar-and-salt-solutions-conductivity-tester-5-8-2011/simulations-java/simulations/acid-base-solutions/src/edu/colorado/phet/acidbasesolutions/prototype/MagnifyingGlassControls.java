// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.util.IntegerRange;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;

/**
 * Controls for the magnifying glass.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class MagnifyingGlassControls extends JPanel {
    
    private final MagnifyingGlass magnifyingGlass;
    private final LinearValueControl diameterControl;
    
    public MagnifyingGlassControls( final MagnifyingGlass magnifyingGlass ) {
        setBorder( new TitledBorder( "Magnifying glass" ) );
        
        this.magnifyingGlass = magnifyingGlass;
        magnifyingGlass.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        IntegerRange diameterRange = MGPConstants.MAGNIFYING_GLASS_DIAMETER_RANGE;
        diameterControl = new LinearValueControl( diameterRange.getMin(), diameterRange.getMax(), "diameter:", "##0", "" );
        diameterControl.setBorder( new EtchedBorder() );
        diameterControl.setUpDownArrowDelta( 1 );
        diameterControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                magnifyingGlass.setDiameter( (int)diameterControl.getValue() );
            }
        });
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        layout.setAnchor( GridBagConstraints.WEST );
        int row = 0;
        int column = 0;
        layout.addComponent( new JLabel( "position: drag to move" ), row, column++ );
        row++;
        column = 0;
        layout.addComponent( diameterControl, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        diameterControl.setValue( magnifyingGlass.getDiameter() );
    }
}
