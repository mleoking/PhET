/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.chart.AbstractFloatingChart;
import edu.colorado.phet.cck3.chart.CCKTime;
import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.analysis.CircuitAnalysisCCKAdapter;
import edu.colorado.phet.cck3.circuit.analysis.CircuitSolutionListener;
import edu.colorado.phet.cck3.circuit.analysis.CircuitSolver;
import edu.colorado.phet.cck3.circuit.analysis.MNASolver;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck3.circuit.particles.ConstantDensityLayout;
import edu.colorado.phet.cck3.circuit.particles.Electron;
import edu.colorado.phet.cck3.circuit.particles.ParticleSet;
import edu.colorado.phet.cck3.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.cck3.circuit.toolbox.Toolbox;
import edu.colorado.phet.cck3.circuit.tools.VirtualAmmeter;
import edu.colorado.phet.cck3.circuit.tools.Voltmeter;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.cck3.common.WiggleMe;
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
    private SetupParameters parameters;
    private Circuit circuit;
    private CircuitGraphic circuitGraphic;
    private boolean inited = false;
    private ModelViewTransform2D transform;
    private AspectRatioPanel aspectRatioPanel;

    private CCK2ImageSuite imageSuite;
    private ParticleSet particleSet;
    private ConstantDensityLayout layout;
    private CircuitSolver circuitSolver;
    private CircuitChangeListener circuitChangeListener;

    private static final double SCALE = .5;
    private double aspectRatio = 1.2;
    private double modelWidth = 10;
    private double modelHeight = modelWidth / aspectRatio;
    //    private static final Rectangle2D.Double INIT_MODEL_BOUNDS = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    private Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    public static double ELECTRON_DX = .56 * SCALE;
    private static final double switchscale = 1.45;
    public static final ComponentDimension RESISTOR_DIMENSION = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension CAP_DIM = new ComponentDimension( 1.8 * SCALE, .6 * SCALE );
    public static final ComponentDimension AC_DIM = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension SWITCH_DIMENSION = new ComponentDimension( 1.5 * SCALE * switchscale, 0.8 * SCALE * switchscale );
    public static final ComponentDimension LEVER_DIMENSION = new ComponentDimension( 1.0 * SCALE * switchscale, 0.5 * SCALE * switchscale );
    public static final ComponentDimension BATTERY_DIMENSION = new ComponentDimension( 1.9 * SCALE, 0.7 * SCALE );
    public static final ComponentDimension SERIES_AMMETER_DIMENSION = new ComponentDimension( 2.33 * SCALE, .92 * SCALE );
    public static final ComponentDimension INDUCTOR_DIM = new ComponentDimension( 2.5 * SCALE, 0.6 * SCALE );
    private static double bulbLength = 1;
    private static double bulbHeight = 1.5;
    private static double bulbDistJ = .39333;
    private static double bulbScale = 1.9;
    public static final BulbDimension BULB_DIMENSION = new BulbDimension( bulbLength * SCALE * bulbScale, bulbHeight * SCALE * bulbScale, bulbDistJ * SCALE * bulbScale );

    public static final double WIRE_LENGTH = BATTERY_DIMENSION.getLength() * 1.2;
    public static final double JUNCTION_GRAPHIC_STROKE_WIDTH = .015;
    public static final double JUNCTION_RADIUS = .162;
    public static final double STICKY_THRESHOLD = SCALE;
    private Toolbox toolbox;
    private VirtualAmmeter virtualAmmeter;
    private InteractiveVoltmeter interactiveVoltmeter;
    private VoltmeterGraphic voltmeterGraphic;
    public static final double SCH_BULB_DIST = 1;
    private WiggleMe wiggleMe;
    private CCKHelp help;
    private CCK3ControlPanel cck3controlPanel;
    public static final Color backgroundColor = new Color( 200, 240, 200 );
