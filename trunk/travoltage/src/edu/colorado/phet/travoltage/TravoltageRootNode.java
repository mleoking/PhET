/* Copyright 2004, Sam Reid */
package edu.colorado.phet.travoltage;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

    public TravoltageRootNode( TravoltageModule travoltageModule, TravoltagePanel travoltagePanel ) {
        this.travoltageModule = travoltageModule;
        travoltageBodyNode = new TravoltageBodyNode();

        final DoorknobNode doorknobNode = new DoorknobNode();
        doorknobNode.setOffset( 286, 172 );
        addChild( doorknobNode );

        addChild( travoltageBodyNode );

        electronSetNode = new ElectronSetNode();
        addChild( electronSetNode );


        LimbNode.Listener listener = new LimbNode.Listener() {
            public void limbRotated() {
                remapLocations();
            }
        };
        getLegNode().addListener( listener );
        getArmNode().addListener( listener );
        getArmNode().setAngle( -0.5663 );
        getArmNode().addListener( new LimbNode.Listener() {
            public void limbRotated() {
                System.out.println( "getArmNode().getAngle() = " + getArmNode().getAngle() );
            }
        } );
        addInputEventListener( new PBasicInputEventHandler() {
            public void mousePressed( PInputEvent event ) {
                super.mousePressed( event );
                System.out.println( "event.getCanvasPosition() = " + event.getCanvasPosition().getX() + "\t" + event.getCanvasPosition().getY() );
//                doorknobNode.setOffset( event.getCanvasPosition().getX(), event.getCanvasPosition().getY() );
            }
        } );
//        travoltagePanel.addMouseMotionListener( new MouseMotionAdapter() {
//            public void mouseDragged( MouseEvent e ) {
//                System.out.println( "e.getX() = " + e.getX() + ", e.y=" + e.getY() );
//                doorknobNode.setOffset( e.getX(), e.getY() );
//            }
//        } );
        travoltagePanel.addMouseListener( new MouseListener() {
            public void mouseClicked( MouseEvent e ) {
                System.out.println( "e = " + e );
            }

            public void mouseEntered( MouseEvent e ) {
            }

            public void mouseExited( MouseEvent e ) {
            }

            public void mousePressed( MouseEvent e ) {
            }

            public void mouseReleased( MouseEvent e ) {
            }
        } );

        Point doorknobSpot = new Point( 317, 207 ); //doorknob position
        Point src = new Point( 100, 100 );
        double angle = Math.PI / 3.8;
        addChild( new SparkNode( getArmNode(), src, doorknobSpot, angle, 4, 6 ) );
    }

    protected void addDebugFootLocation() {
        final PText debugNode = new PText( "Hello" );
        debugNode.setTextPaint( Color.red );
        addChild( debugNode );
        getLegNode().addListener( new LimbNode.Listener() {
            public void limbRotated() {
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
