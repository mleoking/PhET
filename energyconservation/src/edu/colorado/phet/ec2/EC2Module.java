package edu.colorado.phet.ec2;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.model.*;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.InteractiveGraphic;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.coreadditions.clock2.DefaultClock;
import edu.colorado.phet.coreadditions.clock2.SimulationTimeListener;
import edu.colorado.phet.coreadditions.clock2.AbstractClock;
import edu.colorado.phet.coreadditions.clock2.TickListener;
import edu.colorado.phet.coreadditions.clock2.components.DefaultClockStatePanel;
import edu.colorado.phet.coreadditions.graphics.transform.ModelViewTransform2d;
import edu.colorado.phet.coreadditions.graphics.transform.TransformListener;
import edu.colorado.phet.coreadditions.plaf.PlafUtil;
import edu.colorado.phet.coreadditions.plaf.PurpleLookAndFeel;
import edu.colorado.phet.coreadditions.util.VersionUtils;
import edu.colorado.phet.ec2.common.GraphicControllerAdapter;
import edu.colorado.phet.ec2.common.RectangleImageGraphic;
import edu.colorado.phet.ec2.common.StringGraphic;
import edu.colorado.phet.ec2.common.audio.AudioProxy;
import edu.colorado.phet.ec2.common.buffering.BufferedGraphic3;
import edu.colorado.phet.ec2.common.measuringtape.MeasuringTape;
import edu.colorado.phet.ec2.common.measuringtape.MeasuringTapeInteractiveGraphic;
import edu.colorado.phet.ec2.common.rates.FrameRate;
import edu.colorado.phet.ec2.common.rates.FrameRateGraphic;
import edu.colorado.phet.ec2.common.view.creation.CreationEvent;
import edu.colorado.phet.ec2.elements.car.*;
import edu.colorado.phet.ec2.elements.history.EnergyDotGraphic;
import edu.colorado.phet.ec2.elements.history.History;
import edu.colorado.phet.ec2.elements.jchart.EnergyChartGraphic;
import edu.colorado.phet.ec2.elements.scene.Backdrop;
import edu.colorado.phet.ec2.elements.scene.BoundGraphic;
import edu.colorado.phet.ec2.elements.scene.Scene;
import edu.colorado.phet.ec2.elements.spline.DragSplineToCreate;
import edu.colorado.phet.ec2.elements.spline.ModuleSplineInterface;
import edu.colorado.phet.ec2.elements.spline.Spline;
import edu.colorado.phet.ec2.elements.spline.SplineGraphic;
import edu.colorado.phet.ec2.games.BullsEyeGraphic;
import edu.colorado.phet.ec2.games.HalfPipeGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jul 6, 2003
 * Time: 7:01:49 PM
 * To change this template use Options | File Templates.
 */
public class EC2Module extends Module implements ModuleSplineInterface {
    static final Font lucidaFont = new Font( "Lucida Sans", Font.BOLD, 18 );
    Font LUCIDA = new Font( "Lucida Sans", Font.BOLD, 25 );
    static final Color purple = new Color( 190, 175, 245 );
    public static Color LIGHT_PURPLE = new Color( 150, 150, 245 );

    Car car;
    ModelViewTransform2d transform;
    private CarGraphic carGraphic;
    private ImageLoader imageLoader;
    private BufferedImage ideaImage;
    private EnergyChartGraphic energyChart;

    private int SPLINE_LAYER = 9;
    private int ENERGY_DOT_BOTTOM_LAYER = 13;
    private int CAR_LAYER = 14;
    private int CHART_LAYER = 12;
    private int ENERGY_DOT_TOP_LAYER = 15;

    private ArrayList splineGraphics = new ArrayList( 1 );
    private Spline splineInit;
    private ECControlPanel myControlPanel;
    private boolean energyChartVisible = false;

    private SplineGraphic initGraphic;
    private StartupGraphic startupGraphic;
    private Scene currentScene = null;
    private Backdrop backdrop;
    private ArrayList scenes = new ArrayList();

    private static long messageEndTime;
    private static StringGraphic messageGraphic;

    private boolean inited = false;

