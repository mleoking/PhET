/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.piccolo.ShadowHTMLGraphic;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.theramp.RampModule;
import edu.colorado.phet.theramp.TheRampStrings;
import edu.colorado.phet.theramp.model.RampPhysicalModel;
import edu.umd.cs.piccolo.PNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Jun 6, 2005
 * Time: 11:34:54 PM
 * Copyright (c) Jun 6, 2005 by Sam Reid
 */

public class OverheatButton extends PNode {
    private RampPhysicalModel rampPhysicalModel;
    private RampPanel rampPanel;
    private RampModule module;

    public OverheatButton( final RampPanel rampPanel, final RampPhysicalModel rampPhysicalModel, RampModule module ) {
        this.module = module;
        this.rampPhysicalModel = rampPhysicalModel;
        this.rampPanel = rampPanel;
        ShadowHTMLGraphic shadowHTMLGraphic = new ShadowHTMLGraphic( TheRampStrings.getString( "overheated" ) );
        shadowHTMLGraphic.setColor( Color.red );
        shadowHTMLGraphic.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        addChild( shadowHTMLGraphic );
        JButton overheat = new JButton( TheRampStrings.getString( "cool.ramp" ) );
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
        if( rampPhysicalModel.getThermalEnergy() >= max && !getVisible() && module.numMaximizedBarGraphs() > 0 ) {
            setVisible( true );
            setPickable( true );
            setChildrenPickable( true );
//            setOffset( RampPanel.getDefaultRenderSize().width / 2, 50 );
            Rectangle2D r = rampPanel.getBarGraphSuite().getGlobalFullBounds();
//            System.out.println( "r = " + r );
            globalToLocal( r );

//            System.out.println( "globToLoc r=" + r );
            localToParent( r );
//            System.out.println( "locToPar r="+r );
//            setOffset( rampPanel.getBarGraphSuite().getXOffset(), rampPanel.getBarGraphSuite().getYOffset() + 100 );
            setOffset( r.getX() + 20, rampPanel.getHeight() / 2 );
        }
        else if( rampPhysicalModel.getThermalEnergy() < max ) {
            setVisible( false );
            setPickable( false );
            setChildrenPickable( false );
        }
    }
}
