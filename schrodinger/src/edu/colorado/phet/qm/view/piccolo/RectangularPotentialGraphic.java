/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.SchrodingerLookAndFeel;
import edu.colorado.phet.qm.model.Potential;
import edu.colorado.phet.qm.model.potentials.RectangularPotential;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;
import edu.umd.cs.piccolo.nodes.PText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private PText potDisplay;
    private PSwing closeGraphic;
//    private PhetGraphic editGraphic;

    public RectangularPotentialGraphic( SchrodingerPanel component, final RectangularPotential potential ) {
        super( component, potential, new Color( 255, 30, 0, 45 ) );
        this.potential = potential;

        potDisplay = new PText( "" );
        //todo piccolo
        //component, new Font( "Lucida Sans", Font.BOLD, 14 ),
        //, Color.blue
        potDisplay.setPickable( false );
        potDisplay.setChildrenPickable( false );
        addChild( potDisplay );
        potential.addObserver( new SimpleObserver() {
            public void update() {
                RectangularPotentialGraphic.this.update();
            }
        } );
//        super.getAreaGraphic().addMouseInputListener( new MouseInputAdapter() {
//            // implements java.awt.event.MouseListener
//            public void mousePressed( MouseEvent e ) {
//                if( SwingUtilities.isRightMouseButton( e ) ) {
//                    changePotential();
//                }
//            }
//        } );

        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                remove();
            }
        } );
//        closeGraphic = PhetJComponent.newInstance( component, closeButton );
        closeGraphic = new PSwing( component, closeButton );
        addChild( closeGraphic );
        closeGraphic.setOffset( -closeGraphic.getWidth() - 2, 0 );

//        JButton editButton = new JButton( "Edit" );
//        editButton.setMargin( new Insets( 1, 1, 1, 1 ) );
//        editButton.setFont( new Font( "Lucida Sans", Font.PLAIN, 12 ) );
//        editButton.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                changePotential();
//            }
//        } );
//        editGraphic = PhetJComponent.newInstance( getSchrodingerPanel(), editButton );
//        addGraphic( editGraphic );

        update();
    }

    private void remove() {
        super.getSchrodingerPanel().getSchrodingerModule().removePotential( this );
    }

//    private void changePotential() {
//        final JDialog dialog = new JDialog( (Frame)SwingUtilities.getWindowAncestor( getComponent() ), "Change Potential" );
//        VerticalLayoutPanel content = new VerticalLayoutPanel();
//        JLabel explanation = new JLabel( "<html>Potentials are represented here<br>in base E (~2.7)<br>Press Apply after changing the value.</html>" );
//        content.addFullWidth( explanation );
//
//        JTextField old = new JTextField();
//        old.setBorder( BorderFactory.createTitledBorder( "Current Potential" ) );
//        old.setText( format.format( Math.log( potential.getPotential() ) ) );
//        old.setEditable( false );
//
//        final JTextField newOne = new JTextField();
//        newOne.setBorder( BorderFactory.createTitledBorder( "Change to" ) );
//
//        newOne.setText( format.format( Math.log( potential.getPotential() ) ) );
//        newOne.requestFocus();
//        newOne.setSelectionStart( 0 );
//        newOne.setSelectionEnd( newOne.getText().length() );
//
//        content.addFullWidth( old );
//        content.addFullWidth( newOne );
//        dialog.setContentPane( content );
//
//        JButton apply = new JButton( "Apply" );
//        apply.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                try {
//                    double newVal = Double.parseDouble( newOne.getText() );
//                    newVal = Math.pow( Math.E, newVal );
//                    potential.setPotential( newVal );
//                    update();
//                }
//                catch( Exception ex ) {
//                    JOptionPane.showMessageDialog( getComponent(), ex.getMessage() );
//                }
//            }
//        } );
//
//        JButton close = new JButton( "Close Window" );
//        close.addActionListener( new ActionListener() {
//            public void actionPerformed( ActionEvent e ) {
//                dialog.hide();
//                dialog.dispose();
//            }
//        } );
//        content.add( apply );
//        content.add( close );
//        dialog.pack();
//        SwingUtils.centerDialogInParent( dialog );
//        dialog.show();
//    }

    private void update() {
        Rectangle modelRect = potential.getBounds();
        Rectangle viewRect = super.getViewRectangle( modelRect );
//        double probPercent = potential.getPotential();
//        String formatted = format.format( probPercent );
//
//        potDisplay.setText( formatted + " pJ" );
        potDisplay.setText( "" );
        potDisplay.setOffset( (int)viewRect.getX(), (int)viewRect.getY() );
        closeGraphic.setOffset( (int)viewRect.getX() - closeGraphic.getWidth() - 2, (int)viewRect.getY() );
//        editGraphic.setLocation( closeGraphic.getX(), closeGraphic.getY() + closeGraphic.getHeight() + 2 );
    }

    public Potential getPotential() {
        return potential;
    }
}
