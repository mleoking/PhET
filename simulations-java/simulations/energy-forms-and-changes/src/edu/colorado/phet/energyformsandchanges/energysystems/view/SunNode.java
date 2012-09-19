// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.slider.VSliderNode;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkLayer;
import edu.colorado.phet.energyformsandchanges.energysystems.model.Cloud;
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

    public SunNode( final Sun sun, PhetPCanvas canvas, final ModelViewTransform mvt ) {
        super( sun, mvt );

        final double sunRadius = mvt.modelToViewDeltaX( Sun.RADIUS );

        // Add the rays of sunlight.
        final LightRays lightRays = new LightRays( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ), sunRadius, 1000, 40, Color.YELLOW );
        addChild( lightRays );

        // Add the energy chunks.
        final EnergyChunkLayer energyChunkLayer = new EnergyChunkLayer( sun.energyChunkList, canvas, mvt );
        sun.getObservablePosition().addObserver( new VoidFunction1<Vector2D>() {
            public void apply( Vector2D position ) {
                System.out.println( "position = " + position );
                energyChunkLayer.setOffset( -mvt.modelToViewX( position.x ),
                                            -mvt.modelToViewY( position.y ) );
            }
        } );

        addChild( energyChunkLayer );

        // Add the sun.
        PNode sunNode = new PhetPPath( new Ellipse2D.Double( -sunRadius, -sunRadius, sunRadius * 2, sunRadius * 2 ) ) {{
            setOffset( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ).toPoint2D() );
            setPaint( new RoundGradientPaint( 0, 0, Color.WHITE, new Point2D.Double( sunRadius * 0.7, sunRadius * 0.7 ), new Color( 255, 215, 0 ) ) );
            setStroke( new BasicStroke( 1 ) );
            setStrokePaint( Color.YELLOW );
        }};
        addChild( sunNode );

        // Add the clouds.
        for ( Cloud cloud : sun.clouds ) {
            final CloudNode cloudNode = new CloudNode( cloud, mvt );
            addChild( cloudNode );
            final LightAbsorbingShape lightAbsorbingShape = new LightAbsorbingShape( cloudNode.getFullBoundsReference(), 0.0 );
            lightRays.addLightAbsorbingShape( lightAbsorbingShape );
            // TODO: This should probably be handled within the cloud node, and the shape available here.  Or something.  This is awkward.
            cloud.existenceStrength.addObserver( new VoidFunction1<Double>() {
                public void apply( Double existenceStrength ) {
                    lightAbsorbingShape.lightAbsorptionCoefficient.set( existenceStrength / 10 );
                }
            } );
        }

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
                                         sun.cloudiness,
                                         new BooleanProperty( true ) ) {{
                            addLabel( 0, new PhetPText( "None", CONTROL_PANEL_LABEL_FONT_FONT ) ); // TODO: i18n
                            addLabel( 1, new PhetPText( "Lots", CONTROL_PANEL_LABEL_FONT_FONT ) ); // TODO: i18n
                        }}
                ),
                EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
        cloudinessControlPanel.setOffset( -cloudinessControlPanel.getFullBoundsReference().width / 2,
                                          -cloudinessControlPanel.getFullBoundsReference().height / 2 );
        addChild( cloudinessControlPanel );

        // Add/remove the absorption area for the solar panel.
        And sunAndSolarPanelActive = new And( sun.getObservableActiveState(), sun.solarPanel.getObservableActiveState() );
        sunAndSolarPanelActive.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean bothActive ) {
                if ( bothActive ) {
                    // The transforms here are a little tricky, since the
                    // solar panel's absorption shape is in model space, and
                    // the light absorbing shapes are in view space and
                    // relative to the center of the light ray node.
                    System.out.println( "sun.getPosition() = " + sun.getPosition() );
                    Vector2D offsetToCenterOfRays = sun.getPosition().plus( Sun.OFFSET_TO_CENTER_OF_SUN );
//                    AffineTransform offsetCompensationTransform = AffineTransform.getTranslateInstance( offsetToCenterOfRays.x, offsetToCenterOfRays.y );
                    double xOffset = mvt.modelToViewDeltaX( sun.getPosition().minus( Sun.OFFSET_TO_CENTER_OF_SUN ).getX() );
                    double yOffset = mvt.modelToViewDeltaY( sun.getPosition().plus( Sun.OFFSET_TO_CENTER_OF_SUN ).getY() );
                    System.out.println( "xOffset = " + xOffset );
                    System.out.println( "yOffset = " + yOffset );
                    AffineTransform offsetCompensationTransform = AffineTransform.getTranslateInstance( xOffset, yOffset );
                    System.out.println( "offsetToCenterOfRays = " + offsetToCenterOfRays );
                    Shape uncompensatedAbsorptionShape = mvt.modelToView( sun.solarPanel.getAbsorptionShape() );
                    Shape compensatedAbsorptionShape = offsetCompensationTransform.createTransformedShape( uncompensatedAbsorptionShape );
//                    lightRays.addLightAbsorbingShape( new LightAbsorbingShape( compensatedAbsorptionShape, 1 ) );

                    Shape testShape1 = new Rectangle2D.Double( -100, -100, 200, 200 );
//                    lightRays.addLightAbsorbingShape( new LightAbsorbingShape( testShape1, 1 ) );

                    // Centered in the model, but doesn't end up centered in the view.
                    Shape testShape2 = mvt.modelToView( new Rectangle2D.Double( -0.1, -0.1, 0.2, 0.2 ) );
//                    lightRays.addLightAbsorbingShape( new LightAbsorbingShape( testShape2, 1 ) );

                    Shape testShape3 = mvt.modelToView( new Rectangle2D.Double( -0.1 + sun.getPosition().x, -0.1 + sun.getPosition().y, 0.2, 0.2 ) );
//                    lightRays.addLightAbsorbingShape( new LightAbsorbingShape( testShape3, 1 ) );

                    PNode testNode = new PhetPPath( mvt.modelToView( sun.solarPanel.getAbsorptionShape() ) );
                    testNode.setOffset( -mvt.modelToViewX( sun.getPosition().x ),
                                        -mvt.modelToViewY( sun.getPosition().y ) );
                    Shape testShape4 = testNode.getFullBounds();
                    lightRays.addLightAbsorbingShape( new LightAbsorbingShape( testNode.getFullBoundsReference(), 1 ) );

                }
            }
        } );

        // TODO: Temp for prototyping.
//        final PhetPPath solarEnergyAbsorber = new PhetPPath( mvt.modelToView( sun.solarPanel.getAbsorptionShape() ), Color.PINK );
//        solarEnergyAbsorber.setOffset( -mvt.modelToViewX( sun.getPosition().x ),
//                                       -mvt.modelToViewY( sun.getPosition().y ) );
//        addChild( solarEnergyAbsorber );
    }
}
