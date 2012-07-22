// Copyright 2002-2012, University of Colorado

package edu.colorado.phet.conductivity;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.JSpinner;

import edu.colorado.phet.common.phetcommon.application.ApplicationConstructor;
import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationLauncher;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;
import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.common.phetcommon.model.ModelElement;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.conductivity.macro.BandSetGraphic;
import edu.colorado.phet.conductivity.macro.EnergyTextGraphic;
import edu.colorado.phet.conductivity.macro.MacroControlPanel;
import edu.colorado.phet.conductivity.macro.MacroSystem;
import edu.colorado.phet.conductivity.macro.bands.DefaultBandSet;
import edu.colorado.phet.conductivity.macro.battery.BatterySpinner;
import edu.colorado.phet.conductivity.macro.circuit.MacroCircuit;
import edu.colorado.phet.conductivity.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.conductivity.macro.particles.WireParticle;
import edu.colorado.phet.conductivity.macro.particles.WireParticleGraphic;
import edu.colorado.phet.conductivity.oldphetgraphics.ApparatusPanel;
import edu.colorado.phet.conductivity.oldphetgraphics.Graphic;
import edu.colorado.phet.conductivity.oldphetgraphics.ShapeGraphic;

// Referenced classes of package edu.colorado.phet.semiconductor.macro:
//            MacroControlPanel, MacroSystem, EnergyTextGraphic, BandSetGraphic

public class ConductivityApplication {
    // Localization

    double minVolts;
    double maxVolts;
    private MacroSystem model;
    MacroCircuit circuit;
    private MacroCircuitGraphic circuitGraphic;
    private ModelViewTransform2D transform;
    private BatterySpinner batterySpinner;
    private Hashtable bandSetGraphicTable;
    private Flashlight light;
    private boolean lightOn;
    private IClock clock;
    private double timeBetweenFires;
    private ArrayList photons;
    private MacroControlPanel macroControlPanel;
    private FlashlightGraphic flashlightGraphic;
    double timeSinceFire;
    Hashtable photonGraphicTable;

    public static final String localizedStringsPath = "conductivity/localization/conductivity-strings";
    private ApparatusPanel apparatusPanel;
    private MacroControlPanel controlPanel;
    private BaseModel baseModel;

