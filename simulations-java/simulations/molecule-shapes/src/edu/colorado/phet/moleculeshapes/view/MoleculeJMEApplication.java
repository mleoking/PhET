package edu.colorado.phet.moleculeshapes.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.model.ElectronPair;
import edu.colorado.phet.moleculeshapes.model.ImmutableVector3D;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel.Adapter;

import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
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

    private List<AtomNode> atomNodes = new ArrayList<AtomNode>();
    private List<LonePairNode> lonePairNodes = new ArrayList<LonePairNode>();
    private List<BondNode> bondNodes = new ArrayList<BondNode>();
    private boolean dragging = false;

    private MoleculeModel molecule = new MoleculeModel();

    private Node moleculeNode; //The molecule to display and rotate

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

        moleculeNode = new Node();
        rootNode.attachChild( moleculeNode );

        // update the UI when the molecule changes electron pairs
        molecule.addListener( new Adapter() {
            @Override public void onPairAdded( ElectronPair pair ) {
                if ( pair.isLonePair ) {
                    LonePairNode lonePairNode = new LonePairNode( pair.position, assetManager );
                    lonePairNodes.add( lonePairNode );
                    moleculeNode.attachChild( lonePairNode );
                }
                else {
                    AtomNode atomNode = new AtomNode( pair.position, pair.isLonePair ? MoleculeShapesConstants.COLOR_LONE_PAIR : MoleculeShapesConstants.COLOR_ATOM, assetManager );
                    atomNodes.add( atomNode );
                    moleculeNode.attachChild( atomNode );
                    rebuildBonds();
                }
            }

            @Override public void onPairRemoved( ElectronPair pair ) {
                if ( pair.isLonePair ) {
                    for ( LonePairNode lonePairNode : new ArrayList<LonePairNode>( lonePairNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( lonePairNode.position == pair.position ) {
                            lonePairNodes.remove( lonePairNode );
                            moleculeNode.detachChild( lonePairNode );
                        }
                    }
                }
                else {
                    for ( AtomNode atomNode : new ArrayList<AtomNode>( atomNodes ) ) {
                        // TODO: associate these more closely! (comparing positions for equality is bad)
                        if ( atomNode.position == pair.position ) {
                            atomNodes.remove( atomNode );
                            moleculeNode.detachChild( atomNode );
                        }
                    }
                }
            }
        } );

        /*---------------------------------------------------------------------------*
        * atoms
        *----------------------------------------------------------------------------*/

        //Create the central atom
        AtomNode center = new AtomNode( new Property<ImmutableVector3D>( new ImmutableVector3D() ), MoleculeShapesConstants.COLOR_ATOM_CENTER, assetManager );
        moleculeNode.attachChild( center );

        //Create the atoms that circle about the central atom
        double angle = Math.PI * 2 / 5;
        for ( double theta = 0; theta < Math.PI * 2; theta += angle ) {
            double x = 10 * Math.cos( theta );
            double y = 10 * Math.sin( theta );
            molecule.addPair( new ElectronPair( new ImmutableVector3D( x, y, 0 ), false ) );
        }

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

        guiNode.attachChild( new HUDNode( assetManager, inputManager ) );
    }

    public void testAddAtom( boolean isLonePair ) {
        ImmutableVector3D initialPosition = new ImmutableVector3D( Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5 ).normalized().times( 7 );
        molecule.addPair( new ElectronPair( initialPosition, isLonePair ) );
    }

    public synchronized void testRemoveAtom() {
        if ( !molecule.getPairs().isEmpty() ) {
            molecule.removePair( molecule.getPairs().get( random.nextInt( molecule.getPairs().size() ) ) );
        }
    }

    private int counter = 0;

    @Override public synchronized void simpleUpdate( final float tpf ) {
        molecule.update( tpf );
        rebuildBonds();
        moleculeNode.setLocalRotation( rotation );
    }

    private void rebuildBonds() {
        // necessary for now since just updating their geometry shows significant errors
        for ( BondNode bondNode : bondNodes ) {
            moleculeNode.detachChild( bondNode );
        }
        bondNodes.clear();
        for ( ElectronPair pair : molecule.getPairs() ) {
            if ( !pair.isLonePair ) {
                BondNode bondNode = new BondNode( new ImmutableVector3D(), pair.position.get(), assetManager );
                moleculeNode.attachChild( bondNode );
                bondNodes.add( bondNode );
            }
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
        while ( !molecule.getPairs().isEmpty() ) {
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