    boolean createTestSpline = false;
    private boolean createLoop = false;
    private FrameRate frameRate;
    private FrameRateGraphic frameRateGraphic;
    private BufferedGraphic3 buffer;
    private ArrayList dotControls = new ArrayList();
    private BufferedImage bullseye;
    private BoundsSetup boundsSetup;
    private static DefaultClock modelClock;
    private DefaultClockStatePanel clockStatePanel;
    private JFrame clockStateFrame;
    private History history;
    private boolean addTestSpline = true;
    private long lastPlayTime;
    private Random random = new Random();
    public static boolean audioEnabled = true;
    private GraphicControllerAdapter splineCreationControl;
    private DragSplineToCreate splineCreator;
    private BufferedImage appleImage;
    private BufferedImage branchImage;
    private RectangleImageGraphic appleRectoGraphic;
    private JButton resetGameButton;
    private Rectangle2D.Double appleModelRect;
    public static boolean WENDY_MODE = false;
//    public static boolean WENDY_MODE = true;
    private double carAngleOnLaunch;
    private StateTransitionListener useFloatAngle;
    private StateTransitionListener useDefaultAngle;

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public EC2Module() {
        super( "Energy Conservation Module V2" );

        ApparatusPanel mypanel = new ApparatusPanel();
        buffer = new BufferedGraphic3( mypanel, new GradientPaint( 0, 0, Color.red, 200, 350, new Color( 200, 200, 240 ) ) );
        super.setApparatusPanel( mypanel );
        BaseModel model = new BaseModel() {
            public void clockTicked( IClock c, double dt ) {
                double seconds = .05;
                if( inited ) {
                    for( int i = 0; i < numModelElements(); i++ ) {
                        modelElementAt( i ).stepInTime( seconds );
                    }
                }
                updateObservers();
                inited = true;
            }
        };

        model.addModelElement( new ModelElement() {
            public void stepInTime( double v ) {
                if( messageGraphic.isVisible() && messageEndTime < System.currentTimeMillis() ) {
                    messageGraphic.setText( "" );
                }
            }
        } );

        super.setModel( model );
        history = new History( 0, this );
        getModel().addModelElement( history );

//        double carLength = 3.5;//Meters
//        double carLength=73;
        double carLength = 4.5;
        double carHeight = carLength / 2;

//        double modelWidth = carLength * 14;
//        double modelHeight = carLength * 14;
        double modelWidth = 49;
        double modelHeight = 49;
//        double modelWidth=75;
//        double modelHeight=75;
        Rectangle2D.Double modelBounds = new Rectangle2D.Double( 0, 0, modelWidth, modelHeight );//Let'spline put these in meters.
        Rectangle viewBounds = new Rectangle( 0, 0, 400, 400 );
        transform = new ModelViewTransform2d( modelBounds, viewBounds );

        imageLoader = new ImageLoader();
//        BufferedImage sk8r = imageLoader.loadBufferedImage("images/skate100-2.gif");
        BufferedImage sk8r = imageLoader.loadBufferedImage( "images/skate-300.gif" );
//        BufferedImage sk8r = imageLoader.loadBufferedImage("images/skate-100-cutout.gif");
//        BufferedImage ferarriImage = imageLoader.loadBufferedImage("images/ferrari-side2.gif");
        BufferedImage ferarriImage = sk8r;
        BufferedImage flatironsImage = imageLoader.loadBufferedImage( "images/Earth.jpg" );
        Scene earth = new Scene( flatironsImage, ferarriImage, -9.8, "Earth", getTransform(), this );
        final Scene moon = new Scene( imageLoader.loadBufferedImage( "images/Moon.jpg" ),
                                      imageLoader.loadBufferedImage( "images/Lunar-Rover.gif" ), -1.7, "The Moon", getTransform(), this );
        final Scene mars = new Scene( imageLoader.loadBufferedImage( "images/Mars.jpg" ),
                                      imageLoader.loadBufferedImage( "images/Mars-Lander.gif" ), -3.7, "Mars", getTransform(), this );
        Scene trumania = new Scene( imageLoader.loadBufferedImage( "images/1934_11.jpg" ),
                                    imageLoader.loadBufferedImage( "images/motorcycle.gif" ), earth.getGravity() * 10, "Planet PhET", getTransform(), this );
        Scene space = new Scene( imageLoader.loadBufferedImage( "images/milkymoon_casado_big.jpg" ),
                                 imageLoader.loadBufferedImage( "images/Camry150.gif" ), 0, "Outer Space", getTransform(), this );

        scenes.add( earth );
        scenes.add( moon );
        scenes.add( mars );
        scenes.add( trumania );
        scenes.add( space );

        myControlPanel = new ECControlPanel( this );
        setControlPanel( myControlPanel );

        this.currentScene = earth;

        boundsSetup = new BoundsSetup( this );
        car = new Car( this, modelWidth / 2, modelHeight / 2, carLength, carHeight, currentScene.getGravity(), boundsSetup.getFloorHeight(), boundsSetup );
        carGraphic = new CarGraphic( this );
        add( car, carGraphic, CAR_LAYER );

        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentShown( ComponentEvent e ) {
                relayout();
            }

            public void componentResized( ComponentEvent e ) {
                relayout();
            }
        } );

        getApparatusPanel().addGraphic( buffer, -10 );

        backdrop = new Backdrop( this, flatironsImage );
        buffer.addGraphic( backdrop, -1 );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                buffer.updateBuffer();
            }
        } );

        frameRate = new FrameRate( 10 );
        frameRateGraphic = new FrameRateGraphic( frameRate, 100, 100 );

