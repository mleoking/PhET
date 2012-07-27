// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.lwjglphet.nodes;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.model.event.Notifier;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.lwjglphet.SwingImage;
import edu.colorado.phet.lwjglphet.utils.LWJGLUtils;

/**
 * Contains Swing-specific graphics node behavior for the Ortho / Planar varieties
 */
public abstract class AbstractSwingGraphicsNode extends AbstractGraphicsNode {

    protected final JComponent component;

    protected SwingImage componentImage;

    public AbstractSwingGraphicsNode( final JComponent component ) {
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
    }

    public <T> void updateOnEvent( Notifier<T> notifier ) {
        notifier.addUpdateListener( new UpdateListener() {
            public void update() {
                AbstractSwingGraphicsNode.this.update();
            }
        }, false );
    }

    public boolean isReady() {
        return componentImage != null;
    }

    // should be called every frame
    public void update() {
        if ( isReady() ) {
            componentImage.update();
        }
    }

    // force repainting of the image
    public void repaint() {
        if ( isReady() ) {
            componentImage.repaint();
        }
    }

    @Override public int getWidth() {
        return componentImage.getWidth();
    }

    @Override public int getHeight() {
        return componentImage.getHeight();
    }

    public JComponent getComponent() {
        return component;
    }
}
