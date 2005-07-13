/**
 * Copyright (C) 1998-2000 by University of Maryland, College Park, MD 20742, USA
 * All rights reserved.
 */
package edu.colorado.phet.tests.piccolo.pswing.oldimplementation;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class ZSwingEventHandler implements PInputEventListener {

    private ZSwing zSwing;

    public ZSwingEventHandler( ZSwing zSwing ) {
        this.zSwing = zSwing;
    }

    public void processEvent( PInputEvent event, int type ) {
        System.out.println( "event = " + event );
        ConvertedEvent c = convertToSwingEvent( event, type );

        if( c.target != null ) {
            System.out.println( "target = " + c.target );
            c.target.dispatchEvent( c.event );
        }
    }

    static class ConvertedEvent {
        InputEvent event;
        Component target;

        public ConvertedEvent( Component target, InputEvent event ) {
            this.target = target;
            this.event = event;
        }
    }

    private ConvertedEvent convertToSwingEvent( PInputEvent event, int type ) {
        InputEvent inputEvent = event.getSourceSwingEvent();
        inputEvent.setSource( zSwing.getComponent() );
        //put the point in the right reference frame.
        if( inputEvent instanceof MouseEvent ) {
            MouseEvent me = (MouseEvent)inputEvent;

//            Point2D local = zSwing.globalToLocal( me.getPoint() );
            Point2D origPoint = new Point2D.Double( me.getX(), me.getY() );
            Point2D copy = new Point2D.Double( me.getX(), me.getY() );
            copy = zSwing.parentToLocal( copy );

            Point localFramePoint = toLocalFrame( me.getPoint(), event );
            Component targetComponent = findComponentAt( zSwing.getComponent(), localFramePoint.x, localFramePoint.y );
            Component root = zSwing.getComponent();

            if( targetComponent == null ) {
                System.out.println( "ZSwingEventHandler.convertToSwingEvent, target Component is null" );
                return new ConvertedEvent( null, null );
            }
            localFramePoint = retarget( root, targetComponent, localFramePoint );

            System.out.println( "Rectangle bounds=" + zSwing.getComponent().getBounds() + ", me.getPoint() = " + origPoint + ", local=" + localFramePoint );
            MouseEvent mouseEvent = new MouseEvent( targetComponent, inputEvent.getID(),
                                                    inputEvent.getWhen(), inputEvent.getModifiers(),
                                                    localFramePoint.x, localFramePoint.y,
                                                    me.getClickCount(), me.isPopupTrigger(), me.getButton() );
            return new ConvertedEvent( targetComponent, mouseEvent );
        }
        else {
            return new ConvertedEvent( zSwing.getComponent(), inputEvent );
        }
    }

    private Point retarget( Component root, Component targetComponent, Point localFramePoint ) {
        if( root == targetComponent ) {
            return localFramePoint;
        }
        else {
            Container p = targetComponent.getParent();
            Point local = new Point( localFramePoint.x - targetComponent.getX(), localFramePoint.y - targetComponent.getY() );
//            return new Point( 2, 2 );
            return retarget( root, p, local );
        }
    }

    private Component findComponentAt( Component c, int x, int y ) {
        if( !c.contains( x, y ) ) {
            return null;
        }

        if( c instanceof Container ) {
            Container contain = ( (Container)c );
            int ncomponents = contain.getComponentCount();
            Component component[] = contain.getComponents();

            for( int i = 0; i < ncomponents; i++ ) {
                Component comp = component[i];
                if( comp != null ) {
                    Point p = comp.getLocation();
                    if( comp instanceof Container ) {
                        comp = findComponentAt( comp, x - (int)p.getX(), y - (int)p.getY() );
                    }
                    else {
                        comp = comp.getComponentAt( x - (int)p.getX(), y - (int)p.getY() );
                    }
                    if( comp != null && comp.isShowing() ) {
                        return comp;
                    }
                }
            }
        }
        return c;
    }

    private Point toLocalFrame( Point2D point, PInputEvent event ) {
        PCamera pc = event.getTopCamera();

        System.out.println( "pc.getTransform() = " + pc.getTransform() );
        try {
            Point2D tx = pc.getViewTransform().inverseTransform( point, null );
//            System.out.println( "point = " + point );
//            point = zSwing.globalToLocal( point );
//            System.out.println( "global to local= " + point );
            System.out.println( "tx = " + tx );
            tx = zSwing.getTransform().inverseTransform( tx, null );
            return new Point( (int)tx.getX(), (int)tx.getY() );
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
            return null;
        }
    }
}