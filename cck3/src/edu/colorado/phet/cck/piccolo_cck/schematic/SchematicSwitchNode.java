package edu.colorado.phet.cck.piccolo_cck.schematic;

import edu.colorado.phet.cck.ICCKModule;
import edu.colorado.phet.cck.model.CCKModel;
import edu.colorado.phet.cck.model.components.Switch;
import edu.colorado.phet.cck.piccolo_cck.PhetPPath;
import edu.colorado.phet.cck.piccolo_cck.lifelike.SwitchBodyNode;
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

public class SchematicSwitchNode extends SwitchBodyNode {
    private Switch s;
    private ICCKModule module;

    public SchematicSwitchNode( CCKModel model, Switch s, JComponent component, ICCKModule module ) {
        super( model, s, component, module );
        this.s = s;
        this.module = module;
        SchematicSwitchNode.SwitchLeverNode switchLeverNode = new SchematicSwitchNode.SwitchLeverNode();//so the transform on switchBodyNode won't interfere
        addChild( switchLeverNode );
        getpImage().setVisible( false );
    }

//    protected JPopupMenu createPopupMenu() {
//        return new ComponentMenu.SwitchMenu( s, module );
//    }

    class SwitchLeverNode extends PhetPNode {
        private PNode imagePNode;
        int leverWidth = 120;
        int leverHeight = 35;

        public SwitchLeverNode() {
            addInputEventListener( new CursorHandler() );
            imagePNode = new PhetPPath( new Rectangle2D.Double( 0, 0, leverWidth, leverHeight ), Color.black );
            addChild( imagePNode );
            s.addObserver( new SimpleObserver() {
                public void update() {
                    SchematicSwitchNode.SwitchLeverNode.this.update();
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

                    s.setHandleAngle( desiredAngle );
                    event.setHandled( true );
                }

                private double getAngle( PInputEvent event ) {
                    Point2D.Double pivotPoint = new Point2D.Double( leverWidth, leverHeight );
                    imagePNode.localToGlobal( pivotPoint );
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
            imagePNode.setTransform( new AffineTransform() );
            imagePNode.rotateAboutPoint( s.getHandleAngle() + Math.PI, leverWidth, leverHeight / 2 );
        }
    }
}
