/* Copyright 2003-2008, University of Colorado */

package edu.colorado.phet.common.piccolophet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * PhetPCanvas is an extension of Piccolo's canvas that provides 
 * convenience methods for managing "screen" and "world" nodes.
 * <p> 
 * "Screen" nodes are in screen coordinates and have an identity transform.
 * "World" nodes are intended to be in model coordinates, and can have some
 * arbitrary transform specified.
 * There is purposely no support for specifying a transform on screen
 * nodes; they are expected to have a unity transform, and a 1:1 mapping
 * to screen coordinates.
 */
public class PhetPCanvas extends PSwingCanvas implements Updatable {
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private TransformStrategy worldTransformStrategy;
    private ComponentAdapter resizeAdapter;
    private PhetRootPNode phetRootNode;
    private AffineTransform transform;
    private boolean layoutDirty;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructs a PhetPCanvas that uses an identify transform for world nodes.
     */
    public PhetPCanvas() {
        this( new ConstantTransformStrategy( new AffineTransform() ) );
    }

    /**
     * Constructs a PhetPCanvas with the size that will be used as the 
     * reference coordinate frame size for world nodes.
     * When the canvas is resized, world nodes will appear to be scaled
     * to fit the new canvas size.
     * 
     * @param renderingSize the reference coordinate frame size
     */
    public PhetPCanvas( Dimension2D renderingSize ) {
        this( new ConstantTransformStrategy( new AffineTransform() ) ); //HACK
        setWorldTransformStrategy( new RenderingSizeStrategy( this, renderingSize ) );
    }

    /**
     * Constructs a PhetPCanvas with a specified viewport for world nodes.
     * 
     * @param modelViewport
     */
    public PhetPCanvas( Rectangle2D modelViewport ) {
        this( new ConstantTransformStrategy( new AffineTransform() ) ); //HACK
        setWorldTransformStrategy( new ViewportStrategy( this, modelViewport ) );
    }
    
