/* Copyright 2007, University of Colorado */

package edu.colorado.phet.glaciers.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * TestViewport tests viewing one model with two difference canvases.
 * The top canvas is a birds-eye view, with a draggable viewport.
 * The bottom canvas is a zoomed in view.
 * The position and size of the viewport determines what is visible 
 * in the zoomed view.
 * 
 * TODO:
 * - use same scenegraph with both canvases
 * - keep height of top view constant
 * - redraw artifacts when squares are dragged
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestViewport extends JFrame {
    
    private static final Dimension DEFAULT_SQUARE_SIZE = new Dimension( 100, 100 );
    private static final int NUMBER_OF_SQUARES = 100;
    
    /* squares will be distributed in the bounds of the world */
    private static final Dimension WORLD_SIZE = new Dimension( 6000, 2000 );
    
    /* Implement this interface to be notified of changes to a square. */
    private interface SquareListener {
        public void positionChanged();
    }
    
    /* Model of a square, with position being the only mutable property. */
    private static class Square {

        private Point2D _position;
        private final Dimension _size;
        private final Color _color;
        private final ArrayList _listeners;

        public Square( Point2D position, Dimension size, Color color ) {
            _position = new Point2D.Double( position.getX(), position.getY() );
            _size = new Dimension( size );
            _color = color;
            _listeners = new ArrayList();
        }
        
        public void setPosition( Point2D position ) {
            if ( !position.equals( _position ) ) {
                _position.setLocation( position );
                notifyPositionChanged();
            }
        }
        
        public Point2D getPosition() {
            return new Point2D.Double( _position.getX(), _position.getY() );
        }

        public Dimension getSize() {
            return new Dimension( _size );
        }
        
        public Color getColor() {
            return _color;
        }
        
        public Shape getShape() {
            return new Rectangle2D.Double( 0, 0, _size.getWidth(), _size.getHeight() );
        }
        
        public void addListener( SquareListener listener ) {
            _listeners.add( listener );
        }

        public void removeListener( SquareListener listener ) {
            _listeners.remove( listener );
        }
        
        private void notifyPositionChanged() {
            for ( int i = 0; i < _listeners.size(); i++ ) {
                Object listener = _listeners.get( i );
                if ( listener instanceof SquareListener ) {
                    ( (SquareListener) listener ).positionChanged();
                }
            }
        }
    }
    
    /* Model contains a random collection of squares */
    private static class TestModel {
        
        private final ArrayList _squares;
        
        public TestModel() {
            _squares = new ArrayList();
            for ( int i = 0; i < NUMBER_OF_SQUARES; i++ ) {
                Point2D randomPosition = new Point2D.Double( Math.random() * WORLD_SIZE.getWidth(), Math.random() * WORLD_SIZE.getHeight() );
                Color randomColor = new Color( (int) ( 255 * Math.random() ), (int) ( 255 * Math.random() ), (int) ( 255 * Math.random() ) );
                _squares.add( new Square( randomPosition, DEFAULT_SQUARE_SIZE, randomColor ) );
            }
        }

        public Square[] getSquares() {
            return (Square[]) _squares.toArray( new Square[_squares.size()] );
        }
    }
    
    /* View of a square, drag to change square's position. */
    private static class SquareNode extends PPath {
        
        private final Square _square;
        
        public SquareNode( Square square ) {
            super();

            _square = square;
            
            setPathTo( _square.getShape() );
            setPaint( _square.getColor() );
            
            _square.addListener( new SquareListener() {
                public void positionChanged() {
                    updatePosition();
                }
            });
            
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {

                private double _xOffset, _yOffset;

                protected void startDrag( PInputEvent event ) {
                    _xOffset = event.getPosition().getX() - _square.getPosition().getX();
                    _yOffset = event.getPosition().getY() - _square.getPosition().getY();
                    super.startDrag( event );
                }

                protected void drag( PInputEvent event ) {
                    double x = event.getPosition().getX() - _xOffset;
                    double y = event.getPosition().getY() - _yOffset;
                    _square.setPosition( new Point2D.Double( x, y ) );
                }
            } );
            
            updatePosition();
        }
        
        private void updatePosition() {
            setOffset( _square.getPosition() );
        }
    }
    
    /* Canvas, contains a visual representation of the specified model, at the specified scale. */
    private static class TestCanvas extends PCanvas {
        
        public TestCanvas( TestModel model, double scale ) {
            super();
            getCamera().setViewScale( scale );
            
            setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
            removeInputEventListener( getZoomEventHandler() );
            removeInputEventListener( getPanEventHandler() );
            
            Square[] squares = model.getSquares();
            for ( int i = 0; i < squares.length; i++ ) {
                SquareNode node = new SquareNode( squares[i] );
                getLayer().addChild( node );
            }
        }
    }
    
    /* Implement this interface to be notified of changes to a viewport. */
    private interface ViewportListener {
        public void boundsChanged();
    }
    
    /* Model of a viewport, describes a portion of the model that is visible. */
    private static class Viewport {
        
        private Rectangle2D _bounds;
        private ArrayList _listeners;
        
        public Viewport( Rectangle2D bounds ) {
            _bounds = new Rectangle2D.Double( bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight() );
            _listeners = new ArrayList();
        }
        
        public void setBounds( Rectangle2D bounds ) {
            if ( !bounds.equals( _bounds ) ) {
                _bounds.setRect( bounds );
                notifyBoundsChanged();
            }
        }
        
        public Rectangle2D getBounds() {
            return new Rectangle2D.Double( _bounds.getX(), _bounds.getY(), _bounds.getWidth(), _bounds.getHeight() );
        }
        
        public void addListener( ViewportListener listener ) {
            _listeners.add( listener );
        }

        public void removeListener( ViewportListener listener ) {
            _listeners.remove( listener );
        }
        
        private void notifyBoundsChanged() {
            for ( int i = 0; i < _listeners.size(); i++ ) {
                Object listener = _listeners.get( i );
                if ( listener instanceof ViewportListener ) {
                    ( (ViewportListener) listener ).boundsChanged();
                }
            }
        }
    }
    
    /* View of a viewport, drag to change viewport's position */
    private static class ViewportNode extends PPath {
        
        private Viewport _viewport;
        
        public ViewportNode( Viewport viewport ) {
            super();
            
            _viewport = viewport;
            
            setPaint( null );
            setStroke( new BasicStroke( 6f ) );
            setStrokePaint( Color.RED );
            
            _viewport.addListener( new ViewportListener() {
                public void boundsChanged() {
                    updateBounds();
                }
            });
            
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {

                private double _xOffset, _yOffset;

                protected void startDrag( PInputEvent event ) {
                    Rectangle2D viewportBounds = _viewport.getBounds();
                    _xOffset = event.getPosition().getX() - viewportBounds.getX();
                    _yOffset = event.getPosition().getY() - viewportBounds.getY();
                    super.startDrag( event );
                }

                protected void drag( PInputEvent event ) {
                    Rectangle2D viewportBounds = _viewport.getBounds();
                    double x = event.getPosition().getX() - _xOffset;
                    double y = event.getPosition().getY() - _yOffset;
                    double w = viewportBounds.getWidth();
                    double h = viewportBounds.getHeight();
                    _viewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
                }
            } );
            
            updateBounds();
        }
        
        private void updateBounds() {
            Rectangle2D viewportBounds = _viewport.getBounds();
            setPathTo( new Rectangle2D.Double( 0, 0, viewportBounds.getWidth(), viewportBounds.getHeight() ) );
            setOffset( viewportBounds.getX(), viewportBounds.getY() );
        }

    }
    
    /* 
     * Main window, creates one model with two views.
     * The top view has a draggable viewport control that determines what is shown in the bottom view. 
     */
    public static class TestFrame extends JFrame {

        private TestCanvas _bottomCanvas;
        private Viewport _viewport;
        
        public TestFrame() {
            super();

            TestModel model = new TestModel();

            TestCanvas topCanvas = new TestCanvas( model, 0.25 /* scale */);
            _bottomCanvas = new TestCanvas( model, 1 /* scale */);

            // viewport in the top view determines what is shown in the bottom view
            _viewport = new Viewport( new Rectangle2D.Double( 50, 50, 1, 1 ) );
            ViewportNode viewportNode = new ViewportNode( _viewport );
            topCanvas.getLayer().addChild( viewportNode );

            _bottomCanvas.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    handleBottomCanvasResized();
                }
            } );

            _viewport.addListener( new ViewportListener() {
                public void boundsChanged() {
                    handleViewportBoundsChanged();
                }
            } );

            //XXX topCanvas is not visible, zero size?
            //        JPanel panel = new JPanel( new BorderLayout() );
            //        panel.add( topCanvas, BorderLayout.NORTH );
            //        panel.add( bottomCanvas, BorderLayout.CENTER );
            //        getContentPane().add( panel );

            Box box = new Box( BoxLayout.Y_AXIS );
            box.add( topCanvas );
            box.add( _bottomCanvas );
            getContentPane().add( box );

            handleBottomCanvasResized();
            handleViewportBoundsChanged();
        }

        /* when the bottom view is resized, resize the viewport */
        private void handleBottomCanvasResized() {
            Rectangle2D canvasBounds = _bottomCanvas.getBounds();
            double scale = _bottomCanvas.getCamera().getViewScale();
            Rectangle2D viewportBounds = _viewport.getBounds();
            double x = viewportBounds.getX();
            double y = viewportBounds.getY();
            double w = scale * canvasBounds.getWidth();
            double h = scale * canvasBounds.getHeight();
            _viewport.setBounds( new Rectangle2D.Double( x, y, w, h ) );
        }

        /* when the viewport is moved, translate the bottom view's camera */
        private void handleViewportBoundsChanged() {
            Rectangle2D viewportBounds = _viewport.getBounds();
            _bottomCanvas.getCamera().setViewOffset( -viewportBounds.getX(), -viewportBounds.getY() );
        }
    }
    
    /* not intended for instantiation */
    private TestViewport() {}
    
    public static void main( String args[] ) {
        JFrame frame = new TestFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 640, 480 ) );
        frame.show();
    }
}
