/**
 * Copyright (c) 2008, Andrew Carter
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of 
 * conditions and the following disclaimer. Redistributions in binary form must reproduce 
 * the above copyright notice, this list of conditions and the following disclaimer in 
 * the documentation and/or other materials provided with the distribution. Neither the 
 * name of Andrew Carter nor the names of contributors may be used to endorse or promote 
 * products derived from this software without specific prior written permission. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT 
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.acarter.iwave.jme;

import java.nio.FloatBuffer;

import com.acarter.iwave.BesselFunction;
import com.jme.math.Vector3f;
import com.jme.scene.TexCoords;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;

/**
 * @author Carter
 *
 */
public class InteractiveWater extends TriMesh {

	/**  */
	private static final long serialVersionUID = 1L;

	private int size;
	private int area;

	private float alpha = 0.3f;
	private float gravity = 9.8f;

	private float[] obstructionMap;
	private float[] sourceMap;
	private float[] heightMap;
	private float[] previousHeightMap;
	private float[] verticalDerivativeMap;

	private float[][] kernel = new float[13][13];
	private float[][] obstructionBrush = new float[3][3];
	private float[][] sourceBrush = new float[3][3];

	private PaintMode paintMode = PaintMode.PAINT_SOURCE;

	public enum PaintMode {
		PAINT_OBSTRUCTION, PAINT_SOURCE
	};

	/**
	 * 
	 */
	public InteractiveWater(String name, int size) {
		
		super(name);
		
		this.size = size;
		area = size * size;

		// allocate space for fields and initialize them
		heightMap = new float[area];
		previousHeightMap = new float[area];
		verticalDerivativeMap = new float[area];
		obstructionMap = new float[area];
		sourceMap = new float[area];

		clearWaves();
		clearObstruction();
		
		initializeBrushes();

		// build the convolution kernel
		initializeKernel();
	
        buildVertices();
        buildTextureCoordinates();
        buildNormals();

        VBOInfo vbo = new VBOInfo(true);
        setVBOInfo(vbo);
	}

	/**
	 * Compute the elements of the convolution kernel
	 */
	private void initializeKernel() {

		double dk = 0.01;
		double sigma = 1.0;
		double norm = 0;

		for (double k = 0; k < 10; k += dk)
			norm += k * k * Math.exp(-sigma * k * k);

		for (int i = 0; i <= 6; i++) {
			for (int j = 0; j <= 6; j++) {

				double r = Math.sqrt((float) (i * i + j * j));
				double kern = 0;

				for (double k = 0; k < 10; k += dk)
					kern += k * k * Math.exp(-sigma * k * k) * BesselFunction.besselFirstZero(r * k);

				kernel[i + 6][j + 6] = (float) (kern / norm);
				kernel[-i + 6][j + 6] = (float) (kern / norm);
				kernel[i + 6][-j + 6] = (float) (kern / norm);
				kernel[-i + 6][-j + 6] = (float) (kern / norm);
			}
		}
	}

	/**
	 * 
	 */
	private void initializeBrushes() {

		obstructionBrush[1][1] = 0.0f;
		obstructionBrush[1][0] = 0.5f;
		obstructionBrush[0][1] = 0.5f;
		obstructionBrush[2][1] = 0.5f;
		obstructionBrush[1][2] = 0.5f;
		obstructionBrush[0][2] = 0.75f;
		obstructionBrush[2][0] = 0.75f;
		obstructionBrush[0][0] = 0.75f;
		obstructionBrush[2][2] = 0.75f;

		sourceBrush[1][1] = 1.0f;
		sourceBrush[1][0] = 0.5f;
		sourceBrush[0][1] = 0.5f;
		sourceBrush[2][1] = 0.5f;
		sourceBrush[1][2] = 0.5f;
		sourceBrush[0][2] = 0.25f;
		sourceBrush[2][0] = 0.25f;
		sourceBrush[0][0] = 0.25f;
		sourceBrush[2][2] = 0.25f;
	}

