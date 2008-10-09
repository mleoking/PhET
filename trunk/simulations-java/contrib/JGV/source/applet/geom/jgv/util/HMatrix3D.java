//
// Copyright (C) 1998-2000 Geometry Technologies, Inc. <info@geomtech.com>
// Copyright (C) 2002-2004 DaerMar Communications, LLC <jgv@daermar.com>
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
//

package geom.jgv.util;

//import java.io.TurboTokenizer;

/** A fairly conventional 3D matrix object that can transform sets of
    3D points and perform a variety of manipulations on the transform */
//		T0  T1  T2  T3
//  (x y z w) *	T4  T5  T6  T7
//		T8  T9  T10 T11
//		T12 T13 T14 T15

public class HMatrix3D {
    public double T[];	// 4*4
    static final double pi = 3.14159265358979;
    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;
    static final int W = 3;
    static final int DIM = 4;
    static final int DIM2 = 4*4;

    /** Create a new unit matrix */
    public HMatrix3D () {
	T = new double[DIM2];
	T[0] = T[5] = T[10] = T[15] = 1.0;
    }

    /** Initializes to copy of another transform */
    public HMatrix3D( HMatrix3D from ) {
	super();
	T = from.copyData();
    }

    /** Incorporate (by reference) the given array of DIM2 doubles. */
    public HMatrix3D( double T[] ) {
	super();
	this.T = T;
    }

    /** returns copy of the double[] data array; mostly for internal use */
    double[] copyData() {
	double[] cT = new double[DIM2];
	System.arraycopy(this.T, 0, cT, 0, DIM2);
	return cT;
    }

    public void copyFrom( double[] data ) {
	System.arraycopy(data, 0, T, 0, DIM2);
    }

    public void copyFrom( HMatrix3D A ) {
	System.arraycopy(A.T, 0, T, 0, DIM2);
    }

    /** Scale by f in all dimensions */
    public void scale(double f) {
	scale(f, f, f);
    }

    /** Scale along each axis independently */
    public void scale(double xf, double yf, double zf) {
	for(int i = 0; i < DIM2; i+=DIM) {
	    T[i] *= xf;
	    T[i+1] *= yf;
	    T[i+2] *= zf;
	}
    }

    /** Translate (multiplying on the side away from the object) */
    public void rtranslate(double x, double y, double z) {
	T[12] += x;
	T[13] += y;
	T[14] += z;
    }

    /** Translate */
    public void translate(double x, double y, double z) {
	for(int i = 0; i < 3; i++)
	    T[12+i] += T[i]*x + T[4+i]*y + T[8+i]*z;
    }

    public void xrot(double theta) { arot(X, theta); }
    public void yrot(double theta) { arot(Y, theta); }
    public void zrot(double theta) { arot(Z, theta); }

    /** rotate theta degrees about the A axis */
    public void arot(int axis, double theta) {
	theta *= (pi / 180);
	double ct = Math.cos(theta);
	double st = Math.sin(theta);
	int a1 = DIM * ((axis+2) % 3);
	int a2 = DIM * ((axis+1) % 3);

	for(int j = 0; j < DIM; j++) {
	    double t1 = T[a1+j], t2 = T[a2+j];
	    T[a1+j] = ct*t1 + st*t2;
	    T[a2+j] = -st*t1 + ct*t2;
	}
    }

    /** Rotate about arbitrary axis */
    public void rotabout(double axis[], double radians) {
	rotabout(axis[0], axis[1], axis[2], radians);
    }

    public void rotabout(double ax, double ay, double az, double radians) {
	double u = Math.sqrt(ax*ax + ay*ay + az*az);

	if(u == 0)
	    return;

	double x = ax/u, y = ay/u, z = az/u;
	double c = Math.cos(radians), s = Math.sin(radians);
	double v = 1-c;

	double tv[] = {
		x*x*v + c,	x*y*v - z*s,	x*z*v + y*s, 	0,
		y*x*v + z*s,	y*y*v + c,	y*z*v - x*s,	0,
		z*x*v - y*s,	z*y*v + x*s,	z*z*v + c,	0,
		0,		0,		0,		1
	};
	mult(new HMatrix3D(tv));
    }

    /** Right-multiply this matrix by a second: M.rmult(A) yields M := M*A */
    public void rmult(HMatrix3D A) {
	double t[] = new double[DIM2];
	System.arraycopy(T, 0, t, 0, DIM2);
	for(int i = 0; i < DIM2; i += DIM) {
	    for(int j = 0; j < DIM; j++) {
                T[i+j] =   t[i  ] * A.T[j      ]
                         + t[i+1] * A.T[j+DIM  ]
                         + t[i+2] * A.T[j+2*DIM]
                         + t[i+3] * A.T[j+3*DIM];
	    }
	}
    }

    /** Left-multiply this matrix by a second: M = A*M */
    /* Since this works the way scale, translate, etc. do, we call it just "mult". */
    public void mult(HMatrix3D A) {
	double t[] = new double[DIM2];
	System.arraycopy(T, 0, t, 0, DIM2);
	for(int i = 0; i < DIM2; i += DIM) {
	    for(int j = 0; j < DIM; j++) {
		T[i+j] = A.T[i]*t[j] + A.T[i+1]*t[DIM+j]
			+ A.T[i+2]*t[2*DIM+j] + A.T[i+3]*t[3*DIM+j];
	    }
	}
    }

