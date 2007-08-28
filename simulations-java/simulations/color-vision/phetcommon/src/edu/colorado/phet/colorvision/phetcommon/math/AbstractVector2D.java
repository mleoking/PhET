/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author:samreid $
 * Revision : $Revision:14443 $
 * Date modified : $Date:2007-04-12 23:10:41 -0600 (Thu, 12 Apr 2007) $
 */

package edu.colorado.phet.colorvision.phetcommon.math;

import java.awt.geom.Point2D;

/**
 * High level abstraction of a 2-D Vector data structure.  The two main implementations
 * are the Vector2D and ImmutableVector2D.
 *
 * @author Ron LeMaster
 * @version $Revision:14443 $
 */
public interface AbstractVector2D {

    AbstractVector2D getAddedInstance(AbstractVector2D v);

    AbstractVector2D getSubtractedInstance(AbstractVector2D v);

    AbstractVector2D getAddedInstance(double x, double y);

    AbstractVector2D getSubtractedInstance(double x, double y);

    AbstractVector2D getScaledInstance(double scale);

    AbstractVector2D getNormalVector();

    AbstractVector2D getNormalizedInstance();

    double getY();

    double getX();

    double getMagnitudeSq();

    double getMagnitude();

    double dot(AbstractVector2D v);

    double getAngle();

    AbstractVector2D getInstanceOfMagnitude(double magnitude);

    Point2D toPoint2D();

    double getCrossProductScalar(AbstractVector2D v);

    Point2D getDestination(Point2D startPt);

    /**
     * Rerturns a new instance of AbstractVector2D, equal in magnitude to the original and
     * at a specified angle to the origina
     *
     * @param angle The anlge, in radians
     * @return
     */
    AbstractVector2D getRotatedInstance(double angle);

    public class Double implements AbstractVector2D {
        private double x;
        private double y;

        protected Double() {
            this(0, 0);
        }

        protected Double(double x, double y) {
            this.x = x;
            this.y = y;
        }

        protected Double(AbstractVector2D v) {
            this(v.getX(), v.getY());
        }

        protected Double(Point2D p) {
            this(p.getX(), p.getY());
        }

        protected Double(Point2D initialPt, Point2D finalPt) {
            this(finalPt.getX() - initialPt.getX(), finalPt.getY() - initialPt.getY());
        }

        public boolean equals(Object obj) {
            boolean result = true;
            if (this.getClass() != obj.getClass()) {
                result = false;
            } else {
                AbstractVector2D that = (AbstractVector2D) obj;
                result = this.getX() == that.getX() && this.getY() == that.getY();
            }
            return result;
        }

        public String toString() {
            return "AbstractVector2D.Double[" + x + ", " + y + "]";
        }

        public AbstractVector2D getAddedInstance(AbstractVector2D v) {
            return getAddedInstance(v.getX(), v.getY());
        }

        public AbstractVector2D getAddedInstance(double x, double y) {
            return new Double(getX() + x, getY() + y);
        }

        public AbstractVector2D getScaledInstance(double scale) {
            return new Double(getX() * scale, getY() * scale);
        }

        public AbstractVector2D getNormalVector() {
            return new Double(y, -x);
        }

        public AbstractVector2D getNormalizedInstance() {
            double mag = getMagnitude();
            return new AbstractVector2D.Double(getX() / mag, getY() / mag);
        }

        public AbstractVector2D getSubtractedInstance(double x, double y) {
            return new Double(getX() - x, getY() - y);
        }

        public AbstractVector2D getSubtractedInstance(AbstractVector2D v) {
            return getSubtractedInstance(v.getX(), v.getY());
        }

        public double getY() {
            return y;
        }

        public double getX() {
            return x;
        }

        public double getMagnitudeSq() {
            return getX() * getX() + getY() * getY();
        }

        public double getMagnitude() {
            return Math.sqrt(getMagnitudeSq());
        }

        protected void setX(double x) {
            this.x = x;
        }

        protected void setY(double y) {
            this.y = y;
        }

        public double dot(AbstractVector2D that) {
            double result = 0;
            result += this.getX() * that.getX();
            result += this.getY() * that.getY();
            return result;
        }

        public double getAngle() {
            return Math.atan2(y, x);
        }

        public AbstractVector2D getInstanceOfMagnitude(double magnitude) {
            return getScaledInstance(magnitude / getMagnitude());
        }

        public Point2D toPoint2D() {
            return new Point2D.Double(x, y);
        }

        public double getCrossProductScalar(AbstractVector2D v) {
            return (this.getMagnitude() * v.getMagnitude() * Math.sin(this.getAngle() - v.getAngle()));
        }

        public Point2D getDestination(Point2D startPt) {
            return new Point2D.Double(startPt.getX() + getX(), startPt.getY() + getY());
        }

        /**
         * Rerturns a new instance of AbstractVector2D.Float, equal in magnitude to the original and
         * at a specified angle to the origina
         *
         * @param angle The anlge, in radians
         * @return
         */
        public AbstractVector2D getRotatedInstance(double angle) {
            return parseAngleAndMagnitude(getMagnitude(), getAngle() + angle);
        }

        public static AbstractVector2D parseAngleAndMagnitude(double r, double angle) {
            AbstractVector2D vector = new AbstractVector2D.Double(Math.cos(angle), Math.sin(angle));
            return vector.getScaledInstance(r);
        }
    }


}
