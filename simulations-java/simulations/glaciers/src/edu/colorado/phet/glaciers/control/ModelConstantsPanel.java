/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.control;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    
    // controls
    private final double _defaultAccelerationM, _defaultAccelerationB;
    
    public ModelConstantsPanel( final Glacier glacier ) {
        super();

        // acc_m
        _defaultAccelerationM = glacier.debug_getAccelerationM();
        final DoubleSpinner mSpinner = new DoubleSpinner( _defaultAccelerationM, 0.01, 0.05, 0.00001, "0.00000", new Dimension( 100, 22 ) );
        mSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                glacier.debug_setAccelerationM( mSpinner.getValue() );
            }
        });
        JLabel mRangeLabel = new JLabel( "( 0.01, 0.05 )" ); //WARNING! hard-coded label
        
        // acc_b
        _defaultAccelerationB = glacier.debug_getAccelerationB();
        final DoubleSpinner bSpinner = new DoubleSpinner( _defaultAccelerationB, -136, -100, 0.01, "0.00", new Dimension( 100, 22 ) );
        bSpinner.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                glacier.debug_setAccelerationB( bSpinner.getValue() );
            }
        });
        JLabel bRangeLabel = new JLabel( "( -136, -100 )" ); //WARNING! hard-coded label
        
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
        layout.setInsets( new Insets( 2, 3, 2, 3 ) ); // top, left, bottom, right
        layout.setAnchor( GridBagConstraints.WEST );
        this.setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( new JLabel( "See model.txt for details." ), row, col++, 3, 1 );
        row++;
        col = 0;
        layout.addComponent( Box.createVerticalStrut( 10 ), row, col++, 3, 1 );
        row++;
        col = 0;
        layout.addComponent( new JLabel( "<html><u>name</u></html>" ), row, col++ );
        layout.addComponent( new JLabel( "<html><u>value</u></html>" ), row, col++ );
        layout.addComponent( new JLabel( "<html><u>range (min, max)</u></html>" ), row, col++ );
        row++;
        col = 0;
        layout.addAnchoredComponent( new JLabel( "acc_m:" ), row, col++, GridBagConstraints.EAST );
        layout.addComponent( mSpinner, row, col++ );
        layout.addComponent( mRangeLabel, row, col++ );
        row++;
        col = 0;
        layout.addAnchoredComponent( new JLabel( "acc_b:" ), row, col++, GridBagConstraints.EAST);
        layout.addComponent( bSpinner, row, col++ );
        layout.addComponent( bRangeLabel, row, col++ );
        row++;
        col = 0;
        layout.addComponent( Box.createVerticalStrut( 10 ), row, col++, 3, 1 );
        row++;
        col = 0;
        layout.addAnchoredComponent( resetButton, row, col++, 3, 1, GridBagConstraints.CENTER );
    }
    
    public void cleanup() {
        // do nothing
    }
}
