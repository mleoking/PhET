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

    public TravoltageRootNode() {
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
        getLegNode().addListener( new LegNode.Listener() {
            public void legRotated() {
                debugNode.setOffset( getLegNode().getGlobalElectronEntryPoint() );
            }
        } );
    }

    public TravoltageBodyNode getTravoltageBodyNode() {
        return travoltageBodyNode;
    }

    public void pickUpElectron() {
        ElectronNode electronNode = new ElectronNode();
        electronNode.setOffset( getElectronEntryPoint() );
        electronSetNode.addElectronNode( electronNode );
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
}
