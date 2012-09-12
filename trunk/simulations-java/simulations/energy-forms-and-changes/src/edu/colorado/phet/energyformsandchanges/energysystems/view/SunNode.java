// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.RoundGradientPaint;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
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
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.view.EnergyChunkNode;
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

        // TODO: Temp - add the energy chunks.
        final PNode energyChunkLayer = new PNode();
        canvas.addWorldChild( energyChunkLayer ); // Goes directly on canvas so that it can handle its own positioning.
        sun.energyChunkList.addElementAddedObserver( new VoidFunction1<EnergyChunk>() {
            public void apply( final EnergyChunk addedEnergyChunk ) {
                final EnergyChunkNode energyChunkNode = new EnergyChunkNode( addedEnergyChunk, mvt );
                energyChunkLayer.addChild( energyChunkNode );
                sun.energyChunkList.addElementRemovedObserver( new VoidFunction1<EnergyChunk>() {
                    public void apply( EnergyChunk removedEnergyChunk ) {
                        if ( removedEnergyChunk == addedEnergyChunk ) {
                            energyChunkLayer.removeChild( energyChunkNode );
                        }
                    }
                } );
            }
        } );

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

        DoubleGeneralPath solarPanelShape = new DoubleGeneralPath() {{
            double xOrigin = 100;
            double width = 350;
            double height = 180;
            moveTo( xOrigin, 0 );
            lineTo( xOrigin + width, -height );
            lineTo( xOrigin + width, 0 );
            closePath();
        }};
        lightRays.addLightAbsorbingShape( new LightAbsorbingShape( solarPanelShape.getGeneralPath(), 1 ) );

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
    }
}
