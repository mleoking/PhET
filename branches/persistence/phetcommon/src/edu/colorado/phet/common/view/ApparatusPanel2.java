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
import edu.colorado.phet.common.model.clock.*;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.util.GraphicsState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
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
 * <p/>
 * The differences between this class and ApparatusPanel are:
 * <ul>
 * <li>The graphic objects in the panel setReferenceSize when the panel is resized
 * <li>Mouse events are handled in the model loop, not the Swing event dispatch thread
 * <li>An option allows drawing to be done to an offscreen buffer, then the whole buffer
 * written at one time to the graphics card
 * </ul>
 *
 * @author Ron LeMaster
 * @version $Revision$
 * @see edu.colorado.phet.common.view.graphics.Graphic
 */
public class ApparatusPanel2 extends ApparatusPanel {

//    private PaintStrategy =
    //test comment
    private BufferedImage bImg;
    private boolean useOffscreenBuffer = false;
//    private BufferStrategy strategy;
    private ArrayList rectangles = new ArrayList();
    private Rectangle repaintArea;

    private AffineTransform graphicTx = new AffineTransform();
    private AffineTransform mouseTx = new AffineTransform();
    private Rectangle referenceBounds;
    private HashMap componentOrgLocationsMap = new HashMap();
    private boolean modelPaused = false;
    private double scale = 1.0;
    private boolean referenceSizeSet;
    protected ClockTickListener paintTickListener;
    protected ModelElement paintModelElement;

    /**
     * This constructor adds a feature that allows PhetGraphics to get mouse events
     * when the model clock is paused.
     *
     * @param model
     * @param clock
     */
    public ApparatusPanel2( BaseModel model, AbstractClock clock ) {
        super( null );
        init( model );

        // Add a listener that keeps track of when the clock is paused and unpaused. this
        // is needed so the mouse will still work if the clock is paused.
        clock.addClockStateListener( new ClockStateListener() {
            public void delayChanged( int waitTime ) {
            }

            public void dtChanged( double dt ) {
            }

            public void threadPriorityChanged( int priority ) {
            }

            public void pausedStateChanged( boolean isPaused ) {
                modelPaused = isPaused;
            }

            public void stateChanged( ClockStateEvent event ) {
            }
        } );
        modelPaused = clock.isPaused();

//        // Add a clock tick listener that paints the screen on every tick
        paintTickListener = new ClockTickListener() {
            public void clockTicked( ClockTickEvent event ) {
                paint();
            }
        };
        clock.addClockTickListener( paintTickListener );
        //I need more fine grained control, since I have two modules using the same clock.  Isn't this the normal thing?
        //Also, commenting this out didn't change behavior of force1d.
    }

    /**
     * @param model
     * @deprecated
     */
    public ApparatusPanel2( BaseModel model ) {
        super( null );
        init( model );
        model.addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                paint();
            }
        } );
    }

    protected void init( BaseModel model ) {
        // The following lines use a mouse processor in the model loop
        MouseProcessor mouseProcessor = new MouseProcessor( getGraphic() );
        model.addModelElement( mouseProcessor );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );
        this.addKeyListener( getGraphic().getKeyAdapter() );//TODO key events should go in processing thread as well.

