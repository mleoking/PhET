/**
 * Class: EmfGraphicFactory
 * Package: edu.colorado.phet.emf.view
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.emf.view;

import edu.colorado.phet.common.userinterface.graphics.ObservingGraphic;
import edu.colorado.phet.common.userinterface.view.GraphicAndLayer;
import edu.colorado.phet.common.userinterface.view.GraphicFactory;
import edu.colorado.phet.emf.model.FieldElement;

import java.awt.*;

public class EmfGraphicFactory implements GraphicFactory {

    // The rectangle within which field elements should be visible.
    private Rectangle viewBounds = new Rectangle( 20, 20, 500, 400 );

    public GraphicAndLayer createGraphicForModelElement( Object o ) {
        ObservingGraphic graphic = null;
        if( o instanceof FieldElement ) {
            graphic = new FieldElementGraphic();
            ((FieldElementGraphic)graphic).setViewBounds( viewBounds );
            ((FieldElement)o).addObserver( graphic );
        }

        // TODO: add ElectronView

        int layer = 0;
        return new GraphicAndLayer( graphic, layer );
    }
}

