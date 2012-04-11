// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.ValueNotifier;
import edu.colorado.phet.lwjglphet.ComponentImage;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.LWJGLCanvas;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.math.PlaneF;
import edu.colorado.phet.lwjglphet.math.Ray3F;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;
import edu.umd.cs.piccolo.util.PBounds;

import static org.lwjgl.opengl.GL11.*;

public class PlanarComponentNode extends GLNode {
    public final ValueNotifier<PlanarComponentNode> onResize = new ValueNotifier<PlanarComponentNode>( this );

    private final JComponent component;

    private Dimension size = new Dimension(); // our current size
    private ComponentImage componentImage;

    public PlanarComponentNode( final JComponent component ) {
        this.component = component;

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

        // initialize this correctly
        rebuildComponentImage();
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

    public <T> void updateOnEvent( Notifier<T> notifier ) {
        notifier.addUpdateListener( new UpdateListener() {
                                        public void update() {
                                            PlanarComponentNode.this.update();
                                        }
                                    }, false );
    }

    public boolean doesLocalRayHit( Ray3F ray ) {
        ImmutableVector3F planeHitPoint = PlaneF.XY.intersectWithRay( ray );
        return get2DBounds().contains( planeHitPoint.x, planeHitPoint.y );
    }

    public PBounds get2DBounds() {
        return new PBounds( 0, 0, size.width, size.height );
    }

    @Override public void renderSelf( GLOptions options ) {
        if ( componentImage == null ) {
            return;
        }

        glColor4f( 1, 1, 1, 1 );

        // TODO: don't tromp over these settings?
        glEnable( GL_TEXTURE_2D );
        glShadeModel( GL_FLAT );

        componentImage.useTexture();

        // texture coordinates reversed compared to OrthoComponentNode
        glBegin( GL_QUADS );

        float left = 0;
        float right = componentImage.getWidth();
        float top = size.height;
        float bottom = size.height - componentImage.getHeight();

        // lower-left
        glTexCoord2f( 0, 1 );
        glVertex3f( left, bottom, 0 );

        // upper-left
        glTexCoord2f( 0, 0 );
        glVertex3f( left, top, 0 );

        // upper-right
        glTexCoord2f( 1, 0 );
        glVertex3f( right, top, 0 );

        // lower-right
        glTexCoord2f( 1, 1 );
        glVertex3f( right, bottom, 0 );
        glEnd();

        glShadeModel( GL_SMOOTH );
        glDisable( GL_TEXTURE_2D );
    }

    // if necessary, creates a new HUD node of a different size to display our component
    public synchronized void rebuildComponentImage() {
        // how large our HUD node needs to be as a raster to render all of our content
        final int hudWidth = LWJGLUtils.toPowerOf2( size.width );
        final int hudHeight = LWJGLUtils.toPowerOf2( size.height );

        // create the new image within the EDT
        final ComponentImage newComponentImage = new ComponentImage( hudWidth, hudHeight, true, GL_LINEAR, GL_LINEAR, new AffineTransform(), component );

        // do the rest of the work in the LWJGL thread
        LWJGLCanvas.addTask( new Runnable() {
            public void run() {
                // hook up new HUD node.
                componentImage = newComponentImage;
                // TODO: why is this separated in the *ComponentNode classes?
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

}
