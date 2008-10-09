package edu.colorado.phet.rotation.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.*;
import java.util.ArrayList;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.motion.model.ITemporalVariable;
import edu.colorado.phet.common.motion.model.IVariable;
import edu.colorado.phet.common.motion.model.UpdateStrategy;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.math.AbstractVector2D;
import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.math.Vector2D;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearSlider;
import edu.colorado.phet.common.phetcommon.view.util.FrameSetup;
import edu.colorado.phet.common.piccolophet.PiccoloPhetApplication;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.rotation.model.RotationPlatform;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PPanEventHandler;
import edu.umd.cs.piccolo.event.PZoomEventHandler;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Created by: Sam
 * Dec 28, 2007 at 7:50:57 PM
 */
public class PlatformNode2 extends PNode {
    private RotationPlatform platform;
    private ArrayList segments = new ArrayList();
    private PNode outerEdgeLayer = new PNode();
    private PNode innerEdgeLayer = new PNode();
    private PNode foreground = new PNode();
    private PhetPPath decorationLayer;

    public PlatformNode2( RotationPlatform platform ) {
        this.platform = platform;
        updateSegments();

        addChild( innerEdgeLayer );
        addChild( outerEdgeLayer );

        addChild( foreground );
        addChild( new DecorationLayer( platform ) );
        platform.getPositionVariable().addListener( new ITemporalVariable.ListenerAdapter() {
            public void valueChanged() {
                updateAngle();
            }
        } );
        platform.addListener( new RotationPlatform.Adapter() {
            public void radiusChanged() {
                updateSegments();
                updateAngle();
            }

            public void innerRadiusChanged() {
                updateSegments();
                updateAngle();
            }
        } );
        platform.addListener( new RotationPlatform.Adapter() {
            public void massChanged() {
                updateSegments();
                updateAngle();
            }
        } );

        updateAngle();
    }

    protected void updateSegments() {
        clearSegments();

        final double MAX_THICKNESS = 0.4;
        Function.LinearFunction linearFunction = new Function.LinearFunction( RotationPlatform.MIN_MASS, RotationPlatform.MAX_MASS, 0, MAX_THICKNESS );
        double dy = linearFunction.evaluate( platform.getMass() );


        for ( int quadrant = 0; quadrant < 4; quadrant++ ) {
//            for ( int layer = 3; layer >= 0; layer-- ) {
            for ( int layer = 0; layer <= 3; layer++ ) {
                final double startAngle = quadrant * Math.PI * 2 / 4;
//                PlatformSegment segment = new PlatformSegment( this, startAngle, startAngle + Math.PI / 2, layer + 0.1, layer + 1 - 0.1, -0.3, -0.3, layer == 3 );

//                final double LAYER_INSET = 0.1;
//                final double ANGLE_INSET = 0.1;
                final double LAYER_INSET = 0.0;
                final double ANGLE_INSET = 0.0;
                Color color = Color.blue;
                if ( layer == 0 ) {
                    color = quadrant % 2 == 0 ? Color.lightGray : Color.white;
                }
                else if ( layer == 1 ) {
                    color = quadrant % 2 == 0 ? new Color( 215, 215, 255 ) : new Color( 240, 240, 255 );
                }
                else if ( layer == 2 ) {
                    color = quadrant % 2 == 0 ? new Color( 140, 255, 140 ) : new Color( 200, 255, 200 );
                }
                else if ( layer == 3 ) {
                    color = quadrant % 2 == 0 ? new Color( 255, 215, 215 ) : new Color( 255, 240, 240 );
                }
                double innerRadius = layer + LAYER_INSET;
                double outerRadius = layer + 1 - LAYER_INSET;
                Range segmentRange = new Range( innerRadius, outerRadius );
                Range platformRange = new Range( platform.getInnerRadius(), platform.getRadius() );
                final boolean b = segmentRange.overlaps( platformRange );
//                System.out.println( "q=" + quadrant + ", layer=" + layer + ", b = " + b );
                if ( b ) {
//                    outerRadius = Math.max( platform.getRadius(), outerRadius );
//                    innerRadius = Math.max( platform.getInnerRadius(), innerRadius );
                    if ( platform.getRadius() <= outerRadius ) {
                        outerRadius = platform.getRadius();
                    }
                    if ( platform.getInnerRadius() >= innerRadius ) {
                        innerRadius = platform.getInnerRadius();
                    }
                    PlatformSegment segment = new PlatformSegment( startAngle + ANGLE_INSET, startAngle + Math.PI / 2 - ANGLE_INSET,
                                                                   innerRadius, outerRadius, -dy, -dy, outerRadius == platform.getRadius(), innerRadius == platform.getInnerRadius(), color );
                    addSegment( segment );
                }
            }
        }
    }

