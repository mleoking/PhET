/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.graphtheory;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 2, 2003
 * Time: 1:31:41 AM
 * Copyright (c) Sep 2, 2003 by Sam Reid
 */
public class DirectedPath {
    ArrayList pathElements = new ArrayList();

    public DirectedPath(DirectedPathElement start) {
        pathElements.add(start);
    }

    public DirectedPath(DirectedPath path, DirectedPathElement next) {
        pathElements.addAll(path.pathElements);
        pathElements.add(next);
    }

    public boolean containsEdge(DirectedEdge e) {
        for (int i = 0; i < pathElements.size(); i++) {
            DirectedPathElement directedPathElement = (DirectedPathElement) pathElements.get(i);
            if (directedPathElement.getEdge() == e) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoop() {
        return (pathElements.size() > 1) && (getStartVertex() == getEndVertex());
    }

    public DirectedPathElement pathElementAt(int i) {
        return (DirectedPathElement) pathElements.get(i);
    }

    public Object getEndVertex() {
        return pathElementAt(pathElements.size() - 1).getEndVertex();
    }

    private Object getStartVertex() {
        return pathElementAt(0).getStartVertex();
    }

    public Object lastVertex() {
        return getEndVertex();
    }

    public boolean isLegalStep(DirectedPathElement next) {
        if (containsEdge(next.getEdge()))
            return false;//no crossing the same bridge twice in one directed path.
        else
            return true;
    }

    public String toString() {
        return pathElements.toString();
    }

    public String toVertexString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < pathElements.size(); i++) {
            DirectedPathElement directedPathElement = (DirectedPathElement) pathElements.get(i);
            sb.append(directedPathElement.getStartVertex());
            if (i < pathElements.size() - 1) {
                sb.append(", ");
            }
        }
        if (pathElements.size() > 1) {
            sb.append(", " + lastVertex());
        }
        return "<" + sb.toString() + ">";
    }

    public int size() {
        return pathElements.size();
    }

    public Object vertexAt(int index) {
        return this.pathElementAt(index).getStartVertex();
    }

    public boolean containsEdgeData(Object data) {
        for (int i = 0; i < pathElements.size(); i++) {
            DirectedPathElement directedPathElement = (DirectedPathElement) pathElements.get(i);
            if (directedPathElement.getEdge().getData() == data)
                return true;
        }
        return false;
    }

}
