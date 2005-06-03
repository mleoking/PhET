package edu.colorado.phet.theramp.common.scenegraph;

import edu.colorado.phet.common.view.util.GraphicsState;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
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
    private boolean visible = true;
    private String name;

    private boolean mousePressed = false;
    private boolean mouseEntered = false;

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
        if( !( this instanceof GraphicNode ) ) {
            graphics2D.setStroke( new BasicStroke( 1 ) );
            graphics2D.setColor( Color.black );
            graphics2D.draw( getLocalBounds() );
        }
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

    public AffineTransform getTransform() {
        return transform;
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
        if( containsLocal( event.getX(), event.getY() ) ) {
            return this;
        }
        else {
            return null;
        }
    }

    public boolean containsLocal( double x, double y ) {
        return getLocalBounds().contains( x, y );
    }

    public void mouseDragged( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( getTransform(), this );
        if( mousePressed && !event.isConsumed() ) {//TODO only drag if we're the right guy.
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseDragged( event );
            }
        }
        event.restore( orig );
    }

    public void mouseEntered( SceneGraphMouseEvent event ) {
        setMouseEntered( true );
        SceneGraphMouseEvent orig = event.push( transform, this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseEntered( event );
        }
        event.restore( orig );
    }

    private void setMouseEntered( boolean mouseEntered ) {
        this.mouseEntered = mouseEntered;
    }

    public boolean isMouseEntered() {
        return mouseEntered;
    }

    public void mouseExited( SceneGraphMouseEvent event ) {
        setMouseEntered( false );
        SceneGraphMouseEvent orig = event.push( transform, this );
        for( int i = 0; i < mouseHandlers.size(); i++ ) {
            SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
            sceneGraphMouseHandler.mouseExited( event );
        }
        event.restore( orig );
    }

    public void mouseClicked( SceneGraphMouseEvent event ) {
        if( event.isConsumed() ) {
            return;
        }
        if( containsLocal( event.getX(), event.getY() ) ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseClicked( event );
            }
        }
    }

    public void mousePressed( SceneGraphMouseEvent event ) {
        if( event.isConsumed() ) {
            setMousePressed( false );
            return;
        }
//        SceneGraphMouseEvent orig = event.push( transform, this );
        if( containsLocal( event.getX(), event.getY() ) ) {
            event.consume();
            setMousePressed( true );
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mousePressed( event );
            }
        }
//        event.restore( orig );
    }

    private void setMousePressed( boolean mousePressed ) {
        this.mousePressed = mousePressed;
        System.out.println( "Set: pressed = " + mousePressed );
    }

    public void mouseReleased( SceneGraphMouseEvent event ) {
        SceneGraphMouseEvent orig = event.push( transform, this );
        if( containsLocal( orig.getX(), orig.getY() ) && !event.isConsumed() ) {
            event.consume();
            for( int i = 0; i < mouseHandlers.size(); i++ ) {
                SceneGraphMouseHandler sceneGraphMouseHandler = (SceneGraphMouseHandler)mouseHandlers.get( i );
                sceneGraphMouseHandler.mouseReleased( event );
            }
        }
        setMousePressed( false );
        event.restore( orig );
    }

    public void handleEntranceAndExit( SceneGraphMouseEvent event ) {
        if( !mouseEntered && containsLocal( event.getX(), event.getY() ) ) {
            mouseEntered( event );
        }
        else if( mouseEntered && !containsLocal( event.getX(), event.getY() ) ) {
            mouseExited( event );
        }
    }

    public double getLocalWidth() {
        return getLocalBounds().getWidth();
    }

    public double getLocalHeight() {
        return getLocalBounds().getHeight();
    }

    public abstract Rectangle2D getLocalBounds();

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    public void setCursorHand() {
        addMouseListener( new CursorHand() );
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String toString() {
        return "name=" + name + "@" + hashCode();
    }


}
