/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.cck.elements.branch.components;

import edu.colorado.phet.cck.elements.branch.Branch;
import edu.colorado.phet.cck.elements.branch.BranchObserver;
import edu.colorado.phet.cck.elements.branch.CompositeBranch;
import edu.colorado.phet.cck.elements.circuit.Circuit;
import edu.colorado.phet.cck.elements.junction.Junction;
import edu.colorado.phet.cck.elements.xml.BranchData;
import edu.colorado.phet.cck.elements.xml.BulbData;
import edu.colorado.phet.common.math.LinearTransform1d;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.simpleobservable.SimpleObserver;

import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 5, 2003
 * Time: 3:13:07 AM
 * Copyright (c) Sep 5, 2003 by Sam Reid
 * Test comment.
 * Test comment2.
 */
public class Bulb extends Branch implements HasResistance {
    double resistance;
    CompositeBranch cb;
//    private double gapWidth;
    private double height;
    private PhetVector secondStartPoint;
    private double intensity = 0;
    ArrayList intensityObservers = new ArrayList();
//    private Point2D.Double ctrl;
    private boolean recursing = false;
    private double width;
    private PhetVector vecFromStartJunction;
    private PhetVector ctrl;

    public PhetVector getVecFromStartJunction() {
        return vecFromStartJunction;
    }

    public Bulb(Circuit parent, double x1, double y1, double x2, double y2, PhetVector vecFromStartJunction, double resistance) {
        super(parent, x1, y1, x2, y2);
        this.vecFromStartJunction = vecFromStartJunction;
        this.ctrl = new PhetVector(x1, y1).getAddedInstance(vecFromStartJunction);
        this.resistance = resistance;
        addObserver(new BranchObserver() {
            public void junctionMoved(Branch branch2, Junction junction) {
                recomputeState();
                if (recursing)
                    return;
                recursing = true;
                if (junction == getStartJunction()) {
                    getEndJunction().setLocation(getStartJunction().getX() + .22, getStartJunction().getY() - .42);
                } else if (junction == getEndJunction()) {
                    getStartJunction().setLocation(getEndJunction().getX() - .22, getEndJunction().getY() + .42);
                }
                recursing = false;//What a crazy hack.
            }

            public void currentOrVoltageChanged(Branch branch2) {
            }
        });
        recomputeState();
    }

    public void setVoltageDrop(double voltageDrop) {
        super.setVoltageDrop(voltageDrop);
        updateIntensity();
    }

    public double sigmoid(double x, double dx) {
        return 1.0 / (1.0 + Math.exp(-x + dx));
    }

    private void updateIntensity() {
        double voltage = getVoltageDrop();
        double current = Math.abs(super.getCurrent());
        double power = Math.abs(current * getVoltageDrop());
        LinearTransform1d map = new LinearTransform1d(0, 1000, .2, 1);
        double temp = map.operate(power);

        if (temp > 1)
            temp = 1;
        if (temp < 0)
            temp = 0;
        if (power == 0)
            temp = 0;

        System.out.println("current = " + current + ", voltage=" + voltage + ", power=" + power + ", intensity=" + temp);
        setIntensity(temp);
    }

    public void setCurrent(double current) {
        super.setCurrent(current);
        updateIntensity();
    }

    private void recomputeState() {
        cb = new CompositeBranch(getCircuit(), getX1(), getY1());
//        double length = super.getLength();//distance between endpoints.
//        this.vecFromStartJunction = vecFromStartJunction;
        this.ctrl = new PhetVector(getX1(), getY1()).getAddedInstance(vecFromStartJunction);
//        ctrl = new Point2D.Double(getX1(), getY1());

        PhetVector forward = new PhetVector(ctrl.getX() - getX1(), ctrl.getY() - getY1()).getNormalizedInstance();
//        double length = forward.getMagnitude();
//        forward = forward.getNormalizedInstance();
//        PhetVector forward = super.getDirection().getNormalizedInstance();

        PhetVector up = forward.getNormalVector().getNormalizedInstance();

//        double lengthA = length / 2.0 - width/ 2.0;
//        cb.addRelativePoint(forward.getScaledInstance(lengthA));
        cb.addRelativePoint(forward.getScaledInstance(.1));

//        double lengthUp=1;
//        double lengthUp = height*.25;
        double lengthUp = height * .35;
        cb.addRelativePoint(up.getScaledInstance(lengthUp));
        cb.addRelativePoint(forward.getScaledInstance(width / 2.0));

        cb.addRelativePoint(up.getScaledInstance(-lengthUp - .15));
        this.secondStartPoint = cb.getEndPoint();
        cb.lineTo(super.getX2(), super.getY2());
    }

    public PhetVector getMovedInstance(PhetVector start, PhetVector dir, double dist) {
        return start.getAddedInstance(dir.getScaledInstance(dist / dir.getMagnitude()));
    }

    public PhetVector getMovedInstance(PhetVector start, double angle, double magnitude) {
        PhetVector pv = PhetVector.parseAngleAndMagnitude(angle, magnitude);
        return getMovedInstance(start, pv, magnitude);
    }

    public double getResistance() {
        return resistance;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
        parent.fireConnectivityChanged();
        fireCurrentChanged();
    }

    public double getLength() {
        return cb.getLength();
    }

    public PhetVector getPosition2D(double x) {
        PhetVector loc = cb.getPosition2D(x);
        if (loc == null)
            loc = new PhetVector();
        return loc;
    }

    public boolean containsScalarLocation(double x) {
        return cb.containsScalarLocation(x);
    }

    public Branch copy() {
        return new Bulb(parent, getX1(), getY1(), getX2(), getY2(), getVecFromStartJunction().getAddedInstance(0, 0), resistance);
    }

    public BranchData toBranchData() {
        return new BulbData(this);
    }

    public void setImageParametersModelCoords(double width, double height) {
        this.width = width;
        this.height = height;
        recomputeState();
    }

    public PhetVector getSecondStartPoint() {
        return secondStartPoint;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
        for (int i = 0; i < intensityObservers.size(); i++) {
            SimpleObserver simpleObserver = (SimpleObserver) intensityObservers.get(i);
            simpleObserver.update();
        }
    }

    public double getIntensity() {
        return intensity;
    }

    public void addIntensityObserver(SimpleObserver so) {
        this.intensityObservers.add(so);
    }

    public PhetVector getControlPoint() {
        return ctrl;
    }

    public void setControlPointLocation(PhetVector ctrl) {
        this.ctrl = ctrl;
    }

    public void setCurrentAndVoltage(double amps, double volts) {
        super.setCurrentNoUpdate(amps);
        super.setVoltageDropNoUpdate(volts);
        updateIntensity();
        fireCurrentChanged();
    }
}
