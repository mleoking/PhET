/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.module.example;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.simtemplate.control.ExampleSubPanel;
import edu.colorado.phet.simtemplate.control.ExampleSubPanel.ExampleSubPanelListener;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;
import edu.colorado.phet.simtemplate.view.ExampleNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * ExampleController handles the wiring up of listeners between model and view.
 * Note that in a production simulation, you may want to have multiple controller that 
 * handle the wiring of chunks of the sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleController {
    
    public ExampleController( ExampleModel model, ExampleCanvas canvas, ExampleControlPanel controlPanel ) {
        
        final ExampleModelElement modelElement = model.getExampleModelElement();
        final ExampleNode node = canvas.getExampleNode();
        final ExampleSubPanel subPanel = controlPanel.getExampleSubPanel();
        
        /*
         * When the model element changes, update the node and control panel.
         */
        modelElement.addExampleModelElementListener( new ExampleModelElementListener() {

            public void orientationChanged() {
                double radians = modelElement.getOrientation();
                double degrees = Math.toDegrees( radians );
                subPanel.setOrientation( degrees );
                node.setOrientation( radians );
            }

            public void positionChanged() {
                Point2D position = modelElement.getPositionReference();
                subPanel.setPosition( position );
                node.setPosition( position );
            }
        });
        
        /*
         * When the node is dragged, update the model.
         */
        node.addInputEventListener( new CursorHandler() );
        node.addInputEventListener( new PBasicInputEventHandler() {
            public void mouseDragged( PInputEvent event ) {
                PDimension delta = event.getDeltaRelativeTo( node.getParent() );
                Point2D p = modelElement.getPosition();
                Point2D pNew = new Point2D.Double( p.getX() + delta.getWidth(), p.getY() + delta.getHeight() );
                modelElement.setPosition( pNew );
            }
        } );
        
        /*
         * When the controls are changed, update the model.
         */
        subPanel.addExampleSubPanelListener( new ExampleSubPanelListener() {

            public void orientationChanged() {
                double radians = Math.toRadians( subPanel.getOrientation() );
                modelElement.setOrientation( radians );
            }
        } );
    }
}
