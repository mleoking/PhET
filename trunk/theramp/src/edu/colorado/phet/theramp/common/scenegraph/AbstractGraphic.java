package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 1, 2005
 * Time: 8:06:31 PM
 * Copyright (c) Jun 1, 2005 by Sam Reid
 */
public abstract class AbstractGraphic {
    private Shape clip;
    private RenderingHints renderingHints;
    private AffineTransform transform;
    private Paint paint;
    private Stroke stroke;
    private Font font;
    private ArrayList savedStates = new ArrayList();
    private ArrayList mouseHandlers = new ArrayList();
    private boolean pressed;

    public abstract void paint( Graphics2D graphics2D );

    /**
     * Save state & modify graphics.
     */
    protected void setup( Graphics2D graphics2D ) {
        GraphicsState state = new GraphicsState( graphics2D );
        savedStates.add( state );
        if( transform != null ) {
            graphics2D.transform( transform );
        }
        if( paint != null ) {
            graphics2D.setPaint( paint );
        }
        if( font != null ) {
            graphics2D.setFont( font );
        }
        if( clip != null ) {
            graphics2D.clip( clip );
        }
        if( stroke != null ) {
            graphics2D.setStroke( stroke );
        }
        if( renderingHints != null ) {
            graphics2D.setRenderingHints( renderingHints );
        }
    }

    protected void restore( Graphics2D graphics2D ) {
        GraphicsState state = (GraphicsState)savedStates.remove( savedStates.size() - 1 );
        state.restoreGraphics();
    }

    public void translate( double dx, double dy ) {
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.translate( dx, dy );
    }

    public void setClip( Shape clip ) {
        this.clip = clip;
    }

    public void setRenderingHints( RenderingHints renderingHints ) {
        this.renderingHints = renderingHints;
    }

    public void setTransform( AffineTransform transform ) {
        this.transform = transform;
    }

    public void setPaint( Paint paint ) {
        this.paint = paint;
    }

    public void setColor( Color color ) {
        setPaint( color );
    }

    public void setStroke( Stroke stroke ) {
        this.stroke = stroke;
    }

    public void setFont( Font font ) {
        this.font = font;
    }

    public void setRenderingHint( RenderingHints.Key key, Object value ) {
        if( renderingHints == null ) {
            renderingHints = new RenderingHints( key, value );
        }
        else {
            renderingHints.put( key, value );
        }
    }

    public void setAntialias( boolean b ) {
        setRenderingHint( RenderingHints.KEY_ANTIALIASING, b ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF );
    }

    public void scale( double sx, double sy ) {
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.scale( sx, sy );
    }

    public void rotate( double theta, double x, double y ) {
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.rotate( theta, x, y );
    }

    public void rotate( double theta ) {
        if( transform == null ) {
            transform = new AffineTransform();
        }
        transform.rotate( theta );
    }

    public void addMouseListener( SceneGraphMouseHandler mouseHandler ) {
        mouseHandlers.add( mouseHandler );
    }

    public AbstractGraphic getHandler( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        System.out.println( "event = " + event );
        if( contains( event.getX(), event.getY() ) ) {
            event.restore( orig );
            return this;
        }
        else {
            event.restore( orig );
            return null;
        }
    }

    public abstract boolean contains( double x, double y );

    public AffineTransform getTransform() {
        return transform;
    }

    public void mouseDragged( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( pressed && !event.isConsumed() ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseDragged( event );
            }
        }
        event.restore( orig );
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( transform, this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseEntered( event );
        }
        event.restore( orig );
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( transform, this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseExited( event );
        }
        event.restore( orig );
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( transform, this );
        if( contains( event.getX(), event.getY() ) && !event.isConsumed() ) {
            event.consume();
            pressed = true;
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mousePressed( event );
            }
        }
        else {
            pressed = false;
        }
        event.restore( orig );
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( transform, this );
        if( contains( event.getX(), event.getY() ) && !event.isConsumed() ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseReleased( event );
            }
        }
        pressed = false;
        event.restore( orig );
    }

    public abstract double getWidth();

    public abstract double getHeight();
}
