package edu.colorado.phet.cck.piccolo_cck;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 10:08:51 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class VoltmeterModel {
    private boolean visible = false;
    private UnitModel unitModel;
    private ArrayList listeners = new ArrayList();
    private LeadModel redLead;
    private LeadModel blackLead;

    public VoltmeterModel() {
        redLead = new LeadModel( new Point2D.Double( 0, 0 ), Math.PI / 8 );
        blackLead = new LeadModel( new Point2D.Double( 1, 0 ), -Math.PI / 8 );
        unitModel = new UnitModel();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        System.out.println( "visible = " + visible );
        this.visible = visible;
        notifyListeners();
    }

    public LeadModel getBlackLeadModel() {
        return blackLead;
    }

    public LeadModel getRedLeadModel() {
        return redLead;
    }

    public UnitModel getUnitModel() {
        return unitModel;
    }

    public void bodyDragged( double dx, double dy ) {
        unitModel.bodyDragged( dx, dy );
        if( getLeadsShouldTranslateWithBody() ) {
            redLead.translate( dx, dy );
            blackLead.translate( dx, dy );
        }
    }

    private boolean getLeadsShouldTranslateWithBody() {
        return true;
    }

    public static interface Listener {
        void voltmeterChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public void notifyListeners() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.voltmeterChanged();
        }
    }

    public static class LeadModel {
        private Point2D.Double tipLocation;
        private Point2D.Double tailLocation;
        private ArrayList listeners = new ArrayList();
        private double angle;

        public LeadModel( double angle ) {
            this( new Point2D.Double(), angle );
        }

        public LeadModel( Point2D.Double tipLocation, double angle ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
            this.angle = angle;
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyListeners();
        }

        public Point2D getTipLocation() {
            return tipLocation;
        }

        public Shape getTipShape() {
            Rectangle2D.Double tip = new Rectangle2D.Double( tipLocation.x, tipLocation.y, 0.1, 0.5 );
            Shape sh = AffineTransform.getRotateInstance( angle, tipLocation.x, tipLocation.y ).createTransformedShape( tip );
            return sh;
        }

        public double getAngle() {
            return angle;
        }

        static interface Listener {
            void leadModelChanged();
        }


        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyListeners() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.leadModelChanged();
            }
        }
    }

    public static class UnitModel {
        private Point2D.Double location = new Point2D.Double();
        private ArrayList listeners = new ArrayList();

        public Point2D getLocation() {
            return new Point2D.Double( location.x, location.y );
        }

        public void bodyDragged( double dx, double dy ) {
            location.x += dx;
            location.y += dy;
            notifyListeners();
        }

        static interface Listener {
            void unitModelChanged();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyListeners() {
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.unitModelChanged();
            }
        }
    }
}
