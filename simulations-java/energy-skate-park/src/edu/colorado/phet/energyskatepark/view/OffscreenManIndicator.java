/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.nodes.ConnectorGraphic;
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
    private SkaterNode bodyNode;
    private EnergySkateParkModule module;
    private PSwing buttonNode;
    private ConnectorGraphic connectorGraphic;

    public OffscreenManIndicator( PSwingCanvas canvas, final EnergySkateParkModule ec3Module, SkaterNode body ) {
        this.canvas = canvas;
        this.bodyNode = body;
        this.module = ec3Module;
        JButton bringBackSkater = new JButton( EnergySkateParkStrings.getString( "bring.back.the.skater" ) );
        bringBackSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ec3Module.resetSkater();
            }
        } );
        buttonNode = ( new PSwing(bringBackSkater ) );
        addChild( buttonNode );
    }

    public void setBodyGraphic( SkaterNode body ) {
        this.bodyNode = body;
        update();
    }

    public void update() {
        updateVisible();
        updateLocation();
    }

    private void updateVisible() {
        if( bodyNode == null ) {
            setVisible( false );
        }
        else {
            setVisible( !getVisibleBounds().contains( bodyNode.getGlobalFullBounds() ) );
        }
    }

    private void updateLocation() {
        buttonNode.setOffset( canvas.getWidth() / 2 - buttonNode.getFullBounds().getWidth() / 2, canvas.getHeight() / 2 - buttonNode.getFullBounds().getHeight() / 2 );
    }

    private Rectangle getVisibleBounds() {
        return new Rectangle( module.getEnergyConservationCanvas().getSize() );
    }

}
