/**
 * Class: PhysicalPanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 9:11:05 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.nuclearphysics.model.Nucleus;

import java.awt.*;
import java.util.HashMap;

public class PhysicalPanel extends ApparatusPanel {

    private HashMap modelElementToGraphicMap = new HashMap();

    public PhysicalPanel() {
        this.setBackground( backgroundColor );
    }

    public void addNucleus( Nucleus nucleus ) {
        NucleusGraphic ng = new NucleusGraphic( nucleus );
        // Register the graphic to the model element
        modelElementToGraphicMap.put( nucleus, ng );
        addGraphic( ng );
    }

    public void removeNucleus( Nucleus nucleus ) {
        removeGraphic( (Graphic)modelElementToGraphicMap.get( nucleus ) );
        modelElementToGraphicMap.remove( nucleus );
    }

    public synchronized void addGraphic( Graphic graphic ) {
        super.addGraphic( graphic );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public synchronized void removeGraphic( Graphic graphic ) {
        super.removeGraphic( graphic );    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        super.paintComponent( graphics );    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void clear() {
        super.removeAllGraphics();
        modelElementToGraphicMap.clear();
    }

    //
    // Statics
    //
    private static Color backgroundColor = new Color( 255, 255, 230 );
}
