/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.graphtheory;


/**
 * User: Sam Reid
 * Date: Sep 2, 2003
 * Time: 1:50:31 AM
 * Copyright (c) Sep 2, 2003 by Sam Reid
 */
public class Loop {
    DirectedPath path;

    public Loop(DirectedPath path) {
        this.path = path;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Loop))
            return false;
        Loop loop = (Loop) obj;
        if (loop.path.size() != path.size()) {
            return false;
        }
        for (int i = 0; i < path.size(); i++) {
            if (equalsAtForwards(i, loop))
                return true;
            else if (equalsAtBackwards(i, loop))
                return true;
        }
        return false;
    }

    private boolean equalsAtBackwards(int index, Loop loop) {
        for (int i = 0; i < path.size(); i++) {
            int myIndex = (index - i + path.size()) % path.size();
            int yourIndex = i % path.size();
            if (!(loop.path.vertexAt(yourIndex) == path.vertexAt(myIndex))) {
                return false;
            }
        }
        return true;
    }

    private boolean equalsAtForwards(int index, Loop loop) {
        for (int i = 0; i < path.size(); i++) {
            int myIndex = (index + i) % path.size();
            int yourIndex = i % path.size();
            if (!(loop.path.vertexAt(yourIndex) == path.vertexAt(myIndex))) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        return path.toVertexString();
    }

    public int numBranches() {
        return path.size();
    }

    public DirectedPathElement directedPathElementAt(int i) {
        return path.pathElementAt(i);
    }

    public boolean containsEdgeData(Object data) {
        return path.containsEdgeData(data);
    }

    public boolean sharesAnEdgeData(Loop rem) {
        for (int i = 0; i < path.size(); i++) {
            Object data = path.pathElementAt(i).getEdge().getData();
            if (rem.containsEdgeData(data))
                return true;
        }
        return false;
    }
}
