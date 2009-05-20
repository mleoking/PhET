package edu.colorado.phet.densityjava.view.d3;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.KeyboardLookHandler;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.light.PointLight;
import com.jme.math.Matrix3f;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.Java2dOverlay;
import com.jme.util.TextureManager;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.nodes.ArrowNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.densityjava.ModelComponents;
import edu.colorado.phet.densityjava.common.Unit;
import edu.colorado.phet.densityjava.model.Block;
import edu.colorado.phet.densityjava.model.DensityModel;
import edu.colorado.phet.densityjava.model.RectangularObject;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class DensityCanvasImpl extends BasicCanvasImpl implements WaterSurface.WaterSurfaceEnvironment {
    private DensityModel model;
    private DisplaySystem display;
    private Component component;
    private final ModelComponents.DisplayDimensions displayDimensions;
    private PickData picked = null;
    private Point2D pickPt;
    private WaterSurface waterSurface;
    private Java2dOverlay overlay;

    public DensityCanvasImpl(int width, int height, DisplaySystem display, Component component, DensityModel model, ModelComponents.DisplayDimensions displayDimensions) {
        super(width, height, display, component);
        this.model = model;
        this.display = display;
        this.component = component;
        this.displayDimensions = displayDimensions;
    }

    public void simpleSetup() {
        super.simpleSetup();
        waterSurface = new WaterSurface.Motionless(model, model.getWater(), this);
//        waterSurface = new WaterSurface.RippleSurface(model, this);

        //for use with camera steering, see below
        Vector3f location = new Vector3f(4.2810082f, 7.02f, 10.8f), direction = new Vector3f(0.0f, -0.19007912f, -0.9817688f), up = new Vector3f(0.0f, 0.975561f, -0.21972877f);
        cam.setLocation(location);
        cam.setDirection(direction);
        cam.setUp(up);

        rootNode.attachChild(getPoolNode(model.getSwimmingPool()));
//        rootNode.attachChild(getWaterNode(model.getWater()));

        //Handle adding block nodes
        for (int i = 0; i < model.getBlockCount(); i++) {
            doAddBlock(model.getBlock(i));
        }
        model.addListener(new DensityModel.Adapter() {
            public void blockAdded(Block block) {
                doAddBlock(block);
            }
        });

        //Handle adding scale nodes
        for (int i = 0; i < model.getScaleCount(); i++) {
            doAddScale(model.getScale(i));
        }
        model.addListener(new DensityModel.Adapter() {
            public void scaleAdded(DensityModel.Scale scale) {
                doAddScale(scale);
            }
        });


        rootNode.attachChild(new CutawayEarthNode(model));
        rootNode.attachChild(new GrassNode(model));
        rootNode.attachChild(new VolumeReadout(model, displayDimensions));


        //For debugging locations
//        rootNode.attachChild(new Sphere("sphere", 10, 10, 0.5f));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f((float) model.getSwimmingPool().getCenterX(), (float) model.getSwimmingPool().getCenterY(), (float) model.getSwimmingPool().getCenterZ()), 10, 10, 1));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f(10, 0, 0), 10, 10, 1));
//        rootNode.attachChild(new Sphere("sphere", new Vector3f(10, 5, 0), 10, 10, 1));

//        FirstPersonHandler firstPersonHandler = new FirstPersonHandler(cam, 50, 1);
//        firstPersonHandler.setButtonPressRequired(true);
        KeyboardLookHandler keyboardLookHandler = new KeyboardLookHandler(cam, 20, 1);
        input.addToAttachedHandlers(keyboardLookHandler);
//        input.addToAttachedHandlers(firstPersonHandler);

        setupLight();

        addMouseHandling();

//        addJava2DOverlay();
    }

    private static class MyOverlay extends Java2dOverlay {

        private int animationX = 0;
        private int speed = 5;

        public MyOverlay() {
            super(0, 600 - 64, 512, 64);
        }

        @Override
        public void paint(Graphics2D g2) {
            g2.setColor(Color.RED);
            g2.fillRect(animationX, 10, 50, 50);

            animationX += speed;
            if (animationX <= 0 || animationX >= getWidth() - 50) {
                speed = -speed;
            }
        }
    }

    private void addJava2DOverlay() {
        overlay = new MyOverlay();
        overlay.setUpdateTime(0.04f); // Update with around 25 fps
        rootNode.attachChild(overlay.getQuad());
        overlay.start();
    }

    public void simpleUpdate() {
        super.simpleUpdate();    //To change body of overridden methods use File | Settings | File Templates.
        waterSurface.simpleUpdate(tpf);
//        overlay.requestUpdate(0.04f);
//        System.out.println("location=" + toSource(cam.getLocation()) + ", direction=" + toSource(cam.getDirection()) + ", up=" + toSource(cam.getUp()));
    }

    @Override
    public void simpleRender() {
        super.simpleRender();
//        overlay.requestRender();
    }

    private String toSource(Vector3f a) {
        return "new Vector3f(" + a.getX() + "f," + a.getY() + "f," + a.getZ() + "f)";
    }

    static class ScaleNode extends Node {
        private DensityModel.Scale scale;
        private Box geom;
        private Renderer renderer;

        public ScaleNode(DensityModel.Scale scale, final Renderer renderer) {
            this.scale = scale;
            this.renderer = renderer;
            this.geom = new Box("name");
            float w = 1;
            float h = 1;
            float d = 1;
            geom.updateGeometry(new Vector3f(0, 0, 0), w / 2, h / 2, d / 2);
            geom.setLocalTranslation((float) (w / 2 + scale.getX()), (float) (h / 2 + scale.getY()), -d / 2 - 1E-2f);

            attachChild(geom);

            scale.addListener(new DensityModel.Scale.Listener() {
                public void normalForceChanged() {
                    updateTexture();
                }
            });

            //for some reason it doesn't work properly if you do this immediately
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateTexture();
                }
            });
        }

        private void updateTexture() {

            int imageWidth = 128;
            BufferedImage image = new BufferedImage(imageWidth, imageWidth, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = image.createGraphics();

            g2.setPaint(Color.lightGray);
            g2.fillRect(0, 0, 128, 128);
            PNode node = new PNode();

//            System.out.println("scale.getFormattedNormalForceString() = " + scale.getFormattedNormalForceString());
            PText pText = new PText(scale.getFormattedNormalForceString());
            double inset = 5;
            PPath path = new PhetPPath(new RoundRectangle2D.Double(-inset, -inset, pText.getWidth() + inset * 2, pText.getHeight() + inset * 2, 10, 10), Color.gray, new BasicStroke(2), Color.black);
            node.addChild(path);
            node.addChild(pText);
            node.scale(imageWidth / node.getFullBounds().getWidth());
            node.translate(inset, inset);
            node.fullPaint(new PPaintContext(g2));


            //TODO: need to release old textures in this update, or will obtain:
            //TODO: Exception in thread "AWT-EventQueue-0" java.lang.OutOfMemoryError: Direct buffer memory
            Texture texture = TextureManager.loadTexture(image, Texture.MinificationFilter.Trilinear,
                    Texture.MagnificationFilter.Bilinear, true);
            texture.setWrap(Texture.WrapMode.Repeat);
            TextureState textureState = renderer.createTextureState();
            textureState.setTexture(texture);
            geom.setRenderState(textureState);
            geom.updateRenderState();
        }
    }

    private void doAddScale(DensityModel.Scale scale) {
        System.out.println("DensityCanvasImpl.doAddScale");
        final ScaleNode child = new ScaleNode(scale, renderer);
        rootNode.attachChild(child);
        scale.addListener(new RectangularObject.Adapter() {
            public void blockRemoving() {
                rootNode.detachChild(child);
            }
        });
    }

    private void doAddBlock(Block block) {
        System.out.println("DensityCanvasImpl.doAddBlock");
        final RectNode child = new RectNode(block);
        rootNode.attachChild(child);
        block.addListener(new RectangularObject.Adapter() {
            public void blockRemoving() {
                rootNode.detachChild(child);
            }
        });
    }

    private Spatial getPoolNode(DensityModel.SwimmingPool swimmingPool) {
        Node poolNode = new Node();
        float r = (float) (255 / 255.0);
        float g = (float) (255 / 255.0);
        float b = (float) (255 / 255.0);
        MaterialState materialState = display.getRenderer().createMaterialState();
        float opacityAmount = 0.5f;
        materialState.setAmbient(new ColorRGBA(0, 0, 0, opacityAmount));
        materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
        materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
        materialState.setShininess(128.0f);
        materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
        materialState.setEnabled(true);
        materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
        poolNode.setRenderState(materialState);
        Quad quad = new Quad("pool back", (float) swimmingPool.getWidth(), (float) swimmingPool.getHeight());
        quad.setLocalTranslation((float) swimmingPool.getWidth() / 2, (float) swimmingPool.getHeight() / 2, (float) (-swimmingPool.getDepth() - 1E-2));
        poolNode.attachChild(quad);
        return poolNode;
    }

    private void addMouseHandling() {
        component.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                PickResults pickResults = getPickResults();
                for (int i = 0; i < pickResults.getNumber(); i++) {
                    if (i == 0 && pickResults.getPickData(i).getTargetMesh() instanceof ObjectBox) {
                        picked = pickResults.getPickData(i);
                        pickPt = ((ObjectBox) picked.getTargetMesh()).getObject().getPoint2D();
                    }
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (picked != null && picked.getTargetMesh() instanceof ObjectBox) {
                    ObjectBox ob = (ObjectBox) picked.getTargetMesh();
                    ob.getObject().setUserDragging(false);
                }
                picked = null;
            }
        });
        component.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                if (getPickResults().getNumber() > 0) {
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    component.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }

            public void mouseDragged(MouseEvent e) {
                //To change body of implemented methods use File | Settings | File Templates.
                if (picked != null) {
                    double dist = picked.getDistance();
                    Ray initRay = picked.getRay();
                    Ray finalRay = getMouseRay();
                    Vector3f initDir = initRay.getDirection().normalize().mult((float) dist);
                    Vector3f finalDir = finalRay.getDirection().normalize().mult((float) dist);
                    Vector3f initPt = new Vector3f(initRay.getOrigin().getX() + initDir.getX(),
                            initRay.getOrigin().getY() + initDir.getY(),
                            initRay.getOrigin().getZ() + initDir.getZ());
                    Vector3f finalPt = new Vector3f(finalRay.getOrigin().getX() + finalDir.getX(),
                            finalRay.getOrigin().getY() + finalDir.getY(),
                            finalRay.getOrigin().getZ() + finalDir.getZ());
                    double dx = finalPt.getX() - initPt.getX();
                    double dy = finalPt.getY() - initPt.getY();
                    if (picked.getTargetMesh() instanceof ObjectBox) {
                        ObjectBox ob = (ObjectBox) picked.getTargetMesh();
                        ob.getObject().setPosition2D(pickPt.getX() + dx, pickPt.getY() + dy);
                        ob.getObject().setUserDragging(true);
                    }
                }
            }
        });
    }

    private PickResults getPickResults() {
        PickResults pickResults = new BoundingPickResults();
        pickResults.setCheckDistance(true);
        rootNode.findPick(getMouseRay(), pickResults);
        return pickResults;
    }

    private void setupLight() {
        LightState lightState = display.getRenderer().createLightState();
        lightState.setEnabled(true);

        rootNode.setRenderState(lightState);
        lightState.detachAll();

        // our light
        final PointLight light = new PointLight();
        light.setDiffuse(ColorRGBA.white);
        light.setSpecular(ColorRGBA.white);
        light.setLocation(new Vector3f(20.0f, 20.0f, 30.0f));
        light.setEnabled(true);

        // attach the light to a lightState
        lightState.attach(light);
    }

    public BlendState getBlendState() {
        final BlendState alphaState = DisplaySystem.getDisplaySystem().getRenderer().createBlendState();
        alphaState.setBlendEnabled(true);
        alphaState.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        alphaState.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        alphaState.setTestEnabled(true);
        alphaState.setTestFunction(BlendState.TestFunction.GreaterThan);
        alphaState.setEnabled(true);
        return alphaState;
    }

    public void attachChild(Spatial spatial) {
        rootNode.attachChild(spatial);
    }

    public MaterialState getWaterMaterial() {
        MaterialState materialState = display.getRenderer().createMaterialState();
        float opacityAmount = 0.4f;
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
        return materialState;
    }

    class ObjectBox extends Box {
        private Block object;

        public ObjectBox(Block object, String name, Vector3f vector3f, float v, float v1, float v2) {
            super(name, vector3f, v, v1, v2);
            this.object = object;
        }

        public Block getObject() {
            return object;
        }
    }

    private class RectNode extends Node {
        private Block object;

        private RectNode(final Block object) {
            this.object = object;

            final ObjectBox mesh = new ObjectBox(object, object.getName(), new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()),
                    (float) object.getWidth() / 2, (float) object.getHeight() / 2, (float) object.getDepth() / 2);
            mesh.setLocalTranslation(mesh.getLocalTranslation().set(0, 0, -1E-2f));
            mesh.setModelBound(new BoundingBox());

            mesh.updateModelBound();

            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.7f;
            float r = object.getFaceColor().getRed() / 255f;
            float g = object.getFaceColor().getGreen() / 255f;
            float b = object.getFaceColor().getBlue() / 255f;
            materialState.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.1f, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);

            setRenderState(materialState);


            object.addListener(new RectangularObject.Adapter() {
                public void modelChanged() {
                    mesh.setCenter(new Vector3f((float) object.getCenterX(), (float) object.getCenterY(), (float) object.getCenterZ()));
                    mesh.updateGeometry();
                    mesh.updateModelBound();
                    mesh.updateRenderState();
                    //todo update dimensions
                }
            });

            TextureState ts = display.getRenderer().createTextureState();
            try {
                BufferedImage image = ImageLoader.loadBufferedImage("density/images/wall.jpg");
                image = BufferedImageUtils.copyImage(image);
                Graphics2D g2 = image.createGraphics();
                PText text = new PText(new DefaultDecimalFormat("0.00").format(object.getMass()) + " kg");
                text.scale(0.9 * image.getWidth() / text.getFullBounds().getWidth());
                text.fullPaint(new PPaintContext(g2));

                Texture t0 = TextureManager.loadTexture(image,
                        Texture.MinificationFilter.Trilinear,
                        Texture.MagnificationFilter.Bilinear, true);
                t0.setWrap(Texture.WrapMode.Repeat);
                ts.setTexture(t0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            setRenderState(ts);
            attachChild(mesh);//order matters for this call?
        }
    }

    private class CutawayEarthNode extends Node {
        public CutawayEarthNode(DensityModel model) {

            float r = (float) (201 / 255.0);
            float g = (float) (146 / 255.0);
            float b = (float) (81 / 255.0);
            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.8f;
            materialState.setAmbient(new ColorRGBA(0.2f, 0.2f, 0.1f, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);

            setRenderState(materialState);


            float quadWidth = 5;
            float quadHeight = 10;
            final float poolHeight = (float) model.getSwimmingPool().getHeight();
            final float poolWidth = (float) model.getSwimmingPool().getWidth();
            {
                Quad quad = new Quad("cutaway left side", quadWidth, quadHeight);
                quad.setLocalTranslation(-quadWidth / 2, -quadHeight / 2 + poolHeight, 0);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway right side", quadWidth, quadHeight);
                quad.setLocalTranslation(quadWidth / 2 + poolWidth, -quadHeight / 2 + poolHeight, 0);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway lower", poolWidth, quadHeight);
                quad.setLocalTranslation(poolWidth / 2, -quadHeight / 2, 0);
//                final MaterialState state = renderer.createMaterialState();
//                quad.setRenderState(state);
                attachChild(quad);
            }
        }
    }

    private class GrassNode extends Node {
        public GrassNode(DensityModel model) {

            float r = (float) (90 / 255.0);
            float g = (float) (158 / 255.0);
            float b = (float) (48 / 255.0);
            MaterialState materialState = display.getRenderer().createMaterialState();
            float opacityAmount = 0.5f;
            materialState.setAmbient(new ColorRGBA(0, 0, 0, opacityAmount));
            materialState.setDiffuse(new ColorRGBA(r, g, b, opacityAmount));
            materialState.setSpecular(new ColorRGBA(1.0f, 1.0f, 1.0f, opacityAmount));
            materialState.setShininess(128.0f);
            materialState.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.1f, opacityAmount));
            materialState.setEnabled(true);
            materialState.setMaterialFace(MaterialState.MaterialFace.FrontAndBack);
            setRenderState(materialState);


            float quadWidth = 5;
            float quadHeight = 10;
            final float poolHeight = (float) model.getSwimmingPool().getHeight();
            final float poolWidth = (float) model.getSwimmingPool().getWidth();
            final float poolDepth = (float) model.getSwimmingPool().getDepth();
            {
                Quad quad = new Quad("cutaway left side", quadWidth, quadHeight);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));

                quad.setLocalTranslation(-quadWidth / 2, poolHeight, -quadHeight / 2);
                quad.setLocalRotation(m);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway right side", quadWidth, quadHeight);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));

                quad.setLocalTranslation(quadWidth / 2 + poolWidth, poolHeight, -quadHeight / 2);
                quad.setLocalRotation(m);
                attachChild(quad);
            }
            {
                Quad quad = new Quad("cutaway lower grass", poolWidth, quadHeight);
                quad.setLocalTranslation(poolWidth / 2, poolHeight, -poolDepth - quadHeight / 2);
                Matrix3f m = new Matrix3f();
                m.fromAngleAxis((float) (Math.PI / 2), new Vector3f(1, 0, 0));
                quad.setLocalRotation(m);
                attachChild(quad);
            }

            setModelBound(new BoundingBox());

            updateModelBound();

            updateRenderState();
        }
    }

    private class VolumeReadout extends Node {
        private ModelComponents.DisplayDimensions displayDimensions;
        private PiccoloNode element;
        private float localScale = 2.2f;
        private PText text;

        public VolumeReadout(DensityModel model, final ModelComponents.DisplayDimensions displayDimensions) {
            this.displayDimensions = displayDimensions;
            displayDimensions.addListener(new Unit() {
                public void update() {
                    updateVisible();
                }
            });
            PNode node = new PNode();
            text = new PText(model.getWaterHeightText());
            text.setFont(new PhetFont(15, true));

            node.addChild(text);
            final ArrowNode arrowNode = new ArrowNode(new Point2D.Double(0, 0), new Point2D.Double(35, 0), 15, 15, 7);
            arrowNode.setPaint(Color.blue);
            arrowNode.setOffset(text.getFullBounds().getMaxX() + 2, text.getFullBounds().getCenterY() - arrowNode.getFullBounds().getHeight() / 2 - arrowNode.getFullBounds().getY());
            node.addChild(arrowNode);

            final Rectangle2D expanded = RectangleUtils.expand(node.getFullBounds(), 5, 5);
            PhetPPath background = new PhetPPath(new RoundRectangle2D.Double(expanded.getX(), expanded.getY(), expanded.getWidth(), expanded.getHeight(), 10, 10), new Color(0.7f, 0.6f, 0.7f, 0.7f), new BasicStroke(1), Color.gray);
            PNode container = new PNode();
            container.addChild(background);
            container.addChild(node);
            element = new PiccoloNode("readout", container);

            setLocalScale(localScale);
            updateLocation();

            model.getWater().addListener(new RectangularObject.Adapter() {
                public void modelChanged() {
                    updateLocation();
                }
            });

            updateVisible();
        }

        private void updateLocation() {
            setLocalTranslation((float) model.getSwimmingPool().getMaxX() - localScale / 2.0f, (float) model.getWater().getMaxY(), (float) (model.getWater().getZ() + 1E-3));
            if (!text.getText().equals(model.getWaterHeightText())) {
                text.setText(model.getWaterHeightText());
                element.repaint();
            }
        }

        private void updateVisible() {
            if (displayDimensions.isDisplay() && !hasChild(element)) {
                attachChild(element);
            } else {
                if (hasChild(element)) {
                    detachChild(element);
                }
            }
        }
    }

    public static class PiccoloNode extends Node {
        private Java2DQuad java2DQuad;

        public PiccoloNode(String name, final PNode node) {
            java2DQuad = new Java2DQuad(name + ".java2dquad", (int) node.getFullBounds().width, (int) node.getFullBounds().height) {
                protected void paint(Graphics2D g2) {
                    g2.translate(-node.getFullBounds().x, -node.getFullBounds().y);
                    node.fullPaint(new PPaintContext(g2));
                }
            };
            setLocalScale((float) (1.0 / node.getFullBounds().width));
            java2DQuad.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
            java2DQuad.updateRenderState();
            attachChild(java2DQuad);
        }

        public void repaint() {
            java2DQuad.repaint();
        }
    }
}