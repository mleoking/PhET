// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.PointDragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.PlaceThePoints;

/**
 * Challenge graph with manipulators for 3 arbitrary points, which may or may not form a guess line.
 * The answer line is initially hidden.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class ThreePointsGraphNode extends ChallengeGraphNode {

    public ThreePointsGraphNode( final PlaceThePoints challenge ) {
        super( challenge, false /* slopeToolEnabled */ );

        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

        // p1 manipulator
        final LineManipulatorNode p1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_1 );
        p1ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p1Manipulator, UserComponentTypes.sprite,
                                                                       p1ManipulatorNode, challenge.mvt,
                                                                       challenge.p1,
                                                                       new ArrayList<Property<Vector2D>>() {{
                                                                           add( challenge.p2);
                                                                           add( challenge.p3) ;
                                                                       }},
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

        // p1 manipulator
        final LineManipulatorNode p2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_2 );
        p2ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p2Manipulator, UserComponentTypes.sprite,
                                                                       p2ManipulatorNode, challenge.mvt,
                                                                       challenge.p2,
                                                                       new ArrayList<Property<Vector2D>>() {{
                                                                           add( challenge.p1 );
                                                                           add( challenge.p3 );
                                                                       }},
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

        // p3 manipulator
        final LineManipulatorNode p3ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_3 );
        p3ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p3Manipulator, UserComponentTypes.sprite,
                                                                       p3ManipulatorNode, challenge.mvt,
                                                                       challenge.p3,
                                                                       new ArrayList<Property<Vector2D>>() {{
                                                                           add( challenge.p1 );
                                                                           add( challenge.p2 );
                                                                       }},
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

        // Rendering order
        addChild( p1ManipulatorNode );
        addChild( p2ManipulatorNode );
        addChild( p3ManipulatorNode );

        // Sync with points
        final RichSimpleObserver pointsObserver = new RichSimpleObserver() {
            public void update() {
                // move the manipulators
                p1ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p1.get().x, challenge.p1.get().y ) );
                p2ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p2.get().x, challenge.p2.get().y ) );
                p3ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p3.get().x, challenge.p3.get().y ) );
            }
        };
        pointsObserver.observe( challenge.p1, challenge.p2, challenge.p3 );
    }
}
