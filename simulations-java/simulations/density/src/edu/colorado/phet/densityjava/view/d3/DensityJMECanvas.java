package edu.colorado.phet.densityjava.view.d3;

import com.jme.input.KeyInput;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.JMECanvas;
import com.jme.system.lwjgl.LWJGLSystemProvider;
import com.jmex.awt.input.AWTMouseInput;
import com.jmex.awt.lwjgl.LWJGLAWTCanvasConstructor;
import edu.colorado.phet.densityjava.ModelComponents;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.MassVolumeModel;
import edu.colorado.phet.densityjava.view.DensityControlPanel;
import edu.colorado.phet.densityjava.view.MassVolumeChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

public class DensityJMECanvas extends JPanel {
    private JMECanvas canvas = null;
    private DensityCanvasImpl impl;
    private JDialog d1;
    private JDialog d3;

    public DensityJMECanvas(JFrame parent, DensityModel model, MassVolumeModel massVolumeModel, ModelComponents.DisplayDimensions displayDimensions) {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        // -------------GL STUFF------------------

        // make the canvas:
        DisplaySystem display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER);
        display.registerCanvasConstructor("AWT", LWJGLAWTCanvasConstructor.class);
        int width = 640;
        int height = 480;
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
        impl = new DensityCanvasImpl(width, height, display, (Component) canvas, model, displayDimensions);
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
        6. use Java2dOverlay from http://www.jmonkeyengine.com/jmeforum/index.php?action=printpage;topic=11089.0
        */

        d1 = new JDialog(parent, false);
        d1.setContentPane(new MassVolumeChooser(massVolumeModel));
        d1.setUndecorated(true);
        d1.pack();
        d1.setVisible(true);

        parent.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateDialogLocations();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                updateDialogLocations();
            }
        });

        d3 = new JDialog(parent, false);
        d3.setUndecorated(true);
        d3.setContentPane(new DensityControlPanel(model.getUnits(),displayDimensions));
        d3.pack();
        d3.setVisible(true);

        updateDialogLocations();
    }

    private void updateDialogLocations() {
        Canvas c = (Canvas) canvas;
        Point pt = new Point(c.getWidth() - d1.getWidth() - 50, 50);
        SwingUtilities.convertPointToScreen(pt, c);
        d1.setLocation(pt);

        Point p3 = new Point(0, c.getHeight() - d3.getHeight());
        SwingUtilities.convertPointToScreen(p3, c);
        d3.setLocation(p3);
    }

    protected void doResize() {
        impl.resizeCanvas(((Component) canvas).getWidth(), ((Component) canvas).getHeight());
        canvas.makeDirty();
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }

    public Component getCanvas() {
        return (Component) canvas;
    }

    public void activate() {
        updateDialogLocations();
    }
}
