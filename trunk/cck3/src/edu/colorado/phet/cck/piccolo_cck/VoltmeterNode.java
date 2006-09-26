package edu.colorado.phet.cck.piccolo_cck;

import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.colorado.phet.piccolo.nodes.RegisterablePNode;
import edu.colorado.phet.piccolo.util.PImageFactory;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PDimension;

import java.awt.*;
import java.awt.geom.*;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:59:20 AM
 * Copyright (c) Sep 25, 2006 by Sam Reid
 */

public class VoltmeterNode extends PhetPNode {
    private VoltmeterModel voltmeterModel;
    private UnitNode unitImageNode;
    private LeadNode blackProbe;
    private LeadNode redProbe;
    private CableNode redCable;
    private CableNode blackCable;
    private static final double SCALE = 1.0 / 80.0 * 1.2;

    public VoltmeterNode( final VoltmeterModel voltmeterModel ) {
        this.voltmeterModel = voltmeterModel;
        unitImageNode = new UnitNode( voltmeterModel );
        redProbe = new LeadNode( "images/probeRed.gif", voltmeterModel.getRedLeadModel() );
        blackProbe = new LeadNode( "images/probeBlack.gif", voltmeterModel.getBlackLeadModel() );

        addChild( unitImageNode );

        Point2D redUnitConnectionPt = new Point2D.Double( 12 * SCALE, 218 * SCALE );
        Point2D blackUnitConnectionPt = new Point2D.Double( 88 * SCALE, 218 * SCALE );
        redCable = new CableNode( Color.red, redProbe,
                                  voltmeterModel.getUnitModel(),
                                  voltmeterModel.getRedLeadModel(),
                                  redUnitConnectionPt );
        blackCable = new CableNode( Color.black, blackProbe,
                                    voltmeterModel.getUnitModel(),
                                    voltmeterModel.getBlackLeadModel(),
                                    blackUnitConnectionPt );

        addChild( redCable );
        addChild( redProbe );

        addChild( blackCable );
        addChild( blackProbe );
        voltmeterModel.addListener( new VoltmeterModel.Listener() {
            public void voltmeterChanged() {
                update();
            }
        } );
        update();
    }

    private void update() {
        setVisible( voltmeterModel.isVisible() );
    }

    static class UnitNode extends PhetPNode {
        private VoltmeterModel voltmeterModel;

        public UnitNode( final VoltmeterModel voltmeterModel ) {
            this.voltmeterModel = voltmeterModel;
            scale( SCALE );
            voltmeterModel.getUnitModel().addListener( new VoltmeterModel.UnitModel.Listener() {
                public void unitModelChanged() {
                    update();
                }
            } );
            addChild( PImageFactory.create( "images/vm3.gif" ) );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension pt = event.getDeltaRelativeTo( UnitNode.this.getParent() );
                    voltmeterModel.bodyDragged( pt.width, pt.height );
                }
            } );
            addInputEventListener( new CursorHandler() );
            update();
        }

        private void update() {
            setOffset( voltmeterModel.getUnitModel().getLocation() );
        }
    }

    private class CableNode extends PhetPNode {
        private PPath path;
        private final BasicStroke cableStroke = new BasicStroke( (float)( 3 * SCALE ) );
        private LeadNode leadNode;
        private VoltmeterModel.UnitModel unitModel;
        private VoltmeterModel.LeadModel leadModel;
        private Point2D unitConnectionOffset;

        public CableNode( Color color,
                          LeadNode leadNode,
                          VoltmeterModel.UnitModel unitModel,
                          VoltmeterModel.LeadModel leadModel,
                          Point2D unitConnectionOffset ) {
            this.leadNode = leadNode;
            this.unitModel = unitModel;
            this.leadModel = leadModel;
            this.unitConnectionOffset = unitConnectionOffset;
            path = new PPath();
            path.setStroke( cableStroke );
            path.setStrokePaint( color );
            addChild( path );
            unitModel.addListener( new VoltmeterModel.UnitModel.Listener() {
                public void unitModelChanged() {
                    update();
                }
            } );
            leadModel.addListener( new VoltmeterModel.LeadModel.Listener() {
                public void leadModelChanged() {
                    update();
                }
            } );
            update();
        }

        public void update() {
            Point2D leadConnectionPt = leadNode.getTailLocation();
            Point2D unitConnectionPt = new Point2D.Double( unitModel.getLocation().getX() + unitConnectionOffset.getX(),
                                                           unitModel.getLocation().getY() + unitConnectionOffset.getY() );
            path.setPathTo( new Line2D.Double( leadConnectionPt,
                                               unitConnectionPt ) );

            double dx = unitConnectionPt.getX();
            double dy = unitConnectionPt.getY();
            double cx = leadConnectionPt.getX();
            double cy = leadConnectionPt.getY();
            float dcy = (float)( 100 * SCALE );
            CubicCurve2D.Double cableCurve = new CubicCurve2D.Double( cx, cy, cx, cy + dcy, ( 2 * dx + cx ) / 3, dy, dx, dy );
            path.setPathTo( cableCurve );
        }
    }

    private class LeadNode extends RegisterablePNode {
        private VoltmeterModel.LeadModel leadModel;
        private PImage imageNode;
        private PhetPPath tipPath;

        public LeadNode( String imageLocation, final VoltmeterModel.LeadModel leadModel ) {
            this.leadModel = leadModel;

            imageNode = PImageFactory.create( imageLocation );

            addChild( imageNode );
            leadModel.addListener( new VoltmeterModel.LeadModel.Listener() {
                public void leadModelChanged() {
                    updateLead();
                }
            } );

            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension pt = event.getDeltaRelativeTo( LeadNode.this.getParent() );
                    leadModel.translate( pt.width, pt.height );
                }
            } );
            addInputEventListener( new CursorHandler() );

            tipPath = new PhetPPath( Color.blue );
            addChild( tipPath );

            updateLead();
        }

        private void updateLead() {
            imageNode.setTransform( new AffineTransform() );
            imageNode.rotateAboutPoint( leadModel.getAngle(), leadModel.getTipLocation().getX() - imageNode.getWidth() / 2 * SCALE, leadModel.getTipLocation().getY() );
            imageNode.setOffset( leadModel.getTipLocation().getX() - imageNode.getWidth() / 2 * SCALE, leadModel.getTipLocation().getY() );
//            imageNode.rotateAboutPoint( leadModel.getAngle(), leadModel.getTipLocation() );


            imageNode.scale( SCALE );

//            tipPath.setPathTo( leadModel.getTipShape() );
            tipPath.setPathTo( new Rectangle2D.Double( leadModel.getTipLocation().getX(), leadModel.getTipLocation().getY(), 0.5, 0.5 ) );
        }

        public Point2D getTailLocation() {
            Point2D pt = new Point2D.Double( imageNode.getWidth() / 2, imageNode.getHeight() );
            imageNode.localToParent( pt );
            localToParent( pt );
            return new Point2D.Double( pt.getX(), pt.getY() );
        }
    }
}
