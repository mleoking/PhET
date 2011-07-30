package edu.colorado.phet.moleculeshapes.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import edu.colorado.phet.moleculeshapes.model.ElectronPair;
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
public class MoleculeJMEApplication extends BaseJMEApplication {

    public static final String MAP_LEFT = "CameraLeft";
    public static final String MAP_RIGHT = "CameraRight";
    public static final String MAP_UP = "CameraUp";
    public static final String MAP_DOWN = "CameraDown";
    public static final String MAP_DRAG = "CameraDrag";

    private static final float MOUSE_SCALE = 3.0f;

    private static final Random random = new Random( System.currentTimeMillis() );

    //Contains electron pairs for lone pairs or electron pairs
    private List<ElectronPair> pairs = new ArrayList<ElectronPair>();

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private boolean dragging = false;

    // TODO: this doesn't make sense! convert to particle or something
    private ElectronPair centerPair = new ElectronPair( new ImmutableVector3D(), false );

    private Node molecule; //The molecule to display and rotate

    //The angle about which the molecule should be rotated, changes as a function of time
    private Quaternion rotation = new Quaternion();
    private int numSuccess = 0;

    //Time the configuration was started
    private long startTime;

    @Override public void initialize() {
        super.initialize();

        /*---------------------------------------------------------------------------*
        * input handling
        *----------------------------------------------------------------------------*/

        inputManager.addMapping( MoleculeJMEApplication.MAP_LEFT, new MouseAxisTrigger( MouseInput.AXIS_X, true ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_RIGHT, new MouseAxisTrigger( MouseInput.AXIS_X, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_UP, new MouseAxisTrigger( MouseInput.AXIS_Y, false ) );
        inputManager.addMapping( MoleculeJMEApplication.MAP_DOWN, new MouseAxisTrigger( MouseInput.AXIS_Y, true ) );

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
                                              if ( name.equals( MoleculeJMEApplication.MAP_LEFT ) ) {
                                                  rotation = new Quaternion().fromAngles( 0, -value * MOUSE_SCALE, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeJMEApplication.MAP_RIGHT ) ) {
                                                  rotation = new Quaternion().fromAngles( 0, value * MOUSE_SCALE, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeJMEApplication.MAP_UP ) ) {
                                                  rotation = new Quaternion().fromAngles( -value * MOUSE_SCALE, 0, 0 ).mult( rotation );
                                              }
                                              if ( name.equals( MoleculeJMEApplication.MAP_DOWN ) ) {
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

    public synchronized void testRemoveAtom() {
        if ( !pairs.isEmpty() ) {
            removePair( pairs.get( random.nextInt( pairs.size() ) ) );
        }
    }

    @Override public synchronized void simpleUpdate( final float tpf ) {
//        System.out.println( "tpf = " + tpf );
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
        moveTowardGlobalState( tpf );
        rebuildBonds();
        molecule.setLocalRotation( rotation );
    }

    private void moveTowardGlobalState( float tpf ) {
        //Linear
        ArrayList<ElectronPair> bondedAtoms = getBondedAtoms();
        ArrayList<ElectronPair> lonePairs = getLonePairs();
        if ( bondedAtoms.size() == 2 && lonePairs.size() == 3 ) {
            ImmutableVector3D a = bondedAtoms.get( 0 ).position.get();
            ImmutableVector3D b = bondedAtoms.get( 1 ).position.get();

            //Should be 1 if at global state
            double value = a.normalized().dot( b.normalized() ) * -1;

            if ( value < 0.999 ) {
                bondedAtoms.get( 0 ).repulseFrom( bondedAtoms.get( 1 ), tpf );
                bondedAtoms.get( 1 ).repulseFrom( bondedAtoms.get( 0 ), tpf );
            }
            else {
                //Its already close enough to good global maximum
                numSuccess++;
                System.out.println( "number of success: " + numSuccess + ", elapsed time = \t" + ( System.currentTimeMillis() - startTime ) );
                setState( 2, 3 );

            }
        }
    }

    public ArrayList<ElectronPair> getBondedAtoms() {
        return getPairs( false );
    }

    public ArrayList<ElectronPair> getLonePairs() {
        return getPairs( true );
    }

    public ArrayList<ElectronPair> getPairs( final boolean lonePairs ) {
        return new ArrayList<ElectronPair>() {{
            for ( ElectronPair pair : pairs ) {
                if ( pair.isLonePair == lonePairs ) {
                    add( pair );
                }
            }
        }};
    }

    private void addPair( final ElectronPair pair ) {
        pairs.add( pair );
        AtomNode atomNode = new AtomNode( pair, assetManager );
        atomNodes.add( atomNode );
        molecule.attachChild( atomNode );
        rebuildBonds();
    }

    private void removePair( final ElectronPair pair ) {
        pairs.remove( pair );
        for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
            if ( atomNode.pair == pair ) {
                atomNodes.remove( atomNode );
                molecule.detachChild( atomNode );
            }
        }
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
        new MoleculeJMEApplication().start();
    }

    public void removeAllAtoms() {
        while ( !pairs.isEmpty() ) {
            testRemoveAtom();
        }
    }

    public synchronized void setState( final int X, final int E ) {
        startTime = System.currentTimeMillis();
        enqueue( new Callable<Object>() {
            public Object call() throws Exception {
                removeAllAtoms();
                for ( int i = 0; i < X; i++ ) {
                    testAddAtom( false );
                }
                for ( int i = 0; i < E; i++ ) {
                    testAddAtom( true );
                }
                return null;
            }
        } );
    }
}