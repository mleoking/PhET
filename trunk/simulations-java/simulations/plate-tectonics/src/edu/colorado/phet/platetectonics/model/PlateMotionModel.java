// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.common.phetcommon.util.function.Function0.Constant;
import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.regions.Region.Type;
import edu.colorado.phet.platetectonics.model.regions.SimpleConstantRegion;
import edu.colorado.phet.platetectonics.util.Bounds3D;

public class PlateMotionModel extends PlateModel {

    public static enum PlateType {
        CONTINENTAL,
        YOUNG_OCEANIC,
        OLD_OCEANIC
    }

    private final Terrain leftTerrain;
    private final Terrain rightTerrain;

    private float simpleMantleTop = -10000; // 10km depth
    private float simpleMantleBottom = -200000; // 200km depth

    private final int sideSamples = 127;
    private final int totalSamples = 2 * sideSamples + 1;
    private final int zSamples = 32;

    private List<Sample> leftCrustSamples = new ArrayList<Sample>();
    private Sample middleCrustSample;
    private List<Sample> rightCrustSamples = new ArrayList<Sample>();

    private List<Sample> leftMantleSamples = new ArrayList<Sample>();
    private Sample middleMantleSample;
    private List<Sample> rightMantleSamples = new ArrayList<Sample>();

    private List<Sample> bottomSamples = new ArrayList<Sample>();
    private final ImmutableVector2F[] leftCrustTop;
    private final ImmutableVector2F[] rightCrustTop;
    private final ImmutableVector2F[] leftMantleTop;
    private final ImmutableVector2F[] rightMantleTop;
    private final ImmutableVector2F[] mantleTop;
    private final ImmutableVector2F[] mantleBottom;

    private PlateType leftPlateType = null;
    private PlateType rightPlateType = null;

    public PlateMotionModel( final Bounds3D bounds ) {
        super( bounds );

        final float minX = bounds.getMinX();
        final float maxX = bounds.getMaxX();
        final float centerX = bounds.getCenterX();

        final float minZ = bounds.getMinZ();
        final float maxZ = bounds.getMaxZ();

        middleCrustSample = new Sample( new ImmutableVector2F( centerX, 0 ) );
        middleMantleSample = new Sample( new ImmutableVector2F( centerX, simpleMantleTop ) );
        rightCrustSamples.add( middleCrustSample );
        rightMantleSamples.add( middleMantleSample );
        for ( int i = 0; i < sideSamples; i++ ) {
            float leftX = getLeftX( i );
            float rightX = getRightX( i );
            leftCrustSamples.add( new Sample( new ImmutableVector2F( leftX, 0 ) ) );
            rightCrustSamples.add( new Sample( new ImmutableVector2F( rightX, 0 ) ) );
            leftMantleSamples.add( new Sample( new ImmutableVector2F( leftX, simpleMantleTop ) ) );
            rightMantleSamples.add( new Sample( new ImmutableVector2F( rightX, simpleMantleTop ) ) );
        }
        leftCrustSamples.add( middleCrustSample );
        leftMantleSamples.add( middleMantleSample );
        for ( int i = 0; i < totalSamples; i++ ) {
            float x = minX + ( maxX - minX ) * ( (float) i ) / ( (float) ( totalSamples - 1 ) );
            bottomSamples.add( new Sample( new ImmutableVector2F( x, simpleMantleBottom ) ) );
        }

        leftTerrain = new Terrain( sideSamples + 1, zSamples ) {{
            setXBounds( minX, centerX );
            setZBounds( minZ, maxZ );
        }};
        addTerrain( leftTerrain );
        rightTerrain = new Terrain( sideSamples + 1, zSamples ) {{
            setXBounds( centerX, maxX );
            setZBounds( minZ, maxZ );
        }};
        addTerrain( rightTerrain );

        leftCrustTop = new ImmutableVector2F[sideSamples + 1];
        rightCrustTop = new ImmutableVector2F[sideSamples + 1];
        leftMantleTop = new ImmutableVector2F[sideSamples + 1];
        rightMantleTop = new ImmutableVector2F[sideSamples + 1];
        mantleTop = new ImmutableVector2F[totalSamples];
        mantleBottom = new ImmutableVector2F[totalSamples];

        updateTerrain();
        updateRegions();

        for ( int i = 0; i < totalSamples; i++ ) {
            mantleBottom[i] = bottomSamples.get( i ).position;
        }

        addRegion( new SimpleConstantRegion( Type.UPPER_MANTLE, mantleTop, mantleBottom, new Constant<Double>( 3700.0 ),
                                             new Constant<Double>( 0.0 ) ) );
    }

    // i from 0 to sideSamples-1
    private float getLeftX( int i ) {
        return bounds.getMinX() + ( bounds.getCenterX() - bounds.getMinX() ) * ( (float) i ) / (float) sideSamples;
    }

    // i from 0 to sideSamples-1
    private float getRightX( int i ) {
        return bounds.getCenterX() + ( bounds.getMaxX() - bounds.getCenterX() ) * ( (float) ( i + 1 ) ) / (float) sideSamples;
    }

