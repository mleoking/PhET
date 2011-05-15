// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import java.awt.geom.Dimension2D;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.ToolNode;
import edu.colorado.phet.common.piccolophet.nodes.conductivitytester.ConductivityTesterNode;
import edu.colorado.phet.sugarandsaltsolutions.common.model.ConductivityTester;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Tool node that is created when the user drags the conductivity tester from its toolbox.
 *
 * @author Sam Reid
 */
public class ConductivityTesterToolNode extends ToolNode {
    private final ModelViewTransform transform;
    private final ConductivityTester tester;

    //Reuse the same ConductivityTesterNode instead of creating new ones each time we drag out of the toolbox.
    public ConductivityTesterToolNode( ModelViewTransform transform, ConductivityTesterNode node, ConductivityTester tester ) {
        this.transform = transform;
        this.tester = tester;
        addChild( node );
    }

    @Override public void dragAll( PDimension viewDelta ) {
        //Drag the unit (battery + bulb) in view coordinates
        ImmutableVector2D newLocation = new ImmutableVector2D( tester.getLocationReference() ).plus( viewDelta );
        tester.setLocation( newLocation.getX(), newLocation.getY() );

        //How far it was dragged in model coordinates
        Dimension2D modelDelta = transform.viewToModelDelta( viewDelta );

        //Drag the probes in model coordinates
        ImmutableVector2D newPositiveProbeLocation = new ImmutableVector2D( tester.getPositiveProbeLocationReference() ).plus( modelDelta );
        tester.setPositiveProbeLocation( newPositiveProbeLocation.getX(), newPositiveProbeLocation.getY() );

        ImmutableVector2D newNegativeProbeLocation = new ImmutableVector2D( tester.getNegativeProbeLocationReference() ).plus( modelDelta );
        tester.setNegativeProbeLocation( newNegativeProbeLocation.getX(), newNegativeProbeLocation.getY() );
    }
}
