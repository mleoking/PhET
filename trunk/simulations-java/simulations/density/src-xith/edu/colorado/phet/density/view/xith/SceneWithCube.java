package edu.colorado.phet.density.view.xith;

import org.xith3d.resources.ResourceLocator;
import org.xith3d.render.Canvas3DFactory;
import org.xith3d.render.Canvas3D;
import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.scenegraph.BranchGroup;
import org.xith3d.scenegraph.Appearance;
import org.xith3d.scenegraph.Texture;
import org.xith3d.scenegraph.primitives.Cube;
import org.xith3d.loaders.texture.TextureLoader;
import org.xith3d.loop.InputAdapterRenderLoop;
import org.jagatoo.input.devices.components.Key;
import org.jagatoo.input.events.KeyReleasedEvent;

public class SceneWithCube extends InputAdapterRenderLoop {
    @Override
    public void onKeyReleased(KeyReleasedEvent e, Key key) {
        switch (key.getKeyID()) {
            case ESCAPE:
                this.end();
                break;
        }
    }

    private BranchGroup createScene() {
        Cube cube = new Cube(3.0f);
        Texture tex = TextureLoader.getInstance().getTexture("stone.jpg");
        Appearance app = new Appearance();
        app.setTexture(tex);
        cube.setAppearance(app);
        BranchGroup bg = new BranchGroup();
        bg.addChild(cube);
        return (bg);
    }

    public SceneWithCube() throws Exception {
        super(120f);
        Xith3DEnvironment env = new Xith3DEnvironment(this);
        Canvas3D canvas = Canvas3DFactory.createWindowed(800, 600,
                "My empty scene");
        env.addCanvas(canvas);
//        geti
//        this.getInputManager().registerKeyboardAndMouse(canvas);
        ResourceLocator resLoc = ResourceLocator.create("test-resources/");
        resLoc.createAndAddTSL("textures");
        env.addPerspectiveBranch(createScene());
        this.begin();
    }

    public static void main(String[] args) throws Exception {
        new SceneWithCube();
    }
}
