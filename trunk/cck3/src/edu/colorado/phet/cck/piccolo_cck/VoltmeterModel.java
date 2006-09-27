package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.cck.model.Circuit;
import edu.colorado.phet.cck.model.CircuitListenerAdapter;
import edu.colorado.phet.cck.model.Connection;
import edu.colorado.phet.cck.model.Junction;
import edu.colorado.phet.cck.model.components.Branch;

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
    private double voltage = Double.NaN;
    private Circuit circuit;

    public VoltmeterModel( Circuit circuit ) {
        this.circuit = circuit;
        redLead = new LeadModel( circuit, new Point2D.Double( -0.2, 0 ), Math.PI / 8 );
        blackLead = new LeadModel( circuit, new Point2D.Double( 1.7, 0 ), -Math.PI / 8 );
        LeadModel.Listener listener = new LeadModel.Listener() {
            public void leadModelChanged() {
                updateVoltage();
            }
        };
        redLead.addListener( listener );
        blackLead.addListener( listener );
        unitModel = new UnitModel();
        this.bodyDragged( 3, 3 );
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                testUpdate();
            }

            public void junctionsMoved() {
                testUpdate();
            }

            public void junctionsConnected( Junction a, Junction b, Junction newTarget ) {
                testUpdate();
            }
        } );
    }

    private void testUpdate() {
        if( visible ) {
            updateVoltage();
        }
    }

    private void updateVoltage() {
        //todo only notify when there is a change
        this.voltage = new PiccoloVoltageCalculation( circuit ).getVoltage( redLead.getTipShape(), blackLead.getTipShape() );
        notifyListeners();
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible( boolean visible ) {
        this.visible = visible;
        updateVoltage();
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
        Connection a = redLead.getConnection();
        Connection b = blackLead.getConnection();
        if( a == null && b == null ) {
            return true;
        }
        else {
            return false;
        }
    }

    public double getVoltage() {
        return voltage;
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
        private double tipWidth = 0.1 * 0.35;
        private double tipHeight = 0.3 * 1.25;
        private Circuit circuit;

        public LeadModel( Circuit circuit, double angle ) {
            this( circuit, new Point2D.Double(), angle );
        }

        public LeadModel( Circuit circuit, Point2D.Double tipLocation, double angle ) {
            this.tipLocation = new Point2D.Double( tipLocation.getX(), tipLocation.getY() );
            this.angle = angle;
            this.circuit = circuit;
        }

        public void translate( double dx, double dy ) {
            tipLocation.x += dx;
            tipLocation.y += dy;
            notifyListeners();
        }

        public Point2D getTipLocation() {
            return new Point2D.Double( tipLocation.x, tipLocation.y );
        }

        public Shape getTipShape() {
            Rectangle2D.Double tip = new Rectangle2D.Double( tipLocation.x - tipWidth / 2, tipLocation.y, tipWidth, tipHeight );
            return AffineTransform.getRotateInstance( angle, tipLocation.x, tipLocation.y ).createTransformedShape( tip );
        }

        public double getAngle() {
            return angle;
        }

        public Connection getConnection() {
            return new PiccoloVoltageCalculation( circuit ).detectConnection( getTipShape() );
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
