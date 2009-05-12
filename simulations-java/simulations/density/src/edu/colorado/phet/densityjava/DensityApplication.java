package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
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
import com.jme.scene.TriMesh;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import com.jmex.awt.lwjgl.LWJGLCanvas;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.ObjToJme;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import jmetest.intersection.TestTrianglePick;
import jmetest.util.JMESwingTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DensityApplication extends PiccoloPhetApplication {
    private static final Logger logger = Logger.getLogger(JMESwingTest.class
            .getName());

    int width = 640, height = 480;

    public DensityApplication(PhetApplicationConfig config) {
        super(config);
        addModule(new DensityModule());
    }

    class DensityModule extends Module {

        public DensityModule() {
            super("density", new ConstantDtClock(30, 30 / 1000.0));
            setSimulationPanel(new SwingFrame().contentPane);
        }

    }

    public static void main(String[] args) {
        new PhetApplicationLauncher().launchSim(args, "density", DensityApplication.class);
    }

    class SwingFrame {
        private static final long serialVersionUID = 1L;

        JPanel contentPane;
        JPanel mainPanel = new JPanel();
        LWJGLCanvas canvas = null;
        JButton coolButton = new JButton();
        JButton uncoolButton = new JButton();
        JPanel spPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane();
        JTree jTree1 = new JTree();
        JCheckBox scaleBox = new JCheckBox("Scale GL Image");
        JPanel colorPanel = new JPanel();
        JLabel colorLabel = new JLabel("BG Color:");
        MyImplementor impl;
        private AbsoluteMouse am;

        // Construct the frame
        public SwingFrame() {
            init();
        }

        // Component initialization
        private void init() {

            contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());

            mainPanel.setLayout(new GridBagLayout());

            // -------------GL STUFF------------------

            // make the canvas:
            DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
            display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
            canvas = (LWJGLCanvas) display.createCanvas(width, height);
            canvas.setUpdateInput(true);
            canvas.setTargetRate(60);

            // add a listener... if window is resized, we can do something about
            // it.
            canvas.addComponentListener(new ComponentAdapter() {
                public void componentResized(ComponentEvent ce) {
                    doResize();
                }
            });

            // Setup key and mouse input
            KeyInput.setProvider(KeyInput.INPUT_AWT);
            KeyListener kl = (KeyListener) KeyInput.get();
            canvas.addKeyListener(kl);
            AWTMouseInput.setup(canvas, false);

            // Important! Here is where we add the guts to the panel:
            impl = new MyImplementor(width, height, display);
            canvas.setImplementor(impl);

            // -----------END OF GL STUFF-------------

            coolButton.setText("Cool Button");
            uncoolButton.setText("Uncool Button");

            colorPanel.setBackground(java.awt.Color.black);
            colorPanel.setToolTipText("Click here to change Panel BG color.");
            colorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            colorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    final java.awt.Color color = JColorChooser.showDialog(null, "Choose new background color:",
                            colorPanel.getBackground());
                    if (color == null)
                        return;
                    colorPanel.setBackground(color);
                    Callable<?> call = new Callable<Object>() {
                        public Object call() throws Exception {
                            canvas.setBackground(color);
                            return null;
                        }
                    };
                    GameTaskQueueManager.getManager().render(call);
                }
            });

            scaleBox.setOpaque(false);
            scaleBox.setSelected(true);
            scaleBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (canvas != null)
                        doResize();
                }
            });

            spPanel.setLayout(new BorderLayout());
            contentPane.add(mainPanel, BorderLayout.WEST);
            mainPanel.add(scaleBox,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(colorLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(colorPanel, new GridBagConstraints(0, 2, 1, 1, 0.0,
                    0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(5, 5, 0, 5), 25, 25));
            mainPanel.add(coolButton,
                    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(uncoolButton,
                    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 0,
                                    5), 0, 0));
            mainPanel.add(spPanel, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(5, 5, 0, 5), 0, 0));
            spPanel.add(scrollPane, BorderLayout.CENTER);

            scrollPane.setViewportView(jTree1);
            canvas.setBounds(0, 0, width, height);
            contentPane.add(canvas, BorderLayout.CENTER);
        }

        protected void doResize() {
            if (scaleBox != null && scaleBox.isSelected()) {
                impl.resizeCanvas(canvas.getWidth(), canvas.getHeight());
            } else {
                impl.resizeCanvas(width, height);
            }
            ((JMECanvas) canvas).makeDirty();
        }

        // Overridden so we can exit when window is closed
        protected void processWindowEvent(WindowEvent e) {
            if (e.getID() == WindowEvent.WINDOW_CLOSING) {
                System.exit(0);
            }
        }
    }

    class MyImplementor extends SimpleCanvasImpl {

        private Quaternion rotQuat;
        private float angle = 0;
        private Vector3f axis;
        private com.jme.scene.shape.Box box;
        long startTime = 0;
        long fps = 0;
        private InputHandler input;
        private AbsoluteMouse am;
        private DisplaySystem display;

        public MyImplementor(int width, int height, DisplaySystem display) {
            super(width, height);
            this.display = display;
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
            input.addAction(new InputAction() {
                public void performAction(InputActionEvent evt) {
                    logger.info(evt.getTriggerName());
                }
            }, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL,
                    InputHandler.AXIS_NONE, false);

            input.addAction(new InputAction() {
                public void performAction(InputActionEvent evt) {
                    logger.info(evt.getTriggerName());
                }
            }, InputHandler.DEVICE_KEYBOARD, InputHandler.BUTTON_ALL,
                    InputHandler.AXIS_NONE, false);
        }

        boolean inited = false;

        private com.jme.scene.Point pointSelection;

        Spatial maggie;

        private com.jme.scene.Line[] selection;


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
            if (!inited) {
                inited = true;

                // Create a new mouse. Restrict its movements to the display screen.
                am = new AbsoluteMouse("The Mouse", display.getWidth(), display
                        .getHeight());

                // Get a picture for my mouse.
                TextureState ts = display.getRenderer().createTextureState();
                URL cursorLoc = TestTrianglePick.class.getClassLoader().getResource(
                        "jmetest/data/cursor/cursor1.png");
                Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
                        Texture.MagnificationFilter.Bilinear);
                ts.setTexture(t);
                am.setRenderState(ts);

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


                // Create the box in the middle. Give it a bounds
                URL model = TestTrianglePick.class.getClassLoader().getResource(
                        "jmetest/data/model/maggie.obj");
                try {
                    FormatConverter converter = new ObjToJme();
                    converter.setProperty("mtllib", model);
                    ByteArrayOutputStream BO = new ByteArrayOutputStream();
                    converter.convert(model.openStream(), BO);
                    maggie = (Spatial) BinaryImporter.getInstance().load(
                            new ByteArrayInputStream(BO.toByteArray()));
                    // scale rotate and translate to confirm that world transforms are
                    // handled
                    // correctly.
                    maggie.setLocalScale(.1f);
                    maggie.setLocalTranslation(new Vector3f(3, 1, -5));
                    Quaternion q = new Quaternion();
                    q.fromAngleAxis(0.5f, new Vector3f(0, 1, 0));
                    maggie.setLocalRotation(q);
                } catch (IOException e) { // Just in case anything happens
                    logger.logp(Level.SEVERE, this.getClass().toString(),
                            "simpleInitGame()", "Exception", e);
                    System.exit(0);
                }

                maggie.setModelBound(new BoundingSphere());
                maggie.updateModelBound();
                // Attach Children
                rootNode.attachChild(maggie);

                maggie.lockBounds();
                maggie.lockTransforms();
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
            }


            input.update(tpf);

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
                logger.info(fps + " frames in " + (timeUsed / 1000f)
                        + " seconds = " + (fps / (timeUsed / 1000f))
                        + " FPS (average)");
                fps = 0;
            }


            // Is button 0 down? Button 0 is left click
            if (MouseInput.get().isButtonDown(0)) {
                Vector2f screenPos = new Vector2f();
                // Get the position that the mouse is pointing to
                screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
                // Get the world location of that X,Y value
                Vector3f worldCoords = display.getWorldCoordinates(screenPos, 1.0f);
                // Create a ray starting from the camera, and going in the direction
                // of the mouse's location
                final Ray mouseRay = new Ray(cam.getLocation(), worldCoords
                        .subtractLocal(cam.getLocation()));
                mouseRay.getDirection().normalizeLocal();
                results.clear();

//                maggie.calculatePick(mouseRay, results);
                box.calculatePick(mouseRay, results);

            }

        }
    }


}
