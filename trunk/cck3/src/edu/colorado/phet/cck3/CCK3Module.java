/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.components.Battery;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.components.CircuitComponentInteractiveGraphic;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolutionListener;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.cck3.circuit.kirkhoff.ModifiedNodalAnalysis;
import edu.colorado.phet.cck3.circuit.particles.ConstantDensityLayout;
import edu.colorado.phet.cck3.circuit.particles.Electron;
import edu.colorado.phet.cck3.circuit.particles.ParticleSet;
import edu.colorado.phet.cck3.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.cck3.circuit.toolbox.Toolbox;
import edu.colorado.phet.cck3.circuit.tools.VirtualAmmeter;
import edu.colorado.phet.cck3.circuit.tools.Voltmeter;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.cck3.common.ColorDialog;
import edu.colorado.phet.cck3.common.WiggleMe;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.AbstractVector2D;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.PhetFrame;
import edu.colorado.phet.common.view.components.AspectRatioPanel;
import edu.colorado.phet.common.view.graphics.Boundary;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.plaf.PlafUtil;
import edu.colorado.phet.common.view.util.FrameSetup;
import edu.colorado.phet.common.view.util.RectangleUtils;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.*;
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
public class CCK3Module extends Module {
    private boolean virtualLabMode = false;
    private Circuit circuit;
    private CircuitGraphic circuitGraphic;
    private boolean inited = false;
    private ModelViewTransform2D transform;
    private AspectRatioPanel aspectRatioPanel;

    private CCK2ImageSuite imageSuite;
    private ParticleSet particleSet;
    private ConstantDensityLayout layout;
    private KirkhoffSolver kirkhoffSolver;
    private KirkhoffListener kirkhoffListener;

    private static final double SCALE = .5;
    private double aspectRatio = 1.2;
    private double modelWidth = 10;
    private double modelHeight = modelWidth / aspectRatio;
//    private static final Rectangle2D.Double INIT_MODEL_BOUNDS = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    private Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    public static double ELECTRON_DX = .56 * SCALE;
    private static final double switchscale = 1.45;
    public static final ComponentDimension RESISTOR_DIMENSION = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension SWITCH_DIMENSION = new ComponentDimension( 1.5 * SCALE * switchscale, 0.8 * SCALE * switchscale );
    public static final ComponentDimension LEVER_DIMENSION = new ComponentDimension( 1.0 * SCALE * switchscale, 0.5 * SCALE * switchscale );
    public static final ComponentDimension BATTERY_DIMENSION = new ComponentDimension( 1.9 * SCALE, 0.7 * SCALE );
    public static final ComponentDimension SERIES_AMMETER_DIMENSION = new ComponentDimension( 2.33 * SCALE, .92 * SCALE );
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
    public static final Color apparatusPanelColor = new Color( 187, 216, 255 );
    public static final Color toolboxColor = new Color( 241, 241, 241 );
//    public static Color backgroundColor = new Color( 187, 216, 255 );
//    public static Color toolboxColor=new Color( );
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
//    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0000" ); //For debugging.
    private ResistivityManager resistivityManager;
    private boolean internalResistanceOn = false;
    public static final double MIN_RESISTANCE = 0.0001;
    private boolean electronsVisible = true;
    private PhetFrame phetFrame;
    public static boolean SHOW_GRAB_BAG = false;
    private MagicalRepaintPanel magicPanel;
    private static CCK3Module module;

    public MagicalRepaintPanel getMagicPanel() {
        return magicPanel;
    }

    public CCK3Module() throws IOException {
        this( false );
    }

    public boolean isElectronsVisible() {
        return electronsVisible;
    }

