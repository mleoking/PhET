package edu.colorado.phet.densityjava;

import com.jme.input.KeyInput;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 12, 2009
 * Time: 1:15:18 PM
 * To change this template use File | Settings | File Templates.
 */
class DensityJMECanvas extends JPanel {
    int width = 640, height = 480;
    JPanel mainPanel = new JPanel();
    JMECanvas canvas = null;
    DensityCanvasImpl impl;

    // Construct the frame
    public DensityJMECanvas() {
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

        add(mainPanel, BorderLayout.WEST);
        ((Component) canvas).setBounds(0, 0, width, height);
        add((Component) canvas, BorderLayout.CENTER);

        doResize();
    }

    protected void doResize() {
        impl.resizeCanvas(((Component) canvas).getWidth(), ((Component) canvas).getHeight());
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
