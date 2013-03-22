// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.model;

import java.awt.geom.Rectangle2D;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.property.BooleanProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.energyformsandchanges.EnergyFormsAndChangesSimSharing;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkContainerSlice;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyChunkDistributor;
import edu.colorado.phet.energyformsandchanges.intro.model.EnergyContainerCategory;
import edu.colorado.phet.energyformsandchanges.intro.model.HorizontalSurface;
import edu.colorado.phet.energyformsandchanges.intro.model.RectangularThermalMovableModelElement;
import edu.colorado.phet.energyformsandchanges.intro.model.ThermalContactArea;

import static edu.colorado.phet.energyformsandchanges.common.EFACConstants.BOILING_POINT_TEMPERATURE;

/**
 * Model element that represents a beaker that contains some amount of water,
 * and the water contains energy, which includes energy chunks, and has
 * temperature.
 *
 * @author John Blanco
 */
public class Beaker extends RectangularThermalMovableModelElement {

    //-------------------------------------------------------------------------
    // Class Data
    //-------------------------------------------------------------------------

    private static final double MATERIAL_THICKNESS = 0.001; // In meters.
    private static final int NUM_SLICES = 6;
    private static final Random RAND = new Random( 1 ); // This is seeded for consistent initial energy chunk distribution.
    private static final double STEAMING_RANGE = 10; // Number of degrees Kelvin over which steam is emitted.

    // Constants that control the nature of the fluid in the beaker.
    private static final double WATER_SPECIFIC_HEAT = 4186; // In J/kg-K, source = design document.
    private static final double WATER_DENSITY = 1000.0; // In kg/m^3, source = design document (and common knowledge).
    public static final double INITIAL_FLUID_LEVEL = 0.5;

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    // Property that is used to control and track the amount of fluid in the beaker.
    public final Property<Double> fluidLevel = new Property<Double>( INITIAL_FLUID_LEVEL );

