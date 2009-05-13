package edu.colorado.phet.density


import com.jme.bounding.BoundingBox
import com.jme.image.Texture
import com.jme.input.action.{MouseInputAction, InputAction, InputActionEvent}
import com.jme.input.{MouseInput, AbsoluteMouse, InputHandler, KeyInput}
import com.jme.intersection.{TrianglePickResults, PickResults, BoundingPickResults}
import com.jme.math._
import com.jme.scene.shape.Box

import com.jme.renderer.Renderer
import com.jme.scene.{Text, Spatial}

import com.jme.scene.Spatial.TextureCombineMode
import com.jme.scene.state.TextureState
import com.jme.system.canvas.{JMECanvas, SimpleCanvasImpl}
import com.jme.system.DisplaySystem
import com.jme.system.lwjgl.LWJGLSystemProvider
import com.jme.util.TextureManager
import com.jmex.awt.input.AWTMouseInput
import com.jmex.awt.lwjgl.{LWJGLAWTCanvasConstructor, LWJGLCanvas}
import common.phetcommon.application.{PhetApplicationConfig, Module, PhetApplicationLauncher}
import common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel
import common.piccolophet.PiccoloPhetApplication
import java.awt.{Canvas, BorderLayout, Component}
import java.net.URL
import javax.swing.{JComponent, JPanel}
import jmetest.util.JMESwingTest
import scalacommon.ScalaClock

class DensityModule extends Module("Density", new ScalaClock(30, 30 / 1000.0)) {
  setSimulationPanel(new JMEPanel)
  setClockControlPanel(new PiccoloClockControlPanel(getClock))
}

//See JMESwingTest
class JMEPanel extends JPanel {
  setLayout(new BorderLayout)
  val display = DisplaySystem.getDisplaySystem(LWJGLSystemProvider.LWJGL_SYSTEM_IDENTIFIER)
  display.registerCanvasConstructor("AWT", classOf[LWJGLAWTCanvasConstructor])
  val canvas = display.createCanvas(500, 500)
  canvas.setUpdateInput(true)
  canvas.setTargetRate(60)
  val implementor = new MyImplementor(display, canvas)
  canvas.setImplementor(implementor)

  AWTMouseInput.setup(canvas.asInstanceOf[Canvas], false)
  KeyInput.setProvider(KeyInput.INPUT_AWT)

  add(canvas.asInstanceOf[Component], BorderLayout.CENTER)

}

class MyImplementor(val display: DisplaySystem, canvas: JMECanvas) extends SimpleCanvasImpl(499, 499) {
  var angle = 0.0f
  val rotQuat = new Quaternion()
  val axis = new Vector3f(1, 1, 0.5f)
  var fps = 0.0
  val input = new InputHandler()
  val max = new Vector3f(5, 5, 5)
  val min = new Vector3f(-5, -5, -5)

  val box = new Box("MyBox", min, max)
  // This will be my mouse
  val am = new AbsoluteMouse("The Mouse", 499, 499)

  // Move the mouse to the middle of the screen to start with
  //  am.setLocalTranslation(new Vector3f(display.getWidth() / 2, display.getHeight() / 2, 0));
  // Assign the mouse to an input handler

  override def simpleSetup() = {

    axis.normalizeLocal()

    box.setModelBound(new BoundingBox())
    box.updateModelBound()
    box.setLocalTranslation(new Vector3f(0, 0, -10))
    box.setRenderQueueMode(Renderer.QUEUE_SKIP)
    rootNode.attachChild(box)

    val ts = renderer.createTextureState()
    ts.setEnabled(true)
    //    ts.setTexture(TextureManager.loadTexture(classOf[JMESwingTest].getClassLoader().getResource("jmetest/data/images/Monkey.jpg"),
    ts.setTexture(TextureManager.loadTexture(classOf[JMESwingTest].getClassLoader().getResource("phetcommon/images/logos/phet-logo-120x50.jpg"),
      Texture.MinificationFilter.Trilinear,
      Texture.MagnificationFilter.Bilinear))

    rootNode.setRenderState(ts)
    rootNode.attachChild(am)
    val startTime = System.currentTimeMillis() + 5000

    am.registerWithInputHandler(input);

    val ts3 = display.getRenderer().createTextureState();
  val cursorLoc = classOf[MyImplementor].getClassLoader().getResource(
    "jmetest/data/cursor/cursor1.png");
  val t = TextureManager.loadTexture(cursorLoc, Texture.MinificationFilter.NearestNeighborNoMipMaps,
    Texture.MagnificationFilter.Bilinear);
  ts3.setTexture(t);

  am.setRenderState(ts3);

    rootNode.attachChild(am)

    input.addAction(new InputAction() {
      override def performAction(evt: InputActionEvent) {
        println(evt)
        //        logger.info(evt.getTriggerName())
      }
    }, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL,
      InputHandler.AXIS_NONE, false)

    input.addAction(new InputAction() {
      override def performAction(evt: InputActionEvent) {
        //        logger.info(evt.getTriggerName())
      }
    }, InputHandler.DEVICE_KEYBOARD, InputHandler.BUTTON_ALL,
      InputHandler.AXIS_NONE, false)
  }

  override def simpleUpdate() {
    input.update(tpf)

    // Code for rotating the box... no surprises here.
    if (tpf < 1) {
      angle = angle + (tpf * 25)
      if (angle > 360) {
        angle = 0
      }
    }
    rotQuat.fromAngleNormalAxis(angle * FastMath.DEG_TO_RAD, axis)
    box.setLocalRotation(rotQuat)

    box.setModelBound(new BoundingBox());
    box.updateModelBound();

    resizeCanvas(canvas.asInstanceOf[Component].getWidth(), canvas.asInstanceOf[Component].getHeight());



    // Get the mouse input device from the jME mouse
    // Is button 0 down? Button 0 is left click
    if (MouseInput.get().isButtonDown(0)) {
      val screenPos = new Vector2f();
      // Get the position that the mouse is pointing to
      screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
      // Get the world location of that X,Y value
      val worldCoords = display.getWorldCoordinates(screenPos, 0);
//      val worldCoords = cam.getLocation;
      val worldCoords2 = display.getWorldCoordinates(screenPos, 1);
      //      logger.info(worldCoords.toString());
      // Create a ray starting from the camera, and going in the direction
      // of the mouse's location
      val mouseRay = new Ray(worldCoords, worldCoords2
              .subtractLocal(worldCoords).normalizeLocal());
      // Does the mouse's ray intersect the box's world bounds?
      val pr = new TrianglePickResults
      //      pr.clear();
      rootNode.findPick(mouseRay, pr);

      for (i <- 0 until pr.getNumber) {
        //      for (int i = 0;
        //    i < pr.getNumber();
        //    i ++) {
        pr.getPickData(i).getTargetMesh().setRandomColors();
      }
    }
  }
}

class DensityApplication(config: PhetApplicationConfig) extends PiccoloPhetApplication(config: PhetApplicationConfig) {
  addModule(new DensityModule())
}

object DensityApplicationMain {
  def main(args: Array[String]) = new PhetApplicationLauncher().launchSim(args, "density", classOf[DensityApplication])
}