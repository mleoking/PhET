/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.chart.AbstractFloatingChart;
import edu.colorado.phet.cck3.chart.CCKTime;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck3.circuit.analysis.CircuitSolver;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck3.circuit.particles.Electron;
import edu.colorado.phet.cck3.circuit.particles.ParticleSet;
import edu.colorado.phet.cck3.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.cck3.circuit.toolbox.Toolbox;
import edu.colorado.phet.cck3.circuit.tools.VirtualAmmeter;
import edu.colorado.phet.cck3.circuit.tools.Voltmeter;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.cck3.common.WiggleMe;
import edu.colorado.phet.cck3.model.CCKModel;
import edu.colorado.phet.common.model.clock.SwingClock;
import edu.colorado.phet.common_cck.application.Module;
import edu.colorado.phet.common_cck.application.PhetApplication;
import edu.colorado.phet.common_cck.math.AbstractVector2D;
import edu.colorado.phet.common_cck.math.ImmutableVector2D;
import edu.colorado.phet.common_cck.math.Vector2D;
import edu.colorado.phet.common_cck.model.BaseModel;
import edu.colorado.phet.common_cck.model.ModelElement;
import edu.colorado.phet.common_cck.util.SimpleObserver;
import edu.colorado.phet.common_cck.view.BasicGraphicsSetup;
import edu.colorado.phet.common_cck.view.PhetFrame;
import edu.colorado.phet.common_cck.view.components.AspectRatioPanel;
import edu.colorado.phet.common_cck.view.graphics.Boundary;
import edu.colorado.phet.common_cck.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.Graphic;
import edu.colorado.phet.common_cck.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common_cck.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common_cck.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common_cck.view.phetgraphics.PhetShadowTextGraphic;
import edu.colorado.phet.common_cck.view.util.RectangleUtils;
import edu.colorado.phet.common_cck.view.util.SimStrings;
import edu.colorado.phet.piccolo.event.CursorHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolox.pswing.PSwing;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:27:42 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class CCKModule extends Module {

    private CCKModel model;

    private SetupParameters parameters;

    private CircuitGraphic circuitGraphic;
    private boolean inited = false;
    private ModelViewTransform2D transform;
    private AspectRatioPanel aspectRatioPanel;
    private CCK2ImageSuite imageSuite;

    private Toolbox toolbox;
    private VirtualAmmeter virtualAmmeter;
    private InteractiveVoltmeter interactiveVoltmeter;
    private VoltmeterGraphic voltmeterGraphic;

    private WiggleMe wiggleMe;
    private CCKHelp help;
    private CCK3ControlPanel cck3controlPanel;
    public static final Color backgroundColor = new Color( 200, 240, 200 );
    private static final Color apparatusPanelColor = new Color( 100, 160, 255 );
    public static final Color toolboxColor = new Color( 241, 241, 241 );

    private DecimalFormat decimalFormat = new DecimalFormat( "0.0#" );
    private boolean electronsVisible = true;
    private PhetFrame phetFrame;

    private CCKApparatusPanel cckApparatusPanel;
    static CCKModule module;
    public static final boolean GRAPHICAL_DEBUG = false;
    public PhetShadowTextGraphic messageGraphic;
    public static final double SCH_BULB_DIST = 1;

    private Area electronClip = new Area();
    private PNode stopwatch;

//    public static final double STICKY_THRESHOLD = SCALE;

    public CCKModule( String[] args ) throws IOException {
        super( SimStrings.get( "ModuleTitle.CCK3Module" ) );
        module = this;
        setModel( new BaseModel() );
        model = new CCKModel( this );

        this.parameters = new SetupParameters( this, args );

        cckApparatusPanel = new CCKApparatusPanel();
        cckApparatusPanel.setMyBackground( apparatusPanelColor );
        setApparatusPanel( cckApparatusPanel );
        stripChartClock.start();
        stopwatch = new MyPhetPNode( cckApparatusPanel, new PSwing( getApparatusPanel(), new StopwatchDecorator( stripChartClock, 1.0 * CCKTime.scale, "s" ) ) );
        stopwatch.addInputEventListener( new CursorHandler( Cursor.HAND_CURSOR ) );
        stopwatch.addInputEventListener( new PDragEventHandler() );
        getApparatusPanel().addScreenChild( stopwatch );
        stopwatch.setOffset( 150, 150 );
        stopwatch.setVisible( false );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );
        getApparatusPanel().setFocusable( true );
        getApparatusPanel().setBackground( new Color( 0, 0, 0, 0 ) );
        DefaultInteractiveGraphic backgroundGraphic = new DefaultInteractiveGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
            }
        }, new Boundary() {
            public boolean contains( int x, int y ) {
                return true;
            }
        } );
        backgroundGraphic.addMouseInputListener( new MouseInputAdapter() {
            public void mousePressed( MouseEvent e ) {
                getCircuit().clearSelection();
            }
        } );
        getApparatusPanel().addGraphic( backgroundGraphic, Double.NEGATIVE_INFINITY );
        BasicGraphicsSetup setup = new BasicGraphicsSetup();
        setup.setBicubicInterpolation();
        getApparatusPanel().addGraphicsSetup( setup );

        // Create a BaseModel that will get clock ticks and add a model element to
        // it that will tell the CCKModel to step
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                model.stepInTime( dt );
            }
        } );
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }

            public void componentShown( ComponentEvent e ) {
                relayout();
            }
        } );

        aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, 1.2 );
        imageSuite = new CCK2ImageSuite();

        CCK3ControlPanel controlPanel = null;
        controlPanel = new CCK3ControlPanel( this );
        this.cck3controlPanel = controlPanel;
        setControlPanel( cck3controlPanel.getComponent() );
        messageGraphic = new PhetShadowTextGraphic( getApparatusPanel(), " ", new Font( "Lucida Sans", Font.BOLD, 13 ), 50, 100, Color.red, 1, 1, Color.black );
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                int x = messageGraphic.getBounds().height;
                int y = getApparatusPanel().getHeight() - messageGraphic.getBounds().height;
                messageGraphic.setPosition( x, y );
            }
        } );
        getApparatusPanel().addGraphic( messageGraphic, Double.POSITIVE_INFINITY );
        setResistivityEnabled( true );
    }

    private void relayout() {
        if( getApparatusPanel().getWidth() == 0 || getApparatusPanel().getHeight() == 0 ) {
            return;
        }
        if( !inited ) {
            inited = true;
            Runnable r = new Runnable() {
                public void run() {
                    try {
                        doinit();
                        if( !transform.getViewBounds().equals( getViewBounds() ) ) {
                            transform.setViewBounds( getViewBounds() );
                            getApparatusPanel().repaint();
                            getCircuit().updateAll();
                        }
                    }
                    catch( IOException e ) {
                        e.printStackTrace();
                    }
                }
            };

            SwingUtilities.invokeLater( r );
            //            r.run();
        }

        if( transform != null && !transform.getViewBounds().equals( getViewBounds() ) ) {
            transform.setViewBounds( getViewBounds() );
            getApparatusPanel().repaint();
            getCircuit().updateAll();
        }
    }


    private void doinit() throws IOException {
        Rectangle vb = getApparatusPanel().getBounds();
        Rectangle viewBounds = new Rectangle( vb.width, vb.height );
        transform = new ModelViewTransform2D( model.getModelBounds(), viewBounds );
        circuitGraphic = new CircuitGraphic( this );

        toolbox = new Toolbox( createToolboxBounds(), this, toolboxColor );
        getApparatusPanel().addGraphic( toolbox );
        addGraphic( circuitGraphic, 2 );
        addVirtualAmmeter();
        Voltmeter voltmeter = new Voltmeter( 5, 5, .7, this );
        try {
            voltmeterGraphic = new VoltmeterGraphic( voltmeter, getApparatusPanel(), this );
            interactiveVoltmeter = new InteractiveVoltmeter( voltmeterGraphic, this );
            getApparatusPanel().addGraphic( interactiveVoltmeter, 1000 );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        circuitGraphic.addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                voltmeterGraphic.recomputeVoltage();
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                voltmeterGraphic.recomputeVoltage();
            }

            public void junctionsMoved() {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        getCircuitSolver().addSolutionListener( new FireHandler( circuitGraphic ) );
        Rectangle2D rect = toolbox.getBounds2D();
        Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
        pt.translate( -130, 5 );
        wiggleMe = new WiggleMe( getApparatusPanel(), pt, new ImmutableVector2D.Double( 0, 1 ), 10, .025,
                                 SimStrings.get( "CCK3Module.GrabAWire" ) );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D mvt ) {
                Rectangle2D rect = toolbox.getBounds2D();
                Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
                pt.translate( -130, 5 );
                wiggleMe.setVisible( true );
                wiggleMe.setCenter( pt );
            }
        } );
        toolbox.addObserver( new SimpleObserver() {
            public void update() {
                Rectangle2D rect = toolbox.getBounds2D();
                Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
                pt.translate( -130, 5 );
                wiggleMe.setVisible( true );
                wiggleMe.setCenter( pt );
            }
        } );
        getApparatusPanel().addGraphic( wiggleMe, 100 );
        getModel().addModelElement( wiggleMe );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                if( branches.length > 0 ) {
                    getCircuit().removeCircuitListener( this );
                    wiggleMe.setVisible( false );
                    getApparatusPanel().removeGraphic( wiggleMe );
                    getModel().removeModelElement( wiggleMe );
                    if( wiggleMe.getBounds() != null ) {
                        getApparatusPanel().repaint( wiggleMe.getBounds() );
                    }
                }
            }
        } );
        help = new CCKHelp( this );
        setSeriesAmmeterVisible( false );
        setInternalResistanceOn( true );
    }

    public CCKApparatusPanel getCCKApparatusPanel() {
        return cckApparatusPanel;
    }

    public boolean isElectronsVisible() {
        return electronsVisible;
    }

    public void layoutElectrons( Branch[] branches ) {
        model.layoutElectrons( branches );
    }

    public Rectangle getViewBounds() {
        Rectangle viewBounds = getApparatusPanel().getBounds();
        viewBounds = new Rectangle( 0, 0, viewBounds.width, viewBounds.height );
        return viewBounds;
    }

    public CircuitSolver getCircuitSolver() {
        return model.getCircuitSolver();
    }

    public CircuitChangeListener getCircuitChangeListener() {
        return model.getCircuitChangeListener();
    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        help.setEnabled( h );
        getApparatusPanel().repaint();
    }

    private Rectangle2D createToolboxBounds() {
        double toolBoxWidthFrac = .085;
        double toolBoxInsetXFrac = 1 - toolBoxWidthFrac * 1.5;
        double toolBoxHeightFrac = 0.82;
        double toolBoxInsetYFrac = 0.015;
        Rectangle2D modelBounds = transform.getModelBounds();
        double y = modelBounds.getY() + modelBounds.getHeight() * toolBoxInsetYFrac;
        double x = modelBounds.getX() + modelBounds.getWidth() * toolBoxInsetXFrac;
        double h = modelBounds.getHeight() * toolBoxHeightFrac;
        return new Rectangle2D.Double( x, y, modelBounds.getWidth() * toolBoxWidthFrac, h );
    }

    private void addVirtualAmmeter() {
        virtualAmmeter = new VirtualAmmeter( circuitGraphic, getApparatusPanel(), this );
        getCircuitSolver().addSolutionListener( new CircuitSolutionListener() {
            public void circuitSolverFinished() {
                virtualAmmeter.recompute();
            }
        } );
        circuitGraphic.addCircuitGraphicListener( new CircuitGraphicListener() {
            public void graphicAdded( Branch branch, InteractiveGraphic graphic ) {
                virtualAmmeter.recompute();
            }

            public void graphicRemoved( Branch branch, InteractiveGraphic graphic ) {
                virtualAmmeter.recompute();
            }
        } );
        getCircuit().addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                virtualAmmeter.recompute();
            }

            public void junctionsMoved() {
                virtualAmmeter.recompute();
            }
        } );

        getApparatusPanel().addGraphic( virtualAmmeter, 30 );
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
//        this.aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, model.getAspectRatio() );
//        this.aspectRatioPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                relayout();
//            }
//        } );
//        app.getApplicationView().getBasicPhetPanel().setApparatusPanelContainer( aspectRatioPanel );
    }

    public Circuit getCircuit() {
        return model.getCircuit();
    }

    public ModelViewTransform2D getTransform() {
        return transform;
    }

    public CCK2ImageSuite getImageSuite() {
        return imageSuite;
    }

    public CircuitGraphic getCircuitGraphic() {
        return circuitGraphic;
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return circuitGraphic.getParticleSetGraphic();
    }

    public ParticleSet getParticleSet() {
        return model.getParticleSet();
    }

    public Rectangle2D.Double getJunctionBounds() {
        Junction[] j = getCircuit().getJunctions();
        Rectangle2D.Double rect = null;
        for( int i = 0; i < j.length; i++ ) {
            Junction junction = j[i];
            if( rect == null ) {
                rect = new Rectangle2D.Double( junction.getX(), junction.getY(), 0, 0 );
            }
            else {
                rect.add( new Point2D.Double( junction.getX(), junction.getY() ) );
            }
        }
        return rect;
    }

    public void setZoom( double scale ) {
        double newWidth = model.getModelWidth() * scale;
        double newHeight = model.getModelHeight() * scale;

        Rectangle2D jb = getJunctionBounds();
        Point2D.Double center = null;
        if( jb == null ) {
            center = RectangleUtils.getCenter2D( model.getModelBounds() );
        }
        else {
            center = RectangleUtils.getCenter2D( jb );
        }
        Rectangle2D.Double r = new Rectangle2D.Double( center.x - newWidth / 2, center.y - newHeight / 2, newWidth, newHeight );
        //could prevent people from zooming in beyond the boundary of the getCircuit(),
        //but someone may want to zoom in on just the bulb or something.
        transform.setModelBounds( r );
        toolbox.setModelBounds( createToolboxBounds(), cck3controlPanel.isSeriesAmmeterSelected() );
        getApparatusPanel().repaint();
    }

    public void setVirtualAmmeterVisible( boolean visible ) {
        virtualAmmeter.setVisible( visible );
    }

    public void removeParticlesAndGraphics( Branch branch ) {
        Electron[] out = getParticleSet().removeParticles( branch );
        circuitGraphic.getParticleSetGraphic().removeGraphics( out );
    }

    public void removeBranch( Branch branch ) {
        getCircuit().setFireKirkhoffChanges( false );
        removeParticlesAndGraphics( branch );
        circuitGraphic.removeGraphic( branch );
        getCircuit().remove( branch );

        //see if the adjacent junctions are free.
        testRemove( branch.getStartJunction() );
        testRemove( branch.getEndJunction() );

        //see if the adjacent junctions remain, and should be converted to component ends (for rotation)
        //if the junction remains, and it has exactly one connection, which is of type CircuitComponent.
        convertJunctionGraphic( branch.getStartJunction() );
        convertJunctionGraphic( branch.getEndJunction() );
        getCircuit().setFireKirkhoffChanges( true );
        getCircuit().fireKirkhoffChanged();
        getApparatusPanel().repaint();
    }

    private void convertJunctionGraphic( Junction junction ) {
        if( getCircuit().hasJunction( junction ) ) {
            Branch[] outputs = getCircuit().getAdjacentBranches( junction );
            if( outputs.length == 1 ) {
                Branch out = outputs[0];
                if( out instanceof CircuitComponent ) {
                    CircuitComponent cc = (CircuitComponent)out;
                    circuitGraphic.convertToComponentGraphic( junction, cc );
                }
            }
        }
    }

    private void testRemove( Junction st ) {
        Branch[] out = getCircuit().getAdjacentBranches( st );
        if( out.length == 0 ) {
            circuitGraphic.removeGraphic( st );
            getCircuit().remove( st );
        }
    }

    public BufferedImage loadBufferedImage( String s ) throws IOException {
        return imageSuite.getImageLoader().loadImage( s );
    }

    public void setVoltmeterVisible( boolean visible ) {
        interactiveVoltmeter.setVisible( visible );
    }

    public void clear() {
        while( getCircuit().numBranches() > 0 ) {
            Branch br = getCircuit().branchAt( 0 );
            removeBranch( br );
        }
    }

    public void setLifelike( boolean lifelike ) {
        toolbox.setLifelike( lifelike );
        circuitGraphic.setLifelike( lifelike );
        getCircuit().fireBranchesMoved( getCircuit().getBranches() );
        getCircuit().fireKirkhoffChanged();
        getApparatusPanel().repaint();
    }

    public void showMegaHelp() {
        cck3controlPanel.showHelpImage();
    }

    public Toolbox getToolbox() {
        return toolbox;
    }

    public void setCircuit( Circuit newCircuit ) {
        clear();
        for( int i = 0; i < newCircuit.numJunctions(); i++ ) {
            getCircuit().addJunction( newCircuit.junctionAt( i ) );
        }
        for( int i = 0; i < newCircuit.numBranches(); i++ ) {
            getCircuit().addBranch( newCircuit.branchAt( i ) );
        }
        for( int i = 0; i < getCircuit().numBranches(); i++ ) {
            circuitGraphic.addGraphic( getCircuit().branchAt( i ) );
        }
        //        circuitGraphic.fixJunctionGraphics();
        layoutElectrons( getCircuit().getBranches() );
        getCircuitSolver().apply( getCircuit() );
        circuitGraphic.reapplySolderGraphics();
        getApparatusPanel().repaint();
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void deleteSelection() {
        Branch[] sel = getCircuit().getSelectedBranches();
        for( int i = 0; i < sel.length; i++ ) {
            Branch branch = sel[i];
            removeBranch( branch );
        }
    }

    public void desolderSelection() {
        Junction[] sel = getCircuit().getSelectedJunctions();
        for( int i = 0; i < sel.length; i++ ) {
            Junction junction = sel[i];
            int numConnections = getCircuit().getNeighbors( junction ).length;
            if( numConnections > 1 ) {
                circuitGraphic.split( junction );
            }
        }
    }

    public void setSeriesAmmeterVisible( boolean selected ) {
        toolbox.setSeriesAmmeterVisible( selected );
        getApparatusPanel().repaint();
    }

    public void selectAll() {
        getCircuit().selectAll();
    }

    void resetDynamics() {
        getCircuit().resetDynamics();
    }

    void clockTickFinished() {
        if( cckApparatusPanel != null ) {
            cckApparatusPanel.synchronizeImmediately();
        }
    }

    public static CCKModule getModule() {
        return module;
    }

    public PhetShadowTextGraphic getTimescaleGraphic() {
        return messageGraphic;
    }

    public void regainFocus() {
        getApparatusPanel().requestFocus();
        Window window = SwingUtilities.getWindowAncestor( getApparatusPanel() );
        window.requestFocus();
    }

    public Shape getElectronClip() {
        return electronClip;
    }

    public void recomputeElectronClip() {
        this.electronClip = determineElectronClip();
    }

    private Area determineElectronClip() {
        Area area = new Area( new Rectangle2D.Double( -500, -500, 10000, 10000 ) );
        for( int i = 0; i < circuitGraphic.getBranchGraphics().length; i++ ) {
            //            if( circuitGraphic.getBranchGraphics()[i] instanceof SchematicCapacitorGraphic ) {
            if( circuitGraphic.getBranchGraphics()[i] instanceof CircuitComponentInteractiveGraphic ) {
                CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)circuitGraphic.getBranchGraphics()[i];
                if( ccig.getCircuitComponentGraphic() instanceof HasCapacitorClip ) {
                    HasCapacitorClip c = (HasCapacitorClip)ccig.getCircuitComponentGraphic();
                    Shape capacitorClip = c.getCapacitorClip();
                    //                    System.out.println( "capacitorClip.getBounds2D() = " + capacitorClip.getBounds2D() );
                    area.subtract( new Area( capacitorClip ) );
                }
            }
        }
        return area;
    }

    SwingClock stripChartClock = new SwingClock( 30, 1 );

    public void addCurrentChart() {
        stripChartClock.start();
        final CurrentStripChart chart = new CurrentStripChart( getApparatusPanel(), "Current (Amps)", stripChartClock, getCircuitGraphic() );
        chart.setOffset( 200, 200 );
        getApparatusPanel().addScreenChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                getApparatusPanel().removeScreenChild( chart );
            }
        } );
    }

    public void addVoltageChart() {
        stripChartClock.start();
        final VoltageStripChart chart = new VoltageStripChart( getApparatusPanel(), "Current", stripChartClock, getCircuitGraphic() );
        chart.setOffset( 250, 400 );
        getApparatusPanel().addScreenChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                getApparatusPanel().removeScreenChild( chart );
            }
        } );
    }

    public boolean isStopwatchVisible() {
        return stopwatch.getVisible();
    }

    public void setStopwatchVisible( boolean visible ) {
        stopwatch.setVisible( visible );
    }

    void setFrame( PhetFrame phetFrame ) {
        this.phetFrame = phetFrame;
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public void setElectronsVisible( boolean visible ) {
        this.electronsVisible = visible;
        if( model.getElectronLayout() != null && getCircuit() != null && getApparatusPanel() != null ) {
            model.getElectronLayout().branchesMoved( getCircuit().getBranches() );
            getApparatusPanel().repaint();
        }
    }

    private void add( Branch branch ) {
        getCircuit().addBranch( branch );
        circuitGraphic.addGraphic( branch );
    }

    public void addTestCircuit() {
        Branch b1 = toolbox.getBatterySource().createBranch();
        add( b1 );
        Branch b2 = toolbox.getResistorSource().createBranch();
        add( b2 );
        Branch b3 = toolbox.getWireSource().createBranch();
        add( b3 );

        new BranchSet( getCircuit(), new Branch[]{b1} ).translate( -5, 0 );
        AbstractVector2D dv = new Vector2D.Double( b2.getStartJunction().getPosition(), b1.getEndJunction().getPosition() );
        new BranchSet( getCircuit(), new Branch[]{b2} ).translate( dv );
        b3.getStartJunction().setPosition( b2.getEndJunction().getX(), b2.getEndJunction().getY() );
        b3.getEndJunction().setPosition( b1.getStartJunction().getX(), b1.getStartJunction().getY() );
        layoutElectrons( new Branch[]{b1, b2, b3} );
        circuitGraphic.collapseJunctions( b1.getEndJunction(), b2.getStartJunction() );
        circuitGraphic.collapseJunctions( b2.getEndJunction(), b3.getStartJunction() );
        circuitGraphic.collapseJunctions( b3.getEndJunction(), b1.getStartJunction() );
    }

    public CCKModel getCCKModel() {
        return model;
    }

    public void setResistivityEnabled( boolean selected ) {
        model.setResistivityEnabled( selected );
    }

    public ResistivityManager getResistivityManager() {
        return model.getResistivityManager();
    }

    public boolean isInternalResistanceOn() {
        return model.isInternalResistanceOn();
    }

    public void setInternalResistanceOn( boolean selected ) {
        model.setInternalResistanceOn( selected );
        updateReadoutGraphics();
        if( !selected ) {
            //hide all the internal resistance editors
            ArrayList batteryMenus = CircuitComponentInteractiveGraphic.BatteryJMenu.instances;
            for( int i = 0; i < batteryMenus.size(); i++ ) {
                CircuitComponentInteractiveGraphic.BatteryJMenu batteryJMenu = (CircuitComponentInteractiveGraphic.BatteryJMenu)batteryMenus.get( i );
                batteryJMenu.getResistanceEditor().setVisible( false );
            }
        }
    }

    private void updateReadoutGraphics() {
        ReadoutGraphic[] rg = circuitGraphic.getReadoutGraphics();
        for( int i = 0; i < rg.length; i++ ) {
            ReadoutGraphic readoutGraphic = rg[i];
            readoutGraphic.recompute();
        }
    }

//    resistivity is now always on by default.
//    public void setAdvancedEnabled( boolean advanced ) {
////        setInternalResistanceOn( cck3controlPanel.isInternalResistanceEnabled() && advanced );
////        setResistivityEnabled( advanced );
//    }

    public void debugListeners() {
        int numTransformListeners = transform.numTransformListeners();
        int numCircuitListeners = getCircuit().numCircuitListeners();
        System.out.println( "numCircuitListeners = " + numCircuitListeners );
        System.out.println( "numTransformListeners = " + numTransformListeners );
        System.out.println( "Arrays.asList( transform.getTransformListeners() ) = " + Arrays.asList( transform.getTransformListeners() ) );
        System.out.println( "getCircuit().getCircuitListeners() = " + Arrays.asList( getCircuit().getCircuitListeners() ) );
        int jg = JunctionGraphic.getInstanceCount();
        System.out.println( "jg = " + jg );
    }

    public SetupParameters getParameters() {
        return parameters;
    }
}
