package edu.colorado.phet.moleculeshapes.jme;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;

import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

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

    private static final Random random = new Random( System.currentTimeMillis() );

    private List<ElectronPair> pairs = new ArrayList<ElectronPair>();
    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private boolean dragging = false;

    // TODO: this doesn't make sense! convert to particle or something
    private ElectronPair centerPair = new ElectronPair( new ImmutableVector3D(), false );

    private Node molecule; //The molecule to display and rotate

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
        AtomNode center = new AtomNode( centerPair, assetManager );
        molecule.attachChild( center );

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            addPair( new ElectronPair( new ImmutableVector3D( x, y, 0 ), false ) );
        }
        addPair( new ElectronPair( new ImmutableVector3D( 0, 0, 10 ), true ) );
        addPair( new ElectronPair( new ImmutableVector3D( -7, 0, 7 ), true ) );

        rebuildBonds();

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

    public void testAddAtom( boolean isLonePair ) {
        ImmutableVector3D initialPosition = new ImmutableVector3D( Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5 ).normalized().times( 7 );
        addPair( new ElectronPair( initialPosition, isLonePair ) );
    }

    public void testRemoveAtom() {
        if ( !pairs.isEmpty() ) {
            removePair( pairs.get( random.nextInt( pairs.size() ) ) );
        }
    }

    @Override public synchronized void simpleUpdate( final float tpf ) {
//        rotation = new Quaternion().fromAngles( 0, 0.2f * tpf, 0 ).mult( rotation );
        for ( ElectronPair pair : pairs ) {
            // run our fake physics
            pair.stepForward( tpf );
            for ( ElectronPair otherPair : pairs ) {
                if ( otherPair != pair ) {
                    pair.repulseFrom( otherPair, tpf );
                }
            }
            pair.attractTo( centerPair, tpf );
        }
        rebuildBonds();
        molecule.setLocalRotation( rotation );
    }

    private void addPair( final ElectronPair pair ) {
        enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                pairs.add( pair );
                AtomNode atomNode = new AtomNode( pair, assetManager );
                atomNodes.add( atomNode );
                molecule.attachChild( atomNode );
                rebuildBonds();
                return null;
            }
        } );
    }

    private void removePair( final ElectronPair pair ) {
        enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                pairs.remove( pair );
                for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                    if ( atomNode.pair == pair ) {
                        atomNodes.remove( atomNode );
                        molecule.detachChild( atomNode );
                    }
                }
                return null;
            }
        } );
    }

    private void rebuildBonds() {
        // necessary for now since just updating their geometry shows significant errors
        for ( BondNode bondNode : bondNodes ) {
            molecule.detachChild( bondNode );
        }
        bondNodes.clear();
        for ( ElectronPair pair : pairs ) {
            BondNode bondNode = new BondNode( centerPair, pair, assetManager );
            molecule.attachChild( bondNode );
            bondNodes.add( bondNode );
        }
    }

    public static Vector3f vectorConversion( ImmutableVector3D vec ) {
        // TODO: move to utilities
        return new Vector3f( (float) vec.getX(), (float) vec.getY(), (float) vec.getZ() );
    }

    public static void main( String[] args ) throws IOException {
        new MoleculeApplication().start();
    }
}