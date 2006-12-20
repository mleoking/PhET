package edu.colorado.phet.cck.piccolo_cck.lifelike;

import edu.colorado.phet.cck.CCKImageSuite;
import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;

import javax.swing.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SwitchNode extends SwitchBodyImageNode {
    private Switch switch_;
    private ICCKModule module;
    private SwitchLeverNode switchLeverNode;

    public SwitchNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, component, module );
        this.switch_ = s;
        this.module = module;
        switchLeverNode = new SwitchLeverNode();
        addChild( switchLeverNode );
    }

    public void delete() {
        super.delete();
        switchLeverNode.delete();
    }

    class SwitchLeverNode extends PhetPNode {
        private PNode imagePNode;
        private BufferedImage knifeHandleImage;
        private SimpleObserver switchObserver = new SimpleObserver() {
            public void update() {
                SwitchLeverNode.this.update();
            }
        };

        public SwitchLeverNode() {
            addInputEventListener( new CursorHandler() );
            knifeHandleImage = CCKImageSuite.getInstance().getKnifeHandleImage();
            imagePNode = new PImage( knifeHandleImage );
            addChild( imagePNode );
            switch_.addObserver( switchObserver );

            imagePNode.addInputEventListener( new PBasicInputEventHandler() {
                private double origGrabAngle = Double.NaN;
                private double origLeverAngle = Double.NaN;

                public void mouseDragged( PInputEvent event ) {
                    if( Double.isNaN( origGrabAngle ) ) {
                        origGrabAngle = getAngle( event );
                        origLeverAngle = switch_.getHandleAngle();
                    }
//                    System.out.println( "event.getPosition() = " + event.getPosition() );
//                    System.out.println( "event.getPositionRelativeTo( SwitchNode.this) = " + event.getPositionRelativeTo( SwitchNode.this ) );
//                    System.out.println( "event.getPositionRelativeTo( SwitchNode.this.getParent) = " + event.getPositionRelativeTo( SwitchNode.this.getParent() ) );
                    double angle = getAngle( event );
                    double dTheta = angle - origGrabAngle;
                    double desiredAngle = origLeverAngle + dTheta;
                    while( desiredAngle < 0 ) {
                        desiredAngle += Math.PI * 2;
                    }
                    while( desiredAngle > Math.PI * 2 ) {
                        desiredAngle -= Math.PI * 2;
                    }
                    if( desiredAngle < Math.PI ) {
                        desiredAngle = Math.PI;
                    }
                    else if( desiredAngle > 5 ) {
                        desiredAngle = 5;
                    }
                    else if( desiredAngle < 0.6 ) {
                        desiredAngle = Math.PI;
                    }

                    switch_.setHandleAngle( desiredAngle );
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
            imagePNode.rotateAboutPoint( switch_.getHandleAngle() + Math.PI, knifeHandleImage.getWidth(), knifeHandleImage.getHeight() / 2 );
        }

        public void delete() {
            switch_.removeObserver( switchObserver );
        }
    }
}