    public void dropLeftCrust( PlateType type ) {
        // TODO: update the middle crust sample sometime
        leftPlateType = type;
        for ( int i = 0; i < sideSamples; i++ ) {
            float x = getLeftX( i );
            leftCrustSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustTop( type ) );
            leftMantleSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustBottom( type ) );
        }
        updateRegions();
        updateTerrain();
        addRegion( new SimpleConstantRegion( Type.CRUST, leftCrustTop, leftMantleTop,
                                             new Constant<Double>( (double) getFreshDensity( type ) ),
                                             new Constant<Double>( 0.0 ) ) );
    }

    public void dropRightCrust( PlateType type ) {
        // TODO: update the middle crust sample sometime
        rightPlateType = type;
        for ( int i = 0; i < sideSamples; i++ ) {
            float x = getRightX( i );
            rightCrustSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustTop( type ) );
            rightMantleSamples.get( i ).position = new ImmutableVector2F( x, getFreshCrustBottom( type ) );
        }
        updateRegions();
        updateTerrain();
        addRegion( new SimpleConstantRegion( Type.CRUST, rightCrustTop, rightMantleTop,
                                             new Constant<Double>( (double) getFreshDensity( type ) ),
                                             new Constant<Double>( 0.0 ) ) );
    }

    private void updateRegions() {
        mantleTop[sideSamples] = middleMantleSample.position;
        leftCrustTop[sideSamples] = middleCrustSample.position;
        leftMantleTop[sideSamples] = middleMantleSample.position;
        rightCrustTop[0] = middleCrustSample.position;
        rightMantleTop[0] = middleMantleSample.position;
        for ( int i = 0; i < sideSamples; i++ ) {
            // left side
            mantleTop[i] = leftMantleSamples.get( i ).position;
            leftMantleTop[i] = leftMantleSamples.get( i ).position;
            leftCrustTop[i] = leftCrustSamples.get( i ).position;

            // right side
            mantleTop[i + sideSamples + 1] = rightMantleSamples.get( i ).position;
            rightMantleTop[i + 1] = rightMantleSamples.get( i ).position;
            rightCrustTop[i + 1] = rightCrustSamples.get( i ).position;
        }
    }

    private void updateTerrain() {
        // middle elevation is average between the two (for now)
        float middleLeftY = ( hasLeftPlate() ? leftCrustSamples : leftMantleSamples ).get( sideSamples - 1 ).position.y;
        float middleRightY = ( hasRightPlate() ? rightCrustSamples : rightMantleSamples ).get( 0 ).position.y;
        float middleElevation = ( middleLeftY + middleRightY ) / 2;
        for ( int row = 0; row < zSamples; row++ ) {
            leftTerrain.setElevation( middleElevation, sideSamples, row );
            rightTerrain.setElevation( middleElevation, 0, row );
        }
        for ( int column = 0; column < sideSamples; column++ ) {
            // left side
            float leftElevation = hasLeftPlate() ? leftCrustSamples.get( column ).position.y : leftMantleSamples.get( column ).position.y;
            for ( int row = 0; row < zSamples; row++ ) {
                // set the elevation for the whole column
                leftTerrain.setElevation( leftElevation, column, row );
            }

            // right side
            float rightElevation = hasRightPlate() ? rightCrustSamples.get( column ).position.y : rightMantleSamples.get( column ).position.y;
            for ( int row = 0; row < zSamples; row++ ) {
                rightTerrain.setElevation( rightElevation, column + 1, row );
            }
        }
    }

    public static float getFreshDensity( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return 2750;
            case YOUNG_OCEANIC:
                return 3000;
            case OLD_OCEANIC:
                return 3070;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public static float getFreshCrustBottom( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return -40000;
            case YOUNG_OCEANIC:
                return -10000;
            case OLD_OCEANIC:
                return -10000;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public static float getFreshCrustTop( PlateType type ) {
        switch( type ) {
            case CONTINENTAL:
                return 5000;
            case YOUNG_OCEANIC:
                return -3000;
            case OLD_OCEANIC:
                return -3000;
            default:
                throw new RuntimeException( "unknown type: " + type );
        }
    }

    public boolean hasLeftPlate() {
        return leftPlateType != null;
    }

    public boolean hasRightPlate() {
        return rightPlateType != null;
    }

    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );

        modelChanged.updateListeners();
    }

    @Override public double getElevation( double x, double z ) {
        return 0;
    }

    @Override public double getDensity( double x, double y ) {
        return 0; // TODO
    }

    @Override public double getTemperature( double x, double y ) {
        return 0; // TODO
    }

    public static double getSimplifiedMantleTemperature( double y ) {
        double depth = -y;
        return 273.15 + ( 0.0175 - 3.04425e-9 * depth ) * depth; // based on T0 + (q00/k)*y - (rho*H/(2*k))*y^2 from model doc
    }

    public static class Sample {
        public ImmutableVector2F position;

        public Sample( ImmutableVector2F position ) {
            this.position = position;
        }
    }

    public static void main( String[] args ) {
        System.out.println( getSimplifiedMantleTemperature( 0 ) );
        System.out.println( getSimplifiedMantleTemperature( 0 ) - getSimplifiedMantleTemperature( -1000 ) );
        System.out.println( getSimplifiedMantleTemperature( -20000 ) );
        System.out.println( getSimplifiedMantleTemperature( -100000 ) );
    }
}
