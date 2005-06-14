/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.components.VerticalLayoutPanel;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.SwingUtils;
import edu.colorado.phet.qm.model.RectangularPotential;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 11, 2005
 * Time: 8:54:38 PM
 * Copyright (c) Jun 11, 2005 by Sam Reid
 */

public class RectangularPotentialGraphic extends RectangleGraphic {
    private RectangularPotential potential;
    private DecimalFormat format = new DecimalFormat( "0.00" );
    private PhetTextGraphic potDisplay;

    public RectangularPotentialGraphic( SchrodingerPanel component, final RectangularPotential potential ) {
        super( component, potential, new Color( 255, 30, 0, 45 ) );
        this.potential = potential;

        potDisplay = new PhetTextGraphic( component, new Font( "Lucida Sans", Font.BOLD, 14 ), "", Color.blue );
        potDisplay.setIgnoreMouse( true );
        addGraphic( potDisplay );
        potential.addObserver( new SimpleObserver() {
            public void update() {
                RectangularPotentialGraphic.this.update();
            }
        } );
        super.getAreaGraphic().addMouseInputListener( new MouseInputAdapter() {
            // implements java.awt.event.MouseListener
            public void mousePressed( MouseEvent e ) {
                if( SwingUtilities.isRightMouseButton( e ) ) {
                    changePotential();
                }
            }
        } );
        update();
    }

    private void changePotential() {
        final JDialog dialog = new JDialog( (Frame)SwingUtilities.getWindowAncestor( getComponent() ), "Change Potential" );
        VerticalLayoutPanel content = new VerticalLayoutPanel();
        JTextField old = new JTextField();
        old.setBorder( BorderFactory.createTitledBorder( "Current Potential" ) );
        old.setText( format.format( potential.getPotential() ) );
        old.setEditable( false );

        final JTextField newOne = new JTextField();
        newOne.setBorder( BorderFactory.createTitledBorder( "Change to" ) );
        newOne.setText( format.format( potential.getPotential() ) );
        newOne.requestFocus();
        newOne.setSelectionStart( 0 );
        newOne.setSelectionEnd( newOne.getText().length() );

        content.addFullWidth( old );
        content.addFullWidth( newOne );
        dialog.setContentPane( content );

        JButton apply = new JButton( "Apply" );
        apply.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    double newVal = Double.parseDouble( newOne.getText() );
                    potential.setPotential( newVal );
                    update();
                }
                catch( Exception ex ) {
                    JOptionPane.showMessageDialog( getComponent(), ex.getMessage() );
                }
            }
        } );

        JButton close = new JButton( "Close Window" );
        close.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dialog.hide();
                dialog.dispose();
            }
        } );
        content.add( apply );
        content.add( close );
        dialog.pack();
        SwingUtils.centerDialogInParent( dialog );
        dialog.show();
    }

    private void update() {
        Rectangle modelRect = potential.getBounds();
        ColorGrid grid = super.getColorGrid();
        Rectangle viewRect = grid.getViewRectangle( modelRect );

        double probPercent = potential.getPotential();
        String formatted = format.format( probPercent );
        potDisplay.setText( formatted + " pJ" );
        potDisplay.setLocation( (int)viewRect.getX(), (int)viewRect.getY() );
    }

}