//        // Add a model element that paints the panile
        paintModelElement = new ModelElement() {
            public void stepInTime( double dt ) {
                paint();
            }
        };
        model.addModelElement( paintModelElement );
        //I need more fine grained control, since I have two modules using the same clock.  Isn't this the normal thing?

        // Add a listener what will adjust things if the size of the panel changes
        this.addComponentListener( new ComponentAdapter() {

            public void componentResized( ComponentEvent e ) {
                if( !referenceSizeSet ) {
                    setReferenceSize();
                }
                else {
                    // Setup the affine transforms for graphics and mouse events
                    double sx = getWidth() / referenceBounds.getWidth();
                    double sy = getHeight() / referenceBounds.getHeight();
                    // Using a single scale factor keeps the aspect ratio constant
                    double s = Math.min( sx, sy );
                    setScale( s );
                }
                bImg = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB );
            }
        } );
    }

    /**
     * Paints the panel. Exactly how this is depends on if an offscreen buffer is being used,
     * or the union of dirty rectangles.
     */
    public void paint() {
        //TODO: even if we use an offscreen buffer, we could still just throw the changed part to the screen.
        if( useOffscreenBuffer ) {
//          Rectangle region = RectangleUtils.union( (Rectangle[])rectangles.toArray( new Rectangle[0] ) );
            Rectangle region = new Rectangle( 0, 0, getWidth(), getHeight() );
            paintImmediately( region );
        }
        else {
            paintDirtyRectanglesImmediately();
        }
        // Clear the rectangles so they get garbage collectged
        rectangles.clear();
    }

    /**
     * Sets the reference size for this panel. If the panel resizes after this, it will scale its graphicsTx using
     * its current size in relation to the reference size
     */
    public void setReferenceSize() {
        referenceSizeSet = true;
        referenceBounds = ApparatusPanel2.this.getBounds();
        saveSwingComponentCoordinates( 1.0 );
        setScale( 1.0 );
        paintImmediately( 0, 0, getWidth(), getHeight() );

        System.out.println( "referenceBounds = " + referenceBounds );
    }

    private void saveSwingComponentCoordinates( double scale ) {
        Component[] components = getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            Point location = component.getLocation();
            //factor out the old scale, if any.
            componentOrgLocationsMap.put( component, new Point( (int)( location.x / scale ), (int)( location.y / scale ) ) );
        }
    }

    /**
     * Tells if we are using an offscreen buffer or dirty rectangles
     *
     * @return
     */
    public boolean isUseOffscreenBuffer() {
        return useOffscreenBuffer;
    }

    /**
     * Specifies whether the panel is to paint to an offscreen buffer, then paint the buffer,
     * or paint using dirty rectangles.
     *
     * @param useOffscreenBuffer
     */
    public void setUseOffscreenBuffer( boolean useOffscreenBuffer ) {
        // Todo: Determine if the following two lines help or not
//        setOpaque( useOffscreenBuffer );
        setDoubleBuffered( !useOffscreenBuffer );
        this.useOffscreenBuffer = useOffscreenBuffer;
    }

    /**
     * Returns the AffineTransform used by the apparatus panel to size and place graphics
     *
     * @return
     */
    public AffineTransform getGraphicTx() {
        return graphicTx;
    }

    private void saveLocation( Component comp ) {
        componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
    }

    public Component add( Component comp ) {
        saveLocation( comp );
        return super.add( comp );
    }

    public void add( Component comp, Object constraints ) {
        saveLocation( comp );
        super.add( comp, constraints );
    }

    public Component add( Component comp, int index ) {
        saveLocation( comp );
        return super.add( comp, index );
    }

    public Component add( String name, Component comp ) {
        saveLocation( comp );
        return super.add( name, comp );
    }

    //-------------------------------------------------------------------------
    // Rendering
    //-------------------------------------------------------------------------

    public void paintImmediately() {
        paintDirtyRectanglesImmediately();
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param tm
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void repaint( long tm, int x, int y, int width, int height ) {
        addRectangleToRepaintList( x, y, width, height );
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param r
     */
    public void repaint( Rectangle r ) {
        addRectangleToRepaintList( r.x, r.y, r.width, r.height );
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public void repaint( int x, int y, int width, int height ) {
        addRectangleToRepaintList( x, y, width, height );
    }

    /**
     * Overriden as a noop so that nothing happens if a child component calls repaint(). The actions
     * taken by our superclasss' repaint() should only happen in the model loop.
     */
    public void repaint() {
    }

    /**
     * Provided for backward compatibility
     */
    public void megarepaintImmediately() {
        paintDirtyRectanglesImmediately();
    }

    /**
     * Paints immediately the union of dirty rectangles
     */
    private void paintDirtyRectanglesImmediately() {
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

    private void addRectangleToRepaintList( int x, int y, int width, int height ) {
        if( rectangles == null ) {
            rectangles = new ArrayList();
        }
        Rectangle r = new Rectangle( x, y, width, height );
        if( graphicTx != null ) {
            r = graphicTx.createTransformedShape( r ).getBounds();//TODO I thought PhetGraphics should return their exact screen bounds on a call to phetGraphic.getBounds(), which are the x,y,width,height in this method.
            //TODO maybe if we just change the transform on the GraphicLayerSet in this object, this would be automatic, and cleaner.
        }
        rectangles.add( r );
    }

    /**
     * Overriden as a noop so that nothing happens if a child component calls repaint(). The actions
     * taken by our superclasss' repaint( long tm) should only happen in the model loop.
     */
    public void repaint( long tm ) {
    }

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        if( repaintArea == null ) {
            repaintArea = this.getBounds();
        }
        g2.setBackground( super.getBackground() );
//        g2.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        Rectangle clipBounds = g2.getClipBounds();
        g2.clearRect( clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height );
//        g2.clearRect( repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height );
        for( int i = 0; i < getGraphicsSetups().size(); i++ ) {
            GraphicsSetup graphicsSetup = (GraphicsSetup)getGraphicsSetups().get( i );
            graphicsSetup.setup( g2 );
        }

        GraphicsState gs = new GraphicsState( g2 );
        g2.transform( graphicTx );
        if( useOffscreenBuffer && bImg != null ) {
            Graphics2D bImgGraphics = (Graphics2D)bImg.getGraphics();
            bImgGraphics.setColor( this.getBackground() );
            bImgGraphics.fillRect( bImg.getMinX(), bImg.getMinY(), bImg.getWidth(), bImg.getHeight() );
            getGraphic().paint( bImgGraphics );
            g2.drawImage( bImg, new AffineTransform(), null );
            bImgGraphics.dispose();
        }
        else {
            getGraphic().paint( g2 );
        }
        //remove the affine transform.
        gs.restoreGraphics();
        super.drawBorder( g2 );
        //restore color and stroke.
        gs.restoreGraphics();
    }

    public double getScale() {
        return scale;
    }

    public void setScale( double scale ) {
        graphicTx = AffineTransform.getScaleInstance( scale, scale );
        this.scale = scale;
        System.out.println( "Set graphics to scale: " + scale );
        try {
            mouseTx = graphicTx.createInverse();
        }
        catch( NoninvertibleTransformException e1 ) {
            e1.printStackTrace();
        }
        layoutSwingComponents();
    }

    /**
     * Adjust the locations of Swing components based on the current scale
     */
    private void layoutSwingComponents() {
        Component[] components = ApparatusPanel2.this.getComponents();
        for( int i = 0; i < components.length; i++ ) {
            Component component = components[i];
            Point origLocation = (Point)componentOrgLocationsMap.get( component );
            if( origLocation != null ) {
                Point newLocation = new Point( (int)( origLocation.getX() * scale ), (int)( origLocation.getY() * scale ) );
                component.setLocation( newLocation );
            }
        }
    }

    /**
     * todo: is this used anywhere?
     *
     * @return
     */
    protected Rectangle getReferenceBounds() {
        return referenceBounds;
    }

    //-------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------

    /**
     * Handles mouse events in the model loop
     */
    private class MouseProcessor implements ModelElement, MouseListener, MouseMotionListener {
        LinkedList mouseEventList;
        LinkedList mouseMotionEventList;
        private GraphicLayerSet handler;
        // The following Runnable is used to process mouse events when the model clock is paused
        private Runnable pausedEventListProcessor = new Runnable() {
            public void run() {
                stepInTime( 0 );
                ApparatusPanel2.this.paintDirtyRectanglesImmediately();
            }
        };

        public MouseProcessor( GraphicLayerSet mouseDelegator ) {
            mouseEventList = new LinkedList();
            mouseMotionEventList = new LinkedList();
            this.handler = mouseDelegator;
        }

        public void stepInTime( double dt ) {
            processMouseEventList();
            processMouseMotionEventList();
        }

        private void xformEventPt( MouseEvent event ) {
            Point2D.Double p = new Point2D.Double( event.getPoint().getX(), event.getPoint().getY() );
            mouseTx.transform( p, p );
            int dx = (int)( p.getX() - event.getPoint().getX() );
            int dy = (int)( p.getY() - event.getPoint().getY() );
            event.translatePoint( dx, dy );
        }

        private void addMouseEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseEventList ) {
                mouseEventList.add( event );
            }

            // If the clock is paused, then process mouse events
            // in the Swing thread
            if( modelPaused ) {
                SwingUtilities.invokeLater( pausedEventListProcessor );
            }
        }

        private void addMouseMotionEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseMotionEventList ) {
                mouseMotionEventList.add( event );
            }
            // If the clock is paused, then process mouse events
            // in the Swing thread
            if( modelPaused ) {
                SwingUtilities.invokeLater( pausedEventListProcessor );
            }
        }

        private void processMouseEventList() {
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
                    handler.getMouseHandler().mouseClicked( event );
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    handler.getMouseHandler().mouseDragged( event );
                    break;
                case MouseEvent.MOUSE_ENTERED:
                    handler.getMouseHandler().mouseEntered( event );
                    break;
                case MouseEvent.MOUSE_EXITED:
                    handler.getMouseHandler().mouseExited( event );
                    break;
                case MouseEvent.MOUSE_MOVED:
                    handler.getMouseHandler().mouseMoved( event );
                    break;
                case MouseEvent.MOUSE_PRESSED:
                    handler.getMouseHandler().mousePressed( event );
                    break;
                case MouseEvent.MOUSE_RELEASED:
                    handler.getMouseHandler().mouseReleased( event );
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