    /** Construct perspective transformation with given window:
     * near-plane boundaries l <= x <= r,  b <= y <= t,
     * distance n to near clip plane, f to far clip plane.
     * Multiplication by this matrix yields a point in box -1 <= x,y,z <= 1,
     * with z=-1 at near plane, z=+1 at far plane.
     */
    public void perspective( double l, double r,  double b, double t,  double n, double f )
    {
	if(l == r) { l = -1;  r = 1; }
	if(t == b) { t = -1;  b = 1; }
	if(n == f) { n = -1;  f = 1; }
	unit();
	T[X*DIM+X] = 2*n/(r-l);
	T[Y*DIM+Y] = 2*n/(t-b);
	T[Z*DIM+Z] = -(f+n)/(f-n);
	T[W*DIM+W] = 0;
	T[Z*DIM+W] = -1;
	T[Z*DIM+X] = (r+l)/(r-l);
	T[Z*DIM+Y] = (t+b)/(t-b);
	T[W*DIM+Z] = 2*n*f/(n-f);
    }

    /** Reinitialize to the unit matrix */
    public void unit() {
	for(int i = 0; i < DIM2; i++) {
	    T[i] = 0;
	}
	T[0] = T[5] = T[10] = T[15] = 1;
    }

    /** Transform nvert points from v into tv.  v contains the input
        coordinates in floating point.  Three successive entries in
	the array constitute a point.  tv ends up holding the transformed
	points as integers; three successive entries per point */

    public void transformixy(double v[], int xv[], int yv[], int nvert) {
	int i = 0, vi = 0;
	for (i = 0; i < nvert; i++, vi += 3) {
	    double x = v[vi];
	    double y = v[vi + 1];
	    double z = v[vi + 2];
	    double nw = x*T[3] + y*T[7] + z*T[11] + T[15];
	    if(nw == 0) { nw = .0001f; } // Bogus, but keeps things finite

	    xv[i] = (int) ((x*T[0] + y*T[4] + z*T[8] + T[12]) / nw);
	    yv[i] = (int) ((x*T[1] + y*T[5] + z*T[9] + T[13]) / nw);
	}
    }

    /** Transform nvert points from v into nv, as 3-D doubles.
     */
    public void transformd(double v[], double nv[], int nvert) {
	for (int i = nvert * 3; (i -= 3) >= 0;) {
	    double x = v[i];
	    double y = v[i + 1];
	    double z = v[i + 2];
	    double nw = x*T[3] + y*T[7] + z*T[11] + T[15];
	    if(nw == 0) { nw = .0001; } // Bogus, but keeps things finite

	    nv[i    ] = ((x*T[0] + y*T[4] + z*T[8] + T[12]) / nw);
	    nv[i + 1] = ((x*T[1] + y*T[5] + z*T[9] + T[13]) / nw);
	    nv[i + 2] = ((x*T[2] + y*T[6] + z*T[10] + T[14]) / nw);
	}
    }

    /** Transform nvert homogeneous (4-component) points from v into nv */
    public void htransform(double v[], double nv[], int nvert) {
	for(int i = nvert*4; (i -= 4) >= 0; ) {
	    double x = v[i];
	    double y = v[i+1];
	    double z = v[i+2];
	    double w = v[i+3];
	    for(int j = 0; j < DIM; j++)
		nv[i+j] = x*T[0+j] + y*T[4+j] + z*T[8+j] + w*T[12+j];
	}
    }


    /** Set this to be the inverse of mi */
    public void inverseOf(HMatrix3D mi) {

	int i, j, k;
	double x;
	double f;

	double[] t = mi.copyData();
	double[] row = new double[DIM];
	this.unit();

	/* Components of unrolled inner loops: */

	for (i = 0; i < DIM; i++) {
		int largest = i;
		double largesq = t[i*DIM+i]*t[i*DIM+i];
		for (j = i+1; j < DIM; j++) {
		    if ((x = t[j*DIM+i]*t[j*DIM+i]) > largesq) {
			largest = j;
			largesq = x;
		    }
		}

		/* swap t[i][] with t[largest][], likewise with this.T[i][].
		 * We use System.arraycopy() since we know
		 * t[i][0..3] etc. are all adjacent.
		 */
		System.arraycopy(t, i*DIM, row, 0, DIM);
		System.arraycopy(t, largest*DIM, t, i*DIM, DIM);
		System.arraycopy(row, 0, t, largest*DIM, DIM);

		System.arraycopy(T, i*DIM, row, 0, DIM);
		System.arraycopy(T, largest*DIM, T, i*DIM, DIM);
		System.arraycopy(row, 0, T, largest*DIM, DIM);

		for (j = i+1; j < DIM; j++) {
		    f = t[j*DIM+i] / t[i*DIM+i];
		    /* subtract f*t[i][] from t[j][] */
		    for(k = 0; k < DIM; k++) {
			t[j*DIM+k] -= f*t[i*DIM+k];
			T[j*DIM+k] -= f*T[i*DIM+k];
		    }
		}
	}
	for (i = 0; i < DIM; i++) {
		f = t[i*DIM+i];
		for (k = 0; k < 4; k++) {
			t[i*DIM+k] /= f;
			T[i*DIM+k] /= f;
		}
	}
	for (i = DIM; --i >= 0; ) {
	    for (j = i-1; j >= 0; j--) {
		f = t[j*DIM+i];
		for(k = 0; k < DIM; k++) {
		    t[j*DIM+k] -= f*t[i*DIM+k];
		    T[j*DIM+k] -= f*T[i*DIM+k];
		}
	    }
	}
    }

