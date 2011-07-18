// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.teetertotter.model.TeeterTotterTorqueModel;
import edu.colorado.phet.balanceandtorque.teetertotter.model.UserMovableModelElement;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;
import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.MysteryObjectFactory;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;

/**
 * This class represents a mystery object in the mass box.  When the user
 * clicks on this node, the corresponding model element is added to the model
 * at the user's mouse location.
 *
 * @author John Blanco
 */
public class MysteryObjectInMassBoxNode extends MassBoxItem {

    private final int mysteryObjectID;

    // Model-view transform for scaling the node used in the mass box.  This
    // may scale the node differently than what is used in the model.
    protected static final ModelViewTransform SCALING_MVT =
            ModelViewTransform.createOffsetScaleMapping( new Point2D.Double( 0, 0 ), 100 );

    public MysteryObjectInMassBoxNode( int mysteryObjectID, final TeeterTotterTorqueModel model, final ModelViewTransform mvt, final PhetPCanvas canvas ) {
        super( model, mvt, canvas );
        this.mysteryObjectID = mysteryObjectID;
        ImageMass mysteryObject = MysteryObjectFactory.createMysteryObject( mysteryObjectID );
        setSelectionNode( new ImageModelElementNode( SCALING_MVT, mysteryObject ) );
        setPositioningOffset( 0, getSelectionNode().getFullBoundsReference().height / 2 );
    }

    @Override protected UserMovableModelElement addElementToModel( Point2D position ) {
        ImageMass mysteryObject = MysteryObjectFactory.createMysteryObject( mysteryObjectID, new Point2D.Double() );
        mysteryObject.setPosition( position );
        mysteryObject.userControlled.set( true );
        model.addMass( mysteryObject );
        return mysteryObject;
    }
}
