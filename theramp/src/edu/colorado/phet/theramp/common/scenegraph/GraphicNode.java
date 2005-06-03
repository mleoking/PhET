/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

    private boolean composite = false;

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
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mouseEntered( event );
            }
            event.restore( orig );
        }
        super.mouseEntered( event );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mouseExited( event );
            }
            event.restore( orig );
        }
        super.mouseExited( event );
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mousePressed( event );
            }
            event.restore( orig );
        }
        super.mousePressed( event );//if no child handles, give us a chance
    }

    public void mouseClicked( SceneGraphMouseEvent event ) {
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mouseClicked( event );
            }
            event.restore( orig );
        }
        super.mouseClicked( event );//if no child handles, give us a chance
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mouseReleased( event );
            }
            event.restore( orig );
        }
        super.mouseReleased( event );
    }


    public void mouseDragged( SceneGraphMouseEvent event ) {
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length; i++ ) {
                getChildrenMouseOrder()[i].mouseDragged( event );
            }
            event.restore( orig );
        }
        super.mouseDragged( event );
    }

    public AbstractGraphic getHandler( SceneGraphMouseEvent event ) {
        //todo handle composite
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

    public AbstractGraphic[] getChildrenMouseOrder() {
        AbstractGraphic[] ch = getChildren();
        java.util.List asl = new ArrayList( Arrays.asList( ch ) );
        Collections.reverse( asl );
        return (AbstractGraphic[])asl.toArray( new AbstractGraphic[0] );
    }

    public boolean isComposite() {
        return composite;
    }

    public void setComposite( boolean composite ) {
        this.composite = composite;
    }

    public boolean containsLocal( double x, double y ) {
        Point2D pt = new Point2D.Double( x, y );
        Point2D txed = push( pt );
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length; i++ ) {
            if( children[i].containsLocal( txed.getX(), txed.getY() ) ) {
                return true;
            }
        }
        return false;
    }

    public Point2D push( Point2D src ) {
        AffineTransform transform = getTransform();
        if( transform == null ) {
            transform = new AffineTransform();
        }
        try {
            AffineTransform inverse = transform.createInverse();

            Point2D pt = inverse.transform( src, null );
            return pt;
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public Rectangle2D getLocalBounds() {
        AbstractGraphic[] children = getChildren();
        Rectangle2D[] r2 = new Rectangle2D[children.length];
        for( int i = 0; i < r2.length; i++ ) {
            r2[i] = children[i].getLocalBounds();
        }
        Rectangle2D union = RectangleUtils.union( r2 );
        try {
            return getTransformOrIdentity().createInverse().createTransformedShape( union ).getBounds2D();//todo is this correct?
        }
        catch( NoninvertibleTransformException e ) {
            e.printStackTrace();
        }
        return null;
    }

    private AffineTransform getTransformOrIdentity() {
        if( getTransform() == null ) {
            return new AffineTransform();
        }
        else {
            return getTransform();
        }
    }

    protected void restore( Graphics2D graphics2D ) {
        super.restore( graphics2D );
        if( !( this instanceof GraphicNode ) ) {
            graphics2D.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{4, 4}, 0 ) );
            graphics2D.setColor( Color.red );
            graphics2D.draw( getLocalBounds() );
        }
    }
}
