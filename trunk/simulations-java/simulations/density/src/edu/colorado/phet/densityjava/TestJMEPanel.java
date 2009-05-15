package edu.colorado.phet.densityjava;

import com.jme.input.KeyInput;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jme.util.GameTaskQueueManager;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.Callable;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 1:15:18 PM
 * To change this template use File | Settings | File Templates.
 */
class TestJMEPanel extends JPanel {
    int width = 640, height = 480;
    JPanel mainPanel = new JPanel();
    JMECanvas canvas = null;
    JButton coolButton = new JButton();
    JButton uncoolButton = new JButton();
    JPanel spPanel = new JPanel();
    JScrollPane scrollPane = new JScrollPane();
    JTree jTree1 = new JTree();
    JCheckBox scaleBox = new JCheckBox("Scale GL Image");
    JPanel colorPanel = new JPanel();
    JLabel colorLabel = new JLabel("BG Color:");
    DensityCanvasImpl impl;

    // Construct the frame
    public TestJMEPanel() {
        init();
    }

    // Component initialization
    private void init() {

        setLayout(new BorderLayout());

        mainPanel.setLayout(new GridBagLayout());

        // -------------GL STUFF------------------

        // make the canvas:
        DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
        canvas = display.createCanvas(width, height);
        canvas.setUpdateInput(true);
        canvas.setTargetRate(60);

        // add a listener... if window is resized, we can do something about
        // it.
        ((Component) canvas).addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ce) {
                doResize();
            }
        });

        // Setup key and mouse input
        KeyInput.setProvider(KeyInput.INPUT_AWT);
        KeyListener kl = (KeyListener) KeyInput.get();
        ((Component) canvas).addKeyListener(kl);
        AWTMouseInput.setup((Canvas) canvas, false);

        // Important! Here is where we add the guts to the panel:
        impl = new DensityCanvasImpl(width, height, display, (Component) canvas);
        canvas.setImplementor(impl);

        // -----------END OF GL STUFF-------------

        coolButton.setText("Cool Button");
        uncoolButton.setText("Uncool Button");

        colorPanel.setBackground(Color.black);
        colorPanel.setToolTipText("Click here to change Panel BG color.");
        colorPanel.setBorder(BorderFactory.createRaisedBevelBorder());
        colorPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                final Color color = JColorChooser.showDialog(null, "Choose new background color:",
                        colorPanel.getBackground());
                if (color == null)
                    return;
                colorPanel.setBackground(color);
                Callable<?> call = new Callable<Object>() {
                    public Object call() throws Exception {
                        ((Component) canvas).setBackground(color);
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
        add(mainPanel, BorderLayout.WEST);
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
        ((Component) canvas).setBounds(0, 0, width, height);
        add((Component) canvas, BorderLayout.CENTER);

        doResize();
    }

    protected void doResize() {
        if (scaleBox != null && scaleBox.isSelected()) {
            impl.resizeCanvas(((Component) canvas).getWidth(), ((Component) canvas).getHeight());
        } else {
            impl.resizeCanvas(width, height);
        }
        canvas.makeDirty();
    }

    // Overridden so we can exit when window is closed
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    public Component getCanvas() {
        return (Component) canvas;
    }
}
