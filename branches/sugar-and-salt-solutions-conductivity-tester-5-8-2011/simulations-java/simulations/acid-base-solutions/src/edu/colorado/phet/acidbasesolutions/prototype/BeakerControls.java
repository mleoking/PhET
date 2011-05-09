// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.acidbasesolutions.prototype;

import java.awt.GridBagConstraints;

import javax.swing.JFrame;
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
 * Controls for the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class BeakerControls extends JPanel {
    
    private final Beaker beaker;
    private final LinearValueControl widthControl, heightControl;
    
    public BeakerControls( final JFrame parentFrame, final Beaker beaker ) {
        setBorder( new TitledBorder( "Beaker" ) );
        
        this.beaker = beaker;
        beaker.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                updateControls();
            }
        });
        
        // beaker width
        IntegerRange widthRange = MGPConstants.BEAKER_WIDTH_RANGE;
        widthControl = new LinearValueControl( widthRange.getMin(), widthRange.getMax(), "width:", "##0", "" );
        widthControl.setBorder( new EtchedBorder() );
        widthControl.setUpDownArrowDelta( 1 );
        widthControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beaker.setWidth( (int)widthControl.getValue() );
            }
        });

        // beaker height
        IntegerRange heightRange = MGPConstants.BEAKER_HEIGHT_RANGE;
        heightControl = new LinearValueControl( heightRange.getMin(), heightRange.getMax(), "height:", "##0", "" );
        heightControl.setBorder( new EtchedBorder() );
        heightControl.setUpDownArrowDelta( 1 );
        heightControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                beaker.setHeight( (int)heightControl.getValue() );
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
        layout.addComponent( widthControl, row, column++ );
        row++;
        column = 0;
        layout.addComponent( heightControl, row, column++ );
        
        // default state
        updateControls();
    }
    
    private void updateControls() {
        widthControl.setValue( beaker.getWidth() );
        heightControl.setValue( beaker.getHeight() );
    }
}
