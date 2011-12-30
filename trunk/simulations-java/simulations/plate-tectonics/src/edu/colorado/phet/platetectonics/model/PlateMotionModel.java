// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.model;

import java.util.Random;

import edu.colorado.phet.lwjglphet.math.ImmutableVector2F;
import edu.colorado.phet.platetectonics.model.regions.Region.Type;
import edu.colorado.phet.platetectonics.model.regions.SimpleRegion;
import edu.colorado.phet.platetectonics.util.Grid3D;

public class PlateMotionModel extends PlateModel {

    private final Terrain terrain;
    private int rightCrustOffset;

    public PlateMotionModel( final Grid3D grid ) {
        super( grid );

        terrain = new Terrain( 255, 32 ) {{
            setXBounds( grid.getBounds().getMinX(), grid.getBounds().getMaxX() );
            setZBounds( grid.getBounds().getMinZ(), grid.getBounds().getMaxZ() );
        }};
        addTerrain( terrain );
        updateTerrain();


        ImmutableVector2F[] leftCrustTop = new ImmutableVector2F[terrain.numXSamples];
        ImmutableVector2F[] leftCrustBottom = new ImmutableVector2F[terrain.numXSamples];
        ImmutableVector2F[] leftLithosphereBottom = new ImmutableVector2F[terrain.numXSamples];
        ImmutableVector2F[] lowerBounds = new ImmutableVector2F[terrain.numXSamples];

        rightCrustOffset = 0;

        // initialize subducting plate coordinates
        for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
            float x = terrain.xData[xIndex];
            float minX = terrain.xData[0];
            if ( x <= 0 ) {
                rightCrustOffset = xIndex;
            }
            float topElevation = getLeftCrustTopElevation( x );
            leftCrustTop[xIndex] = new ImmutableVector2F( x, topElevation );
            leftCrustBottom[xIndex] = new ImmutableVector2F( Math.max( x - 2000, minX ), topElevation - 7000 );
            leftLithosphereBottom[xIndex] = new ImmutableVector2F( Math.max( x - 17000, minX ), topElevation - 57000 );
            if ( x < 0 ) {
                leftLithosphereBottom[xIndex] = new ImmutableVector2F(
                        leftLithosphereBottom[xIndex].x, // original x
                        leftLithosphereBottom[xIndex].getY() - x / 75 ); // offset y
            }
            lowerBounds[xIndex] = new ImmutableVector2F( x, grid.getBounds().getMinY() - 500000 );
        }

        // lazy-blur the left lithosphere bottom
        boxBlurY( leftLithosphereBottom );
        boxBlurY( leftLithosphereBottom );
        boxBlurY( leftLithosphereBottom );
        boxBlurY( leftLithosphereBottom );
        boxBlurY( leftLithosphereBottom );
        boxBlurY( leftLithosphereBottom );

