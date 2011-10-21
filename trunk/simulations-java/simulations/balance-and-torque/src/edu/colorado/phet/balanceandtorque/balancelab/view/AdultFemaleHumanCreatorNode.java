// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.balancelab.model.BalancingActModel;
import edu.colorado.phet.balanceandtorque.common.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.common.model.masses.Woman;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents an adult female human in a tool box.  When the user
 * clicks on this node, the corresponding model element is added to the model at
 * the user's mouse location.
 *
 * @author John Blanco
 */
public class AdultFemaleHumanCreatorNode extends ImageMassCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 120 );

    public AdultFemaleHumanCreatorNode( final BalancingActModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas, Woman.MASS );
        ImageMass adultFemaleHuman = new Woman();
        setSelectionNode( new ImageMassNode( SCALING_MVT, adultFemaleHuman, canvas, new BooleanProperty( false ) ) );
        // Set the positioning offset slightly off center so users don't appear
        // to be holding him in a - well - sensitive area.
        setPositioningOffset( 0, -mvt.modelToViewDeltaY( adultFemaleHuman.getHeight() * 0.6 ) );
    }

    @Override protected ImageMass createImageMassInstance() {
        return new Woman();
    }
}
