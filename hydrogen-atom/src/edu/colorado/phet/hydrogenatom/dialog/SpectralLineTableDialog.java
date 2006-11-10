/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.hydrogenatom.model.BohrModel;


public class SpectralLineTableDialog extends JDialog {

    private static final DecimalFormat WAVELENGTH_FORMATTER = new DecimalFormat( "0.0" );
    
    public SpectralLineTableDialog( Frame owner ) {
        super( owner, SimStrings.get( "dialog.spectralLineTable.title") );
        setResizable( false );
        
        JPanel inputPanel = createInputPanel();
        JPanel actionsPanel = createActionsPanel();
        
        VerticalLayoutPanel panel = new VerticalLayoutPanel();
        panel.setFillHorizontal();
        panel.add( inputPanel );
        panel.add( new JSeparator() );
        panel.add( actionsPanel );
        
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createInputPanel() {
        
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        layout.setInsets( new Insets( 0, 10, 0, 10 ) );
        panel.setLayout( layout );
        int row = 0;
        int col = 0;
        
        layout.addComponent( new JLabel( SimStrings.get( "dialog.spectralLineTable.state" ) ), row, col++ );
        layout.addComponent( new JLabel( SimStrings.get( "dialog.spectralLineTable.state" ) ), row, col++ );
        layout.addComponent( new JLabel( SimStrings.get( "dialog.spectralLineTable.wavelength" ) ), row, col++ );
        row++;
        col = 0;
        layout.addComponent( new JSeparator(), row, col, 3, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL );
        row++;

        final int groundState = BohrModel.getGroundState();
        final int numberOfStates = BohrModel.getNumberOfStates();
        final int maxState = groundState + numberOfStates - 1;
        for ( int m = groundState; m < maxState; m++ ) {
            for ( int n = m + 1; n <= maxState; n++ ) {
                double wavelength = BohrModel.getWavelengthAbsorbed( m, n );
                String s = WAVELENGTH_FORMATTER.format( wavelength );
                col = 0;
                layout.addComponent( new JLabel( Integer.toString( m ) ), row, col++ );
                layout.addComponent( new JLabel( Integer.toString( n ) ), row, col++ );
                layout.addAnchoredComponent( new JLabel( s ), row, col++, GridBagConstraints.EAST );
                row++;
            }
        }
        
        return panel;
    }
    
    private JPanel createActionsPanel() {
        JButton closeButton = new JButton( SimStrings.get( "button.close" ) );
        closeButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                dispose();
            }
        } );

        JPanel innerPanel = new JPanel( new GridLayout( 1, 1, 0, 0 ) );
        innerPanel.add( closeButton );

        JPanel actionPanel = new JPanel( new FlowLayout() );
        actionPanel.add( innerPanel );

        return actionPanel;
    }
}
