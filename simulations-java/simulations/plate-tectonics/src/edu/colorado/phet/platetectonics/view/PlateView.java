// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.GLOptions;
import edu.colorado.phet.lwjglphet.GLOptions.RenderPass;
import edu.colorado.phet.lwjglphet.math.ImmutableVector3F;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.lwjglphet.shapes.UnitMarker;
import edu.colorado.phet.platetectonics.model.PlateModel;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.Terrain;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.model.regions.CrossSectionStrip;
import edu.colorado.phet.platetectonics.modules.PlateMotionTab;
import edu.colorado.phet.platetectonics.modules.PlateTectonicsTab;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.labels.BoundaryLabelNode;

/**
 * A view (node) that displays everything physical related to a plate model, within the bounds
 * of the specified grid
 * <p/>
 * TODO: allow a certain method of putting a certain side "in front" instead of putting everything in one?
 */
public class PlateView extends GLNode {

    // keep track of which object corresponds to which node, so we can remove them later
    private final Map<Object, GLNode> nodeMap = new HashMap<Object, GLNode>();
    private final PlateModel model;
    private final PlateTectonicsTab tab;
    private final Property<Boolean> showWater;

    // by default, show water
    public PlateView( final PlateModel model, final PlateTectonicsTab tab ) {
        this( model, tab, new Property<Boolean>( true ) );
    }

    public PlateView( final PlateModel model, final PlateTectonicsTab tab, final Property<Boolean> showWater ) {
        this.model = model;
        this.tab = tab;
        this.showWater = showWater;

        for ( final Terrain terrain : model.getTerrains() ) {
            addTerrain( terrain );
        }

        for ( CrossSectionStrip strip : model.getCrossSectionStrips() ) {
            addCrossSectionStrip( strip );
        }

        model.crossSectionStripAdded.addListener( new VoidFunction1<CrossSectionStrip>() {
            public void apply( CrossSectionStrip strip ) {
                addCrossSectionStrip( strip );
            }
        } );
        model.terrainAdded.addListener( new VoidFunction1<Terrain>() {
            public void apply( Terrain terrain ) {
                addTerrain( terrain );
            }
        } );

        model.crossSectionStripRemoved.addListener( new VoidFunction1<CrossSectionStrip>() {
            public void apply( CrossSectionStrip strip ) {
                removeChild( nodeMap.get( strip ) );
                nodeMap.remove( strip );
            }
        } );
        model.terrainRemoved.addListener( new VoidFunction1<Terrain>() {
            public void apply( Terrain terrain ) {
                removeChild( nodeMap.get( terrain ) );
                nodeMap.remove( terrain );
            }
        } );

        // add a marker when debugPing is notified
        model.debugPing.addListener( new VoidFunction1<ImmutableVector3F>() {
            public void apply( final ImmutableVector3F location ) {
                addChild( new UnitMarker() {{
                    ImmutableVector3F viewLocation = tab.getModelViewTransform().transformPosition( PlateModel.convertToRadial( location ) );
                    translate( viewLocation.x, viewLocation.y, viewLocation.z );
                    scale( 3 );
                }} );
            }
        } );

        if ( tab instanceof PlateMotionTab ) {
            final SmokeNode smokeNode = new SmokeNode( tab, ( (PlateMotionTab) tab ).getPlateMotionModel().smokePuffs );
            addChild( smokeNode );

            // hack way to keep this in front for now
            model.modelChanged.addListener( new VoidFunction1<Void>() {
                public void apply( Void aVoid ) {
                    removeChild( smokeNode );
                    addChild( smokeNode );
                }
            } );

            /*---------------------------------------------------------------------------*
            * boundary labels
            *----------------------------------------------------------------------------*/
            PlateMotionModel plateMotionModel = (PlateMotionModel) model;
            plateMotionModel.boundaryLabels.addElementAddedObserver( new VoidFunction1<BoundaryLabel>() {
                public void apply( BoundaryLabel boundaryLabel ) {
                    final BoundaryLabelNode boundaryLabelNode = new BoundaryLabelNode( boundaryLabel, tab.getModelViewTransform(), tab.colorMode ) {{
                        ( (PlateMotionTab) tab ).showLabels.addObserver( new SimpleObserver() {
                            public void update() {
                                setVisible( ( (PlateMotionTab) tab ).showLabels.get() );
                            }
                        } );
                    }};
                    addChild( boundaryLabelNode );
                    nodeMap.put( boundaryLabel, boundaryLabelNode );
                }
            } );

            plateMotionModel.boundaryLabels.addElementRemovedObserver( new VoidFunction1<BoundaryLabel>() {
                public void apply( BoundaryLabel boundaryLabel ) {
                    removeChild( nodeMap.get( boundaryLabel ) );
                    nodeMap.remove( boundaryLabel );
                }
            } );

            // respond to events that tell the view to put a certain side in front
            plateMotionModel.frontBoundarySideNotifier.addListener( new VoidFunction1<Side>() {
                public void apply( Side side ) {
                    // move all boundary nodes on top of this afterwards
                    for ( GLNode boundaryNode : new ArrayList<GLNode>( getChildren() ) ) {
                        if ( boundaryNode instanceof BoundaryLabelNode && ( (BoundaryLabelNode) boundaryNode ).getBoundaryLabel().side == side ) {
                            removeChild( boundaryNode );
                            addChild( boundaryNode );
                        }
                    }
                }
            } );
        }
    }

    public void addCrossSectionStrip( final CrossSectionStrip strip ) {
        addWrappedChild( strip, new CrossSectionStripNode( tab.getModelViewTransform(), tab.colorMode, strip ) );

        // if this fires, add the node to the front of the list
        strip.moveToFrontNotifier.addUpdateListener( new UpdateListener() {
            public void update() {
                GLNode node = nodeMap.get( strip );
                if ( node != null && node.getParent() != null ) {
                    removeChild( node );
                    addChild( node );
                }
            }
        }, false );
    }

    public void addTerrain( final Terrain terrain ) {
        addWrappedChild( terrain, new GLNode() {{
            final TerrainNode terrainNode = new TerrainNode( terrain, tab.getModelViewTransform() );
            addChild( terrainNode );

            if ( terrain.hasWater() ) {
                final WaterStripNode waterNode = new WaterStripNode( terrain, model, tab );
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
        }} );
    }

    public void addWrappedChild( Object object, GLNode node ) {
        assert nodeMap.get( object ) == null;
        addChild( node );
        nodeMap.put( object, node );
    }

    @Override
    protected void renderChildren( GLOptions options ) {
        // render children with a normal pass
        super.renderChildren( options );

        // then render them with the transparency pass
        GLOptions transparencyOptions = options.getCopy();
        transparencyOptions.renderPass = RenderPass.TRANSPARENCY;

        for ( GLNode child : new ArrayList<GLNode>( getChildren() ) ) {
            child.render( transparencyOptions );
        }
    }
}
