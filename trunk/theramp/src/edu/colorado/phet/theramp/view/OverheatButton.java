/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.common.model.ModelElement;
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
    private RampPhysicalModel rampPhysicalModel;
    private double max;
    private RampPanel rampPanel;

    public OverheatButton( final RampPanel rampPanel, final RampPhysicalModel rampPhysicalModel, double maxDisplayableEnergy, RampModule module ) {
        super();
        this.rampPhysicalModel = rampPhysicalModel;
        this.rampPanel = rampPanel;
        ShadowHTMLGraphic shadowHTMLGraphic = new ShadowHTMLGraphic( "Warning: overheated." );
        shadowHTMLGraphic.setColor( Color.red );
        shadowHTMLGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
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
//        rampPhysicalModel.addListener( new RampPhysicalModel.Listener() {
//            public void appliedForceChanged() {
//            }
//
//            public void zeroPointChanged() {
//            }
//
//            public void stepFinished() {
//                update();
//            }
//        } );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );

        this.max = maxDisplayableEnergy * 0.8;
        update();

        buttonGraphic.setOffset( 0, shadowHTMLGraphic.getFullBounds().getHeight() + 5 );
    }

    private void update() {
        if( rampPhysicalModel.getThermalEnergy() >= max && !getVisible() ) {
            setVisible( true );
            Point viewLocation = rampPanel.getRampGraphic().getViewLocation( 0 );
            setOffset( viewLocation.x, viewLocation.y + 10 );
        }
        else if( rampPhysicalModel.getThermalEnergy() < max ) {
            setVisible( false );
        }
    }
}
