/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.tools;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.debug.SimpleObservableDebug;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.CompositeGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic;
import edu.colorado.phet.common.view.util.RectangleUtils;
import edu.colorado.phet.common.view.util.SimStrings;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 17, 2004
 * Time: 2:17:01 PM
 * Copyright (c) Jun 17, 2004 by Sam Reid
 */
public class VoltmeterGraphic extends CompositeGraphic {
    private UnitGraphic unitGraphic;
    private Voltmeter voltmeter;
    private ModelViewTransform2D transform;
    private LeadGraphic redLeadGraphic;
    private LeadGraphic blackLeadGraphic;
    private CableGraphic redCableGraphic;
    private CableGraphic blackCableGraphic;
    private CCK3Module module;

    public void setVisible( boolean visible ) {
        super.setVisible( visible );
        redLeadGraphic.setVisible( visible );
        blackLeadGraphic.setVisible( visible );
        redCableGraphic.setVisible( visible );
        blackCableGraphic.setVisible( visible );
        unitGraphic.setVisible( visible );
    }

    public VoltmeterGraphic( Voltmeter voltmeter, Component parent, CCK3Module module ) throws IOException {
        this.transform = module.getTransform();
        this.voltmeter = voltmeter;
        this.module = module;
        unitGraphic = new UnitGraphic( voltmeter.getUnit(), module.loadBufferedImage( "images/vm3.gif" ), parent, module.getDecimalFormat() );
        addGraphic( unitGraphic );
        redLeadGraphic = new LeadGraphic( voltmeter.getRedLead(), module.loadBufferedImage( "images/probeRed.gif" ), parent, Math.PI / 8 );
        blackLeadGraphic = new LeadGraphic( voltmeter.getBlackLead(), module.loadBufferedImage( "images/probeBlack.gif" ), parent, -Math.PI / 8 );

        redCableGraphic = new CableGraphic( transform, Color.red, redLeadGraphic, new Point2D.Double( 12, 218 ) );
        blackCableGraphic = new CableGraphic( transform, Color.black, blackLeadGraphic, new Point2D.Double( 88, 218 ) );
        voltmeter.getRedLead().addObserver( new SimpleObserver() {
            public void update() {
                redCableGraphic.changed();
            }
        } );
        voltmeter.getBlackLead().addObserver( new SimpleObserver() {
            public void update() {
                blackCableGraphic.changed();
            }
        } );
        addGraphic( redCableGraphic );
        addGraphic( redLeadGraphic );

        addGraphic( blackCableGraphic );
        addGraphic( blackLeadGraphic );
    }

    public LeadGraphic getRedLeadGraphic() {
        return redLeadGraphic;
    }

    public LeadGraphic getBlackLeadGraphic() {
        return blackLeadGraphic;
    }

    public class LeadGraphic extends PhetImageGraphic {
        private Voltmeter.Lead lead;
        private double angle;
        private Stroke stroke = new BasicStroke( 1.5f );
        private InteractiveBranchGraphic overlap;

