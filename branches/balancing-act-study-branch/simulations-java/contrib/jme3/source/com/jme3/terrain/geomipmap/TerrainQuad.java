/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.terrain.geomipmap;

import com.jme3.material.Material;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.Collidable;
import com.jme3.collision.CollisionResults;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.terrain.ProgressMonitor;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.lodcalc.LodCalculatorFactory;
import com.jme3.terrain.geomipmap.lodcalc.LodDistanceCalculatorFactory;
import com.jme3.terrain.geomipmap.picking.BresenhamTerrainPicker;
import com.jme3.terrain.geomipmap.picking.TerrainPickData;
import com.jme3.terrain.geomipmap.picking.TerrainPicker;
import com.jme3.util.BufferUtils;
import com.jme3.util.TangentBinormalGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A terrain quad is a node in the quad tree of the terrain system.
 * The root terrain quad will be the only one that receives the update() call every frame
 * and it will determine if there has been any LOD change.
 *
 * The leaves of the terrain quad tree are Terrain Patches. These have the real geometry mesh.
 *
 * @author Brent Owens
 */
public class TerrainQuad extends Node implements Terrain {

    protected Vector2f offset;

    protected int totalSize; // the size of this entire terrain tree (on one side)

    protected int size; // size of this quad, can be between totalSize and patchSize

    protected int patchSize; // size of the individual patches

    protected Vector3f stepScale;

    protected float offsetAmount;

    protected int quadrant = 1; // 1=upper left, 2=lower left, 3=upper right, 4=lower right

    protected LodCalculatorFactory lodCalculatorFactory;


    protected List<Vector3f> lastCameraLocations; // used for LOD calc
    private boolean lodCalcRunning = false;
    private int maxLod = -1;
    private HashMap<String,UpdatedTerrainPatch> updatedPatches;
    private final Object updatePatchesLock = new Object();
    private BoundingBox affectedAreaBBox; // only set in the root quad

    private TerrainPicker picker;


