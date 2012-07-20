// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.jmephet.hud;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.jmephet.CanvasTransform;
import edu.colorado.phet.jmephet.CanvasTransform.IdentityCanvasTransform;
import edu.colorado.phet.jmephet.JMETab;
import edu.colorado.phet.jmephet.JMEUtils;
import edu.colorado.phet.jmephet.input.JMEInputHandler;
import edu.umd.cs.piccolo.util.PBounds;

import com.jme3.scene.Node;

/**
 * Creates a JME node that displays a Swing component, keeps it up-to-date, and
 * can resize its underlying 3D representation when the component resizes
 */
public class SwingJMENode extends Node {

    // position property (offset from the lower-left, x to the right, y up. Modify on the Swing EDT!!!
    public final Property<Vector2D> position;

    // TODO: remove this?
    public final VoidNotifier onResize = new VoidNotifier(); // notifier that fires when this node is resized

    private final JComponent component;
    private final JMEInputHandler inputHandler;
    private final JMETab tab;
    private final CanvasTransform canvasTransform;

    private volatile Dimension size = new Dimension(); // our current size
    private volatile HUDNode hudNode; // the current node displaying our component

    private boolean ignoreInput = false;

    /**
     * Basically whether this node should be antialiased. If it is set up in a position where the texture (image)
     * pixels are not 1-to-1 with the screen pixels (say, translated by fractions of a pixel, or any rotation),
     * this should be set to true.
     */
    public final Property<Boolean> antialiased = new Property<Boolean>( false );

    public SwingJMENode( final JComponent component, final JMEInputHandler inputHandler, final JMETab tab ) {
        this( component, inputHandler, tab, getDefaultTransform() );
    }

    public SwingJMENode( final JComponent component, final JMEInputHandler inputHandler, final JMETab tab, CanvasTransform canvasTransform ) {
        this( component, inputHandler, tab, canvasTransform, getDefaultPosition() );
    }

