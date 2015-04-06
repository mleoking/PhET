// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.circuitconstructionkit.view.piccolo;

import java.awt.*;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.*;

import edu.colorado.phet.circuitconstructionkit.CCKModule;
import edu.colorado.phet.circuitconstructionkit.CCKResources;
import edu.colorado.phet.circuitconstructionkit.CCKSimSharing;
import edu.colorado.phet.circuitconstructionkit.model.CCKModel;
import edu.colorado.phet.circuitconstructionkit.model.Circuit;
import edu.colorado.phet.circuitconstructionkit.model.NoiseGenerator;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DefaultDecimalFormat;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.util.PImageFactory;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * User: Sam Reid
 * Date: Sep 25, 2006
 * Time: 9:59:20 AM
 */

public class VoltmeterNode extends PhetPNode {
    private VoltmeterModel voltmeterModel;
    private UnitNode unitImageNode;
    private LeadNode blackProbe;
    private LeadNode redProbe;
    private CableNode redCable;
    private CableNode blackCable;
    private static final double SCALE = 1.0 / 80.0 * 1.2 * 0.725;
    private double noise = 0;
    private boolean noiseDirty = true;

    public VoltmeterNode( final VoltmeterModel voltmeterModel ) {
        this.voltmeterModel = voltmeterModel;
        unitImageNode = new UnitNode( voltmeterModel );
        redProbe = new LeadNode( CCKSimSharing.UserComponents.redProbe, "circuit-construction-kit/images/probeRed.gif", voltmeterModel.getRedLeadModel() );
        blackProbe = new LeadNode( CCKSimSharing.UserComponents.blackProbe, "circuit-construction-kit/images/probeBlack.gif", voltmeterModel.getBlackLeadModel() );

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
        private PText textNode;
        public static DecimalFormat decimalFormat = new DefaultDecimalFormat( "0.00" );
        private final String UNKNOWN_VOLTS = CCKResources.getString( "VoltmeterGraphic.UnknownVolts" );
        private double randomness = 0;
        private double noise = 0;
        private boolean noiseDirty = true;

        public UnitNode( final VoltmeterModel voltmeterModel ) {
            this.voltmeterModel = voltmeterModel;
            scale( SCALE );
            voltmeterModel.addListener( new VoltmeterModel.Listener() {
                public void voltmeterChanged() {
                    update();
                }
            } );
            voltmeterModel.getUnitModel().addListener( new VoltmeterModel.UnitModel.Listener() {
                public void unitModelChanged() {
                    update();
                }
            } );
            addChild( PImageFactory.create( "circuit-construction-kit/images/vm3.gif" ) );
            textNode = new PText();
            textNode.setFont( new PhetFont( Font.PLAIN, 20 ) );
            textNode.setOffset( 15, 20 );
            addChild( textNode );
            addInputEventListener( new PBasicInputEventHandler() {
                public void mouseDragged( PInputEvent event ) {
                    PDimension pt = event.getDeltaRelativeTo( UnitNode.this.getParent() );
                    voltmeterModel.bodyDragged( pt.width, pt.height );
                }
            } );
            addInputEventListener( new CursorHandler() );
            update();

            CCKModule.fluctuateRandomly( new Runnable() {
                @Override public void run() {
                    noiseDirty = true;
                    update();
                }
            } );
        }

        private void update() {
            setOffset( voltmeterModel.getUnitModel().getLocation() );
            double voltage = voltmeterModel.getVoltage();
            if ( Double.isNaN( voltage ) ) {
                textNode.setText( UNKNOWN_VOLTS );
            }
            else {
                if ( CCKModule.randomFluctuations && noiseDirty ) {
                    double readout = NoiseGenerator.getReadout( voltage );
                    noise = readout - voltage;
                    noiseDirty = false;
                }
                if ( CCKModule.randomFluctuations ) {
                    voltage = voltage + noise;
                }
                textNode.setText( decimalFormat.format( voltage ) + " V" );
            }
        }
    }

