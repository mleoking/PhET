// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
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

        // Add the images used to represent this node.
        addChild( new ModelElementImageNode( SolarPanel.WIRE_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.CONNECTOR_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.BASE_IMAGE, mvt ) );
        addChild( new ModelElementImageNode( SolarPanel.SOLAR_PANEL_IMAGE, mvt ) );

        // Add the layer that will manage the energy chunks.
        addChild( new EnergyChunkLayer( solarPanel.energyChunkList, solarPanel.getObservablePosition(), mvt ) );
    }
}