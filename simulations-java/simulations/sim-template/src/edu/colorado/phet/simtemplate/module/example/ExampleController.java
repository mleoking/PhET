/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.simtemplate.module.example;

import edu.colorado.phet.simtemplate.control.ExampleSubPanel;
import edu.colorado.phet.simtemplate.control.ExampleSubPanel.ExampleSubPanelListener;
import edu.colorado.phet.simtemplate.model.ExampleModelElement;
import edu.colorado.phet.simtemplate.model.ExampleModelElement.ExampleModelElementListener;

/**
 * ExampleController handles the wiring up of listeners between model and view.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ExampleController {
    
    public ExampleController( ExampleModel model, ExampleControlPanel controlPanel ) {
        
        final ExampleModelElement modelElement = model.getExampleModelElement();
        final ExampleSubPanel subPanel = controlPanel.getExampleSubPanel();
        
        modelElement.addExampleModelElementListener( new ExampleModelElementListener() {

            public void widthChanged() {
                subPanel.setWidthValue( modelElement.getWidth() );
            }
            
            public void heightChanged() {
                subPanel.setHeightValue( modelElement.getHeight() );
            }
            
            public void orientationChanged() {
                double degrees = Math.toDegrees( modelElement.getOrientation() );
                subPanel.setOrientationValue( degrees );
            }

            public void positionChanged() {
                subPanel.setPosition( modelElement.getPositionReference() );
            }
        });
        
        subPanel.addExampleSubPanelListener( new ExampleSubPanelListener() {

            public void widthChanged() {
                modelElement.setWidth( subPanel.getWidthValue() );
            }
            
            public void heightChanged() {
                modelElement.setHeight( subPanel.getHeightValue() );
            }
            
            public void orientationChanged() {
                double radians = Math.toRadians( subPanel.getOrientationValue() );
                modelElement.setOrientation( radians );
            }
        } );
    }
}
