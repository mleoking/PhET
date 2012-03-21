// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;

import org.lwjgl.input.Mouse;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.lwjglphet.CanvasTransform;
import edu.colorado.phet.lwjglphet.ComponentImage;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.LWJGLTab;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.util.PBounds;

import static org.lwjgl.opengl.GL11.*;

/**
 * Allows overlaying a GUI onto LWJGL. This should only be rendered in an orthographic mode.
 */
public class OrthoComponentNode extends GLNode {

    public final ValueNotifier<OrthoComponentNode> onResize = new ValueNotifier<OrthoComponentNode>( this );
    public final Property<ImmutableVector2D> position;

    // whether mouse events will pass through
    private boolean mouseEnabled = true;

    private final JComponent component;
    private final LWJGLTab tab;
    private final CanvasTransform canvasTransform;

    private Dimension size = new Dimension(); // our current size
    private ComponentImage componentImage;

    private int offsetX;
    private int offsetY;

    public OrthoComponentNode( final JComponent component, final LWJGLTab tab, CanvasTransform canvasTransform, Property<ImmutableVector2D> position, final VoidNotifier mouseEventNotifier ) {
        this.component = component;
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

                    rebuildComponentImage();

                    // run notifications in the LWJGL thread
                    LWJGLUtils.invoke( new Runnable() {
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
                                      rebuildComponentImage();
                                  }
                              }, false );
        canvasTransform.transform.addObserver( new SimpleObserver() {
            public void update() {
                rebuildComponentImage();
            }
        } );