//        DTObserver dto = new DTObserver(10);
//        DTGraphic dtg = new DTGraphic(dto, 100, 80);
//        dtg.setVisible(false);
//        add(dto, dtg, 3);

        energyChart = new EnergyChartGraphic( car, new Rectangle2D.Double() );
        energyChart.setVisible( energyChartVisible );
        getApparatusPanel().addGraphic( energyChart, CHART_LAYER );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                energyChart.viewChanged( getTransform() );
            }
        } );

        if( createTestSpline ) {
            Spline spline = new Spline();
            SplineGraphic sg = new SplineGraphic( getTransform(), this, spline );
            spline.addControlPoint( modelWidth / 2, modelHeight * .2 );
            spline.addControlPoint( modelWidth * .6, modelHeight * .4 );
            spline.addControlPoint( modelWidth * .8, modelHeight * .6 );
            addSpline( spline, sg );
        }
        if( createLoop ) {
            createLoop();
        }
        if( addTestSpline ) {
            addHalfPipe();
        }

        splineInit = new Spline();
        splineInit.addControlPoint( modelWidth * .085, modelHeight * .85 );
        splineInit.addControlPoint( modelWidth * .159, modelHeight * .85 );
        splineInit.addControlPoint( modelWidth * .233, modelHeight * .85 );
        initGraphic = new SplineGraphic( getTransform(), this, splineInit );

        final Font font = new Font( "dialog", 0, 20 );
        Graphic boundary = new Graphic() {
            public void paint( Graphics2D g ) {
                int x = initGraphic.vertexGraphicAt( 0 ).getX();
                int y = initGraphic.vertexGraphicAt( 0 ).getY();
                g.setColor( Color.blue );
                g.setFont( font );
                g.drawString( "Drag to create track.", x, y - font.getSize() );
            }
        };
        splineCreator = new DragSplineToCreate( initGraphic, new CreationEvent() {
            /*Creates and adds to the model and view, and returns the interactive graphic for event forwarding purposes.*/
            public InteractiveGraphic createElement() {
                Spline copy = splineInit.copySpline();
                SplineGraphic graphic = new SplineGraphic( getTransform(), EC2Module.this, copy );
                addSpline( copy, graphic );
                return graphic;
            }
        }, boundary );

        splineCreationControl = new GraphicControllerAdapter( splineCreator );
        addSplineCreator();


        getApparatusPanel().addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                startupGraphic.setVisible( false );
                getApparatusPanel().removeGraphic( startupGraphic );
            }
        } );

        messageGraphic = new StringGraphic( "", Color.black, LIGHT_PURPLE, LUCIDA, 50, 50 );
        getApparatusPanel().addGraphic( messageGraphic, 100 );
        getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                Rectangle viewBounds = transform.getViewBounds();
                messageGraphic.setLocation( viewBounds.x + 40, viewBounds.y + viewBounds.height - 15 );
            }
        } );

