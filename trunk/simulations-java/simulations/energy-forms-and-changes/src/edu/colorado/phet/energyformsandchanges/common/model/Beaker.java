// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.common.model;

import java.awt.geom.Rectangle2D;
import java.util.Random;

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

    private static final double WIDTH = 0.085; // In meters.
    private static final double HEIGHT = WIDTH * 1.1;
    private static final double MATERIAL_THICKNESS = 0.001; // In meters.
    private static final int NUM_SLICES = 6;
    private static final Random RAND = new Random( 1 ); // This is seeded for consistent initial energy chunk distribution.

    // Constants that control the nature of the fluid in the beaker.
    private static final double WATER_SPECIFIC_HEAT = 4186; // In J/kg-K, source = design document.
    private static final double WATER_DENSITY = 1000.0; // In kg/m^3, source = design document (and common knowledge).
    private static final double DEFAULT_INITIAL_FLUID_LEVEL = 0.5;
    private static final double FLUID_VOLUME = Math.PI * Math.pow( WIDTH / 2, 2 ) * ( DEFAULT_INITIAL_FLUID_LEVEL * HEIGHT ); // In m^3
    private static final double FLUID_MASS = FLUID_VOLUME * WATER_DENSITY; // In kg

    //-------------------------------------------------------------------------
    // Instance Data
    //-------------------------------------------------------------------------

    protected final double initialFluidLevel;

    // Property that is used to control and track the amount of fluid in the beaker.
    public final Property<Double> fluidLevel = new Property<Double>( DEFAULT_INITIAL_FLUID_LEVEL );

    // Top surface, which is the surface upon which other model elements can
    // sit.  For the beaker, this is only slightly above the bottom surface.
    private final Property<HorizontalSurface> topSurface = new Property<HorizontalSurface>( null );

    // Bottom surface, which is the surface that touches the ground, or sits
    // on a burner, etc.
    private final Property<HorizontalSurface> bottomSurface = new Property<HorizontalSurface>( null );

    //-------------------------------------------------------------------------
    // Constructor(s)
    //-------------------------------------------------------------------------

    /*
     * Constructor.  Initial position is the center bottom of the beaker
     * rectangle.
     */
    public Beaker( ConstantDtClock clock, Vector2D initialPosition, BooleanProperty energyChunksVisible ) {
        super( clock, initialPosition, WIDTH, HEIGHT, FLUID_MASS, WATER_SPECIFIC_HEAT, energyChunksVisible );
        this.initialFluidLevel = DEFAULT_INITIAL_FLUID_LEVEL;

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

    /**
     * Get the untranslated rectangle that defines the shape of the beaker.
     *
     * @return
     */
    public static Rectangle2D getRawOutlineRect() {
        return new Rectangle2D.Double( -WIDTH / 2, 0, WIDTH, HEIGHT );
    }

    @Override public Rectangle2D getRect() {
        return new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                       position.get().getY(),
                                       WIDTH,
                                       HEIGHT);
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

    public ThermalContactArea getThermalContactArea() {
        return new ThermalContactArea( new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                               position.get().getY(),
                                                               WIDTH,
                                                               HEIGHT * fluidLevel.get() ), true );
    }

    @Override protected void addEnergyChunkSlices() {
        assert slices.size() == 0; // Check that his has not been already called.
        final Rectangle2D fluidRect = new Rectangle2D.Double( position.get().getX() - WIDTH / 2,
                                                              position.get().getY(),
                                                              WIDTH,
                                                              HEIGHT * DEFAULT_INITIAL_FLUID_LEVEL );
        double widthYProjection = Math.abs( WIDTH * EFACConstants.Z_TO_Y_OFFSET_MULTIPLIER );
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
            slices.add( new EnergyChunkContainerSlice( slicePath.getGeneralPath(), -proportion * WIDTH, position ) );
        }
    }

    public EnergyContainerCategory getEnergyContainerCategory() {
        return EnergyContainerCategory.WATER;
    }

    @Override public IUserComponent getUserComponent() {
        return EnergyFormsAndChangesSimSharing.UserComponents.beaker;
    }
}