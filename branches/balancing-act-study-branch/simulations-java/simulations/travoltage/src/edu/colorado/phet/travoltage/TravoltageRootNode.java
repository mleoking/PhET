// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 30, 2006
 * Time: 11:25:26 PM
 */

public class TravoltageRootNode extends PNode {
    private TravoltageBodyNode travoltageBodyNode;
    private ElectronSetNode electronSetNode;
    private TravoltageModule travoltageModule;
    private DoorknobNode doorknobNode;
    private SparkNode sparkNode;
    private TravoltageBackgroundNode backgroundNode;

    public TravoltageRootNode( TravoltageModule travoltageModule, TravoltagePanel travoltagePanel, TravoltageModel travoltageModel ) {
        this.travoltageModule = travoltageModule;
        travoltageBodyNode = new TravoltageBodyNode();
        Point2D inset = new Point2D.Double( 300, 75 );

        backgroundNode = new TravoltageBackgroundNode();
        backgroundNode.setOffset( -inset.getX(), -inset.getY() );
        addChild( backgroundNode );

        final PImage doorNode = PImageFactory.create( "travoltage/images/door.gif" );
        doorNode.scale( 0.75 );
        addChild( doorNode );
        doorNode.setOffset( 290, -42 );
//        doorNode.addInputEventListener( new PDragEventHandler(){
//            protected void drag( PInputEvent event ) {
//                super.drag( event );
//                System.out.println( "doorNode = " + doorNode.getOffset() );
//            }
//        } );

        doorknobNode = new DoorknobNode();
//        doorknobNode.setOffset( 286, 172 );
        doorknobNode.setOffset( 286 + 14, 172 - 60 );
        addChild( doorknobNode );

        addChild( travoltageBodyNode );

        electronSetNode = new ElectronSetNode( travoltageModel.getJadeElectronSet() );
        addChild( electronSetNode );

        LimbNode.Listener listener = new LimbNode.Listener() {
            public void limbRotated() {
                remapLocations();
            }
        };
        getLegNode().addListener( listener );
        getArmNode().addListener( listener );
//        getArmNode().setAngle( -0.5663 );
        getArmNode().setAngle( -0.95 );
//        travoltagePanel.addMouseListener( new MouseAdapter() {
//            public void mouseClicked( MouseEvent e ) {
//                System.out.println( "e = " + e );
//            }
//        } );

        double angle = Math.PI / 3.8;
        sparkNode = new SparkNode( getArmNode(), getDoorknobNode(), angle, 4, 6, travoltageModule.getClock() );
        travoltageModel.addListener( new TravoltageModel.Listener() {
            public void sparkStarted() {
//                sparkNode.setVisible( true );
            }

            public void sparkFinished() {
                sparkNode.setVisible( false );
            }

            public void electronExitedFinger() {
                sparkNode.setVisible( true );
            }
        } );
        addChild( sparkNode );
        setSparkVisible( false );
        setOffset( inset );

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
        Point2D pt = getElectronEntryPoint();
        JadeElectron jadeElectron = new JadeElectron( pt.getX(), pt.getY(), JadeElectronNode.getViewRadius() );
        JadeElectronNode electronNode = new JadeElectronNode( getTravoltageBodyNode(), jadeElectron );
        electronNode.setLocationMap( new LimbLocationMap( getLegNode(), getArmNode(), new DefaultLocationMap( electronNode.getRadius() ), getTravoltageBodyNode() ) );
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

    public DoorknobNode getDoorknobNode() {
        return doorknobNode;
    }

    public void setSparkVisible( boolean b ) {
        sparkNode.setVisible( b );
    }
}
