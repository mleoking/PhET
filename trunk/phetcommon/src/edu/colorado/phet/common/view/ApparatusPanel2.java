/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * This is a base class for panels that contain graphic representations
 * of elements in the PhysicalSystem.
 * <p/>
 * The graphic objects to be displayed are maintained in "layers". Each layer can
 * contain any number of Graphic objects, and each layer has an integer "level"
 * associated with it. Layers are drawn in ascending order of their levels. The order
 * in which objects in a given level are drawn is undefined.
 * Test Comment.
 * <p/>
 *
 * @see edu.colorado.phet.common.view.graphics.Graphic
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ApparatusPanel2 extends ApparatusPanel {

    ////////////////////////////////////////////////////////////////////////////////////////
    // Class
    //
    public static final double LAYER_TOP = Double.POSITIVE_INFINITY;
    public static final double LAYER_BOTTOM = Double.NEGATIVE_INFINITY;
    public static final double LAYER_DEFAULT = 0;


    ///////////////////////////////////////////////////////////////////////////////////////
    // Instance
    //
    private BasicStroke borderStroke = new BasicStroke( 1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );
    GraphicLayerSet graphic;
    BufferedImage bImg;

    ArrayList graphicsSetups = new ArrayList();
    //    private boolean paintEnabled;
    private BufferStrategy strategy;
    private ArrayList rectangles = new ArrayList();
    private Rectangle repaintArea;

    private AffineTransform graphicTx = new AffineTransform();
    private AffineTransform mouseTx = new AffineTransform();
    private Rectangle orgBounds;
    private HashMap componentOrgLocationsMap = new HashMap();

    public ApparatusPanel2( BaseModel model ) {
        super( null );
        graphic = new GraphicLayerSet( this );
        // The following lines use a mouse processor in the model loop
        MouseProcessor mouseProcessor = new MouseProcessor( graphic );
        model.addModelElement( mouseProcessor );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );

        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                megapaintImmediately();
            }
        } );

        //        setOpaque( true );
        //        setDoubleBuffered( false );
        this.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                if( strategy == null ) {
                    strategy = SwingUtilities.getWindowAncestor( ApparatusPanel2.this ).getBufferStrategy();
                    if( !strategy.getCapabilities().isPageFlipping() ) {
                        System.out.println( "Page flipping not supported." );
                    }
                    if( strategy.getCapabilities().isFullScreenRequired() ) {
                        System.out.println( "Full screen is required for buffering." );
                    }
                }
            }
        } );

        this.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                if( orgBounds == null ) {
                    orgBounds = ApparatusPanel2.this.getBounds();
                    Component[] components = ApparatusPanel2.this.getComponents();
                    for( int i = 0; i < components.length; i++ ) {
                        Component component = components[i];
                        if( !componentOrgLocationsMap.containsKey( component ) ) {
                            componentOrgLocationsMap.put( component, new Point( component.getLocation() ) );
                        }
                    }
                }

                // Setup the affine transforms for graphics and mouse events
                double sx = ApparatusPanel2.this.getBounds().getWidth() / orgBounds.getWidth();
                double sy = ApparatusPanel2.this.getBounds().getHeight() / orgBounds.getHeight();
                // Using a single scale factor keeps the aspect ratio constant
                double s = Math.min( sx, sy );
                graphicTx = AffineTransform.getScaleInstance( s, s );
                try {
                    mouseTx = graphicTx.createInverse();
                }
                catch( NoninvertibleTransformException e1 ) {
                    e1.printStackTrace();
                }
                bImg = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );

                // Adjust the locations of Swing components
                Component[] components = ApparatusPanel2.this.getComponents();
                for( int i = 0; i < components.length; i++ ) {
                    Component component = components[i];
                    Point p = (Point)componentOrgLocationsMap.get( component );
                    if( p != null ) {
                        Point pNew = new Point( (int)( p.getX() * s ), (int)( p.getY() * s ) );
                        component.setLocation( pNew );
                    }
                }
            }
        } );
    }

    /**
     * Returns the AffineTransform used by the apparatus panel to size and place graphics
     *
     * @return the transform for this panel.
     */
    public AffineTransform getGraphicTx() {
        return graphicTx;
    }

    public void add( Component comp, Object constraints ) {
        componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
        super.add( comp, constraints );
    }

    public Component add( Component comp, int index ) {
        componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
        return super.add( comp, index );
    }

    public Component add( String name, Component comp ) {
        componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
        return super.add( name, comp );
    }


    //////////////////////////////////////////////////////////////////////////////////////
    // Rendering
    //
    public void paintImmediately() {
        megapaintImmediately();
    }

    public void megapaintImmediately() {
        if( rectangles.size() == 0 ) {
            return;
        }
        else {
            Rectangle union = (Rectangle)rectangles.remove( 0 );
            while( rectangles.size() > 0 ) {
                union = union.union( (Rectangle)rectangles.remove( 0 ) );
            }
            repaintArea = union;
            paintImmediately( union );
        }
    }

    public void repaint( long tm, int x, int y, int width, int height ) {
        megarepaint( x, y, width, height );
    }

    private void megarepaint( int x, int y, int width, int height ) {
        if( rectangles == null ) {
            rectangles = new ArrayList();
        }
        Rectangle r = new Rectangle( x, y, width, height );
        if( graphicTx != null ) {
            r = graphicTx.createTransformedShape( r ).getBounds();
        }
        rectangles.add( r );
    }

    public void repaint( Rectangle r ) {
        megarepaint( r.x, r.y, r.width, r.height );
        System.out.println( "r = " + r );
    }

    public void repaint() {
    }

    public void repaint( int x, int y, int width, int height ) {
        megarepaint( x, y, width, height );
    }

    public void repaint( long tm ) {
    }

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );
        drawIt( g2 );
        gs.restoreGraphics();
    }

    private void drawIt( Graphics2D g2 ) {
        if( repaintArea == null ) {
            repaintArea = this.getBounds();
        }
        long now = System.currentTimeMillis();
        g2.setBackground( super.getBackground() );
        g2.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        g2.clearRect( repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height );
        for( int i = 0; i < graphicsSetups.size(); i++ ) {
            GraphicsSetup graphicsSetup = (GraphicsSetup)graphicsSetups.get( i );
            graphicsSetup.setup( g2 );
        }

        GraphicsState gs = new GraphicsState( g2 );
        g2.transform( graphicTx );
        //        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        graphic.paint( g2 );
        //        g2.drawImage( bImg, new AffineTransform(), null );
        gs.restoreGraphics();
        Color origColor = g2.getColor();
        Stroke origStroke = g2.getStroke();

        g2.setColor( Color.black );
        g2.setStroke( borderStroke );
        Rectangle border = new Rectangle( 0, 0, (int)this.getBounds().getWidth() - 1, (int)this.getBounds().getHeight() - 1 );
        g2.draw( border );

        g2.setColor( origColor );
        g2.setStroke( origStroke );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Inner classes
    //    
    private class MouseProcessor implements ModelElement, MouseListener, MouseMotionListener {
        LinkedList mouseEventList;
        LinkedList mouseMotionEventList;
        private GraphicLayerSet handler;

        public MouseProcessor( GraphicLayerSet mouseDelegator ) {
            mouseEventList = new LinkedList();
            mouseMotionEventList = new LinkedList();
            this.handler = mouseDelegator;
        }

        public void stepInTime( double dt ) {
            processMouseEventList();
            processMouseMotionEventList();
        }


        /////////////////////////////////////////////////////////////////////////////////////////////
        // Mouse event handling
        //
        private void xformEventPt( MouseEvent event ) {
            Point2D.Double p = new Point2D.Double( event.getPoint().getX(), event.getPoint().getY() );
            mouseTx.transform( p, p );
            int dx = (int)( p.getX() - event.getPoint().getX() );
            int dy = (int)( p.getY() - event.getPoint().getY() );
            event.translatePoint( dx, dy );
        }

        public void addMouseEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseEventList ) {
                mouseEventList.add( event );
            }
        }

        public void addMouseMotionEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseMotionEventList ) {
                mouseMotionEventList.add( event );
            }
        }

        public void processMouseEventList() {
            MouseEvent event;
            while( mouseEventList.size() > 0 ) {
                synchronized( mouseEventList ) {
                    event = (MouseEvent)mouseEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        public void processMouseMotionEventList() {
            MouseEvent event;
            while( mouseMotionEventList.size() > 0 ) {
                synchronized( mouseMotionEventList ) {
                    event = (MouseEvent)mouseMotionEventList.removeFirst();
                }
                handleMouseEvent( event );
            }
        }

        private void handleMouseEvent( MouseEvent event ) {
            switch( event.getID() ) {
                case MouseEvent.MOUSE_CLICKED:
                    handler.fireMouseClicked( event );
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    handler.fireMouseDragged( event );
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    handler.fireMouseEntered( event );
                    break;
                case MouseEvent.MOUSE_EXITED:
                    handler.fireMouseExited( event );
                    break;
                case MouseEvent.MOUSE_MOVED:
                    handler.fireMouseMoved( event );
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    handler.fireMousePressed( event );
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    handler.fireMouseReleased( event );
                    break;
            }
        }

        public void mouseClicked( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseEntered( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseExited( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mousePressed( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseReleased( MouseEvent e ) {
            this.addMouseEvent( e );
        }

        public void mouseDragged( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }

        public void mouseMoved( MouseEvent e ) {
            this.addMouseMotionEvent( e );
        }
    }
}
