// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;

import java.nio.ByteBuffer;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.event.UpdateListener;
import edu.colorado.phet.common.phetcommon.model.event.VoidNotifier;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.LodDistanceCalculatorFactory;
import com.jme3.terrain.geomipmap.lodcalc.SimpleLodThreshold;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;

public class HelloTerrain extends SimpleApplication {

    private TerrainQuad terrain;
    Material mat_terrain;
    private AnimatedPlateModel model;
    private static final int PIXELS = 512; // number of pixels
    private static final double PIXEL_SCALE = 250; // each pixel spans over this many meters
    private static final double SEA_LEVEL_IN_PIXELS = 100;
    private static final VoidNotifier updateNotifier = new VoidNotifier();

    public static void main( String[] args ) {
        HelloTerrain app = new HelloTerrain();
        app.start();
    }

    public HelloTerrain() {
        model = new AnimatedPlateModel();
    }

    @Override public void simpleUpdate( float tpf ) {
        super.simpleUpdate( tpf );

        model.update( tpf );
        updateNotifier.updateListeners();
    }

    public float getHeightAtPixel( int x, int z ) {
        double elevation = getElevationAtPixel( x, z );
        return (float) MathUtil.clamp( 0, elevation / PIXEL_SCALE + SEA_LEVEL_IN_PIXELS, 255 );
    }

    private double getElevationAtPixel( int x, int z ) {
        return model.getElevation(
                ( x - PIXELS / 2 ) * PIXEL_SCALE,
                ( z - PIXELS / 2 ) * PIXEL_SCALE );
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed( 50 );

        AbstractHeightMap heightmap = new AbstractHeightMap() {
            {
                size = PIXELS + 1;
            }

            public boolean load() {
                // clean up data if needed.
                if ( null != heightData ) {
                    unloadHeightMap();
                }
                heightData = new float[size * size];
                for ( int i = 0; i < size; i++ ) {
                    for ( int j = 0; j < size; j++ ) {
                        setHeightAtPoint( getHeightAtPixel( i, j ), j, i );
                    }
                }
                return true;
            }
        };
        heightmap.load();

        /** 1. Create terrain material and load four textures into it. */
        mat_terrain = new Material( assetManager, "Common/MatDefs/Terrain/Terrain.j3md" ) {{

            /** 1.1) Add ALPHA map (for red-blue-green coded splat textures) */
            setTexture( "Alpha", new Texture2D() {{
                int width = PIXELS;
                int height = PIXELS;
                setImage( new com.jme3.texture.Image( Format.RGBA8, width, height, ByteBuffer.allocateDirect( 4 * width * height ) ) {
                    {
                        updateTerrain();
                        updateNotifier.addUpdateListener( new UpdateListener() {
                            public void update() {
                                updateTerrain();
                            }
                        }, true );
                    }

                    public void updateTerrain() {
                        ByteBuffer buffer = data.get( 0 );
                        buffer.clear();
                        for ( int i = 0; i < width; i++ ) {
                            for ( int j = 0; j < height; j++ ) {
                                double elevation = getElevationAtPixel( i, j );
                                int stonyness = MathUtil.clamp( 0, (int) ( ( elevation - 10000 ) / 20 ) + 255, 255 ); // fully stony at 10km
                                int beachyness = elevation < 10 ? 255 : 0;
                                buffer.put( (byte) ( 255 - stonyness - beachyness ) ); // grass
                                buffer.put( (byte) stonyness ); // stone
                                buffer.put( (byte) beachyness ); // beach (cobbles right now)
                                buffer.put( (byte) 1 );
                            }
                        }
                        setUpdateNeeded();
                    }
                } );
            }} );

            /** 1.2) Add GRASS texture into the red layer (Tex1). */
            Texture grass = assetManager.loadTexture( "Textures/Terrain/splat/grass.jpg" );
            grass.setWrap( WrapMode.Repeat );
            setTexture( "Tex1", grass );
            setFloat( "Tex1Scale", 64f );

            /** 1.3) Add DIRT texture into the green layer (Tex2) */
            Texture dirt = assetManager.loadTexture( "Textures/Terrain/splat/dirt.jpg" );
            dirt.setWrap( WrapMode.Repeat );
            setTexture( "Tex2", dirt );
            setFloat( "Tex2Scale", 32f );

            /** 1.4) Add ROAD texture into the blue layer (Tex3) */
            Texture rock = assetManager.loadTexture( "Textures/Terrain/splat/road.jpg" );
            rock.setWrap( WrapMode.Repeat );
            setTexture( "Tex3", rock );
            setFloat( "Tex3Scale", 128f );
        }};

        /** 3. We have prepared material and heightmap.
         * Now we create the actual terrain:
         * 3.1) Create a TerrainQuad and name it "my terrain".
         * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
         * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
         * 3.4) As LOD step scale we supply Vector3f(1,1,1).
         * 3.5) We supply the prepared heightmap itself.
         */
        int patchSize = 65;
        terrain = new TerrainQuad( "my terrain", patchSize, 513, heightmap.getHeightMap() );

        /** 4. We give the terrain its material, position & scale it, and attach it. */
        terrain.setMaterial( mat_terrain );
        terrain.setLocalTranslation( 0, (float) -SEA_LEVEL_IN_PIXELS, 0 );
        rootNode.attachChild( terrain );

        /** 5. The LOD (level of detail) depends on were the camera is: */
        TerrainLodControl control = new TerrainLodControl( terrain, getCamera() );

        terrain.setLodCalculatorFactory( new LodDistanceCalculatorFactory( new SimpleLodThreshold( patchSize, 2.7f ) {{
            setLodMultiplier( 7f );
        }} ) );
//        control.setLodCalculator( new DistanceLodCalculator( patchSize, 2.7f ) ); // patch size, and a multiplier
        terrain.addControl( control );
    }
}
