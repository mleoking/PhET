/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.application;

import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;

/**
 * ModuleStateDescriptor
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetGraphicsModuleStateDescriptor extends ModuleStateDescriptor {
    private GraphicLayerSet graphic;

    protected PhetGraphicsModuleStateDescriptor( PhetGraphicsModule module ) {
        super( module );
        setGraphic( module.getApparatusPanel().getGraphic() );
    }

    public GraphicLayerSet getGraphic() {
        return graphic;
    }

    public void setGraphic( GraphicLayerSet graphic ) {
        this.graphic = graphic;
    }

    public void setModuleState( PhetGraphicsModule module ) {
        super.setModuleState( module );
        // Set up the restored graphics
        module.getApparatusPanel().setGraphic( graphic );

        // Force a repaint on the apparatus panel
        module.getApparatusPanel().paintImmediately( module.getApparatusPanel().getBounds() );
    }
}
