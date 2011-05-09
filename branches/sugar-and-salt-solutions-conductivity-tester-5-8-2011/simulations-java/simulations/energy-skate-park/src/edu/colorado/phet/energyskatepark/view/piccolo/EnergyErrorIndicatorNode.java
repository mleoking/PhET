// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.ShadowPText;
import edu.colorado.phet.energyskatepark.model.Body;
import edu.colorado.phet.energyskatepark.model.EnergySkateParkModel;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

/**
 * Author: Sam Reid
 * Apr 6, 2007, 10:04:58 AM
 */
public class EnergyErrorIndicatorNode extends PhetPNode {
    private Body body;
    private EnergySkateParkModel model;
    private ShadowPText textNode;
    private Body.Listener listener;

    public EnergyErrorIndicatorNode( EnergySkateParkModel model ) {
        this.model = model;
        listener = new Body.ListenerAdapter() {
            public void energyChanged() {
                update();
            }
        };
        model.addEnergyModelListener( new EnergySkateParkModel.EnergyModelListenerAdapter() {
            public void bodyCountChanged() {
                updateBody();
            }
        } );
        updateBody();

        textNode = createTextNode( "Energy Error" );
        addChild( textNode );

        JButton clear = new JButton( "Clear" );
        clear.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                body.clearEnergyError();
            }
        } );
        PSwing child = new PSwing( clear );

        child.setOffset( 0, createTextNode( "Energy Error: 0.00E-00" ).getFullBounds().getHeight() );
        addChild( child );
    }

    private ShadowPText createTextNode( String textValue ) {
        ShadowPText text = new ShadowPText( textValue );
        text.setFont( new PhetFont( 18, true ) );
        text.setShadowColor( Color.black );
        text.setShadowOffset( 2, 2 );
        text.setTextPaint( Color.red );
        return text;
    }

    private void update() {
        if( body == null ) {
            setVisible( false );
        }
        else {
            setVisible( body.getErrorCount() > 0 );
            if( body.getErrorCount() > 0 ) {
                double energyError = body.getFractionalEnergyError() * 100;
                String energyPct = new DecimalFormat( "0.00" ).format( energyError );
                textNode.setText( "Energy Error: " + energyPct + "% (" + body.getErrorCount() + " errors)" );
            }
        }

    }

    private void updateBody() {
        Body newBody = model.getNumBodies() > 0 ? model.getBody( 0 ) : null;
        if( newBody != body ) {
            if( body != null ) {
                body.removeListener( listener );
            }
            if( newBody != null ) {
                newBody.addListener( listener );
            }
            this.body = newBody;
            update();
        }
    }

}
