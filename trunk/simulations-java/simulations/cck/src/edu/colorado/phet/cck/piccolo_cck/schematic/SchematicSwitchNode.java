package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.cck.piccolo_cck.lifelike.SwitchBodyRectangleNode;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 * Copyright (c) Sep 19, 2006 by Sam Reid
 */

public class SchematicSwitchNode extends SwitchBodyRectangleNode {
    private Switch s;
    private ICCKModule module;
    private SwitchLeverNode switchLeverNode;

    public SchematicSwitchNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, component, module );
        this.s = s;
        this.module = module;
        switchLeverNode = new SwitchLeverNode();
        addChild( switchLeverNode );

        int attachmentWidth = 40;
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, attachmentWidth, getDimension().getHeight() ), Color.black ) );
        addChild( new PhetPPath( new Rectangle2D.Double( getDimension().getWidth() - attachmentWidth, 0, attachmentWidth, getDimension().getHeight() ), Color.black ) );
    }

    public void delete() {
        super.delete();
        switchLeverNode.delete();
    }

    class SwitchLeverNode extends PhetPNode {//todo consolidate this code with lifelike version
        private PNode pathNode;
        double leverWidth = getDimension().getWidth();
        double leverHeight = 20;
        private SimpleObserver switchObserver = new SimpleObserver() {
            public void update() {
                SwitchLeverNode.this.update();
            }
        };

        public SwitchLeverNode() {
            addInputEventListener( new CursorHandler() );
            pathNode = new PhetPPath( new Rectangle2D.Double( 0, 0, leverWidth, leverHeight ), Color.black );
            addChild( pathNode );
            s.addObserver( switchObserver );

            pathNode.addInputEventListener( new PBasicInputEventHandler() {
                private double origGrabAngle = Double.NaN;
                private double origLeverAngle = Double.NaN;

                public void mouseDragged( PInputEvent event ) {
                    if( Double.isNaN( origGrabAngle ) ) {
                        origGrabAngle = getAngle( event );
                        origLeverAngle = s.getHandleAngle();
                    }
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

                    s.setHandleAngle( desiredAngle );
                    event.setHandled( true );
                }

                private double getAngle( PInputEvent event ) {
                    Point2D.Double pivotPoint = new Point2D.Double( leverWidth, leverHeight );
                    pathNode.localToGlobal( pivotPoint );
                    SchematicSwitchNode.this.getParent().globalToLocal( pivotPoint );
                    Vector2D.Double vector = new Vector2D.Double( pivotPoint, event.getPositionRelativeTo( SchematicSwitchNode.this.getParent() ) );
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
            pathNode.setTransform( new AffineTransform() );
            pathNode.translate( 0, leverHeight );
//            pathNode.rotateAboutPoint( s.getHandleAngle() + Math.PI, leverWidth, leverHeight / 2 );
            pathNode.rotateAboutPoint( s.getHandleAngle() + Math.PI, leverWidth, leverHeight );
        }

        public void delete() {
            s.removeObserver( switchObserver );
        }
    }
}
