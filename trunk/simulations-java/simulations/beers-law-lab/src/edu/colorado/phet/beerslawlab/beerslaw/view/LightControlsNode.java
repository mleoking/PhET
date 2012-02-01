// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.LightRepresentation;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.WavelengthControlType;
import edu.colorado.phet.beerslawlab.common.BLLResources;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel for the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LightControlsNode extends PNode {

    private static final PhetFont CONTROL_FONT = new PhetFont( 16 ); //TODO move to a more general location

    public LightControlsNode( Property<LightRepresentation> lightRepresentation, Property<WavelengthControlType> wavelengthControlType ) {

        // nodes
        PSwing beamNode = new PSwing( new PropertyRadioButton<LightRepresentation>( Strings.BEAM, lightRepresentation, LightRepresentation.BEAM ) {{
            setFont( CONTROL_FONT );
        }} );
        PSwing photonsNode = new PSwing( new PropertyRadioButton<LightRepresentation>( Strings.PHOTONS, lightRepresentation, LightRepresentation.PHOTONS ) {{
            setFont( CONTROL_FONT );
        }} );
         PSwing lambdaMaxNoe = new PSwing( new PropertyRadioButton<WavelengthControlType>( Strings.LAMBDA_MAX, wavelengthControlType, WavelengthControlType.LAMBDA_MAX ) {{
            setFont( CONTROL_FONT );
        }} );
         PSwing manualNode = new PSwing( new PropertyRadioButton<WavelengthControlType>( Strings.MANUAL, wavelengthControlType, WavelengthControlType.MANUAL ) {{
            setFont( CONTROL_FONT );
        }} );

        // rendering order
        addChild( beamNode );
        addChild( photonsNode );
        addChild( lambdaMaxNoe );
        addChild( manualNode );

        // layout
        final double xSpacing = 5;
        final double ySpacing = 15;
        photonsNode.setOffset( beamNode.getFullBoundsReference().getMaxX() + xSpacing, beamNode.getYOffset() );
        lambdaMaxNoe.setOffset( beamNode.getXOffset(), beamNode.getFullBoundsReference().getMaxY() + ySpacing );
        manualNode.setOffset(  lambdaMaxNoe.getFullBoundsReference().getMaxX() + xSpacing, lambdaMaxNoe.getYOffset() );
    }
}
