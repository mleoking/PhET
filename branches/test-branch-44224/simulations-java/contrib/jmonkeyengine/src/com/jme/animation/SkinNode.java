/*
 * Copyright (c) 2003-2009 jMonkeyEngine
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

package com.jme.animation;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.jme.math.Matrix4f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.ConnectionPoint;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import com.jme.util.export.Savable;
import com.jme.util.geom.VertMap;

/**
 * SkinNode defines a scene node that contains skinned mesh data. A skinned mesh
 * is defined by a Geometry object representing the "skin" that is attached to a
 * skeleton (or a tree of Bones). The orientation, translation of these bones
 * define the position of the skin vertices. These bones can then be driven by
 * an animation system to provide the animation of the skin. SkinNode defines
 * for each vertex of the skin the bone that affects it and the weight
 * (BoneInfluence) of that affect. This allows multiple bones to share a single
 * vertex (although the total weight must add up to 1).
 * 
 * @author Joshua Slack
 * @author Mark Powell
 */
public class SkinNode extends Node implements Savable, BoneChangeListener {

    private static final long serialVersionUID = 1L;

    protected static Vector3f vertex = new Vector3f();
    protected static Vector3f normal = new Vector3f();

    protected boolean needsRefresh = true;

    protected Node skins = null;

    protected Bone skeleton = null;
    protected ArrayList<BoneInfluence>[][] cache = null;
    
    protected ArrayList<ConnectionPoint> connectionPoints;
    
    protected transient boolean newSkeletonAssigned = false;
    protected transient Matrix4f bindMatrix = new Matrix4f();

    private final Vector3f tmpTranslation = new Vector3f();
    private final Quaternion tmpRotation = new Quaternion();
    private final Vector3f tmpScale = new Vector3f();

    private boolean externalControl = false;
    
    /**
     * Empty Constructor to be used internally only.
     */
    public SkinNode() {
        setLastFrustumIntersection(Camera.FrustumIntersect.Inside);
    }

    /**
     * Constructor creates a new SkinNode object with the supplied name.
     * 
     * @param name
     *            the name of this SkinNode
     */
    public SkinNode(String name) {
        super(name);
    }

    /**
     * getSkin returns the skins (Geometry objects) that the SkinNode is currently controlling.
     * 
     * @return the skins contained in this SkinNode
     */
    public Node getSkins() {
        return skins;
    }

    /**
     * setSkin sets the skin that the SkinNode will affect.
     * 
     * @param skins
     *            the skins that this SkinNode will affect.
     */
    public void setSkins(Node skins) {
        this.skins = skins;
        attachChild(skins);
    }

    /**
     * addSkins sets the skin that the SkinNode will affect.
     * 
     * @param skin
     *            an additional skin that this SkinNode will affect.
     */
    public void addSkin(Geometry skin) {
        if (skins == null) {
            skins = new Node("Skins");
            attachChild(skins);
        }
        this.skins.attachChild(skin);
    }

    /**
     * addBoneInfluence defines how a vertex will be affected by a bone. This is
     * given with four values, the geometry child the vertex is found, the index
     * to the vertex in the geometry, the index of the bone that has been or
     * will be set via setBones or addBone and the weight that this indexed bone
     * affects the vertex.
     * 
     * @param geomIndex
     *            the geometry child that contains the vertex to be affected.
     * @param vert
     *            the index to the vertex.
     * @param bone
     *            the bone that affects the vertex.
     * @param weight
     *            the weight that the bone will affect the vertex.
     */
    public void addBoneInfluence(int geomIndex, int vert, Bone bone,
            float weight) {
    	if (weight == 0) return;
        if (cache == null)
            recreateCache();

        ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
        if (infs == null) {
            infs = new ArrayList<BoneInfluence>(1);
            cache[geomIndex][vert] = infs;
        }
        BoneInfluence i = new BoneInfluence(bone, weight);
        i.boneId = bone.getName();
        if (!infs.contains(i))
        	infs.add(i);
    }
    
