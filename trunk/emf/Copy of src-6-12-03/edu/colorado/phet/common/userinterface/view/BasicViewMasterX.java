/**
 * Class: BasicViewMasterX
 * Package: edu.colorado.phet.common.userinterface.view
 * Author: Another Guy
 * Date: May 29, 2003
 */
package edu.colorado.phet.common.userinterface.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.userinterface.graphics.ApparatusPanel;

/**
 * The difference between this and the BasicViewMaster is that this implements
 * createViewElement without the line
 *         Message message = (Message) m;//Could avoid this cast by statically typing the creation type.
 * and it handles the situation in which there is no view element for the model element
 *
 */
public class BasicViewMasterX extends BasicViewMaster {
    GraphicFactory factory;

    public BasicViewMasterX( ApparatusPanel apparatusPanel, GraphicFactory graphicFactory ) {
        super( apparatusPanel, graphicFactory );
        factory = graphicFactory;
    }

    protected void createViewElement( ModelElement m ) {
        if( t.containsKey( m ) )
            throw new RuntimeException( "View already contains element for model piece: " + m );
        GraphicAndLayer g = factory.createGraphicForModelElement( m );

        // Extended behavior
        if( g.getGraphic() != null ) {
            apparatus.addGraphic( g.getGraphic(), g.getLayer() );

            g.getGraphic().update( m, null );
            t.put( m, g );
        }
    }
}
