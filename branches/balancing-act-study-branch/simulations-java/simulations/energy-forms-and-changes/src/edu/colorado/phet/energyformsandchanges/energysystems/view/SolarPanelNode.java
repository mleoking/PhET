// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.SolarPanel;

/**
 * Piccolo node that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanelNode extends PositionableFadableModelElementNode {

    public SolarPanelNode( SolarPanel solarPanel, final ModelViewTransform mvt ) {
        super( solarPanel, mvt );

        // Add the images and the node that will manage the energy chunks in
        // the order needed for the desired layering.
        addChild( new ModelElementImageNode( SolarPanel.CURVED_WIRE_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.POST_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.SOLAR_PANEL_IMAGE, mvt ) );
        addChild( new EnergyChunkLayer( solarPanel.energyChunkList, solarPanel.getObservablePosition(), mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.CONVERTER_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.CONNECTOR_IMAGE, mvt ) );
    }
}