//        URL u = getClass().getClassLoader().getResource("audio/whee.wav");
//        System.out.println("u = " + u);
        final ArrayList proxies = new ArrayList();
        proxies.add( new AudioProxy( getClass().getClassLoader().getResource( "audio/whee.wav" ) ) );
        proxies.add( new AudioProxy( getClass().getClassLoader().getResource( "audio/awesome.wav" ) ) );
        proxies.add( new AudioProxy( getClass().getClassLoader().getResource( "audio/imawesome.wav" ) ) );
        proxies.add( new AudioProxy( getClass().getClassLoader().getResource( "audio/ohyes.wav" ) ) );
        proxies.add( new AudioProxy( getClass().getClassLoader().getResource( "audio/somekindabird.wav" ) ) );
//        final AudioClip ac = Applet.newAudioClip(u);
//        ac.play();
        lastPlayTime = System.currentTimeMillis();
        car.addObserver( new Observer() {
            public void update( Observable o, Object arg ) {
                if( System.currentTimeMillis() - lastPlayTime > 2000 ) {
                    if( car.isFalling() && audioEnabled ) {
                        double height = car.getHeightAboveGround();
                        double vy = car.getVelocityVector().getY();
                        if( vy > 2 && height > 30 ) {
                            lastPlayTime = System.currentTimeMillis();
                            int playIndex = random.nextInt( proxies.size() );
                            AudioProxy ap = (AudioProxy)proxies.get( playIndex );
                            ap.play();
                        }
                    }
                }
//                boolean flying = car.isFalling();
            }
        } );
//        crash.play();
        startupGraphic = new StartupGraphic( this );
        getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
                startupGraphic.transformChanged( modelViewTransform2d );
            }
        } );
        getApparatusPanel().addGraphic( startupGraphic, 30 );

        getTransform().addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d modelViewTransform2d ) {
//                buffer.rebuildBuffer();
                buffer.updateBuffer();
            }
        } );

        double tapeLength = 10;
        MeasuringTape tape = new MeasuringTape( modelWidth / 3, modelHeight / 2, modelWidth / 3 + tapeLength, modelHeight / 2 );
        Color tapeColor = new Color( 240, 242, 38 );
        MeasuringTapeInteractiveGraphic tapeGraphic = new MeasuringTapeInteractiveGraphic( new BasicStroke( 5 ), tapeColor, tape, getTransform(), getApparatusPanel() );
        getApparatusPanel().addGraphic( tapeGraphic, 100 );

        changeScene( earth );
