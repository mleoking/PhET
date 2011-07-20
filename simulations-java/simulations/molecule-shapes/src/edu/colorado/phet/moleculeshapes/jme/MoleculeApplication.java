package edu.colorado.phet.moleculeshapes.jme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
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
public class MoleculeApplication extends BaseApplication {

    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";

    public static final String MAP_DRAG = "CameraDrag";

    private static final float MOUSE_SCALE = 3.0f;

    private boolean dragging = false;

    //The molecule to display and rotate
    private Node molecule;

    //The angle about which the molecule should be rotated, changes as a function of time
    private Quaternion rotation = new Quaternion();

    @Override public void initialize() {
        super.initialize();

        /*---------------------------------------------------------------------------*
        * input handling
        *----------------------------------------------------------------------------*/

        inputManager.addMapping( MoleculeApplication.MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputManager.addMapping( MoleculeApplication.MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputManager.addMapping( MoleculeApplication.MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputManager.addMapping( MoleculeApplication.MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

        inputManager.addMapping( MAP_DRAG, new MouseButtonTrigger( MouseInput.BUTTON_LEFT ) );

        inputManager.addListener( new ActionListener() {
                                      public void onAction( String name, boolean value, float tpf ) {

                                          // record whether the mouse button is down
                                          if ( name.equals( MAP_DRAG ) ) {
                                              dragging = value;
                                          }
                                      }
                                  }, MAP_DRAG );
        inputManager.addListener( new AnalogListener() {
                                      public void onAnalog( String name, float value, float tpf ) {
                                          if ( dragging ) {
                                              if ( name.equals( MoleculeApplication.MAP_LEFT ) ) {
                                                  rotation = new Quaternion().fromAngles( 0, -value * MOUSE_SCALE, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeApplication.MAP_RIGHT ) ) {
                                                  rotation = new Quaternion().fromAngles( 0, value * MOUSE_SCALE, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeApplication.MAP_UP ) ) {
                                                  rotation = new Quaternion().fromAngles( -value * MOUSE_SCALE, 0, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeApplication.MAP_DOWN ) ) {
                                                  rotation = new Quaternion().fromAngles( value * MOUSE_SCALE, 0, 0 ).mult( rotation );
                                              }
                                          }
                                      }
                                  }, MAP_LEFT, MAP_RIGHT, MAP_UP, MAP_DOWN, MAP_DRAG );

        molecule = new Node();
        rootNode.attachChild( molecule );

        /*---------------------------------------------------------------------------*
        * atoms
        *----------------------------------------------------------------------------*/

        //Create the central atom
        AtomNode center = new AtomNode( new ElectronPair( new ImmutableVector3D() ), assetManager );
        molecule.attachChild( center );

        List<ElectronPair> pairs = new ArrayList<ElectronPair>();

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            pairs.add( new ElectronPair( new ImmutableVector3D( x, y, 0 ) ) );
        }
        pairs.add( new ElectronPair( new ImmutableVector3D( 0, 0, 10 ) ) );
        pairs.add( new ElectronPair( new ImmutableVector3D( 0, 0, -10 ) ) );

        // construct the other atom nodes
        for ( ElectronPair pair : pairs ) {
            attach( center, new AtomNode( pair, assetManager ) );
        }

        /*---------------------------------------------------------------------------*
        * lights
        *----------------------------------------------------------------------------*/
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection( new Vector3f( 1, -0.5f, -2 ).normalizeLocal() );
        sun.setColor( new ColorRGBA( 0.8f, 0.8f, 0.8f, 1f ) );
        rootNode.addLight( sun );

        DirectionalLight moon = new DirectionalLight();
        moon.setDirection( new Vector3f( -2, 1, -1 ).normalizeLocal() );
        moon.setColor( new ColorRGBA( 0.6f, 0.6f, 0.6f, 1f ) );
        rootNode.addLight( moon );

        /*---------------------------------------------------------------------------*
        * camera
        *----------------------------------------------------------------------------*/
        cam.setLocation( new Vector3f( cam.getLocation().getX(), cam.getLocation().getY(), cam.getLocation().getZ() + 30 ) );

        setDisplayFps( false );
        setDisplayStatView( false );
    }

    @Override public void simpleUpdate( final float tpf ) {
        rotation = new Quaternion().fromAngles( 0, 0.2f * tpf, 0 ).mult( rotation );
        molecule.setLocalRotation( rotation );
    }

    //Attach the two atoms together with a bond and add to the molecule
    private void attach( Geometry center, Geometry newSphere ) {
        molecule.attachChild( newSphere );
        final LineCylinder shiny_rock = new LineCylinder( center.getLocalTranslation(), newSphere.getLocalTranslation() );
        Material mat_lit = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
        shiny_rock.setMaterial( mat_lit );
        molecule.attachChild( shiny_rock );
    }

    //Taken from the forum here: http://jmonkeyengine.org/groups/graphics/forum/topic/creating-a-cylinder-from-one-point-to-another/
    public class LineCylinder extends Geometry {
        public LineCylinder( Vector3f start, Vector3f end ) {
            super( "LineCylinder" );
            this.mesh = new Cylinder( 4, 8, .5f, start.distance( end ) );
            setLocalTranslation( FastMath.interpolateLinear( .5f, start, end ) );
            lookAt( end, Vector3f.UNIT_Y );
        }
    }

    public static void main( String[] args ) throws IOException {
        new MoleculeApplication().start();
    }
}