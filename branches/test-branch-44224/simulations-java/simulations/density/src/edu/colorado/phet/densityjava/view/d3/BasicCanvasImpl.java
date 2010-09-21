package edu.colorado.phet.densityjava.view.d3;

import com.jme.image.Texture;
import com.jme.input.InputHandler;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.system.canvas.SimpleCanvasImpl;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.TextureManager;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.densityjava.view.d3.SRRMouse;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;

public class BasicCanvasImpl extends SimpleCanvasImpl {

    long startTime = 0;
    long fps = 0;
    protected InputHandler input;
    private SRRMouse srrMouse;
    private DisplaySystem display;
    private Component canvas;

    public BasicCanvasImpl(int width, int height, DisplaySystem display, Component canvas) {
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
        startTime = System.currentTimeMillis() + 5000;

        input = new InputHandler();

        // Create a new mouse. Restrict its movements to the display screen.
        srrMouse = createMouse();
        rootNode.attachChild(srrMouse);
    }

    private SRRMouse createMouse() {
        SRRMouse m = new SRRMouse("The Mouse", display.getWidth(), display.getHeight());

        // Get a picture for my mouse.
        TextureState mouseTextureState = display.getRenderer().createTextureState();
        URL cursorLoc = BasicCanvasImpl.class.getClassLoader().getResource(
                "density/images/cursor1.png");
        Texture t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
                Texture.MagnificationFilter.Bilinear);
        mouseTextureState.setTexture(t);
//        m.setRenderState(mouseTextureState);//DEBUG: add cursor icon

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

    private static final Font FONT = new PhetFont(17);
    private Color[] colors = new Color[]{Color.red, Color.green, Color.blue,
            Color.yellow, Color.white, Color.orange};

    protected void setTexture(final Spatial s) {
        final BufferedImage bi = new BufferedImage(64, 512,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D bg = (Graphics2D) bi.getGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        bg.setFont(FONT);
        for (int i = 0; i < 6; i++) {
            bg.setColor(colors[i]);
            bg.fillRect(0, i * 64, 64, (i + 1) * 64);
            bg.setColor(Color.black);
            bg.drawString("100 kg", 5, 64 * i + 38);
        }
        bg.dispose();
        GameTaskQueueManager.getManager().update(new Callable<Object>() {
            public Object call() throws Exception {
                try {
                    TextureState ts = DisplaySystem.getDisplaySystem()
                            .getRenderer().createTextureState();
                    Texture t = TextureManager.loadTexture(bi,
                            Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear, 1, false);
                    ts.setTexture(t);
                    TextureState oldTs = (TextureState) s
                            .getRenderState(RenderState.StateType.Texture);
                    if (oldTs != null) {
                        TextureManager.releaseTexture(oldTs.getTexture());
                        oldTs.deleteAll(true);
                    }
                    s.setRenderState(ts);
                    s.updateRenderState();
                } catch (ConcurrentModificationException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        });
    }

    public void simpleUpdate() {
        input.update(tpf);
        if (srrMouse != null)
            srrMouse.setLimit(canvas.getWidth(), canvas.getHeight());
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

    public Vector2f getMouseScreenPosition() {
        Vector2f screenPos = new Vector2f();
        // Get the position that the mouse is pointing to
        screenPos.set(srrMouse.getHotSpotPosition().x, srrMouse.getHotSpotPosition().y);
        return screenPos;
    }
}