    public static HMatrix3D parse(TurboTokenizer st) throws Exception {
      int i;
      HMatrix3D M;
      st.nextToken();
      if ( st.ttype == '{' ) {
	M = parse(st);
	st.nextToken();
	if ( st.ttype != '}' ) {
	  throw new ParseException(st, "missing '}' at end of transform");
	}
      } else {
	M = new HMatrix3D();
	M.T[0] = st.nval;
	for (i=1; i<16; ++i) {
	  st.nextToken();
	  if (st.ttype != st.TT_NUMBER) {
	    throw new ParseException(st, "Expected number in transform");
	  }
	  M.T[i] = st.nval;
	}
      }
      return M;
    }


    public String toString() {
	return (
		"\n[" + T[0] + "," + T[1] + "," + T[2] + "," + T[3] + "\n "
		+ T[4] + "," + T[5] + "," + T[6] + "," + T[7] + "\n "
		+ T[8] + "," + T[9] + "," + T[10] + "," + T[11] + "\n "
		+ T[12] + "," + T[13] + "," + T[14] + "," + T[15] + "]");
    }

    public static void main(String args[]) {
	HMatrix3D A = new HMatrix3D();
	HMatrix3D B = new HMatrix3D();
	HMatrix3D t = new HMatrix3D();
	double v[] = new double[16];
	String key = "";
	int nv;

	TurboTokenizer st = new TurboTokenizer(System.in);
	st.eolIsSignificant(true);
	st.wordChars('!','@');
	st.parseNumbers();


	try {
	  while(true) {
	    System.out.print("> ");  System.out.flush();
            int token = st.nextToken();
	    if (token == st.TT_EOL) {
              continue;
            } else if (token == st.TT_EOF) {
              System.exit(0);
            } else if (token == st.TT_WORD) {
              key = st.sval;
            } else {
              key = String.valueOf((char)st.ttype);
            }
	    for(nv = 0; st.nextToken() == st.TT_NUMBER; nv++) {
		if(nv < 16)
			v[nv] = st.nval;
	    }
	    if(key.endsWith("&")) t.copyFrom(A); else t.unit();
	    switch(nv) {
	    case 0: break;
	    case 1: t.scale(v[0]); break;
	    case 4: t.rotabout(v[0], v[1], v[2], v[3]); break;
	    case 16: System.arraycopy(v, 0, t.T, 0, DIM2); break;
	    default: System.out.println("Expected 0, 1(scale), 4(x y z angle) or 16 numbers");
	    }

	    if(key.startsWith("=")) {
		if(nv > 0) A.copyFrom(t);
		System.out.println("A=" + A);
	    } else if(key.startsWith("*")) {
		System.out.println("A" + A + "\n*" + t);
		A.mult(t);
		System.out.println("=" + A);
	    } else if(key.startsWith("i")) {
		if(nv == 0) t.copyFrom(A);
		System.out.println("inv" + t + " =");
		B.copyFrom(t);
		t.inverseOf(t);
		System.out.println(t);
		B.mult(t);
		System.out.println("product = " + B);
	    } else if(key.startsWith("pt")) {
		int nvert = nv/3;
		double newv[] = new double[nvert*3];
		System.out.print("A" + A + "*" + nvert + " points\n{");
		for(int i = 0; i < nvert; i++) System.out.print("[" + v[i*3] + " " + v[i*3+1] + " " + v[i*3+2] + "] ");
		System.out.println("} = {\n");
		A.transformd(v, newv, nvert);
		for(int i = 0; i < nvert; i++) System.out.print("[" + newv[i*3] + " " + newv[i*3+1] + " " + newv[i*3+2] + "] ");
		System.out.println("}");
	    } else if(key.startsWith("P")) {
		if(nv < 6) {
		    double thfov = Math.tan(.5*Math.PI*v[0]/180);
		    double aspect = (nv > 1) ? v[1] : 1;
		    double near = (nv > 2) ? v[2] : .01;
		    double far = (nv > 3) ? v[3] : 100;
		    A.perspective(-aspect*thfov*near, aspect*thfov*near,
				-thfov*near, thfov*near,
				near, far);
		} else {
		    A.perspective(v[0],v[1], v[2],v[3], v[4],v[5]);
		}
		System.out.println(A);
	    } else {
		System.out.println("Expected to begin with = or * or i, got " + key);
	    }
	  }
	} catch(Exception e) {
	}
    }
}