    static class Range {
        double min;
        double max;

        Range( double min, double max ) {
            this.min = min;
            this.max = max;
        }

        public boolean overlaps( Range b ) {
            return this.containsEndpoint( b ) || b.containsEndpoint( this );
        }

        private boolean containsEndpoint( Range b ) {
            return containsEndpoint( b.min ) || containsEndpoint( b.max );
        }

        private boolean containsEndpoint( double v ) {
            return v >= min && v <= max;
        }
    }

    private void clearSegments() {
        while ( segments.size() > 0 ) {
            PlatformSegment platformSegment = (PlatformSegment) segments.get( 0 );
            removeSegment( platformSegment );
        }
    }

    private void removeSegment( PlatformSegment segment ) {
        segments.remove( segment );
        if ( segment.showOuterEdge ) {
            outerEdgeLayer.removeChild( segment.northPanel );
        }
        if ( segment.showInnerEdge ) {
            innerEdgeLayer.removeChild( segment.southPanel );
        }
        foreground.removeChild( segment.body );
    }

    public void fullPaint( PPaintContext paintContext ) {
        Object a = paintContext.getGraphics().getRenderingHint( RenderingHints.KEY_ANTIALIASING );
        paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        super.fullPaint( paintContext );
        paintContext.getGraphics().setRenderingHint( RenderingHints.KEY_ANTIALIASING, a == null ? RenderingHints.VALUE_ANTIALIAS_DEFAULT : a );
    }

    private void addSegment( PlatformSegment segment ) {
        segments.add( segment );
//        background.addChild( segment.bottomPanel );
        if ( segment.showOuterEdge ) {
            outerEdgeLayer.addChild( segment.northPanel );
        }
        if ( segment.showInnerEdge ) {
            innerEdgeLayer.addChild( segment.southPanel );
        }

        foreground.addChild( segment.body );
    }

    protected void updateAngle() {
        for ( int i = 0; i < segments.size(); i++ ) {
            PlatformSegment platformSegment = (PlatformSegment) segments.get( i );
            platformSegment.updateAngle();
        }
    }

    private class PlatformSegment extends PNode {
        private double startAngle;
        private double endAngle;
        private double innerRadius;
        private double outerRadius;
        private PhetPPath body;
        //        private PhetPPath bottomPanel;
        private PhetPPath northPanel;
        private double edgeDX;
        private double edgeDY;
        private PhetPPath southPanel;
        private boolean showOuterEdge;
        private boolean showInnerEdge;

        public PlatformSegment( double startAngle, double endAngle, double innerRadius, double outerRadius, double edgeDX, double edgeDY, boolean showOuterEdge, boolean showInnerEdge, Color color ) {
            this.edgeDX = edgeDX;
            this.edgeDY = edgeDY;
            this.startAngle = startAngle;// + 0.1;
            this.endAngle = endAngle;// - 0.1;
            this.innerRadius = innerRadius;
            this.outerRadius = outerRadius;
            this.showOuterEdge = showOuterEdge;
            this.showInnerEdge = showInnerEdge;

//            bottomPanel = new PhetPPath( Color.red );
//            addChild( bottomPanel );

            northPanel = new PhetPPath( color.darker(), new BasicStroke( 0.03f ), Color.black );
            southPanel = new PhetPPath( color.darker(), new BasicStroke( 0.03f ), Color.black );
            if ( showOuterEdge ) {
                addChild( northPanel );

            }
            if ( showInnerEdge ) {
                addChild( southPanel );
            }

//            body = new PhetPPath( new Rectangle2D.Double( innerRadius, 0, 1, 1 ), new Color( 0f, 0, 1f, 0.5f ), new BasicStroke( 0.03f ), Color.black );

//            body = new PhetPPath( color, new BasicStroke( 0.03f ), Color.black );
            body = new PhetPPath( color );
            addChild( body );
        }

