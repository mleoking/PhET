// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * When dropping a tool such that it overlaps the toolbox, it should snap into position
 *
 * @author Sam Reid
 */
public class SnapToToolbox extends PBasicInputEventHandler {

    //Function that checks the bounds of the node to see if it should go back to the toolbox
    private final Function0<PBounds> getDragNodeGlobalFullBounds;

    private final FluidPressureAndFlowControlPanelNode sensorToolBoxNode;
    private final SettableProperty<Vector2D> modelPosition;
    private final Vector2D controlPanelModelPosition;

    public SnapToToolbox( FluidPressureAndFlowControlPanelNode sensorToolBoxNode, SettableProperty<Vector2D> modelPosition, Vector2D controlPanelModelPosition, Function0<PBounds> getDragNodeGlobalFullBounds ) {
        this.getDragNodeGlobalFullBounds = getDragNodeGlobalFullBounds;
        this.sensorToolBoxNode = sensorToolBoxNode;
        this.modelPosition = modelPosition;
        this.controlPanelModelPosition = controlPanelModelPosition;
    }

    //When dropping a tool such that it overlaps the toolbox, it should snap into position
    @Override public void mouseReleased( PInputEvent event ) {
        if ( getDragNodeGlobalFullBounds.apply().intersects( sensorToolBoxNode.getGlobalFullBounds() ) ) {
            modelPosition.set( controlPanelModelPosition );
        }
    }
}
