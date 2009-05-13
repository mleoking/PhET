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
import com.jme.intersection.PickData;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.*;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Torus;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;
import com.jmex.physics.DynamicPhysicsNode;
import com.jmex.physics.PhysicsSpace;
import com.jmex.physics.StaticPhysicsNode;
import com.jmex.physics.geometry.PhysicsMesh;
import com.jmex.physics.geometry.PhysicsSphere;
import com.jmex.physics.util.PhysicsPicker;
import jmetest.intersection.TestTrianglePick;
import jmetest.util.JMESwingTest;

import java.awt.*;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 1:10:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class DensityCanvasImpl extends SimpleCanvasImpl {

    private Quaternion rotQuat;
    private float angle = 0;
    private Vector3f axis;
    private com.jme.scene.shape.Box box;
    long startTime = 0;
    long fps = 0;
    private InputHandler input;
    private SRRMouse am;
    private DisplaySystem display;
    private Component canvas;

    private com.jme.scene.Point pointSelection;
    Spatial maggie;
    private com.jme.scene.Line[] selection;

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
        if (am != null)
            am.setLimit(newWidth, newHeight);
    }

    public void simpleSetup() {

        // Normal Scene setup stuff...
        rotQuat = new Quaternion();
        axis = new Vector3f(1, 1, 0.5f);
        axis.normalizeLocal();

        Vector3f max = new Vector3f(5, 5, 5);
        Vector3f min = new Vector3f(-5, -5, -5);

        box = new com.jme.scene.shape.Box("Box", min, max);
        box.setModelBound(new BoundingBox());
        box.updateModelBound();
        box.setLocalTranslation(new Vector3f(0, 0, -10));
        box.setRenderQueueMode(com.jme.renderer.Renderer.QUEUE_SKIP);
        rootNode.attachChild(box);

        box.setRandomColors();

        TextureState ts = renderer.createTextureState();
        ts.setEnabled(true);
        ts.setTexture(TextureManager.loadTexture(JMESwingTest.class
                .getClassLoader().getResource(
                "jmetest/data/images/Monkey.jpg"),
                Texture.MinificationFilter.BilinearNearestMipMap,
                Texture.MagnificationFilter.Bilinear));

        rootNode.setRenderState(ts);
        startTime = System.currentTimeMillis() + 5000;

        input = new InputHandler();

        // Create a new mouse. Restrict its movements to the display screen.
        am = new SRRMouse("The Mouse", display.getWidth(), display.getHeight());

        // Get a picture for my mouse.
        TextureState mouseTextureState = display.getRenderer().createTextureState();
        URL cursorLoc = TestTrianglePick.class.getClassLoader().getResource(
                "jmetest/data/cursor/cursor1.png");
        Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        mouseTextureState.setTexture(t);
        am.setRenderState(mouseTextureState);

        // Make the mouse's background blend with what's already there
        BlendState as = display.getRenderer().createBlendState();
        as.setBlendEnabled(true);
        as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        as.setTestEnabled(true);
        as.setTestFunction(BlendState.TestFunction.GreaterThan);
        am.setRenderState(as);

        // Move the mouse to the middle of the screen to start with
        am.setLocalTranslation(new Vector3f(display.getWidth() / 2, display
                .getHeight() / 2, 0));
        // Assign the mouse to an input handler
        am.registerWithInputHandler(input);

        rootNode.attachChild(am);
        results.setCheckDistance(true);

        pointSelection = new com.jme.scene.Point("selected triangle", new Vector3f[1], null,
                new ColorRGBA[1], null);
        pointSelection.setSolidColor(new ColorRGBA(1, 0, 0, 1));
        pointSelection.setPointSize(10);
        pointSelection.setAntialiased(true);
        ZBufferState zbs = display.getRenderer().createZBufferState();
        zbs.setFunction(ZBufferState.TestFunction.Always);
        pointSelection.setRenderState(zbs);
        pointSelection.setLightCombineMode(Spatial.LightCombineMode.Off);

        rootNode.attachChild(pointSelection);


        initPhysics();

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
//        PhysicsMesh mesh = staticNode.createMesh( "mesh" );
//        mesh.copyFrom( trimesh );
        staticNode.attachChild(trimesh);
        staticNode.generatePhysicsGeometry(false);

        staticNode.getLocalTranslation().set(0, -5, 0);
        rootNode.attachChild(staticNode);

        final DynamicPhysicsNode dynamicNode1 = getPhysicsSpace().createDynamicNode();
        TriMesh mesh1 = new Sphere("meshsphere", 10, 10, 2);
        mesh1.setModelBound(new BoundingSphere());
        mesh1.updateModelBound();
        PhysicsMesh sphere = dynamicNode1.createMesh("sphere mesh");
        sphere.getLocalTranslation().set(-1, 0, 0);
        mesh1.getLocalTranslation().set(-1, 0, 0);
        sphere.copyFrom(mesh1);
        dynamicNode1.attachChild(mesh1);
        final PhysicsSphere sphere2 = dynamicNode1.createSphere("sphere physics");
        sphere2.getLocalTranslation().set(0.3f, 0, 0);
        dynamicNode1.detachChild(sphere2);
        rootNode.attachChild(dynamicNode1);
        dynamicNode1.computeMass();

        final DynamicPhysicsNode dynamicNode2 = getPhysicsSpace().createDynamicNode();
        TriMesh mesh2 = new Torus("torus", 15, 10, 1, 4);
        mesh2.setModelBound(new BoundingSphere());
        mesh2.updateModelBound();
        PhysicsMesh physicsMesh2 = dynamicNode2.createMesh("torus phyics geometry");
        physicsMesh2.copyFrom(mesh2);
        dynamicNode2.attachChild(mesh2);
        CullState cs = display.getRenderer().createCullState();
        cs.setCullFace(CullState.Face.Back);
        mesh2.setRenderState(cs);

        rootNode.attachChild(dynamicNode2);
        dynamicNode2.computeMass();

        final InputAction resetAction = new InputAction() {
            public void performAction(InputActionEvent evt) {
                if (evt == null || evt.getTriggerPressed()) {
                    dynamicNode1.getLocalTranslation().set(0, 3, 0);
                    dynamicNode1.getLocalRotation().set(0, 0, 0, 1);
                    dynamicNode1.clearDynamics();

                    dynamicNode2.getLocalTranslation().set(0, 5f, 0);
                    dynamicNode2.getLocalRotation().fromAngleNormalAxis(FastMath.PI / 2 - 0.2f, new Vector3f(1, 0, 0));
                    dynamicNode2.clearDynamics();
                }
            }
        };
        input.addAction(resetAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_R, InputHandler.AXIS_NONE, false);
        resetAction.performAction(null);

        InputAction detachAction = new InputAction() {
            public void performAction(InputActionEvent evt) {
                if (sphere2.getParent() != null) {
                    dynamicNode1.detachChild(sphere2);
                } else {
                    dynamicNode1.attachChild(sphere2);
                }
            }
        };
        input.addAction(detachAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_INSERT, InputHandler.AXIS_NONE, false);

        InputAction removeAction = new InputAction() {
            public void performAction(InputActionEvent evt) {
                staticNode.setActive(!staticNode.isActive());
            }
        };
        input.addAction(removeAction, InputHandler.DEVICE_KEYBOARD, KeyInput.KEY_DELETE, InputHandler.AXIS_NONE, false);

        cameraInputHandler.setEnabled(false);
        new PhysicsPicker(input, rootNode, getPhysicsSpace(), false, this);
        MouseInput.get().setCursorVisible(true);

        Text label = Text.createDefaultTextLabel("instructions", "[r] to reset. Hold [ins] to attach second sphere.");
        label.setLocalTranslation(0, 20, 0);
//        statNode.attachChild( label );
    }

    private void createSelectionTriangles(int number) {
        clearPreviousSelections();
        selection = new com.jme.scene.Line[number];
        for (int i = 0; i < selection.length; i++) {
            selection[i] = new com.jme.scene.Line("selected triangle" + i, new Vector3f[4],
                    null, new ColorRGBA[4], null);
            selection[i].setSolidColor(new ColorRGBA(0, 1, 0, 1));
            selection[i].setLineWidth(5);
            selection[i].setAntialiased(true);
            selection[i].setMode(com.jme.scene.Line.Mode.Connected);

            ZBufferState zbs = display.getRenderer().createZBufferState();
            zbs.setFunction(ZBufferState.TestFunction.Always);
            selection[i].setRenderState(zbs);
            selection[i].setLightCombineMode(Spatial.LightCombineMode.Off);

            rootNode.attachChild(selection[i]);
        }

        rootNode.updateGeometricState(0, true);
        rootNode.updateRenderState();
    }

    private void clearPreviousSelections() {
        if (selection != null) {
            for (com.jme.scene.Line line : selection) {
                rootNode.detachChild(line);
            }
        }
    }


    TrianglePickResults results = new TrianglePickResults() {

        public void processPick() {

            // initialize selection triangles, this can go across multiple
            // target
            // meshes.
            int total = 0;
            for (int i = 0; i < getNumber(); i++) {
                total += getPickData(i).getTargetTris().size();
            }
            createSelectionTriangles(total);
            if (getNumber() > 0) {
                int previous = 0;
                for (int num = 0; num < getNumber(); num++) {
                    PickData pData = getPickData(num);
                    java.util.List<Integer> tris = pData.getTargetTris();
                    TriMesh mesh = (TriMesh) pData.getTargetMesh();

                    for (int i = 0; i < tris.size(); i++) {
                        int triIndex = tris.get(i);
                        Vector3f[] vec = new Vector3f[3];
                        mesh.getTriangle(triIndex, vec);
                        FloatBuffer buff = selection[i + previous]
                                .getVertexBuffer();

                        for (Vector3f v : vec) {
                            v.multLocal(mesh.getWorldScale());
                            mesh.getWorldRotation().mult(v, v);
                            v.addLocal(mesh.getWorldTranslation());
                        }

                        BufferUtils.setInBuffer(vec[0], buff, 0);
                        BufferUtils.setInBuffer(vec[1], buff, 1);
                        BufferUtils.setInBuffer(vec[2], buff, 2);
                        BufferUtils.setInBuffer(vec[0], buff, 3);

                        if (num == 0 && i == 0) {
                            selection[i + previous]
                                    .setSolidColor(new ColorRGBA(1, 0, 0, 1));
                            Vector3f loc = new Vector3f();
                            pData.getRay().intersectWhere(vec[0], vec[1],
                                    vec[2], loc);
                            BufferUtils.setInBuffer(loc, pointSelection
                                    .getVertexBuffer(), 0);
                        }
                    }

                    previous = tris.size();
                }
            }
        }
    };

    public void simpleUpdate() {
        input.update(tpf);
        if (am != null)
            am.setLimit(canvas.getWidth(), canvas.getHeight());

        // Code for rotating the box... no surprises here.
        if (tpf < 1) {
            angle = angle + (tpf * 25);
            if (angle > 360) {
                angle = 0;
            }
        }
        rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis);
        box.setLocalRotation(rotQuat);

        if (startTime > System.currentTimeMillis()) {
            fps++;
        } else {
            long timeUsed = 5000 + (startTime - System.currentTimeMillis());
            startTime = System.currentTimeMillis() + 5000;
            fps = 0;
        }


        final Ray mouseRay = getMouseRay();

        // Is button 0 down? Button 0 is left click
        if (MouseInput.get().isButtonDown(0)) {
            results.clear();
            box.calculatePick(mouseRay, results);
        }
//        System.out.println("box=" + box.getLocalTranslation() + ", am=" + am.getHotSpotPosition() + ", am.world=" + worldCoords);

        mouseRay.setDirection(mouseRay.getDirection().mult(10.0f));//so z=-10
        Vector3f dst = mouseRay.getOrigin().add(mouseRay.getDirection());
        Vector3f newV = new Vector3f(dst.x, dst.y, -10);
        box.setLocalTranslation(newV);


        updatePhysics();
    }

    public Ray getMouseRay() {
        Vector2f screenPos = new Vector2f();
        // Get the position that the mouse is pointing to
        screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
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

//        input.update(tpf);

        if (!pause) {
            /** Call simpleUpdate in any derived classes of SimpleGame. */
//            simpleUpdate();

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
        screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
        return screenPos;
    }
}
