/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.umd.cs.piccolo.PNode;
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
 */

public class OffscreenManIndicatorNode extends PhetPNode {
    private PSwingCanvas canvas;
    private SkaterNode skaterNode;
    private EnergySkateParkModule module;
    private PNode buttonNode;
    private JButton bringBackSkater = new JButton( "" );
    private EnergySkateParkModule.Listener moduleListener;
    private Body.ListenerAdapter bodyListener;

    public OffscreenManIndicatorNode( PSwingCanvas canvas, final EnergySkateParkModule module, SkaterNode skaterNode ) {
        this.canvas = canvas;
        this.skaterNode = skaterNode;
        this.module = module;
        bringBackSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.resetSkater();
            }
        } );
        buttonNode = new PhetPNode( new PSwing( bringBackSkater ) );
        addChild( buttonNode );
        moduleListener = new EnergySkateParkModule.Listener() {
            public void skaterCharacterChanged() {
                updateText();
            }
        };
        bodyListener = new Body.ListenerAdapter() {
            public void positionAngleChanged() {
                update();
            }
        };
        module.addListener( moduleListener );
        if( skaterNode != null ) {
            skaterNode.getBody().addListener( bodyListener );
        }

        update();
    }

    public void delete() {
        module.removeListener( moduleListener );
        skaterNode.getBody().removeListener( bodyListener );
    }

    private void updateText() {
        bringBackSkater.setText( EnergySkateParkStrings.getString( "controls.bring-back" ) + " " + module.getSkaterCharacter().getName() );
    }

    public void update() {
        updateVisible();
        updateLocation();
    }

    private void updateVisible() {
        if( skaterNode == null ) {
            setVisible( false );
        }
        else {
            setVisible( !getVisibleBounds().contains( skaterNode.getGlobalFullBounds() ) );
        }
//        System.out.println( "OffscreenManIndicatorNode.updateVisible = "+getVisible() );
    }

    private void updateLocation() {
        buttonNode.setOffset( canvas.getWidth() / 2 - buttonNode.getFullBounds().getWidth() / 2, canvas.getHeight() / 2 - buttonNode.getFullBounds().getHeight() / 2 );
    }

    private Rectangle getVisibleBounds() {
        return new Rectangle( module.getEnergyConservationCanvas().getSize() );
    }

    public void setSkaterNode( SkaterNode skaterNode ) {
        if( this.skaterNode != null ) {
            this.skaterNode.getBody().removeListener( bodyListener );
        }
        this.skaterNode = skaterNode;
        this.skaterNode.getBody().addListener( bodyListener );
        update();
    }
}
