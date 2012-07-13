package edu.colorado.phet.functions;

import fj.data.List;
import lombok.Data;

import java.util.ArrayList;

import edu.umd.cs.piccolo.PNode;

/**
 * With the assumption that each face is completely behind or in front of each other, we can sort nodes for display.
 *
 * @author Sam Reid
 */
public class Iso3DNode {
    public final ArrayList<ZFace> zNodes = new ArrayList<ZFace>();
    public final ArrayList<YFace> yNodes = new ArrayList<YFace>();
    public final ArrayList<XFace> xNodes = new ArrayList<XFace>();

    public final List<Face> sort() {

    }

    public static @Data class Face {
        public final PNode node;
    }

    private static @Data class ZFace extends Face {
        public final double z;
        public final Range xRange;
        public final Range yRange;
    }

    private static @Data class YFace {
        final double y;
        final Range zRange;
        final Range xRange;
        final PNode node;
    }

    //Node that has a constant "x" value,
    private static @Data class XFace {
        final double x;
        final Range zRange;
        final Range yRange;
        final PNode node;
    }

    private @Data static class Range {
        public final double min;
        public final double extent;
    }
}