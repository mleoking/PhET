/** Sam Reid*/
package edu.colorado.phet.cck3;

import edu.colorado.phet.cck3.circuit.*;
import edu.colorado.phet.cck3.circuit.components.CircuitComponent;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolutionListener;
import edu.colorado.phet.cck3.circuit.kirkhoff.KirkhoffSolver;
import edu.colorado.phet.cck3.circuit.particles.ConstantDensityLayout;
import edu.colorado.phet.cck3.circuit.particles.Electron;
import edu.colorado.phet.cck3.circuit.particles.ParticleSet;
import edu.colorado.phet.cck3.circuit.particles.ParticleSetGraphic;
import edu.colorado.phet.cck3.circuit.toolbox.Toolbox;
import edu.colorado.phet.cck3.circuit.tools.VirtualAmmeter;
import edu.colorado.phet.cck3.circuit.tools.Voltmeter;
import edu.colorado.phet.cck3.circuit.tools.VoltmeterGraphic;
import edu.colorado.phet.cck3.common.MovieRecorder;
import edu.colorado.phet.cck3.common.WiggleMe;
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.ImmutableVector2D;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.components.AspectRatioPanel;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.bounds.Boundary;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.phetgraphics.RepaintDebugGraphic;
import edu.colorado.phet.common.view.plaf.PlafUtil;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
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
    private Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    public static double ELECTRON_DX = .56 * SCALE;
    private static final double switchscale = 1.45;
    public static final ComponentDimension RESISTOR_DIMENSION = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension SWITCH_DIMENSION = new ComponentDimension( 1.5 * SCALE * switchscale, 0.8 * SCALE * switchscale );
    public static final ComponentDimension LEVER_DIMENSION = new ComponentDimension( 1.0 * SCALE * switchscale, 0.5 * SCALE * switchscale );
    public static final ComponentDimension BATTERY_DIMENSION = new ComponentDimension( 1.9 * SCALE, 0.7 * SCALE );
    public static final ComponentDimension SERIES_AMMETER_DIMENSION = new ComponentDimension( 2.33 * SCALE, .92 * SCALE );
//    private static double bulbLength = 1;
//    private static double bulbHeight = 1.4;
    private static double bulbLength = 1;
    private static double bulbHeight = 1.5;

//    private static double bulbDistJ = .333;
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
    public static Color backgroundColor = new Color( 200, 240, 200 );
    private DecimalFormat decimalFormat = new DecimalFormat( "#0.00" );
//    private DecimalFormat decimalFormat = new DecimalFormat( "#0.0000" ); //For debugging.
    private ResistivityManager resistivityManager;
    private static final double DEFAULT_RESISTANCE = 0.0001;
    private boolean internalResistanceOn = false;

    public CCK3Module() throws IOException {
        this( false );
    }

    public CCK3Module( boolean virtualLabMode ) throws IOException {
        super( "cck-iii" );
        this.virtualLabMode = virtualLabMode;
        Color backgroundColor = new Color( 166, 177, 204 );//not so bright
        setApparatusPanel( new ApparatusPanel() {
            public void repaint( long tm, int x, int y, int width, int height ) {
                super.repaint( tm, x, y, width, height );
            }

            public void repaint( Rectangle r ) {
                super.repaint( r );
            }

            public void repaint() {
                super.repaint();
            }

            public void repaint( int x, int y, int width, int height ) {
                super.repaint( x, y, width, height );
            }

            public void repaint( long tm ) {
                super.repaint( tm );
            }
        } );
        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                getApparatusPanel().requestFocus();
            }
        } );
        getApparatusPanel().setFocusable( true );
        getApparatusPanel().setBackground( backgroundColor.brighter() );
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

        setModel( new BaseModel() );
        kirkhoffSolver = new KirkhoffSolver();
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
//        transform = new ModelViewTransform2D.OriginTopLeft( modelBounds, viewBounds );
        circuitGraphic = new CircuitGraphic( this );
        setupToolbox();
        particleSet = new ParticleSet( circuit );
        layout = new ConstantDensityLayout( this );
        circuit.addCircuitListener( layout );
        addModelElement( particleSet );
        addGraphic( circuitGraphic, 2 );
        addVirtualAmmeter();
        Voltmeter voltmeter = new Voltmeter( 5, 5, .7 );
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
        wiggleMe = new WiggleMe( getApparatusPanel(), pt, new ImmutableVector2D.Double( 0, 1 ), 10, .025, "Grab a wire." );
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
        toolbox = new Toolbox( createToolboxBounds(), this, backgroundColor );
        getApparatusPanel().addGraphic( toolbox );
    }

    private void addVirtualAmmeter() {
        virtualAmmeter = new VirtualAmmeter( circuitGraphic, getApparatusPanel() );
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

    public void solve() {
        KirkhoffSolver ks = new KirkhoffSolver();
        ks.apply( circuit );
    }

    public ParticleSetGraphic getParticleSetGraphic() {
        return circuitGraphic.getParticleSetGraphic();
    }

    public ParticleSet getParticleSet() {
        return particleSet;
    }

    public void setZoom( double scale ) {
        double newWidth = modelWidth * scale;
        double newHeight = modelHeight * scale;
        Rectangle2D.Double r = new Rectangle2D.Double( 0, 0, newWidth, newHeight );
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
        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );

        boolean debugMode = false;
        if( Arrays.asList( args ).contains( "debug" ) ) {
            debugMode = true;
        }
        boolean virtualLab = false;
        if( Arrays.asList( args ).contains( "-virtuallab" ) ) {
            virtualLab = true;
        }

        final CCK3Module cck = new CCK3Module( virtualLab );
        RepaintDebugGraphic colorG = new RepaintDebugGraphic( cck.getApparatusPanel(), clock );

        FrameSetup fs = new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100, 100 ) );
        if( debugMode ) {
            fs = new FrameSetup.CenteredWithInsets( 0, 200 );
        }
        ApplicationModel model = new ApplicationModel( "Circuit Construction Kit III", "cck-v3", "III-v8+", fs, cck, clock );
        model.setName( "cck" );
        model.setUseClockControlPanel( false );
        PhetApplication app = new PhetApplication( model );

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

        UIManager.setLookAndFeel( cckLookAndFeel );
        PlafUtil.updateFrames();
        app.startApplication();
        PlafUtil.updateFrames();
        app.getApplicationView().getPhetFrame().doLayout();
        app.getApplicationView().getPhetFrame().repaint();
        cck.getApparatusPanel().addKeyListener( new CCKKeyListener( cck, colorG ) );
        cck.getApparatusPanel().requestFocus();
//        PlafUtil.updateFrames();
        if( debugMode ) {
            app.getApplicationView().getPhetFrame().setLocation( 0, 0 );
        }
        MovieRecorder movieRecorder = new MovieRecorder( app.getApplicationView().getPhetFrame(), "cckmovie" );
        clock.addClockTickListener( movieRecorder );
        movieRecorder.setRecording( false );
        cck.getApparatusPanel().addKeyListener( movieRecorder );
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
        if( selected == resistivityManager.isEnabled() ) {
            return;
        }
        else {
            resistivityManager.setEnabled( selected );
            if( !selected ) {
                setWireResistance( DEFAULT_RESISTANCE );
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
    }

}
