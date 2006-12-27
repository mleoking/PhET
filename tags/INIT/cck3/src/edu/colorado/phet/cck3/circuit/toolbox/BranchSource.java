/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.toolbox;

import edu.colorado.phet.cck3.ComponentDimension;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.BoundedGraphic;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 2:32:10 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public abstract class BranchSource extends DefaultInteractiveGraphic {
    private CircuitGraphic circuitGraphic;
    private Branch branch;
    private KirkhoffListener kirkhoffListener;
    private boolean created = false;

    protected BranchSource( BoundedGraphic boundedGraphic, final CircuitGraphic circuitGraphic, final ApparatusPanel panel,
                            Branch branch, KirkhoffListener kl ) {
        super( boundedGraphic );
        this.circuitGraphic = circuitGraphic;
        this.branch = branch;
        this.kirkhoffListener = kl;
        addCursorHandBehavior();
        MouseInputAdapter ad = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Branch b = createBranch();
                circuitGraphic.getCircuit().addBranch( b );
                //add the graphic.
                circuitGraphic.addGraphic( b );
                created = true;
                //transfer control to the new component.
                InteractiveGraphic g = circuitGraphic.getGraphic( b );
                panel.getGraphic().startDragging( g, e );
            }
        };
        addMouseInputListener( ad );
    }

    public abstract Branch createBranch();

    private Vector2D getDirection() {
        return new Vector2D.Double( branch.getStartJunction().getPosition(), branch.getEndJunction().getPosition() );
    }

    public static class WireSource extends BranchSource {
        private double finalLength;

        public WireSource( Branch branch, BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic, ApparatusPanel panel, KirkhoffListener kl, double finalLength ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.finalLength = finalLength;
        }

        public Branch createBranch() {
            AbstractVector2D dir = new Vector2D.Double( super.branch.getStartJunction().getPosition(), super.branch.getEndJunction().getPosition() );
            dir = dir.getInstanceOfMagnitude( -finalLength );
            Point2D src = super.branch.getEndJunction().getPosition();
            Point2D dst = dir.getDestination( src );
            Branch b = new Branch( super.kirkhoffListener, new Junction( src.getX(), src.getY() ), new Junction( dst.getX(), dst.getY() ) );
            return b;
        }

    }

    public static class BatterySource extends BranchSource {
        private ComponentDimension finalDim;

        public BatterySource( BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Branch branch, ComponentDimension finalDim, KirkhoffListener kl ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.finalDim = finalDim;
        }

        public Branch createBranch() {
            AbstractVector2D dir = new Vector2D.Double( super.branch.getStartJunction().getPosition(), super.branch.getEndJunction().getPosition() );
            dir = dir.getInstanceOfMagnitude( finalDim.getLength() );
            Battery batt = new Battery( super.branch.getStartJunction().getPosition(), dir, dir.getMagnitude(), finalDim.getHeight(), super.kirkhoffListener );
            return batt;
        }
    }

    public static class BulbSource extends BranchSource {
        private ComponentDimension finalDim;
        private double distBetweenJunctions;

        public BulbSource( BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic,
                           ApparatusPanel panel, Branch branch, ComponentDimension finalDim, KirkhoffListener kl,
                           double distBetweenJunctions ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.finalDim = finalDim;
            this.distBetweenJunctions = distBetweenJunctions;
        }

        public Branch createBranch() {
            AbstractVector2D dir = super.getDirection();
            dir = dir.getInstanceOfMagnitude( finalDim.getLength() );
            Bulb bulb = new Bulb( super.branch.getStartJunction().getPosition(), dir, distBetweenJunctions, dir.getMagnitude(), finalDim.getHeight(), super.kirkhoffListener );
//            Bulb bulb = null;
            return bulb;
        }
    }

    public static class ResistorSource extends BranchSource {
        private ComponentDimension cd;

        public ResistorSource( BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic,
                               ApparatusPanel panel, Branch branch, KirkhoffListener kl, ComponentDimension cd ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.cd = cd;
        }

        public Branch createBranch() {
            AbstractVector2D dir = super.getDirection();
            dir = dir.getInstanceOfMagnitude( cd.getLength() );
            Resistor res = new Resistor( super.branch.getStartJunction().getPosition(), dir, dir.getMagnitude(), cd.getHeight(), super.kirkhoffListener );
            return res;
        }
    }

    public static class SwitchSource extends BranchSource {
        private ComponentDimension cd;

        public SwitchSource( BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Branch branch, KirkhoffListener kl, ComponentDimension cd ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.cd = cd;
        }

        public Branch createBranch() {
            AbstractVector2D dir = super.getDirection();
            dir = dir.getInstanceOfMagnitude( cd.getLength() );
            Switch myswitch = new Switch( super.branch.getStartJunction().getPosition(), dir, dir.getMagnitude(), cd.getHeight(), super.kirkhoffListener );
            return myswitch;
        }
    }

    public static class AmmeterSource extends BranchSource {
        private AbstractVector2D dir;
        private double length;
        private double height;

        public AmmeterSource( BoundedGraphic boundedGraphic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Branch branch, KirkhoffListener kl,
                              AbstractVector2D dir, double length, double height ) {
            super( boundedGraphic, circuitGraphic, panel, branch, kl );
            this.dir = dir;
            this.length = length;
            this.height = height;
        }

        public Branch createBranch() {
            SeriesAmmeter sam = new SeriesAmmeter( super.kirkhoffListener, super.branch.getStartJunction().getPosition(), dir, length, height );
            return sam;
        }
    }

}
