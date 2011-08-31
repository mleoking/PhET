// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * When dropping a tool such that it overlaps the toolbox, it should snap into position
 *
 * @author Sam Reid
 */
public class SnapToToolbox extends PBasicInputEventHandler {
    private PNode dragNode;
    private SensorToolBoxNode sensorToolBoxNode;
    private SettableProperty<ImmutableVector2D> modelPosition;
    private ImmutableVector2D controlPanelModelPosition;

    public SnapToToolbox( PNode dragNode, SensorToolBoxNode sensorToolBoxNode, SettableProperty<ImmutableVector2D> modelPosition, ImmutableVector2D controlPanelModelPosition ) {
        this.dragNode = dragNode;
        this.sensorToolBoxNode = sensorToolBoxNode;
        this.modelPosition = modelPosition;
        this.controlPanelModelPosition = controlPanelModelPosition;
    }

    //When dropping a tool such that it overlaps the toolbox, it should snap into position
    @Override public void mouseReleased( PInputEvent event ) {
        if ( dragNode.getGlobalFullBounds().intersects( sensorToolBoxNode.getGlobalFullBounds() ) ) {
            modelPosition.set( controlPanelModelPosition );
        }
    }
}
