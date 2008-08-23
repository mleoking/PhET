/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.forces1d.phetcommon.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import edu.colorado.phet.forces1d.phetcommon.model.BaseModel;
import edu.colorado.phet.forces1d.phetcommon.model.clock.AbstractClock;
import edu.colorado.phet.forces1d.phetcommon.util.EventChannel;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.forces1d.phetcommon.view.phetgraphics.PhetGraphics2D;
import edu.colorado.phet.forces1d.phetcommon.view.util.GraphicsState;
import edu.colorado.phet.forces1d.phetcommon.view.util.RectangleUtils;

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
 */
public class ApparatusPanel2 extends ApparatusPanel {

    private static final boolean DEBUG_OUTPUT_ENABLED = false;

    // Identifiers for different painting strategies
    public static final int DEFAULT_PAINT_STRATEGY = 1;
    public static final int OFFSCREEN_BUFFER_STRATEGY = 2;
    public static final int OFFSCREEN_BUFFER__DIRTY_REGION_STRATEGY = 3;

    private TransformManager transformManager;
    private PaintStrategy paintStrategy;

    private ArrayList rectangles = new ArrayList();
    private Rectangle repaintArea;

    private ScaledComponentLayout scaledComponentLayout;
    private PanelResizeHandler panelResizeHandler;
    private MouseProcessor mouseProcessor;
    private AbstractClock clock;

    /**
     * Creates a new ApparatusPanel2, observing the specified clock for paused-ness.
     *
     * @param clock
     */
    public ApparatusPanel2( AbstractClock clock ) {
        super( null );
        init( clock );
    }

    /**
     * This constructor adds a feature that allows PhetGraphics to get mouse events
     * when the model clock is paused.
     *
     * @param model
     * @param clock
     * @deprecated No longer requires a BaseModel.
     */
    public ApparatusPanel2( BaseModel model, AbstractClock clock ) {
        super( null );
        init( clock );
    }

    /**
     * @param model
     * @deprecated
     */
    public ApparatusPanel2( BaseModel model ) {
        super( null );
        init( null );
        throw new RuntimeException( "Don't work no more!" );
    }

    protected void init( AbstractClock clock ) {
        this.clock = clock;
        // The following lines use a mouse processor in the model loop
        mouseProcessor = new MouseProcessor( getGraphic(), clock );
        this.addMouseListener( mouseProcessor );
        this.addMouseMotionListener( mouseProcessor );
        this.addKeyListener( getGraphic().getKeyAdapter() );//TODO key events should go in processing thread as well.

        // Add a listener what will adjust things if the size of the panel changes
        panelResizeHandler = new PanelResizeHandler();
        this.addComponentListener( panelResizeHandler );
        transformManager = new TransformManager( this );
        paintStrategy = new DefaultPaintStrategy( this );
        scaledComponentLayout = new ScaledComponentLayout( this );
    }

    public void removePanelResizeHandler() {
        removeComponentListener( panelResizeHandler );
    }

    public void setPaintStrategyDisjoint() {
        paintStrategy = new DisjointRectanglePaintStrategy( this );
    }

    public void setPaintStrategy( int strategy ) {
        switch( strategy ) {
            case DEFAULT_PAINT_STRATEGY:
                paintStrategy = new DefaultPaintStrategy( this );
                break;
            case OFFSCREEN_BUFFER_STRATEGY:
                paintStrategy = new OffscreenBufferStrategy( this );
                break;
            case OFFSCREEN_BUFFER__DIRTY_REGION_STRATEGY:
                paintStrategy = new OffscreenBufferDirtyRegion( this );
                break;
            default:
                throw new RuntimeException( "Invalid paint strategy specified" );
        }
    }

    public TransformManager getTransformManager() {
        return transformManager;
    }

    /**
     * Paints the panel. Exactly how this is depends on if an offscreen buffer is being used,
     * or the union of dirty rectangles.
     */
    public void paint() {
        paintStrategy.paintImmediately();
    }

