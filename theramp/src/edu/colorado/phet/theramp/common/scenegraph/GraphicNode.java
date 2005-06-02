/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:20:24 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */

public abstract class GraphicNode extends AbstractGraphic {
    public abstract AbstractGraphic[] getChildren();

    public void paint( Graphics2D graphics2D ) {
        super.setup( graphics2D );
        AbstractGraphic[] g = getChildren();
        for( int i = 0; i < g.length; i++ ) {
            AbstractGraphic abstractGraphic = g[i];
            abstractGraphic.paint( graphics2D );
        }
        super.restore( graphics2D );
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        super.mouseEntered( event );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            getChildrenMouseOrder()[i].mouseEntered( event );
        }
        event.restore( orig );
    }

    public AbstractGraphic[] getChildrenMouseOrder() {
        AbstractGraphic[] ch = getChildren();
        java.util.List asl = new ArrayList( Arrays.asList( ch ) );

        Collections.reverse( asl );
        return (AbstractGraphic[])asl.toArray( new AbstractGraphic[0] );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        super.mouseExited( event );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            getChildrenMouseOrder()[i].mouseExited( event );
        }
        event.restore( orig );
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        super.mousePressed( event );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            getChildrenMouseOrder()[i].mousePressed( event );
        }
        event.restore( orig );
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        super.mousePressed( event );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            getChildrenMouseOrder()[i].mouseReleased( event );
        }
        event.restore( orig );
    }


    public void mouseDragged( SceneGraphMouseEvent event ) {
        super.mouseDragged( event );
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            getChildrenMouseOrder()[i].mouseDragged( event );
        }
        event.restore( orig );
    }

    public AbstractGraphic getHandler( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
            AbstractGraphic handler = getChildrenMouseOrder()[i].getHandler( event );
            if( handler != null ) {
                event.restore( orig );
                return handler;
            }
        }
        event.restore( orig );
        return null;
    }

    public double getWidth() {
        System.out.println( "GraphicNode.getWidth//todo error" );
        return 0;
    }

    public double getHeight() {
        System.out.println( "GraphicNode.getWidth//todo error" );
        return 0;
    }
}
