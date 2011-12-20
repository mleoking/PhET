// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.lwjglphet.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Region;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.Grid3D;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends GLNode {

    public PlateView( final PlateModel model, final PlateTectonicsTab module, final Grid3D grid ) {
        // add all of the terrain
        for ( Terrain terrain : model.getTerrains() ) {
            addChild( new TerrainNode( terrain, model, module ) ); // TODO: strip out model reference? a lot of unneeded stuff

            // only include water nodes if it wants water
            if ( terrain.hasWater() ) {
                addChild( new WaterNode( terrain, model, module ) );
            }
        }

        // add all of the regions
        for ( Region region : model.getRegions() ) {
            addChild( new RegionNode( region, model, module ) );
        }
    }
}
