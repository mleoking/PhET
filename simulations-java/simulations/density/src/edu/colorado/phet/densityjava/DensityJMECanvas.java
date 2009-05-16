package edu.colorado.phet.densityjava;

import com.jme.input.KeyInput;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.MassVolumeModel;
import edu.colorado.phet.densityjava.view.MassVolumeChooser;

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
    private JDialog dialog;
    private JDialog d2;

    // Construct the frame
    public DensityJMECanvas(JFrame parent, DensityModel model) {
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
        impl = new DensityCanvasImpl(width, height, display, (Component) canvas, model);
        canvas.setImplementor(impl);

        // -----------END OF GL STUFF-------------

        add(mainPanel, BorderLayout.WEST);
        ((Component) canvas).setBounds(0, 0, width, height);
        add((Component) canvas, BorderLayout.CENTER);

        doResize();

        /*ideas for putting controls is play area:
        1. Use Glass pane, or post-rendering on the canvas
        2. Embed in world using a JMEDesktop or equivalent
        3. Overlay with a separate (invisible) dialog.
        4. JMECanvas subclasses PhetPCanvas?
        5. add swing components directly to the JMECanvas, and do proper light + heavy mixing
        */

        dialog = new JDialog(parent, false);
        dialog.setContentPane(new MassVolumeChooser(new MassVolumeModel()));
        dialog.setUndecorated(true);
        dialog.pack();
        dialog.setVisible(true);
        dialog.setBackground(new Color(0, 0, 0, 0.3f));

        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateDialogLocation();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                updateDialogLocation();
            }
        });

        //workaround for file menu lightweight mixing
        d2 = new JDialog(parent, false);
        d2.setUndecorated(true);
        JPanel pane = new JPanel();
        pane.setPreferredSize(new Dimension(100,1));
        d2.setContentPane(pane);
        d2.pack();
        d2.setVisible(true);

        updateDialogLocation();
    }

    private void updateDialogLocation() {
        Canvas c = (Canvas) canvas;
        Point pt = new Point(c.getWidth() - dialog.getWidth() - 50, 50);
        SwingUtilities.convertPointToScreen(pt, c);
        dialog.setLocation(pt);

        Point p2 = new Point(0, 0);
        SwingUtilities.convertPointToScreen(p2, c);
        d2.setLocation(p2);
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

    public void activate() {
        updateDialogLocation();
    }
}
