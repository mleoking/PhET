package edu.colorado.phet.bernoulli;

import edu.colorado.phet.bernoulli.common.FakeClock;
import edu.colorado.phet.bernoulli.common.RepaintManager;
import edu.colorado.phet.bernoulli.meter.Barometer;
import edu.colorado.phet.bernoulli.meter.BarometerView;
import edu.colorado.phet.bernoulli.pump.*;
import edu.colorado.phet.bernoulli.tube.Tube;
import edu.colorado.phet.bernoulli.tube.TubeGraphic;
import edu.colorado.phet.bernoulli.valves.VerticalValveGraphic;
import edu.colorado.phet.bernoulli.watertower.CompositeWaterTowerGraphic;
import edu.colorado.phet.bernoulli.watertower.TankWaterGraphic;
import edu.colorado.phet.bernoulli.watertower.WaterTower;
import edu.colorado.phet.bernoulli.zoom.ZoomPins;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.coreadditions.clock2.DefaultClock;
import edu.colorado.phet.coreadditions.clock2.IdentityTimeConverter;
import edu.colorado.phet.coreadditions.clock2.SimulationTimeListener;
import edu.colorado.phet.coreadditions.clock2.WallToSimulationTimeConverter;
import edu.colorado.phet.coreadditions.clock2.clocks.SwingTimerClock;
import edu.colorado.phet.coreadditions.clock2.components.DefaultClockStatePanel;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.plaf.PlafUtil;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObservable;
import edu.colorado.phet.coreadditions.simpleobserver.SimpleObserver;
import edu.colorado.phet.coreadditions.util.VersionUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Aug 19, 2003
 * Time: 12:46:01 AM
 * Copyright (c) Aug 19, 2003 by Sam Reid
 */
public class BernoulliModule extends Module {
    private static final String VERSION = "0.00.01";
    private int backgroundGraphicLevel = 0;
    int vesselGraphicLevel = 50;
    private int valveGraphicLevel = 60;
    int tubeGraphicLevel = 40;
    private int waterGraphicLevel = vesselGraphicLevel + 5;
    private int instrumentGraphicLevel = 1000;

    private static DefaultClock defaultClock;
    private WaterTower tower;
    PhetApplication parent;
    private ModelViewTransform2d transform;
    RepaintManager rm;
    public static boolean antialias = true;
    private int waterTowerX = 4;
    private int waterTowerY = 15;
    private int waterTowerWidth = 3;
    private int waterTowerHeight = 3;
    private double pumpX = -1;
    private double pumpY = .5;
    private double pumpWidth = 3;
    private double pumpHeight = 3;
    private int groundHeight = 0;
    Rectangle2D.Double viewPortInit;
    Color backgroundColor;
    private Tube pumpToTowerTube;
    private Pump pump;
    private BernoulliModel bernoulliModel;
    private static Module activeModule;
    boolean gravity = true;
    protected ArrayList drops = new ArrayList();
    private static boolean addedViewMenu;
    private static JFrame clockControlFrame;


    public BernoulliModule( DefaultClock dc ) {
        this( "Water Distribution System", dc );
    }