    public void setAnimation(BoneAnimation anim) {
        
        if(skeleton != null && skeleton.getAnimationController() != null) {
        	skeleton.getAnimationController().setActiveAnimation(anim);
        }
    }
    
    public void setAnimation(int index) {
        if(skeleton != null && skeleton.getAnimationController() != null) {
            skeleton.getAnimationController().setActiveAnimation(index);
        }
    }
    
    public void setAnimation(String name) {
        if(skeleton != null && skeleton.getAnimationController() != null) {
            skeleton.getAnimationController().setActiveAnimation(name);
        }
    }
    
    public String getAnimationString() {
    	if(skeleton != null && skeleton.getAnimationController() != null ) {
        	return skeleton.getAnimationController().getActiveAnimation().getName();
    	}
    	return null;
    	
   	}
    
    public void addBoneInfluence(int geomIndex, int vert, String boneId,
            float weight) {
    	if (weight == 0) return;
        if (cache == null) {
            recreateCache();
        }
        
        if (vert > cache[geomIndex].length) {
            System.out.println("vert: " + vert);
            System.out.println("cache[" + geomIndex + "].length: " + cache[geomIndex].length);
            
            for (int i=0;i<cache.length;i++) {
                System.out.println("cache[" + i + "].length: " + cache[i].length);                
            }
        }
        ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
        if (infs == null) {
            infs = new ArrayList<BoneInfluence>(1);
            cache[geomIndex][vert] = infs;
        }
        BoneInfluence i = new BoneInfluence(null, weight);
        i.boneId = boneId;
        if (!infs.contains(i))
        	infs.add(i);
    }
    
    public ConnectionPoint addConnectionPoint(String name, Bone b) {
        ConnectionPoint cp = new ConnectionPoint(name, b);
        if(connectionPoints == null) {
            connectionPoints = new ArrayList<ConnectionPoint>();
        }
        connectionPoints.add(cp);
        this.attachChild(cp);
        return cp;
    }
    
    public ArrayList<ConnectionPoint> getConnectionPoints() {
        return connectionPoints;
    }

    /**
     * recreateCache initializes the cache of BoneInfluences for use by the skin
     * node.
     */
    @SuppressWarnings("unchecked")
    public void recreateCache() {
        cache = new ArrayList[skins.getQuantity()][];
        for (int x = 0; x < cache.length; x++) {
        	cache[x] = new ArrayList[skins.getChild(x).getVertexCount()];
        }
    }

    /**
     * updateGeometricState overrides Spatials updateGeometric state to update
     * the assigned skeleton bone influences, if changed.
     * 
     * @param time
     *            the time that has passed between calls.
     * @param initiator
     *            true if this is the top level being called.
     */
    public void updateGeometricState(float time, boolean initiator) {
        if (newSkeletonAssigned) {
            assignSkeletonBoneInfluences();
        }

        if (!externalControl && skins != null && needsRefresh) {
        	updateSkin();
        	skins.updateModelBound();

        	needsRefresh = false;
        }

        super.updateGeometricState(time, initiator);
    }
    
    /**
     * normalizeWeights insures that all vertex BoneInfluences equal 1. The total
     * BoneInfluence on a single vertex should be 1 otherwise the position of the
     * vertex will be multiplied.
     */
    public void normalizeWeights() {
        if (cache == null)
            return;
        for (int geomIndex = cache.length; --geomIndex >= 0;) {
            normalizeWeights(geomIndex);
        }
    }

    public int getInfluenceCount(int geomIndex) {
        if (cache == null)
            return 0;
        int rVal = 0;
        for (int vert = cache[geomIndex].length; --vert >= 0;) {
            ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
            if (infs != null)
                rVal+=infs.size();
        }
        return rVal;
    }
    
    public void normalizeWeights(int geomIndex) {
        if (cache == null)
            return;
        for (int vert = cache[geomIndex].length; --vert >= 0;) {
            ArrayList<BoneInfluence> infs = cache[geomIndex][vert];
            if (infs == null)
                continue;
            float total = 0;
            for (int x = infs.size(); --x >= 0;) {
                BoneInfluence influence = infs.get(x);
                total += influence.weight;
            }
            for (int x = infs.size(); --x >= 0;) {
                BoneInfluence influence = infs.get(x);
                influence.weight /= total;
            }
        }
    }
    