//    public static final Color apparatusPanelColor = new Color( 187, 216, 255 );
    private static final Color apparatusPanelColor = new Color( 100, 160, 255 );
    public static final Color toolboxColor = new Color( 241, 241, 241 );

    private DecimalFormat decimalFormat = new DecimalFormat( "0.0#" );
    private ResistivityManager resistivityManager;
    private boolean internalResistanceOn = false;
    public static final double MIN_RESISTANCE = 1E-8;
    private boolean electronsVisible = true;
    private PhetFrame phetFrame;

    private CCKApparatusPanel magicPanel;
    static CCKModule module;
    public static final boolean GRAPHICAL_DEBUG = false;
    public PhetShadowTextGraphic messageGraphic;

    // Localization

    private Area electronClip = new Area();
    private PNode stopwatch;
    private boolean changed = false;

    public CCKModule( String[] args ) throws IOException {
        super( SimStrings.get( "ModuleTitle.CCK3Module" ) );
        module = this;
        this.parameters = new SetupParameters( this, args );

        magicPanel = new CCKApparatusPanel();
        magicPanel.setMyBackground( apparatusPanelColor );
        setApparatusPanel( magicPanel );
        clock.start();
        stopwatch = new MyPhetPNode( magicPanel, new PSwing( getApparatusPanel(), new StopwatchDecorator( clock, 1.0 * CCKTime.scale, "s" ) ) );
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

        setModel( new BaseModel() {
            public void stepInTime( double dt ) {
                //                long time = System.currentTimeMillis();
                super.stepInTime( dt );
                //                long now = System.currentTimeMillis();
                //                long dx = now - time;
                //                if( dx != 0 ) {
                //                    System.out.println( "Base Model = " + dx );
                //                }
            }
        } );
        circuitSolver = new CircuitAnalysisCCKAdapter( new MNASolver() );
        circuitChangeListener = new CircuitChangeListener() {
            public void circuitChanged() {
                if( isRunning() ) {
                    changed = true;
                }
                else {
                    circuitSolver.apply( circuit );
                }
            }

        };

        addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
//                System.out.println( "dt = " + dt );
                dt = 1.0;//todo we can no longer have DT dynamic because it destroys smoothness of the plots
                if( circuit.isDynamic() || changed ) {
                    circuit.stepInTime( dt );
                    circuitSolver.apply( circuit );
                }
            }
        } );

        circuit = new Circuit( circuitChangeListener );
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
        this.resistivityManager = new ResistivityManager( this );
        circuit.addCircuitListener( resistivityManager );
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

    private boolean isRunning() {
        return true;
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
                            circuit.updateAll();
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
            circuit.updateAll();
        }
    }

    public CCKApparatusPanel getMagicPanel() {
        return magicPanel;
    }

    public boolean isElectronsVisible() {
        return electronsVisible;
    }

    public void relayout( Branch[] branches ) {
        layout.relayout( branches );
    }

    public Rectangle getViewBounds() {
        Rectangle viewBounds = getApparatusPanel().getBounds();
        viewBounds = new Rectangle( 0, 0, viewBounds.width, viewBounds.height );
        return viewBounds;
    }

    public CircuitSolver getKirkhoffSolver() {
        return circuitSolver;
    }

    public CircuitChangeListener getKirkhoffListener() {
        return circuitChangeListener;
    }

    private void doinit() throws IOException {
        //        Rectangle viewBounds = new Rectangle( 0, 0, 100, 100 );
        Rectangle vb = getApparatusPanel().getBounds();
        Rectangle viewBounds = new Rectangle( vb.width, vb.height );
        transform = new ModelViewTransform2D( modelBounds, viewBounds );
        //        transform = new ModelViewTransform2D.OriginTopLeft( INIT_MODEL_BOUNDS, viewBounds );
        circuitGraphic = new CircuitGraphic( this );
        setupToolbox();
        particleSet = new ParticleSet( this, circuit );
        layout = new ConstantDensityLayout( this );
        circuit.addCircuitListener( layout );
        addModelElement( particleSet );
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

        circuitSolver.addSolutionListener( new CircuitSolutionListener() {
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
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                voltmeterGraphic.recomputeVoltage();
            }

            public void junctionsMoved() {
                voltmeterGraphic.recomputeVoltage();
            }
        } );
        circuitSolver.addSolutionListener( new FireHandler( circuitGraphic ) );
        Rectangle2D rect = toolbox.getBounds2D();
        Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
        pt.translate( -130, 5 );
        //        wiggleMe = new WiggleMe( getApparatusPanel(), pt,
        //                                 new ImmutableVector2D.Double( 0, 1 ), 10, .025, "Grab a wire." );
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
        circuit.addCircuitListener( new CircuitListenerAdapter() {
            public void branchesMoved( Branch[] branches ) {
                if( branches.length > 0 ) {
                    circuit.removeCircuitListener( this );
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

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        help.setEnabled( h );
        getApparatusPanel().repaint();
    }

    private Rectangle2D createToolboxBounds() {
        double toolBoxWidthFrac = .085;
        double toolBoxInsetXFrac = 1 - toolBoxWidthFrac * 1.5;
        double toolBoxHeightFrac = 0.82;
//        double toolBoxInsetYFrac = .05;
        double toolBoxInsetYFrac = 0.015;
        Rectangle2D modelBounds = transform.getModelBounds();
        double y = modelBounds.getY() + modelBounds.getHeight() * toolBoxInsetYFrac;
//        System.out.println( "modelBounds.getY() = " + modelBounds.getY() + ", modelBounds.geth=" + modelBounds.getHeight() + ", tbiyf=" + toolBoxInsetYFrac );
//        System.out.println( "y = " + y );
        double x = modelBounds.getX() + modelBounds.getWidth() * toolBoxInsetXFrac;
        double h = modelBounds.getHeight() * toolBoxHeightFrac;
        return new Rectangle2D.Double( x, y,
                                       modelBounds.getWidth() * toolBoxWidthFrac,
                                       h );
    }

    private void setupToolbox() {
        if( toolbox != null ) {
            throw new RuntimeException( "Only one toolbox per app, please." );
        }
        toolbox = new Toolbox( createToolboxBounds(), this, toolboxColor );
        getApparatusPanel().addGraphic( toolbox );
    }

    private void addVirtualAmmeter() {
        virtualAmmeter = new VirtualAmmeter( circuitGraphic, getApparatusPanel(), this );
        circuitSolver.addSolutionListener( new CircuitSolutionListener() {
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
        circuit.addCircuitListener( new CircuitListenerAdapter() {
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
        //Todo see below.
        /**
         * This code was originally used in CCK to ensure aspect ratio is correct.  However, it replaces the
         * existing apparatus panel container and is incompatibile with multiple modules.
         */

//        this.aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, aspectRatio );
//        this.aspectRatioPanel.addComponentListener( new ComponentAdapter() {
//            public void componentResized( ComponentEvent e ) {
//                relayout();
//            }
//        } );
//        app.getApplicationView().getBasicPhetPanel().setApparatusPanelContainer( aspectRatioPanel );

    }

    public Circuit getCircuit() {
        return circuit;
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
        return particleSet;
    }

    public Rectangle2D.Double getJunctionBounds() {
        Junction[] j = circuit.getJunctions();
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
        double newWidth = modelWidth * scale;
        double newHeight = modelHeight * scale;

        Rectangle2D jb = getJunctionBounds();
        Point2D.Double center = null;
        if( jb == null ) {
            center = RectangleUtils.getCenter2D( modelBounds );
        }
        else {
            center = RectangleUtils.getCenter2D( jb );
        }
        Rectangle2D.Double r = new Rectangle2D.Double( center.x - newWidth / 2, center.y - newHeight / 2, newWidth, newHeight );
        //could prevent people from zooming in beyond the boundary of the circuit,
        //but someone may want to zoom in on just the bulb or something.
        transform.setModelBounds( r );
        toolbox.setModelBounds( createToolboxBounds(), cck3controlPanel.isSeriesAmmeterSelected() );
        getApparatusPanel().repaint();
    }

    public void setVirtualAmmeterVisible( boolean visible ) {
        virtualAmmeter.setVisible( visible );
    }

    public void removeParticlesAndGraphics( Branch branch ) {
        Electron[] out = particleSet.removeParticles( branch );
        circuitGraphic.getParticleSetGraphic().removeGraphics( out );
    }

    public void removeBranch( Branch branch ) {
        circuit.setFireKirkhoffChanges( false );
        removeParticlesAndGraphics( branch );
        circuitGraphic.removeGraphic( branch );
        circuit.remove( branch );

        //see if the adjacent junctions are free.
        testRemove( branch.getStartJunction() );
        testRemove( branch.getEndJunction() );

        //see if the adjacent junctions remain, and should be converted to component ends (for rotation)
        //if the junction remains, and it has exactly one connection, which is of type CircuitComponent.
        convertJunctionGraphic( branch.getStartJunction() );
        convertJunctionGraphic( branch.getEndJunction() );
        circuit.setFireKirkhoffChanges( true );
        circuit.fireKirkhoffChanged();
        getApparatusPanel().repaint();

    }

    private void convertJunctionGraphic( Junction junction ) {
        if( circuit.hasJunction( junction ) ) {
            Branch[] outputs = circuit.getAdjacentBranches( junction );
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
        Branch[] out = circuit.getAdjacentBranches( st );
        if( out.length == 0 ) {
            circuitGraphic.removeGraphic( st );
            circuit.remove( st );
        }
    }

    public BufferedImage loadBufferedImage( String s ) throws IOException {
        return imageSuite.getImageLoader().loadImage( s );
    }

    public void setVoltmeterVisible( boolean visible ) {
        interactiveVoltmeter.setVisible( visible );
    }

    public void clear() {
        while( circuit.numBranches() > 0 ) {
            Branch br = circuit.branchAt( 0 );
            removeBranch( br );
        }
    }

    public void setLifelike( boolean lifelike ) {
        toolbox.setLifelike( lifelike );
        circuitGraphic.setLifelike( lifelike );
        circuit.fireBranchesMoved( circuit.getBranches() );
        circuit.fireKirkhoffChanged();
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
            circuit.addJunction( newCircuit.junctionAt( i ) );
        }
        for( int i = 0; i < newCircuit.numBranches(); i++ ) {
            circuit.addBranch( newCircuit.branchAt( i ) );
        }
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            circuitGraphic.addGraphic( circuit.branchAt( i ) );
        }
        //        circuitGraphic.fixJunctionGraphics();
        layout.relayout( circuit.getBranches() );
        circuitSolver.apply( circuit );
        circuitGraphic.reapplySolderGraphics();
        getApparatusPanel().repaint();
    }

    public DecimalFormat getDecimalFormat() {
        return decimalFormat;
    }

    public void deleteSelection() {
        Branch[] sel = circuit.getSelectedBranches();
        for( int i = 0; i < sel.length; i++ ) {
            Branch branch = sel[i];
            removeBranch( branch );
        }
    }

    public void desolderSelection() {
        Junction[] sel = circuit.getSelectedJunctions();
        for( int i = 0; i < sel.length; i++ ) {
            Junction junction = sel[i];
            int numConnections = circuit.getNeighbors( junction ).length;
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
        circuit.selectAll();
    }

    void resetDynamics() {
        circuit.resetDynamics();
    }

    void clockTickFinished() {
        if( magicPanel != null ) {
            magicPanel.synchronizeImmediately();
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
//        this.electronClip = recomputeElectronClip();
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

    SwingClock clock = new SwingClock( 30, 1 );

    public void addCurrentChart() {
        clock.start();
        final CurrentStripChart chart = new CurrentStripChart( getApparatusPanel(), "Current (Amps)", clock, getCircuitGraphic() );
        chart.setOffset( 200, 200 );
        getApparatusPanel().addScreenChild( chart );
        chart.addListener( new AbstractFloatingChart.Listener() {
            public void chartClosing() {
                getApparatusPanel().removeScreenChild( chart );
            }
        } );
    }

    public void addVoltageChart() {
        clock.start();
        final VoltageStripChart chart = new VoltageStripChart( getApparatusPanel(), "Current", clock, getCircuitGraphic() );
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
        if( layout != null && circuit != null && getApparatusPanel() != null ) {
            layout.branchesMoved( circuit.getBranches() );
            getApparatusPanel().repaint();
        }
    }

    private void add( Branch branch ) {
        circuitGraphic.getCircuit().addBranch( branch );
        circuitGraphic.addGraphic( branch );
    }

    public void addTestCircuit() {
        Branch b1 = toolbox.getBatterySource().createBranch();
        add( b1 );
        Branch b2 = toolbox.getResistorSource().createBranch();
        add( b2 );
        Branch b3 = toolbox.getWireSource().createBranch();
        add( b3 );

        new BranchSet( circuit, new Branch[]{b1} ).translate( -5, 0 );
        AbstractVector2D dv = new Vector2D.Double( b2.getStartJunction().getPosition(), b1.getEndJunction().getPosition() );
        new BranchSet( circuit, new Branch[]{b2} ).translate( dv );
        b3.getStartJunction().setPosition( b2.getEndJunction().getX(), b2.getEndJunction().getY() );
        b3.getEndJunction().setPosition( b1.getStartJunction().getX(), b1.getStartJunction().getY() );
        relayout( new Branch[]{b1, b2, b3} );
        circuitGraphic.collapseJunctions( b1.getEndJunction(), b2.getStartJunction() );
        circuitGraphic.collapseJunctions( b2.getEndJunction(), b3.getStartJunction() );
        circuitGraphic.collapseJunctions( b3.getEndJunction(), b1.getStartJunction() );
    }

    public static class ResistivityManager extends CircuitListenerAdapter {
        private CCKModule module;
        private Circuit circuit;
//        public static final double DEFAULT_RESISTIVITY = 0.01;
        public static final double DEFAULT_RESISTIVITY = MIN_RESISTANCE;
        private double resistivity = DEFAULT_RESISTIVITY;
        private boolean enabled = true;

        public ResistivityManager( CCKModule module ) {
            this.module = module;
            this.circuit = module.getCircuit();
        }

        public void junctionsMoved() {
            changed();
        }

        private void changed() {
            if( enabled ) {
                for( int i = 0; i < circuit.numBranches(); i++ ) {
                    Branch b = circuit.branchAt( i );
                    if( b.getClass().equals( Branch.class ) ) {//make sure it's not a component.
                        double resistance = getResistance( b );
                        b.setResistance( resistance );
                        //                        System.out.println( "resistance = " + resistance );
                    }
                }
            }
        }

        private double getResistance( Branch b ) {
            double length = b.getLength();
            double resistance = length * resistivity;
//            System.out.println( "resistance=" + resistance + ", min=" + MIN_RESISTANCE );
            if( resistance < MIN_RESISTANCE ) {
//                System.out.println( "Branch resistance was less than min, returning min." );//todo remove debug comment
                return MIN_RESISTANCE;
            }
            else {
                return resistance;
            }
//            resistance = Math.max( resistance, MIN_RESISTANCE );
//            return resistance;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled( boolean enabled ) {
            this.enabled = enabled;
            if( enabled ) {
                changed();
            }
        }

        public double getResistivity() {
            return resistivity;
        }

        public void setResistivity( double resistivity ) {
            if( this.resistivity != resistivity ) {
                this.resistivity = resistivity;
                changed();
            }
        }
    }

    public void setResistivityEnabled( boolean selected ) {
//        System.out.println( "Set resistivity enabled= " + selected );
        if( selected == resistivityManager.isEnabled() ) {
            return;
        }
        else {
            resistivityManager.setEnabled( selected );
            if( !selected ) {
                setWireResistance( MIN_RESISTANCE );
            }
        }
    }

    public ResistivityManager getResistivityManager() {
        return resistivityManager;
    }

    private void setWireResistance( double defaultResistance ) {
        for( int i = 0; i < circuit.numBranches(); i++ ) {
            Branch br = circuit.branchAt( i );
            if( br.getClass().equals( Branch.class ) ) {
                br.setResistance( defaultResistance );
            }
        }
    }

    public boolean isInternalResistanceOn() {
        return internalResistanceOn;
    }

    public void setInternalResistanceOn( boolean selected ) {
        if( this.internalResistanceOn != selected ) {
            this.internalResistanceOn = selected;
        }
        ReadoutGraphic[] rg = circuitGraphic.getReadoutGraphics();
        for( int i = 0; i < rg.length; i++ ) {
            ReadoutGraphic readoutGraphic = rg[i];
            readoutGraphic.recompute();
        }
        Branch[] b = circuit.getBranches();
        for( int i = 0; i < b.length; i++ ) {
            Branch branch = b[i];
            if( branch instanceof Battery ) {
                Battery batt = (Battery)branch;
                batt.setInternalResistanceOn( selected );
            }
        }
        if( !selected ) {
            //hide all the internal resistance editors
            ArrayList batteryMenus = CircuitComponentInteractiveGraphic.BatteryJMenu.instances;
            for( int i = 0; i < batteryMenus.size(); i++ ) {
                CircuitComponentInteractiveGraphic.BatteryJMenu batteryJMenu = (CircuitComponentInteractiveGraphic.BatteryJMenu)batteryMenus.get( i );
                batteryJMenu.getResistanceEditor().setVisible( false );
            }
        }
    }

    //todo: resistivity is now always on by default.
    public void setAdvancedEnabled( boolean advanced ) {
//        setInternalResistanceOn( cck3controlPanel.isInternalResistanceEnabled() && advanced );
//        setResistivityEnabled( advanced );
    }

    public void debugListeners() {
        int numTransformListeners = transform.numTransformListeners();
        int numCircuitListeners = circuit.numCircuitListeners();
        System.out.println( "numCircuitListeners = " + numCircuitListeners );
        System.out.println( "numTransformListeners = " + numTransformListeners );
        System.out.println( "Arrays.asList( transform.getTransformListeners() ) = " + Arrays.asList( transform.getTransformListeners() ) );
        System.out.println( "circuit.getCircuitListeners() = " + Arrays.asList( circuit.getCircuitListeners() ) );
        int jg = JunctionGraphic.getInstanceCount();
        System.out.println( "jg = " + jg );
    }

    public SetupParameters getParameters() {
        return parameters;
    }
}
