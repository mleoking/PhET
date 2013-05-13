// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorquestudy.common.model.masses.MysteryMass;
import edu.colorado.phet.balanceandtorquestudy.common.view.MysteryMassNode;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents a "mystery mass" in a tool box.  When the user clicks
 * on this node, the corresponding model element is added to the model at the
 * user's mouse location.
 *
 * @author John Blanco
 */
public class MysteryMassCreatorNode extends ImageMassCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 200 );

    public MysteryMassCreatorNode( int mysteryObjectID, final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas, new MysteryMass( mysteryObjectID ), false );
        setSelectionNode( new MysteryMassNode( SCALING_MVT, (MysteryMass) prototypeImageMass, canvas, new BooleanProperty( false ) ) );
        setPositioningOffset( 0, -mvt.modelToViewDeltaY( prototypeImageMass.getHeight() / 2 ) );
    }
}
