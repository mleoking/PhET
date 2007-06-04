/* Copyright 2007, University of Colorado */
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkStrings;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
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

public class ReturnSkaterButtonNode extends PhetPNode {
    private EnergySkateParkModule module;
    private PSwingCanvas canvas;
    
    private SkaterNode skaterNode;
    private PNode buttonNode;
    private JButton bringBackSkater = new JButton( "" );

    public ReturnSkaterButtonNode( PSwingCanvas canvas, final EnergySkateParkModule module, SkaterNode skaterNode ) {
        this.canvas = canvas;
        this.skaterNode = skaterNode;
        this.module = module;
        SwingUtils.fixButtonOpacity( bringBackSkater );
        bringBackSkater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.resetSkater();
            }
        } );
        buttonNode = new PhetPNode( new PSwing( bringBackSkater ) );
        addChild( buttonNode );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                update();
            }
        } );
        update();
    }

    private void updateText() {
        if( skaterNode != null ) {
            bringBackSkater.setText( EnergySkateParkStrings.getString( "controls.bring-back" ) + " " + skaterNode.getBody().getSkaterCharacter().getName() );
        }
    }

    private void update() {
        updateText();
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
    }

    private void updateLocation() {
        buttonNode.setOffset( canvas.getWidth() / 2 - buttonNode.getFullBounds().getWidth() / 2, canvas.getHeight() / 2 - buttonNode.getFullBounds().getHeight() / 2 );
    }

    private Rectangle getVisibleBounds() {
        return new Rectangle( module.getEnergySkateParkSimulationPanel().getSize() );
    }

    public void setSkaterNode( SkaterNode skaterNode ) {
        this.skaterNode = skaterNode;
        update();
    }
}