    public void setSkeleton(Bone b) {
        if (skeleton != null)
            skeleton.removeBoneListener(this);

        skeleton = b;
        skeleton.addBoneListener(this);
        newSkeletonAssigned = true;
    }

    public Bone getSkeleton() {
        return skeleton;
    }

    public void assignSkeletonBoneInfluences() {
        if (skeleton != null) {
            for(int i = 0; i < cache.length; i++) {
                
                for(int j = 0; j < cache[i].length; j++) {
                	if(cache[i][j] != null) {
                        for(int k = 0; k < cache[i][j].size(); k++) {
                            cache[i][j].get(k).assignBone(skeleton);
                        }
	                }
            	}
            }
        }

        newSkeletonAssigned = false;
    }
    
    /**
     * regenInfluenceOffsets calculate the offset of a particular vertex from a
     * bone. This allows the bone's rotation to position the vertex in world
     * space. This should only be called a single time during initialization.
     */
    public void regenInfluenceOffsets() {
        if (cache == null)
            return;

        Vector3f vertex = new Vector3f();
        Vector3f normal = new Vector3f();

        FloatBuffer verts, norms;
        for (int index = cache.length; --index >= 0;) {
            Geometry geom = (Geometry)skins.getChild(index);
            verts = geom.getVertexBuffer();
            norms = geom.getNormalBuffer();
            verts.clear();
            norms.clear();
            for (int vert = 0, max = cache[index].length; vert < max; vert++) {
                ArrayList<BoneInfluence> infs = cache[index][vert];

                vertex.set(verts.get(), verts.get(), verts.get());
                normal.set(norms.get(), norms.get(), norms.get());

                if (infs == null)
                    continue;

                bindMatrix.mult(vertex, vertex);
                bindMatrix.rotateVect(normal);

                for (int x = infs.size(); --x >= 0;) {
                    BoneInfluence infl = infs.get(x);
                    if(infl.bone != null) {
                        infl.vOffset = new Vector3f(vertex);
                        infl.bone.bindMatrix.inverseTranslateVect(infl.vOffset);
                        infl.bone.bindMatrix.inverseRotateVect(infl.vOffset);
    
                        infl.nOffset = new Vector3f(normal);
                        infl.bone.bindMatrix.inverseRotateVect(infl.nOffset);
                    }
                }
            }
        }
    }

    /**
     * updateSkin positions the vertices of the skin based on the bones and the
     * BoneInfluences those bones have on the vertices. Each vertex is placed into
     * world space for rendering.
     */
    public synchronized void updateSkin() {
        if (cache == null || skins == null)
            return;
        
        if (skeleton != null) {
        	if (skeleton.getParent() != null) {
        		tmpTranslation.set(skeleton.getParent().getWorldTranslation());
        		tmpRotation.set(skeleton.getParent().getWorldRotation());
        		tmpScale.set(skeleton.getParent().getWorldScale());

        		skeleton.getParent().getWorldTranslation().set(0,0,0);
        		skeleton.getParent().getWorldRotation().set(0,0,0,1);
        		skeleton.getParent().getWorldScale().set(1,1,1);
        		skeleton.updateWorldVectors(true);
        	}
        	
        	skeleton.update();
        }
        
        FloatBuffer verts, norms;

        for (int index = cache.length; --index >= 0;) {
            Geometry geom = (Geometry)skins.getChild(index);
            verts = geom.getVertexBuffer();
            verts.clear();
            norms = geom.getNormalBuffer();
            norms.clear();
            geom.setHasDirtyVertices(true);
            for (int vert = 0, max = cache[index].length; vert < max; vert++) {
                ArrayList<BoneInfluence> infs = cache[index][vert];
                if (infs == null)
                    continue;
                vertex.zero();
                normal.zero();

                for (int x = infs.size(); --x >= 0;) {
                    BoneInfluence inf = infs.get(x);
                    if (inf.bone != null) {
                        inf.bone.applyBone(inf, vertex, normal);
                    } 
                }

                if (verts.remaining() > 2)
                    verts.put(vertex.x).put(vertex.y).put(vertex.z);
                if (norms.remaining() > 2) {
                    norms.put(normal.x).put(normal.y).put(normal.z);
                }
            }
        }
        
        if (skeleton != null && skeleton.getParent() != null) {
    		skeleton.getParent().getWorldTranslation().set(tmpTranslation);
    		skeleton.getParent().getWorldRotation().set(tmpRotation);
    		skeleton.getParent().getWorldScale().set(tmpScale);
    		skeleton.updateWorldVectors(true);
        }        
    }   

