package edu.colorado.phet.density.view.xith;

import edu.colorado.phet.densityjava.ModelComponents;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.MassVolumeModel;
import org.jagatoo.input.InputSystem;
import org.jagatoo.input.InputSystemException;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.xith3d.loop.RenderLoop;
import org.xith3d.render.Canvas3DJPanel;
import org.xith3d.render.config.OpenGLLayer;

import javax.media.opengl.GLJPanel;
import javax.swing.*;
import java.awt.*;

public class XithCanvas extends JPanel {
    public XithCanvas(JFrame frame, DensityModel model, MassVolumeModel massVolumeModel, ModelComponents.DisplayDimensions displayDimensions) {

        RenderLoop renderLoop = new InputAdapterRenderLoop();
        Xith3DEnvironment env = new Xith3DEnvironment(renderLoop);

        Canvas3DJPanel canvasPanel = new Canvas3DJPanel(OpenGLLayer.JOGL_SWING);
        MySC container = new MySC(canvasPanel);
        setLayout(new BorderLayout());
        final GLJPanel panel = container.getGlComponent();
        panel.add(new JButton("Test"));


        // register an AWTKeyboard on the RenderLoop
        try {
            InputSystem.getInstance().registerNewKeyboardAndMouse(canvasPanel.getCanvas().getPeer());
        } catch (InputSystemException e) {
            e.printStackTrace();
        }
        env.addCanvas(canvasPanel);

        add(panel, BorderLayout.CENTER);
    }
}
