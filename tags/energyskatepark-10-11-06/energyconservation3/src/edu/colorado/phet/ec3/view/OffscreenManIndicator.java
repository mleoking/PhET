/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.view;

import edu.colorado.phet.ec3.EnergySkateParkModule;
import edu.colorado.phet.ec3.EnergySkateParkStrings;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Oct 27, 2005
 * Time: 9:18:57 AM
 * Copyright (c) Oct 27, 2005 by Sam Reid
 */

public class OffscreenManIndicator extends PhetPNode {
    private PSwingCanvas canvas;
    private BodyGraphic body;
    private EnergySkateParkModule module;
    private PSwing buttonNode;
    private ConnectorGraphic connectorGraphic;

    public OffscreenManIndicator( PSwingCanvas canvas, final EnergySkateParkModule ec3Module, BodyGraphic body ) {
        this.canvas = canvas;
        this.body = body;
        this.module = ec3Module;
        JButton bringBackSkater = new JButton( EnergySkateParkStrings.getString( "bring.back.the.skater" ) );
        bringBackSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ec3Module.resetSkater();
            }
        } );
        buttonNode = ( new PSwing( canvas, bringBackSkater ) );
        addChild( buttonNode );
//        connectorGraphic = new ConnectorGraphic( this, body == null ? this : (PNode)body );
    }

    public void setBodyGraphic( BodyGraphic body ) {
        this.body = body;
//        connectorGraphic.set
        update();
    }

    public void update() {
        updateVisible();
        updateLocation();
    }

    private void updateVisible() {
        if( body == null ) {
            setVisible( false );
        }
        else {
            PBounds s = body.getGlobalFullBounds();
            Rectangle visBounds = getVisibleBounds();
//            setVisible( !visBounds.contains( s ) );
            boolean offscreen = visBounds.intersection( s.getBounds() ).isEmpty();
//            System.out.println( "offscreen = " + offscreen );
            setVisible( offscreen );
        }
    }

    private void updateLocation() {
        buttonNode.setOffset( canvas.getWidth() / 2 - buttonNode.getFullBounds().getWidth() / 2, canvas.getHeight() / 2 - buttonNode.getFullBounds().getHeight() / 2 );
    }

    private Rectangle getVisibleBounds() {
        return new Rectangle( module.getEnergyConservationCanvas().getSize() );
    }

}
