// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.balancelab.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.common.model.BalanceModel;
import edu.colorado.phet.balanceandtorque.common.model.masses.Boy;
import edu.colorado.phet.balanceandtorque.common.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents an adolescent boy in a tool box.  When the user clicks
 * on this node, the corresponding model element is added to the model at the
 * user's mouse location.
 *
 * @author John Blanco
 */
public class AdolescentBoyCreatorNode extends ImageMassCreatorNode {

    // Model-view transform for scaling the node used in the tool box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 100 );

    //REVIEW All of these ImageMassCreatorNode subclasses rely on having a public constant like Boy.MASS.
    // Rather than passing that constant to super(), why not pass "new Boy()", get the mass from the Boy instance
    // and replace usages of adolescentHuman with getImageMass?  Then you're getting the info from the instance,
    // and are less likely to introduce a problem where a constant is used (eg, Girl.MASS) that doesn't match the instance.

    public AdolescentBoyCreatorNode( final BalanceModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas, Boy.MASS );
        //REVIEW why aren't you using createImageMassInstance instead of "new Boy()" ?
        //   This duplicates the information about what type of ImageMass to create, and done it this way in all ImageMassCreatorNode subclasses.

        ImageMass adolescentHuman = new Boy();
        setSelectionNode( new ImageMassNode( SCALING_MVT, adolescentHuman, canvas, new BooleanProperty( false ) ) );
        setPositioningOffset( 0, -mvt.modelToViewDeltaY( adolescentHuman.getHeight() / 2 ) );
    }

    @Override protected ImageMass createImageMassInstance() {
        return new Boy();
    }
}
