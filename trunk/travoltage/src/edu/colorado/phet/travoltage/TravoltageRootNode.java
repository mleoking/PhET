/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:25:26 PM
 * Copyright (c) Jun 30, 2006 by Sam Reid
 */

public class TravoltageRootNode extends PNode {
    private TravoltageBodyNode travoltageBodyNode;
    private ElectronSetNode electronSetNode;
    private TravoltageModule travoltageModule;

    public TravoltageRootNode( TravoltageModule travoltageModule ) {
        this.travoltageModule = travoltageModule;
        travoltageBodyNode = new TravoltageBodyNode();
        addChild( travoltageBodyNode );

        electronSetNode = new ElectronSetNode();
        addChild( electronSetNode );

        addChild( new SparkNode() );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                System.out.println( "event.getCanvasPosition() = " + event.getCanvasPosition().getX() + "\t" + event.getCanvasPosition().getY() );
            }
        } );
        LimbNode.Listener listener = new LimbNode.Listener() {
            public void legRotated() {
                remapLocations();
            }
        };
        getLegNode().addListener( listener );
        getArmNode().addListener( listener );
//        addDebugFootLocation();
//        Rectangle armRect = new Rectangle();
//        armRect.setFrameFromDiagonal(198.0, 118.0, 330.0, 200.0 );
//        PPath path=new PPath( armRect);
//        addChild( path);
    }

    protected void addDebugFootLocation() {
        final PText debugNode = new PText( "Hello" );
        debugNode.setTextPaint( Color.red );
        addChild( debugNode );
        getLegNode().addListener( new LimbNode.Listener() {
            public void legRotated() {
                debugNode.setOffset( getLegNode().getGlobalElectronEntryPoint() );
            }
        } );
    }

    public TravoltageBodyNode getTravoltageBodyNode() {
        return travoltageBodyNode;
    }

    public void pickUpElectron() {
//        Point2D pt = getElectronEntryPoint();
        Point2D pt = getElectronEntryPoint();
        JadeElectron jadeElectron = new JadeElectron( pt.getX(), pt.getY(), JadeElectronNode.getViewRadius() );
        JadeElectronNode electronNode = new JadeElectronNode( jadeElectron );
        electronNode.setLocationMap( new LimbLocationMap( getLegNode(), getArmNode(), new DefaultLocationMap( electronNode.getRadius() ) ) );
        travoltageModule.addElectron( jadeElectron );
        electronSetNode.addElectronNode( electronNode );
    }

    private ArmNode getArmNode() {
        return travoltageBodyNode.getArmNode();
    }

    private Point2D getElectronEntryPoint() {
        return getLegNode().getGlobalElectronEntryPoint();
    }

    private LegNode getLegNode() {
        return travoltageBodyNode.getLegNode();
    }

    public ElectronSetNode getElectronSetNode() {
        return electronSetNode;
    }

    private void remapLocations() {
        for( int i = 0; i < electronSetNode.getNumElectrons(); i++ ) {
            electronSetNode.getElectronNode( i ).update();
        }
    }
}