    public CCK3Module( boolean virtualLabMode ) throws IOException {
        super( "cck-iii" );
        this.virtualLabMode = virtualLabMode;
//        Color backgroundColor = new Color( 166, 177, 204 );//not so bright

        magicPanel = new MagicalRepaintPanel();
        setApparatusPanel( magicPanel );
//
//        setApparatusPanel( new ApparatusPanel() );
//        setApparatusPanel( new ApparatusPanel() {
//            protected void paintComponent( Graphics graphics ) {
////                long time = System.currentTimeMillis();
//                super.paintComponent( graphics );
////                long now = System.currentTimeMillis();
////                long dx = now - time;
////                if( dx != 0 ) {
////                    System.out.println( "Paint Time= " + dx );
////                }
//            }
//        } );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );
        getApparatusPanel().setFocusable( true );
        getApparatusPanel().setBackground( apparatusPanelColor );
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
            //Not allowed to mess with the way we call our abstract method.
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
//        kirkhoffSolver = new KirkhoffSolver();
        kirkhoffSolver = new ModifiedNodalAnalysis();
        kirkhoffListener = new KirkhoffListener() {
            public void circuitChanged() {
                kirkhoffSolver.apply( circuit );
            }
        };

        circuit = new Circuit( kirkhoffListener );
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
        setControlPanel( controlPanel );
//        doinit();
//        inited = true;
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

    public boolean isVirtualLabMode() {
        return virtualLabMode;
    }

    public void relayout( Branch[] branches ) {
        layout.relayout( branches );
    }

    public Rectangle getViewBounds() {
        Rectangle viewBounds = getApparatusPanel().getBounds();
        viewBounds = new Rectangle( 0, 0, viewBounds.width, viewBounds.height );
        return viewBounds;
    }

    public KirkhoffSolver getKirkhoffSolver() {
        return kirkhoffSolver;
    }

    public KirkhoffListener getKirkhoffListener() {
        return kirkhoffListener;
    }

    private void doinit() throws IOException {
//        Rectangle viewBounds = new Rectangle( 0, 0, 100, 100 );
        Rectangle vb = getApparatusPanel().getBounds();
        Rectangle viewBounds = new Rectangle( vb.width, vb.height );
        transform = new ModelViewTransform2D( modelBounds, viewBounds );
//        transform = new ModelViewTransform2D.OriginTopLeft( INIT_MODEL_BOUNDS, viewBounds );
        circuitGraphic = new CircuitGraphic( this );
        setupToolbox();
        particleSet = new ParticleSet( circuit );
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

        kirkhoffSolver.addSolutionListener( new KirkhoffSolutionListener() {
            public void finishedKirkhoff() {
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
        kirkhoffSolver.addSolutionListener( new FireHandler( circuitGraphic ) );
        Rectangle2D rect = toolbox.getBounds2D();
        Point pt = transform.modelToView( rect.getX(), rect.getY() + rect.getHeight() );
        pt.translate( -130, 5 );
//        wiggleMe = new WiggleMe( getApparatusPanel(), pt,
//                                 new ImmutableVector2D.Double( 0, 1 ), 10, .025, "Grab a wire." );
        wiggleMe = new WiggleMe( getApparatusPanel(), pt, new ImmutableVector2D.Double( 0, 1 ), 10, .025, "Grab a wire" );
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
        //a debugging element.
        //        ModelElement me = new ModelElement() {
        //            public void stepInTime( double dt ) {
        //                int numJunctions = circuit.numJunctions();
        //                int numBranches = circuit.numBranches();
        //                String text = "numJunctions=" + numJunctions + ", branches=" + numBranches;
        //                if( !debugText.equals( text ) ) {
        //                    debugText = text;
        //                    int ival = (int)( Math.random() * 10 );
        //                    System.out.println( ival + ": " + text );
        //                }
        //            }
        //        };
        //        getModel().addModelElement( me );
        setSeriesAmmeterVisible( false );

    }

    public void setHelpEnabled( boolean h ) {
        super.setHelpEnabled( h );
        help.setEnabled( h );
        getApparatusPanel().repaint();
    }

    private Rectangle2D createToolboxBounds() {
        double toolBoxWidthFrac = .07;
        double toolBoxInsetXFrac = 1 - toolBoxWidthFrac * 1.5;
        double toolBoxHeightFrac = .7;
        double toolBoxInsetYFrac = .05;
        Rectangle2D modelBounds = transform.getModelBounds();
        Rectangle2D toolboxBounds = new Rectangle2D.Double( modelBounds.getX() + modelBounds.getWidth() * toolBoxInsetXFrac,
                                                            modelBounds.getY() + modelBounds.getHeight() * toolBoxInsetYFrac,
                                                            modelBounds.getWidth() * toolBoxWidthFrac,
                                                            modelBounds.getHeight() * toolBoxHeightFrac );
        return toolboxBounds;
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
        kirkhoffSolver.addSolutionListener( new KirkhoffSolutionListener() {
            public void finishedKirkhoff() {
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
        this.aspectRatioPanel = new AspectRatioPanel( getApparatusPanel(), 5, 5, aspectRatio );
        this.aspectRatioPanel.addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );
        app.getApplicationView().getBasicPhetPanel().setApparatusPanelContainer( aspectRatioPanel );
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

//    public void solve() {
//
//    }

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
        kirkhoffSolver.apply( circuit );
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

    public static void main( String[] args ) throws IOException, UnsupportedLookAndFeelException {
        SwingTimerClock clock = new SwingTimerClock( 1, 30, false );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
        }
        boolean virtualLab = false;
        if( Arrays.asList( args ).contains( "-virtuallab" ) ) {
            virtualLab = true;
        }
        if( Arrays.asList( args ).contains( "-grabbag" ) ) {
            SHOW_GRAB_BAG = true;
        }

        final CCK3Module cck = new CCK3Module( virtualLab );
        CCK3Module.module = cck;

        RepaintDebugGraphic colorG = new RepaintDebugGraphic( cck.getApparatusPanel(), clock );

        FrameSetup fs = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
        if( debugMode ) {
            fs = new FrameSetup.CenteredWithInsets( 0, 200 );
        }
        ApplicationModel model = new ApplicationModel( "Circuit Construction Kit III", "cck-v3", "III-v8+", fs, cck, clock );
        model.setName( "cck" );
        model.setUseClockControlPanel( false );
        final PhetApplication app = new PhetApplication( model );

        CCKLookAndFeel cckLookAndFeel = new CCKLookAndFeel();
        UIManager.installLookAndFeel( "CCK Default", cckLookAndFeel.getClass().getName() );

        JMenu laf = new JMenu( "View" );
        laf.setMnemonic( 'v' );
        JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < jmi.length; i++ ) {
            JMenuItem jMenuItem = jmi[i];
            laf.add( jMenuItem );
        }
        app.getApplicationView().getPhetFrame().addMenu( laf );

        JMenu dev = new JMenu( "Options" );
        dev.setMnemonic( 'o' );
        JMenuItem changeBackgroundColor = new JMenuItem( "Background Color" );
        changeBackgroundColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.getApparatusPanel().setBackground( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.getApparatusPanel().setBackground( orig );
                    }

                    public void ok( Color color ) {
                        cck.getApparatusPanel().setBackground( color );
                    }
                };
                ColorDialog.showDialog( "Background Color", app.getApplicationView().getPhetFrame(), cck.getApparatusPanel().getBackground(), listy );
            }
        } );
        cck.setFrame( app.getApplicationView().getPhetFrame() );
        JMenuItem toolboxColor = new JMenuItem( "Toolbox Color" );
        toolboxColor.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ColorDialog.Listener listy = new ColorDialog.Listener() {
                    public void colorChanged( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }

                    public void cancelled( Color orig ) {
                        cck.getToolbox().setBackgroundColor( orig );
                    }

                    public void ok( Color color ) {
                        cck.getToolbox().setBackgroundColor( color );
                    }
                };
                ColorDialog.showDialog( "Toolbox Color", app.getApplicationView().getPhetFrame(), cck.getToolbox().getBackgroundColor(), listy );
            }
        } );

        dev.add( changeBackgroundColor );
        dev.add( toolboxColor );
        app.getApplicationView().getPhetFrame().addMenu( dev );

        UIManager.setLookAndFeel( cckLookAndFeel );
        updateFrames();
        app.startApplication();
        updateFrames();
        app.getApplicationView().getPhetFrame().doLayout();
        app.getApplicationView().getPhetFrame().repaint();
        cck.getApparatusPanel().addKeyListener( new CCKKeyListener( cck, colorG ) );
        cck.getApparatusPanel().addKeyListener( new SimpleKeyEvent( KeyEvent.VK_D ) {
            public void invoke() {
                cck.debugListeners();
            }
        } );
        cck.getApparatusPanel().requestFocus();
