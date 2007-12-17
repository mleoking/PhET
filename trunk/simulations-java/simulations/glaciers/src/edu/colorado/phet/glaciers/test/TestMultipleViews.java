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
 * TestMultipleViews tests viewing one model with two difference canvases,
 * with each camera having a different camera scale.  Dragging a node in
 * one view updates the model, and causes all views to update.
 * 
 * TODO:
 * - keep height of top view constant
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TestMultipleViews extends JFrame {
    
    private static final Dimension DEFAULT_SQUARE_SIZE = new Dimension( 100, 100 );
    
    /** Implement this interface to be notified of changes to a square. */
    private interface SquareListener {
        public void positionChanged();
    }
    
    /** Model of a square, with position being the only mutable property. */
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
    
    /** Model for the application, contains a couple of squares */
    private static class TestModel {
        
        private final Square _redSquare, _blueSquare;
        
        public TestModel() {
            final int leftMargin = 50;
            final int topMargin = 50;
            final int spacing = 50;
            _redSquare = new Square( new Point2D.Double( leftMargin, topMargin ), DEFAULT_SQUARE_SIZE, Color.RED );
            _blueSquare = new Square( new Point2D.Double( leftMargin + _redSquare.getSize().getWidth() + spacing, topMargin ), DEFAULT_SQUARE_SIZE, Color.BLUE );
        }

        public Square getRedSquare() {
            return _redSquare;
        }
        
        public Square getBlueSquare() {
            return _blueSquare;
        }
    }
    
    /** Visual representation of a square. Dragging this node updates the model. */
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
    
    /** Canvas, contains a visual representation of the specified model, at the specified scale. */
    private static class TestCanvas extends PCanvas {
        
        private final SquareNode _redSquareNode, _blueSquareNode;
        
        public TestCanvas( TestModel model, double scale ) {
            super();
            getCamera().setViewScale( scale );
            
            setBorder( BorderFactory.createLineBorder( Color.BLACK, 1 ) );
            removeInputEventListener( getZoomEventHandler() );
            removeInputEventListener( getPanEventHandler() );
            
            _redSquareNode = new SquareNode( model.getRedSquare() );
            getLayer().addChild( _redSquareNode );
            _blueSquareNode = new SquareNode( model.getBlueSquare() );
            getLayer().addChild( _blueSquareNode );
        }
    }
    
    /** Implement this interface to be notified of changes to the pan control. */
    private interface PannerListener {
        public void positionChanged();
        public void sizeChanged();
    }
    
    /** Adapter for PannerListener */
    private static class PannerAdapter implements PannerListener {
        public void positionChanged() {};
        public void sizeChanged() {};
    }
    
    /** Panner describes the visible bounds of a portion of the model. */
    private static class Panner {
        
        private Point2D _position;
        private Dimension _size;
        private ArrayList _listeners;
        
        public Panner( Point2D position, Dimension size ) {
            _position = new Point2D.Double( position.getX(), position.getY() );
            _size = new Dimension( size );
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
        
        public void setSize( Dimension size ) {
            if ( !size.equals( _size ) ) {
                _size.setSize( size );
                notifySizeChanged();
            }
        }

        public Dimension getSize() {
            return new Dimension( _size );
        }
        
        public void addListener( PannerListener listener ) {
            _listeners.add( listener );
        }

        public void removeListener( PannerListener listener ) {
            _listeners.remove( listener );
        }
        
        private void notifyPositionChanged() {
            for ( int i = 0; i < _listeners.size(); i++ ) {
                Object listener = _listeners.get( i );
                if ( listener instanceof PannerListener ) {
                    ( (PannerListener) listener ).positionChanged();
                }
            }
        }
        
        private void notifySizeChanged() {
            for ( int i = 0; i < _listeners.size(); i++ ) {
                Object listener = _listeners.get( i );
                if ( listener instanceof PannerListener ) {
                    ( (PannerListener) listener ).sizeChanged();
                }
            }
        }
    }
    
    /** View of the panner */
    private static class PannerNode extends PPath {
        
        private Panner _panner;
        
        public PannerNode( Panner panner ) {
            super();
            
            _panner = panner;
            
            setPaint( null );
            setStroke( new BasicStroke( 6f ) );
            setStrokePaint( Color.GREEN );
            
            _panner.addListener( new PannerListener() {

                public void positionChanged() {
                    updatePosition();
                }

                public void sizeChanged() {
                    updateSize();
                }
                
            });
            
            addInputEventListener( new CursorHandler() );
            addInputEventListener( new PDragEventHandler() {

                private double _xOffset, _yOffset;

                protected void startDrag( PInputEvent event ) {
                    _xOffset = event.getPosition().getX() - _panner.getPosition().getX();
                    _yOffset = event.getPosition().getY() - _panner.getPosition().getY();
                    super.startDrag( event );
                }

                protected void drag( PInputEvent event ) {
                    double x = event.getPosition().getX() - _xOffset;
                    double y = event.getPosition().getY() - _yOffset;
                    _panner.setPosition( new Point2D.Double( x, y ) );
                }
            } );
            
            updatePosition();
            updateSize();
        }
        
        private void updatePosition() {
            setOffset( _panner.getPosition() );
        }
        
        private void updateSize() {
            Dimension size = _panner.getSize();
            Rectangle2D r = new Rectangle2D.Double( 0, 0, size.getWidth(), size.getHeight() );
            setPathTo( r );
        }

    }
    
    private TestCanvas bottomCanvas;
    private Panner panner;
    
    /** Main window, creates one model with two views. */
    public TestMultipleViews() {
        super();
        
        TestModel model = new TestModel();
        
        TestCanvas topCanvas = new TestCanvas( model, 0.5 /* scale */ );
        bottomCanvas = new TestCanvas( model, 1 /* scale */ );
        
        // panner controls what portion of the top view is shown in the bottom view
        panner = new Panner( new Point2D.Double( 0, 0 ), new Dimension( 1, 1 ) );
        PannerNode pannerNode = new PannerNode( panner );
        topCanvas.getLayer().addChild( pannerNode );
        
        bottomCanvas.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                handleBottomCanvasResized();
            }
        } );
        
        panner.addListener( new PannerAdapter() {
            public void positionChanged() {
                handlePannerPositionChanged();
            }
        });
        
        //XXX topCanvas is not visible, zero size?
//        JPanel panel = new JPanel( new BorderLayout() );
//        panel.add( topCanvas, BorderLayout.NORTH );
//        panel.add( bottomCanvas, BorderLayout.CENTER );
//        getContentPane().add( panel );
        
        Box box = new Box( BoxLayout.Y_AXIS );
        box.add( topCanvas );
        box.add( bottomCanvas );
        getContentPane().add( box );
        
        handleBottomCanvasResized();
        handlePannerPositionChanged();
    }
    
    /* when the bottom view is resized, resize the panner */
    private void handleBottomCanvasResized() {
        Rectangle2D r = bottomCanvas.getBounds();
        double scale = bottomCanvas.getCamera().getViewScale();
        panner.setSize( new Dimension( (int)( scale * r.getWidth() ), (int)( scale * r.getHeight() ) ) );
    }
    
    /* when the panner is moved, translate the bottom view's camera */
    private void handlePannerPositionChanged() {
        Point2D p = panner.getPosition();
        bottomCanvas.getCamera().setViewOffset( -p.getX(), -p.getY() );
    }
    
    public static void main( String args[] ) {
        JFrame frame = new TestMultipleViews();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( new Dimension( 640, 480 ) );
        frame.show();
    }
}
