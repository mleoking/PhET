/** Sam Reid*/
package edu.colorado.phet.cck3.circuit.tools;

import edu.colorado.phet.cck3.CCK3Module;
import edu.colorado.phet.cck3.circuit.Branch;
import edu.colorado.phet.cck3.circuit.CircuitGraphic;
import edu.colorado.phet.cck3.circuit.InteractiveBranchGraphic;
import edu.colorado.phet.cck3.common.RectangleUtils;
import edu.colorado.phet.common.util.SimpleObservable;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.fastpaint.FastPaintImageGraphic;
import edu.colorado.phet.common.view.fastpaint.FastPaintTextGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.GraphicsUtil;

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
public class VoltmeterGraphic extends CompositeInteractiveGraphic {
    UnitGraphic unitGraphic;
    private Voltmeter voltmeter;
    private ModelViewTransform2D transform;
    private LeadGraphic redLeadGraphic;
    private LeadGraphic blackLeadGraphic;
    private CableGraphic redCableGraphic;
    private CableGraphic blackCableGraphic;
    private CCK3Module module;

    public VoltmeterGraphic( Voltmeter voltmeter, Component parent, CCK3Module module ) throws IOException {
        this.transform = module.getTransform();
        this.voltmeter = voltmeter;
        this.module = module;
        unitGraphic = new UnitGraphic( voltmeter.getUnit(), module.loadBufferedImage( "images/vm3.gif" ), parent );
        addGraphic( unitGraphic );
        redLeadGraphic = new LeadGraphic( voltmeter.getRedLead(), module.loadBufferedImage( "images/dvm/probeRed.gif" ), parent, Math.PI / 8 );
        blackLeadGraphic = new LeadGraphic( voltmeter.getBlackLead(), module.loadBufferedImage( "images/dvm/probeBlack.gif" ), parent, -Math.PI / 8 );

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

    public class LeadGraphic extends FastPaintImageGraphic {
        private Voltmeter.Lead lead;
        private double angle;
        private Stroke stroke = new BasicStroke( 1.5f );
        private InteractiveBranchGraphic overlap;

        public LeadGraphic( Voltmeter.Lead lead, BufferedImage bufferedImage, Component parent, double angle ) {
            super( bufferedImage, parent );
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
            double imWidth = getBufferedImage().getWidth();
            double imHeight = getBufferedImage().getHeight();

            Point2D tx = new Point2D.Double( ctr.x - imWidth / 2, ctr.y - imHeight / 2 );
            AffineTransform imageTransform = AffineTransform.getTranslateInstance( tx.getX(), tx.getY() );
            imageTransform.rotate( angle );//tx.getX(), tx.getY() );
            super.setTransform( imageTransform );
            VoltmeterGraphic.this.recomputeVoltage();
        }

        public Shape getTipShape() {
            int tipWidth = 3;
            int tipHeight = 25;
            int imWidth = getBufferedImage().getWidth();
            Rectangle r = new Rectangle( imWidth / 2 - tipWidth / 2, 0, tipWidth, tipHeight );
            return getTransform().createTransformedShape( r );
        }

        public Point2D getInputPoint() {
            BufferedImage image = super.getBufferedImage();
            Rectangle rect = new Rectangle( 0, 0, image.getWidth(), image.getHeight() );
            Point2D pt = new Point2D.Double( rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() );
            return getTransform().transform( pt, null );
        }

        public void paint( Graphics2D graphics2D ) {
            super.paint( graphics2D );
            graphics2D.setColor( Color.white );
            graphics2D.setStroke( stroke );
            Shape tipShape = getTipShape();
            graphics2D.draw( tipShape );
            graphics2D.setColor( Color.gray );
            graphics2D.fill( tipShape );
        }

        public Branch detectBranch( CircuitGraphic circuitGraphic ) {
            Graphic[] g = circuitGraphic.getBranchGraphics();
            Shape tipShape = getTipShape();
            overlap = null;
            for( int i = g.length - 1; i >= 0; i-- ) {
                Graphic graphic = g[i];
                if( graphic instanceof InteractiveBranchGraphic ) {
                    InteractiveBranchGraphic ibg = (InteractiveBranchGraphic)graphic;
                    Shape shape = ibg.getBranchGraphic().getShape();
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

        public void setAngle( double angle ) {
            this.angle = angle;
            changed();
        }
    }

    public void recomputeVoltage() {
        if( redLeadGraphic == null || blackLeadGraphic == null ) {
            return;
        }

        Area tipIntersection = new Area( redLeadGraphic.getTipShape() );
        tipIntersection.intersect( new Area( blackLeadGraphic.getTipShape() ) );
        if( !tipIntersection.isEmpty() ) {
            unitGraphic.setVoltage( 0 );
        }
        else {
//        if (new ArearedLeadGraphic.getTipShape())
            Branch red = redLeadGraphic.detectBranch( module.getCircuitGraphic() );
            Branch black = blackLeadGraphic.detectBranch( module.getCircuitGraphic() );
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

    class UnitGraphic extends CompositeInteractiveGraphic {
        FastPaintImageGraphic unitGraphic;
        FastPaintTextGraphic textGraphic;
        Font font = new Font( "Dialog", 0, 20 );
        private Voltmeter.VoltmeterUnit vm;
        private Point ctr;
        double relX = -35;
        double relY = -73;
        private static final String UNKNOWN_VOLTS = "???";
        private String voltageString = UNKNOWN_VOLTS;
        private DecimalFormat voltFormatter = new DecimalFormat( "#0.0#" );

        public UnitGraphic( Voltmeter.VoltmeterUnit vm, BufferedImage image, Component parent ) {
            unitGraphic = new FastPaintImageGraphic( image, parent );
            textGraphic = new FastPaintTextGraphic( UNKNOWN_VOLTS, font, 0, 0, parent );
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
//            final JTextField xField=new JTextField( "  0  ");
//            final JTextField yField=new JTextField( "  0   ");
//            JButton jb=new JButton( "Update");
//            jb.addActionListener( new ActionListener() {
//                public void actionPerformed( ActionEvent e ) {
//                    relX=Double.parseDouble( xField.getText() );
//                    relY=Double.parseDouble( yField.getText() );
//                    changed();
//                }
//            } );
//
//            JPanel panel=new JPanel( );
//            panel.add(xField);
//            panel.add(yField);
//            panel.add(jb);
//            JFrame jf=new JFrame( );
//            jf.setContentPane( panel );
//            jf.pack();
//            jf.setVisible( true );
        }

        public Rectangle getUnitBounds() {
            return unitGraphic.getVisibleRect();
        }

        private void changed() {
            Point2D.Double loc = new Point2D.Double( vm.getX(), vm.getY() );
            ctr = transform.modelToView( loc );
            unitGraphic.setPosition( ctr );
            textGraphic.setLocation( (float)( ctr.getX() + relX ), (float)( ctr.getY() + relY ) );
        }

        public Point getPosition() {
            return ctr;
        }

        public void setVoltage( double volts ) {
            voltageString = voltFormatter.format( volts );
            if( Double.parseDouble( voltageString ) == 0 ) {
                voltageString = voltFormatter.format( 0 );
            }
            textGraphic.setText( voltageString+" V" );
        }

        public void setUnknownVoltage() {
            textGraphic.setText( UNKNOWN_VOLTS );
        }
    }

    public class CableGraphic extends SimpleObservable implements Graphic {
        private ModelViewTransform2D transform;
        private Color color;
        private LeadGraphic leadGraphic;
        private Point2D rel;
        private CubicCurve2D.Double cableCurve;

        public CableGraphic( ModelViewTransform2D transform, Color color, LeadGraphic leadGraphic, Point2D rel ) {
            this.transform = transform;
            this.color = color;
            this.leadGraphic = leadGraphic;
            this.rel = rel;
            transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2D ModelViewTransform2D ) {
                    changed();
                }
            } );
            changed();
        }

        public void changed() {
            Rectangle rect1 = getBounds();

            Point unitGraphicPt = unitGraphic.getUnitBounds().getLocation();
            Point in = new Point( (int)( rel.getX() + unitGraphicPt.getX() ), (int)( rel.getY() + unitGraphicPt.getY() ) );
            Point2D out = leadGraphic.getInputPoint();
            double dx = in.getX();
            double dy = in.getY();
            double cx = out.getX();
            double cy = out.getY();
            float dcy = 100;
            cableCurve = new CubicCurve2D.Double( cx, cy, cx, cy + dcy, ( 2 * dx + cx ) / 3, dy, dx, dy );
            notifyObservers();
            Rectangle rect2 = getBounds();
            if( rect1 != null ) {
                GraphicsUtil.fastRepaint( module.getApparatusPanel(), rect1, rect2 );
            }
            else {
                GraphicsUtil.fastRepaint( module.getApparatusPanel(), rect2 );
            }
        }

        private Rectangle getBounds() {
            if( cableCurve == null ) {
                return null;
            }
            return RectangleUtils.expand( cableCurve.getBounds(), 4, 4 );
        }

        public void paint( Graphics2D g ) {
            g.setColor( color );
            g.setStroke( new BasicStroke( 4.0f ) );
            g.draw( cableCurve );
        }
    }

}
