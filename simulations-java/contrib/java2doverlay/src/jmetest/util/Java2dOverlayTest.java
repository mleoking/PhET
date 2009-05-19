package jmetest.util;

import java.awt.Color;
import java.awt.Graphics2D;

import com.jme.app.SimpleGame;
import com.jme.app.AbstractGame.ConfigShowMode;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.jme.scene.state.TextureState;
import com.jme.util.Java2dOverlay;
import com.jme.util.TextureManager;

/**
 * Test for the {@code Java2dOverlay} class.
 */

public class Java2dOverlayTest extends SimpleGame {

	private MyOverlay overlay;

	public static void main(String[] args) {
		Java2dOverlayTest app = new Java2dOverlayTest();
		app.setConfigShowMode(ConfigShowMode.AlwaysShow);
		app.start();
	}

	@Override
	protected void simpleInitGame() {

		lightState.setEnabled(false);

		Box floor = new Box("Floor", new Vector3f(), 100, 1, 100);
		floor.setModelBound(new BoundingBox());
		floor.updateModelBound();
		floor.getLocalTranslation().y = -20;

		TextureState ts = display.getRenderer().createTextureState();
		Texture t0 = TextureManager.loadTexture(Java2dOverlayTest.class.
				getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
				Texture.MinificationFilter.Trilinear,
				Texture.MagnificationFilter.Bilinear);
		t0.setWrap(Texture.WrapMode.Repeat);
		ts.setTexture(t0);
		floor.setRenderState(ts);
		floor.scaleTextureCoordinates(0, 5);
		rootNode.attachChild(floor);

		// Create the overlay and add it to the scene

		overlay = new MyOverlay();
		overlay.setUpdateTime(0.04f); // Update with around 25 fps
		rootNode.attachChild(overlay.getQuad());
		overlay.start();
	}

	@Override
	protected void simpleUpdate() {
		super.simpleUpdate();
		overlay.requestUpdate(0.04f);
	}

	@Override
	protected void simpleRender() {
		super.simpleRender();
		overlay.requestRender();
	}

	private static class MyOverlay extends Java2dOverlay {

		private int animationX = 0;
		private int speed = 1;

		public MyOverlay() {
			super(0, 600 - 64, 512, 64);
		}

		@Override
		public void paint(Graphics2D g2) {
			g2.setColor(Color.RED);
			g2.fillRect(animationX, 10, 50, 50);

			animationX += speed;
			if (animationX <=0 || animationX >= getWidth() - 50) {
				speed = -speed;
			}
		}
	}
}
