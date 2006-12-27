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
import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.components.AspectRatioPanel;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.FrameSetup;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: May 24, 2004
 * Time: 1:27:42 AM
 * Copyright (c) May 24, 2004 by Sam Reid
 */
public class CCK3Module extends Module {
    Circuit circuit;
    CircuitGraphic circuitGraphic;
    boolean inited = false;
    private ModelViewTransform2D transform;
    private AspectRatioPanel aspectRatioPanel;

    CCK2ImageSuite imageSuite;
//    private ReadoutGraphic resistorReadout;
//    private ReadoutGraphic battReadout;
    private ParticleSet particleSet;
//    private ParticleSetGraphic particleSetGraphic;
    private ConstantDensityLayout layout;
    private KirkhoffSolver kirkhoffSolver;
    private KirkhoffListener kirkhoffListener;

    private static final double SCALE = .6;

    //    private static final double SCALE = .3;
    private double aspectRatio = 1.5;
    double modelWidth = 10;
    double modelHeight = modelWidth / aspectRatio;
    private Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );
    public static double ELECTRON_DX = .43 * SCALE;
    public static final ComponentDimension RESISTOR_DIMENSION = new ComponentDimension( 1.3 * SCALE, .6 * SCALE );
    public static final ComponentDimension SWITCH_DIMENSION = new ComponentDimension( 1.5 * SCALE, .8 * SCALE );
    public static final ComponentDimension LEVER_DIMENSION = new ComponentDimension( 1 * SCALE, .5 * SCALE );
    public static final ComponentDimension BATTERY_DIMENSION = new ComponentDimension( 1.5 * SCALE, .65 * SCALE );
    public static final ComponentDimension SERIES_AMMETER_DIMENSION = new ComponentDimension( 1.5 * SCALE, .65 * SCALE );
    static double bulbLength = 1;
    static double bulbHeight = 1.4;
    static double bulbDistJ = .333;
    static double bulbScale = 1.6;
    public static final BulbDimension BULB_DIMENSION = new BulbDimension( bulbLength * SCALE * bulbScale, bulbHeight * SCALE * bulbScale, bulbDistJ * SCALE * bulbScale );

//    private CompositeInteractiveGraphic readoutLayer;
    public static final double WIRE_LENGTH = BATTERY_DIMENSION.getLength() * 1.2;
    public static final double JUNCTION_GRAPHIC_STROKE_WIDTH = .01;
    public static final double JUNCTION_RADIUS = .162;
    public static final double STICKY_THRESHOLD = SCALE;
    private Toolbox toolbox;
    private VirtualAmmeter virtualAmmeter;
    private InteractiveVoltmeter interactiveVoltmeter;
    private VoltmeterGraphic voltmeterGraphic;


    public CCK3Module() throws IOException {
        super( "cck-iii" );
        Color backgroundColor = new Color( 166, 177, 204 );//not so bright
        setApparatusPanel( new ApparatusPanel() );
        getApparatusPanel().setBackground( backgroundColor.brighter() );
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
        setControlPanel( new CCK3ControlPanel( this ) );
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
        }

        if( transform != null && !transform.getViewBounds().equals( getViewBounds() ) ) {
            transform.setViewBounds( getViewBounds() );
            getApparatusPanel().repaint();
            circuit.updateAll();
        }
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
        transform = new ModelViewTransform2D( modelBounds, getViewBounds() );
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


        testInit();
    }

    private void setupToolbox() {
        double toolBoxWidthFrac = .07;
        double toolBoxInsetXFrac = 1 - toolBoxWidthFrac * 1.5;
        double toolBoxHeightFrac = .7;
        double toolBoxInsetYFrac = .05;
        Rectangle2D modelBounds = transform.getModelBounds();
        Rectangle2D toolboxBounds = new Rectangle2D.Double( modelBounds.getX() + modelBounds.getWidth() * toolBoxInsetXFrac,
                                                            modelBounds.getY() + modelBounds.getHeight() * toolBoxInsetYFrac,
                                                            modelBounds.getWidth() * toolBoxWidthFrac,
                                                            modelBounds.getHeight() * toolBoxHeightFrac );
        toolbox = new Toolbox( toolboxBounds, this );
        getApparatusPanel().addGraphic( toolbox );
    }

    private void testInit() {
//        Branch branch = circuit.createBranch( 4, 4, 7, 4 );
//        circuitGraphic.addWireGraphic( branch );
//
//        Branch branch2 = circuit.createBranch( 4, 2, 7, 2 );
//        circuitGraphic.addWireGraphic( branch2 );

//        Battery battery = new Battery( new Point2D.Double( 1, 1 ), new Vector2D.Double( 1, 3 ), 1, .5, getKirkhoffListener() );
//        circuitGraphic.addGraphic( battery );
//        circuit.addBranch( battery );
//
//        final Resistor resistor = new Resistor( new Point2D.Double( 1, 3 ), new Vector2D.Double( 1, 0 ), RESISTOR_LENGTH, RESISTOR_HEIGHT, getKirkhoffListener() );
//        circuitGraphic.addGraphic( resistor );
//        circuit.addBranch( resistor );
//
//        Switch switch1 = new Switch( new Point2D.Double( 5, 5 ), new Vector2D.Double( -1, 0 ), 1.5, .8, getKirkhoffListener() );
//        circuitGraphic.addGraphic( switch1 );
//        circuit.addBranch( switch1 );
//        double SUPER_BULB_SCALE = 3;
//        final Bulb bulb = new Bulb( new Point2D.Double( 5, 5 ), new ImmutableVector2D.Double( 0, 1 ),
//                                    BULB_DIMENSION.getLength() * SUPER_BULB_SCALE / 3,
//                                    BULB_DIMENSION.getLength() * SUPER_BULB_SCALE,
//                                    BULB_DIMENSION.getHeight() * SUPER_BULB_SCALE,
//                                    getKirkhoffListener() );
//        circuit.addBranch( bulb );
//        circuitGraphic.addGraphic( bulb );
//
//        layout.relayout( circuit.getBranches() );

        layout.relayout( circuit.getBranches() );
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

    static int r = 250;
    static int g = 250;
    static int b = 250;

    public static void main( String[] args ) throws IOException, UnsupportedLookAndFeelException {
        SwingTimerClock clock = new SwingTimerClock( 1, 30, true );

        Graphic colorG = new Graphic() {
            public void paint( Graphics2D gr ) {
                gr.setColor( new Color( r, g, b ) );
                gr.fillRect( 0, 0, 1000, 1000 );
            }
        };
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock c, double dt ) {
                r = ( r + 2 ) % 255;
                g = ( g + 3 ) % 255;
                b = ( b + 4 ) % 255;
            }
        } );

        final CCK3Module cck = new CCK3Module();
        //TODO debug useColors.
        boolean useColors = false;
