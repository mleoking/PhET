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
// $Id: AxisRods.java 4131 2009-03-19 20:15:28Z blaine.dev $
package com.jme.scene.shape;

import java.io.IOException;

import com.jme.bounding.BoundingVolume;
import com.jme.math.FastMath;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;

/**
 * Three coloured arrows, one pointing along each axis.
 * 
 * @author Joshua Slack
 * @version $Revision: 4131 $, $Date: 2009-03-19 14:15:28 -0600 (Thu, 19 Mar 2009) $
 */
public class AxisRods extends Node {

    private static final long serialVersionUID = 1L;

    private static final ColorRGBA X_AXIS_COLOUR = new ColorRGBA(1, 0, 0, .4f);
    private static final ColorRGBA Y_AXIS_COLOUR = new ColorRGBA(0, 1, 0, .25f);
    private static final ColorRGBA Z_AXIS_COLOUR = new ColorRGBA(0, 0, 1, .4f);

    private float length;
    private float width;
    private boolean rightHanded;

    private Arrow xAxis;
    private Arrow yAxis;
    private Arrow zAxis;

    public AxisRods() {
    }
    
    public AxisRods(String name) {
        this(name, true, 1);
    }
    
    public AxisRods(String name, boolean rightHanded, float baseScale) {
        this(name, rightHanded, baseScale, baseScale * 0.125f);
    }

    public AxisRods(String name, boolean rightHanded, float length, float width) {
        super(name);
        setLightCombineMode(Spatial.LightCombineMode.Off);
        setTextureCombineMode(Spatial.TextureCombineMode.Off);
        updateGeometry(length, width, rightHanded);
    }

    public float getLength() {
        return length;
    }

    public float getWidth() {
        return width;
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
        length = capsule.readFloat("length", 1);
        width = capsule.readFloat("width", 0.125f);
        rightHanded = capsule.readBoolean("rightHanded", true);
        updateGeometry(length, width, rightHanded);
    }

    /**
     * @deprecated use {@link #updateGeometry(float, float, boolean)}.
     */
    public void setLength(float length) {
        this.length = length;
    }

    public void setModelBound(BoundingVolume bound) {
        xAxis.setModelBound(bound);
        yAxis.setModelBound(bound);
        zAxis.setModelBound(bound);
    }

    /**
     * @deprecated use {@link #updateGeometry(float, float, boolean)}.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    public void updateGeometry(float length, float width, boolean rightHanded) {
        this.length = length;
        this.width = width;
        this.rightHanded = rightHanded;
        if (xAxis == null) {
            xAxis = new Arrow("xAxis", length, width);
            xAxis.setSolidColor(X_AXIS_COLOUR);
            xAxis.getLocalRotation().fromAngles(0,0,-90*FastMath.DEG_TO_RAD);
            xAxis.getLocalTranslation().addLocal(length*.5f, 0, 0);
            attachChild(xAxis);
            yAxis = new Arrow("yAxis", length, width);
            yAxis.setSolidColor(Y_AXIS_COLOUR);
            yAxis.getLocalTranslation().addLocal(0, length*.5f, 0);
            attachChild(yAxis);
            zAxis = new Arrow("zAxis", length, width);
            zAxis.setSolidColor(Z_AXIS_COLOUR);
            int dir = rightHanded ? 1 : -1;
            zAxis.getLocalRotation().fromAngles(dir * 90 * FastMath.DEG_TO_RAD, 0, 0);
            zAxis.getLocalTranslation().addLocal(0, 0, dir * length * 0.5f);
            attachChild(zAxis);
        } else {
            xAxis.updateGeometry(length, width);
            yAxis.updateGeometry(length, width);
            zAxis.updateGeometry(length, width);
        }
    }

    public void updateModelBound() {
        xAxis.updateModelBound();
        yAxis.updateModelBound();
        zAxis.updateModelBound();
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
        capsule.write(length, "length", 1);
        capsule.write(width, "width", 0.125f);
        capsule.write(rightHanded, "rightHanded", true);
    }

}