        public void updateAngle() {

            final double extent = ( endAngle - startAngle ) * 360 / 2 / Math.PI;
//            final double platformAngle = platform.getPosition() * 360 / Math.PI / 2;
            final double platformAngle = -platform.getPosition() * 360 / Math.PI / 2;
            final double angle1 = startAngle * 360 / 2 / Math.PI + platformAngle;
//            System.out.println( "platform.getPosition() = " + platform.getPosition() + ", angle=" + angle1 );
            Arc2D.Double outerArc = new Arc2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2, angle1, extent, Arc2D.Double.OPEN );
            Arc2D.Double outerArcRev = new Arc2D.Double( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2, angle1 + extent, -extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArc = new Arc2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2, startAngle * 360 / 2 / Math.PI + extent + platformAngle, -extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArcRev = new Arc2D.Double( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2, angle1, extent, Arc2D.Double.OPEN );
            GeneralPath path = toPathSegment( outerArc, innerArc );
            body.setPathTo( path );

            Arc2D.Double outerArcDepth = new Arc2D.Double( -outerRadius + edgeDX, -outerRadius + edgeDY, outerRadius * 2, outerRadius * 2, angle1, extent, Arc2D.Double.OPEN );
            Arc2D.Double innerArcDepth = new Arc2D.Double( -innerRadius + edgeDX, -innerRadius + edgeDY, innerRadius * 2, innerRadius * 2, startAngle * 360 / 2 / Math.PI + extent + platformAngle, -extent, Arc2D.Double.OPEN );

//            Shape p2 = path.createTransformedShape( AffineTransform.getTranslateInstance( edgeDX, edgeDY ) );
//            bottomPanel.setPathTo( toPathSegment( outerArcDepth, innerArcDepth ) );
            northPanel.setPathTo( toPathSegment( outerArcRev, outerArcDepth ) );
            southPanel.setPathTo( toPathSegment( innerArcRev, innerArcDepth ) );
        }

        private GeneralPath toPathSegment( Arc2D.Double outerArc, Arc2D.Double innerArc ) {
            GeneralPath path = new GeneralPath();
            path.moveTo( (float) outerArc.getStartPoint().getX(), (float) outerArc.getStartPoint().getY() );
            path.append( outerArc, true );
            path.append( innerArc, true );
            path.closePath();
            return path;
        }
    }

    static class TestModule extends Module {
        private PCanvas panel;