//        setGameMode();
        setOpenMode();
    }

    private void addSplineCreator() {
        buffer.addGraphic( splineCreator, 4 );
        getApparatusPanel().addGraphic( splineCreationControl, 4 );
    }

    private void removeSplineCreator() {
        buffer.removeGraphic( splineCreator );
        getApparatusPanel().removeGraphic( splineCreationControl );
    }

    public void setGameMode() {
        clearSplines();
        resetCar();
        addHalfPipe();
        removeSplineCreator();

        if( appleImage == null ) {
            appleImage = imageLoader.loadBufferedImage( "images/games/apple-60.gif" );
            branchImage = imageLoader.loadBufferedImage( "images/games/branch.gif" );
            resetGameButton = new JButton( "Reset Apple" );
            resetGameButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    appleRectoGraphic.setVisible( true );
                    setMessage( "Try to grab the apple!" );
                    getApparatusPanel().remove( resetGameButton );
                }
            } );
            appleRectoGraphic = new RectangleImageGraphic( appleImage );
            appleModelRect = new Rectangle2D.Double( transform.getModelBounds().getWidth() - 14,
                                                     transform.getModelBounds().getHeight() * .82, 2, 2 );
            transform.addTransformListener( new TransformListener() {
                public void transformChanged( ModelViewTransform2d mvt ) {
                    appleRectoGraphic.setOutputRect( mvt.modelToView( appleModelRect ) );
                }
            } );

            useFloatAngle = new StateTransitionListener() {
                public void transitionChanged( CarMode targetState ) {
//                -velocityVector.getAngle() + Math.PI;
                    carAngleOnLaunch = -car.getVelocityVector().getAngle() + Math.PI;
                    carGraphic.setAngleChooser( new AngleChooser() {
                        public double getAngle( Car c ) {
                            return carAngleOnLaunch;
                        }
                    } );
                }
            };
            useDefaultAngle = new StateTransitionListener() {
                public void transitionChanged( CarMode targetState ) {
                    carGraphic.setDefaultAngleChooser();
                }
            };

        }
        car.getLaunchTransition().addStateTransitionListener( useFloatAngle );
        car.getLandTransition().addStateTransitionListener( useDefaultAngle );

        getApparatusPanel().addGraphic( appleRectoGraphic, 11 );

        double distToBranchY = 3;
        final RectangleImageGraphic branchrig = new RectangleImageGraphic( branchImage );
        final Rectangle2D.Double branchRect = new Rectangle2D.Double( appleModelRect.getX() - 3, appleModelRect.getY() + distToBranchY, 15, 2.8 );
        final Rectangle2D.Double branchCollisionRect = new Rectangle2D.Double( branchRect.x, branchRect.y + branchRect.height / 2, branchRect.width, branchRect.height * 3 );
        getApparatusPanel().addGraphic( branchrig, 11 );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                branchrig.setOutputRect( mvt.modelToView( branchRect ) );
            }
        } );

        final Point2D.Double appleCenter = new Point2D.Double( appleModelRect.x + appleModelRect.width / 2,
                                                               appleModelRect.y + appleModelRect.height / 2 );
        final Point2D.Double stemOut = new Point2D.Double( appleCenter.x, appleCenter.y + distToBranchY );
        final StemGraphic stem = new StemGraphic();
        getApparatusPanel().addGraphic( stem, 10 );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2d mvt ) {
                Point appleStart = mvt.modelToView( appleCenter );
                Point stemEnd = mvt.modelToView( stemOut );
                stem.setState( appleStart.x, appleStart.y, stemEnd.x, stemEnd.y );
            }
        } );

        getModel().addModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( car.isFalling() && car.getRectangle().intersects( appleModelRect ) && appleRectoGraphic.isVisible() ) {
                    setMessage( "You got the apple!" );
                    appleRectoGraphic.setVisible( false );
                    getApparatusPanel().add( resetGameButton );
                    Point pt = transform.modelToView( appleCenter.x, appleCenter.y );
                    resetGameButton.setBounds( pt.x + 50, pt.y, 140, 30 );

                }
                else if( car.isFalling() && car.getRectangle().intersects( branchCollisionRect ) ) {
                    setMessage( "You got the apple, but you hit your head on a branch.  Ouch." );
                    double energy = car.getMechanicalEnergy();
                    car.setVelocityY( -2 );
                    car.setVelocityX( 0 );
                    double energyFinal = car.getMechanicalEnergy();
                    double de = energyFinal - energy;
                    car.addFriction( de );
                    car.updateObservers();
                }
            }
        } );


        buffer.updateBuffer();
        transform.setModelBounds( transform.getModelBounds() );
        getApparatusPanel().repaint();
    }

    public void setOpenMode() {
        car.getLaunchTransition().removeStateTransitionListener( useFloatAngle );
        resetCar();
        clearSplines();
        addSplineCreator();
        buffer.updateBuffer();
    }

    private void addHalfPipe() {
        Spline spline = new Spline();
        HalfPipeGraphic sg = new HalfPipeGraphic( getTransform(), this, spline, 7 );

        spline.addControlPoint( 4.353642925890282, 32.17486187845305 );
        spline.addControlPoint( 6.602502406159773, 32.053038674033154 );
        spline.addControlPoint( 8.111645813282003, 31.24088397790057 );
        spline.addControlPoint( 8.677574590952839, 28.04640883977901 );
        spline.addControlPoint( 8.866217516843115, 24.96022099447518 );
        spline.addControlPoint( 8.819056785370549, 21.440883977900558 );
        spline.addControlPoint( 8.77189605389798, 17.217679558011053 );
        spline.addControlPoint( 10.23387872954768, 7.82375690607739 );
        spline.addControlPoint( 16.789220404234893, 3.465193370165739 );
        spline.addControlPoint( 22.872954764196354, 2.544751381215469 );
        spline.addControlPoint( 30.512993262752676, 3.627624309392259 );
        spline.addControlPoint( 36.73820981713186, 9.745856353591156 );
        spline.addControlPoint( 38.01565255052942, 20.10082872928183 );

        addSpline( spline, sg );
    }

    public ModelViewTransform2d getTransform() {
        return transform;
    }

    public void changeScene( Scene scene ) {
        scene.setup();
    }

    public void setChartRange() {
        double maxEnergy = Math.abs( currentScene.getGravity() * transform.getModelBounds().getHeight() * car.getMass() );
        maxEnergy = Math.max( maxEnergy, 500 * car.getMass() );
        if( !energyChart.isAutoRange() ) {
            energyChart.setRange( 0, maxEnergy );
        }
    }

    public void showFrameRate() {
        add( frameRate, frameRateGraphic, 10 );
    }

    public void createLoop() {
        Spline spline = new Spline();
        SplineGraphic sg = new SplineGraphic( getTransform(), this, spline );
        double modelWidth = transform.getModelBounds().getWidth();
        double modelHeight = transform.getModelBounds().getWidth();
        spline.addControlPoint( modelWidth * .2, modelHeight * .5 );
        spline.addControlPoint( modelWidth * .4, modelHeight * .5 );
        spline.addControlPoint( modelWidth * .6, modelHeight * .6 );
        spline.addControlPoint( modelWidth * .5, modelHeight * .7 );
        spline.addControlPoint( modelWidth * .4, modelHeight * .6 );
        spline.addControlPoint( modelWidth * .6, modelHeight * .5 );
        spline.addControlPoint( modelWidth * .8, modelHeight * .5 );
        addSpline( spline, sg );
    }

    public void showStartupGraphic() {
        startupGraphic.setVisible( true );
        getApparatusPanel().removeGraphic( startupGraphic );//make sure no instances.
        getApparatusPanel().addGraphic( startupGraphic, 30 );
    }

    public void add( ModelElement element, Graphic graphic, int i ) {
        getModel().addModelElement( element );
        getApparatusPanel().addGraphic( graphic, i );
    }

    public void relayout() {
        JPanel app = getApparatusPanel();
        Rectangle viewBounds = new Rectangle( 0, 0, app.getWidth(), app.getHeight() );
//        buffer.rebuildBuffer(getApparatusPanel());
        buffer.setSize( getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
        buffer.updateBuffer();
        transform.setViewBounds( viewBounds );
        getApparatusPanel().repaint();
    }

    public void activateInternal( final PhetApplication app ) {
        app.getApplicationView().getBasicPhetPanel().setAppControlPanel( new JPanel() );
//        JMenu gamesMenu = new JMenu("Games");
        JMenuItem constantHeightGame = new JMenuItem( "Velocitracker" );
        constantHeightGame.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                setVelocitrackerMode();
            }
        } );