    public SwingJMENode( final JComponent component, final JMEInputHandler inputHandler, final JMETab tab, CanvasTransform canvasTransform, Property<Vector2D> position ) {
        this.component = component;
        this.inputHandler = inputHandler;
        this.tab = tab;
        this.canvasTransform = canvasTransform;
        this.position = position;

        size = component.getPreferredSize();

        // ensure that we have it at its preferred size before sizing and painting
        component.setSize( component.getPreferredSize() );

        component.setDoubleBuffered( false ); // avoids having the RepaintManager attempt to get the containing window (and throw a NPE)

        // by default, the components should usually be transparent
        component.setOpaque( false );

        // when our component resizes, we need to handle it!
        component.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                final Dimension componentSize = component.getPreferredSize();
                if ( !componentSize.equals( size ) ) {
                    // update the size if it changed
                    size = componentSize;

                    rebuildHUD();

                    // run notifications in the JME thread
                    JMEUtils.invoke( new Runnable() {
                        public void run() {
                            // notify that we resized
                            onResize.updateListeners();
                        }
                    } );
                }
            }
        } );
        position.addObserver( new SimpleObserver() {
            public void update() {
                rebuildHUD();
            }
        }, false );
        canvasTransform.transform.addObserver( new SimpleObserver() {
            public void update() {
                rebuildHUD();
            }
        } );
    }

    // TODO: doc
    public Rectangle2D transformBoundsToStage( Rectangle2D localBounds ) {
        // get the translation of the control panel
        float offsetX = (float) position.get().getX();
        float offsetY = (float) position.get().getY();

        // convert these to stage-offset from the lower-left, since the control panel itself is translated
        double localLeft = localBounds.getMinX() + offsetX;
        double localRight = localBounds.getMaxX() + offsetX;
        double localTop = getComponentHeight() - localBounds.getMinY() + offsetY; // remember, Y is flipped here
        double localBottom = getComponentHeight() - localBounds.getMaxY() + offsetY; // remember, Y is flipped here

        return new PBounds( localLeft, localBottom, localRight - localLeft, localTop - localBottom );
    }

    /*---------------------------------------------------------------------------*
    * defaults
    *----------------------------------------------------------------------------*/

    public static CanvasTransform getDefaultTransform() {
        return new IdentityCanvasTransform();
    }

    public static Property<Vector2D> getDefaultPosition() {
        return new Property<Vector2D>( new Vector2D() );
    }

    // flags the HUD node as needing a full repaint
    public void repaint() {
        if ( hudNode != null ) {
            hudNode.repaint();
        }
    }

    // if necessary, creates a new HUD node of a different size to display our component
    public synchronized void rebuildHUD() {
        /*
         * Here, we basically take our integral component coordinates and find out where (after our projection
         * transformation) we should actually place the component. Usually it ends up with fractional coordinates.
         * Since we want to keep the HUD node's offset as an integral number so that the HUD pixels map exactly to
         * the screen pixels, we need to essentially compute the rectangle with integral coordinates that contains
         * all of our non-integral transformed component (IE, offsetX, offsetY, hudWidth, hudHeight). We then
         * position the HUD at those coordinates, and pass in the scale and slight offset so that our Graphics2D
         * calls paint it at the precise sub-pixel location.
         */

        // these are our stage bounds, relative to the SwingJMENode's location
        PBounds localBounds = new PBounds( position.get().getX(), position.get().getY(), size.width, size.height );

        // here we calculate our actual JME bounds
        Rectangle2D transformedBounds = canvasTransform.getTransformedBounds( localBounds );

        // for rendering the image, we need to know how much to scale it by
        final double scaleX = transformedBounds.getWidth() / localBounds.getWidth();
        final double scaleY = transformedBounds.getHeight() / localBounds.getHeight();

        // find the largest integer offsets that allow us to cover the entire renderable area
        final int offsetX = (int) Math.floor( transformedBounds.getMinX() ); // int truncation isn't good for the negatives here
        final int offsetY = (int) Math.floor( transformedBounds.getMinY() );

        // get how much we need to offset our rendered image by for sub-pixel accuracy (since we translate by offsetX/Y, we need to render at the difference)
        final double imageOffsetX = transformedBounds.getMinX() - offsetX;
        final double imageOffsetY = Math.ceil( transformedBounds.getMaxY() ) - transformedBounds.getMaxY(); // reversed Y handling

        // how large our HUD node needs to be as a raster to render all of our content
        final int hudWidth = ( (int) Math.ceil( transformedBounds.getMaxX() ) ) - offsetX;
        final int hudHeight = ( (int) Math.ceil( transformedBounds.getMaxY() ) ) - offsetY;

        // debugging for the translation image-offset issues
//        if ( this instanceof PiccoloJMENode ) {
//            PiccoloJMENode pthis = (PiccoloJMENode) this;
//            PNode node = pthis.getNode();
//            if ( node != null && node.getClass().getName().equals( "edu.colorado.phet.moleculeshapes.control.MoleculeShapesControlPanel" ) ) {
//                System.out.println( "----" );
//                System.out.println( "hash: " + node.hashCode() );
//                System.out.println( "canvas: " + module.getCanvasSize() );
//                System.out.println( "position: " + position.get() );
//                System.out.println( "localBounds: " + localBounds );
//                System.out.println( "transformedBounds: " + transformedBounds );
//                System.out.println( "scales: " + scaleX + ", " + scaleY );
//                System.out.println( "offsets: " + offsetX + ", " + offsetY );
//                System.out.println( "image offsets: " + imageOffsetX + ", " + imageOffsetY );
//                System.out.println( "hud dimension: " + hudWidth + ", " + hudHeight );
//                System.out.println( "----" );
//            }
//        }

        // create the new HUD node within the EDT
        final HUDNode newHudNode = new HUDNode( component, hudWidth, hudHeight, new AffineTransform() {{
            translate( imageOffsetX, imageOffsetY );
            scale( scaleX, scaleY );
        }}, inputHandler, tab, antialiased );
        newHudNode.setLocalTranslation( offsetX, offsetY, 0 );

        // do the rest of the work in the JME thread
        JMEUtils.invoke( new Runnable() {
            public void run() {
                // ditch the old HUD node
                if ( hudNode != null ) {
                    detachChild( hudNode );
                    hudNode.dispose();
                }

                // hook up new HUD node.
                hudNode = newHudNode;
                attachChild( newHudNode );

                if ( ignoreInput ) {
                    hudNode.ignoreInput();
                }
            }
        } );
    }

    public void ignoreInput() {
        this.ignoreInput = true;
        if ( hudNode != null ) {
            hudNode.ignoreInput();
        }
    }

    public int getComponentWidth() {
        return size.width;
    }

    public int getComponentHeight() {
        return size.height;
    }

    public int getWidth() {
        return hudNode.getWidth();
    }

    public int getHeight() {
        return hudNode.getHeight();
    }

    public JComponent getComponent() {
        return component;
    }
}
