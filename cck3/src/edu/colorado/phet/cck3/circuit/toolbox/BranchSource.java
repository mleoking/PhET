/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.toolbox;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.ComponentDimension;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.Junction;
import edu.colorado.phet.cck3.circuit.KirkhoffListener;
import edu.colorado.phet.cck3.circuit.components.*;
import edu.colorado.phet.cck3.common.PhetTooltipControl;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.util.SimStrings;

import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;

/**
 * User: Sam Reid
 * Date: May 29, 2004
 * Time: 2:32:10 AM
 * Copyright (c) May 29, 2004 by Sam Reid
 */
public abstract class BranchSource extends DefaultInteractiveGraphic {
    private PhetGraphic schematic;
    private CircuitGraphic circuitGraphic;
    private Branch branch;
    private KirkhoffListener kirkhoffListener;
    private CCK3Module module;
    private PhetGraphic lifelike;
    private PhetTextGraphic textGraphic;
    private PhetShapeGraphic shapeGraphic;//for debugging.
    private TransformListener tl;

    protected BranchSource( final PhetGraphic lifelike,
                            final PhetGraphic schematic,
                            final CircuitGraphic circuitGraphic, final ApparatusPanel panel,
                            final Branch branch, KirkhoffListener kl, String name, final CCK3Module module ) {
        super( lifelike );
        this.lifelike = lifelike;
        this.schematic = schematic;
        this.circuitGraphic = circuitGraphic;
        this.branch = branch;
        this.kirkhoffListener = kl;
        this.module = module;
        addCursorHandBehavior();
        MouseInputAdapter ad = new MouseInputAdapter() {
            public void mouseDragged( MouseEvent e ) {
                Branch b = createBranch();
                circuitGraphic.getCircuit().addBranch( b );
                //add the graphic.
                circuitGraphic.addGraphic( b );
                //transfer control to the new component.
                InteractiveGraphic g = circuitGraphic.getGraphic( b );
                panel.getMouseDelegator().startDragging( e, g );
            }
        };
        addMouseInputListener( ad );
        addMouseInputListener( new PhetTooltipControl( panel, name ) );

        Point2D.Double loc = branch.getStartJunction().getPosition();
        Point vpt = module.getTransform().modelToView( loc );
        textGraphic = new PhetTextGraphic( panel, new Font( "Lucida Sans", Font.PLAIN, 11 ), name, Color.black, vpt.x, vpt.y );
        textGraphic.setVisible( true );
        shapeGraphic = new PhetShapeGraphic( panel, new Area(), new BasicStroke( 4 ), Color.red );
//        shapeGraphic.setVisible( true );
        shapeGraphic.setVisible( false );
        tl = new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                PhetGraphic on = (PhetGraphic)getGraphic();
                if( on == null ) {
                    throw new RuntimeException( "Null Graphic, branch=" + branch );
//                    new RuntimeException( "Null graphic").printStackTrace( );
//                    return;
                }
                Rectangle rect = on.getBounds();
                if( rect == null ) {
                    if( on instanceof Toolbox.TestCG ) {
                        Toolbox.TestCG cpg = (Toolbox.TestCG)on;
                        cpg.setBoundsDirty();
                        rect = cpg.getBounds();
                    }
                    if( rect == null ) {
                        throw new RuntimeException( "Bounds was null, branch=" + branch );
                    }
//                    new RuntimeException( "Bounds was null").printStackTrace( );
//                    return;
                }
                Point bc = RectangleUtils.getBottomCenter( rect );
                int width = textGraphic.getBounds().width;
                int height = textGraphic.getBounds().height;
                Point at = new Point( bc.x - width / 2, bc.y + height );
                textGraphic.setPosition( at.x, at.y );
//                shapeGraphic.setShape( new Rectangle( rect ) );
                if( branch.getClass().equals( Branch.class ) ) {
//                    System.out.println( "rect = " + rect );
                }
                else if( branch instanceof Switch ) {
//                    System.out.println( "rect = " + rect );
                }
            }
        };
        module.getTransform().addTransformListener( tl );
        tl.transformChanged( module.getTransform() );
    }

    public void paint( Graphics2D g ) {
        super.paint( g );    //To change body of overridden methods use File | Settings | File Templates.
        textGraphic.paint( g );
        shapeGraphic.paint( g );
    }

    public void setLifelike( boolean lifelike ) {
        if( lifelike ) {
            setBoundedGraphic( this.lifelike );
            tl.transformChanged( module.getTransform() );
        }
        else {
            setBoundedGraphic( this.schematic );
            tl.transformChanged( module.getTransform() );
        }
    }

    public abstract Branch createBranch();

    private Vector2D getDirection() {
        return new Vector2D.Double( branch.getStartJunction().getPosition(), branch.getEndJunction().getPosition() );
    }

    public static class WireSource extends BranchSource {
        private double finalLength;

        public WireSource( Branch branch, PhetGraphic boundedGraphic, PhetGraphic schematic, CircuitGraphic circuitGraphic, ApparatusPanel panel, KirkhoffListener kl, double finalLength, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.Wire" ), module );
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

        public BatterySource( PhetGraphic boundedGraphic, PhetGraphic schematic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Branch branch, ComponentDimension finalDim, KirkhoffListener kl, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.Battery" ), module );
            this.finalDim = finalDim;
        }

        public Branch createBranch() {
            AbstractVector2D dir = new Vector2D.Double( super.branch.getStartJunction().getPosition(), super.branch.getEndJunction().getPosition() );
            dir = dir.getInstanceOfMagnitude( finalDim.getLength() );
            Battery batt = new Battery( super.branch.getStartJunction().getPosition(), dir, dir.getMagnitude(), finalDim.getHeight(), super.kirkhoffListener, super.module.isInternalResistanceOn() );
            batt.setInternalResistance( Battery.DEFAULT_INTERNAL_RESISTANCE );
            return batt;
        }
    }

    public static class BulbSource extends BranchSource {
        private ComponentDimension finalDim;
        private double distBetweenJunctions;

        public BulbSource( PhetGraphic boundedGraphic, CircuitGraphic circuitGraphic,
                           ApparatusPanel panel, Bulb branch, PhetGraphic schematic, ComponentDimension finalDim, KirkhoffListener kl,
                           double distBetweenJunctions, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.LightBulb" ), module );
            this.finalDim = finalDim;
            this.distBetweenJunctions = distBetweenJunctions;
        }

        public Branch createBranch() {
            AbstractVector2D dir = super.getDirection();
            dir = dir.getInstanceOfMagnitude( finalDim.getLength() );
            Point2D start = super.branch.getStartJunction().getPosition();
            if( !super.circuitGraphic.isLifelike() ) {
                dir = new ImmutableVector2D.Double( 1, 0 );
                start = new Point2D.Double( start.getX() - distBetweenJunctions, start.getY() );
            }
            Bulb bulb = new Bulb( start, dir, distBetweenJunctions, dir.getMagnitude(), finalDim.getHeight(), super.kirkhoffListener );
            if( super.circuitGraphic.isLifelike() ) {
                return bulb;
            }
            else {
                bulb.setSchematic( true, super.circuitGraphic.getCircuit() );
                return bulb;
            }
        }
    }

    public static class ResistorSource extends BranchSource {
        private ComponentDimension cd;

        public ResistorSource( PhetGraphic boundedGraphic, PhetGraphic schematic, CircuitGraphic circuitGraphic,
                               ApparatusPanel panel, Branch branch, KirkhoffListener kl, ComponentDimension cd, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.Resistor" ), module );
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
        Switch myswitch;

        public SwitchSource( PhetGraphic boundedGraphic, PhetGraphic schematic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Switch branch, KirkhoffListener kl, ComponentDimension cd, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.Switch" ), module );
            this.cd = cd;
            this.myswitch = branch;
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

        public AmmeterSource( PhetGraphic boundedGraphic, PhetGraphic schematic, CircuitGraphic circuitGraphic, ApparatusPanel panel, Branch branch, KirkhoffListener kl,
                              AbstractVector2D dir, double length, double height, CCK3Module module ) {
            super( boundedGraphic, schematic, circuitGraphic, panel, branch, kl, SimStrings.get( "BranchSource.Ammeter" ), module );
            this.dir = dir;
            this.length = length;
            this.height = height;
        }

        public Branch createBranch() {
            SeriesAmmeter sam = new SeriesAmmeter( super.kirkhoffListener, super.branch.getStartJunction().getPosition(), dir, length, height );
            return sam;
        }

        public void setVisible( boolean visible ) {
            super.setVisible( visible );    //To change body of overridden methods use File | Settings | File Templates.
            super.textGraphic.setVisible( visible );
        }
    }

}
