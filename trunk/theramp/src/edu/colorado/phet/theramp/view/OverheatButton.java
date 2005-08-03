/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.model.RampModel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 11:34:54 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class OverheatButton extends PNode {
    private RampModel rampModel;
    private double max;
    private RampPanel rampPanel;

    public OverheatButton( final RampPanel rampPanel, final RampModel rampModel, double maxDisplayableEnergy ) {
        super();
        this.rampModel = rampModel;
        this.rampPanel = rampPanel;
        ShadowHTMLGraphic shadowHTMLGraphic = new ShadowHTMLGraphic( "Warning: overheated." );
        shadowHTMLGraphic.setColor( Color.red );
        shadowHTMLGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 24 ) );
        addChild( shadowHTMLGraphic );
        JButton overheat = new JButton( "Remove Heat" );
        overheat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.getRampModule().clearHeat();
            }
        } );
        PSwing buttonGraphic = new PSwing( rampPanel, overheat );
        buttonGraphic.setOffset( 0, shadowHTMLGraphic.getHeight() );
        addChild( buttonGraphic );
        rampModel.addListener( new RampModel.Listener() {
            public void appliedForceChanged() {
            }

            public void zeroPointChanged() {
            }

            public void stepFinished() {
                update();
            }
        } );
        this.max = maxDisplayableEnergy * 0.8;
        update();

        buttonGraphic.setOffset( 0, shadowHTMLGraphic.getFullBounds().getHeight() + 5 );
    }

    private void update() {
        if( rampModel.getThermalEnergy() >= max && !getVisible() ) {
            setVisible( true );
            Point viewLocation = rampPanel.getRampGraphic().getViewLocation( 0 );
            setOffset( viewLocation.x, viewLocation.y + 10 );
        }
        else if( rampModel.getThermalEnergy() < max ) {
            setVisible( false );
        }
    }
}
