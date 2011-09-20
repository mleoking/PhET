// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
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
    private Function0<PBounds> getDragNodeGlobalFullBounds;

    private SensorToolBoxNode sensorToolBoxNode;
    private SettableProperty<ImmutableVector2D> modelPosition;
    private ImmutableVector2D controlPanelModelPosition;

    public SnapToToolbox( SensorToolBoxNode sensorToolBoxNode, SettableProperty<ImmutableVector2D> modelPosition, ImmutableVector2D controlPanelModelPosition, Function0<PBounds> getDragNodeGlobalFullBounds ) {
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
