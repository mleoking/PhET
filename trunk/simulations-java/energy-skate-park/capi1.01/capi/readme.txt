Useful Information
------------------

The copyfiles.bat file is not required.  It is used to copy only
the necessary files from the com.graphbuilder package hierarchy
that are required for the Curve API.

In the src directory, there is a makejar.bat file.  This file will
compile the nested Java files and create a Jar file called Capi.jar.
Compilation requires Java 1.2 or higher.  After the Jar file is
created, add it to the classpath.  Note: CAPI comes with MESP (Math
Expression String Parser).

The following is a sample program:

import com.graphbuilder.curve.*;

public class CurveTest {

	public static void main(String[] args) {
		ControlPath cp = new ControlPath();
		cp.addPoint(createPoint(0,0));
		cp.addPoint(createPoint(10,10));
		cp.addPoint(createPoint(20, 10));
		cp.addPoint(createPoint(30,0));

		cp.addCurve(new BezierCurve(cp, new GroupIterator("0:n-1", cp.numPoints())));

		int dimension = 2;
		MultiPath mp = new MultiPath(dimension);
		mp.setFlatness(1.0); // default flatness is 1.0

		for (int i = 0; i < cp.numCurves(); i++)
			cp.getCurve(i).appendTo(mp);

		for (int i = 0; i < mp.getNumPoints(); i++) {
			double[] p = mp.get(i);
			System.out.println(p[0] + ", " + p[1]);
		}
	}

	private static Point createPoint(double x, double y) {
		final double[] arr = new double[] { x, y };
		return new Point() {
			public double[] getLocation() {
				return arr;
			}

			public void setLocation(double[] loc) {
				arr[0] = loc[0];
				arr[1] = loc[1];
			}
		};
	}
}

The demo directory contains a GUI to the API.  To run it first compile the
files and then run java CurveDemo.  The demo also requires Java 1.2 or higher.