    /**
     * Sets the reference size for this panel. If the panel resizes after this, it will scale its graphicsTx using
     * its current size in relation to the reference size.
     * <p/>
     * This should be called as soon as the application knows that the apparatus panel is at its reference size.
     */
    public void setReferenceSize() {
        transformManager.setReferenceSize();
        scaledComponentLayout.saveSwingComponentCoordinates( 1.0 );
        setScale( 1.0 );

        // TODO: moved this here from init(). Decide whether it should stay here or move back
//        panelResizeHandler = new PanelResizeHandler();
//        this.addComponentListener( panelResizeHandler );

        // Set the canvas size
        determineCanvasSize();

        if ( DEBUG_OUTPUT_ENABLED ) {
            System.out.println( "ApparatusPanel2.setReferenceBounds: referenceBounds=" + transformManager.getReferenceBounds() );
        }
    }

    /**
     * Explicitly sets the apparatus panel's reference size to a specific dimension.
     *
     * @param renderingSize
     */
    public void setReferenceSize( Dimension renderingSize ) {
        setReferenceSize( renderingSize.width, renderingSize.height );
    }

    /**
     * Explicitly sets the apparatus panel's reference size to a specific dimension.     *
     *
     * @param width
     * @param height
     */
    public void setReferenceSize( int width, int height ) {
        transformManager.setReferenceSize( width, height );
        scaledComponentLayout.saveSwingComponentCoordinates( 1.0 );
    }

    /**
     * Tells if we are using an offscreen buffer or dirty rectangles
     *
     * @return
     */
    public boolean isUseOffscreenBuffer() {
        return paintStrategy instanceof OffscreenBufferStrategy;
    }

    /**
     * Specifies whether the panel is to paint to an offscreen buffer, then paint the buffer,
     * or paint using dirty rectangles.
     * This method chooses between the DefaultPaintStrategy and teh OffscreenBufferStrategy.
     *
     * @param useOffscreenBuffer
     */
    public void setUseOffscreenBuffer( boolean useOffscreenBuffer ) {
        this.paintStrategy = useOffscreenBuffer ? new OffscreenBufferStrategy( this ) : (PaintStrategy) new DefaultPaintStrategy( this );
        // Todo: Determine if the following two lines help or not
//        setOpaque( useOffscreenBuffer );
        setDoubleBuffered( !useOffscreenBuffer );
    }

    public void setUseOffscreenBufferDirtyRegion() {
        this.paintStrategy = new OffscreenBufferDirtyRegion( this );
    }

    /**
     * Handle mouse input, and eventually, keyboard input
     */
    public void handleUserInput() {
        mouseProcessor.handleUserInput();
    }

    /**
     * Returns the AffineTransform used by the apparatus panel to size and place graphics
     *
     * @return
     */
    public AffineTransform getGraphicTx() {
        return transformManager.getGraphicTx();
    }

    private void saveLocation( Component comp ) {
        scaledComponentLayout.saveLocation( comp );
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

    /**
     * @deprecated Use Paint();
     */
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
        repaint( x, y, width, height );
    }

    /**
     * Adds a dirty rectangle to the repaint list. Does not invoke a repaint itself.
     *
     * @param r
     */
    public void repaint( Rectangle r ) {
        repaint( r.x, r.y, r.width, r.height );
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
        if ( clock != null && clock.isPaused() ) {
            super.repaint();
        }
    }

    /**
     * Provided for backward compatibility
     *
     * @deprecated Use Paint()
     */
    public void megarepaintImmediately() {
        paintDirtyRectanglesImmediately();
    }

    /**
     * Paints immediately the union of dirty rectangles
     */
    private void paintDirtyRectanglesImmediately() {
        if ( rectangles.size() > 0 ) {
            Rectangle unionRectangle = RectangleUtils.union( rectangles );
            this.repaintArea = transformManager.transform( unionRectangle );
            paintImmediately( repaintArea );
            rectangles.clear();
        }
    }

