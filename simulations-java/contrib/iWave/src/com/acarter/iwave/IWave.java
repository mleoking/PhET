package com.acarter.iwave;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class IWave {

	private int size;
	private int area;

	private float alpha = 0.3f;
	private float gravity = 9.8f;
	private float scale = 1.0f;

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

	int xmouse_prev, ymouse_prev;

	public IWave(int size) {

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

		initialize(sourceMap, area, 0);
		initializeBrushes();

		// build the convolution kernel
		initializeKernel();
	}

	/**
	 * Initialize all of the fields to zero
	 */
	private void initialize(float[] data, int size, float value) {

		for (int i = 0; i < size; i++)
			data[i] = value;
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

	/**
	 * 
	 * @param image
	 * @return
	 */
	public BufferedImage converToDisplay(BufferedImage image) {

		int[] buffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

		for (int i = 0; i < area; i++) {

			int val = (int) ((128.0f * (0.5f * (heightMap[i] / scale + 1.0f) * obstructionMap[i]) / 4) + 128);

			if (val > 255)
				val = 255;
			else if (val < 0)
				val = 0;

			buffer[i] = 0 << 24 | val << 16 | val << 8 | val;
		}

		return image;
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
}
