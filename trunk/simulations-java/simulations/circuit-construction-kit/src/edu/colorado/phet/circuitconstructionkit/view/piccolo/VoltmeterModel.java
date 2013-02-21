// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.CircuitListenerAdapter;
import edu.colorado.phet.circuitconstructionkit.model.Connection;
import edu.colorado.phet.circuitconstructionkit.model.Junction;
import edu.colorado.phet.circuitconstructionkit.model.analysis.CircuitSolutionListener;
import edu.colorado.phet.circuitconstructionkit.model.components.Branch;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IModelAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ModelComponentTypes;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 10:08:51 AM
 */

public class VoltmeterModel {
    private boolean visible = false;
    private UnitModel unitModel;
    private ArrayList listeners = new ArrayList();
    private LeadModel redLead;
    private Connection prevRedLeadConnection = null;
    private LeadModel blackLead;
    private Connection prevBlackLeadConnection = null;
    private double voltage = Double.NaN;
    private CCKModel model;
    private Circuit circuit;

    public VoltmeterModel( CCKModel model, Circuit circuit ) {
        this.model = model;
        this.circuit = circuit;
        redLead = new LeadModel( circuit, new Point2D.Double( -0.2, 0 ), Math.PI / 8 );
        blackLead = new LeadModel( circuit, new Point2D.Double( 1.7 * 0.72, 0 ), -Math.PI / 8 );
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
        model.getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                testUpdate();
            }
        } );
    }

    private void testUpdate() {
        if ( visible ) {
            updateVoltage();
        }
    }

    DelayedRunner runner = new DelayedRunner();

    private void updateVoltage() {
        final double voltage = circuit.getVoltage( redLead.getTipShape(), blackLead.getTipShape() );

        if ( voltage != this.voltage && !( Double.isNaN( this.getVoltage() ) && Double.isNaN( voltage ) ) ) {
            this.voltage = voltage;
            notifyListeners();

            //Limit messages to every 500ms
            runner.set( new Runnable() {
                public void run() {
                    // Send out sim sharing message indication that voltage has changed.
                    String voltageString = Double.isNaN( voltage ) ? "undefined" : VoltmeterNode.UnitNode.decimalFormat.format( voltage );
                    SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.voltmeterModel,
                                                        ModelComponentTypes.modelElement,
                                                        CCKSimSharing.ModelActions.measuredVoltageChanged,
                                                        new ParameterSet( new Parameter( new ParameterKey( "voltage" ), voltageString ) ) );
                }
            } );
        }

        // Send out sim sharing message if connection state of leads has changed.
        if ( redLead.getConnection() != prevRedLeadConnection && ( redLead.getConnection() == null || prevRedLeadConnection == null ) ) {
            IModelAction modelAction = prevRedLeadConnection == null ? CCKSimSharing.ModelActions.connectionFormed : CCKSimSharing.ModelActions.connectionBroken;
            SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.voltmeterRedLeadModel,
                                                ModelComponentTypes.modelElement,
                                                modelAction, getConnectionMessage( redLead )
            );
            prevRedLeadConnection = redLead.getConnection();
        }
        if ( blackLead.getConnection() != prevBlackLeadConnection && ( blackLead.getConnection() == null || prevBlackLeadConnection == null ) ) {
            IModelAction modelAction = prevBlackLeadConnection == null ? CCKSimSharing.ModelActions.connectionFormed : CCKSimSharing.ModelActions.connectionBroken;
            SimSharingManager.sendModelMessage( CCKSimSharing.ModelComponents.voltmeterBlackLeadModel,
                                                ModelComponentTypes.modelElement,
                                                modelAction, getConnectionMessage( blackLead )
            );
            prevBlackLeadConnection = blackLead.getConnection();
        }
    }

    //Output the connections formed by the voltmeter probes, including adjacent branches, see #3497
    private ParameterSet getConnectionMessage( LeadModel probe ) {
        Connection c = probe.getConnection();
        String text = "";
        if ( c == null ) {
            text = "none";
        }
        else if ( c instanceof Connection.BranchConnection ) {
            Connection.BranchConnection b = (Connection.BranchConnection) c;
            text = "branch: " + b.getBranch().getUserComponentID().toString();
        }
        else if ( c instanceof Connection.JunctionConnection ) {
            Connection.JunctionConnection jc = (Connection.JunctionConnection) c;
            Branch[] branches = this.model.getCircuit().getAdjacentBranches( jc.getJunction() );
            String branchesString = "";
            for ( int i = 0; i < branches.length; i++ ) {
                Branch branch = branches[i];
                if ( i == 0 ) {
                    branchesString = branch.getUserComponentID().toString();
                }
                else {
                    branchesString = branchesString + ", " + branch.getUserComponentID().toString();
                }
            }
            text = "junction: " + jc.getJunction().getLabel() + ", branches: " + branchesString;
        }
        ParameterSet originalSet = ParameterSet.parameterSet( CCKSimSharing.ParameterKeys.connections, text );
        return c == null ? originalSet : originalSet.with( CCKSimSharing.ParameterKeys.voltageAddon, c.getVoltageAddon() );
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
        if ( getLeadsShouldTranslateWithBody() ) {
            redLead.translate( dx, dy );
            blackLead.translate( dx, dy );
        }
    }

    private boolean getLeadsShouldTranslateWithBody() {
        Connection a = redLead.getConnection();
        Connection b = blackLead.getConnection();
        if ( a == null && b == null ) {
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
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.voltmeterChanged();
        }
    }

    public static class LeadModel {
        private Point2D.Double tipLocation;
        private Point2D.Double tailLocation;
        private ArrayList listeners = new ArrayList();
        private double angle;
        private double tipWidth = 0.1 * 0.35;
        private double tipHeight = 0.3 * 1.25 * 0.75;
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
            return circuit.getConnection( getTipShape() );
        }

        static interface Listener {
            void leadModelChanged();
        }

        public void addListener( Listener listener ) {
            listeners.add( listener );
        }

        public void notifyListeners() {
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
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
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.unitModelChanged();
            }
        }
    }
}