    public ArrayList<BoneInfluence>[][] getCache() {
        return cache;
    }

    public void setCache(ArrayList<BoneInfluence>[][] cache) {
        this.cache = cache;
    }

    public void setBindMatrix(Matrix4f mat) {
        bindMatrix = mat;
    }
    
    public void childChange(Geometry geometry, int index1, int index2) {
        if(skins != null && skins.hasChild(geometry)) {
            ArrayList<BoneInfluence>[] temp1 = cache[index1];
            ArrayList<BoneInfluence>[] temp2 = cache[index2];
            cache[index1] = temp2;
            cache[index2] = temp1;
        }
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule cap = e.getCapsule(this);

        cap.write(skins, "skins", null);
        cap.write(skeleton, "skeleton", null);
        cap.writeSavableArrayListArray2D(cache, "cache", null);
        cap.writeSavableArrayList(connectionPoints, "connectionPoints", null);
    }

    @SuppressWarnings("unchecked")
    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule cap = e.getCapsule(this);
        
        skins = (Node)cap.readSavable("skins", null);
        skeleton = (Bone)cap.readSavable("skeleton", null);
        cache = cap.readSavableArrayListArray2D("cache", null);
        connectionPoints = cap.readSavableArrayList("connectionPoints", null);
        
        if (skeleton != null) {
            //skeleton.revertToBind();
            skeleton.addBoneListener(this);
        }

        updateWorldBound();
    }

    public void revertToBind() {
        bindMatrix.loadIdentity();
        updateSkin();
    }
    
    public void boneChanged(BoneChangeEvent e) {
        needsRefresh = true;
    }
    
    public void remapInfluences(VertMap[] mappings) {
        for (int x = 0; x < mappings.length; x++) {
            remapInfluences(mappings[x], x);
        }
    }
    
    @SuppressWarnings("unchecked")
    public void remapInfluences(VertMap mappings, int geomIndex) {
    	ArrayList<BoneInfluence>[] infls = cache[geomIndex];
        ArrayList<BoneInfluence>[] newInfls = new ArrayList[skins.getChild(geomIndex).getVertexCount()];
        cache[geomIndex] = newInfls;
        for (int x = 0; x < infls.length; x++) {
        	for (int y = 0; y < infls[x].size(); y++) {
            	
                BoneInfluence bi = infls[x].get(y);
                if (bi.bone != null)
                    addBoneInfluence(geomIndex, mappings.getNewIndex(x), bi.bone, bi.weight);
                else
                    addBoneInfluence(geomIndex, mappings.getNewIndex(x), bi.boneId, bi.weight);
            }
        }
        normalizeWeights(geomIndex);
    }
    
    @SuppressWarnings("unchecked")
    public void removeGeometry(int geomIndex) {
    	if (geomIndex >= cache.length)
    		return;
        ArrayList<BoneInfluence>[][] newCache = new ArrayList[skins.getQuantity()-1][];
        for (int x = 0; x < cache.length-1; x++) {
            if (x < geomIndex)
                newCache[x] = cache[x];
            else
                newCache[x] = cache[x+1];
        }
        cache = newCache;
    }

	public void setExternalControl(boolean externalControl) {
		this.externalControl = externalControl;
	}

	public boolean isExternalControl() {
		return externalControl;
	}
}
