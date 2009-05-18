package edu.colorado.phet.densityjava.tests;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 15, 2009
 * Time: 1:30:26 PM
 * To change this template use File | Settings | File Templates.
 */
import com.jme.app.SimpleGame;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Sphere;
import com.jme.scene.shape.Teapot;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.system.DisplaySystem;

/**
 * This tutorial deals with object transparency.
 *
 * @author <a href="mailto:loic.lefevre@gmail.com">Loïc Lefèvre</a>
 */
public class TestTransparency extends SimpleGame {

    /**
     * The sphere material.
     */
    private MaterialState materialState;

    /**
     * The amount of opacity (0 = fully transparent or invisible).
     */
    private float opacityAmount = 1.0f;

    /**
     * Step amount for transparency changes.
     */
    private float step = -0.5f;

    /**
     * A node to rotate two teapots at a time.
     */
    private Node teapots;

    /**
     * Current rotation angle of the teapots node.
     */
    private float rotationAngle = 0.0f;

    /**
     * A Text object to display information.
     */
    private Text tutorialStepText;

    /**
     * Tutorial main entry point.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        final TestTransparency app = new TestTransparency();
        // Always show the configuration dialog box for display settings
        app.setConfigShowMode(SimpleGame.ConfigShowMode.ShowIfNoConfig);
        // Start to render
        app.start();
    }

    /**
     * Initialization code of the tutorial
     */
    protected void simpleInitGame() {
        // hide the mouse cursor
        MouseInput.get().setCursorVisible(false);

        // set the title of our tutorial
        display.setTitle("JME 2.X - Material Tutorial 3 - Object transparency");

        // set the background color to light gray
        display.getRenderer().setBackgroundColor(ColorRGBA.lightGray);

        // detach all possible lights, we will setup our own
        lightState.detachAll();

        // our light
        final PointLight light = new PointLight();
        light.setDiffuse(ColorRGBA.white);
        light.setSpecular(ColorRGBA.white);
        light.setLocation(new Vector3f(100.0f, 100.0f, 100.0f));
        light.setEnabled(true);

        // attach the light to a lightState
        lightState.attach(light);

        // build a node that will hold two teapots
        // this is used to rotate teapots along the reference Y axis
        // by translating the two teapots, we will see the two teapots
        // rotating around the sphere instead of rotating around there
        // center
        // see: http://www.jmonkeyengine.com/jmeforum/index.php?topic=7849.0
        teapots = new Node("teapots");

        // first teapot
        final Teapot teapot = new Teapot("teapot");
        teapot.setLocalTranslation(0.0f, -2.0f, 6.0f);
        teapots.attachChild(teapot);

        // second teapot
        final Teapot farTeapot = new Teapot("farTeapot");
        farTeapot.setLocalTranslation(0.0f, -2.0f, -6.0f);
        teapots.attachChild(farTeapot);

        // attach it to the root node
        rootNode.attachChild(teapots);

        // our sphere
        final Sphere sphere = new Sphere("sphere", Vector3f.ZERO, 128, 128, 7f);

        // the sphere material taht will be modified to make the sphere
        // look opaque then transparent then opaque and so on
        materialState = display.getRenderer().createMaterialState();
        materialState.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setDiffuse(new ColorRGBA(0.1f, 0.5f, 0.8f, opacityAmount));
        materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
        materialState.setShininess(128.0f);
        materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, opacityAmount));
        materialState.setEnabled(true);

        // IMPORTANT: this is used to handle the internal sphere faces when
        // setting them to transparent, try commenting this line to see what
        // happens
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);

        sphere.setRenderState(materialState);
        sphere.updateRenderState();

        rootNode.attachChild(sphere);

        // to handle transparency: a BlendState
        // an other tutorial will be made to deal with the possibilities of this
        // RenderState
        final BlendState alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        alphaState.setBlendEnabled(true);
        alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        alphaState.setTestEnabled(true);
        alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
        alphaState.setEnabled(true);

        sphere.setRenderState(alphaState);
        sphere.updateRenderState();

        // IMPORTANT: since the sphere will be transparent, place it
        // in the transparent render queue!
        sphere.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);

        // create the Text object that will display information
        // about transparency
        tutorialStepText = new Text("sphereTransparencyLevel", "Sphere transparency: " + (int) ((1 - opacityAmount) * 100.0f) + "%");
        // no light effect
        tutorialStepText.setLightCombineMode(Spatial.LightCombineMode.Off);
        // no culling
        tutorialStepText.setCullHint(Spatial.CullHint.Never);
        tutorialStepText.setRenderState(Text.getDefaultFontTextureState());
        tutorialStepText.setRenderState(Text.getFontBlend());
        // font size
        tutorialStepText.setLocalScale(.8f);
        // text color
        tutorialStepText.setTextColor(ColorRGBA.black);
        // (x, y) position
        tutorialStepText.setLocalTranslation(15, 10, 0);
        tutorialStepText.updateRenderState();

        // attach the text to the root node of our scenegraph
        rootNode.attachChild(tutorialStepText);
    }

    /**
     * Callback method which is called by SimpleGame each time it asks us to update our data.
     */
    @Override
    protected void simpleUpdate() {
        // update the opacity amount
        opacityAmount += step * tpf;

        // check for limits
        if (opacityAmount < 0.0) {
            opacityAmount = 0.0f;
            step = -step;
        }

        if (opacityAmount > 1.0f) {
            opacityAmount = 1.0f;
            step = -step;
        }

        // change the opacity of the sphere material
        materialState.getDiffuse().a = opacityAmount;

        // print information
        tutorialStepText.print("Sphere transparency: " + (int) ((1 - opacityAmount) * 100.0f) + "%");

        // rotate the two teapots at the same time around the sphere center
        rotationAngle += tpf * 22.25f;
        // handle limits
        rotationAngle %= 360;
        teapots.setLocalRotation(new Quaternion().fromAngleNormalAxis(rotationAngle * FastMath.DEG_TO_RAD, Vector3f.UNIT_Y));
    }
}