    // Top surface, which is the surface upon which other model elements can
    // sit.  For the beaker, this is only slightly above the bottom surface.
    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );

    // Bottom surface, which is the surface that touches the ground, or sits
    // on a burner, etc.
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    // Property that allows temperature changes to be monitored.
    public final Property<Double> temperature = new Property<Double>( EFACConstants.ROOM_TEMPERATURE );

    // Indicator of how much steam is being emitted.  Ranges from 0 to 1, where
    // 0 is no steam, 1 is the max amount (full boil).
    public double steamingProportion = 0;

    // Max height above water where steam still affects the temperature.
    private final double maxSteamHeight;

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /*
     * Constructor.  Initial position is the center bottom of the beaker
     * rectangle.
     */
    public Beaker( ConstantDtClock clock, Vector2D initialPosition, double width, double height, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, width, height, calculateWaterMass( width, height * INITIAL_FLUID_LEVEL ), WATER_SPECIFIC_HEAT, energyChunksVisible );
        this.maxSteamHeight = 2 * height;

        // Update the top and bottom surfaces whenever the position changes.
        position.addObserver( new VoidFunction1<Vector2D>() {
            public void apply( final Vector2D immutableVector2D ) {
                updateSurfaces();
            }
        } );
    }

    //-------------------------------------------------------------------------
    // Methods
    //-------------------------------------------------------------------------

    private void updateSurfaces() {
        topSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ),
                                               getRect().getMinY() + MATERIAL_THICKNESS,
                                               this ) );
        bottomSurface.set( new HorizontalSurface( new DoubleRange( getRect().getMinX(), getRect().getMaxX() ),
                                                  getRect().getMinY(),
                                                  this ) );
    }

    /*
     * Get the untranslated rectangle that defines the shape of the beaker.
     */
    public Rectangle2D getRawOutlineRect() {
        return new Rectangle2D.Double( -width / 2, 0, width, height );
    }

    @Override public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - width / 2,
                                       position.get().getY(),
                                       width,
                                       height );
    }

    @Override protected void stepInTime( double dt ) {
        super.stepInTime( dt );
        temperature.set( getTemperature() );
        steamingProportion = 0;
        if ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature.get() < STEAMING_RANGE ) {
            // Water is emitting some amount of steam.  Set the proportionate amount.
            steamingProportion = MathUtil.clamp( 0, 1 - ( ( EFACConstants.BOILING_POINT_TEMPERATURE - temperature.get() ) / STEAMING_RANGE ), 1 );
        }
    }

    @Override public Property<HorizontalSurface> getTopSurfaceProperty() {
        return topSurface;
    }

    @Override public Property<HorizontalSurface> getBottomSurfaceProperty() {
        return bottomSurface;
    }

    @Override protected void addInitialEnergyChunks() {
        for ( EnergyChunkContainerSlice slice : slices ) {
            slice.energyChunkList.clear();
        }
        int targetNumChunks = EFACConstants.ENERGY_TO_NUM_CHUNKS_MAPPER.apply( energy );
        Rectangle2D initialChunkBounds = getSliceBounds();
        while ( getNumEnergyChunks() < targetNumChunks ) {
            // Add a chunk at a random location in the beaker.
            addEnergyChunkToNextSlice( new EnergyChunk( EnergyType.THERMAL, EnergyChunkDistributor.generateRandomLocation( initialChunkBounds ), energyChunksVisible ) );
        }

        // Distribute the energy chunks within the beaker.
        for ( int i = 0; i < 1000; i++ ) {
            if ( !EnergyChunkDistributor.updatePositions( slices, EFACConstants.SIM_TIME_PER_TICK_NORMAL ) ) {
                break;
            }
        }
    }

    @Override protected void addEnergyChunkToNextSlice( EnergyChunk ec ) {
        double totalSliceArea = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            totalSliceArea += slice.getShape().getBounds2D().getWidth() * slice.getShape().getBounds2D().getHeight();
        }
        double sliceSelectionValue = RAND.nextDouble();
        EnergyChunkContainerSlice chosenSlice = slices.get( 0 );
        double accumulatedArea = 0;
        for ( EnergyChunkContainerSlice slice : slices ) {
            accumulatedArea += slice.getShape().getBounds2D().getWidth() * slice.getShape().getBounds2D().getHeight();
            if ( accumulatedArea / totalSliceArea >= sliceSelectionValue ) {
                chosenSlice = slice;
                break;
            }
        }
        chosenSlice.addEnergyChunk( ec );
    }

    private static double calculateWaterMass( double width, double height ) {
        return Math.PI * Math.pow( width / 2, 2 ) * height * WATER_DENSITY;
    }

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( new Rectangle2D.Double( position.get().getX() - width / 2,
                                                               position.get().getY(),
                                                               width,
                                                               height * fluidLevel.get() ), true );
    }

    /*
     * Get the area where the temperature of the steam can be sensed.
     */
    public Rectangle2D getSteamArea() {
        // Height of steam rectangle is based on beaker height and steamingProportion.
        double liquidWaterHeight = height * fluidLevel.get();
        return new Rectangle2D.Double( position.get().getX() - width / 2,
                                       position.get().getY() + liquidWaterHeight,
                                       width,
                                       maxSteamHeight );
    }

    public double getSteamTemperature( double heightAboveWater ) {
        Function.LinearFunction mappingFunction = new Function.LinearFunction( 0, maxSteamHeight * steamingProportion, temperature.get(), EFACConstants.ROOM_TEMPERATURE );
        return Math.max( mappingFunction.evaluate( heightAboveWater ), EFACConstants.ROOM_TEMPERATURE );
    }

    @Override protected void addEnergyChunkSlices() {
        assert slices.size() == 0; // Check that his has not been already called.
        final Rectangle2D fluidRect = new Rectangle2D.Double( position.get().getX() - width / 2,
                                                              position.get().getY(),
                                                              width,
                                                              height * INITIAL_FLUID_LEVEL );
        double widthYProjection = Math.abs( width * EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER );
        for ( int i = 0; i < NUM_SLICES; i++ ) {
            double proportion = ( i + 1 ) * ( 1 / (double) ( NUM_SLICES + 1 ) );
            DoubleGeneralPath slicePath = new DoubleGeneralPath();
            {
                // The slice width is calculated to fit into the 3D projection.
                // It uses an exponential function that is shifted in order to
                // yield width value proportional to position in Z-space.
                double sliceWidth = ( -Math.pow( ( 2 * proportion - 1 ), 2 ) + 1 ) * fluidRect.getWidth();
                double bottomY = fluidRect.getMinY() - ( widthYProjection / 2 ) + ( proportion * widthYProjection );
                double topY = bottomY + fluidRect.getHeight();
                double centerX = fluidRect.getCenterX();
                double controlPointYOffset = ( bottomY - fluidRect.getMinY() ) * 0.5;
                slicePath.moveTo( centerX - sliceWidth / 2, bottomY );
                slicePath.curveTo( centerX - sliceWidth * 0.33, bottomY + controlPointYOffset, centerX + sliceWidth * 0.33, bottomY + controlPointYOffset, centerX + sliceWidth / 2, bottomY );
                slicePath.lineTo( centerX + sliceWidth / 2, topY );
                slicePath.curveTo( centerX + sliceWidth * 0.33, topY + controlPointYOffset, centerX - sliceWidth * 0.33, topY + controlPointYOffset, centerX - sliceWidth / 2, topY );
                slicePath.lineTo( centerX - sliceWidth / 2, bottomY );
            }
            slices.add( new EnergyChunkContainerSlice( slicePath.getGeneralPath(), -proportion * width, position ) );
        }
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.WATER;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.beaker;
    }

    public double getEnergyBeyondMaxTemperature() {
        return Math.max( energy - ( BOILING_POINT_TEMPERATURE * mass * specificHeat ), 0 );
    }

    // Limit max temp to the boiling point.
    @Override public double getTemperature() {
        return Math.min( super.getTemperature(), BOILING_POINT_TEMPERATURE );
    }

    /*
     * This override handles the case where the point is above the beaker.
     * In this case, we want to pull from all slices evenly, and not favor
     * the slices the bump up at the top in order to match the 3D look of the
     * water surface.
     */
    @Override public EnergyChunk extractClosestEnergyChunk( Vector2D point ) {
        boolean pointIsAboveWaterSurface = true;
        for ( EnergyChunkContainerSlice slice : slices ) {
            if ( point.getY() < slice.getShape().getBounds2D().getMaxY() ) {
                pointIsAboveWaterSurface = false;
                break;
            }
        }

        if ( !pointIsAboveWaterSurface ) {
            return super.extractClosestEnergyChunk( point );
        }

        // Point is above water surface.  Identify the slice with the highest
        // density, since this is where we will get the energy chunk.
        double maxSliceDensity = 0;
        EnergyChunkContainerSlice densestSlice = null;
        for ( EnergyChunkContainerSlice slice : slices ) {
            double sliceDensity = slice.energyChunkList.size() / ( slice.getShape().getBounds2D().getWidth() * slice.getShape().getBounds2D().getHeight() );
            if ( sliceDensity > maxSliceDensity ) {
                maxSliceDensity = sliceDensity;
                densestSlice = slice;
            }
        }

        if ( densestSlice.energyChunkList.size() == 0 ) {
            System.out.println( getClass().getName() + " - Warning: No energy chunks in the beaker, can't extract any." );
            return null;
        }

        EnergyChunk highestEnergyChunk = densestSlice.energyChunkList.get( 0 );
        for ( EnergyChunk ec : densestSlice.energyChunkList ) {
            if ( ec.position.get().getY() > highestEnergyChunk.position.get().getY() ) {
                highestEnergyChunk = ec;
            }
        }

        removeEnergyChunk( highestEnergyChunk );
        return highestEnergyChunk;
    }
}