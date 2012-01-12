// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.simsharing.SimSharingTextButtonNode;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.playAreaReturnSkaterButton;

/**
 * User: Sam Reid
 * Date: Oct 27, 2005
 * Time: 9:18:57 AM
 */

public class ReturnSkaterButtonNode extends PhetPNode {
    private final AbstractEnergySkateParkModule module;
    private final PSwingCanvas canvas;

    private SkaterNode skaterNode;
    private final PNode buttonNode;

    public ReturnSkaterButtonNode( PSwingCanvas canvas, final AbstractEnergySkateParkModule module, SkaterNode skaterNode ) {
        this.canvas = canvas;
        this.skaterNode = skaterNode;
        this.module = module;
        ButtonNode returnSkaterButton = new SimSharingTextButtonNode( playAreaReturnSkaterButton, EnergySkateParkResources.getString( "controls.reset-character" ) ) {{

            //Changed "Return Skater" button to be orange instead of yellow to distinguish from "Reset all" Button
            setBackground( Color.ORANGE );
        }};
        returnSkaterButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                module.returnOrResetSkater();
            }
        } );
        buttonNode = new PhetPNode( returnSkaterButton );
        addChild( buttonNode );
        module.getEnergySkateParkModel().addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void primaryBodyChanged() {
                update();
            }
        } );
        update();
        canvas.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                update();
            }
        } );
    }

    private void update() {
        updateVisible();
        updateLocation();
    }

    private void updateVisible() {
        if ( skaterNode == null ) {
            setVisible( false );
        }
        else {
            setVisible( !isSkaterFullyOnscreen() );
        }
    }

    private boolean isSkaterFullyOnscreen() {
        return module.getEnergySkateParkSimulationPanel().isSkaterOnscreen( skaterNode );
    }

    private void updateLocation() {
        buttonNode.setOffset( canvas.getWidth() / 2 - buttonNode.getFullBounds().getWidth() / 2, canvas.getHeight() / 2 - buttonNode.getFullBounds().getHeight() / 2 );
    }

    public void setSkaterNode( SkaterNode skaterNode ) {
        this.skaterNode = skaterNode;
        update();
    }
}
