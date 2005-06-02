/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 10:39:47 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public class SceneGraphMouseEvent {
    private boolean consumed = false;
    private MouseEvent mouseEvent;
    private MouseEvent prevEvent;
    private AbstractGraphic abstractGraphic;
    private double x;
    private double y;

    private double dx;
    private double dy;

    public SceneGraphMouseEvent( MouseEvent mouseEvent, MouseEvent prevEvent ) {
        this.mouseEvent = mouseEvent;
        this.prevEvent = prevEvent;
        this.x = mouseEvent.getX();
        this.y = mouseEvent.getY();

        dx = mouseEvent.getX() - prevEvent.getX();
        dy = mouseEvent.getY() - prevEvent.getY();
    }

    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public SceneGraphMouseEvent push( AffineTransform transform, AbstractGraphic handler ) {
        SceneGraphMouseEvent orig = copyState();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        try {
            AffineTransform inverse = transform.createInverse();

            Point2D pt = inverse.transform( new Point2D.Double( x, y ), null );
            x = pt.getX();
            y = pt.getY();
            Point2D delta = inverse.deltaTransform( new Point2D.Double( dx, dy ), null );
            dx = delta.getX();
            dy = delta.getY();
            this.abstractGraphic = handler;
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        return orig;
    }

    public SceneGraphMouseEvent copyState() {
        SceneGraphMouseEvent scme = new SceneGraphMouseEvent( mouseEvent, prevEvent );
        scme.setAbstractGraphic( abstractGraphic );
        scme.x = this.x;
        scme.y = this.y;
        scme.dx = this.dx;
        scme.dy = this.dy;
        scme.consumed = this.consumed;

        return scme;
    }

    public void restore( SceneGraphMouseEvent sceneGraphMouseEvent ) {
        this.x = sceneGraphMouseEvent.x;
        this.y = sceneGraphMouseEvent.y;
        this.dx = sceneGraphMouseEvent.dx;
        this.dy = sceneGraphMouseEvent.dy;
//        this.consumed = sceneGraphMouseEvent.consumed;
        this.abstractGraphic = sceneGraphMouseEvent.abstractGraphic;
    }

    public AbstractGraphic getAbstractGraphic() {
        return abstractGraphic;
    }

    public void setAbstractGraphic( AbstractGraphic abstractGraphic ) {
        this.abstractGraphic = abstractGraphic;
    }

    public String toString() {
        return "scme, x=" + x + ", y=" + y + ", graphic=" + abstractGraphic + ", mouseEvent=" + mouseEvent;
    }

    public MouseEvent getPrevEvent() {
        return prevEvent;
    }

    public double getDx() {
        return dx;
    }

    public double getDy() {
        return dy;
    }

    public void consume() {
        this.consumed = true;
    }

    public boolean isConsumed() {
        return consumed;
    }

}
