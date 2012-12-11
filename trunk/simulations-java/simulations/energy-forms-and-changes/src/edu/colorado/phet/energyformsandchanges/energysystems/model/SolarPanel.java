// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Class that represents a solar panel in the view.
 *
 * @author John Blanco
 */
public class SolarPanel extends EnergyConverter {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double CONVERSION_EFFICIENCY = 0.3;

    private static final Vector2D SOLAR_PANEL_OFFSET = new Vector2D( 0, 0.044 );
    public static final ModelElementImage SOLAR_PANEL_IMAGE = new ModelElementImage( SOLAR_PANEL, 0.15, SOLAR_PANEL_OFFSET );
    public static final Vector2D CONVERTER_IMAGE_OFFSET = new Vector2D( 0.015, -0.040 );
    public static final ModelElementImage CONVERTER_IMAGE = new ModelElementImage( SOLAR_PANEL_GEN, CONVERTER_IMAGE_OFFSET );
    public static final ModelElementImage CURVED_WIRE_IMAGE = new ModelElementImage( WIRE_BLACK_LEFT, CONVERTER_IMAGE_OFFSET.plus( 0.009, 0.024 ) );
    public static final ModelElementImage POST_IMAGE = new ModelElementImage( SOLAR_PANEL_POST_2, CONVERTER_IMAGE_OFFSET.plus( new Vector2D( 0, 0.04 ) ) );
    public static final Vector2D CONNECTOR_IMAGE_OFFSET = new Vector2D( 0.057, -0.04 );
    public static final ModelElementImage CONNECTOR_IMAGE = new ModelElementImage( CONNECTOR, CONNECTOR_IMAGE_OFFSET );

    // Constants used for creating the path followed by the energy chunks.
    // Many of these numbers were empirically determined based on the images,
    // and will need updating if the images change.
    private static final Vector2D OFFSET_TO_CONVERGENCE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX(), 0.01 );
    private static final Vector2D OFFSET_TO_FIRST_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX(), -0.025 );
    private static final Vector2D OFFSET_TO_SECOND_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX() + 0.005, -0.033 );
    private static final Vector2D OFFSET_TO_THIRD_CURVE_POINT = new Vector2D( CONVERTER_IMAGE_OFFSET.getX() + 0.015, CONNECTOR_IMAGE_OFFSET.getY() );
    private static final Vector2D OFFSET_TO_CONNECTOR_CENTER = CONNECTOR_IMAGE_OFFSET;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    private List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    protected SolarPanel() {
        super( EnergyFormsAndChangesResources.Images.SOLAR_PANEL_ICON );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    @Override public Energy stepInTime( double dt, Energy incomingEnergy ) {

        if ( isActive() ) {
            // Handle any incoming energy chunks.
            if ( !incomingEnergyChunks.isEmpty() ) {
                for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                    if ( incomingEnergyChunk.energyType.get() == EnergyType.LIGHT ) {
                        // Convert this chunk to electrical energy and add it to
                        // the list of energy chunks being managed.
                        incomingEnergyChunk.energyType.set( EnergyType.ELECTRICAL );
                        energyChunkList.add( incomingEnergyChunk );

                        // And a "mover" that will move this energy chunk
                        // through the solar panel and the wire.
                        energyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk,
                                                                         getEnergyChunkPath( getPosition() ),
                                                                         EFACConstants.ENERGY_CHUNK_VELOCITY ) );
                    }
                    else {
                        // By design, this shouldn't happen, so warn if it does.
                        System.out.println( getClass().getName() + " - Warning: Ignoring energy chunk with unexpected type, type = " + incomingEnergyChunk.energyType.get().toString() );
                    }
                }
                incomingEnergyChunks.clear();
            }

            // Move the energy chunks that are currently under management.
            for ( EnergyChunkPathMover energyChunkMover : new ArrayList<EnergyChunkPathMover>( energyChunkMovers ) ) {
                energyChunkMover.moveAlongPath( dt );
                if ( energyChunkMover.isPathFullyTraversed() ) {
                    // The energy chunk has traveled across the panel and the wire,
                    // so pass it off to the next element in the system.
                    outgoingEnergyChunks.add( energyChunkMover.energyChunk );
                    energyChunkList.remove( energyChunkMover.energyChunk );
                    energyChunkMovers.remove( energyChunkMover );
                }
            }
        }

        // Produce the appropriate amount of energy.
        double energyProduced = 0;
        if ( isActive() && incomingEnergy.type == EnergyType.LIGHT ) {
            energyProduced = incomingEnergy.amount * CONVERSION_EFFICIENCY;
        }
        return new Energy( EnergyType.ELECTRICAL, energyProduced, 0 );
    }

    @Override public void clearEnergyChunks() {
        super.clearEnergyChunks();
        energyChunkMovers.clear();
    }

    private static List<Vector2D> getEnergyChunkPath( final Vector2D panelPosition ) {
        return new ArrayList<Vector2D>() {{
            add( panelPosition.plus( OFFSET_TO_CONVERGENCE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_FIRST_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_SECOND_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_THIRD_CURVE_POINT ) );
            add( panelPosition.plus( OFFSET_TO_CONNECTOR_CENTER ) );
        }};
    }

    /**
     * Get the shape of the region that absorbs sunlight.
     *
     * @return A shape, in model space, of the region of the solar panel that
     *         can absorb sunlight.
     */
    public Shape getAbsorptionShape() {
        final Rectangle2D panelBounds = new Rectangle2D.Double( -SOLAR_PANEL_IMAGE.getWidth() / 2,
                                                                -SOLAR_PANEL_IMAGE.getHeight() / 2,
                                                                SOLAR_PANEL_IMAGE.getWidth(),
                                                                SOLAR_PANEL_IMAGE.getHeight() );
        DoubleGeneralPath absorptionShape = new DoubleGeneralPath() {{
            moveTo( panelBounds.getMinX(), panelBounds.getMinY() );
            lineTo( panelBounds.getMaxX(), panelBounds.getMaxY() );
            lineTo( panelBounds.getMaxX(), panelBounds.getMinY() );
            closePath();
        }};
        AffineTransform transform = AffineTransform.getTranslateInstance( getPosition().getX() + SOLAR_PANEL_OFFSET.getX(),
                                                                          getPosition().getY() + SOLAR_PANEL_OFFSET.getY() );
        return transform.createTransformedShape( absorptionShape.getGeneralPath() );
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.selectSolarPanelButton;
    }
}