    private class CableNode extends PhetPNode {
        private PPath path;
        private final BasicStroke cableStroke = new BasicStroke( (float) ( 3 * SCALE ) );
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
            float dcy = (float) ( 100 * SCALE );
            CubicCurve2D.Double cableCurve = new CubicCurve2D.Double( cx, cy, cx, cy + dcy, ( 2 * dx + cx ) / 3, dy, dx, dy );
            path.setPathTo( cableCurve );
        }
    }

    private class LeadNode extends PhetPNode {
        private VoltmeterModel.LeadModel leadModel;
        private PImage imageNode;
        private PhetPPath tipPath;
        private final DelayedRunner runner = new DelayedRunner();

        public LeadNode( final IUserComponent userComponent, String imageLocation, final VoltmeterModel.LeadModel leadModel ) {
            this.leadModel = leadModel;

            imageNode = PImageFactory.create( imageLocation );
            imageNode.rotateAboutPoint( leadModel.getAngle(), 0.1, 0.1 );
            imageNode.scale( SCALE );
            addChild( imageNode );
            leadModel.addListener( new VoltmeterModel.LeadModel.Listener() {
                public void leadModelChanged() {
                    updateLead();
                }
            } );

            addInputEventListener( new PBasicInputEventHandler() {
                @Override public void mousePressed( PInputEvent event ) {
                    SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.sprite, UserActions.startDrag,
                                                       ParameterSet.parameterSet( ParameterKeys.x, leadModel.getTipLocation().getX() ).
                                                               with( ParameterKeys.y, leadModel.getTipLocation().getY() ) );
                    super.mousePressed( event );    //To change body of overridden methods use File | Settings | File Templates.
                }

                public void mouseDragged( PInputEvent event ) {
                    final PDimension pt = event.getDeltaRelativeTo( LeadNode.this.getParent() );
                    runner.set( new Runnable() {
                        public void run() {
                            SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.sprite, UserActions.drag,
                                                               ParameterSet.parameterSet( ParameterKeys.x, leadModel.getTipLocation().getX() + pt.width ).
                                                                       with( ParameterKeys.y, leadModel.getTipLocation().getY() + pt.height ) );
                        }
                    } );
                    leadModel.translate( pt.width, pt.height );
                }

                @Override public void mouseReleased( PInputEvent event ) {
                    SimSharingManager.sendUserMessage( userComponent, UserComponentTypes.sprite, UserActions.endDrag,
                                                       ParameterSet.parameterSet( ParameterKeys.x, leadModel.getTipLocation().getX() ).
                                                               with( ParameterKeys.y, leadModel.getTipLocation().getY() ) );
                    runner.terminate();
                    super.mouseReleased( event );    //To change body of overridden methods use File | Settings | File Templates.
                }
            } );
            addInputEventListener( new CursorHandler() );

            tipPath = new PhetPPath( Color.lightGray );
            addChild( tipPath );

            updateLead();
        }

        private void updateLead() {
            double dx = imageNode.getImage().getWidth( null ) * SCALE * Math.cos( leadModel.getAngle() ) / 2;
            double dy = imageNode.getImage().getWidth( null ) * SCALE * Math.sin( leadModel.getAngle() ) / 2;
            imageNode.setOffset( leadModel.getTipLocation().getX() - dx, leadModel.getTipLocation().getY() - dy );

            tipPath.setPathTo( leadModel.getTipShape() );
        }

        public Point2D getTailLocation() {
            Point2D pt = new Point2D.Double( imageNode.getWidth() / 2, imageNode.getHeight() );
            imageNode.localToParent( pt );
            localToParent( pt );
            return new Point2D.Double( pt.getX(), pt.getY() );
        }
    }

    public static void main( String[] args ) {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                VoltmeterModel model = new VoltmeterModel( new CCKModel(), new Circuit() );
                model.setVisible( true );
                VoltmeterNode voltmeterNode = new VoltmeterNode( model );
                voltmeterNode.scale( 100 );
                JFrame frame = new JFrame();
                PCanvas contentPane = new PhetPCanvas();
                contentPane.getLayer().addChild( voltmeterNode );
                frame.setSize( 800, 600 );

                frame.setContentPane( contentPane );
                frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
                frame.setVisible( true );
            }
        } );
    }
}