    protected ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        public Thread newThread(Runnable r) {
            Thread th = new Thread(r);
            th.setName("jME Terrain Thread");
            th.setDaemon(true);
            return th;
        }
    });


    public TerrainQuad() {
        super("Terrain");
    }

    public TerrainQuad(String name, int patchSize, int totalSize, float[] heightMap) {
        this(name, patchSize, totalSize, heightMap, null);
    }

    public TerrainQuad(String name, int patchSize, int totalSize, float[] heightMap, LodCalculatorFactory lodCalculatorFactory) {
        this(name, patchSize, totalSize, Vector3f.UNIT_XYZ, heightMap, lodCalculatorFactory);
    }

    public TerrainQuad(String name, int patchSize, int size, Vector3f scale, float[] heightMap, LodCalculatorFactory lodCalculatorFactory) {
        this(name, patchSize, size, scale, heightMap, size, new Vector2f(), 0, lodCalculatorFactory);
        affectedAreaBBox = new BoundingBox(new Vector3f(0,0,0), size, Float.MAX_VALUE, size);
        fixNormalEdges(affectedAreaBBox);
        addControl(new NormalRecalcControl(this));
    }

    protected TerrainQuad(String name, int patchSize, int size,
                            Vector3f stepScale, float[] heightMap, int totalSize,
                            Vector2f offset, float offsetAmount,
                            LodCalculatorFactory lodCalculatorFactory)
    {
        super(name);
        if (!FastMath.isPowerOfTwo(size - 1)) {
            throw new RuntimeException("size given: " + size + "  Terrain quad sizes may only be (2^N + 1)");
        }

        if (heightMap == null)
            heightMap = generateDefaultHeightMap(size);

        this.offset = offset;
        this.offsetAmount = offsetAmount;
        this.totalSize = totalSize;
        this.size = size;
        this.patchSize = patchSize;
        this.stepScale = stepScale;
        this.lodCalculatorFactory = lodCalculatorFactory;
        split(patchSize, heightMap);
    }

    public void setLodCalculatorFactory(LodCalculatorFactory lodCalculatorFactory) {
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).setLodCalculatorFactory(lodCalculatorFactory);
                } else if (child instanceof TerrainPatch) {
                    ((TerrainPatch) child).setLodCalculator(lodCalculatorFactory.createCalculator((TerrainPatch) child));
                }
            }
        }
    }


    /**
     * Create just a flat heightmap
     */
    private float[] generateDefaultHeightMap(int size) {
        float[] heightMap = new float[size*size];
        return heightMap;
    }

     /**
      * Call from the update() method of a terrain controller to update
      * the LOD values of each patch.
      * This will perform the geometry calculation in a background thread and
      * do the actual update on the opengl thread.
      */
    public void update(List<Vector3f> locations) {
        updateLOD(locations);
    }

    /**
     * update the normals if there were any height changes recently.
     * Should only be called on the root quad
     */
    protected void updateNormals() {

        if (needToRecalculateNormals()) {
            //TODO background-thread this if it ends up being expensive
            fixNormals(affectedAreaBBox); // the affected patches
            fixNormalEdges(affectedAreaBBox); // the edges between the patches

            setNormalRecalcNeeded(null); // set to false
        }
    }

    // do all of the LOD calculations
    protected void updateLOD(List<Vector3f> locations) {
        // update any existing ones that need updating
        updateQuadLODs();

        if (lastCameraLocations != null) {
            if (lastCameraLocationsTheSame(locations))
                return; // don't update if in same spot
            else
                lastCameraLocations = cloneVectorList(locations);
        }
        else {
            lastCameraLocations = cloneVectorList(locations);
            return;
        }

        if (isLodCalcRunning()) {
            return;
        }

        if (getParent() instanceof TerrainQuad) {
            return; // we just want the root quad to perform this.
        }

        UpdateLOD updateLodThread = new UpdateLOD(locations);
        executor.execute(updateLodThread);
    }

    private synchronized boolean isLodCalcRunning() {
        return lodCalcRunning;
    }

    private synchronized void setLodCalcRunning(boolean running) {
        lodCalcRunning = running;
    }

    private List<Vector3f> cloneVectorList(List<Vector3f> locations) {
        List<Vector3f> cloned = new ArrayList<Vector3f>();
        for(Vector3f l : locations)
            cloned.add(l.clone());
        return cloned;
    }

    private boolean lastCameraLocationsTheSame(List<Vector3f> locations) {
        boolean theSame = true;
        for (Vector3f l : locations) {
            for (Vector3f v : lastCameraLocations) {
                if (!v.equals(l) ) {
                    theSame = false;
                    return false;
                }
            }
        }
        return theSame;
    }

    private int collideWithRay(Ray ray, CollisionResults results) {
        if (picker == null)
            picker = new BresenhamTerrainPicker(this);

        Vector3f intersection = picker.getTerrainIntersection(ray, results);
        if (intersection != null)
            return 1;
        else
            return 0;
    }

    public void generateEntropy(ProgressMonitor progressMonitor) {
        // only check this on the root quad
        if (isRootQuad())
            if (progressMonitor != null) {
                int numCalc = (totalSize-1)/(patchSize-1); // make it an even number
                progressMonitor.setMonitorMax(numCalc*numCalc);
            }

        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                        ((TerrainQuad) child).generateEntropy(progressMonitor);
                } else if (child instanceof TerrainPatch) {
                    ((TerrainPatch) child).generateLodEntropies();
                    if (progressMonitor != null)
                        progressMonitor.incrementProgress(1);
                }
            }
        }

        // only do this on the root quad
        if (isRootQuad())
            if (progressMonitor != null)
                progressMonitor.progressComplete();
    }

    protected boolean isRootQuad() {
        return (getParent() != null && !(getParent() instanceof TerrainQuad) );
    }

    public Material getMaterial() {
        // get the material from one of the children. They all share the same material
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    return ((TerrainQuad)child).getMaterial();
                } else if (child instanceof TerrainPatch) {
                    return ((TerrainPatch)child).getMaterial();
                }
            }
        }
        return null;
    }

    public float getTextureCoordinateScale() {
        return 1f/(float)totalSize;
    }

    /**
     * Calculates the LOD of all child terrain patches.
     */
    private class UpdateLOD implements Runnable {
        private List<Vector3f> camLocations;

        UpdateLOD(List<Vector3f> location) {
            camLocations = location;
        }

        public void run() {
            long start = System.currentTimeMillis();
            if (isLodCalcRunning()) {
                //System.out.println("thread already running");
                return;
            }
            //System.out.println("spawned thread "+toString());
            setLodCalcRunning(true);

            // go through each patch and calculate its LOD based on camera distance
            HashMap<String,UpdatedTerrainPatch> updated = new HashMap<String,UpdatedTerrainPatch>();
            boolean lodChanged = calculateLod(camLocations, updated); // 'updated' gets populated here

            if (!lodChanged) {
                // not worth updating anything else since no one's LOD changed
                setLodCalcRunning(false);
                return;
            }
            // then calculate its neighbour LOD values for seaming in the shader
            findNeighboursLod(updated);

            fixEdges(updated); // 'updated' can get added to here

            reIndexPages(updated);

            setUpdateQuadLODs(updated); // set back to main ogl thread

            setLodCalcRunning(false);
            //double duration = (System.currentTimeMillis()-start);
            //System.out.println("terminated in "+duration);
        }
    }

    private void setUpdateQuadLODs(HashMap<String,UpdatedTerrainPatch> updated) {
        synchronized (updatePatchesLock) {
            updatedPatches = updated;
        }
    }

    /**
     * Back on the ogl thread: update the terrain patch geometries
     * @param updatedPatches to be updated
     */
    private void updateQuadLODs() {
        synchronized (updatePatchesLock) {
            
            if (updatedPatches == null || updatedPatches.isEmpty())
                return;

            // do the actual geometry update here
            for (UpdatedTerrainPatch utp : updatedPatches.values()) {
                utp.updateAll();
            }

            updatedPatches.clear();
        }
    }

    protected boolean calculateLod(List<Vector3f> location, HashMap<String,UpdatedTerrainPatch> updates) {

        boolean lodChanged = false;

        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    boolean b = ((TerrainQuad) child).calculateLod(location, updates);
                    if (b)
                        lodChanged = true;
                } else if (child instanceof TerrainPatch) {
                    boolean b = ((TerrainPatch) child).calculateLod(location, updates);
                    if (b)
                        lodChanged = true;
                }
            }
        }

        return lodChanged;
    }

    protected synchronized void findNeighboursLod(HashMap<String,UpdatedTerrainPatch> updated) {
        if (children != null) {
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).findNeighboursLod(updated);
                } else if (child instanceof TerrainPatch) {

                    TerrainPatch patch = (TerrainPatch) child;
                    if (!patch.searchedForNeighboursAlready) {
                        // set the references to the neighbours
                        patch.rightNeighbour = findRightPatch(patch);
                        patch.bottomNeighbour = findDownPatch(patch);
                        patch.leftNeighbour = findLeftPatch(patch);
                        patch.topNeighbour = findTopPatch(patch);
                        patch.searchedForNeighboursAlready = true;
                    }
                    TerrainPatch right = patch.rightNeighbour;
                    TerrainPatch down = patch.bottomNeighbour;

                    UpdatedTerrainPatch utp = updated.get(patch.getName());
                    if (utp == null) {
                        utp = new UpdatedTerrainPatch(patch, patch.lod);
                        updated.put(utp.getName(), utp);
                    }

                    if (right != null) {
                        UpdatedTerrainPatch utpR = updated.get(right.getName());
                        if (utpR == null) {
                            utpR = new UpdatedTerrainPatch(right, right.lod);
                            updated.put(utpR.getName(), utpR);
                        }

                        utp.setRightLod(utpR.getNewLod());
                        utpR.setLeftLod(utp.getNewLod());
                    }
                    if (down != null) {
                        UpdatedTerrainPatch utpD = updated.get(down.getName());
                        if (utpD == null) {
                            utpD = new UpdatedTerrainPatch(down, down.lod);
                            updated.put(utpD.getName(), utpD);
                        }

                        utp.setBottomLod(utpD.getNewLod());
                        utpD.setTopLod(utp.getNewLod());
                    }

                }
            }
        }
    }

    /**
     * Find any neighbours that should have their edges seamed because another neighbour
     * changed its LOD to a greater value (less detailed)
     */
    protected synchronized void fixEdges(HashMap<String,UpdatedTerrainPatch> updated) {
        if (children != null) {
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).fixEdges(updated);
                } else if (child instanceof TerrainPatch) {
                    TerrainPatch patch = (TerrainPatch) child;
                    UpdatedTerrainPatch utp = updated.get(patch.getName());

                    if(utp.lodChanged()) {
                        if (!patch.searchedForNeighboursAlready) {
                            // set the references to the neighbours
                            patch.rightNeighbour = findRightPatch(patch);
                            patch.bottomNeighbour = findDownPatch(patch);
                            patch.leftNeighbour = findLeftPatch(patch);
                            patch.topNeighbour = findTopPatch(patch);
                            patch.searchedForNeighboursAlready = true;
                        }
                        TerrainPatch right = patch.rightNeighbour;
                        TerrainPatch down = patch.bottomNeighbour;
                        TerrainPatch top = patch.topNeighbour;
                        TerrainPatch left = patch.leftNeighbour;
                        if (right != null) {
                            UpdatedTerrainPatch utpR = updated.get(right.getName());
                            if (utpR == null) {
                                utpR = new UpdatedTerrainPatch(right, right.lod);
                                updated.put(utpR.getName(), utpR);
                            }
                            utpR.setFixEdges(true);
                        }
                        if (down != null) {
                            UpdatedTerrainPatch utpD = updated.get(down.getName());
                            if (utpD == null) {
                                utpD = new UpdatedTerrainPatch(down, down.lod);
                                updated.put(utpD.getName(), utpD);
                            }
                            utpD.setFixEdges(true);
                        }
                        if (top != null){
                            UpdatedTerrainPatch utpT = updated.get(top.getName());
                            if (utpT == null) {
                                utpT = new UpdatedTerrainPatch(top, top.lod);
                                updated.put(utpT.getName(), utpT);
                            }
                            utpT.setFixEdges(true);
                        }
                        if (left != null){
                            UpdatedTerrainPatch utpL = updated.get(left.getName());
                            if (utpL == null) {
                                utpL = new UpdatedTerrainPatch(left, left.lod);
                                updated.put(utpL.getName(), utpL);
                            }
                            utpL.setFixEdges(true);
                        }
                    }
                }
            }
        }
    }

    protected synchronized void reIndexPages(HashMap<String,UpdatedTerrainPatch> updated) {
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).reIndexPages(updated);
                } else if (child instanceof TerrainPatch) {
                    ((TerrainPatch) child).reIndexGeometry(updated);
                }
            }
        }
    }

    /**
     * <code>split</code> divides the heightmap data for four children. The
     * children are either pages or blocks. This is dependent on the size of the
     * children. If the child's size is less than or equal to the set block
     * size, then blocks are created, otherwise, pages are created.
     *
     * @param blockSize
     *			the blocks size to test against.
     * @param heightMap
     *			the height data.
     */
    protected void split(int blockSize, float[] heightMap) {
        if ((size >> 1) + 1 <= blockSize) {
            createQuadPatch(heightMap);
        } else {
            createQuad(blockSize, heightMap);
        }

    }

    /**
     * <code>createQuadPage</code> generates four new pages from this page.
     */
    protected void createQuad(int blockSize, float[] heightMap) {
        // create 4 terrain pages
        int quarterSize = size >> 2;

        int split = (size + 1) >> 1;

        Vector2f tempOffset = new Vector2f();
        offsetAmount += quarterSize;

        if (lodCalculatorFactory == null)
            lodCalculatorFactory = new LodDistanceCalculatorFactory(); // set a default one

        // 1 upper left
        float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

        Vector3f origin1 = new Vector3f(-quarterSize * stepScale.x, 0,
                        -quarterSize * stepScale.z);

        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin1.x;
        tempOffset.y += origin1.z;

        TerrainQuad page1 = new TerrainQuad(getName() + "Quad1", blockSize,
                        split, stepScale, heightBlock1, totalSize, tempOffset,
                        offsetAmount, lodCalculatorFactory);
        page1.setLocalTranslation(origin1);
        page1.quadrant = 1;
        this.attachChild(page1);

        // 2 lower left
        float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1,
                        split);

        Vector3f origin2 = new Vector3f(-quarterSize * stepScale.x, 0,
                        quarterSize * stepScale.z);

        tempOffset = new Vector2f();
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin2.x;
        tempOffset.y += origin2.z;

        TerrainQuad page2 = new TerrainQuad(getName() + "Quad2", blockSize,
                        split, stepScale, heightBlock2, totalSize, tempOffset,
                        offsetAmount, lodCalculatorFactory);
        page2.setLocalTranslation(origin2);
        page2.quadrant = 2;
        this.attachChild(page2);

        // 3 upper right
        float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0,
                        split);

        Vector3f origin3 = new Vector3f(quarterSize * stepScale.x, 0,
                        -quarterSize * stepScale.z);

        tempOffset = new Vector2f();
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin3.x;
        tempOffset.y += origin3.z;

        TerrainQuad page3 = new TerrainQuad(getName() + "Quad3", blockSize,
                        split, stepScale, heightBlock3, totalSize, tempOffset,
                        offsetAmount, lodCalculatorFactory);
        page3.setLocalTranslation(origin3);
        page3.quadrant = 3;
        this.attachChild(page3);
        // //
        // 4 lower right
        float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1,
                        split - 1, split);

        Vector3f origin4 = new Vector3f(quarterSize * stepScale.x, 0,
                        quarterSize * stepScale.z);

        tempOffset = new Vector2f();
        tempOffset.x = offset.x;
        tempOffset.y = offset.y;
        tempOffset.x += origin4.x;
        tempOffset.y += origin4.z;

        TerrainQuad page4 = new TerrainQuad(getName() + "Quad4", blockSize,
                        split, stepScale, heightBlock4, totalSize, tempOffset,
                        offsetAmount, lodCalculatorFactory);
        page4.setLocalTranslation(origin4);
        page4.quadrant = 4;
        this.attachChild(page4);

    }

    public void generateDebugTangents(Material mat) {
        for (int x = children.size(); --x >= 0;) {
            Spatial child = children.get(x);
            if (child instanceof TerrainQuad) {
                ((TerrainQuad)child).generateDebugTangents(mat);
            } else if (child instanceof TerrainPatch) {
                Geometry debug = new Geometry( "Debug " + name,
                    TangentBinormalGenerator.genTbnLines( ((TerrainPatch)child).getMesh(), 0.1f));
                attachChild(debug);
                debug.setLocalTranslation(child.getLocalTranslation());
                debug.setCullHint(CullHint.Never);
                debug.setMaterial(mat);
            }
        }
    }

    /**
     * <code>createQuadBlock</code> creates four child blocks from this page.
     */
    protected void createQuadPatch(float[] heightMap) {
        // create 4 terrain blocks
        int quarterSize = size >> 2;
        int halfSize = size >> 1;
        int split = (size + 1) >> 1;

        if (lodCalculatorFactory == null)
            lodCalculatorFactory = new LodDistanceCalculatorFactory(); // set a default one

        offsetAmount += quarterSize;

        // 1 upper left
        float[] heightBlock1 = createHeightSubBlock(heightMap, 0, 0, split);

        Vector3f origin1 = new Vector3f(-halfSize * stepScale.x, 0, -halfSize
                        * stepScale.z);

        Vector2f tempOffset1 = new Vector2f();
        tempOffset1.x = offset.x;
        tempOffset1.y = offset.y;
        tempOffset1.x += origin1.x / 2;
        tempOffset1.y += origin1.z / 2;

        TerrainPatch patch1 = new TerrainPatch(getName() + "Patch1", split,
                        stepScale, heightBlock1, origin1, totalSize, tempOffset1,
                        offsetAmount);
        patch1.setQuadrant((short) 1);
        this.attachChild(patch1);
        patch1.setModelBound(new BoundingBox());
        patch1.updateModelBound();
        patch1.setLodCalculator(lodCalculatorFactory);
        TangentBinormalGenerator.generate(patch1);

        // 2 lower left
        float[] heightBlock2 = createHeightSubBlock(heightMap, 0, split - 1,
                        split);

        Vector3f origin2 = new Vector3f(-halfSize * stepScale.x, 0, 0);

        Vector2f tempOffset2 = new Vector2f();
        tempOffset2.x = offset.x;
        tempOffset2.y = offset.y;
        tempOffset2.x += origin1.x / 2;
        tempOffset2.y += quarterSize * stepScale.z;

        TerrainPatch patch2 = new TerrainPatch(getName() + "Patch2", split,
                        stepScale, heightBlock2, origin2, totalSize, tempOffset2,
                        offsetAmount);
        patch2.setQuadrant((short) 2);
        this.attachChild(patch2);
        patch2.setModelBound(new BoundingBox());
        patch2.updateModelBound();
        patch2.setLodCalculator(lodCalculatorFactory);
        TangentBinormalGenerator.generate(patch2);

        // 3 upper right
        float[] heightBlock3 = createHeightSubBlock(heightMap, split - 1, 0,
                        split);

        Vector3f origin3 = new Vector3f(0, 0, -halfSize * stepScale.z);

        Vector2f tempOffset3 = new Vector2f();
        tempOffset3.x = offset.x;
        tempOffset3.y = offset.y;
        tempOffset3.x += quarterSize * stepScale.x;
        tempOffset3.y += origin3.z / 2;

        TerrainPatch patch3 = new TerrainPatch(getName() + "Patch3", split,
                        stepScale, heightBlock3, origin3, totalSize, tempOffset3,
                        offsetAmount);
        patch3.setQuadrant((short) 3);
        this.attachChild(patch3);
        patch3.setModelBound(new BoundingBox());
        patch3.updateModelBound();
        patch3.setLodCalculator(lodCalculatorFactory);
        TangentBinormalGenerator.generate(patch3);

        // 4 lower right
        float[] heightBlock4 = createHeightSubBlock(heightMap, split - 1,
                        split - 1, split);

        Vector3f origin4 = new Vector3f(0, 0, 0);

        Vector2f tempOffset4 = new Vector2f();
        tempOffset4.x = offset.x;
        tempOffset4.y = offset.y;
        tempOffset4.x += quarterSize * stepScale.x;
        tempOffset4.y += quarterSize * stepScale.z;

        TerrainPatch patch4 = new TerrainPatch(getName() + "Patch4", split,
                        stepScale, heightBlock4, origin4, totalSize, tempOffset4,
                        offsetAmount);
        patch4.setQuadrant((short) 4);
        this.attachChild(patch4);
        patch4.setModelBound(new BoundingBox());
        patch4.updateModelBound();
        patch4.setLodCalculator(lodCalculatorFactory);
        TangentBinormalGenerator.generate(patch4);
    }

    public float[] createHeightSubBlock(float[] heightMap, int x,
                    int y, int side) {
        float[] rVal = new float[side * side];
        int bsize = (int) FastMath.sqrt(heightMap.length);
        int count = 0;
        for (int i = y; i < side + y; i++) {
            for (int j = x; j < side + x; j++) {
                if (j < bsize && i < bsize)
                    rVal[count] = heightMap[j + (i * bsize)];
                count++;
            }
        }
        return rVal;
    }

    /**
     * A handy method that will attach all bounding boxes of this terrain
     * to the node you supply.
     * Useful to visualize the bounding boxes when debugging.
     *
     * @param parent that will get the bounding box shapes of the terrain attached to
     */
    public void attachBoundChildren(Node parent) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainQuad) {
                ((TerrainQuad) getChild(i)).attachBoundChildren(parent);
            } else if (this.getChild(i) instanceof TerrainPatch) {
                BoundingVolume bv = getChild(i).getWorldBound();
                if (bv instanceof BoundingBox) {
                    attachBoundingBox((BoundingBox)bv, parent);
                }
            }
        }
        BoundingVolume bv = getWorldBound();
        if (bv instanceof BoundingBox) {
            attachBoundingBox((BoundingBox)bv, parent);
        }
    }

    /**
     * used by attachBoundChildren()
     */
    private void attachBoundingBox(BoundingBox bb, Node parent) {
        WireBox wb = new WireBox(bb.getXExtent(), bb.getYExtent(), bb.getZExtent());
        Geometry g = new Geometry();
        g.setMesh(wb);
        g.setLocalTranslation(bb.getCenter());
        parent.attachChild(g);
    }

    /**
     * Signal if the normal vectors for the terrain need to be recalculated.
     * Does this by looking at the affectedAreaBBox bounding box. If the bbox
     * exists already, then it will grow the box to fit the new changedPoint.
     * If the affectedAreaBBox is null, then it will create one of unit size.
     *
     * @param needToRecalculateNormals if null, will cause needToRecalculateNormals() to return false
     */
    protected void setNormalRecalcNeeded(Vector2f changedPoint) {
        if (changedPoint == null) { // set needToRecalculateNormals() to false
            affectedAreaBBox = null;
            return;
        }

        if (affectedAreaBBox == null) {
            affectedAreaBBox = new BoundingBox(new Vector3f(changedPoint.x, 0, changedPoint.y), 0.5f, Float.MAX_VALUE, 0.5f); // unit length
        } else {
            // adjust size of box to be larger
            affectedAreaBBox.mergeLocal(new BoundingBox(new Vector3f(changedPoint.x, 0, changedPoint.y), 0.5f, Float.MAX_VALUE, 0.5f));
        }
    }

    protected boolean needToRecalculateNormals() {
        return affectedAreaBBox != null;
    }

    public float getHeightmapHeight(Vector2f xz) {
        // offset
        int halfSize = totalSize / 2;
        int x = Math.round((xz.x / getLocalScale().x) + halfSize);
        int z = Math.round((xz.y / getLocalScale().z) + halfSize);

        return getHeightmapHeight(x, z);
    }

    /**
     * This will just get the heightmap value at the supplied point,
     * not an interpolated (actual) height value.
     */
    protected float getHeightmapHeight(int x, int z) {
        int quad = findQuadrant(x, z);
        int split = (size + 1) >> 1;
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial spat = children.get(i);
                int col = x;
                int row = z;
                boolean match = false;

                // get the childs quadrant
                int childQuadrant = 0;
                if (spat instanceof TerrainQuad) {
                    childQuadrant = ((TerrainQuad) spat).getQuadrant();
                } else if (spat instanceof TerrainPatch) {
                    childQuadrant = ((TerrainPatch) spat).getQuadrant();
                }

                if (childQuadrant == 1 && (quad & 1) != 0) {
                    match = true;
                } else if (childQuadrant == 2 && (quad & 2) != 0) {
                    row = z - split + 1;
                    match = true;
                } else if (childQuadrant == 3 && (quad & 4) != 0) {
                    col = x - split + 1;
                    match = true;
                } else if (childQuadrant == 4 && (quad & 8) != 0) {
                    col = x - split + 1;
                    row = z - split + 1;
                    match = true;
                }

                if (match) {
                    if (spat instanceof TerrainQuad) {
                        return ((TerrainQuad) spat).getHeightmapHeight(col, row);
                    } else if (spat instanceof TerrainPatch) {
                        return ((TerrainPatch) spat).getHeightmapHeight(col, row);
                    }
                }

            }
        }
        return Float.NaN;
    }


    public float getHeight(Vector2f xz) {
        // offset
        float x = (float)(((xz.x - getLocalTranslation().x) / getLocalScale().x) + (float)totalSize / 2f);
        float z = (float)(((xz.y - getLocalTranslation().z) / getLocalScale().z) + (float)totalSize / 2f);
        return getHeight(x, z, xz);
    }

    /*
     * gets an interpolated value at the specified point
     * @param x coordinate translated into actual (positive) terrain grid coordinates
     * @param y coordinate translated into actual (positive) terrain grid coordinates
     */
    protected float getHeight(float x, float z, Vector2f xz) {
        float topLeft = getHeightmapHeight((int)FastMath.floor(x), (int)FastMath.ceil(z));
        float topRight = getHeightmapHeight((int)FastMath.ceil(x), (int)FastMath.ceil(z));
        float bottomLeft = getHeightmapHeight((int)FastMath.floor(x), (int)FastMath.floor(z));
        float bottomRight = getHeightmapHeight((int)FastMath.ceil(x), (int)FastMath.floor(z));

        // create a vertical, down-facing, ray and get the height from that
        float max = Math.max(Math.max(Math.max(topLeft, topRight), bottomRight),bottomLeft);
        max = max*getWorldScale().y;
        Ray ray = new Ray(new Vector3f(xz.x,max+10f,xz.y), new Vector3f(0,-1,0).normalizeLocal());
        CollisionResults cr = new CollisionResults();
        int num = this.collideWith(ray, cr);
        if (num > 0)
            return cr.getClosestCollision().getContactPoint().y;
        else
            return 0;
    }

    public void setHeight(Vector2f xz, float height) {
        List<Vector2f> coord = new ArrayList<Vector2f>();
        coord.add(xz);
        List<Float> h = new ArrayList<Float>();
        h.add(height);

        setHeight(coord, h);
    }

    public void adjustHeight(Vector2f xz, float delta) {
        List<Vector2f> coord = new ArrayList<Vector2f>();
        coord.add(xz);
        List<Float> h = new ArrayList<Float>();
        h.add(delta);

        adjustHeight(coord, h);
    }

    public void setHeight(List<Vector2f> xz, List<Float> height) {
        setHeight(xz, height, true);
    }

    public void adjustHeight(List<Vector2f> xz, List<Float> height) {
        setHeight(xz, height, false);
    }

    protected void setHeight(List<Vector2f> xz, List<Float> height, boolean overrideHeight) {
        if (xz.size() != height.size())
            throw new IllegalArgumentException("Both lists must be the same length!");

        int halfSize = totalSize / 2;

        List<LocationHeight> locations = new ArrayList<LocationHeight>();

        // offset
        for (int i=0; i<xz.size(); i++) {
            int x = Math.round((xz.get(i).x / getLocalScale().x) + halfSize);
            int z = Math.round((xz.get(i).y / getLocalScale().z) + halfSize);
            locations.add(new LocationHeight(x,z,height.get(i)));
        }

        setHeight(locations, overrideHeight); // adjust height of the actual mesh

        // signal that the normals need updating
        for (int i=0; i<xz.size(); i++)
            setNormalRecalcNeeded(xz.get(i) );
    }

    protected class LocationHeight {
        int x;
        int z;
        float h;

        LocationHeight(){}

        LocationHeight(int x, int z, float h){
            this.x = x;
            this.z = z;
            this.h = h;
        }
    }

    protected void setHeight(List<LocationHeight> locations, boolean overrideHeight) {
        if (children == null)
            return;

        List<LocationHeight> quadLH1 = new ArrayList<LocationHeight>();
        List<LocationHeight> quadLH2 = new ArrayList<LocationHeight>();
        List<LocationHeight> quadLH3 = new ArrayList<LocationHeight>();
        List<LocationHeight> quadLH4 = new ArrayList<LocationHeight>();
        Spatial quad1 = null;
        Spatial quad2 = null;
        Spatial quad3 = null;
        Spatial quad4 = null;

        // get the child quadrants
        for (int i = children.size(); --i >= 0;) {
            Spatial spat = children.get(i);
            int childQuadrant = 0;
            if (spat instanceof TerrainQuad) {
                childQuadrant = ((TerrainQuad) spat).getQuadrant();
            } else if (spat instanceof TerrainPatch) {
                childQuadrant = ((TerrainPatch) spat).getQuadrant();
            }

            if (childQuadrant == 1)
                quad1 = spat;
            else if (childQuadrant == 2)
                quad2 = spat;
            else if (childQuadrant == 3)
                quad3 = spat;
            else if (childQuadrant == 4)
                quad4 = spat;
        }

        int split = (size + 1) >> 1;

        // distribute each locationHeight into the quadrant it intersects
        for (LocationHeight lh : locations) {
            int quad = findQuadrant(lh.x, lh.z);

            int col = lh.x;
            int row = lh.z;

            if ((quad & 1) != 0) {
                quadLH1.add(lh);
            }
            if ((quad & 2) != 0) {
                row = lh.z - split + 1;
                quadLH2.add(new LocationHeight(lh.x, row, lh.h));
            }
            if ((quad & 4) != 0) {
                col = lh.x - split + 1;
                quadLH3.add(new LocationHeight(col, lh.z, lh.h));
            }
            if ((quad & 8) != 0) {
                col = lh.x - split + 1;
                row = lh.z - split + 1;
                quadLH4.add(new LocationHeight(col, row, lh.h));
            }
        }

        // send the locations to the children
        if (!quadLH1.isEmpty()) {
            if (quad1 instanceof TerrainQuad)
                ((TerrainQuad)quad1).setHeight(quadLH1, overrideHeight);
            else if(quad1 instanceof TerrainPatch)
                ((TerrainPatch)quad1).setHeight(quadLH1, overrideHeight);
        }

        if (!quadLH2.isEmpty()) {
            if (quad2 instanceof TerrainQuad)
                ((TerrainQuad)quad2).setHeight(quadLH2, overrideHeight);
            else if(quad2 instanceof TerrainPatch)
                ((TerrainPatch)quad2).setHeight(quadLH2, overrideHeight);
        }

        if (!quadLH3.isEmpty()) {
            if (quad3 instanceof TerrainQuad)
                ((TerrainQuad)quad3).setHeight(quadLH3, overrideHeight);
            else if(quad3 instanceof TerrainPatch)
                ((TerrainPatch)quad3).setHeight(quadLH3, overrideHeight);
        }

        if (!quadLH4.isEmpty()) {
            if (quad4 instanceof TerrainQuad)
                ((TerrainQuad)quad4).setHeight(quadLH4, overrideHeight);
            else if(quad4 instanceof TerrainPatch)
                ((TerrainPatch)quad4).setHeight(quadLH4, overrideHeight);
        }
    }

    protected boolean isPointOnTerrain(int x, int z) {
        return (x >= 0 && x <= totalSize && z >= 0 && z <= totalSize);
    }

    
    public int getTerrainSize() {
        return totalSize;
    }


    // a position can be in multiple quadrants, so use a bit anded value.
    private int findQuadrant(int x, int y) {
        int split = (size + 1) >> 1;
        int quads = 0;
        if (x < split && y < split)
            quads |= 1;
        if (x < split && y >= split - 1)
            quads |= 2;
        if (x >= split - 1 && y < split)
            quads |= 4;
        if (x >= split - 1 && y >= split - 1)
            quads |= 8;
        return quads;
    }

    /**
     * lock or unlock the meshes of this terrain.
     * Locked meshes are uneditable but have better performance.
     * @param locked or unlocked
     */
    public void setLocked(boolean locked) {
        for (int i = 0; i < this.getQuantity(); i++) {
            if (this.getChild(i) instanceof TerrainQuad) {
                ((TerrainQuad) getChild(i)).setLocked(locked);
            } else if (this.getChild(i) instanceof TerrainPatch) {
                if (locked)
                    ((TerrainPatch) getChild(i)).lockMesh();
                else
                    ((TerrainPatch) getChild(i)).unlockMesh();
            }
        }
    }


    public int getQuadrant() {
        return quadrant;
    }

    public void setQuadrant(short quadrant) {
        this.quadrant = quadrant;
    }


    protected TerrainPatch getPatch(int quad) {
        if (children != null)
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainPatch) {
                    TerrainPatch tb = (TerrainPatch) child;
                    if (tb.getQuadrant() == quad)
                        return tb;
                }
            }
        return null;
    }

    protected TerrainQuad getQuad(int quad) {
        if (children != null)
            for (int x = children.size(); --x >= 0;) {
                Spatial child = children.get(x);
                if (child instanceof TerrainQuad) {
                    TerrainQuad tq = (TerrainQuad) child;
                    if (tq.getQuadrant() == quad)
                        return tq;
                }
            }
        return null;
    }

    protected TerrainPatch findRightPatch(TerrainPatch tp) {
        if (tp.getQuadrant() == 1)
            return getPatch(3);
        else if (tp.getQuadrant() == 2)
            return getPatch(4);
        else if (tp.getQuadrant() == 3) {
            // find the page to the right and ask it for child 1.
            TerrainQuad quad = findRightQuad();
            if (quad != null)
                return quad.getPatch(1);
        } else if (tp.getQuadrant() == 4) {
            // find the page to the right and ask it for child 2.
            TerrainQuad quad = findRightQuad();
            if (quad != null)
                return quad.getPatch(2);
        }

        return null;
    }

    protected TerrainPatch findDownPatch(TerrainPatch tp) {
        if (tp.getQuadrant() == 1)
            return getPatch(2);
        else if (tp.getQuadrant() == 3)
            return getPatch(4);
        else if (tp.getQuadrant() == 2) {
            // find the page below and ask it for child 1.
            TerrainQuad quad = findDownQuad();
            if (quad != null)
                return quad.getPatch(1);
        } else if (tp.getQuadrant() == 4) {
            TerrainQuad quad = findDownQuad();
            if (quad != null)
                return quad.getPatch(3);
        }

        return null;
    }


    protected TerrainPatch findTopPatch(TerrainPatch tp) {
        if (tp.getQuadrant() == 2)
            return getPatch(1);
        else if (tp.getQuadrant() == 4)
            return getPatch(3);
        else if (tp.getQuadrant() == 1) {
            // find the page above and ask it for child 2.
            TerrainQuad quad = findTopQuad();
            if (quad != null)
                return quad.getPatch(2);
        } else if (tp.getQuadrant() == 3) {
            TerrainQuad quad = findTopQuad();
            if (quad != null)
                return quad.getPatch(4);
        }

        return null;
    }

    protected TerrainPatch findLeftPatch(TerrainPatch tp) {
        if (tp.getQuadrant() == 3)
            return getPatch(1);
        else if (tp.getQuadrant() == 4)
            return getPatch(2);
        else if (tp.getQuadrant() == 1) {
            // find the page above and ask it for child 2.
            TerrainQuad quad = findLeftQuad();
            if (quad != null)
                return quad.getPatch(3);
        } else if (tp.getQuadrant() == 2) {
            TerrainQuad quad = findLeftQuad();
            if (quad != null)
                return quad.getPatch(4);
        }

        return null;
    }

    protected TerrainQuad findRightQuad() {
        if (getParent() == null || !(getParent() instanceof TerrainQuad))
            return null;

        TerrainQuad pQuad = (TerrainQuad) getParent();

        if (quadrant == 1)
            return pQuad.getQuad(3);
        else if (quadrant == 2)
            return pQuad.getQuad(4);
        else if (quadrant == 3) {
            TerrainQuad quad = pQuad.findRightQuad();
            if (quad != null)
                return quad.getQuad(1);
        } else if (quadrant == 4) {
            TerrainQuad quad = pQuad.findRightQuad();
            if (quad != null)
                return quad.getQuad(2);
        }

        return null;
    }

    protected TerrainQuad findDownQuad() {
        if (getParent() == null || !(getParent() instanceof TerrainQuad))
            return null;

        TerrainQuad pQuad = (TerrainQuad) getParent();

        if (quadrant == 1)
            return pQuad.getQuad(2);
        else if (quadrant == 3)
            return pQuad.getQuad(4);
        else if (quadrant == 2) {
            TerrainQuad quad = pQuad.findDownQuad();
            if (quad != null)
                return quad.getQuad(1);
        } else if (quadrant == 4) {
            TerrainQuad quad = pQuad.findDownQuad();
            if (quad != null)
                return quad.getQuad(3);
        }

        return null;
    }

    protected TerrainQuad findTopQuad() {
        if (getParent() == null || !(getParent() instanceof TerrainQuad))
                return null;

        TerrainQuad pQuad = (TerrainQuad) getParent();

        if (quadrant == 2)
            return pQuad.getQuad(1);
        else if (quadrant == 4)
            return pQuad.getQuad(3);
        else if (quadrant == 1) {
            TerrainQuad quad = pQuad.findTopQuad();
            if (quad != null)
                return quad.getQuad(2);
        } else if (quadrant == 3) {
            TerrainQuad quad = pQuad.findTopQuad();
            if (quad != null)
                return quad.getQuad(4);
        }

        return null;
    }

    protected TerrainQuad findLeftQuad() {
        if (getParent() == null || !(getParent() instanceof TerrainQuad))
                return null;

        TerrainQuad pQuad = (TerrainQuad) getParent();

        if (quadrant == 3)
            return pQuad.getQuad(1);
        else if (quadrant == 4)
            return pQuad.getQuad(2);
        else if (quadrant == 1) {
            TerrainQuad quad = pQuad.findLeftQuad();
            if (quad != null)
                return quad.getQuad(3);
        } else if (quadrant == 2) {
            TerrainQuad quad = pQuad.findLeftQuad();
            if (quad != null)
                return quad.getQuad(4);
        }

        return null;
    }

    /**
     * Find what terrain patches need normal recalculations and update
     * their normals;
     */
    protected void fixNormals(BoundingBox affectedArea) {
        if (children == null)
            return;

        // go through the children and see if they collide with the affectedAreaBBox
        // if they do, then update their normals
        for (int x = children.size(); --x >= 0;) {
            Spatial child = children.get(x);
            if (child instanceof TerrainQuad) {
                if (affectedArea != null && affectedArea.intersects(((TerrainQuad) child).getWorldBound()) )
                    ((TerrainQuad) child).fixNormals(affectedArea);
            } else if (child instanceof TerrainPatch) {
                if (affectedArea != null && affectedArea.intersects(((TerrainPatch) child).getWorldBound()) )
                    ((TerrainPatch) child).updateNormals(); // recalculate the patch's normals
            }
        }
    }

    /**
     * fix the normals on the edge of the terrain patches.
     */
    protected void fixNormalEdges(BoundingBox affectedArea) {
        if (children == null)
            return;

        for (int x = children.size(); --x >= 0;) {
            Spatial child = children.get(x);
            if (child instanceof TerrainQuad) {
                if (affectedArea != null && affectedArea.intersects(((TerrainQuad) child).getWorldBound()) )
                    ((TerrainQuad) child).fixNormalEdges(affectedArea);
            } else if (child instanceof TerrainPatch) {
                if (affectedArea != null && !affectedArea.intersects(((TerrainPatch) child).getWorldBound()) ) // if doesn't intersect, continue
                    continue;

                TerrainPatch tp = (TerrainPatch) child;
                TerrainPatch right = findRightPatch(tp);
                TerrainPatch bottom = findDownPatch(tp);
                TerrainPatch top = findTopPatch(tp);
                TerrainPatch left = findLeftPatch(tp);
                TerrainPatch topLeft = null;
                if (top != null)
                    topLeft = findLeftPatch(top);
                TerrainPatch bottomRight = null;
                if (right != null)
                    bottomRight = findDownPatch(right);
                TerrainPatch topRight = null;
                if (top != null)
                    topRight = findRightPatch(top);
                TerrainPatch bottomLeft = null;
                if (left != null)
                    bottomLeft = findDownPatch(left);

                tp.fixNormalEdges(right, bottom, top, left, bottomRight, bottomLeft, topRight, topLeft);

            }
        } // for each child

    }



    @Override
    public int collideWith(Collidable other, CollisionResults results){
        int total = 0;

        if (other instanceof Ray)
            return collideWithRay((Ray)other, results);

        // if it didn't collide with this bbox, return
        if (other instanceof BoundingVolume)
            if (!this.getWorldBound().intersects((BoundingVolume)other))
                return total;

        for (Spatial child : children){
            total += child.collideWith(other, results);
        }
        return total;
    }

    /**
     * Gather the terrain patches that intersect the given ray (toTest).
     * This only tests the bounding boxes
     * @param toTest
     * @param results
     */
    public void findPick(Ray toTest, List<TerrainPickData> results) {

        if (getWorldBound() != null) {
            if (getWorldBound().intersects(toTest)) {
                // further checking needed.
                for (int i = 0; i < getQuantity(); i++) {
                    if (children.get(i) instanceof TerrainPatch) {
                        TerrainPatch tp = (TerrainPatch) children.get(i);
                        if (tp.getWorldBound().intersects(toTest)) {
                            CollisionResults cr = new CollisionResults();
                            toTest.collideWith(tp.getWorldBound(), cr);
                            if (cr != null && cr.getClosestCollision() != null) {
                                cr.getClosestCollision().getDistance();
                                results.add(new TerrainPickData(tp, cr.getClosestCollision()));
                            }
                        }
                    }
                    else
                        ((TerrainQuad) children.get(i)).findPick(toTest, results);
                }
            }
        }
    }


    /**
     * Retrieve all Terrain Patches from all children and store them
     * in the 'holder' list
     * @param holder must not be null, will be populated when returns
     */
    public void getAllTerrainPatches(List<TerrainPatch> holder) {
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).getAllTerrainPatches(holder);
                } else if (child instanceof TerrainPatch) {
                    holder.add((TerrainPatch)child);
                }
            }
        }
    }

    public void getAllTerrainPatchesWithTranslation(Map<TerrainPatch,Vector3f> holder, Vector3f translation) {
        if (children != null) {
            for (int i = children.size(); --i >= 0;) {
                Spatial child = children.get(i);
                if (child instanceof TerrainQuad) {
                    ((TerrainQuad) child).getAllTerrainPatchesWithTranslation(holder, translation.clone().add(child.getLocalTranslation()));
                } else if (child instanceof TerrainPatch) {
                    //if (holder.size() < 4)
                    holder.put((TerrainPatch)child, translation.clone().add(child.getLocalTranslation()));
                }
            }
        }
    }

    @Override
    public void read(JmeImporter e) throws IOException {
        super.read(e);
        InputCapsule c = e.getCapsule(this);
        size = c.readInt("size", 0);
        stepScale = (Vector3f) c.readSavable("stepScale", null);
        offset = (Vector2f) c.readSavable("offset", new Vector2f(0,0));
        offsetAmount = c.readFloat("offsetAmount", 0);
        quadrant = c.readInt("quadrant", 0);
        totalSize = c.readInt("totalSize", 0);
        lodCalculatorFactory = (LodCalculatorFactory) c.readSavable("lodCalculatorFactory", null);
    }

    @Override
    public void write(JmeExporter e) throws IOException {
        super.write(e);
        OutputCapsule c = e.getCapsule(this);
        c.write(size, "size", 0);
        c.write(totalSize, "totalSize", 0);
        c.write(stepScale, "stepScale", null);
        c.write(offset, "offset", new Vector2f(0,0));
        c.write(offsetAmount, "offsetAmount", 0);
        c.write(quadrant, "quadrant", 0);
        c.write(lodCalculatorFactory, "lodCalculatorFactory", null);
    }

    @Override
    public TerrainQuad clone() {
        return this.clone(true);
    }

	@Override
    public TerrainQuad clone(boolean cloneMaterials) {
        TerrainQuad quadClone = (TerrainQuad) super.clone(cloneMaterials);
        quadClone.name = name.toString();
        quadClone.size = size;
        quadClone.totalSize = totalSize;
        quadClone.stepScale = stepScale.clone();
        quadClone.offset = offset.clone();
        quadClone.offsetAmount = offsetAmount;
        quadClone.quadrant = quadrant;
        quadClone.lodCalculatorFactory = lodCalculatorFactory.clone();
        TerrainLodControl lodControl = quadClone.getControl(TerrainLodControl.class);
        if (lodControl != null && !(getParent() instanceof TerrainQuad)) {
            lodControl.setTerrain(quadClone); // set println in controller update to see if it is updating
        }
        NormalRecalcControl normalControl = getControl(NormalRecalcControl.class);
        if (normalControl != null)
            normalControl.setTerrain(this);

        return quadClone;
    }


    public int getMaxLod() {
        if (maxLod < 0)
            maxLod = Math.max(1, (int) (FastMath.log(size-1)/FastMath.log(2)) -1); // -1 forces our minimum of 4 triangles wide

        return maxLod;
    }

    public int getPatchSize() {
        return patchSize;
    }

    public int getTotalSize() {
        return totalSize;
    }


    public float[] getHeightMap() {

        float[] hm = null;
        int length = ((size-1)/2)+1;
        int area = size*size;
        hm = new float[area];

        if (getChildren() != null) {
            float[] ul=null, ur=null, bl=null, br=null;
            // get the child heightmaps
            if (getChild(0) instanceof TerrainPatch) {
                for (Spatial s : getChildren()) {
                    if ( ((TerrainPatch)s).getQuadrant() == 1)
                        ul = BufferUtils.getFloatArray(((TerrainPatch)s).getHeightmap());
                    else if(((TerrainPatch) s).getQuadrant() == 2)
                        bl = BufferUtils.getFloatArray(((TerrainPatch)s).getHeightmap());
                    else if(((TerrainPatch) s).getQuadrant() == 3)
                        ur = BufferUtils.getFloatArray(((TerrainPatch)s).getHeightmap());
                    else if(((TerrainPatch) s).getQuadrant() == 4)
                        br = BufferUtils.getFloatArray(((TerrainPatch)s).getHeightmap());
                }
            }
            else {
                ul = getQuad(1).getHeightMap();
                bl = getQuad(2).getHeightMap();
                ur = getQuad(3).getHeightMap();
                br = getQuad(4).getHeightMap();
            }

            // combine them into a single heightmap


            // first upper blocks
            for (int y=0; y<length; y++) { // rows
                for (int x1=0; x1<length; x1++) {
                    int row = y*size;
                    hm[row+x1] = ul[y*length+x1];
                }
                for (int x2=1; x2<length; x2++) {
                    int row = y*size + length;
                    hm[row+x2-1] = ur[y*length + x2];
                }
            }
            // second lower blocks
            int rowOffset = size*length;
            for (int y=1; y<length; y++) { // rows
                for (int x1=0; x1<length; x1++) {
                    int row = (y-1)*size;
                    hm[rowOffset+row+x1] = bl[y*length+x1];
                }
                for (int x2=1; x2<length; x2++) {
                    int row = (y-1)*size + length;
                    hm[rowOffset+row+x2-1] = br[y*length + x2];
                }
            }
        }

        return hm;
    }
}

