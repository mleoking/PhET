/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.glaciers.model.Glacier;

/**
 * Provides developers with ability to tweak various model constants.
 * This panel is for debugging purposes and is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ModelConstantsPanel extends JPanel {
    
    private static final double DELTA_ACCELERATION_M = 1;
    private static final double DELTA_ACCELERATION_B = 10;
    
    // controls
    private final double _defaultAccelerationM, _defaultAccelerationB;
    
    public ModelConstantsPanel( final Glacier glacier ) {
        super();

        // acc_m
        _defaultAccelerationM = glacier.debug_getAccelerationM();
        final double mDelta = DELTA_ACCELERATION_M;
        final DoubleSpinner mSpinner = new DoubleSpinner( _defaultAccelerationM, _defaultAccelerationM - mDelta, _defaultAccelerationM + mDelta, 0.00001, "0.00000", new Dimension( 100, 22 ) );
        mSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                glacier.debug_setAccelerationM( mSpinner.getValue() );
            }
        });
        
        // acc_b
        _defaultAccelerationB = glacier.debug_getAccelerationB();
        final double bDelta = DELTA_ACCELERATION_B;
        final DoubleSpinner bSpinner = new DoubleSpinner( _defaultAccelerationB, _defaultAccelerationB - bDelta, _defaultAccelerationB + bDelta, 0.01, "0.00", new Dimension( 100, 22 ) );
        bSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                glacier.debug_setAccelerationB( bSpinner.getValue() );
            }
        });
        
        // Reset button, restores defaults
        JButton resetButton = new JButton( "Reset" );
        resetButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                mSpinner.setValue( _defaultAccelerationM );
                glacier.debug_setAccelerationM( _defaultAccelerationM );
                bSpinner.setValue( _defaultAccelerationB );
                glacier.debug_setAccelerationB( _defaultAccelerationB );
            }
        });
        
        // layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 3, 10, 3, 10 ) ); // top, left, bottom, right
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addAnchoredComponent( new JLabel( "See model.txt for details." ), row, col++, 2, 1, GridBagConstraints.WEST );
        row++;
        col = 0;
        layout.addAnchoredComponent( new JLabel( "acc_m:" ), row, col++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( mSpinner, row, col++, GridBagConstraints.WEST );
        row++;
        col = 0;
        layout.addAnchoredComponent( new JLabel( "acc_b:" ), row, col++, GridBagConstraints.EAST );
        layout.addAnchoredComponent( bSpinner, row, col++, GridBagConstraints.WEST );
        row++;
        col = 0;
        layout.addAnchoredComponent( resetButton, row, col++, 2, 1, GridBagConstraints.CENTER );
    }
    
    public void cleanup() {
        // do nothing
    }
}