    /**
     * <code>buildVertices</code> sets up the vertex and index arrays of the
     * TriMesh.
     */
    private void buildVertices() {
    	
        setVertexCount(heightMap.length);
        setVertexBuffer(BufferUtils.createVector3Buffer(getVertexBuffer(), getVertexCount()));
        
        Vector3f point = new Vector3f();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
            	
                point.set(x, heightMap[x + (y * size)], y);
                BufferUtils.setInBuffer(point, getVertexBuffer(), (x + (y * size)));
            }
        }

        // set up the indices
        setTriangleQuantity(((size - 1) * (size - 1)) * 2);
        setIndexBuffer(BufferUtils.createIntBuffer(getTriangleCount() * 3));

        // go through entire array up to the second to last column.
        for (int i = 0; i < (size * (size - 1)); i++) {
        	
            // we want to skip the top row.
            if (i % ((size * (i / size + 1)) - 1) == 0 && i != 0) {
                continue;
            }
            
            // set the top left corner.
            getIndexBuffer().put(i);
            
            // set the bottom right corner.
            getIndexBuffer().put((1 + size) + i);
            
            // set the top right corner.
            getIndexBuffer().put(1 + i);
            
            // set the top left corner
            getIndexBuffer().put(i);
            
            // set the bottom left corner
            getIndexBuffer().put(size + i);
            
            // set the bottom right corner
            getIndexBuffer().put((1 + size) + i);
        }
    }

    /**
     * <code>buildTextureCoordinates</code> calculates the texture coordinates
     * of the terrain.
     */
    public void buildTextureCoordinates() {

        setTextureCoords(new TexCoords(BufferUtils.createVector2Buffer(getVertexCount())));
        FloatBuffer texs = getTextureCoords(0).coords;
        texs.clear();

        getVertexBuffer().rewind();
        
        for (int i = 0; i < getVertexCount(); i++) {
        	
            texs.put(getVertexBuffer().get());
            getVertexBuffer().get(); // ignore vertex y coordinate.
            texs.put(getVertexBuffer().get());
        }
    }

    /**
     * <code>buildNormals</code> calculates the normals of each vertex that
     * makes up the block of terrain.
     */
    private void buildNormals() {
    	
        setNormalBuffer(BufferUtils.createVector3Buffer(getNormalBuffer(), getVertexCount()));
        
        Vector3f oppositePoint = new Vector3f();
        Vector3f adjacentPoint = new Vector3f();
        Vector3f rootPoint = new Vector3f();
        Vector3f tempNorm = new Vector3f();
        
        int adj = 0, opp = 0, normalIndex = 0;
        
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
            	
                BufferUtils.populateFromBuffer(rootPoint, getVertexBuffer(), normalIndex);
                
                if (row == size - 1) {
                    if (col == size - 1) { // last row, last col
                        // up cross left
                        adj = normalIndex - size;
                        opp = normalIndex - 1;
                    } else { // last row, except for last col
                        // right cross up
                        adj = normalIndex + 1;
                        opp = normalIndex - size;
                    }
                } else {
                    if (col == size - 1) { // last column except for last row
                        // left cross down
                        adj = normalIndex - 1;
                        opp = normalIndex + size;
                    } else { // most cases
                        // down cross right
                        adj = normalIndex + size;
                        opp = normalIndex + 1;
                    }
                }
                
                BufferUtils.populateFromBuffer(adjacentPoint, getVertexBuffer(), adj);
                BufferUtils.populateFromBuffer(oppositePoint, getVertexBuffer(), opp);
                
                tempNorm.set(adjacentPoint).subtractLocal(rootPoint).crossLocal(oppositePoint.subtractLocal(rootPoint)).normalizeLocal();
                
                BufferUtils.setInBuffer(tempNorm, getNormalBuffer(), normalIndex);
                normalIndex++;
            }
        }
    }
    
    /**
     * 
     * @param tpf
     */
    public void update(float tpf) {
    	
    	propagate(tpf);
    	updateFromHeightMap();
    }

	/**
	 * In Propagate(), we have not bothered to handle the boundary conditions.
	 * This makes the outermost 6 pixels all the way around act like a hard
	 * wall.
	 */
	public void propagate(float dt) {

		// apply obstruction
		for (int i = 0; i < area; i++)
			heightMap[i] *= obstructionMap[i];

		// compute vertical derivative
		computeVerticalDerivative();

		// advance surface
		float adt = alpha * dt;
		float gravity = this.gravity * dt * dt;
		float adt2 = 1.0f / (1.0f + adt);

		for (int i = 0; i < area; i++) {

			float temp = heightMap[i];
			heightMap[i] = heightMap[i] * (2.0f - adt) - previousHeightMap[i] - gravity * verticalDerivativeMap[i];
			heightMap[i] *= adt2;
			heightMap[i] += sourceMap[i];
			heightMap[i] *= obstructionMap[i];
			previousHeightMap[i] = temp;

			// reset source each step
			sourceMap[i] = 0;
		}
	}

	/**
	 * 
	 */
	private void computeVerticalDerivative() {

		// first step: the interior
		for (int ix = 6; ix < size - 6; ix++) {

			for (int iy = 6; iy < size - 6; iy++) {

				int index = ix + size * iy;
				float vd = 0;

				for (int iix = -6; iix <= 6; iix++) {

					for (int iiy = -6; iiy <= 6; iiy++) {

						int iindex = ix + iix + size * (iy + iiy);
						vd += kernel[iix + 6][iiy + 6] * heightMap[iindex];
					}
				}

				verticalDerivativeMap[index] = vd;
			}
		}
	}

    /**
     * Updates the vertices and normals from the current height map values.
     */
    public void updateFromHeightMap() {
        
        Vector3f point = new Vector3f();
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
            	
                point.set(x, heightMap[x + (y * size)], y);
                BufferUtils.setInBuffer(point, getVertexBuffer(), (x + (y * size)));
            }
        }
        
        buildNormals();

        if (getVBOInfo() != null) {
        	
            getVBOInfo().setVBOVertexID(-1);
            getVBOInfo().setVBONormalID(-1);
            
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(getVertexBuffer());
            DisplaySystem.getDisplaySystem().getRenderer().deleteVBO(getNormalBuffer());
        }
    }
	
	/**
	 * 
	 * @return
	 */
	public PaintMode getPaintMode() {
		
		return paintMode;
	}
	
	/**
	 * 
	 * @param mode
	 */
	public void setPaintMode(PaintMode mode) {
		
		paintMode = mode;
	}

	/**
	 * 
	 */
	public void dabSomePaint(int x, int y) {

		int xstart = x - 1;
		int ystart = y - 1;

		if (xstart < 0)
			xstart = 0;

		if (ystart < 0)
			ystart = 0;

		int xend = x + 1;
		int yend = y + 1;

		if (xend >= size)
			xend = size - 1;

		if (yend >= size)
			yend = size - 1;

		if (paintMode == PaintMode.PAINT_OBSTRUCTION) {

			for (int ix = xstart; ix <= xend; ix++) {

				for (int iy = ystart; iy <= yend; iy++) {

					int index = ix + size * (size - iy - 1);
					obstructionMap[index] *= obstructionBrush[ix - xstart][iy - ystart];
				}
			}
		}
		else if (paintMode == PaintMode.PAINT_SOURCE) {

			for (int ix = xstart; ix <= xend; ix++) {

				for (int iy = ystart; iy <= yend; iy++) {

					int index = ix + size * (size - iy - 1);
					sourceMap[index] += sourceBrush[ix - xstart][iy - ystart];
				}
			}
		}
	}

	/**
	 * 
	 */
	public void clearObstruction() {

		for (int i = 0; i < area; i++)
			obstructionMap[i] = 1.0f;
	}

	/**
	 * 
	 */
	public void clearWaves() {

		for (int i = 0; i < area; i++) {

			heightMap[i] = 0.0f;
			previousHeightMap[i] = 0.0f;
			verticalDerivativeMap[i] = 0.0f;
		}
	}
}
