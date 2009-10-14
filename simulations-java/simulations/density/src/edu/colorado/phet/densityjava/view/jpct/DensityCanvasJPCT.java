package edu.colorado.phet.densityjava.view.jpct;

import com.jme.math.Vector3f;
import com.threed.jpct.*;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.densityjava.model.Block;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.RectangularObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;

public class DensityCanvasJPCT extends PhetPCanvas {
    private World world;
//    private ModelViewTransform2D modelViewTransform2D;

    static class BlockNode extends PNode {
        private RectangularObject rectangularObject;
        private ModelViewTransform2D modelViewTransform2D;
        private PhetPPath face;
        private PhetPPath topFace;
        private PhetPPath rightFace;

        public BlockNode(RectangularObject rectangularObject, ModelViewTransform2D modelViewTransform2D) {
            this.rectangularObject = rectangularObject;
            this.modelViewTransform2D = modelViewTransform2D;
            face = new PhetPPath(rectangularObject.getFaceColor());
            addChild(face);

            topFace = new PhetPPath(rectangularObject.getTopFaceColor());
            addChild(topFace);

            rightFace = new PhetPPath(rectangularObject.getRightFaceColor());
            addChild(rightFace);

            rectangularObject.addListener(new RectangularObject.Adapter() {
                public void modelChanged() {
                    updateShapes();
                }
            });
            updateShapes();
        }

        private void updateShapes() {
            face.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getFrontFace()));
            topFace.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getTopFace()));
            rightFace.setPathTo(modelViewTransform2D.createTransformedShape(rectangularObject.getRightFace()));
        }
    }

    static class DraggableBlockNode extends BlockNode {

        public DraggableBlockNode(final Block rectangularObject, final ModelViewTransform2D modelViewTransform2D) {
            super(rectangularObject, modelViewTransform2D);
            addInputEventListener(new CursorHandler());
            addInputEventListener(new PBasicInputEventHandler() {
                public void mouseDragged(PInputEvent event) {
                    Point2D pt = new Point2D.Double(event.getDeltaRelativeTo(getParent()).getWidth(), event.getDeltaRelativeTo(getParent()).getHeight());
                    rectangularObject.setUserDragging(true);
                    rectangularObject.translate(modelViewTransform2D.viewToModelDifferential(pt));
                }

                public void mouseReleased(PInputEvent event) {
                    rectangularObject.setUserDragging(false);
                }
            });
        }
    }

    public void addBlock(final Block block) {
//        TextureManager.getInstance().addTexture("box", new Texture(64,64, Color.red));

        final Object3D box = Primitives.getBox((float) block.getWidth(), 1f);
        block.addListener(new RectangularObject.Adapter() {
            public void modelChanged() {
                box.getTranslationMatrix().setIdentity();
                box.getTranslationMatrix().translate((float) block.getCenterX(), (float) block.getCenterY(), (float) block.getCenterZ());
            }
        });
        box.translate((float) block.getCenterX(), (float) block.getCenterY(), (float) block.getCenterZ());
        box.setTexture("box");
        box.setEnvmapped(Object3D.ENVMAP_ENABLED);
        box.setShadingMode(Object3D.SHADING_FAKED_FLAT);
        box.build();

        world.addObject(box);

//        world.getCamera().lookAt( box.getTransformedCenter() );
    }

    public DensityCanvasJPCT(DensityModel model) {
        setWorldTransformStrategy(new CenteringBoxStrategy(this, new PDimension(800, 800)));

        world = new World();
        TextureManager.getInstance().addTexture("box", new Texture(64, 64, Color.red));

        world.addLight(new SimpleVector(-10, -5, -10), Color.gray);
        world.getCamera().setPosition(10, 0, 30);

        for (int i = 0; i < model.getBlockCount(); i++) {
            addBlock(model.getBlock(i));
        }
//        addBlock(new DraggableBlockJPCT(model.getWater(), modelViewTransform2D));


        //for use with camera steering, see below
        Vector3f location = new Vector3f(4.2810082f, 7.02f, 10.8f), direction = new Vector3f(0.0f, -0.19007912f, -0.9817688f), up = new Vector3f(0.0f, 0.975561f, -0.21972877f);
        world.getCamera().setPosition(location.x, location.y, location.z );
//        world.getCamera().lookAt(new SimpleVector(direction.x,direction.y,direction.z));
        world.getCamera().setOrientation(new SimpleVector(direction.x, direction.y, direction.z), new SimpleVector(up.x, -up.y, up.z));
//        world.getCamera().rotateCameraZ((float) Math.PI);
        requestFocus();
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                System.out.println("e = " + e);
                SimpleVector d = getDelta(e);
                if (e.isControlDown()) {
                    d.add(world.getCamera().getDirection());
                    System.out.println("world.getCamera().getUpVector() = " + world.getCamera().getUpVector());
                    SimpleVector newUP = new SimpleVector();
                    newUP.add(world.getCamera().getUpVector());
                    newUP.scalarMul(-1);
                    world.getCamera().setOrientation(d, newUP);
//                    world.getCamera().setOrientation(d, world.getCamera().getUpVector());
                } else {
                    d.add(world.getCamera().getPosition());
                    world.getCamera().setPosition(d);
                }
            }

            private SimpleVector getDelta(KeyEvent e) {
                float dx = (float) ((float) 0.3/2.0);
                if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    return new SimpleVector(dx, 0, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    return new SimpleVector(-dx, 0, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                    return new SimpleVector(0, dx, 0);
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    return new SimpleVector(0, -dx, 0);
                } else return new SimpleVector(0, 0, 0);
            }

            public void keyReleased(KeyEvent e) {
            }
        });
//        world.getCamera().
//        cam.setDirection(direction);
//        cam.setUp(up);

        setOpaque(false);

        Timer timer = new Timer(30, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                box.rotateY( 0.01f );
                requestFocus();
                repaint();
            }
        });
        timer.start();
    }

    FrameBuffer buffer = new FrameBuffer(800, 600, FrameBuffer.SAMPLINGMODE_OGSS);

    public void paintComponent(Graphics g) {
        buffer.clear(java.awt.Color.BLUE);
        world.renderScene(buffer);
        world.draw(buffer);
        buffer.update();
        buffer.display(g);
        super.paintComponent(g);
    }

    private class UndraggableBlockNode extends BlockNode {
        public UndraggableBlockNode(RectangularObject object, ModelViewTransform2D modelViewTransform2D) {
            super(object, modelViewTransform2D);
            setPickable(false);
            setChildrenPickable(false);
        }
    }
}