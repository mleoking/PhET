// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Frame;

import edu.colorado.phet.beerslawlab.beerslaw.model.BeersLawModel;
import edu.colorado.phet.beerslawlab.common.view.BLLCanvas;
import edu.colorado.phet.beerslawlab.common.view.SoluteChoiceNode;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.umd.cs.piccolo.PNode;

/**
 * Canvas for the "Beer's Law" module.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class BeersLawCanvas extends BLLCanvas {

    public enum LightRepresentation {BEAM, PHOTONS}
    public enum WavelengthControlType {LAMBDA_MAX, MANUAL}

    private final Property<LightRepresentation> lightRepresentation = new Property<LightRepresentation>( LightRepresentation.BEAM );
    private final Property<WavelengthControlType> wavelengthControlType = new Property<WavelengthControlType>( WavelengthControlType.LAMBDA_MAX  );

    public BeersLawCanvas( final BeersLawModel model, Frame parentFrame ) {

        // Nodes
        PNode soluteChoiceNode = new SoluteChoiceNode( model.getSolutes(), model.solute );
        PNode lightControlsNode = new LightControlsNode( lightRepresentation, wavelengthControlType );

        // Rendering order
        {
            addChild( soluteChoiceNode );
            addChild( lightControlsNode );
        }

        // layout
        {
            final double xMargin = 20;
            final double yMargin = 20;
            // solution combo box at top center
            soluteChoiceNode.setOffset( ( getStageSize().getWidth() - soluteChoiceNode.getFullBoundsReference().getWidth() ) / 2,
                                        yMargin );
            // left center
            lightControlsNode.setOffset( xMargin,
                                         ( getStageSize().getHeight() - lightControlsNode.getFullBoundsReference().getWidth() ) / 2 );
        }
    }
}
