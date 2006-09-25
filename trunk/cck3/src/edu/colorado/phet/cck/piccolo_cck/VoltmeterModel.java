package edu.colorado.phet.cck.piccolo_cck;

import java.awt.geom.Point2D;
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
        redLead = new LeadModel( new Point2D.Double( 0, 0 ) );
        blackLead = new LeadModel( new Point2D.Double( 1, 0 ) );
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

        public LeadModel() {
            this( new Point2D.Double() );
        }

        public LeadModel( Point2D.Double tipLocation ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyListeners();
        }

        public Point2D getTipLocation() {
            return tipLocation;
        }

        static interface Listener {
            void leadModelChanged();
        }

        ArrayList listeners = new ArrayList();

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

        public Point2D getLocation() {
            return new Point2D.Double( location.x, location.y );
        }

        public void translateBody( double dx, double dy ) {
            location.x += dx;
            location.y += dy;
            notifyListeners();
        }

        static interface Listener {
            void unitModelChanged();
        }

        ArrayList listeners = new ArrayList();

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
