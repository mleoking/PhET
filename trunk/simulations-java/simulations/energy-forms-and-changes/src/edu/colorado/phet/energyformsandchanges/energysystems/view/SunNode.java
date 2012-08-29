// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Sun;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Node that represents the sun, clouds, and a slider to control the level
 * of cloudiness in the view.
 *
 * @author John Blanco
 */
public class SunNode extends PositionableFadableModelElementNode {

    private static final Font CONTROL_PANEL_TITLE_FONT = new PhetFont( 16, true );
    private static final Font CONTROL_PANEL_LABEL_FONT_FONT = new PhetFont( 14, false );

    public SunNode( final Sun sun, final ModelViewTransform mvt ) {
        super( sun, mvt );
        final double radius = mvt.modelToViewDeltaX( Sun.RADIUS );
        PNode sunNode = new PhetPPath( new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 ) ) {{
            setOffset( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ).toPoint2D() );
            setPaint( new RoundGradientPaint( 0, 0, Color.WHITE, new Point2D.Double( radius * 0.7, radius * 0.7 ), new Color( 255, 215, 0 ) ) );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.YELLOW );
        }};
        final LightRays lightRays = new LightRays( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ), radius, 450, 40, Color.YELLOW );
        addChild( lightRays );
        addChild( sunNode );

        // Add the control panel for the clouds.
        PNode cloudIcon = new PImage( EnergyFormsAndChangesResources.Images.CLOUD_1 ) {{
            setScale( 0.2 );
        }};
        final ControlPanelNode cloudinessControlPanel = new ControlPanelNode(
                new VBox(
                        new HBox( new PhetPText( "Clouds", CONTROL_PANEL_TITLE_FONT ), cloudIcon ), // TODO: i18n
                        new VSliderNode( EnergyFormsAndChangesSimSharing.UserComponents.cloudinessSlider,
                                         0,
                                         1,
                                         5,
                                         80,
                                         new Property<Double>( 0.0 ),
                                         new BooleanProperty( true ) ) {{
                            addLabel( 0, new PhetPText( "None", CONTROL_PANEL_LABEL_FONT_FONT ) ); // TODO: i18n
                            addLabel( 1, new PhetPText( "Lots", CONTROL_PANEL_LABEL_FONT_FONT ) ); // TODO: i18n
                        }}
                ),
                EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
        cloudinessControlPanel.setOffset( -cloudinessControlPanel.getFullBoundsReference().width / 2,
                                          -cloudinessControlPanel.getFullBoundsReference().height / 2 );
        addChild( cloudinessControlPanel );
    }
}
