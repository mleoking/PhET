// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsModule;
import edu.colorado.phet.platetectonics.util.Grid3D;

import com.jme3.scene.Node;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends Node {

    public PlateView( PlateModel model, final PlateTectonicsModule module, final Grid3D grid ) {
        attachChild( new TerrainNode( model, module, grid ) );
        attachChild( new CrossSectionNode( model, module, grid ) );
        attachChild( new WaterNode( model, module, grid ) );
    }
}
