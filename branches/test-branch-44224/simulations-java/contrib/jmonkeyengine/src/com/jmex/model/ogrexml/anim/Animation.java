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

package com.jmex.model.ogrexml.anim;

import java.io.Serializable;

/**
 * Combines mesh and bone animations into one class for easier access
 */
public class Animation implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String name;
    private float length;

    private BoneAnimation boneAnim;
    private MeshAnimation meshAnim;

    Animation(BoneAnimation boneAnim, MeshAnimation meshAnim){
        this.boneAnim = boneAnim;
        this.meshAnim = meshAnim;

        if (boneAnim == null){
            this.name = meshAnim.getName();
            this.length = meshAnim.getLength();
        }else if (meshAnim == null){
            this.name = boneAnim.getName();
            this.length = boneAnim.getLength();
        }else{
            this.name = boneAnim.getName();
            this.length = Math.max(boneAnim.getLength(),
                                   meshAnim.getLength());
        }
    }

    void setBoneAnimation(BoneAnimation boneAnim){
        this.boneAnim = boneAnim;

        this.length = Math.max(boneAnim.getLength(),
                               meshAnim.getLength());
    }

    void setMeshAnimation(MeshAnimation meshAnim){
        this.meshAnim = meshAnim;

        this.length = Math.max(boneAnim.getLength(),
                               meshAnim.getLength());
    }

    boolean hasMeshAnimation(){
        return meshAnim != null;
    }

    boolean hasBoneAnimation(){
        return boneAnim != null;
    }

    String getName(){
        return name;
    }

    float getLength(){
        return length;
    }

    void setTime(float time, OgreMesh[] targets, Skeleton skeleton){
        if (meshAnim != null)
            meshAnim.setTime(time, targets);

        if (boneAnim != null){
            boneAnim.setTime(time, skeleton);
        }
    }

    MeshAnimation getMeshAnimation() {
        return meshAnim;
    }

    BoneAnimation getBoneAnimation(){
        return boneAnim;
    }


}
