package edu.colorado.phet.moleculeshapes.jme;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;
import edu.umd.cs.piccolox.pswing.PSwing;

import com.jme3.app.SimpleApplication;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.*;
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
import com.jme3.system.JmeCanvasContext;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;
import com.jme3.ui.Picture;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Use jme3 to show a rotating molecule
 */
public class MoleculeApplication extends SimpleApplication {

    //The molecule to display and rotate
    private Node molecule;

    //The angle about which the molecule should be rotated, changes as a function of time
    float angle = 0;
    private Picture hudPicture;

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

        JmeCanvasContext c = (JmeCanvasContext) getContext();
        Canvas canvas = c.getCanvas();
        canvas.addMouseListener( new MouseEventInputSource() );
        canvas.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                System.out.println( "hello" );
            }
        } );

        inputManager.addRawInputListener( new RawInputListener() {
            public void beginInput() {
            }

            public void endInput() {
            }

            public void onJoyAxisEvent( JoyAxisEvent evt ) {
            }

            public void onJoyButtonEvent( JoyButtonEvent evt ) {
            }

            public void onMouseMotionEvent( MouseMotionEvent evt ) {
                System.out.println( "evt = " + evt );
                sendInputEventToInputManager( new MouseEvent(  canvas, 0,System.currentTimeMillis(),), );
            }

            public void onMouseButtonEvent( MouseButtonEvent evt ) {
                System.out.println( "evt = " + evt );
            }

            public void onKeyEvent( KeyInputEvent evt ) {
            }

            public void onTouchEvent( TouchEvent evt ) {
            }
        } );
//
        setDisplayFps( false );
        setDisplayStatView( false );
//
//        //Add some bonds the user can drag into the play area
//        rootNode.attachChild( createCylinder( new Vector3f( 15, 0, 0 ), new Vector3f( 22, 0, 0 ) ) );
//        rootNode.attachChild( createCylinder( new Vector3f( 15, 1.5f, 0 ), new Vector3f( 22, 1.5f, 0 ) ) );
//
        guiNode.detachAllChildren();
        hudPicture = new Picture( "Hud Pic" );
        updateTexture( createImage() );
        hudPicture.setWidth( 100 );
        hudPicture.setHeight( 100 );
        guiNode.attachChild( hudPicture );
