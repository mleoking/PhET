// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.view;

import java.awt.Insets;
import java.text.MessageFormat;

import javax.swing.JLabel;

import edu.colorado.phet.beerslawlab.beerslaw.model.Light;
import edu.colorado.phet.beerslawlab.beerslaw.model.Light.LightRepresentation;
import edu.colorado.phet.beerslawlab.common.BLLConstants;
import edu.colorado.phet.beerslawlab.common.BLLResources.Strings;
import edu.colorado.phet.beerslawlab.common.BLLSimSharing.UserComponents;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Anchor;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel.Fill;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Control panel for the light.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
class LightControlsNode extends PNode {

    private static final PhetFont FONT = new PhetFont( BLLConstants.CONTROL_FONT_SIZE );

    public LightControlsNode( Light light ) {

        // Beam vs Photons radio buttons
        JLabel lightViewLabel = new JLabel( MessageFormat.format( Strings.PATTERN_0LABEL, Strings.LIGHT_VIEW ) );
        lightViewLabel.setFont( FONT );
        PropertyRadioButton<LightRepresentation> beamRadioButton = new PropertyRadioButton<LightRepresentation>( UserComponents.beamRadioButton, Strings.BEAM, light.representation, Light.LightRepresentation.BEAM );
        beamRadioButton.setFont( FONT );
        PropertyRadioButton<LightRepresentation> photonsRadioButton = new PropertyRadioButton<LightRepresentation>( UserComponents.photonsRadioButton, Strings.PHOTONS, light.representation, Light.LightRepresentation.PHOTONS );
        photonsRadioButton.setFont( FONT );

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

        // Put it in a Piccolo control panel
        addChild( new ControlPanelNode( panel ) );
    }
}
