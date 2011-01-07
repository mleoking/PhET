// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.model.components;

import edu.colorado.phet.circuitconstructionkit.model.CCKDefaults;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.CircuitChangeListener;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: Jun 13, 2004
 * Time: 8:06:21 PM
 */
public class Filament extends PathBranch {
    private Junction shellJunction;
    private Junction tailJunction;
    private double resistorDY;//the pin is the assumed origin.
    private double resistorWidth;
    private ImmutableVector2D northDir;
    private ImmutableVector2D eastDir;
    private Point2D pin;
    private boolean connectAtRight = true;

    public Filament(CircuitChangeListener kl, Junction tailJunction, Junction shellJunction, int numPeaks, double pivotToResistorDY, double resistorWidth, double zigHeight) {
        super(kl, tailJunction, shellJunction);
        this.shellJunction = shellJunction;
        this.tailJunction = tailJunction;
        this.resistorDY = pivotToResistorDY;
        this.resistorWidth = resistorWidth;
        recompute();
    }

    public boolean isConnectAtRight() {
        return connectAtRight;
    }

    public void setConnectAtRight(boolean connectAtRight) {
        this.connectAtRight = connectAtRight;
        recompute();
    }

    public void setStartJunction(Junction newJunction) {
        super.setStartJunction(newJunction);
        this.tailJunction = newJunction;
        recompute();
    }

    public void setEndJunction(Junction newJunction) {
        super.setEndJunction(newJunction);
        this.shellJunction = newJunction;
        recompute();
    }

    private Point2D getPoint(double east, double north) {
        ImmutableVector2D e = eastDir.getScaledInstance(east);
        ImmutableVector2D n = northDir.getScaledInstance(north);
        ImmutableVector2D sum = e.getAddedInstance(n);
        return sum.getDestination(pin);
    }

    private ImmutableVector2D getVector(double east, double north) {
        ImmutableVector2D e = eastDir.getScaledInstance(east);
        ImmutableVector2D n = northDir.getScaledInstance(north);
        return e.getAddedInstance(n);
    }

    public GeneralPath getPath() {
        DoubleGeneralPath path = new DoubleGeneralPath(segmentAt(0).getStart());
        for (int i = 0; i < numSegments(); i++) {
            Segment seg = segmentAt(i);
            path.lineTo(seg.getEnd());
        }
        return path.getGeneralPath();
    }

    boolean isNaN(ImmutableVector2D vector) {
        return Double.isNaN(vector.getX()) || Double.isNaN(vector.getY());
    }

    public void recompute() {
        if (tailJunction == null || shellJunction == null) {
            return;
        }
        double tilt = CCKDefaults.determineTilt();
        if (!connectAtRight) {
            tilt = -tilt;
        }
        northDir = new Vector2D(tailJunction.getPosition(), shellJunction.getPosition()).getNormalizedInstance();
        northDir = northDir.getRotatedInstance(-tilt);
        eastDir = northDir.getNormalVector().getNormalizedInstance();
        if (!connectAtRight) {
            eastDir = eastDir.getScaledInstance(-1);
        }
        if (isNaN(northDir) || isNaN(eastDir)) {
            System.out.println("Bulb basis set is not a number.");
            return;
        }
        pin = shellJunction.getPosition();

        Point2D pt = getPoint(-resistorWidth / 2, resistorDY);
        if (Double.isNaN(pt.getX()) || Double.isNaN(pt.getY())) {
            throw new RuntimeException("Point was nan: " + pt);
        }
        super.reset(tailJunction.getPosition(), pt);
        addPoint(getVector(-resistorWidth / 4, CCKModel.BULB_DIMENSION.getHeight() * 0.2));
        addPoint(getVector(resistorWidth * .68, 0));
        addPoint(pin);
    }

    public boolean isHiddenBranch(Location loc) {
        Segment seg = loc.getSegment();
        int index = indexOf(seg);
        if (index == 0 || index == 1) {
            return true;
        }
        return false;
    }

    private int indexOf(Segment seg) {
        return segments.indexOf(seg);
    }
}