//        gamesMenu.add(constantHeightGame);
//        app.getApplicationView().getPhetFrame().addMenu(gamesMenu);
        app.getApplicationView().getPhetFrame().getJMenuBar().repaint();

        JMenu view = new JMenu( "View" );
        JMenuItem[] items = PlafUtil.getLookAndFeelItems();
        for( int i = 0; i < items.length; i++ ) {
            JMenuItem item = items[i];
            view.add( item );
        }
        app.getApplicationView().getPhetFrame().addMenu( view );

        JMenuItem jmi = new JMenuItem( "Version Info" );
        jmi.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                VersionUtils.showBuildNumber( app );
            }
        } );

        JMenuItem showClock = new JMenuItem( "Clock Controls v2" );
        showClock.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if( clockStatePanel == null ) {
                    clockStatePanel = new DefaultClockStatePanel( modelClock );
                    clockStateFrame = new JFrame( "Clock Controls v2" );
                    clockStateFrame.setContentPane( clockStatePanel );
                    clockStateFrame.pack();
                    clockStateFrame.setLocation( 400, 400 );

                }
                clockStateFrame.setVisible( true );
            }
        } );
//        view.add(showClock);


        JMenuBar jmb = app.getApplicationView().getPhetFrame().getJMenuBar();
        for( int i = 0; i < jmb.getComponentCount(); i++ ) {
            Component cx = jmb.getComponent( i );
            if( cx != null && cx instanceof JMenu ) {
                JMenu jm = (JMenu)cx;
                if( jm.getText().equalsIgnoreCase( "help" ) ) {
                    jm.add( jmi );
                }
                if( jm.getText().equalsIgnoreCase( "controls" ) ) {
                    jm.add( showClock );
                }
            }

        }
    }

    private void setVelocitrackerMode() {
        if( bullseye == null ) {
            bullseye = new ImageLoader().loadBufferedImage( "images/bullseye.gif" );
            double floorHeight = boundsSetup.getFloorHeight();
            double modelWidth = transform.getModelBounds().getWidth();
            BullsEyeGraphic beg = new BullsEyeGraphic( bullseye, modelWidth * .75, floorHeight, modelWidth / 16, getTransform() );
            getApparatusPanel().addGraphic( beg, 1000 );
            getApparatusPanel().repaint();
        }
    }

    public void deactivateInternal( PhetApplication app ) {
    }

    public void activate( PhetApplication phetApplication ) {
    }

    public void deactivate( PhetApplication phetApplication ) {
    }

    static PhetApplication pa;
    static boolean init = false;