        public TestModule( String name, IClock clock ) {
            super( name, clock );
            panel = new PCanvas();
            panel.setDefaultRenderQuality( PPaintContext.LOW_QUALITY_RENDERING );
            panel.addComponentListener( new ComponentAdapter() {
                public void componentResized( ComponentEvent e ) {
                    updateTx();
                }
            } );
            panel.setZoomEventHandler( new PZoomEventHandler() );
            panel.setPanEventHandler( new PPanEventHandler() );
//            panel.getPhetRootNode().scaleWorldAboutPoint( 100, new Point2D.Double( ) );
            final RotationPlatform rotationPlatform = new RotationPlatform();
            final PlatformNode2 node2 = new PlatformNode2( rotationPlatform );
            panel.getLayer().addChild( node2 );
//            panel.getCamera().animateViewToPanToBounds( node2.getGlobalFullBounds(), 1000 );
//            panel.getCamera().animateViewToCenterBounds( node2.getGlobalFullBounds(), true, 1000 );

            setSimulationPanel( panel );
            updateTx();
            rotationPlatform.setVelocity( 1.0 / Math.PI / 2.0 * 3 );
            rotationPlatform.setVelocityDriven();
            getClock().addClockListener( new ClockAdapter() {
                public void simulationTimeChanged( ClockEvent clockEvent ) {
//                    System.out.println( "rotationPlatform.getPosition() = " + rotationPlatform.getPosition() );
                    rotationPlatform.stepInTime( clockEvent.getSimulationTime(), clockEvent.getSimulationTimeChange() );
                }
            } );
            setControlPanel( new ControlPanel() );
            {
                final LinearSlider slider = new LinearSlider( 0, 4, 4, 1000 );
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        rotationPlatform.setRadius( slider.getModelValue() );
                    }
                } );
                getControlPanel().addControlFullWidth( slider );
            }
            {
                final LinearSlider slider = new LinearSlider( 0, 4, 0, 1000 );
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        rotationPlatform.setInnerRadius( slider.getModelValue() );
                    }
                } );
                getControlPanel().addControlFullWidth( slider );
            }
            {
                final LinearSlider slider = new LinearSlider( 0, 0.25, rotationPlatform.getMass(), 1000 );
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        rotationPlatform.setMass( slider.getModelValue() );
                    }
                } );
                getControlPanel().addControlFullWidth( slider );
            }
            {
                final LinearSlider slider = new LinearSlider( 0, Math.PI * 2, rotationPlatform.getPosition(), 1000 );
                slider.addChangeListener( new ChangeListener() {
                    public void stateChanged( ChangeEvent e ) {
                        rotationPlatform.setUpdateStrategy( new UpdateStrategy.PositionDriven() );
                        final double value = slider.getModelValue();
                        System.out.println( "value = " + value );
                        rotationPlatform.setPosition( value );

                    }
                } );
                getControlPanel().addControlFullWidth( slider );
            }
        }

        private void updateTx() {
            panel.getCamera().setViewTransform( new AffineTransform() );
            panel.getCamera().translateView( panel.getWidth() / 2, panel.getHeight() / 2 );
            panel.getCamera().scaleView( 50 );
        }
    }

    private static class DecorationLayer extends PNode {
        private RotationPlatform platform;
        private PhetPPath outerRim;
        private PhetPPath innerRim;

        private PhetPPath angleZero;

        public DecorationLayer( RotationPlatform platform ) {
            this.platform = platform;

            outerRim = new PhetPPath( new BasicStroke( 0.04f ), Color.black );
            addChild( outerRim );

            innerRim = new PhetPPath( new BasicStroke( 0.04f ), Color.black );
            addChild( innerRim );

            angleZero = new PhetPPath( new BasicStroke( 0.04f ), Color.gray );
            addChild( angleZero );

            platform.getPositionVariable().addListener( new IVariable.Listener() {
                public void valueChanged() {
                    update();
                }
            } );
            platform.addListener( new RotationPlatform.Adapter() {
                public void radiusChanged() {
                    update();
                }

                public void innerRadiusChanged() {
                    update();
                }
            } );
            update();
        }

        private void update() {
            outerRim.setPathTo( new Ellipse2D.Double( -platform.getRadius(), -platform.getRadius(), platform.getRadius() * 2, platform.getRadius() * 2 ) );
            innerRim.setVisible( platform.getInnerRadius() > 0 && platform.getRadius() != platform.getInnerRadius() );
            innerRim.setPathTo( new Ellipse2D.Double( -platform.getInnerRadius(), -platform.getInnerRadius(), platform.getInnerRadius() * 2, platform.getInnerRadius() * 2 ) );

            double angle = platform.getPosition();
            AbstractVector2D a = Vector2D.Double.parseAngleAndMagnitude( platform.getInnerRadius(), angle );
            AbstractVector2D b = Vector2D.Double.parseAngleAndMagnitude( platform.getRadius(), angle );

            angleZero.setPathTo( new Line2D.Double( a.toPoint2D(), b.toPoint2D() ) );
        }
    }
}
