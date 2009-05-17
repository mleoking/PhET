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
package com.acarter.iwave;

/**
 * @author Carter
 * 
 */
public final class BesselFunction {

	/**
	 * Series for bm0 on the interval 0. to 6.25000d-02 with weighted error
	 * 4.98e-17 log weighted error 16.30 significant figures required 14.97
	 * decimal places required 16.96
	 */
	private final static double bm0cs[] = { 0.09284961637381644, -0.00142987707403484, 0.00002830579271257, -0.00000143300611424,
			0.00000012028628046, -0.00000001397113013, 0.00000000204076188, -0.00000000035399669, 0.00000000007024759, -0.00000000001554107,
			0.00000000000376226, -0.00000000000098282, 0.00000000000027408, -0.00000000000008091, 0.00000000000002511, -0.00000000000000814,
			0.00000000000000275, -0.00000000000000096, 0.00000000000000034, -0.00000000000000012, 0.00000000000000004 };

	/**
	 * Series for bth0 on the interval 0. to 6.25000d-02 with weighted error
	 * 3.67e-17 log weighted error 16.44 significant figures required 15.53
	 * decimal places required 17.13
	 */
	private final static double bth0cs[] = { -0.24639163774300119, 0.001737098307508963, -0.000062183633402968, 0.000004368050165742,
			-0.000000456093019869, 0.000000062197400101, -0.000000010300442889, 0.000000001979526776, -0.000000000428198396, 0.000000000102035840,
			-0.000000000026363898, 0.000000000007297935, -0.000000000002144188, 0.000000000000663693, -0.000000000000215126, 0.000000000000072659,
			-0.000000000000025465, 0.000000000000009229, -0.000000000000003448, 0.000000000000001325, -0.000000000000000522, 0.000000000000000210,
			-0.000000000000000087, 0.000000000000000036 };

	/**
	 * Series for bj0 on the interval 0. to 1.60000d+01 with weighted error
	 * 7.47e-18 log weighted error 17.13 significant figures required 16.98
	 * decimal places required 17.68
	 */
	private final static double bj0cs[] = { 0.100254161968939137, -0.665223007764405132, 0.248983703498281314, -0.0332527231700357697,
			0.0023114179304694015, -0.0000991127741995080, 0.0000028916708643998, -0.0000000612108586630, 0.0000000009838650793,
			-0.0000000000124235515, 0.0000000000001265433, -0.0000000000000010619, 0.0000000000000000074 };

	/**
	 * Evaluates a Chebyshev series.
	 * 
	 * @param x
	 *            value at which to evaluate series
	 * @param series
	 *            the coefficients of the series
	 */
	private static double chebyshev(double x, double series[]) {
		double twox, b0 = 0.0, b1 = 0.0, b2 = 0.0;
		twox = 2 * x;
		for (int i = series.length - 1; i > -1; i--) {
			b2 = b1;
			b1 = b0;
			b0 = twox * b1 - b2 + series[i];
		}
		return 0.5 * (b0 - b2);
	}

	/**
	 * Bessel function of first kind, order zero. Based on the NETLIB Fortran
	 * function besj0 written by W. Fullerton.
	 */
	public static double besselFirstZero(double x) {
		double y = Math.abs(x);
		if (y > 4.0) {
			final double z = 32 / (y * y) - 1;
			final double amplitude = (0.75 + chebyshev(z, bm0cs)) / Math.sqrt(y);
			final double theta = y - Math.PI / 4.0 + chebyshev(z, bth0cs) / y;
			return amplitude * Math.cos(theta);
		}
		else if (y == 0.0)
			return 1.0;
		else
			return chebyshev(0.125 * y * y - 1, bj0cs);
	}
}