        // left crust region
        addRegion( new SimpleRegion( Type.CRUST, leftCrustTop, leftCrustBottom ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return 2700; // TODO!
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                return 0;
            }

            @Override public boolean isStatic() {
                return true;
            }
        } );

        addRegion( new SimpleRegion( Type.UPPER_MANTLE, leftCrustBottom, leftLithosphereBottom ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return 3300; // TODO!
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                return 0;
            }

            @Override public boolean isStatic() {
                return true;
            }
        } );

        addRegion( new SimpleRegion( Type.UPPER_MANTLE, leftLithosphereBottom, lowerBounds ) {
            @Override public float getDensity( ImmutableVector2F position ) {
                return 3500; // TODO!
            }

            @Override public float getTemperature( ImmutableVector2F position ) {
                return 0;
            }

            @Override public boolean isStatic() {
                return true;
            }
        } );
    }

    private int eruptZIndex = 0;
    private int eruptCounter = 0;
    private final Random random = new Random( System.currentTimeMillis() );

    @Override public void update( double timeElapsed ) {
        super.update( timeElapsed );
        if ( eruptCounter++ > 2000 ) {
            eruptCounter = 0;
            eruptZIndex = random.nextInt( terrain.numZSamples );
        }
//        erupt( rightCrustOffset + 15, eruptZIndex );
//        erode();
//        smootheRightCrust();
//        randomCrustStuff();

        modelChanged.updateListeners();
    }

    private void randomCrustStuff() {
        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
            for ( int xIndex = rightCrustOffset + 2; xIndex < terrain.numXSamples; xIndex++ ) {
                float elevation = terrain.getElevation( xIndex, zIndex );
                if ( elevation < 30 ) {
                    continue;
                }
                terrain.setElevation( elevation + 50 * ( random.nextFloat() - 0.499f ), xIndex, zIndex );
            }
        }
    }

    private void erupt( int xIndex, int zIndex ) {
        terrain.setElevation( terrain.getElevation( xIndex, zIndex ) + 150, xIndex, zIndex );
    }

    private void erode( int xFrom, int zFrom, int xTo, int zTo, float ratio ) {
        float fromElevation = terrain.getElevation( xFrom, zFrom );
        float toElevation = terrain.getElevation( xTo, zTo );
        float difference = ( fromElevation - toElevation ) * ratio;
        terrain.setElevation( fromElevation - difference, xFrom, zFrom );
        terrain.setElevation( toElevation + difference, xTo, zTo );
    }

    private void erode() {
        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
            for ( int xIndex = rightCrustOffset + 2; xIndex < terrain.numXSamples; xIndex++ ) {
                float elevation = terrain.getElevation( xIndex, zIndex );
                if ( elevation < 30 ) {
                    continue;
                }
                erode( xIndex - 1, zIndex, xIndex, zIndex, 0.00001f );
                if ( zIndex > 1 ) {
                    erode( xIndex - 1, zIndex - 1, xIndex, zIndex, 0.00001f );
                }
                if ( zIndex < terrain.numZSamples - 1 ) {
                    erode( xIndex - 1, zIndex + 1, xIndex, zIndex, 0.00001f );
                }
            }
        }
    }

    private void smootheRightCrust() {
        for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
            for ( int xIndex = rightCrustOffset + 1; xIndex < terrain.numXSamples; xIndex++ ) {
                float elevation = terrain.getElevation( xIndex, zIndex );
                if ( elevation < 30 ) {
                    continue;
                }
                float newRatio = 0.001f;
                terrain.setElevation( elevation * ( 1 - newRatio ) + terrain.getElevation( xIndex - 1, zIndex ) * newRatio, xIndex, zIndex );
            }
        }
    }

    private void boxBlurY( ImmutableVector2F[] vertexArray ) {
        for ( int i = 2; i < vertexArray.length - 2; i++ ) {
            vertexArray[i].y = (
                                       vertexArray[i - 2].getY() +
                                       vertexArray[i - 1].getY() +
                                       vertexArray[i].getY() +
                                       vertexArray[i + 1].getY() +
                                       vertexArray[i + 2].getY()
                               ) / 5;
        }
    }

    private float trenchDepth = -11000;
    private float oceanSlope = -0.005f;

    private void updateTerrain() {
        for ( int xIndex = 0; xIndex < terrain.numXSamples; xIndex++ ) {
            float x = terrain.xData[xIndex];
            for ( int zIndex = 0; zIndex < terrain.numZSamples; zIndex++ ) {
                float z = terrain.zData[zIndex];

                if ( x < 0 ) {
                    terrain.setElevation( getLeftCrustTopElevation( x ), xIndex, zIndex );
                }
                else {
                    terrain.setElevation( (float) ( trenchDepth + Math.atan( x / 20000 ) * 9000 ), xIndex, zIndex );
                }
            }
        }
    }

    private float trenchPointSlope = -0.5f;
    private float finalSlope = -1;
    private float leftXScale = 8000;
    private float rightXScale = 80000;

    private float getLeftCrustTopElevation( float x ) {
        if ( x <= 0 ) {
            return ( (float) ( trenchDepth + oceanSlope * x + Math.atan( x / leftXScale ) * leftXScale * trenchPointSlope ) );
        }
        else {
            // used mathematica to integrate: Integrate[((-ArcTan[x/rightXScale] + \[Pi]/2)/(\[Pi]/2))*(trenchPointSlope - finalSlope) + finalSlope,x] // InputForm
            // then offset so at x=0 it is trenchDepth
            return (float) ( ( Math.PI * trenchPointSlope * x +
                               2 * ( finalSlope - trenchPointSlope ) * x * Math.atan( x / rightXScale ) -
                               rightXScale * ( finalSlope - trenchPointSlope ) *
                               Math.log( rightXScale * rightXScale + x * x ) ) /
                             Math.PI + ( ( rightXScale * ( finalSlope - trenchPointSlope ) *
                                           Math.log( rightXScale * rightXScale ) ) / Math.PI ) + trenchDepth );
        }
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

    public static void main( String[] args ) {
        System.out.println( getSimplifiedMantleTemperature( 0 ) );
        System.out.println( getSimplifiedMantleTemperature( 0 ) - getSimplifiedMantleTemperature( -1000 ) );
        System.out.println( getSimplifiedMantleTemperature( -20000 ) );
        System.out.println( getSimplifiedMantleTemperature( -100000 ) );
    }
}