    protected BernoulliModule( String name, DefaultClock dc ) {
        super( name );
        super.setModel( new BaseModel() );
        super.setApparatusPanel( new ApparatusPanel() );
        rm = new RepaintManager( getApparatusPanel() );

        // Set up transform
        createModelViewTransform();

        // Make the pump
        final Pump pump = makePump( dc );

        // Make the pump
        final AutoPump autopump = new AutoPump( pump );
        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                autopump.simulationTimeIncreased( dt );
            }
        } );

        // Make the tower
        tower = new WaterTower( waterTowerX, waterTowerY, waterTowerWidth, waterTowerHeight, groundHeight, this );
        getModel().addModelElement( tower.getModelElement() );
        tower.setFractionalWaterVolume( .8 );
        CompositeWaterTowerGraphic towerGraphic = new CompositeWaterTowerGraphic( tower, transform, getApparatusPanel(), backgroundColor );
        getApparatusPanel().addGraphic( towerGraphic, vesselGraphicLevel );

        // Add a pipe from the lake to the pump, and pump to tower
        makeLakeToPumpTube();
        makePumpToTowerTube();

        // Create the Earth
        double radius = 6.37 * Math.pow( 10, 6 );//mean earth radius in meters.
        Point2D.Double centerOfEarth = new Point2D.Double( 0, -radius );
        EarthGraphic earthGraphic = new EarthGraphic( centerOfEarth.x, centerOfEarth.y, radius, transform );
        getApparatusPanel().addGraphic( earthGraphic, backgroundGraphicLevel );
        double lakeWidth = 80;//in meters
        double lakeHeight = 20;
        double lakeFudgeFactor = 7.5;
        LakeGraphic lakeGraphic = new LakeGraphic( pump.getX() - lakeWidth + lakeFudgeFactor, 0, lakeWidth, lakeHeight, transform, earthGraphic );
        getApparatusPanel().addGraphic( lakeGraphic, backgroundGraphicLevel + 5 );

        // Add the tanks, pumps, and stuff to the bernoulliModel
        bernoulliModel = new BernoulliModel();
        bernoulliModel.addVessel( pump.getTank() );
        bernoulliModel.addVessel( tower.getTank() );
        pump.addPumpListener( tower );

        // Add the instrumentation to the panel
        final Barometer b = new Barometer( pump.getX(), 18, bernoulliModel );

        BarometerView barometerView = new BarometerView( b, 20, 20, getTransform() );
        b.addObserver( new SimpleObserver() {
            public void update() {
                getApparatusPanel().repaint();
            }
        } );
        getApparatusPanel().addGraphic( barometerView, instrumentGraphicLevel );
        getTower().getTank().addObserver( new SimpleObserver() {
            public void update() {
                b.update();
            }
        } );
    }


    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public PhetApplication getParent() {
        return parent;
    }

    public void zoomTo( Rectangle2D.Double rect ) {
        zoomTo( rect, 30 );
    }

    public void zoomTo( Rectangle2D.Double rect, int numSteps ) {
        defaultClock.addTickListener( new ZoomPins( defaultClock, rect, transform, numSteps ) );
    }

    public void resize() {
        Rectangle bounds = getApparatusPanel().getBounds();
        if( bounds.width > 100 && bounds.height > 100 ) {
            transform.setViewBounds( getApparatusPanel().getBounds() );
        }
    }

    public void activateInternal( PhetApplication app ) {
        activate( app );
    }

    public void deactivateInternal( PhetApplication app ) {
    }

    public static void addViewMenu( PhetApplication app ) {
        if( addedViewMenu ) {
            return;
        }
        else {
            JMenuItem[] jmi = PlafUtil.getLookAndFeelItems();
            JMenu menu = new JMenu( "View" );
            for( int i = 0; i < jmi.length; i++ ) {
                JMenuItem jMenuItem = jmi[i];
                menu.add( jMenuItem );
            }
            app.getApplicationView().getPhetFrame().addMenu( menu );
            addedViewMenu = true;
        }
    }

    public void activate( PhetApplication app ) {
        activeModule = this;
        parent = app;
        addViewMenu( app );
    }

    public void deactivate( PhetApplication app ) {
        parent = null;
    }

    public boolean isGravity() {
        return gravity;
    }

    public void setGravity( boolean gravity ) {
        this.gravity = gravity;
    }

    public void addDrop( Drop drop ) {
        DropGraphic graphic = new DropGraphic( drop, transform, rm, this );
        drops.add( drop );
        getApparatusPanel().addGraphic( graphic, waterGraphicLevel );
        getModel().addModelElement( drop );
    }

    public void removeDropAndGraphic( DropGraphic dropGraphic ) {
        getModel().removeModelElement( dropGraphic.getDrop() );
        getApparatusPanel().removeGraphic( dropGraphic );
        drops.remove( dropGraphic.getDrop() );
    }

    public RepaintManager getRepaintManager() {
        return rm;
    }

    public WaterTower getWaterTower() {
        return tower;
    }

    public ModelViewTransform2d getTransform() {
        return transform;
    }

    public void zoomToPump() {
        Rectangle2D.Double pumpRect = new Rectangle2D.Double( pumpX, pumpY, pumpWidth, pumpHeight );
        pumpRect.x -= .5;
        pumpRect.y -= .5;
        pumpRect.width += 1;
        pumpRect.height += 1;
        zoomTo( pumpRect );
    }

    public void zoomToFull() {
        zoomTo( viewPortInit );
    }

    private void setPumpToTowerTube() {
        final AttachmentPoint ap = tower.getTopleftAttachmentPoint();
        pumpToTowerTube.clear();
        pumpToTowerTube.addPoint( new Point2D.Double( pump.getTopValve().getX(), pump.getTopValve().getY() ) );
        pumpToTowerTube.addPoint( new Point2D.Double( pump.getTopValve().getX(), ap.getY() + .2 ) );
        pumpToTowerTube.addPoint( new Point2D.Double( 4, ap.getY() + .2 ) );
        pumpToTowerTube.updateObservers();
    }

    private Pump makePump( DefaultClock dc ) {
        pump = new Pump( rm, this, new Rectangle2D.Double( pumpX, pumpY, pumpWidth, pumpHeight ) );
        getModel().addModelElement( pump );
        VerticalValveGraphic bottomValveGraphic = new VerticalValveGraphic( pump.getBottomValve(), transform, getApparatusPanel(), false, backgroundColor );
        getApparatusPanel().addGraphic( bottomValveGraphic, valveGraphicLevel );
        pump.getBottomValve().addObserver( rm );

        dc.addTickListener( rm );
        VerticalValveGraphic topValveGraphic = new VerticalValveGraphic( pump.getTopValve(), transform, getApparatusPanel(), true, backgroundColor );
        getApparatusPanel().addGraphic( topValveGraphic, valveGraphicLevel );

        Piston piston = pump.getPiston();
        PistonGraphic pistonGraphic = new PistonGraphic( piston, transform, getApparatusPanel(), rm );
        getApparatusPanel().addGraphic( pistonGraphic, vesselGraphicLevel );

        TankWaterGraphic tankGraphic = new TankWaterGraphic( pump.getTank(), transform );
        getApparatusPanel().addGraphic( tankGraphic, vesselGraphicLevel );

        RectangleGraphic pumpBoundsGraphic = new RectangleGraphic( pump.getTank().getRectangle2D(), transform );
        getApparatusPanel().addGraphic( pumpBoundsGraphic, vesselGraphicLevel );
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                resize();
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );

        // Make the button that turns the pump on and off
        final JButton jb = new JButton( "On" );
        jb.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        jb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( jb.getText().equals( "On" ) ) {
                    jb.setText( "Off" );
                    AutoPump.active = true;
                }
                else {
                    jb.setText( "On" );
                    AutoPump.active = false;
                }
            }
        } );
        final Rectangle2D.Double buttonModelBounds = new Rectangle2D.Double( .27, 3.56, 2, 1.3 );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                Rectangle r = mvt.modelToView( buttonModelBounds );
                jb.setBounds( r );
            }
        } );
        getApparatusPanel().add( jb );

        return pump;
    }

    private void makeLakeToPumpTube() {
        Tube lakeTube = new Tube();
        lakeTube.addPoint( new Point2D.Double( pump.getBottomValve().getX(), -.2 ) );
        lakeTube.addPoint( new Point2D.Double( pump.getBottomValve().getX(), pump.getBottomValve().getY() ) );
        TubeGraphic lakeTubeGraphic = new TubeGraphic( lakeTube, transform );
        getApparatusPanel().addGraphic( lakeTubeGraphic, tubeGraphicLevel );
    }

    private void makePumpToTowerTube() {
        pumpToTowerTube = new Tube();
        setPumpToTowerTube();
        TubeGraphic pumpToTowerTubeGraphic = new TubeGraphic( pumpToTowerTube, transform );
        getApparatusPanel().addGraphic( pumpToTowerTubeGraphic, tubeGraphicLevel );


        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                getApparatusPanel().repaint();
            }
        } );
        super.setControlPanel( new PumpControlPanel( this ) );
        pump.getPiston().updateObservers();

        tower.addObserver( new SimpleObserver() {
            public void update() {
                setPumpToTowerTube();
            }
        } );
    }

    protected void createModelViewTransform() {
        Rectangle2D.Double viewport = new Rectangle2D.Double( -2, -1, 40, 20 );
        this.viewPortInit = new Rectangle2D.Double( viewport.x, viewport.y, viewport.width, viewport.height );
        backgroundColor = new Color( 120, 170, 245 );
        getApparatusPanel().setBackground( backgroundColor );
        getApparatusPanel().addGraphic( new Graphic() {
            public void paint( Graphics2D g ) {
                if( antialias ) {
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                }
                else {
                    g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
                }
            }
        }, -1 );
        transform = new ModelViewTransform2d( viewport, new Rectangle( 0, 0, 1, 1 ) );
    }

    protected RepaintManager getRm() {
        return rm;
    }

    protected WaterTower getTower() {
        return tower;
    }

    protected BernoulliModel getBernoulliModel() {
        return bernoulliModel;
    }

    protected void setTransform( ModelViewTransform2d transform ) {
        this.transform = transform;
    }

    protected void addViewedObservable( SimpleObservable so ) {
        so.addObserver( new SimpleObserver() {
            public void update() {
                getApparatusPanel().repaint();
            }
        } );
    }

    //
    //
    //
    public static void main( String[] args ) {
//        System.out.println(
// System.getProperties()
//        );
        SwingTimerClock clock = new SwingTimerClock( 40 );
//        ThreadClock clock=new ThreadClock(30,200,Thread.MIN_PRIORITY);

        defaultClock = new DefaultClock( clock, new IdentityTimeConverter() );

        FakeClock fc = new FakeClock();
        final BernoulliModule module = new BernoulliModule( defaultClock );
        final PipeModule pipeModule = new PipeModule( defaultClock );
        final FirefighterModule firefighterModule = new FirefighterModule( defaultClock );
        String title = "Bernoulli's Fountain (" + VERSION + ")";
        String desc = "The user tries to pump water in their computer.";
        String version = "0.00.01";
        final PhetApplication app = new PhetApplication( new ApplicationDescriptor( title, desc, version, new FrameSetup() {
            public void initialize( JFrame frame ) {
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                frame.setSize( dim.width - 100, dim.height - 100 );
                frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
            }
        } ), new Module[]{module,
                pipeModule,
                firefighterModule},
             fc );

        activeModule = pipeModule;
        defaultClock.addSimulationTimeListener( new SimulationTimeListener() {
            public void simulationTimeIncreased( double dt ) {
                activeModule.getModel().stepInTime( dt );
//                pipeModule.getModel().stepInTime( dt );
            }
        } );

        DefaultClockStatePanel dcsp = new DefaultClockStatePanel( defaultClock );
        final JCheckBox jcb = new JCheckBox( "Identity Time", true );
        jcb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( jcb.isSelected() ) {
                    defaultClock.setWallToSimulationTimeConverter( new IdentityTimeConverter() );
                }
                else {
                    defaultClock.setWallToSimulationTimeConverter( new WallToSimulationTimeConverter() {
                        public double toSimulationTime( long l ) {
                            return defaultClock.getRequestedDelay() / 2;
                        }
                    } );
                }
            }
        } );
        dcsp.add( jcb );
        clockControlFrame = new JFrame( "Clock controls" );
        clockControlFrame.setLocation( 400, 20 );
        clockControlFrame.setContentPane( dcsp );
        clockControlFrame.pack();
//        clockControlFrame.setVisible( true );
//        clockControlFrame.setVisible( true );

        JMenu versionMenu = new JMenu( "Version" );
        JMenuItem buildItem = new JMenuItem( "Build Number" );
        buildItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                VersionUtils.showBuildNumber( app );
            }
        } );
        versionMenu.add( buildItem );
        app.getApplicationView().getPhetFrame().getJMenuBar().add( versionMenu );

//        module.activate( app );
        app.getApplicationView().getBasicPhetPanel().setAppControlPanel( null );
        module.activate( app );
        app.startApplication( module );

        defaultClock.start();
    }
}


