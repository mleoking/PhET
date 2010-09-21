package edu.colorado.phet.densityjava.tests;

import com.jme.app.SimpleGame;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.math.FastMath;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jmex.awt.swingui.JMEAction;
import edu.colorado.phet.densityjava.tests.PiccoloNode;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 15, 2009
 * Time: 7:44:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class HelloPiccoloNode extends SimpleGame {
    private static final Logger logger = Logger.getLogger(HelloPiccoloNode.class
            .getName());

    private Node guiNode;

    protected void simpleInitGame() {
        // create a node for ortho gui stuff
        guiNode = new Node("gui");
        guiNode.setRenderQueueMode(Renderer.QUEUE_ORTHO);

        // create the desktop Quad
        final PiccoloNode desktop = new PiccoloNode("desktop", 500, 400, input);
        // and attach it to the gui node
        guiNode.attachChild(desktop);
        // center it on screen
        desktop.getLocalTranslation().set(display.getWidth() / 2 - 30, display.getHeight() / 2 + 50, 0);

        // perform all the swing stuff in the swing thread
        // (Only access the Swing UI from the Swing event dispatch thread!
        // See SwingUtilities.invokeLater()
        // and http://java.sun.com/docs/books/tutorial/uiswing/concurrency/index.html for details.)
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    // make it transparent blue
//                    desktop.getJDesktop().setBackground(new Color(0, 0, 1, 0.2f));

                    // create a swing button
                    final JButton button = new JButton("click me");
                    // and put it directly on the desktop
//                    desktop.getJDesktop().add(button);
                    // desktop has no layout - we layout ourselfes (could assign a layout to desktop here instead)
                    button.setLocation(200, 200);
                    button.setSize(button.getPreferredSize());
                    // add some actions
                    // standard swing action:
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            // this gets executed in swing thread
                            // alter swing components ony in swing thread!
                            button.setLocation(FastMath.rand.nextInt(400), FastMath.rand.nextInt(300));
                            logger.info("clicked!");
                        }
                    });
                    // action that gets executed in the update thread:
                    button.addActionListener(new JMEAction("my action", input) {
                        public void performAction(InputActionEvent evt) {
                            // this gets executed in jme thread
                            // do 3d system calls in jme thread only!
                            guiNode.updateRenderState(); // this call has no effect but should be done in jme thread :)
                        }
                    });
                }
            });
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        // don't cull the gui away
        guiNode.setCullHint(Spatial.CullHint.Never);
        // gui needs no lighting
        guiNode.setLightCombineMode(Spatial.LightCombineMode.Off);
        // update the render states (especially the texture state of the deskop!)
        guiNode.updateRenderState();
        // update the world vectors (needed as we have altered local translation of the desktop and it's
        //  not called in the update loop)
        guiNode.updateGeometricState(0, true);

        // finally show the system mouse cursor to allow the user to click our button
        MouseInput.get().setCursorVisible(true);
    }

    protected void simpleRender() {
        System.out.println("HelloPiccoloNode.simpleRender");
        guiNode.updateGeometricState(0, true);
        guiNode.updateRenderState();
        // draw the gui stuff after the scene
        display.getRenderer().draw(guiNode);
    }

    public static void main(String[] args) {
        new HelloPiccoloNode().start();
    }
}
