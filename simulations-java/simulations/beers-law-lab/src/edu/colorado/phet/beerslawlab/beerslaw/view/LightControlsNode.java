// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.WavelengthControlType;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;

/**
 * Control panel for the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LightControlsNode extends PNode {

    private static final PhetFont FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

    public LightControlsNode( Light light, Property<WavelengthControlType> wavelengthControlType ) {

        // Swing components
        JLabel lightViewLabel = new JLabel( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.LIGHT_VIEW ) );
        lightViewLabel.setFont( FONT );
        PropertyRadioButton<LightRepresentation> beamRadioButton = new PropertyRadioButton<LightRepresentation>( Strings.BEAM, light.representation, Light.LightRepresentation.BEAM );
        beamRadioButton.setFont( FONT );
        PropertyRadioButton<LightRepresentation> photonsRadioButton = new PropertyRadioButton<LightRepresentation>( Strings.PHOTONS, light.representation, Light.LightRepresentation.PHOTONS );
        photonsRadioButton.setFont( FONT );
        JLabel wavelengthLabel = new JLabel( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.WAVELENGTH ) );
        wavelengthLabel.setFont( FONT );
        PropertyRadioButton<WavelengthControlType> lambdaMaxRadioButton = new PropertyRadioButton<WavelengthControlType>( Strings.LAMBDA_MAX, wavelengthControlType, WavelengthControlType.LAMBDA_MAX );
        lambdaMaxRadioButton.setFont( FONT );
        PropertyRadioButton<WavelengthControlType> variableRadioButton = new PropertyRadioButton<WavelengthControlType>( Strings.VARIABLE, wavelengthControlType, WavelengthControlType.VARIABLE );
        variableRadioButton.setFont( FONT );

        // Panel
        final int xSpacing = 4;
        final int ySpacing = 4;
        int row = 0;
        GridPanel panel = new GridPanel();
        panel.setInsets( new Insets( ySpacing, xSpacing, ySpacing, xSpacing ) );
        panel.setOpaque( false );
        panel.setAnchor( Anchor.WEST );
        panel.setFill( Fill.HORIZONTAL );
        panel.add( lightViewLabel, row++, 0, 2, 1 );
        panel.add( beamRadioButton, row, 0 );
        panel.add( photonsRadioButton, row++, 1 );
        panel.add( new JSeparator(), row++, 0, 2, 1 );
        panel.add( wavelengthLabel, row++, 0, 2, 1 );
        panel.add( lambdaMaxRadioButton, row, 0 );
        panel.add( variableRadioButton, row++, 1 );

        // Put it in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );
    }
}
