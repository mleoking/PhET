/**
 * Class: ModelViewCoordinator
 * Package: edu.colorado.phet.common.examples.examples
 * Author: Another Guy
 * Date: Dec 19, 2003
 */
package edu.colorado.phet.common.view.graphics.transforms;

import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

public class ModelViewCoordinator {
    private Rectangle2D.Double modelBounds;
    private JPanel panel;
    private AffineTransform modelToViewTx = new AffineTransform();
    private AffineTransform viewToModelTx = new AffineTransform();
    private ModelViewTransform2D mvtx;

    public ModelViewCoordinator( Rectangle2D.Double modelBounds, JPanel panel ) {
        this.modelBounds = modelBounds;
        this.panel = panel;

        // Create the transform with a dummy view rectangle. It will get updated
        // to the proper rectangle as soon as the panel appears on the screen
        panel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                updateTx();
            }

            public void componentShown( ComponentEvent e ) {
                updateTx();
            }
        } );
    }

    private Rectangle getRelativeBounds() {
        Rectangle localBounds = panel.getBounds();
        Rectangle bounds = new Rectangle( 0, 0, localBounds.width, localBounds.height );
        return bounds;
    }

    private void updateTx() {
        // Use panel and model bounds to set the transforms
        if( mvtx == null ) {
            if( panel.getBounds() != null ) {
                mvtx = new ModelViewTransform2D( ModelViewCoordinator.this.modelBounds,
                                                 new Rectangle( 0, 0, 1, 1 ) );
                //add any pending transform listeners.
                for( int i = 0; i < transformListeners.size(); i++ ) {
                    TransformListener transformListener = (TransformListener)transformListeners.get( i );
                    mvtx.addTransformListener(transformListener );
                }
            }
        }
        if( mvtx != null ) {
            mvtx.setViewBounds( getRelativeBounds() );
            modelToViewTx.setTransform( mvtx.toAffineTransform() );
            try {
                viewToModelTx.setTransform( modelToViewTx.createInverse() );
            }
            catch( NoninvertibleTransformException e ) {
                e.printStackTrace();
            }
        }
    }

    public AffineTransform getModelToViewTx() {
        return modelToViewTx;
    }

    public AffineTransform getViewToModelTx() {
        return viewToModelTx;
    }
    ArrayList transformListeners=new ArrayList();
    public void addTransformListener( TransformListener tl ) {
        if( mvtx != null ) {
            this.mvtx.addTransformListener( tl );
        }else{
            transformListeners.add(tl);
        }
    }
}
