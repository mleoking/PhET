/**
 * Class: Lobe
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: Sep 23, 2003
 */
package edu.colorado.phet.microwave.model;

import java.awt.geom.Point2D;

public class Lobe {
        private double radius;
        private double oldX, oldY;
        private Point2D.Double center = new Point2D.Double();
        private Point2D.Double centerPrevious = new Point2D.Double();

        public double getRadius() {
            return radius;
        }

        public void setRadius( double radius ) {
            this.radius = radius;
        }

        public Point2D.Double getCenter() {
            return center;
        }

        public double getCenterX() {
            return center.getX();
        }

        public double getCenterY() {
            return center.getY();
        }

        public double getCenterPreviousX() {
            return centerPrevious.getX();
        }

        public double getCenterPreviousY() {
            return centerPrevious.getY();
        }

        public double getDistanceSq( Lobe that ) {
            return this.getCenter().distanceSq( that.getCenter() );
        }

        public double getPrevDistanceSq( Lobe that ) {
            return this.getCenterPrevious().distanceSq( that.getCenterPrevious() );
        }

        public void setCenter( double x, double y ) {
            centerPrevious.setLocation( center.getX(), center.getY() );
            center.setLocation( x, y );
        }

        private Point2D.Double getCenterPrevious() {
            return centerPrevious;
        }
    }
