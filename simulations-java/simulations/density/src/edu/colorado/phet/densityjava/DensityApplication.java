package edu.colorado.phet.densityjava;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.input.KeyInput;
import com.jme.input.action.InputAction;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.canvas.JMECanvasImplementor;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import com.jmex.awt.lwjgl.LWJGLCanvas;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import jmetest.util.JMESwingTest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;
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

    class SwingFrame extends JFrame {
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
        JMECanvasImplementor impl;

        // Construct the frame
        public SwingFrame() {
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    dispose();
                }
            });

            init();
            pack();
        }

        // Component initialization
        private void init() {
            contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());

            mainPanel.setLayout(new GridBagLayout());

            setTitle("JME - SWING INTEGRATION TEST");

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
            impl = new MyImplementor(width, height);
            canvas.setImplementor(impl);

            // -----------END OF GL STUFF-------------

            coolButton.setText("Cool Button");
            uncoolButton.setText("Uncool Button");

            colorPanel.setBackground(java.awt.Color.black);
            colorPanel.setToolTipText("Click here to change Panel BG color.");
            colorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
            colorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    final java.awt.Color color = JColorChooser.showDialog(
                            SwingFrame.this, "Choose new background color:",
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
            super.processWindowEvent(e);
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

        public MyImplementor(int width, int height) {
            super(width, height);
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

        public void simpleUpdate() {
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
        }
    }


}
