// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyType;

import static edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesResources.Images.*;

/**
 * Base class for light bulbs in the model.
 *
 * @author John Blanco
 */
public class LightBulb extends EnergyUser {

    private final double energyToFullyLight; // In joules/sec, a.k.a. watts.
    private final IUserComponent userComponent;

    public final Property<Double> litProportion = new Property<Double>( 0.0 );

    private List<EnergyChunkPathMover> energyChunkMovers = new ArrayList<EnergyChunkPathMover>();

    protected LightBulb( IUserComponent userComponent, Image icon, final ModelElementImage offImage, final ModelElementImage onImage, double energyToFullyLight ) {
        super( icon, assembleImageList( offImage, onImage ) );
        this.userComponent = userComponent;
        this.energyToFullyLight = energyToFullyLight;
    }

    @Override public void stepInTime( double dt, Energy incomingEnergy ) {

        // Handle any incoming energy chunks.
        if ( !incomingEnergyChunks.isEmpty() ) {
            for ( EnergyChunk incomingEnergyChunk : incomingEnergyChunks ) {
                if ( incomingEnergyChunk.energyType.get() == EnergyType.ELECTRICAL ) {
                    // Add the energy chunk to the list of those under management.
                    energyChunkList.add( incomingEnergyChunk );

                    // And a "mover" that will move this energy chunk through
                    // the wire to the bulb.
                    energyChunkMovers.add( new EnergyChunkPathMover( incomingEnergyChunk, getEnergyChunkPath( getPosition() ), EFACConstants.ELECTRICAL_ENERGY_CHUNK_VELOCITY ) );
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
                // TODO
            }
        }

        // Set how lit the bulb is.
        if ( isActive() && incomingEnergy.type == EnergyType.ELECTRICAL ) {
            litProportion.set( MathUtil.clamp( 0, incomingEnergy.amount / energyToFullyLight, 1 ) );
        }
        else {
            litProportion.set( 0.0 );
        }
    }

    private static List<Vector2D> getEnergyChunkPath( final Vector2D centerPosition ) {
        return new ArrayList<Vector2D>() {{
            add( centerPosition );
        }};
    }


    @Override public void deactivate() {
        super.deactivate();
        litProportion.set( 0.0 );
    }

    static private List<ModelElementImage> assembleImageList( final ModelElementImage offImage, final ModelElementImage onImage ) {
        return new ArrayList<ModelElementImage>() {{
            add( new ModelElementImage( WIRE_BLACK_MIDDLE_62, new Vector2D( -0.037, -0.04 ) ) );
            add( new ModelElementImage( WIRE_BLACK_RIGHT, new Vector2D( -0.009, -0.016 ) ) );
            add( new ModelElementImage( ELEMENT_BASE, new Vector2D( 0, 0.0 ) ) );
            add( offImage );
            add( onImage );
        }};
    }

    @Override public IUserComponent getUserComponent() {
        return userComponent;
    }
}