        // forward events to the component image
        mouseEventNotifier.addUpdateListener(
                new UpdateListener() {
                    public void update() {
                        if ( componentImage != null && isMouseEnabled() ) {
                            // reversal of Y coordinate, and subtract out the offset that the ComponentImage doesn't have access to
                            ImmutableVector2F localCoordinates = screenToLocalCoordinates( new ImmutableVector2F( Mouse.getEventX(), Mouse.getEventY() ) );
                            componentImage.handleMouseEvent( (int) localCoordinates.x, (int) localCoordinates.y );
                        }
                    }
                }, false );
    }

    // NOTE: run from Swing EDT
    public Component getComponentAt( int mouseX, int mouseY ) {
        if ( componentImage != null ) {
            ImmutableVector2F localCoordinates = screenToLocalCoordinates( new ImmutableVector2F( Mouse.getEventX(), Mouse.getEventY() ) );
            ImmutableVector2F componentCoordinates = componentImage.localToComponentCoordinates( new ImmutableVector2F( localCoordinates.x, localCoordinates.y ) );
            return componentImage.componentAt( (int) componentCoordinates.x, (int) componentCoordinates.y );
        }
        return null;
    }

    public ImmutableVector2F screenToLocalCoordinates( ImmutableVector2F screenCoordinates ) {
        return new ImmutableVector2F( screenCoordinates.x - offsetX,
                                      ( tab.canvasSize.get().height - Mouse.getEventY() ) - offsetY
        );
    }

    public ImmutableVector2F screentoComponentCoordinates( ImmutableVector2F screenCoordinates ) {
        return componentImage.localToComponentCoordinates( screenToLocalCoordinates( screenCoordinates ) );
    }

    public boolean isReady() {
        return componentImage != null;
    }

    public <T> void updateOnEvent( Notifier<T> notifier ) {
        notifier.addUpdateListener( new UpdateListener() {
                                        public void update() {
                                            OrthoComponentNode.this.update();
                                        }
                                    }, false );
    }

    // should be called every frame
    public void update() {
        if ( componentImage != null ) {
            componentImage.update();
        }
    }

    // force repainting of the image
    public void repaint() {
        if ( componentImage != null ) {
            componentImage.repaint();
        }
    }

    @Override public void renderSelf( GLOptions options ) {
        if ( componentImage == null ) {
            return;
        }
        // TODO: don't tromp over these settings?
        glEnable( GL_TEXTURE_2D );
        glShadeModel( GL_FLAT );

        componentImage.useTexture();

        glColor4f( 1, 1, 1, 1 );

        glBegin( GL_QUADS );
        glTexCoord2f( 0, 0 );
        glVertex3f( offsetX, offsetY, 0 );
        glTexCoord2f( 0, 1 );
        glVertex3f( offsetX, offsetY + componentImage.getHeight(), 0 );
        glTexCoord2f( 1, 1 );
        glVertex3f( offsetX + componentImage.getWidth(), offsetY + componentImage.getHeight(), 0 );
        glTexCoord2f( 1, 0 );
        glVertex3f( offsetX + componentImage.getWidth(), offsetY, 0 );
        glEnd();

        glShadeModel( GL_SMOOTH );
        glDisable( GL_TEXTURE_2D );
    }

    // if necessary, creates a new HUD node of a different size to display our component
    public synchronized void rebuildComponentImage() {
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

        // here we calculate our actual canvas bounds
        Rectangle2D transformedBounds = canvasTransform.getTransformedBounds( localBounds );

        // for rendering the image, we need to know how much to scale it by
        final double scaleX = transformedBounds.getWidth() / localBounds.getWidth();
        final double scaleY = transformedBounds.getHeight() / localBounds.getHeight();

        // find the largest integer offsets that allow us to cover the entire renderable area
        offsetX = (int) Math.floor( transformedBounds.getMinX() ); // int truncation isn't good for the negatives here
        offsetY = (int) Math.floor( transformedBounds.getMinY() );

        // get how much we need to offset our rendered image by for sub-pixel accuracy (since we translate by offsetX/Y, we need to render at the difference)
        final double imageOffsetX = transformedBounds.getMinX() - offsetX;
        final double imageOffsetY = Math.ceil( transformedBounds.getMaxY() ) - transformedBounds.getMaxY(); // reversed Y handling

        // how large our HUD node needs to be as a raster to render all of our content
        final int hudWidth = LWJGLUtils.toPowerOf2( ( (int) Math.ceil( transformedBounds.getMaxX() ) ) - offsetX );
        final int hudHeight = LWJGLUtils.toPowerOf2( ( (int) Math.ceil( transformedBounds.getMaxY() ) ) - offsetY );

        // debugging for the translation image-offset issues
//        System.out.println( "----" );
//        System.out.println( "transform: " + canvasTransform.transform.get() );
//        System.out.println( "position: " + position.get() );
//        System.out.println( "localBounds: " + localBounds );
//        System.out.println( "transformedBounds: " + transformedBounds );
//        System.out.println( "scales: " + scaleX + ", " + scaleY );
//        System.out.println( "offsets: " + offsetX + ", " + offsetY );
//        System.out.println( "image offsets: " + imageOffsetX + ", " + imageOffsetY );
//        System.out.println( "hud dimension: " + hudWidth + ", " + hudHeight );
//        System.out.println( "----" );

        // create the new image within the EDT
        final ComponentImage newComponentImage = new ComponentImage( hudWidth, hudHeight, true, GL_NEAREST, GL_NEAREST, new AffineTransform() {{
            // TODO: note that these image offset values are being ignored in TextureImage due to unexplained "jittery" behavior when slight offsets are encountered
            translate( imageOffsetX, -imageOffsetY );
            scale( scaleX, scaleY );
        }}, component );

        // do the rest of the work in the JME thread
        LWJGLCanvas.addTask( new Runnable() {
            public void run() {
                // hook up new HUD node.
                componentImage = newComponentImage;
            }
        } );
    }

    public int getComponentWidth() {
        return size.width;
    }

    public int getComponentHeight() {
        return size.height;
    }

    public int getWidth() {
        return componentImage.getWidth();
    }

    public int getHeight() {
        return componentImage.getHeight();
    }

    public JComponent getComponent() {
        return component;
    }

    public boolean isMouseEnabled() {
        return mouseEnabled;
    }

    public void setMouseEnabled( boolean mouseEnabled ) {
        this.mouseEnabled = mouseEnabled;
    }
}