//        boolean useColors = true;
        if( useColors ) {
            cck.addGraphic( colorG, -1 );
        }

        ApplicationModel model = new ApplicationModel( "CCK3", "cck-v3", "III",
//                                                       new FrameSetup.CenteredWithInsets( 50, 50 ), cck, clock );
//                                                       new FrameSetup.MaxExtent( new FrameSetup.CenteredWithInsets( 100,100) ), cck, clock );
                                                       new FrameSetup.CenteredWithInsets( 100, 100 ), cck, clock );
        PhetApplication app = new PhetApplication( model );

        app.startApplication();
//        Color backgroundColor = new Color( 200, 240, 200 );
//        PlayfulLookAndFeel laf = new PlayfulLookAndFeel( new Font( "Lucida Sans", 0, 28 ), Color.black, backgroundColor );
//        UIManager.setLookAndFeel( laf );
//        SwingUtilities.updateComponentTreeUI( app.getApplicationView().getPhetFrame() );
//        clock.addClockTickListener( new MovieRecorder(app.getApplicationView().getPhetFrame(),"cck") );
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
        getApparatusPanel().removeGraphic( toolbox );
        setupToolbox();
        getApparatusPanel().repaint();
    }

    public void setVirtualAmmeterVisible( boolean visible ) {
        virtualAmmeter.setVisible( visible );
    }

    public void removeBranch( Branch branch ) {
        Electron[] out = particleSet.removeParticles( branch );
        circuitGraphic.getParticleSetGraphic().removeGraphics( out );
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

//    boolean lifelike=true;
    public void setLifelike( boolean lifelike ) {
        circuitGraphic.setLifelike(lifelike);
        getApparatusPanel().repaint( );
//        if( lifelike == circuitGraphic.isLifelike() ) {
//            return;
//        }
//        getApparatusPanel().removeGraphic( circuitGraphic );
//        try {
//            circuitGraphic = new CircuitGraphic( this );
//            for( int i = 0; i < circuit.numBranches(); i++ ) {
//                Branch b = circuit.branchAt( i );
//                circuitGraphic.addGraphic( b );
//            }
//        }
//        catch( IOException e ) {
//            e.printStackTrace();
//        }
        //rebuild the graphics, based on lifelike-ness.
//        getApparatusPanel().removeGraphic( circuitGraphic );
        //TODO graphic elements should remove themselves as observers of model elemnts
        //so we don't bog down.

//        circuitGraphic.clear();
//        circuitGraphic.
    }

}
