/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
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
    private RampPanel rampPanel;

    public OverheatButton( final RampPanel rampPanel, final RampPhysicalModel rampPhysicalModel, RampModule module ) {
        this.rampPhysicalModel = rampPhysicalModel;
        this.rampPanel = rampPanel;
        ShadowHTMLGraphic shadowHTMLGraphic = new ShadowHTMLGraphic( "Warning: overheated." );
        shadowHTMLGraphic.setColor( Color.red );
        shadowHTMLGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 22 ) );
        addChild( shadowHTMLGraphic );
        JButton overheat = new JButton( "Remove Heat" );
        overheat.setFont( RampFontSet.getFontSet().getNormalButtonFont() );
        overheat.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                rampPanel.getRampModule().clearHeat();
            }
        } );
        PSwing buttonGraphic = new PSwing( rampPanel, overheat );
        buttonGraphic.setOffset( 0, shadowHTMLGraphic.getHeight() );
        addChild( buttonGraphic );
        module.getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                update();
            }
        } );
        update();
        buttonGraphic.setOffset( 0, shadowHTMLGraphic.getFullBounds().getHeight() + 5 );
    }

    private void update() {

        double max = rampPanel.getOverheatEnergy();
//        System.out.println( "<----OverheatButton.update" );
//        System.out.println( "rampPhysicalModel.getThermalEnergy() = " + rampPhysicalModel.getThermalEnergy() );
//        System.out.println( "max=" + max );
//        System.out.println( "" );
        if( rampPhysicalModel.getThermalEnergy() >= max && !getVisible() ) {
            setVisible( true );
            setPickable( true );
            setChildrenPickable( true );
//            Point viewLocation = rampPanel.getRampGraphic().getViewLocation( 0 );
//            Point2D viewLoc=rampPanel.getRampGraphic().
//            setOffset( viewLocation.x, viewLocation.y + 10 );

            setOffset( RampPanel.getDefaultRenderSize().width / 2, 50 );
        }
        else if( rampPhysicalModel.getThermalEnergy() < max ) {
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
