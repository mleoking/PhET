package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.TextureManager;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;

import java.awt.*;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 1:10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DensityCanvasImpl extends SimpleCanvasImpl {

//    BoxState boxState = null;

    long startTime = 0;
    long fps = 0;
    private InputHandler input;
    private SRRMouse srrMouse;
    private DisplaySystem display;
    private Component canvas;

    //Physics
    private PhysicsSpace physicsSpace;
    protected InputHandler cameraInputHandler;
    protected boolean showPhysics;
    private float physicsSpeed = 1f;
    protected StaticPhysicsNode staticNode;
    boolean firstFrame = true;

    public PhysicsSpace getPhysicsSpace() {
        return physicsSpace;
    }

    public DensityCanvasImpl(int width, int height, DisplaySystem display, Component canvas) {
        super(width, height);
        this.display = display;
        this.canvas = canvas;
    }

    @Override
    public void resizeCanvas(int newWidth, int newHeight) {
        super.resizeCanvas(newWidth, newHeight);    //To change body of overridden methods use File | Settings | File Templates.
        if (srrMouse != null)
            srrMouse.setLimit(newWidth, newHeight);
    }

    public void simpleSetup() {
        TextureState ts = renderer.createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(DensityCanvasImpl.class
                .getClassLoader().getResource(
                "phetcommon/images/logos/phet-logo-120x50.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));

        rootNode.setRenderState(ts);
        startTime = System.currentTimeMillis() + 5000;

        input = new InputHandler();

        // Create a new mouse. Restrict its movements to the display screen.
        srrMouse = createMouse();
        rootNode.attachChild(srrMouse);

        initPhysics();
    }

    private SRRMouse createMouse() {
        SRRMouse m = new SRRMouse("The Mouse", display.getWidth(), display.getHeight());

        // Get a picture for my mouse.
        TextureState mouseTextureState = display.getRenderer().createTextureState();
        URL cursorLoc = DensityCanvasImpl.class.getClassLoader().getResource(
                "density/images/cursor1.png");
        Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        mouseTextureState.setTexture(t);
        m.setRenderState(mouseTextureState);

        // Make the mouse's background blend with what's already there
        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        m.setRenderState(as);

        // Move the mouse to the middle of the screen to start with
        m.setLocalTranslation(new Vector3f(display.getWidth() / 2, display
                .getHeight() / 2, 0));
        // Assign the mouse to an input handler
        m.registerWithInputHandler(input);
        return m;
    }

    private void initPhysics() {
        /** Create a basic input controller. */
        cameraInputHandler = new FirstPersonHandler(cam, 50, 1);
//        input = new InputHandler();
        input.addToAttachedHandlers(cameraInputHandler);

        physicsSpace = PhysicsSpace.create();

//        input.addAction(new InputAction() {
//            public void performAction(InputActionEvent evt) {
//                if (evt.getTriggerPressed()) {
//                    showPhysics = !showPhysics;
//                }
//            }
//        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_V, InputHandler.AXIS_NONE, false);


        staticNode = getPhysicsSpace().createStaticNode();
        TriMesh trimesh = new Box("trimesh", new Vector3f(), 15, 0.5f, 15);
        trimesh.setModelBound(new BoundingBox());
        trimesh.updateModelBound();
        staticNode.attachChild(trimesh);
        staticNode.generatePhysicsGeometry(false);

        staticNode.getLocalTranslation().set(0, -5, 0);
        rootNode.attachChild(staticNode);

        final DynamicPhysicsNode sphere = createSphere();
        rootNode.attachChild(sphere);

        final DynamicPhysicsNode torus = createTorus();
        rootNode.attachChild(torus);

        final DynamicPhysicsNode box = createBox();
        rootNode.attachChild(box);

        input.addAction(new InputAction() {
            public void performAction(InputActionEvent inputActionEvent) {
                if (inputActionEvent.getTriggerPressed()) {
                    final DynamicPhysicsNode box = createBox();
                    rootNode.attachChild(box);
                }
            }
        }, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_SPACE, InputHandler.AXIS_NONE, false);

        final InputAction resetAction = new InputAction() {
            public void performAction(InputActionEvent evt) {
                if (evt == null || evt.getTriggerPressed()) {
                    System.out.println("DensityCanvasImpl.performAction");
                    sphere.getLocalTranslation().set(0, 3, 0);
                    sphere.getLocalRotation().set(0, 0, 0, 1);
                    sphere.clearDynamics();

                    torus.getLocalTranslation().set(0, 5f, 0);
                    torus.getLocalRotation().fromAngleNormalAxis(FastMath.PI / 2 - 0.2f, new Vector3f(1, 0, 0));
                    torus.clearDynamics();
                }
            }
        };
        input.addAction(resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false);
        resetAction.performAction(null);

        InputAction removeAction = new InputAction() {
            public void performAction(InputActionEvent evt) {
                staticNode.setActive(!staticNode.isActive());
            }
        };
        input.addAction(removeAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false);

        cameraInputHandler.setEnabled(false);
        new SRRPhysicsPicker(input, rootNode, getPhysicsSpace(), false, this);
        MouseInput.get().setCursorVisible(true);

        Text label = Text.createDefaultTextLabel("instructions", "[r] to reset. Hold [ins] to attach second sphere.");
        label.setLocalTranslation(0, 20, 0);
//        statNode.attachChild( label );
    }

    private DynamicPhysicsNode createBox() {
        DynamicPhysicsNode node = getPhysicsSpace().createDynamicNode();
        TriMesh mesh = new Box("meshsphere", new Vector3f(0, 0, 0), 2, 2, 2);
        mesh.setModelBound(new BoundingSphere());
        mesh.updateModelBound();
        PhysicsMesh physMesh = node.createMesh("box mesh");
        physMesh.getLocalTranslation().set(-1, 0, 0);
        mesh.getLocalTranslation().set(-1, 0, 0);
        physMesh.copyFrom(mesh);
        node.attachChild(mesh);
        node.computeMass();
        return node;
    }

    private DynamicPhysicsNode createSphere() {
        DynamicPhysicsNode node = getPhysicsSpace().createDynamicNode();
        TriMesh mesh = new Sphere("meshsphere", 10, 10, 2);
        mesh.setModelBound(new BoundingSphere());
        mesh.updateModelBound();
        PhysicsMesh physMesh = node.createMesh("sphere mesh");
        physMesh.getLocalTranslation().set(-1, 0, 0);
        mesh.getLocalTranslation().set(-1, 0, 0);
        physMesh.copyFrom(mesh);
        node.attachChild(mesh);
        node.computeMass();
        return node;
    }

    private DynamicPhysicsNode createTorus() {
        final DynamicPhysicsNode node = getPhysicsSpace().createDynamicNode();
        TriMesh mesh = new Torus("torus", 15, 10, 1, 4);
        mesh.setModelBound(new BoundingSphere());
        mesh.updateModelBound();
        PhysicsMesh physMesh = node.createMesh("torus phyics geometry");
        physMesh.copyFrom(mesh);
        node.attachChild(mesh);
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        mesh.setRenderState(cs);
        node.computeMass();
        return node;
    }


    public void simpleUpdate() {
        input.update(tpf);
        if (srrMouse != null)
            srrMouse.setLimit(canvas.getWidth(), canvas.getHeight());
        updatePhysics();
    }

    public Ray getMouseRay() {
        Vector2f screenPos = new Vector2f();
        // Get the position that the mouse is pointing to
        screenPos.set(srrMouse.getHotSpotPosition().x, srrMouse.getHotSpotPosition().y);
        // Get the world location of that X,Y value
        Vector3f worldCoords = display.getWorldCoordinates(screenPos, 1.0f);
        // Create a ray starting from the camera, and going in the direction
        // of the mouse's location
        final Ray mouseRay = new Ray(cam.getLocation(), worldCoords.subtractLocal(cam.getLocation()));
        mouseRay.getDirection().normalizeLocal();
        return mouseRay;
    }

    private void updatePhysics() {
        boolean pause = false;
        if (!pause) {
            float tpf = this.tpf;
            if (tpf > 0.2 || Float.isNaN(tpf)) {
                Logger.getLogger(PhysicsSpace.LOGGER_NAME).warning("Maximum physics update interval is 0.2 seconds - capped.");
                tpf = 0.2f;
            }
            physicsSpace.update(tpf * physicsSpeed);
        }

        if (!pause) {
            /** Update controllers/render states/transforms/bounds for rootNode. */
            rootNode.updateGeometricState(tpf, true);
//            statNode.updateGeometricState(tpf, true);
        }

        if (firstFrame) {
            // drawing and calculating the first frame usually takes longer than the rest
            // to avoid a rushing simulation we reset the timer
            timer.reset();
            firstFrame = false;
        }
    }

    public Vector2f getMouseScreenPosition() {
        Vector2f screenPos = new Vector2f();
        // Get the position that the mouse is pointing to
        screenPos.set(srrMouse.getHotSpotPosition().x, srrMouse.getHotSpotPosition().y);
        return screenPos;
    }
}
