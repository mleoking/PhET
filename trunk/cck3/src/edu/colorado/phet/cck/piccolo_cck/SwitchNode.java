package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Branch;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SwitchNode extends BranchNode {
    private Switch s;

    public SwitchNode( CCKModel model, Switch s ) {
        this.s = s;
        SwitchBodyNode switchBodyNode = new SwitchBodyNode( model, s );
        addChild( switchBodyNode );
        SwitchLeverNode switchLeverNode = new SwitchLeverNode();//so the transform on switchBodyNode won't interfere
        addChild( switchLeverNode );
    }

    public Branch getBranch() {
        return s;
    }

    class SwitchLeverNode extends PhetPNode {
        private PNode imagePNode;
        private BufferedImage knifeHandleImage;


        public SwitchLeverNode() {
            addInputEventListener( new CursorHandler() );
            knifeHandleImage = CCKImageSuite.getInstance().getKnifeHandleImage();
            imagePNode = new PImage( knifeHandleImage );
            addChild( imagePNode );
            s.addObserver( new SimpleObserver() {
                public void update() {
                    SwitchLeverNode.this.update();
                }
            } );

            imagePNode.addInputEventListener( new PBasicInputEventHandler() {
                private double origGrabAngle = Double.NaN;
                private double origLeverAngle = Double.NaN;

                public void mouseDragged( PInputEvent event ) {
                    if( Double.isNaN( origGrabAngle ) ) {
                        origGrabAngle = getAngle( event );
                        origLeverAngle = s.getHandleAngle();
                    }
                    System.out.println( "event.getPosition() = " + event.getPosition() );
                    System.out.println( "event.getPositionRelativeTo( SwitchNode.this) = " + event.getPositionRelativeTo( SwitchNode.this ) );
                    System.out.println( "event.getPositionRelativeTo( SwitchNode.this.getParent) = " + event.getPositionRelativeTo( SwitchNode.this.getParent() ) );
                    double angle = getAngle( event );
                    double dTheta = angle - origGrabAngle;
                    s.setHandleAngle( origLeverAngle + dTheta );
                    event.setHandled( true );
                }

                private double getAngle( PInputEvent event ) {
                    Point2D.Double pivotPoint = new Point2D.Double( knifeHandleImage.getWidth(), knifeHandleImage.getHeight() );
                    imagePNode.localToGlobal( pivotPoint );
                    SwitchNode.this.getParent().globalToLocal( pivotPoint );
                    Vector2D.Double vector = new Vector2D.Double( pivotPoint, event.getPositionRelativeTo( SwitchNode.this.getParent() ) );
                    return vector.getAngle();
                }

                public void mouseReleased( PInputEvent e ) {
                    origGrabAngle = Double.NaN;
                    origLeverAngle = Double.NaN;
                }
            } );
            update();
        }

        private void update() {
            imagePNode.setTransform( new AffineTransform() );
            imagePNode.setScale( 0.01 );
            imagePNode.setOffset( getBranch().getStartPoint() );
//            Vector2D.Double vector = new Vector2D.Double( event.getPositionRelativeTo( SwitchNode.this.getParent() ) );
//            double angle = vector.getAngle();
            imagePNode.rotateAboutPoint( s.getHandleAngle() + Math.PI, knifeHandleImage.getWidth(), knifeHandleImage.getHeight() / 2 );
        }
    }
}