//
//        camera.setComponent( new PComponent() {
//            public void repaint( PBounds bounds ) {
//            }
//
//            public void paintImmediately() {
//            }
//
//            public void pushCursor( Cursor cursor ) {
//            }
//
//            public void popCursor() {
//            }
//
//            public void setInteracting( boolean interacting ) {
//            }
//        } );
//
        final VBox child = new VBox(
                new PSwing( new JButton( "jbutton" ) ),
                new PSwing( new JRadioButton( "radio button 1" ) ),
                new PText( "PText" ) {{
                    setTextPaint( Color.red );
                    setFont( new PhetFont( 32, true ) );
                }} );
        camera.getLayer( 0 ).addChild( child );
    }

    private void updateTexture( BufferedImage bufferedImage ) {
        hudPicture.setTexture( assetManager, new Texture2D( new AWTLoader().load( bufferedImage, true ) ), true );
    }

    private BufferedImage createImage() {
        BufferedImage bufferedImage = new BufferedImage( 100, 100, BufferedImage.TYPE_INT_ARGB_PRE );
        Graphics2D g2 = bufferedImage.createGraphics();
        camera.getLayer( 0 ).fullPaint( new PPaintContext( g2 ) );
        g2.dispose();
        return bufferedImage;
    }

    @Override public void simpleUpdate( final float tpf ) {
        angle += 2 * tpf;
        final Quaternion quaternion = new Quaternion();
        quaternion.fromAngles( 0, angle, 0 );
        molecule.setLocalRotation( quaternion );

        updateTexture( createImage() );
    }

    //Attach the two atoms together with a bond and add to the molecule
    private void attach( Geometry center, Geometry newSphere ) {
        molecule.attachChild( newSphere );
        molecule.attachChild( createCylinder( center.getLocalTranslation(), newSphere.getLocalTranslation() ) );
    }

    private LineCylinder createCylinder( Vector3f start, Vector3f end ) {
        final LineCylinder shiny_rock = new LineCylinder( start, end );
        Material mat_lit = new Material( assetManager, "Common/MatDefs/Light/Lighting.j3md" );
        shiny_rock.setMaterial( mat_lit );
        return shiny_rock;
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
        mat_lit.setTexture( "DiffuseMap", assetManager.loadTexture( "molecule-shapes/jme3/Textures/Terrain/Pond/Pond.png" ) );
        mat_lit.setTexture( "NormalMap", assetManager.loadTexture( "molecule-shapes/jme3/Textures/Terrain/Pond/Pond_normal.png" ) );
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

    //Main
    public static void main( String[] args ) throws IOException {
        //Launch the application
        new MoleculeApplication().start();
    }

    private PCamera camera = PUtil.createBasicScenegraph();

    protected void sendInputEventToInputManager( final InputEvent event, final int type ) {
        getRoot().getDefaultInputManager().processEventFromCamera( event, type, camera );
    }

    public PRoot getRoot() {
        return camera.getRoot();
    }

    private final class MouseEventInputSource implements MouseListener {
        private boolean isButton1Pressed;
        private boolean isButton2Pressed;
        private boolean isButton3Pressed;

        /**
         * {@inheritDoc}
         */
        public void mouseClicked( final MouseEvent e ) {
            sendInputEventToInputManager( e, MouseEvent.MOUSE_CLICKED );
        }

        /**
         * {@inheritDoc}
         */
        public void mouseEntered( final MouseEvent e ) {
            MouseEvent simulated = null;

            if ( isAnyButtonDown( e ) ) {
                simulated = buildRetypedMouseEvent( e, MouseEvent.MOUSE_DRAGGED );
            }
            else {
                simulated = buildRetypedMouseEvent( e, MouseEvent.MOUSE_MOVED );
            }

            sendInputEventToInputManager( e, MouseEvent.MOUSE_ENTERED );
            sendInputEventToInputManager( simulated, simulated.getID() );
        }

        private boolean isAnyButtonDown( final MouseEvent e ) {
            return ( e.getModifiersEx() & ALL_BUTTONS_MASK ) != 0;
        }

        private static final int ALL_BUTTONS_MASK = InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK
                                                    | InputEvent.BUTTON3_DOWN_MASK;

        /**
         * {@inheritDoc}
         */
        public void mouseExited( final MouseEvent e ) {
            MouseEvent simulated = null;

            if ( isAnyButtonDown( e ) ) {
                simulated = buildRetypedMouseEvent( e, MouseEvent.MOUSE_DRAGGED );
            }
            else {
                simulated = buildRetypedMouseEvent( e, MouseEvent.MOUSE_MOVED );
            }

            sendInputEventToInputManager( simulated, simulated.getID() );
            sendInputEventToInputManager( e, MouseEvent.MOUSE_EXITED );
        }

        /**
         * {@inheritDoc}
         */
        public void mousePressed( final MouseEvent rawEvent ) {
//            requestFocus();

            boolean shouldBalanceEvent = false;

            final MouseEvent event = copyButtonsFromModifiers( rawEvent, MouseEvent.MOUSE_PRESSED );

            switch( event.getButton() ) {
                case MouseEvent.BUTTON1:
                    if ( isButton1Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = true;
                    break;

                case MouseEvent.BUTTON2:
                    if ( isButton2Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = true;
                    break;

                case MouseEvent.BUTTON3:
                    if ( isButton3Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = true;
                    break;
                default:
                    throw new RuntimeException( "mousePressed without buttons specified" );

            }

            if ( shouldBalanceEvent ) {
                sendRetypedMouseEventToInputManager( event, MouseEvent.MOUSE_RELEASED );
            }

            sendInputEventToInputManager( event, MouseEvent.MOUSE_PRESSED );
        }

        /**
         * {@inheritDoc}
         */
        public void mouseReleased( final MouseEvent rawEvent ) {
            boolean shouldBalanceEvent = false;

            final MouseEvent event = copyButtonsFromModifiers( rawEvent, MouseEvent.MOUSE_RELEASED );

            switch( event.getButton() ) {
                case MouseEvent.BUTTON1:
                    if ( !isButton1Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton1Pressed = false;
                    break;

                case MouseEvent.BUTTON2:
                    if ( !isButton2Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton2Pressed = false;
                    break;

                case MouseEvent.BUTTON3:
                    if ( !isButton3Pressed ) {
                        shouldBalanceEvent = true;
                    }
                    isButton3Pressed = false;
                    break;
                default:
                    throw new RuntimeException( "mouseReleased without buttons specified" );
            }

            if ( shouldBalanceEvent ) {
                sendRetypedMouseEventToInputManager( event, MouseEvent.MOUSE_PRESSED );
            }

            sendInputEventToInputManager( event, MouseEvent.MOUSE_RELEASED );
        }

        private MouseEvent copyButtonsFromModifiers( final MouseEvent rawEvent, final int eventType ) {
            if ( rawEvent.getButton() != MouseEvent.NOBUTTON ) {
                return rawEvent;
            }

            int newButton = 0;

            if ( hasButtonModifier( rawEvent, InputEvent.BUTTON1_MASK ) ) {
                newButton = MouseEvent.BUTTON1;
            }
            else if ( hasButtonModifier( rawEvent, InputEvent.BUTTON2_MASK ) ) {
                newButton = MouseEvent.BUTTON2;
            }
            else if ( hasButtonModifier( rawEvent, InputEvent.BUTTON3_MASK ) ) {
                newButton = MouseEvent.BUTTON3;
            }

            return buildModifiedMouseEvent( rawEvent, eventType, newButton );
        }

        private boolean hasButtonModifier( final MouseEvent event, final int buttonMask ) {
            return ( event.getModifiers() & buttonMask ) == buttonMask;
        }

        public MouseEvent buildRetypedMouseEvent( final MouseEvent e, final int newType ) {
            return buildModifiedMouseEvent( e, newType, e.getButton() );
        }

        public MouseEvent buildModifiedMouseEvent( final MouseEvent e, final int newType, final int newButton ) {
            return new MouseEvent( (Component) e.getSource(), newType, e.getWhen(), e.getModifiers(), e.getX(),
                                   e.getY(), e.getClickCount(), e.isPopupTrigger(), newButton );
        }

        private void sendRetypedMouseEventToInputManager( final MouseEvent e, final int newType ) {
            final MouseEvent retypedEvent = buildRetypedMouseEvent( e, newType );
            sendInputEventToInputManager( retypedEvent, newType );
        }
    }
}