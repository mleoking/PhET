// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo.schematic;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.components.Switch;
import edu.colorado.phet.circuitconstructionkit.view.piccolo.lifelike.SwitchBodyRectangleNode;
import edu.colorado.phet.common.phetcommon.math.MutableVector2D;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: Sam Reid
 * Date: Sep 19, 2006
 * Time: 2:33:35 PM
 */

public class SchematicSwitchNode extends SwitchBodyRectangleNode {
    private Switch s;
    private CCKModule module;
    private SwitchLeverNode switchLeverNode;

    public SchematicSwitchNode( CCKModel model, Switch s, JComponent component, CCKModule module ) {
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
                    if ( Double.isNaN( origGrabAngle ) ) {
                        origGrabAngle = getAngle( event );
                        origLeverAngle = s.getHandleAngle();
                    }
                    double angle = getAngle( event );
                    double dTheta = angle - origGrabAngle;
                    double desiredAngle = origLeverAngle + dTheta;
                    while ( desiredAngle < 0 ) {
                        desiredAngle += Math.PI * 2;
                    }
                    while ( desiredAngle > Math.PI * 2 ) {
                        desiredAngle -= Math.PI * 2;
                    }
                    if ( desiredAngle < Math.PI ) {
                        desiredAngle = Math.PI;
                    }
                    else if ( desiredAngle > 5 ) {
                        desiredAngle = 5;
                    }
                    else if ( desiredAngle < 0.6 ) {
                        desiredAngle = Math.PI;
                    }

                    s.setHandleAngle( desiredAngle );
                    event.setHandled( true );
                }

                private double getAngle( PInputEvent event ) {
                    Point2D.Double pivotPoint = new Point2D.Double( leverWidth, leverHeight );
                    pathNode.localToGlobal( pivotPoint );
                    SchematicSwitchNode.this.getParent().globalToLocal( pivotPoint );
                    MutableVector2D vector = new MutableVector2D( pivotPoint, event.getPositionRelativeTo( SchematicSwitchNode.this.getParent() ) );
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
            pathNode.rotateAboutPoint( s.getHandleAngle() + Math.PI, leverWidth, leverHeight );
        }

        public void delete() {
            s.removeObserver( switchObserver );
        }
    }
}
