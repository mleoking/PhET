// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.platetectonics.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.lwjglphet.nodes.GLNode;
import edu.colorado.phet.platetectonics.model.PlateMotionModel;
import edu.colorado.phet.platetectonics.model.labels.BoundaryLabel;
import edu.colorado.phet.platetectonics.tabs.PlateMotionTab;
import edu.colorado.phet.platetectonics.util.Side;
import edu.colorado.phet.platetectonics.view.labels.BoundaryLabelNode;

/**
 * Specialized view with additional behavior for the Plate Motion tab.
 */
public class PlateMotionView extends PlateTectonicsView {
    public PlateMotionView( final PlateMotionModel model, final PlateMotionTab tab, final Property<Boolean> showWater ) {
        super( model, tab, showWater );

        // add smoke handling in
        final SmokeNode smokeNode = new SmokeNode( tab.getModelViewTransform(), ( (PlateMotionTab) tab ).getPlateMotionModel().smokePuffs );
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
