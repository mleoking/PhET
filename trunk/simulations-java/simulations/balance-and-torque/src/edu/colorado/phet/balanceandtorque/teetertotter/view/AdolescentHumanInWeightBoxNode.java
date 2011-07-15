// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.AdolescentHuman;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents an adolescent human in the weight box.  When
 * the user clicks on this node, the corresponding model element is added to
 * the model at the user's mouse location.
 *
 * @author John Blanco
 */
public class AdolescentHumanInWeightBoxNode extends WeightBoxItem {

    // Model-view transform for scaling the node used in the weight box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 100 );

    public AdolescentHumanInWeightBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
        ImageMass adolescentHuman = new AdolescentHuman();
        setSelectionNode( new ImageModelElementNode( SCALING_MVT, adolescentHuman ) );
        setPositioningOffset( 0, getSelectionNode().getFullBoundsReference().height / 2 );
        // TODO: i18n (units too)
        setCaption( adolescentHuman.getMass() + " kg" );
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        AdolescentHuman adolescentHuman = new AdolescentHuman();
        adolescentHuman.setPosition( position );
        adolescentHuman.userControlled.set( true );
        model.addWeight( adolescentHuman );
        return adolescentHuman;
    }
}
