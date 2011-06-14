// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.piccolophet;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.patterns.Updatable;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;

/**
 * PhetPCanvas is an extension of Piccolo's canvas that provides
 * convenience methods for managing "screen" and "world" nodes.
 * <p/>
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
    private PhetRootPNode phetRootNode;
    private AffineTransform transform;

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
    public PhetPCanvas( PhetPCanvas.TransformStrategy worldTransformStrategy ) {

        // things look lousy while interacting & animating unless we set these
        setAnimatingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );
        setInteractingRenderQuality( PPaintContext.HIGH_QUALITY_RENDERING );

        this.worldTransformStrategy = worldTransformStrategy;

        this.phetRootNode = new PhetRootPNode();
        getLayer().addChild( phetRootNode );

        removeInputEventListener( getZoomEventHandler() );
        removeInputEventListener( getPanEventHandler() );

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
    }

    /**
     * See #2015, ensure that scaling and layout are updated when bounds change.
     * This must happen synchronously; if you schedule it in a ComponentEvent,
     * you will see the scaling and layout.
     */
    @Override
    public void setBounds( int x, int y, int w, int h ) {
        if ( getBounds().getX() != x || getBounds().getY() != y || getBounds().getWidth() != w || getBounds().getHeight() != h ) {
            super.setBounds( x, y, w, h );
            updateWorldScale();
            updateLayout();
        }
    }

    /**
     * Updates the layout when the canvas is resized.
     * Default implementation does nothing.
     * Subclasses should override this method.
     */
    protected void updateLayout() {
    }

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

    protected TransformStrategy getWorldTransformStrategy() {
        return worldTransformStrategy;
    }

    /**
     * Sets the scale for world nodes.
     *
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

    public void addWorldChild( PNode node ) {
        phetRootNode.addWorldChild( node );
    }

    public void addWorldChild( int index, PNode node ) {
        phetRootNode.addWorldChild( index, node );
    }

    /**
     * This may become deprecated (just use phetRootNode.removeChild)
     * <p/>
     * cmalley note: I don't think deprecating this is a good idea.
     * Nodes that are added via addWorldChild should be removed via
     * removeWorldChild. This method should be cleaned up, and it
     * should fail if we tried to remove a node that doesn't exist.
     *
     * @param node
     */
    public void removeWorldChild( PNode node ) {
        try {
            phetRootNode.removeWorldChild( node );
        }
        catch ( ArrayIndexOutOfBoundsException e ) {
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
     *
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
     * <p/>
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
     * CenteringBoxStrategy implements a transform strategy that varies with
     * the canvas size and that keeps center of the world in the middle of the
     * canvas.  This is as opposed to other transform strategies that may keep
     * the relative distance from an edge of the canvas to items in the canvas
     * as a constant.
     * <p/>
     * NOTE: This should be implemented as an extension of ViewportStrategy,
     * with viewport (x,y)=(0,0). It's not implemented that way because
     * the current implementation of ViewportStrategy flips the y axis,
     * and this may break some sims.
     */
    public static class CenteringBoxStrategy implements TransformStrategy {
        private PhetPCanvas phetPCanvas;
        private Dimension2D renderingSize;

        public CenteringBoxStrategy( PhetPCanvas phetPCanvas, Dimension2D renderingSize ) {
            this.phetPCanvas = phetPCanvas;
            this.renderingSize = renderingSize;
            phetPCanvas.addComponentListener( new ComponentAdapter() {
                public void componentShown( ComponentEvent e ) {
                    if ( CenteringBoxStrategy.this.renderingSize == null ) {
                        setRenderingSize();
                    }
                }
            } );
        }

        public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
            this.phetPCanvas = phetPCanvas;
        }

        public AffineTransform getTransform() {
            AffineTransform transform;
            if ( renderingSize == null && phetPCanvas.isVisible() ) {
                setRenderingSize();
            }
            if ( phetPCanvas.getWidth() > 0 && phetPCanvas.getHeight() > 0 ) {

                double sx = getScaleX();
                double sy = getScaleY();

                //use the smaller
                double scale = sx < sy ? sx : sy;
                scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

                Rectangle2D outputBox;

                if ( scale == sx ) {
                    outputBox = new Rectangle2D.Double( 0, ( phetPCanvas.getHeight() - phetPCanvas.getWidth() ) / 2,
                                                        phetPCanvas.getWidth(), phetPCanvas.getWidth() );
                }
                else {
                    outputBox = new Rectangle2D.Double( ( phetPCanvas.getWidth() - phetPCanvas.getHeight() ) / 2, 0,
                                                        phetPCanvas.getHeight(), phetPCanvas.getHeight() );
                }
                transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, renderingSize.getWidth(), renderingSize.getHeight() ),
                                                      outputBox, false ).getAffineTransform();
            }
            else {
                // Use a basic 1 to 1 transform in this case.
                transform = new AffineTransform();
            }

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
     * CenterWidthScaleHeight implements a transform strategy that varies with
     * the canvas size and that keeps center of the world in the middle of the
     * canvas, makes things get bigger when the world is enlarged height-wise,
     * and maintains the aspect ratio of items on the canvas.  This means that
     * if the user enlarges the window in the x direction only, they will see
     * more of the world and no sizes will change.  If they enlarge only the y
     * dimension, things will grow in both dimensions (in order to maintain
     * the aspect ratio).
     * <p/>
     * NOTE: This should be implemented as an extension of ViewportStrategy,
     * with viewport (x,y)=(0,0). It's not implemented that way because
     * the current implementation of ViewportStrategy flips the y axis,
     * and this may break some sims.
     */
    public static class CenterWidthScaleHeight implements TransformStrategy {
        private PhetPCanvas phetPCanvas;
        private final Dimension2D renderingSize;

        public CenterWidthScaleHeight( PhetPCanvas phetPCanvas, Dimension2D renderingSize ) {
            this.phetPCanvas = phetPCanvas;
            this.renderingSize = renderingSize;
        }

        public void setPhetPCanvas( PhetPCanvas phetPCanvas ) {
            this.phetPCanvas = phetPCanvas;
        }

        public AffineTransform getTransform() {
            AffineTransform transform;
            if ( phetPCanvas.getWidth() > 0 && phetPCanvas.getHeight() > 0 ) {

                // Scale based only on growth/shrinkage in the y dimension,
                // i.e. height.
                double scale = getScaleY();

                // Translate in order to keep things centered in the x direction.
                double translationX = ( phetPCanvas.getWidth() / 2 ) - ( ( renderingSize.getWidth() * scale ) / 2 );

                // Create the target rectangle.
                Rectangle2D outputBox;
                outputBox = new Rectangle2D.Double( translationX, 0, renderingSize.getWidth() * scale, renderingSize.getHeight() * scale );

                // Create the transform from the rendering size to the
                // (presumably new) canvas size.
                transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, renderingSize.getWidth(), renderingSize.getHeight() ),
                                                      outputBox, false ).getAffineTransform();
            }
            else {
                // Use a basic 1 to 1 transform in this case.
                transform = new AffineTransform();
            }

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
     * <p/>
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
                System.err.println( this.getClass().getName() + ": Warning: Sometimes in 1.5, getWidth() and getHeight() return negative values, causing troubles for this layout code." );
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

    /**
     * Centers a "stage" area, of a specified size, within the PhetPCanvas, scaling it up and down so it is always
     * entirely visible, and always at an aspect ratio of 1.
     *
     * @author Sam Reid
     * @author John Blanco
     */
    public static class CenteredStage implements TransformStrategy {
        private PhetPCanvas canvas;
        private Dimension2D stageSize;

        public CenteredStage( PhetPCanvas canvas, Dimension2D stageSize ) {
            this.canvas = canvas;
            this.stageSize = stageSize;
        }

        public AffineTransform getTransform() {
            double sx = ( (double) canvas.getWidth() ) / stageSize.getWidth();
            double sy = ( (double) canvas.getHeight() ) / stageSize.getHeight();

            //use the smaller and maintain aspect ratio so that circles don't become ellipses
            double scale = sx < sy ? sx : sy;
            scale = scale <= 0 ? 1.0 : scale;//if scale is negative or zero, just use scale=1

            AffineTransform transform = new AffineTransform();
            double scaledStageWidth = scale * stageSize.getWidth();
            double scaledStageHeight = scale * stageSize.getHeight();
            //center it in width and height
            transform.translate( canvas.getWidth() / 2 - scaledStageWidth / 2, canvas.getHeight() / 2 - scaledStageHeight / 2 );
            transform.scale( scale, scale );

            return transform;
        }
    }


}