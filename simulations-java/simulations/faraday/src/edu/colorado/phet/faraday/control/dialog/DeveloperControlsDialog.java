/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.control.dialog;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.faraday.view.ElectromagnetGraphic;
import edu.colorado.phet.faraday.view.PickupCoilGraphic;

/**
 * DeveloperControlsDialog is a dialog that contains "developer only" controls.
 * These controls will not be available to the user, and are not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsDialog extends JDialog {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    public DeveloperControlsDialog( Frame owner ) {
        super( owner, "Developer Controls" );
        
        setResizable( false );
        setModal( false );

        JPanel inputPanel = createInputPanel();

        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }

    private JPanel createInputPanel() {

        // Pickup Coil sample points
        final JCheckBox showSamplePointsCheckBox = new JCheckBox( "Show pickup coil sample points" );
        showSamplePointsCheckBox.setSelected( PickupCoilGraphic.DEBUG_DRAW_PICKUP_SAMPLE_POINTS );
        showSamplePointsCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PickupCoilGraphic.DEBUG_DRAW_PICKUP_SAMPLE_POINTS = showSamplePointsCheckBox.isSelected();
            }
        } );
        add( showSamplePointsCheckBox );
        
        // Pickup Coil flux display
        final JCheckBox displayFluxCheckBox = new JCheckBox( "Display pickup coil flux" );
        displayFluxCheckBox.setSelected( PickupCoilGraphic.DEBUG_DISPLAY_FLUX );
        displayFluxCheckBox.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PickupCoilGraphic.DEBUG_DISPLAY_FLUX = displayFluxCheckBox.isSelected();
            }
        } );
        add( displayFluxCheckBox );
        
        // Electromagnet shape
        final JCheckBox showElectromagnetShape = new JCheckBox( "Show electromagnet model shape" );
        showElectromagnetShape.setSelected( ElectromagnetGraphic.DEBUG_DRAW_ELECTROMAGNET_MODEL_SHAPE );
        showElectromagnetShape.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ElectromagnetGraphic.DEBUG_DRAW_ELECTROMAGNET_MODEL_SHAPE = showElectromagnetShape.isSelected();
            }
        } );
        add( showElectromagnetShape ); 

        // Layout
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 3, 5, 3, 5 ) );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( showSamplePointsCheckBox, row++, column );
        layout.addComponent( displayFluxCheckBox, row++, column );
        layout.addComponent( showElectromagnetShape, row++, column );

        return panel;
    }
}
