package jme3test.terrain;

import java.util.ArrayList;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.HeightfieldCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.terrain.geomipmap.TerrainGrid;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.FractalHeightMapGrid;
import com.jme3.terrain.heightmap.ImageBasedHeightMapGrid;
import com.jme3.terrain.heightmap.Namer;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import org.novyon.noise.ShaderUtils;
import org.novyon.noise.basis.FilteredBasis;
import org.novyon.noise.filter.IterativeFilter;
import org.novyon.noise.filter.OptimizedErode;
import org.novyon.noise.filter.PerturbFilter;
import org.novyon.noise.filter.SmoothFilter;
import org.novyon.noise.fractal.FractalSum;
import org.novyon.noise.modulator.NoiseModulator;

public class TerrainFractalGridTest extends SimpleApplication {

    private Material mat_terrain;
    private TerrainGrid terrain;
    private float grassScale = 64;
    private float dirtScale = 16;
    private float rockScale = 128;
    private boolean usePhysics = true;

    public static void main(final String[] args) {
        TerrainFractalGridTest app = new TerrainFractalGridTest();
        app.start();
    }
    private CharacterControl player3;
    private FractalSum base;
    private PerturbFilter perturb;
    private OptimizedErode therm;
    private SmoothFilter smooth;
    private IterativeFilter iterate;

    @Override
    public void simpleInitApp() {
        this.flyCam.setMoveSpeed(100f);
        ScreenshotAppState state = new ScreenshotAppState();
        this.stateManager.attach(state);

        // TERRAIN TEXTURE material
        this.mat_terrain = new Material(this.assetManager, "Common/MatDefs/Terrain/HeightBasedTerrain.j3md");

        // Parameters to material:
        // regionXColorMap: X = 1..4 the texture that should be appliad to state X
        // regionX: a Vector3f containing the following information:
        //      regionX.x: the start height of the region
        //      regionX.y: the end height of the region
        //      regionX.z: the texture scale for the region
        //  it might not be the most elegant way for storing these 3 values, but it packs the data nicely :)
        // slopeColorMap: the texture to be used for cliffs, and steep mountain sites
        // slopeTileFactor: the texture scale for slopes
        // terrainSize: the total size of the terrain (used for scaling the texture)
        // GRASS texture
        Texture grass = this.assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        grass.setWrap(WrapMode.Repeat);
        this.mat_terrain.setTexture("region1ColorMap", grass);
        this.mat_terrain.setVector3("region1", new Vector3f(88, 200, this.grassScale));

        // DIRT texture
        Texture dirt = this.assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        dirt.setWrap(WrapMode.Repeat);
        this.mat_terrain.setTexture("region2ColorMap", dirt);
        this.mat_terrain.setVector3("region2", new Vector3f(0, 90, this.dirtScale));

        // ROCK texture
        Texture rock = this.assetManager.loadTexture("Textures/Terrain/Rock2/rock.jpg");
        rock.setWrap(WrapMode.Repeat);
        this.mat_terrain.setTexture("region3ColorMap", rock);
        this.mat_terrain.setVector3("region3", new Vector3f(198, 260, this.rockScale));

        this.mat_terrain.setTexture("region4ColorMap", rock);
        this.mat_terrain.setVector3("region4", new Vector3f(198, 260, this.rockScale));

        this.mat_terrain.setTexture("slopeColorMap", rock);
        this.mat_terrain.setFloat("slopeTileFactor", 32);

        this.mat_terrain.setFloat("terrainSize", 513);

        this.base = new FractalSum();
        this.base.setRoughness(0.7f);
        this.base.setFrequency(1.0f);
        this.base.setAmplitude(1.0f);
        this.base.setLacunarity(2.12f);
        this.base.setOctaves(8);
        this.base.setScale(0.02125f);
        this.base.addModulator(new NoiseModulator() {

            @Override
            public float value(float... in) {
                return ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1);
            }
        });

        FilteredBasis ground = new FilteredBasis(this.base);

        this.perturb = new PerturbFilter();
        this.perturb.setMagnitude(0.119f);

        this.therm = new OptimizedErode();
        this.therm.setRadius(5);
        this.therm.setTalus(0.011f);

        this.smooth = new SmoothFilter();
        this.smooth.setRadius(1);
        this.smooth.setEffect(0.7f);

