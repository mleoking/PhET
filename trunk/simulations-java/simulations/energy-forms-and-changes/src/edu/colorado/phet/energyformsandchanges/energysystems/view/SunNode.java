// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.And;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
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

    public SunNode( final Sun sun, ObservableProperty<Boolean> energyChunksVisible, final ModelViewTransform mvt ) {
        super( sun, mvt );

        final double sunRadius = mvt.modelToViewDeltaX( Sun.RADIUS );

        // Add the rays of sunlight.
        final LightRays lightRays = new LightRays( mvt.modelToViewDelta( Sun.OFFSET_TO_CENTER_OF_SUN ), sunRadius, 1000, 40, Color.YELLOW );
        addChild( lightRays );
        energyChunksVisible.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean energyChunksVisible ) {
                // Only show the rays then the energy chunks are not shown.
                lightRays.setVisible( !energyChunksVisible );
            }
        } );

        // Add the energy chunks, which reside on their own layer.
        addChild( new EnergyChunkLayer( sun.energyChunkList, sun.getObservablePosition(), mvt ) );

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
                            addLabel( 0, new TickMarkLabel( "None" ) ); // TODO: i18n
                            addLabel( 1, new TickMarkLabel( "Lots" ) ); // TODO: i18n
                        }}
                ),
                EFACConstants.CONTROL_PANEL_BACKGROUND_COLOR );
        cloudinessControlPanel.setOffset( -cloudinessControlPanel.getFullBoundsReference().width / 2,
                                          -cloudinessControlPanel.getFullBoundsReference().height / 2 );
        addChild( cloudinessControlPanel );

        // Add/remove the absorption area for the solar panel.
        And sunAndSolarPanelActive = new And( sun.getObservableActiveState(), sun.solarPanel.getObservableActiveState() );
        sunAndSolarPanelActive.addObserver( new VoidFunction1<Boolean>() {
            private LightAbsorbingShape currentLightAbsorbingShape = null;

            public void apply( Boolean bothActive ) {
                if ( bothActive ) {
                    // The transforms here are a little tricky, since the
                    // solar panel's absorption shape is in model space, and
                    // the light absorbing shapes are in view space and
                    // relative to this node.
                    Shape transformedButUncompensatedShape = mvt.modelToView( sun.solarPanel.getAbsorptionShape() );
                    AffineTransform compensationTransform = AffineTransform.getTranslateInstance( -mvt.modelToViewX( sun.getPosition().x ),
                                                                                                  -mvt.modelToViewY( sun.getPosition().y ) );
                    Shape absorptionShape = compensationTransform.createTransformedShape( transformedButUncompensatedShape );
                    currentLightAbsorbingShape = new LightAbsorbingShape( absorptionShape, 1 );
                    lightRays.addLightAbsorbingShape( currentLightAbsorbingShape );
                }
                else if ( currentLightAbsorbingShape != null ) {
                    lightRays.removeLightAbsorbingShape( currentLightAbsorbingShape );
                    currentLightAbsorbingShape = null;
                }
            }
        } );
    }

    // Convenience class for creating a tick mark label that works for the slider.
    private static class TickMarkLabel extends PNode {
        private static final double INDENT = 4;
        private static final double LENGTH = 7;
        private static final float STROKE_WIDTH = 1;
        private static final Stroke STROKE = new BasicStroke( STROKE_WIDTH );
        private static final Font LABEL_FONT = new PhetFont( 14, false );

        private TickMarkLabel(String text ) {
            DoubleGeneralPath path = new DoubleGeneralPath( INDENT, STROKE_WIDTH / 2 ) {{
                lineTo( INDENT + LENGTH, STROKE_WIDTH / 2 );
            }};

            final PNode tickMark = new PhetPPath( path.getGeneralPath(), STROKE, Color.BLACK );
            final PNode label = new PhetPText( text, LABEL_FONT ){{
                setOffset( tickMark.getFullBoundsReference().getMaxX() + 3, -getFullBoundsReference().height / 2 );
            }};

            PNode rootNode = new PNode();
            rootNode.addChild( tickMark );
            rootNode.addChild( label );
            addChild( new ZeroOffsetNode( rootNode ) );
        }
    }
}
