package edu.colorado.phet.moleculeshapes.reid;

import java.io.File;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Use jme3 to show a rotating molecule
 */
public class ShowMolecule extends SimpleApplication {

    //The molecule to display and rotate
    private Node molecule;

    //The angle about which the molecule should be rotated, changes as a function of time
    float angle = 0;

    //Initialize the application, creating the molecule and attaching it to the scene
    @Override public void simpleInitApp() {
        molecule = new Node();
        rootNode.attachChild( molecule );

        //Create the central atom
        double z = 0;
        final Geometry center = createSphere( 0, 0, z );
        molecule.attachChild( center );

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            attach( center, createSphere( x, y, z ) );
        }

        //Two more atoms, why not?
        attach( center, createSphere( 0, 0, z + 10 ) );
        attach( center, createSphere( 0, 0, z - 10 ) );

        /** Must add a light to make the lit object visible! */
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, 0, -2 ).normalizeLocal() );
        sun.setColor( ColorRGBA.White );
        rootNode.addLight( sun );

        cam.setLocation( new Vector3f( cam.getLocation().getX(), cam.getLocation().getY(), cam.getLocation().getZ() + 30 ) );
    }

    @Override public void simpleUpdate( final float tpf ) {
        angle += 2 * tpf;
        final Quaternion quaternion = new Quaternion();
        quaternion.fromAngles( 0, angle, 0 );
        molecule.setLocalRotation( quaternion );
    }

    //Attach the two atoms together with a bond and add to the molecule
    private void attach( Geometry center, Geometry newSphere ) {
        molecule.attachChild( newSphere );
        final LineCylinder shiny_rock = new LineCylinder( center.getLocalTranslation(), newSphere.getLocalTranslation() );
        Material mat_lit = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
        shiny_rock.setMaterial( mat_lit );
        molecule.attachChild( shiny_rock );
    }

    //Create a sphere at the specified location
    private Geometry createSphere( double x, double y, double z ) {
        Sphere sphere = new Sphere( 32, 32, 2f );
        Geometry shiny_rock = createGeometry( (float) x, (float) y, (float) z, sphere );
        return shiny_rock;
    }

    //Create a geometry for the specified sphere, uses textures from the auxiliary jar
    private Geometry createGeometry( float x, float y, float z, Sphere mesh ) {
        Geometry shiny_rock = new Geometry( "Shiny rock", mesh );
        mesh.setTextureMode( Sphere.TextureMode.Projected ); // better quality on spheres
        TangentBinormalGenerator.generate( mesh );           // for lighting effect
        Material mat_lit = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
        mat_lit.setTexture( "DiffuseMap", assetManager.loadTexture( "Textures/Terrain/Pond/Pond.png" ) );
        mat_lit.setTexture( "NormalMap", assetManager.loadTexture( "Textures/Terrain/Pond/Pond_normal.png" ) );
        mat_lit.setFloat( "Shininess", 5f ); // [0,128]
        shiny_rock.setMaterial( mat_lit );
        shiny_rock.setLocalTranslation( x, y, z ); // Move it a bit
        shiny_rock.rotate( 1.6f, 0, 0 );          // Rotate it a bit
        return shiny_rock;
    }

    //Taken from the forum here: http://jmonkeyengine.org/groups/graphics/forum/topic/creating-a-cylinder-from-one-point-to-another/
    public class LineCylinder extends Geometry {
        public LineCylinder( Vector3f start, Vector3f end ) {
            super( "LineCylinder" );
            Cylinder cyl = new Cylinder( 4, 8, .5f, start.distance( end ) );
            this.mesh = cyl;
            setLocalTranslation( FastMath.interpolateLinear( .5f, start, end ) );
            lookAt( end, Vector3f.UNIT_Y );
        }
    }


    public static void main( String[] args ) throws IOException {

//This runs the application in another JVM
//            //extract natives to user's .phet-natives directory.
//            //copy this jar file so it can be unzipped while it's being read?
//            String[] cmdArray = new String[]{PhetUtilities.getJavaPath(), "-jar", updaterBootstrap.getAbsolutePath(), src.getAbsolutePath(), dst.getAbsolutePath()};
////        log("Starting updater bootstrap with cmdArray=" + Arrays.asList(cmdArray).toString());
//            Runtime.getRuntime().exec(cmdArray);

        if ( FileUtils.isJarCodeSource() ) {
            //add natives to path
            File jarFile = FileUtils.getCodeSource();
            File tempDir = new File( System.getProperty( "java.io.tmpdir" ), jarFile.getName() );
            LibraryPathUtils.copyTo( jarFile, tempDir );
            File unzipDir = new File( tempDir.getParentFile(), "phet-unzipped" );
            LibraryPathUtils.unzip( tempDir, unzipDir );
            System.out.println( "DensityApplication.main, unzip dir=" + unzipDir.getAbsolutePath() );
            LibraryPathUtils.addDir( new File( unzipDir, "native/windows" ).getAbsolutePath() );
            LibraryPathUtils.addDir( new File( unzipDir, "native/linux" ).getAbsolutePath() );
            LibraryPathUtils.addDir( new File( unzipDir, "native/macosx" ).getAbsolutePath() );
            LibraryPathUtils.addDir( new File( unzipDir, "native/solaris" ).getAbsolutePath() );
        }

        ShowMolecule app = new ShowMolecule();
        app.start();
    }
}