//    private static BufferStrategy bufferStrategy;

    public static void main( String[] args ) throws UnsupportedLookAndFeelException {
//        System.setProperty("sun.java2d.ddoffscreen", "false");
        final EC2Module module = new EC2Module();
        torepaint = module.getApparatusPanel();
        final BufferedImage image = new ImageLoader().loadBufferedImage( "images/windowicons/butrfly2.jpg" );
//        final BufferedImage image = new ImageLoader().loadBufferedImage("images/windowicons/sparrowLarge.gif");
//        final BufferedImage image = new ImageLoader().loadBufferedImage("images/windowicons/gyroscope.gif");
//        final BufferedImage image = new ImageLoader().loadBufferedImage("images/Camry150.gif");
        FrameSetup fullScreen = new MaximizeFrame2() {
            public void initialize( JFrame jFrame ) {
                super.initialize( jFrame );
                jFrame.setIconImage( image );
            }
        };
        ApplicationDescriptor desc = new ApplicationDescriptor( "PhET - Energy Conservation Kit",
                                                                "An energy conservation program.", ".0.0025", fullScreen );
        IClock c = new DynamicClock( 1, 1000, ThreadPriority.MIN ) {
            public void tickOnce( double dt ) {
            }

            public void start() {
                //no starting this old version of the clock, please.
            }
        };
        modelClock = new DefaultClock( 40 );
        modelClock.addSimulationTimeListener( new SimulationTimeListener() {
            public void simulationTimeIncreased( double v ) {
                module.getModel().clockTicked( null, v );
            }
        } );
        //TODO this requests a full repaint every clock tick.
        modelClock.addTickListener( new TickListener() {
            public void clockTicked( AbstractClock abstractClock ) {
                module.getApparatusPanel().repaint();
            }
        } );
        pa = new PhetApplication( desc, module, c ) {

            public void startApplication( Module initialModule ) {
                modelClock.start();
                getModuleManager().setActiveModule( initialModule );
                getApplicationView().setVisible( true );
            }
        };
        UIManager.setLookAndFeel( new PurpleLookAndFeel() );
        updateFrames();
        pa.startApplication( module );
        updateFrames();
    }

    public static void updateFrames() {
        Frame[] frames = JFrame.getFrames();
        ArrayList alreadyChecked = new ArrayList();
        for( int i = 0; i < frames.length; i++ ) {
            SwingUtilities.updateComponentTreeUI( frames[i] );
            if( !alreadyChecked.contains( frames[i] ) ) {
                Window[] owned = frames[i].getOwnedWindows();
                for( int j = 0; j < owned.length; j++ ) {
                    Window window = owned[j];
                    SwingUtilities.updateComponentTreeUI( window );
                }
            }
            alreadyChecked.add( frames[i] );
        }
    }

    public Car getCar() {
        return car;
    }

    public BufferedImage getIdeaImage() {
        if( ideaImage == null ) {
            ideaImage = getImageLoader().loadBufferedImage( "images/icons/About24.gif" );
        }
        return ideaImage;
    }

    public Spline[] getSplines() {
        Spline[] splines = new Spline[splineGraphics.size()];
        for( int i = 0; i < splines.length; i++ ) {
            splines[i] = ( (SplineGraphic)splineGraphics.get( i ) ).getSpline();
        }
        return splines;
    }

    public void removeSpline( SplineGraphic sg ) {
        remove( sg.getSpline(), sg );
        getTransform().removeTransformListener( sg );
        splineGraphics.remove( sg );
        if( car.isSplineMode() && car.getSplineMode().getSpline() == sg.getSpline() ) {
            car.setFalling();
        }
    }

    public void addSpline( Spline spline ) {
        SplineGraphic graphic = new SplineGraphic( getTransform(), this, spline );
        addSpline( spline, graphic );
    }

    private void addSpline( Spline spline, final SplineGraphic sg ) {
        splineGraphics.add( sg );
        add( spline, sg, SPLINE_LAYER );
        transform.addTransformListener( sg );
        if( transform != null ) {
            sg.transformChanged( transform );
        }
    }

    public void remove( ModelElement me, Graphic g ) {
        getModel().removeModelElement( me );
        getApparatusPanel().removeGraphic( g );
    }

    public void setAutoScale( boolean selected ) {
        energyChart.setAutoScale( selected );
    }

    public void clearHistory() {
        history.clear();
    }

    public void setHistoryActive( boolean historyActive ) {
        history.setActive( historyActive );
    }

    public void setEnergyChartVisible( boolean visible ) {
        energyChart.setVisible( visible );
    }

    public boolean isChartVisible() {
        if( energyChart != null ) {
            return energyChart.isVisible();
        }
        else {
            return energyChartVisible;
        }
    }

    public void reset() {
        resetCar();
        clearHistory();
        clearSplines();
        buffer.updateBuffer();
    }

    private void clearSplines() {
        for( int i = 0; i < splineGraphics.size(); i++ ) {
            SplineGraphic sg = (SplineGraphic)splineGraphics.get( i );
            removeSpline( sg );
            i = -1;
        }
    }

    private void resetCar() {
        car.setFriction( 0 );
        car.setFalling();

        car.setPosition( transform.getModelBounds().getWidth() / 2, transform.getModelBounds().getHeight() / 2 );
        car.setVelocity( 0, 0 );
    }

    public void setEnergyDotDT( double energyDotDT ) {
        history.setEnergyDotDT( energyDotDT );
    }

    public CarGraphic getCarGraphic() {
        return carGraphic;
    }

    public SplineGraphic getSplineInitGraphic() {
        return initGraphic;
    }

    public BufferedImage getCarImage() {
        return currentScene.getCarImage();
    }

    public Scene[] getScenes() {
        return (Scene[])scenes.toArray( new Scene[0] );
    }

    public Scene getScene() {
        return currentScene;
    }

    public void setShowBackground( boolean selected ) {
        backdrop.setVisible( selected );
        buffer.updateBuffer();
    }

    public static void setMessage( String s ) {
        setMessage( s, 1000000 );// a long time.
    }

    public static void setMessage( String s, long time ) {
        messageGraphic.setText( s );
        messageEndTime = System.currentTimeMillis() + time;
    }

    public void addBoundGraphic( BoundGraphic bg ) {
        buffer.addGraphic( bg, 1 );
    }

    public int numHistoryDots() {
        return history.numHistoryDots();
    }

    public void addDotGraphic( EnergyDotGraphic dotGraphic ) {
        getApparatusPanel().addGraphic( dotGraphic, ENERGY_DOT_TOP_LAYER );
    }

    public void addToBuffer( EnergyDotGraphic last ) {
        buffer.addGraphic( last, ENERGY_DOT_BOTTOM_LAYER );
    }

    public void addDotControl( InteractiveGraphic dotControl ) {
        dotControls.add( dotControl );
    }

    public BufferedGraphic3 getBuffer() {
        return buffer;
    }

    public ECControlPanel getEC2ControlPanel() {
        return myControlPanel;
    }

    public boolean isHistoryActive() {
        return history.isActive();
    }

    public double getModelWidth() {
        return transform.getModelBounds().getWidth();
    }

    public double getModelHeight() {
        return transform.getModelBounds().getHeight();
    }

    public void setBackground( BufferedImage background, ModelViewTransform2d transform ) {
        backdrop.setBufferedImage( background, transform );
    }

    public void setCurrentScene( Scene scene ) {
        this.currentScene = scene;
    }

    public void updateBuffer() {
        buffer.updateBuffer();
    }

    public Scene getCurrentScene() {
        return currentScene;
    }

    public int numSplines() {
        return this.getSplines().length;
    }

    public Spline splineAt( int i ) {
        return getSplines()[i];
    }

    static JComponent torepaint = null;

    public static void repaint( Rectangle pre, Rectangle post ) {
        if( torepaint != null ) {
            torepaint.repaint( pre );
            torepaint.repaint( post );
        }
    }
}
