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

    public RectangularPotentialGraphic( SchrodingerPanel component, final RectangularPotential potential ) {
        super( component, potential, new Color( 255, 30, 0, 45 ) );
        this.potential = potential;

        potDisplay = new PText( "" );
        potDisplay.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        potDisplay.setTextPaint( Color.blue );
        potDisplay.setPickable( false );
        potDisplay.setChildrenPickable( false );
        addChild( potDisplay );
        potential.addObserver( new SimpleObserver() {
            public void update() {
                RectangularPotentialGraphic.this.update();
            }
        } );
        JButton closeButton = SchrodingerLookAndFeel.createCloseButton();
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                remove();
            }
        } );
        closeGraphic = new PSwing( component, closeButton );
        addChild( closeGraphic );
        closeGraphic.setOffset( -closeGraphic.getWidth() - 2, 0 );
        update();
    }

    private void remove() {
        super.getSchrodingerPanel().getSchrodingerModule().removePotential( this );
    }

    private void update() {
        Rectangle modelRect = potential.getBounds();
        Rectangle viewRect = super.getViewRectangle( modelRect );
        potDisplay.setText( "" );
        potDisplay.setOffset( (int)viewRect.getX(), (int)viewRect.getY() );
        closeGraphic.setOffset( (int)viewRect.getX() - closeGraphic.getWidth() - 2, (int)viewRect.getY() );
    }

    public Potential getPotential() {
        return potential;
    }
}