        public LeadGraphic( Voltmeter.Lead lead, BufferedImage bufferedImage, Component parent, double angle ) {
            super( parent, bufferedImage, new AffineTransform() );
            this.lead = lead;
            this.angle = angle;
            VoltmeterGraphic.this.transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2D mvt ) {
                    changed();
                }
            } );

            lead.addObserver( new SimpleObserver() {
                public void update() {
                    changed();
                }
            } );
            changed();
        }

        private void changed() {
            Point2D.Double loc = new Point2D.Double( lead.getX(), lead.getY() );
            Point ctr = VoltmeterGraphic.this.transform.modelToView( loc );
            double imWidth = getImage().getWidth();
            double imHeight = getImage().getHeight();

            Point2D tx = new Point2D.Double( ctr.x - imWidth / 2, ctr.y - imHeight / 2 );
            AffineTransform imageTransform = AffineTransform.getTranslateInstance( tx.getX(), tx.getY() );
            imageTransform.rotate( angle );//tx.getX(), tx.getY() );
            super.setTransform( imageTransform );
            VoltmeterGraphic.this.recomputeVoltage();
        }

        public Shape getTipShape() {
            int tipWidth = 3;
            int tipHeight = 25;
            int imWidth = getImage().getWidth();
            Rectangle r = new Rectangle( imWidth / 2 - tipWidth / 2, 0, tipWidth, tipHeight );
            return getTransform().createTransformedShape( r );
        }

        public Point2D getInputPoint() {
            BufferedImage image = super.getImage();
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            Point2D pt = new Point2D.Double( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() );
            return getTransform().transform( pt, null );
        }

        public void paint( Graphics2D graphics2D ) {
            super.paint( graphics2D );
            Stroke origStroke = graphics2D.getStroke();
            graphics2D.setColor( Color.white );
            graphics2D.setStroke( stroke );
            Shape tipShape = getTipShape();
            graphics2D.draw( tipShape );
            graphics2D.setColor( Color.gray );
            graphics2D.fill( tipShape );
            graphics2D.setStroke( origStroke );
        }

        private Branch detectBranch( CircuitGraphic circuitGraphic ) {
            Graphic[] g = circuitGraphic.getBranchGraphics();
            Shape tipShape = getTipShape();
            overlap = null;
            for( int i = g.length - 1; i >= 0; i-- ) {
                Graphic graphic = g[i];
                if( graphic instanceof InteractiveBranchGraphic ) {
                    InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                    Shape shape = ibg.getBranchGraphic().getCoreShape();//getShape();
                    Area intersection = new Area( tipShape );
                    intersection.intersect( new Area( shape ) );
                    if( !intersection.isEmpty() ) {
                        this.overlap = ibg;
                        break;
                    }
                }
            }
//            System.out.println( "overlap = " + overlap );
            if( overlap == null ) {
                return null;
            }
            else {
                return overlap.getBranch();
            }
        }

        public Connection detectConnection( CircuitGraphic circuitGraphic ) {
            Branch branch = detectBranch( circuitGraphic );
            Junction junction = detectJunction( circuitGraphic );
            Connection result = null;
            if( junction != null ) {
                result = new JunctionConnection( junction );
            }
            else if( branch != null ) {
                //could choose the closest junction
                //but we want a potentiometer.
                result = new JunctionConnection( branch.getStartJunction() );
                Rectangle tipRect = getTipShape().getBounds();
                Point2D tipCenter = RectangleUtils.getCenter( tipRect );
                Point2D tipCenterModel = circuitGraphic.getTransform().viewToModel( new Point( (int)tipCenter.getX(), (int)tipCenter.getY() ) );
                Point2D.Double branchStartModel = branch.getStartJunction().getPosition();
//                Point2D branchStartModel = circuitGraphic.getTransform().viewToModel( (int)branchCenterView.getX(), (int)branchCenterView.getY() );
                Vector2D vec = new Vector2D.Double( branchStartModel, tipCenterModel );
                double dist = vec.getMagnitude();
                result = new BranchConnection( branch, dist );
            }
            return result;
        }

        private Junction detectJunction( CircuitGraphic circuitGraphic ) {
            Graphic[] j = circuitGraphic.getJunctionGraphics();
            Shape tipShape = getTipShape();
            Junction junction = null;
            for( int i = 0; i < j.length; i++ ) {
                Graphic graphic = j[i];
                HasJunctionGraphic hj = (HasJunctionGraphic)graphic;
                JunctionGraphic jg = hj.getJunctionGraphic();
                Area area = new Area( jg.getShape() );
                area.intersect( new Area( tipShape ) );
                if( !area.isEmpty() ) {
                    junction = jg.getJunction();
                    break;
                }
            }
            return junction;
        }

        public void setAngle( double angle ) {
            this.angle = angle;
            changed();
        }
    }

    public void recomputeVoltage() {
        if( !isVisible() ) {
            return;
        }
        if( redLeadGraphic == null || blackLeadGraphic == null ) {
            return;
        }

        Area tipIntersection = new Area( redLeadGraphic.getTipShape() );
        tipIntersection.intersect( new Area( blackLeadGraphic.getTipShape() ) );
        if( !tipIntersection.isEmpty() ) {
            unitGraphic.setVoltage( 0 );
        }
        else {
            Connection red = redLeadGraphic.detectConnection( module.getCircuitGraphic() );
            Connection black = blackLeadGraphic.detectConnection( module.getCircuitGraphic() );
            if( red == null || black == null ) {
                unitGraphic.setUnknownVoltage();
            }
            else {
                //dfs from one branch to the other, counting the voltage drop.
                double volts = module.getCircuit().getVoltage( red, black );
                if( Double.isInfinite( volts ) ) {
                    unitGraphic.setUnknownVoltage();
                }
                else {
                    unitGraphic.setVoltage( volts );
                }
            }
        }
    }

    public abstract static class Connection {
        public abstract Junction getJunction();

        public abstract double getVoltageAddon();
    }

    public static class JunctionConnection extends Connection {
        Junction junction;

        public JunctionConnection( Junction junction ) {
            this.junction = junction;
        }

        public boolean equals( Object o ) {
            if( o instanceof JunctionConnection ) {
                JunctionConnection jc = (JunctionConnection)o;
                if( jc.junction == junction ) {
                    return true;
                }
            }
            return false;
        }

        public Branch[] getBranchesToIgnore() {
            return new Branch[0];
        }

        public Junction getJunction() {
            return junction;
        }

        public double getVoltageAddon() {
            return 0;
        }
    }

    public static class BranchConnection extends Connection {
        Branch branch;
        double dist;

        public BranchConnection( Branch branch, double dist ) {
            this.branch = branch;
            this.dist = dist;
        }

        public Junction getJunction() {
            return branch.getStartJunction();
        }

        public double getVoltageAddon() {
//            System.out.println( "Getting voltage ADDON, junction=" + getJunction() + ", branch = " + branch );
            double resistance = branch.getResistance();
            double length = branch.getLength();
            double resistivity = resistance / length; //infer a resistivity.
            double incrementalResistance = dist * resistivity;
            double current = branch.getCurrent();
            double voltage = current * incrementalResistance;//the sign is probably right
//            System.out.println( "dist=" + dist + ", resistance = " + resistance + ", incrementalRes=" + incrementalResistance + ", current=" + current + ", DV=" + voltage );
            return voltage;
        }

    }

    public Voltmeter getVoltmeter() {
        return voltmeter;
    }

    public VoltmeterGraphic.UnitGraphic getUnitGraphic() {
        return unitGraphic;
    }

    public CableGraphic getRedCableGraphic() {
        return redCableGraphic;
    }

    public CableGraphic getBlackCableGraphic() {
        return blackCableGraphic;
    }

    class UnitGraphic extends CompositeGraphic {
        PhetImageGraphic unitGraphic;
        PhetTextGraphic textGraphic;
        Font font = new Font( "Dialog", 0, 20 );
        private Voltmeter.VoltmeterUnit vm;
        private Point ctr;
        double relX = -35;
        double relY = -73;
        private final String UNKNOWN_VOLTS = SimStrings.get( "VoltmeterGraphic.UnknownVolts" );
        private String voltageString = UNKNOWN_VOLTS;
        private DecimalFormat voltFormatter;

        public UnitGraphic( Voltmeter.VoltmeterUnit vm, BufferedImage image, Component parent, DecimalFormat voltFormatter ) {
            this.voltFormatter = voltFormatter;
            unitGraphic = new PhetImageGraphic( parent, image );
            textGraphic = new PhetTextGraphic( parent, font, UNKNOWN_VOLTS, Color.black, 0, 0 );
            addGraphic( unitGraphic );
            addGraphic( textGraphic );
            vm.addObserver( new SimpleObserver() {
                public void update() {
                    changed();
                }
            } );
            transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2D mvt ) {
                    changed();
                }
            } );
            this.vm = vm;
            changed();
            setVisible( true );
        }

        public void setVisible( boolean visible ) {
            super.setVisible( visible );
            unitGraphic.setVisible( visible );
            textGraphic.setVisible( visible );
        }

        public Rectangle getUnitBounds() {
            return unitGraphic.getBounds();
        }

        private void changed() {
            Point2D.Double loc = new Point2D.Double( vm.getX(), vm.getY() );
            ctr = transform.modelToView( loc );
            unitGraphic.setPositionCentered( (int)ctr.getX(), (int)ctr.getY() );
            textGraphic.setPosition( (int)( ctr.getX() + relX ), (int)( ctr.getY() + relY ) );
        }

        public Point getPosition() {
            return ctr;
        }

        public void setVoltage( double volts ) {
            voltageString = voltFormatter.format( volts );
            if( Double.parseDouble( voltageString ) == 0 ) {
                voltageString = voltFormatter.format( 0 );
            }
            textGraphic.setText( voltageString + " " + SimStrings.get( "VoltmeterGraphic.VoltAbrev" ) );
        }

        public void setUnknownVoltage() {
            textGraphic.setText( UNKNOWN_VOLTS );
        }
    }

    public class CableGraphic extends SimpleObservableDebug implements Graphic {
        private ModelViewTransform2D transform;
        private Color color;
        private LeadGraphic leadGraphic;
        private Point2D rel;
        private CubicCurve2D.Double cableCurve;
        private PhetShapeGraphic graphic;

        public CableGraphic( ModelViewTransform2D transform, Color color, LeadGraphic leadGraphic, Point2D rel ) {
            this.transform = transform;
            this.color = color;
            this.leadGraphic = leadGraphic;
            this.rel = rel;
            Stroke stroke = ( new BasicStroke( 4.0f ) );
            this.graphic = new PhetShapeGraphic( module.getApparatusPanel(), new Area(), stroke, color );
            transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
                    changed();
                }
            } );
            changed();
        }

        public void changed() {
            Point unitGraphicPt = unitGraphic.getUnitBounds().getLocation();
            Point in = new Point( (int)( rel.getX() + unitGraphicPt.getX() ), (int)( rel.getY() + unitGraphicPt.getY() ) );
            Point2D out = leadGraphic.getInputPoint();
            double dx = in.getX();
            double dy = in.getY();
            double cx = out.getX();
            double cy = out.getY();
            float dcy = 100;
            cableCurve = new CubicCurve2D.Double( cx, cy, cx, cy + dcy, ( 2 * dx + cx ) / 3, dy, dx, dy );
            graphic.setShape( cableCurve );
            notifyObservers();
        }

        public void paint( Graphics2D g ) {
            graphic.paint( g );
        }

        public void setVisible( boolean visible ) {
            graphic.setVisible( visible );
        }
    }

}
