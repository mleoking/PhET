/* Copyright 2004, Sam Reid */
package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.RectangleUtils;

import java.awt.*;
import java.awt.event.KeyEvent;
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
        setup( graphics2D );
        AbstractGraphic[] g = getChildren();
        for( int i = 0; i < g.length; i++ ) {
            AbstractGraphic abstractGraphic = g[i];
            abstractGraphic.paint( graphics2D );
        }
        restore( graphics2D );
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {

                getChildrenMouseOrder()[i].mouseEntered( event );

            }
            event.restore( orig );
        }
        super.mouseEntered( event );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }

        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {

                getChildrenMouseOrder()[i].mouseExited( event );

            }
            event.restore( orig );
        }
        super.mouseExited( event );
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }

        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {

                getChildrenMouseOrder()[i].mousePressed( event );

            }
            event.restore( orig );
        }
        super.mousePressed( event );//if no child handles, give us a chance
    }

    public void mouseClicked( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {

                getChildrenMouseOrder()[i].mouseClicked( event );

            }
            event.restore( orig );
        }
        super.mouseClicked( event );//if no child handles, give us a chance
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {

                getChildrenMouseOrder()[i].mouseReleased( event );

            }
            event.restore( orig );
        }
        super.mouseReleased( event );
    }


    public void mouseDragged( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        if( event.isConsumed() ) {
            return;
        }
        if( !composite ) {
            SceneGraphMouseEvent orig = event.push( getTransform(), this );
            for( int i = 0; i < getChildrenMouseOrder().length && !event.isConsumed(); i++ ) {
                getChildrenMouseOrder()[i].mouseDragged( event );
            }
            event.restore( orig );
        }
        super.mouseDragged( event );
    }


    public void handleEntranceAndExit( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return;
        }
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        AbstractGraphic[] children = getChildrenMouseOrder();
        for( int i = 0; i < children.length; i++ ) {
            AbstractGraphic child = children[i];
            child.handleEntranceAndExit( event );
        }
        event.restore( orig );
        super.handleEntranceAndExit( event );
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

    public boolean containsMousePointLocal( double x, double y ) {
        Point2D src = new Point2D.Double( x, y );
        Point2D pt = null;
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length; i++ ) {
            pt = children[i].push( src );
            if( children[i].containsMousePointLocal( pt.getX(), pt.getY() ) ) {
                return true;
            }
        }
        return false;
    }

    public Rectangle2D getLocalBounds() {
        AbstractGraphic[] children = getChildren();
        Rectangle2D[] r2 = new Rectangle2D[children.length];
        for( int i = 0; i < r2.length; i++ ) {
            AbstractGraphic child = children[i];
//            r2[i] = children[i].getTransform().createTransformedShape( children[i].getLocalBounds() ).getBounds2D();
            if( child == null ) {
                throw new RuntimeException( "Null child" );
            }
            if( child.getTransform() == null ) {
                throw new RuntimeException( "Null transform" );
            }
            if( child.getLocalBounds() == null ) {
                child.getLocalBounds();
                throw new RuntimeException( "Null bounds" );
            }
            r2[i] = child.getTransform().createTransformedShape( child.getLocalBounds() ).getBounds2D();
        }
        if( r2.length == 0 ) {
            return new Rectangle2D.Double();
        }
        else {
            Rectangle2D union = RectangleUtils.union( r2 );
            return union;
        }
    }

    protected void drawBorderDebug( Graphics2D graphics2D ) {
        GraphicsState state = new GraphicsState( graphics2D );
        graphics2D.setStroke( new BasicStroke( 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float[]{4, 4}, 0 ) );
        graphics2D.setColor( Color.red );
        graphics2D.draw( getLocalBounds() );
        state.restoreGraphics();
    }

    public AbstractGraphic getHandler( SceneGraphMouseEvent event ) {
        if( isIgnoreMouse() ) {
            return null;
        }
        if( !composite ) {
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
        else {
            return super.getHandler( event );
        }
    }

    protected void graphicAdded( AbstractGraphic graphic ) {
        graphic.setParent( this );
    }

    public void disableKeyFocusTree() {
        super.disableKeyFocusTree();
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length; i++ ) {
            children[i].disableKeyFocusTree();
        }
    }

    public void keyPressed( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        //pass to super, if children fail.
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length && !e.isConsumed(); i++ ) {
            children[i].keyPressed( e );
        }
        super.keyPressed( e );
    }

    public void keyTyped( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        //pass to super, if children fail.
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length && !e.isConsumed(); i++ ) {
            children[i].keyTyped( e );
        }
        super.keyPressed( e );
    }

    public void keyReleased( KeyEvent e ) {
        if( e.isConsumed() ) {
            return;
        }
        //pass to super, if children fail.
        AbstractGraphic[] children = getChildren();
        for( int i = 0; i < children.length && !e.isConsumed(); i++ ) {
            children[i].keyReleased( e );
        }
        super.keyPressed( e );
    }
}