//        PlafUtil.updateFrames();
        if( debugMode ) {
            app.getApplicationView().getPhetFrame().setLocation( 0, 0 );
        }
        final JFrame frame = app.getApplicationView().getPhetFrame();
        final Runnable repainter = new Runnable() {
            public void run() {
                Component c = frame.getContentPane();
                c.invalidate();
                c.validate();
                c.repaint();
            }
        };
        frame.addWindowFocusListener( new WindowFocusListener() {
            public void windowGainedFocus( WindowEvent e ) {
                repainter.run();
            }

            public void windowLostFocus( WindowEvent e ) {
            }
        } );
        frame.addWindowListener( new WindowAdapter() {
            public void windowActivated( WindowEvent e ) {
            }

            public void windowStateChanged( WindowEvent e ) {
                repainter.run();
            }

            public void windowGainedFocus( WindowEvent e ) {
                repainter.run();
            }
        } );
        frame.addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                repainter.run();
            }

            public void componentResized( ComponentEvent e ) {
                repainter.run();
            }

            public void componentMoved( ComponentEvent e ) {
                repainter.run();
            }
        } );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                cck.clockTickFinished();
            }
        } );
    }

    private void clockTickFinished() {
        if( magicPanel != null ) {
            magicPanel.synchronizeImmediately();
        }
    }

    public static CCK3Module getModule() {
        return module;
    }

    public static class SimpleKeyEvent implements KeyListener {
        int keycode;

        protected SimpleKeyEvent( int keycode ) {
            this.keycode = keycode;
        }

        public void keyPressed( KeyEvent e ) {
        }

        public void keyReleased( KeyEvent e ) {
            if( e.getKeyCode() == keycode ) {
                invoke();
            }
        }

        public void invoke() {
        }

        public void keyTyped( KeyEvent e ) {
        }
    }

    private void setFrame( PhetFrame phetFrame ) {
        this.phetFrame = phetFrame;
    }

    public PhetFrame getPhetFrame() {
        return phetFrame;
    }

    public static void testUpdate( Window window ) {
        String title = window.getName();
        if( window instanceof Frame ) {
            Frame f = (Frame)window;
            title = f.getTitle();
            if( title == null ) {
                title = "";
            }
            title = title.trim().toLowerCase();
            if( title.indexOf( "Java Web Start Console".toLowerCase() ) >= 0 ) {
                //ignore
            }
            else if( title.indexOf( "Java Console".toLowerCase() ) >= 0 ) {
                //ignore.
            }
            else {
                SwingUtilities.updateComponentTreeUI( window );
            }
        }

    }

    public static void updateFrames() {
        Frame[] frames = JFrame.getFrames();
        ArrayList alreadyChecked = new ArrayList();
        for( int i = 0; i < frames.length; i++ ) {
            Frame frame = frames[i];
            testUpdate( frame );
            if( !alreadyChecked.contains( frame ) ) {
                Window[] owned = frames[i].getOwnedWindows();
                for( int j = 0; j < owned.length; j++ ) {
                    Window window = owned[j];
                    testUpdate( window );
                }
            }
            alreadyChecked.add( frames[i] );
        }
    }

    public void setElectronsVisible( boolean visible ) {
        this.electronsVisible = visible;
        layout.branchesMoved( circuit.getBranches() );
        getApparatusPanel().repaint();
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
        private CCK3Module module;
        private Circuit circuit;
        private static double DEFAULT_RESISTIVITY = 0.0;
        private double resistivity = DEFAULT_RESISTIVITY;
        private boolean enabled = true;

        public ResistivityManager( CCK3Module module ) {
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
            resistance = Math.max( resistance, MIN_RESISTANCE );
            return resistance;
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
//                Graphic g = circuitGraphic.getGraphic( batt );
//                if( g != null ) {
//                    CircuitComponentInteractiveGraphic ccig = (CircuitComponentInteractiveGraphic)g;
//                    if (!selected){
//                        //hide any battery internal resistance editors.
//
//                    }
//                }
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

    public void setAdvancedEnabled( boolean advanced ) {
//        this.advanced = advanced;
        if( advanced ) {
            setInternalResistanceOn( cck3controlPanel.isInternalResistanceEnabled() );
            setResistivityEnabled( true );
//            resistivityManager.setResistivity( );
        }
        else {
            setInternalResistanceOn( false );
            setResistivityEnabled( false );
//            resistivityManager.setResistivity( ResistivityManager.DEFAULT_RESISTIVITY );
        }
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
//        SimpleObservableDebug[] sod = SimpleObservableDebug.instances();
//        for( int i = 0; i < sod.length; i++ ) {
//            SimpleObservableDebug observable = sod[i];
//            System.out.println( "observable[" + i + "] < x" + observable.numObservers() + ">  = " + observable );
//        }

//        for( int i = 0; i < sod.length; i++ ) {
//            SimpleObservableDebug observable = sod[i];
//            System.out.println( "observable[" + i + "] < x" + observable.numObservers() + ">  = " + observable );
//            SimpleObserver[] so = observable.getObservers();
//            System.out.println( "Arrays.asList( so ) = " + Arrays.asList( so ) );
//        }
    }

    static class MagicalRepaintPanel extends ApparatusPanel {
        private ArrayList rectangles = new ArrayList();

        public MagicalRepaintPanel() {
            addMouseMotionListener( new MouseMotionAdapter() {
                public void mouseDragged( MouseEvent e ) {
                    synchronizeImmediately();
                    try {
                        Thread.sleep( 10 );
                    }
                    catch( InterruptedException e1 ) {
                        e1.printStackTrace();
                    }
                }
            } );
        }

        public void repaint( long tm, int x, int y, int width, int height ) {
            repaint( x, y, width, height );
        }

        public void repaint( Rectangle r ) {
            repaint( r.x, r.y, r.width, r.height );
        }

        public void repaint() {
            repaint( 0, 0, getWidth(), getHeight() );
        }

        public void repaint( int x, int y, int width, int height ) {
            if( rectangles != null ) {
                Rectangle r = new Rectangle( x, y, width, height );
                rectangles.add( r );
            }
        }

        public void repaint( long tm ) {
            repaint();
        }

        public void synchronizeImmediately() {
            Rectangle rect = union();
            if( rect != null ) {
                paintImmediately( rect );
            }
            rectangles.clear();
        }

        private Rectangle union() {
            if( rectangles.size() == 0 ) {
                return new Rectangle();
            }
            else {
                Rectangle union = (Rectangle)rectangles.get( 0 );
                for( int i = 1; i < rectangles.size(); i++ ) {
                    Rectangle rectangle = (Rectangle)rectangles.get( i );
                    union = union.union( rectangle );
                }
                return union;
            }
        }
    }
}
