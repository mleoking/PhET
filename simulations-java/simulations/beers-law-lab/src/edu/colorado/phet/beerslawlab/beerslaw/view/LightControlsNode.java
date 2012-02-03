// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.text.MessageFormat;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.beerslawlab.beerslaw.view.BeersLawCanvas.WavelengthControlType;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.WavelengthControl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel for the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LightControlsNode extends PNode {

    private static final Dimension WAVELENGTH_CONTROL_TRACK_SIZE = new Dimension( 150, 30 );
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

        // Wavelength control
        final BLLWavelengthControl wavelengthControl = new BLLWavelengthControl( WAVELENGTH_CONTROL_TRACK_SIZE, light.wavelength );

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
        PSwing pswingPanel = new PSwing( panel );

        // Workaround: Spacer to prevent Piccolo control panel from changing as wavelength slider is adjusted.
        final double maxWidth =  Math.max( pswingPanel.getFullBoundsReference().getWidth(), wavelengthControl.getFullBoundsReference().getWidth() );
        PPath spacer = new PPath() {{
            setPathTo( new Rectangle2D.Double( 0, 0, maxWidth + 20, 1 ) );
            setStroke( null );
        }};

        // Put it in a Piccolo control panel
        PNode parentNode = new PNode();
        parentNode.addChild( pswingPanel );
        parentNode.addChild( wavelengthControl );
        parentNode.addChild( spacer );
        wavelengthControl.setOffset( pswingPanel.getFullBoundsReference().getCenterX() - ( WAVELENGTH_CONTROL_TRACK_SIZE.width / 2 ),
                                     pswingPanel.getFullBoundsReference().getMaxY() + 30 );
        spacer.setOffset( pswingPanel.getXOffset(), wavelengthControl.getFullBoundsReference().getMaxY() + 1 );
        addChild( new ControlPanelNode( parentNode ) );

        // Make the wavelength control visible if the user choose variable control
        wavelengthControlType.addObserver( new VoidFunction1<WavelengthControlType>() {
            public void apply( WavelengthControlType wavelengthControlType ) {
                wavelengthControl.setVisible( wavelengthControlType == WavelengthControlType.VARIABLE );
            }
        });
    }
}
