// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.model;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.energyformsandchanges.common.model.Beaker;
import edu.colorado.phet.energyformsandchanges.common.model.EnergyChunk;

/**
 * Model element that represents a beaker that can contain other thermal
 * model elements.
 *
 * @author John Blanco
 */
public class BeakerContainer extends Beaker {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // List of model elements that may be moved into this beaker.
    private final List<RectangularThermalMovableModelElement> potentiallyContainedElements;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /*
     * Constructor.
     */
    public BeakerContainer( ConstantDtClock clock, Vector2D initialPosition, double width, double height,
                            List<RectangularThermalMovableModelElement> potentiallyContainedElements, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, width, height, energyChunksVisible );
        this.potentiallyContainedElements = potentiallyContainedElements;
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    /*
     * Update the fluid level in the beaker based upon any displacement that
     * could be caused by the given rectangles.  This algorithm is strictly
     * two dimensional, even though displacement is more of the 3D concept.
     *
     * @param potentiallyDisplacingRectangles
     *
     */
    public void updateFluidLevel( List<Rectangle2D> potentiallyDisplacingRectangles ) {

        // Calculate the amount of overlap between the rectangle that
        // represents the fluid and the displacing rectangles.
        Rectangle2D fluidRectangle = new Rectangle2D.Double( getRect().getX(),
                                                             getRect().getY(),
                                                             width,
                                                             height * fluidLevel.get() );
        double overlappingArea = 0;
        for ( Rectangle2D rectangle2D : potentiallyDisplacingRectangles ) {
            if ( rectangle2D.intersects( fluidRectangle ) ) {
                Rectangle2D intersection = rectangle2D.createIntersection( fluidRectangle );
                overlappingArea += intersection.getWidth() * intersection.getHeight();
            }
        }

        // Map the overlap to a new fluid height.  The scaling factor was
        // empirically determined to look good.
        double newFluidLevel = Math.min( INITIAL_FLUID_LEVEL + overlappingArea * 120, 1 );
        double proportionateIncrease = newFluidLevel / fluidLevel.get();
        fluidLevel.set( newFluidLevel );

        // Update the shapes of the energy chunk slices.
        for ( EnergyChunkContainerSlice slice : slices ) {
            Shape originalShape = slice.getShape();
            Shape expandedOrCompressedShape = AffineTransform.getScaleInstance( 1, proportionateIncrease ).createTransformedShape( originalShape );
            AffineTransform translationTransform = AffineTransform.getTranslateInstance( originalShape.getBounds2D().getX() - expandedOrCompressedShape.getBounds2D().getX(),
                                                                                         originalShape.getBounds2D().getY() - expandedOrCompressedShape.getBounds2D().getY() );
            slice.setShape( translationTransform.createTransformedShape( expandedOrCompressedShape ) );
        }
    }

    private boolean isEnergyChunkObscured( EnergyChunk ec ) {
        for ( RectangularThermalMovableModelElement potentiallyContainedElement : potentiallyContainedElements ) {
            if ( this.getThermalContactArea().getBounds().contains( potentiallyContainedElement.getRect() ) && potentiallyContainedElement.getProjectedShape().contains( ec.position.get().toPoint2D() ) ) {
                return true;
            }
        }
        return false;
    }

    @Override protected void animateNonContainedEnergyChunks( double dt ) {
        for ( EnergyChunkWanderController energyChunkWanderController : new ArrayList<EnergyChunkWanderController>( energyChunkWanderControllers ) ) {
            EnergyChunk ec = energyChunkWanderController.getEnergyChunk();
            if ( isEnergyChunkObscured( ec ) ) {
                // This chunk is being transferred from a container in the
                // beaker to the fluid, so move it sideways.
                double xVel = 0.05 * dt * ( getCenterPoint().getX() > ec.position.get().getX() ? -1 : 1 );
                Vector2D motionVector = new Vector2D( xVel, 0 );
                ec.translate( motionVector );
            }
            else {
                energyChunkWanderController.updatePosition( dt );
            }

            if ( !isEnergyChunkObscured( ec ) && getSliceBounds().contains( ec.position.get().toPoint2D() ) ) {
                // Chunk is in a place where it can migrate to the slices and
                // stop moving.
                moveEnergyChunkToSlices( energyChunkWanderController.getEnergyChunk() );
            }
        }
    }

    @Override public void addEnergyChunk( EnergyChunk ec ) {
        if ( isEnergyChunkObscured( ec ) ) {
            // Chunk obscured by a model element in the beaker, probably
            // because the chunk just came from the model element.
            ec.zPosition.set( 0.0 );
            approachingEnergyChunks.add( ec );
            energyChunkWanderControllers.add( new EnergyChunkWanderController( ec, position ) );
        }
        else {
            super.addEnergyChunk( ec );
        }
    }
}