        this.iterate = new IterativeFilter();
        this.iterate.addPreFilter(this.perturb);
        this.iterate.addPostFilter(this.smooth);
        this.iterate.setFilter(this.therm);
        this.iterate.setIterations(1);

        ground.addPreFilter(this.iterate);

        this.terrain = new TerrainGrid("terrain", 65, 257, new FractalHeightMapGrid(ground, null, 256f));

        this.terrain.setMaterial(this.mat_terrain);
        this.terrain.setLocalTranslation(0, 0, 0);
        this.terrain.setLocalScale(2f, 1f, 2f);
        this.terrain.initialize(Vector3f.ZERO);
        this.rootNode.attachChild(this.terrain);

        List<Camera> cameras = new ArrayList<Camera>();
        cameras.add(this.getCamera());
        TerrainLodControl control = new TerrainLodControl(this.terrain, cameras);
        this.terrain.addControl(control);

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);


        this.getCamera().setLocation(new Vector3f(0, 256, 0));

        this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

        if (usePhysics) {
            RigidBodyControl body = new RigidBodyControl(new HeightfieldCollisionShape(terrain.getHeightMap(), terrain.getLocalScale()), 0);
            terrain.addControl(body);
            bulletAppState.getPhysicsSpace().add(terrain);
            CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(0.5f, 1.8f, 1);
            this.player3 = new CharacterControl(capsuleShape, 0.5f);
            this.player3.setJumpSpeed(20);
            this.player3.setFallSpeed(30);
            this.player3.setGravity(30);

            this.player3.setPhysicsLocation(new Vector3f(0, 256, 0));

            bulletAppState.getPhysicsSpace().add(this.player3);
        }
        this.initKeys();
    }

    private void initKeys() {
        // You can map one or several inputs to one named action
        this.inputManager.addMapping("Lefts", new KeyTrigger(KeyInput.KEY_A));
        this.inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
        this.inputManager.addMapping("Ups", new KeyTrigger(KeyInput.KEY_W));
        this.inputManager.addMapping("Downs", new KeyTrigger(KeyInput.KEY_S));
        this.inputManager.addMapping("Jumps", new KeyTrigger(KeyInput.KEY_SPACE));
        this.inputManager.addListener(this.actionListener, "Lefts");
        this.inputManager.addListener(this.actionListener, "Rights");
        this.inputManager.addListener(this.actionListener, "Ups");
        this.inputManager.addListener(this.actionListener, "Downs");
        this.inputManager.addListener(this.actionListener, "Jumps");
    }

    private boolean left;
    private boolean right;
    private boolean up;
    private boolean down;
    private final ActionListener actionListener = new ActionListener() {

        @Override
        public void onAction(final String name, final boolean keyPressed, final float tpf) {
            if (name.equals("Lefts")) {
                if (keyPressed) {
                    TerrainFractalGridTest.this.left = true;
                } else {
                    TerrainFractalGridTest.this.left = false;
                }
            } else if (name.equals("Rights")) {
                if (keyPressed) {
                    TerrainFractalGridTest.this.right = true;
                } else {
                    TerrainFractalGridTest.this.right = false;
                }
            } else if (name.equals("Ups")) {
                if (keyPressed) {
                    TerrainFractalGridTest.this.up = true;
                } else {
                    TerrainFractalGridTest.this.up = false;
                }
            } else if (name.equals("Downs")) {
                if (keyPressed) {
                    TerrainFractalGridTest.this.down = true;
                } else {
                    TerrainFractalGridTest.this.down = false;
                }
            } else if (name.equals("Jumps")) {
                TerrainFractalGridTest.this.player3.jump();
            }
        }
    };
    private final Vector3f walkDirection = new Vector3f();

    @Override
    public void simpleUpdate(final float tpf) {
        Vector3f camDir = this.cam.getDirection().clone().multLocal(0.6f);
        Vector3f camLeft = this.cam.getLeft().clone().multLocal(0.4f);
        this.walkDirection.set(0, 0, 0);
        if (this.left) {
            this.walkDirection.addLocal(camLeft);
        }
        if (this.right) {
            this.walkDirection.addLocal(camLeft.negate());
        }
        if (this.up) {
            this.walkDirection.addLocal(camDir);
        }
        if (this.down) {
            this.walkDirection.addLocal(camDir.negate());
        }

        if (usePhysics) {
            this.player3.setWalkDirection(this.walkDirection);
            this.cam.setLocation(this.player3.getPhysicsLocation());
        }
    }
}