    private void addRectangleToRepaintList( int x, int y, int width, int height ) {
        if ( height > 0 && width > 0 ) {
            Rectangle r = new Rectangle( x, y, width, height );
            rectangles.add( r );
        }
    }

    /**
     * Overriden as a noop so that nothing happens if a child component calls repaint(). The actions
     * taken by our superclasss' repaint( long tm) should only happen in the model loop.
     */
    public void repaint( long tm ) {
    }

    protected void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D) graphics;
        g2 = new PhetGraphics2D( g2 );
        if ( repaintArea == null ) {
            repaintArea = this.getBounds();
        }
        g2.setBackground( super.getBackground() );
//        g2.clearRect( 0, 0, this.getWidth(), this.getHeight() );
        Rectangle clipBounds = g2.getClipBounds();
//        System.out.println( "clipBounds = " + clipBounds );
        g2.clearRect( clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height );
//        g2.clearRect( repaintArea.x, repaintArea.y, repaintArea.width, repaintArea.height );
        setup( g2 );
        GraphicsState gs = new GraphicsState( g2 );
//        g2.transform( transformManager.getGraphicTx() );
        paintStrategy.render( g2, transformManager.getGraphicTx() );

        //remove the affine transform.
        gs.restoreGraphics();
        super.drawBorder( g2 );
    }

    /**
     * Gets the size of the drawing area (the canvas) that is available to clients.
     * This is the size of the apparatus panel, adjusted for scaling.
     * This method is intended for use by clients who need to know how big
     * an area is available for drawing.
     * <p/>
     * An example: The client is a "grid" that needs to cover all visible space in
     * the apparatus panel.  The apparatus panel's size is currently 500x250, and its
     * scaling is 0.5.  If the grid uses 500x250, it will only 25% of the
     * apparatus panel after scaling.  Using getCanvasSize adjusts for
     * scaling and returns 1000x500 (ie, 500/0.5 x 250/0.5).
     *
     * @return the size
     */
    public Dimension getCanvasSize() {
        return transformManager.getCanvasSize();
    }

    //-----------------------------------------------------------------
    // Resizing and scaling
    //-----------------------------------------------------------------

    private class PanelResizeHandler extends ComponentAdapter {

        public void componentResized( ComponentEvent e ) {
            if ( !transformManager.isReferenceSizeSet() ) {
                setReferenceSize();
            }
            else {
                // Setup the affine transforms for graphics and mouse events
                Rectangle referenceBounds = transformManager.getReferenceBounds();
                double sx = getWidth() / referenceBounds.getWidth();
                double sy = getHeight() / referenceBounds.getHeight();
                // Using a single scale factor keeps the aspect ratio constant
                double s = Math.min( sx, sy );
                setScale( s );
                determineCanvasSize();
            }
            paintStrategy.componentResized();
        }
    }

    /**
     * Computes the size of the canvas on which PhetGraphics attached to this panel are drawn.
     * If the size changed, an canvasSizeChanged is called on all ChangeListeners
     */
    private void determineCanvasSize() {
        boolean changed = transformManager.determineCanvasSize();
        if ( changed ) {
            changeListenerProxy.canvasSizeChanged( new ApparatusPanel2.ChangeEvent( ApparatusPanel2.this ) );
        }
    }

    public double getScale() {
        return transformManager.getScale();
    }

    public void setScale( double scale ) {
        transformManager.setScale( scale );
        scaledComponentLayout.layoutSwingComponents( scale );
        repaint( 0, 0, getWidth(), getHeight() );
    }

    //-------------------------------------------------------------------------
    // Inner classes
    //-------------------------------------------------------------------------

    /**
     * Handles mouse events in the model loop
     */
    private class MouseProcessor implements MouseInputListener {
        private LinkedList mouseEventList;
        private AbstractClock clock;
        private GraphicLayerSet handler;

        // The following Runnable is used to process mouse events when the model clock is paused
        private Runnable pausedEventListProcessor = new Runnable() {
            public void run() {
                MouseProcessor.this.handleUserInput();
                ApparatusPanel2.this.paint();
            }
        };

        public MouseProcessor( GraphicLayerSet mouseDelegator, final AbstractClock clock ) {
            this.clock = clock;
            mouseEventList = new LinkedList();
            this.handler = mouseDelegator;
        }

        public void handleUserInput() {
            processMouseEventList();
        }

        private void xformEventPt( MouseEvent event ) {
            Point2D.Double p = new Point2D.Double( event.getPoint().getX(), event.getPoint().getY() );
            AffineTransform mouseTx = transformManager.getMouseTx();
            mouseTx.transform( p, p );
            int dx = (int) ( p.getX() - event.getPoint().getX() );
            int dy = (int) ( p.getY() - event.getPoint().getY() );
            event.translatePoint( dx, dy );
        }

        private void addMouseEvent( MouseEvent event ) {
            xformEventPt( event );
            synchronized( mouseEventList ) {
                mouseEventList.add( event );
            }

            // If the clock is paused, then process mouse events
            // in the Swing thread
            if ( clock.isPaused() ) {
                SwingUtilities.invokeLater( pausedEventListProcessor );
            }
        }

        private void processMouseEventList() {
            MouseEvent event;
            while ( mouseEventList.size() > 0 ) {
                synchronized( mouseEventList ) {
                    event = (MouseEvent) mouseEventList.removeFirst();
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
            this.addMouseEvent( e );
        }

        public void mouseMoved( MouseEvent e ) {
            this.addMouseEvent( e );
        }
    }

    //-----------------------------------------------------------------
    // Event-related classes
    //-----------------------------------------------------------------
    public class ChangeEvent extends EventObject {
        public ChangeEvent( ApparatusPanel2 source ) {
            super( source );
        }

        public Dimension getCanvasSize() {
            return transformManager.getCanvasSize();
        }
    }

    public interface ChangeListener extends EventListener {
        void canvasSizeChanged( ChangeEvent event );
    }

    private EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    private ChangeListener changeListenerProxy = (ChangeListener) changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    //----------------------------------------------------------------
    // Rendering strategies
    //----------------------------------------------------------------

    static interface PaintStrategy {

        void paintImmediately();

        void render( Graphics2D g2, AffineTransform graphicTx );

        void componentResized();
    }

    private static class DisjointRectanglePaintStrategy implements PaintStrategy {

        ApparatusPanel2 apparatusPanel2;

        public DisjointRectanglePaintStrategy( ApparatusPanel2 apparatusPanel2 ) {
            this.apparatusPanel2 = apparatusPanel2;
            componentResized();
        }

        public void paintImmediately() {
            apparatusPanel2.paintImmediatelyDisjoint();
        }

        public void render( Graphics2D g2, AffineTransform graphicTx ) {
            g2.transform( graphicTx );
            apparatusPanel2.getGraphic().paint( g2 );
        }

        public void componentResized() {
        }
    }

    private void paintImmediatelyDisjoint() {
        for ( int i = 0; i < rectangles.size(); i++ ) {
            Rectangle rectangle = (Rectangle) rectangles.get( i );
            paintImmediately( transformManager.transform( rectangle ) );
        }
        rectangles.clear();
    }

    public class OffscreenBufferDirtyRegion extends OffscreenBufferStrategy {

        public OffscreenBufferDirtyRegion( ApparatusPanel2 apparatusPanel2 ) {
            super( apparatusPanel2 );
        }

        public void paintImmediately() {
            //TODO this works great, without the full-screen, but relies on having rectangles.
            apparatusPanel2.paintDirtyRectanglesImmediately();
        }

    }

    /**
     * Renders everything to an offscreen buffer, then draws the buffer to the screen at one time
     */
    public class OffscreenBufferStrategy implements PaintStrategy {
        private BufferedImage bImg;
        protected ApparatusPanel2 apparatusPanel2;
        private AffineTransform IDENTITY = new AffineTransform();
        private static final int BUFFER_TYPE = BufferedImage.TYPE_INT_RGB;//TODO Macs may need ARGB here

        public OffscreenBufferStrategy( ApparatusPanel2 apparatusPanel2 ) {
            this.apparatusPanel2 = apparatusPanel2;
            componentResized();
        }

        public void paintImmediately() {
            Rectangle region = new Rectangle( apparatusPanel2.getWidth(), apparatusPanel2.getHeight() );
            apparatusPanel2.paintImmediately( region );
            apparatusPanel2.rectangles.clear();
        }

        public void render( Graphics2D g2, AffineTransform graphicTx ) {
            if ( bImg == null ) {
                componentResized();
            }
            if ( bImg != null ) {
                Graphics2D bImgGraphics = (Graphics2D) bImg.getGraphics();
                //TODO: we'll be painting over this region, do we really have to clear it?
                //todo especially if our image has no alpha?
                bImgGraphics.setColor( apparatusPanel2.getBackground() );
                bImgGraphics.fillRect( bImg.getMinX(), bImg.getMinY(), bImg.getWidth(), bImg.getHeight() );

                setup( bImgGraphics );
//                bImgGraphics.setClip( g2.getClip() );//apply the clip to the buffer (in case we're not painting everything.)
                //TODO is clipping the bImgGraphics helping..?
                bImgGraphics.transform( graphicTx );
                apparatusPanel2.getGraphic().paint( bImgGraphics );
                g2.drawImage( bImg, IDENTITY, apparatusPanel2 );
                bImgGraphics.dispose();
            }
        }

        public void componentResized() {
            Rectangle r = new Rectangle( getWidth(), getHeight() );
            if ( r.width > 0 && r.height > 0 ) {
                bImg = new BufferedImage( r.width, r.height, BUFFER_TYPE );
            }
        }
    }

    public static class DefaultPaintStrategy implements PaintStrategy {
        ApparatusPanel2 apparatusPanel2;

        public DefaultPaintStrategy( ApparatusPanel2 apparatusPanel2 ) {
            this.apparatusPanel2 = apparatusPanel2;
            componentResized();
        }

        public void paintImmediately() {
            apparatusPanel2.paintDirtyRectanglesImmediately();
        }

        public void render( Graphics2D g2, AffineTransform graphicTx ) {
//            QuickTimer renderTime=new QuickTimer();
            g2.transform( graphicTx );
            apparatusPanel2.getGraphic().paint( g2 );
//            System.out.println( "renderTime = " + renderTime );
        }

        public void componentResized() {
        }
    }

    /**
     * Places Swing components in the proper places when the ApparatusPanel2 is resized
     */
    static class ScaledComponentLayout {
        private HashMap componentOrgLocationsMap = new HashMap();
        JComponent component;

        public ScaledComponentLayout( JComponent component ) {
            this.component = component;
        }

        private void saveSwingComponentCoordinates( double scale ) {
            Component[] components = component.getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                Component component = components[i];
                Point location = component.getLocation();

                // TEST
                Dimension refSize = component.getPreferredSize();
//                component.setSize( (int)(refSize.width * scale), (int)(refSize.height * scale ));

                //factor out the old scale, if any.
                componentOrgLocationsMap.put( component, new Point( (int) ( location.x / scale ), (int) ( location.y / scale ) ) );
            }
        }

        public void saveLocation( Component comp ) {
            componentOrgLocationsMap.put( comp, new Point( comp.getLocation() ) );
        }

        /**
         * Adjust the locations of Swing components based on the current scale
         */
        private void layoutSwingComponents( double scale ) {
            Component[] components = component.getComponents();
            for ( int i = 0; i < components.length; i++ ) {
                Component component = components[i];
                Point origLocation = (Point) componentOrgLocationsMap.get( component );
                if ( origLocation != null ) {
                    Point newLocation = new Point( (int) ( origLocation.getX() * scale ), (int) ( origLocation.getY() * scale ) );
                    component.setLocation( newLocation );
                }
            }
        }

    }
}

