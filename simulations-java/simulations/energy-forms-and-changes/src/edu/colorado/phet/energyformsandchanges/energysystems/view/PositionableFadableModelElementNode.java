// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.energyformsandchanges.energysystems.model.PositionableFadableModelElement;
import edu.umd.cs.piccolo.PNode;

/**
 * Base class for model elements whose position and opacity can change.
 *
 * @author John Blanco
 */
public abstract class PositionableFadableModelElementNode extends PNode {

    protected PositionableFadableModelElementNode( PositionableFadableModelElement modelElement, final ModelViewTransform mvt ) {

        // Update the overall offset based on the model position.
        modelElement.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D immutableVector2D ) {
                setOffset( mvt.modelToView( immutableVector2D ).toPoint2D() );
            }
        } );

        // Update the overall opacity base on model element opacity.
        modelElement.opacity.addObserver( new VoidFunction1<Double>() {
            public void apply( Double opacity ) {
                setTransparency( opacity.floatValue() );
            }
        } );
    }
}
