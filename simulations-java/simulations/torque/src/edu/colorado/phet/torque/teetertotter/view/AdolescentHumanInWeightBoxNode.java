// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.torque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.torque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.torque.teetertotter.model.weights.AdolescentHuman;
import edu.colorado.phet.torque.teetertotter.model.weights.ImageWeight;

/**
 * This class represents a brick or stack of bricks in the weight box.  When
 * the user clicks on this node, a stack of bricks of the appropriate size is
 * added to the model at the user's mouse location.
 *
 * @author John Blanco
 */
public class AdolescentHumanInWeightBoxNode extends WeightBoxItem {

    public AdolescentHumanInWeightBoxNode( final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
        ImageWeight adolescentHuman = new AdolescentHuman();
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
