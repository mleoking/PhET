package edu.colorado.phet.density.view.xith;

import org.xith3d.base.Xith3DEnvironment;
import org.xith3d.loop.RenderLoop;
import org.xith3d.render.Canvas3D;
import org.xith3d.render.Canvas3DFactory;

public class TestXith {
    public TestXith() {
        Xith3DEnvironment env = new Xith3DEnvironment();
        Canvas3D canvas = Canvas3DFactory.createWindowed(800, 600,
                "My empty scene");
        env.addCanvas(canvas);
        RenderLoop rl = new RenderLoop();
        rl.setMaxFPS(120f);
        rl.setXith3DEnvironment(env);
// never forget to start the RenderLoop!
        rl.begin();
    }

    public static void main(String[] args) {
        new TestXith();
    }
}
