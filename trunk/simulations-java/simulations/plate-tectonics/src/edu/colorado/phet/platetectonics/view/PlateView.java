// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionPatch;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.model.regions.Region;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 */
public class PlateView extends GLNode {

    private final PlateTectonicsTab tab;

    // by default, show water
    public PlateView( final PlateModel model, final PlateTectonicsTab tab ) {
        this( model, tab, new Property<Boolean>( true ) );
    }

    public PlateView( final PlateModel model, final PlateTectonicsTab tab, final Property<Boolean> showWater ) {
        this.tab = tab;
        // add all of the terrain
        for ( final Terrain terrain : model.getTerrains() ) {
            addChild( new TerrainNode( terrain, model, tab ) ); // TODO: strip out model reference? a lot of unneeded stuff

            // only include water nodes if it wants water
            if ( terrain.hasWater() ) {
                final WaterNode waterNode = new WaterNode( terrain, model, tab );
                showWater.addObserver( new SimpleObserver() {
                    public void update() {
                        if ( showWater.get() ) {
                            addChild( waterNode );
                        }
                        else {
                            if ( waterNode.getParent() != null ) {
                                removeChild( waterNode );
                            }
                        }
                    }
                } );
            }
        }

        // add all of the regions
        for ( Region region : model.getRegions() ) {
            addChild( new RegionNode( region, model, tab ) );
        }

        for ( CrossSectionPatch patch : model.getPatches() ) {
            addChild( new CrossSectionPatchNode( tab.getModelViewTransform(), tab.colorMode, patch ) );
        }

        for ( CrossSectionStrip strip : model.getStrips() ) {
            addChild( new CrossSectionStripNode( tab.getModelViewTransform(), tab.colorMode, strip ) );
        }

        // TODO: add new strips in if needed?

        // handle regions when they are added
        model.regionAdded.addListener( new VoidFunction1<Region>() {
            public void apply( Region region ) {
                addChild( new RegionNode( region, model, tab ) );
            }
        } );
        model.patchAdded.addListener( new VoidFunction1<CrossSectionPatch>() {
            public void apply( CrossSectionPatch patch ) {
                addChild( new CrossSectionPatchNode( tab.getModelViewTransform(), tab.colorMode, patch ) );
            }
        } );

        // TODO: handle region removals in a better way
        model.regionRemoved.addListener( new VoidFunction1<Region>() {
            public void apply( Region region ) {
                for ( GLNode node : new ArrayList<GLNode>( getChildren() ) ) {
                    if ( node instanceof RegionNode && ( (RegionNode) node ).getRegion() == region ) {
                        removeChild( node );
                    }
                }
            }
        } );
        model.patchRemoved.addListener( new VoidFunction1<CrossSectionPatch>() {
            public void apply( CrossSectionPatch patch ) {
                for ( GLNode node : new ArrayList<GLNode>( getChildren() ) ) {
                    if ( node instanceof CrossSectionPatchNode && ( (CrossSectionPatchNode) node ).getPatch() == patch ) {
                        removeChild( node );
                    }
                }
            }
        } );
    }

    @Override protected void renderChildren( GLOptions options ) {
        // render children with a normal pass
        super.renderChildren( options );

        // then render them with the transparency pass
        GLOptions transparencyOptions = options.getCopy();
        transparencyOptions.renderPass = RenderPass.TRANSPARENCY;

        for ( GLNode child : getChildren() ) {
            child.render( transparencyOptions );
        }
    }
}
