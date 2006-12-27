/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.math;

/**
 * User: Sam Reid
 * Date: Jul 17, 2003
 * Time: 12:46:57 AM
 * Copyright (c) Jul 17, 2003 by Sam Reid
 */
public class CircularBuffer {
    double[] data;
    int startPtr;
    int endPtr;

    public CircularBuffer(int length) {
        this.data = new double[length + 1];
    }

    public void addPoint(double pt) {
        data[endPtr] = pt;
        endPtr++;
        if (endPtr >= data.length)
            endPtr = 0;
        if (startPtr == endPtr)
            startPtr++;
        if (startPtr >= data.length)
            startPtr = 0;
    }

    public int numPoints() {
        if (endPtr >= startPtr)
            return endPtr - startPtr;
        else
            return data.length - startPtr + endPtr;
    }

    public double pointAt(int i) {
        int index = (i + startPtr) % data.length;
        return data[index];
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = startPtr; i != endPtr; i = (i + 1) % data.length) {
            double v = data[i];
            sb.append(v);
            if ((i + 1) % data.length != endPtr)
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        CircularBuffer cb = new CircularBuffer(10);
        for (int i = 0; i < 20; i++) {
            cb.addPoint(i);
            System.out.println("cb = " + cb + ", num Points=" + cb.numPoints()+", avg="+cb.average());
        }
    }

    public double average() {
        double sum = 0;
        int numPoints = numPoints();
        for (int i = 0; i < numPoints; i++) {
            sum += pointAt(i);
        }
        return sum / numPoints;
    }
}