    /**
     * Constructs a PhetPCanvas with a specified world transform strategy.
     * 
     * @param worldTransformStrategy
     */
    public PhetPCanvas( TransformStrategy worldTransformStrategy ) {
        
        this.worldTransformStrategy = worldTransformStrategy;
        
        this.phetRootNode = new PhetRootPNode();
        getLayer().addChild( phetRootNode );
        
        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

        resizeAdapter = new ResizeAdapter();
        addComponentListener( resizeAdapter );
        addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                requestFocus();
            }
        } );
        
        /*
         * By default, a PhETPCanvas is opaque, that is, no components should be visible underneath this panel.
         * This allows for usage of performance improving facilities, such as immediate painting.
         */
        setOpaque( true );
        setBorder( BorderFactory.createLineBorder( Color.black ) );
        requestFocus();
        
        // update layout when the canvas is resized
        {
            layoutDirty = true;
            
            addComponentListener( new ComponentAdapter() {

                // if the component is resized while visible, update the layout.
                // otherwise, mark the layout as dirty.
                public void componentResized( ComponentEvent e ) {
                    if ( e.getComponent().isShowing() ) {
                        updateLayout();
                        layoutDirty = false;
                    }
                    else {
                        layoutDirty = true;
                    }
                }
            } );
            
            addAncestorListener( new AncestorListener() {

                // called when the source or one of its ancestors is make visible either
                // by setVisible(true) being called or by its being added to the component hierarchy
                public void ancestorAdded( AncestorEvent e ) {
                    if ( layoutDirty && e.getComponent().isShowing() ) {
                        updateLayout();
                        layoutDirty = false;
                    }
                }

                public void ancestorMoved( AncestorEvent event ) {}

                public void ancestorRemoved( AncestorEvent event ) {}
            } );
        }
    }
    
    /**
     * Updates the layout when the canvas is resized.
     * Default implementation does nothing.
     * Subclasses should override this method.
     */
    protected void updateLayout() {}
    
    //----------------------------------------------------------------------------
    // World transform management
    //----------------------------------------------------------------------------
    
    /**
     * Sets the transform strategy for world nodes.
     * 
     * @param transformStrategy
     */
    public void setWorldTransformStrategy( TransformStrategy transformStrategy ) {
        this.worldTransformStrategy = transformStrategy;
        updateWorldScale();
    }

    /*
     * Updates the scale for world nodes.
     */
    protected void updateWorldScale() {
        phetRootNode.setWorldTransform( worldTransformStrategy.getTransform() );
    }

    /**
     * Sets the scale for world nodes.
     * @param scale
     */
    public void setWorldScale( double scale ) {
        phetRootNode.setWorldScale( scale );
    }

    //----------------------------------------------------------------------------
    // Updatable implementation
    //----------------------------------------------------------------------------
    
    public void update() {
        update( phetRootNode );
    }

    /*
     * PLEASE DOCUMENT ME.
     */
    private void update( PNode node ) {
        if ( node instanceof Updatable ) {
            Updatable updatable = (Updatable) node;
            updatable.update();
        }

        for ( int i = 0; i < node.getChildrenCount(); i++ ) {
            update( node.getChild( i ) );
        }
    }

    //----------------------------------------------------------------------------
    
    /*
     * PLEASE DOCUMENT ME.
     */
    protected class ResizeAdapter extends ComponentAdapter {
        public void componentResized( ComponentEvent e ) {
            updateWorldScale();
        }

        public void componentShown( ComponentEvent e ) {
            updateWorldScale();
        }
    }

    /**
     * Gets the PhetRootPNode associated with the PCanvas.
     * 
     * @return PhetRootPNode
     */
    public PhetRootPNode getPhetRootNode() {
        return phetRootNode;
    }

    /**
     * Sets the PhetRootPNode associated with the PCanvas.
     * 
     * @param phetRootNode
     */
    public void setPhetRootNode( PhetRootPNode phetRootNode ) {
        if ( this.phetRootNode != null ) {
            getLayer().removeChild( this.phetRootNode );
        }
        this.phetRootNode = phetRootNode;
        getLayer().addChild( this.phetRootNode );
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for adding and removing nodes
    //----------------------------------------------------------------------------

    public void addScreenChild( PNode node ) {
        phetRootNode.addScreenChild( node );
    }

    public void addScreenChild( int index, PNode node ) {
        phetRootNode.addScreenChild( index, node );
    }

    public void removeScreenChild( PNode node ) {
        phetRootNode.removeChild( node );
    }

    public void addWorldChild( PNode graphic ) {
        phetRootNode.addWorldChild( graphic );
    }

    public void addWorldChild( int index, PNode graphic ) {
        phetRootNode.addWorldChild( index, graphic );
    }

    /**
     * This may become deprecated (just use phetRootNode.removeChild)
     *
     * @param graphic
     */
    public void removeWorldChild( PNode graphic ) {
        try {
            phetRootNode.removeWorldChild( graphic );
        }
        catch( ArrayIndexOutOfBoundsException e ) {
            // Hack because Piccolo can't be modified
            // It doesn't expose world children so we can't
            // safely check for their presence
        }
    }
    
    //----------------------------------------------------------------------------
    // Convenience methods for setting Piccolo debug flags
    //----------------------------------------------------------------------------
    
    public static void setDebugRegionManagement( boolean debugRegionManagement ) {
        PDebug.debugRegionManagement = debugRegionManagement;
    }

    public static void setDebugFrameRateToConsole( boolean frameRateToConsole ) {
        PDebug.debugPrintFrameRate = frameRateToConsole;
    }

    public static void setDebugFullBounds( boolean debugFullBounds ) {
        PDebug.debugFullBounds = debugFullBounds;
    }

    //----------------------------------------------------------------------------
    
    /**
     * Gets the size of the canvas in screen coordinates.
     *
     * @return Dimension2D
     */
    public Dimension2D getScreenSize() {
        return new PDimension( getWidth(), getHeight() );
    }

    /**
     * Gets the size of the canvas is world coordinates.
     *
     * @return Dimension2D
     */
    public Dimension2D getWorldSize() {
        Dimension2D dim = getScreenSize();
        getPhetRootNode().screenToWorld( dim ); // modifies dim!
        return dim;
    }

    /**
     * Gets the transform that was used for the most recent paintComponent call.
     * TODO: WHY WOULD WE NEED THIS?
     *
     * @return AffineTransform, null if paintComponent hasn't been called yet
     */
    public AffineTransform getTransform() {
        return transform;
    }
    
    /**
     * Remembers the AffineTransform that was used to paint the canvas.
     * TODO: BUT WHY?!?!
     *
     * @param g
     */
    public void paintComponent( Graphics g ) {
        transform = ( (Graphics2D) g ).getTransform();
        super.paintComponent( g );
    }

    /**
     * Adds an activity to the root node.
     */
    public void addActivity( PActivity activity ) {
        getRoot().addActivity( activity );
    }

    /**
     * Removes an activity from the root node.
     * @param activity
     */
    public void removeActivity( PActivity activity ) {
        getRoot().getActivityScheduler().removeActivity( activity );
    }
    
    //----------------------------------------------------------------------------
    // Transform strategies
    //----------------------------------------------------------------------------

    /**
     * TransformStrategy is the interface implemented by all transform strategies.
     */
    public static interface TransformStrategy {
        AffineTransform getTransform();
    }

    /**
     * ConstantTransformStrategy implements a constant transform
     * that doesn't vary with the canvas size.
     */
    public static class ConstantTransformStrategy implements TransformStrategy {
        private AffineTransform affineTransform;

        public ConstantTransformStrategy( AffineTransform affineTransform ) {
            this.affineTransform = affineTransform;
        }

        public AffineTransform getTransform() {
            return new AffineTransform( affineTransform );
        }
    }

    /**
     * RenderingSizeStrategy implements a transform strategy that varies 
     * with the canvas size.  As the canvas is resized, the transform is
     * varied based on a reference rendering size.
     * 
     * NOTE: This should be implemented as an extension of ViewportStrategy,
     * with viewport (x,y)=(0,0). It's not implemented that way because 
     * the current implementation of ViewportStrategy flips the y axis,
     * and this may break some sims.
     */
    public static class RenderingSizeStrategy implements TransformStrategy {
        private PhetPCanvas phetPCanvas;
        private Dimension2D renderingSize;

        public RenderingSizeStrategy( PhetPCanvas phetPCanvas, Dimension2D renderingSize ) {
            this.phetPCanvas = phetPCanvas;
            this.renderingSize = renderingSize;
            phetPCanvas.addComponentListener( new ComponentAdapter() {
                public void componentShown( ComponentEvent e ) {
                    if ( RenderingSizeStrategy.this.renderingSize == null ) {
                        setRenderingSize();
                    }
                }
            } );
        }

        public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
            this.phetPCanvas = phetPCanvas;
        }

        public AffineTransform getTransform() {
            if ( renderingSize == null && phetPCanvas.isVisible() ) {
                setRenderingSize();
            }
            double sx = getScaleX();
            double sy = getScaleY();

            //use the smaller
            double scale = sx < sy ? sx : sy;
            scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

            AffineTransform transform = getPreprocessedTransform();
            
            transform.scale( scale, scale );
            
            return transform;
        }
        
        /**
         * This method returns the transform for this canvas, and is intended
         * to be overridden in subclasses that need to perform transforms other
         * than straight scaling.
         * 
         * @return The current affine transform.
         */
        protected AffineTransform getPreprocessedTransform() {
            return new AffineTransform();
        }

        private void setRenderingSize() {
            setRenderingSize( phetPCanvas.getSize() );
        }

        public void setRenderingSize( Dimension dim ) {
            this.renderingSize = new Dimension( dim );
        }

        public void setRenderingSize( int width, int height ) {
            setRenderingSize( new Dimension( width, height ) );
        }

        private double getScaleY() {
            return ( (double) phetPCanvas.getHeight() ) / renderingSize.getHeight();
        }

        private double getScaleX() {
            return ( (double) phetPCanvas.getWidth() ) / renderingSize.getWidth();
        }
    }

    /**
     * ViewportStrategy implements a transform strategy that varies 
     * with the canvas size.  As the canvas is resized, the transform is
     * varied based on a reference viewport.
     * <p>
     * Note that this implementation flips the y axis.
     */
    public static class ViewportStrategy implements TransformStrategy {
        private Rectangle2D modelViewport;
        private PhetPCanvas phetPCanvas;

        public ViewportStrategy( PhetPCanvas phetPCanvas, Rectangle2D modelViewport ) {
            this.phetPCanvas = phetPCanvas;
            this.modelViewport = modelViewport;
        }

        protected double getScaleY() {
            return phetPCanvas.getHeight() / modelViewport.getHeight();
        }

        protected double getScaleX() {
            return phetPCanvas.getWidth() / modelViewport.getWidth();
        }

        public void componentShown( ComponentEvent e ) {
        }

        public AffineTransform getTransform() {
            double sx = getScaleX();
            double sy = getScaleY();

            //use the smaller
            double scale = sx < sy ? sx : sy;
            if ( scale < 0 ) {
                System.err.println( this.getClass().getName() + ": Warning: Sometimes in 1.5, sometimes getWidth() and getHeight() return negative values, causing troubles for this layout code." );
            }
            if ( scale != 0.0 ) {
                AffineTransform worldTransform = new AffineTransform();
                worldTransform.translate( 0, phetPCanvas.getHeight() );
                worldTransform.scale( scale, -scale );
                worldTransform.translate( modelViewport.getX(), -modelViewport.getY() );
                return worldTransform;
            }
            else {
//                System.err.println( "Scale evaluated to zero!" );//removed debugging statements
            }
            return new AffineTransform();
        }
    }
}