    public ConductivityApplication( IClock clock )
            throws IOException {
//        super( ConductivityResources.getString( "ModuleTitle.SemiconductorsModule" ) );
        this.clock = clock;
        clock.addClockListener( new ClockAdapter() {
            public void simulationTimeChanged( ClockEvent clockEvent ) {
                baseModel.update( clockEvent );
            }
        } );
        minVolts = 0.0D;
        maxVolts = 2D;
//        initstroke = new BasicStroke( 1.0F, 2, 0 );
        bandSetGraphicTable = new Hashtable();
        photons = new ArrayList();
        timeSinceFire = 1.7976931348623157E+308D;
        photonGraphicTable = new Hashtable();
        transform = new ModelViewTransform2D( new java.awt.geom.Rectangle2D.Double( 0.0D, 0.0D, 1.0D, 1.0D ), new Rectangle( 0, 0, 600, 600 ) );
        setApparatusPanel( new ApparatusPanel() );
        getApparatusPanel().setBackground( new Color( 230, 220, 255 ) );
//        getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
        macroControlPanel = new MacroControlPanel( this );
        setControlPanel( macroControlPanel );
        setBaseModel( new BaseModel() );
        addViewBoundsListener();
        double particleWidth = transform.viewToModelDifferentialX( MacroCircuitGraphic.getParticleImage().getWidth() );
        model = new MacroSystem( minVolts, maxVolts, particleWidth );
        circuit = model.getCircuit();
        circuitGraphic = new MacroCircuitGraphic( circuit, transform );
        for ( int i = 0; i < circuitGraphic.numWireGraphics(); i++ ) {
            getApparatusPanel().addGraphic( circuitGraphic.wireGraphicAt( i ), 0.0D );
        }

        getApparatusPanel().addGraphic( circuitGraphic.getBatteryGraphic(), 2D );
        getApparatusPanel().addGraphic( circuitGraphic.getResistorGraphic(), 2D );
        getBaseModel().addModelElement( model );
        double d1 = 0.050000000000000003D;
        double d2 = circuit.getLength();
        int j = (int) ( d2 / d1 + 1.0D );
        double d3 = 0.0D;
        for ( int k = 0; k < j; k++ ) {
            WireParticle wireparticle = new WireParticle( d3, circuit );
            getBaseModel().addModelElement( wireparticle );
            model.particles.add( wireparticle );
            WireParticleGraphic wireparticlegraphic = new WireParticleGraphic( wireparticle, transform, MacroCircuitGraphic.getParticleImage() );
            getApparatusPanel().addGraphic( wireparticlegraphic, 3D );
            d3 += d1;
        }

        java.awt.geom.Rectangle2D.Double double1 = model.getBandSet().getBounds();
        final ShapeGraphic backgroundWhite = new ShapeGraphic( double1, Color.white );
        final ShapeGraphic backgroundBorder = new ShapeGraphic( double1, Color.blue, new BasicStroke( 2.0F, 1, 1 ) );
        final java.awt.geom.Rectangle2D.Double lowBandRect1 = double1;
        transform.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                java.awt.Shape shape = transform.getAffineTransform().createTransformedShape( lowBandRect1 );
                backgroundWhite.setShape( shape );
                backgroundBorder.setShape( shape );
            }

        } );
        getApparatusPanel().addGraphic( backgroundWhite, -1D );
        getApparatusPanel().addGraphic( backgroundBorder, 4D );
        batterySpinner = new BatterySpinner( circuit.getBattery(), this );
        getApparatusPanel().setLayout( null );
        transform.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                relayoutBatterySpinner();
            }

        } );
        getApparatusPanel().add( batterySpinner.getSpinner() );
        addLight();
        addCableGraphic();
        setConductor();
        java.awt.geom.Rectangle2D.Double double2 = model.getBandSet().getBounds();
        MutableVector2D phetvector = new MutableVector2D( double2.x, double2.y );
        EnergyTextGraphic energytextgraphic = new EnergyTextGraphic( transform, phetvector );
        getApparatusPanel().addGraphic( energytextgraphic, 1000D );
    }

    private void setBaseModel( BaseModel baseModel ) {
        this.baseModel = baseModel;
    }

    private void setControlPanel( MacroControlPanel macroControlPanel ) {
        this.controlPanel = macroControlPanel;
    }

    private void setApparatusPanel( ApparatusPanel apparatusPanel ) {
        this.apparatusPanel = apparatusPanel;
    }

    private void addLight()
            throws IOException {
        timeBetweenFires = 3D;
        light = new Flashlight( 0.80000000000000004D, 0.10000000000000001D, 0.0D );
        ImageLoader imageloader = new ImageLoader();
        BufferedImage bufferedimage = imageloader.loadImage( "conductivity/images/light.gif" );
        flashlightGraphic = new FlashlightGraphic( light, bufferedimage, transform );
        getApparatusPanel().addGraphic( flashlightGraphic, 100D );
        Vector2D phetvector = getResistorCenter();
        pointAt( phetvector );
        ModelElement modelelement = new ModelElement() {

            public void stepInTime( double d ) {
//                System.out.println( "d = " + d );
//                System.out.println( "timeSinceFire = " + timeSinceFire );
//                System.out.println( "timeBetweenFires = " + timeBetweenFires );
                if ( timeSinceFire > timeBetweenFires && lightOn ) {
                    firePhoton();
                    timeSinceFire = 0.0D;
                }
                else {
                    timeSinceFire += d;
                }
            }

        };
        getBaseModel().addModelElement( modelelement );
        getBaseModel().addModelElement( new ModelElement() {

            public void stepInTime( double d ) {
                Vector2D resistorCenter = getResistorCenter();
                for ( int i = 0; i < photons.size(); i++ ) {
                    Photon photon = (Photon) photons.get( i );
                    photon.stepInTime( d );
                    double distToCenter = photon.getPosition().minus( resistorCenter ).magnitude();
                    int DIST_FUDGE_FACTOR = 5;//introduced when switched from arrow to image.
                    if ( distToCenter <= photon.getSpeed() * d * DIST_FUDGE_FACTOR ) {
                        photons.remove( i );
                        Graphic graphic = (Graphic) photonGraphicTable.get( photon );
                        getApparatusPanel().removeGraphic( graphic );
                        model.photonHit();
                    }
                }

            }

        } );
    }

    private Vector2D getResistorCenter() {
        MutableVector2D phetvector = circuit.getResistor().getStartPosition();
        MutableVector2D phetvector1 = circuit.getResistor().getEndPosition();
        Vector2D phetvector2 = phetvector1.minus( phetvector ).times( 0.5D );
        Vector2D phetvector3 = phetvector.plus( phetvector2 );
        return phetvector3;
    }

    public void pointAt( Vector2D phetvector ) {
        MutableVector2D phetvector1 = light.getPosition();
        Vector2D phetvector2 = phetvector.minus( phetvector1 ).normalized();
        double d = phetvector2.getAngle();
        double d1 = Math.toRadians( 126D );
        double d2 = -d + d1;
        light.setAngle( d2 );
    }

    private void addCableGraphic() {
        java.awt.geom.Rectangle2D.Double double1 = model.getBandSet().getBounds();
        final GeneralPath cablePath = new GeneralPath();
        cablePath.moveTo( (float) double1.getX() + (float) double1.getWidth() / 2.0F, (float) double1.getY() );
        MacroCircuit macrocircuit = model.getCircuit();
        Vector2D phetvector = macrocircuit.getResistor().getLocation( macrocircuit.getResistor().getLength() / 2D );
        MutableVector2D phetvector1 = new MutableVector2D( double1.getX() + 0.29999999999999999D, double1.getY() - 0.20000000000000001D );
        Vector2D phetvector2 = phetvector.minus( 0.0D, 0.10000000000000001D );
        cablePath.curveTo( (float) phetvector1.getX(), (float) phetvector1.getY(), (float) phetvector2.getX(), (float) phetvector2.getY(), (float) phetvector.getX(), (float) phetvector.getY() );
        final ShapeGraphic curveShape = new ShapeGraphic( cablePath, Color.black, new BasicStroke( 2.0F ) );
        transform.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                java.awt.Shape shape = transform.getAffineTransform().createTransformedShape( cablePath );
                curveShape.setShape( shape );
            }

        } );
        getApparatusPanel().addGraphic( curveShape, 3D );
    }

    private void relayoutBatterySpinner() {
        Point point = transform.modelToView( circuit.getBattery().getEndPosition() );
        JSpinner jspinner = batterySpinner.getSpinner();
//        jspinner.setBounds( point.x, point.y + jspinner.getPreferredSize().height, jspinner.getPreferredSize().width, jspinner.getPreferredSize().height );
        int fudgeY = -5;
        jspinner.setBounds( point.x, point.y + jspinner.getPreferredSize().height + fudgeY, jspinner.getPreferredSize().width, jspinner.getPreferredSize().height );
        jspinner.repaint();
    }

    private void addViewBoundsListener() {
        getApparatusPanel().addComponentListener( new ComponentAdapter() {

            public void componentResized( ComponentEvent componentevent ) {
                setTransform();
            }

            public void componentShown( ComponentEvent componentevent ) {
                setTransform();
            }

        } );
    }

    void setTransform() {
        Rectangle rectangle = new Rectangle( 0, 0, getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
        transform.setViewBounds( rectangle );
        getApparatusPanel().repaint();
    }

    public void setBandSet( DefaultBandSet defaultbandset ) {
        DefaultBandSet defaultbandset1 = model.getBandSet();
        BandSetGraphic bandsetgraphic = (BandSetGraphic) bandSetGraphicTable.get( defaultbandset1 );
        if ( bandsetgraphic != null ) {
            getApparatusPanel().removeGraphic( bandsetgraphic );
        }
        BandSetGraphic bandsetgraphic1 = getBandSetGraphic( defaultbandset );
        defaultbandset.initParticles();
        getApparatusPanel().addGraphic( bandsetgraphic1 );
        model.setBandSet( defaultbandset );
        model.doVoltageChanged();
    }

    private BandSetGraphic getBandSetGraphic( DefaultBandSet defaultbandset ) {
        BandSetGraphic bandsetgraphic = (BandSetGraphic) bandSetGraphicTable.get( defaultbandset );
        if ( bandsetgraphic == null ) {
            bandsetgraphic = new BandSetGraphic( transform, defaultbandset.getBounds(), defaultbandset );
            defaultbandset.addBandParticleObserver( bandsetgraphic );
            bandSetGraphicTable.put( defaultbandset, bandsetgraphic );
        }
        return bandsetgraphic;
    }

    public void setConductor() {
        setBandSet( model.getConductor() );

        circuitGraphic.getResistorGraphic().setFillPaint( new Color( 210, 210, 210 ) );
        circuitGraphic.getResistorGraphic().setOutlinePaint( new Color( 210, 210, 210 ) );
//        disableFlashlight();
        enableFlashlight();
    }

    public void setInsulator() {
        setBandSet( model.getInsulator() );
        macroControlPanel.disableFlashlight();
        circuitGraphic.getResistorGraphic().setFillPaint( Color.orange );
        circuitGraphic.getResistorGraphic().setOutlinePaint( Color.orange );
//        disableFlashlight();
        enableFlashlight();
    }

    private void disableFlashlight() {
        macroControlPanel.disableFlashlight();
        flashlightGraphic.setVisible( false );
        setFlashlightOn( false );
    }

    public void setPhotoconductor() {
        setBandSet( model.getPhotoconductor() );
        macroControlPanel.enableFlashlight();
        circuitGraphic.getResistorGraphic().setFillPaint( Color.yellow );
        circuitGraphic.getResistorGraphic().setOutlinePaint( Color.yellow );
        enableFlashlight();
    }

    private void enableFlashlight() {
        macroControlPanel.enableFlashlight();
        flashlightGraphic.setVisible( true );
    }

    public void setFlashlightOn( boolean flag ) {
        lightOn = flag;
        model.getPhotoconductor().setFlashlightOn( flag );
    }

    public void firePhoton() {
        Vector2D phetvector = getResistorCenter();
        Photon photon = new Photon();
        photon.setPosition( light.getPosition() );
        double d = 0.01D;
        Vector2D phetvector1 = phetvector.minus( photon.getPosition() ).getInstanceOfMagnitude( d );
        photon.setVelocity( phetvector1 );
        PhotonArrowGraphic photonarrowgraphic = new PhotonArrowGraphic( photon, transform );
        getApparatusPanel().addGraphic( photonarrowgraphic, 3D );
        photons.add( photon );
        photonGraphicTable.put( photon, photonarrowgraphic );
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
    }

    public BaseModel getBaseModel() {
        return baseModel;
    }

    public void stopClock() {
        clock.pause();
    }

    public void startClock() {
        clock.start();
    }

    private static class ConductivityPhetApplication extends edu.colorado.phet.common.phetcommon.application.PhetApplication {
        protected ConductivityPhetApplication( PhetApplicationConfig config ) {
            super( config );
            addModule( new ConductivityModule( config ) );
        }

    }

    private static class ConductivityModule extends Module {
        public ConductivityModule( PhetApplicationConfig config ) {
            super( "name", new ConstantDtClock( 30, 1 ) );
            try {
                ConductivityApplication ca = new ConductivityApplication( getClock() );
                setSimulationPanel( ca.getApparatusPanel() );
                setControlPanel( ca.controlPanel );
                getClock().addClockListener( new ClockAdapter() {
                    public void clockTicked( ClockEvent clockEvent ) {
                        getSimulationPanel().validate(); // workaround for #562 (battery spinner is not visible on Mac)
                        getSimulationPanel().repaint();
                    }
                } );
            }
            catch ( IOException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    public static void main( final String[] args ) {
        ApplicationConstructor appConstructor = new ApplicationConstructor() {
            public PhetApplication getApplication( PhetApplicationConfig config ) {
                return new ConductivityPhetApplication( config );
            }
        };
        PhetApplicationConfig appConfig = new PhetApplicationConfig( args, "conductivity" );
        appConfig.getLookAndFeel().setBackgroundColor( new Color( 245, 245, 255 ) );
        new PhetApplicationLauncher().launchSim( appConfig, appConstructor );
    }

}
