// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.apparatuspanelcontainment.SingleApparatusPanelContainer;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.AspectRatioLayout;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.semiconductor.Flashlight;
import edu.colorado.phet.semiconductor.FlashlightGraphic;
import edu.colorado.phet.semiconductor.macro.bands.DefaultBandSet;
import edu.colorado.phet.semiconductor.macro.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuit;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.particles.WireParticle;
import edu.colorado.phet.semiconductor.macro.particles.WireParticleGraphic;
import edu.colorado.phet.semiconductor.PhotonArrowGraphic;
import edu.colorado.phet.semiconductor.Photon;
import edu.colorado.phet.semiconductor.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

// Referenced classes of package edu.colorado.phet.semiconductor.macro:
//            MacroControlPanel, MacroSystem, EnergyTextGraphic, BandSetGraphic

public class MacroModule extends Module {

    public MacroModule( AbstractClock abstractclock )
            throws IOException {
        super( "Semiconductors!" );
        minVolts = 0.0D;
        maxVolts = 2D;
//        initstroke = new BasicStroke( 1.0F, 2, 0 );
        bandSetGraphicTable = new Hashtable();
        photons = new ArrayList();
        timeSinceFire = 1.7976931348623157E+308D;
        photonGraphicTable = new Hashtable();
        clock = abstractclock;
        transform = new ModelViewTransform2D( new java.awt.geom.Rectangle2D.Double( 0.0D, 0.0D, 1.0D, 1.0D ), new Rectangle( 0, 0, 600, 600 ) );
        setApparatusPanel( new ApparatusPanel() );
        getApparatusPanel().setBackground( new Color( 230, 220, 255 ) );
        getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
        macroControlPanel = new MacroControlPanel( this );
        setControlPanel( macroControlPanel );
        setModel( new BaseModel( abstractclock ) );
        addViewBoundsListener();
        double d = transform.viewToModelDifferentialX( MacroCircuitGraphic.getParticleImage().getWidth() );
        model = new MacroSystem( minVolts, maxVolts, d );
        circuit = model.getCircuit();
        circuitGraphic = new MacroCircuitGraphic( circuit, transform );
        for( int i = 0; i < circuitGraphic.numWireGraphics(); i++ ) {
            getApparatusPanel().addGraphic( circuitGraphic.wireGraphicAt( i ), 0.0D );
        }

        getApparatusPanel().addGraphic( circuitGraphic.getBatteryGraphic(), 2D );
        getApparatusPanel().addGraphic( circuitGraphic.getResistorGraphic(), 2D );
        getModel().addModelElement( model );
        double d1 = 0.050000000000000003D;
        double d2 = circuit.getLength();
        int j = (int)( d2 / d1 + 1.0D );
        double d3 = 0.0D;
        for( int k = 0; k < j; k++ ) {
            WireParticle wireparticle = new WireParticle( d3, circuit );
            getModel().addModelElement( wireparticle );
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
                java.awt.Shape shape = transform.toAffineTransform().createTransformedShape( lowBandRect1 );
                backgroundWhite.setShape( shape );
                backgroundBorder.setShape( shape );
            }

        } );
        getApparatusPanel().addGraphic( backgroundWhite, -1D );
        getApparatusPanel().addGraphic( backgroundBorder, 4D );
        batterySpinner = new BatterySpinner( circuit.getBattery() );
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
        PhetVector phetvector = new PhetVector( double2.x, double2.y );
        EnergyTextGraphic energytextgraphic = new EnergyTextGraphic( transform, phetvector );
        getApparatusPanel().addGraphic( energytextgraphic, 1000D );
    }

    private void addLight()
            throws IOException {
        timeBetweenFires = clock.getDt() * 3D;
        light = new Flashlight( 0.80000000000000004D, 0.10000000000000001D, 0.0D );
        ImageLoader imageloader = new ImageLoader();
        BufferedImage bufferedimage = imageloader.loadImage( "images/light.gif" );
        flashlightGraphic = new FlashlightGraphic( light, bufferedimage, transform );
        getApparatusPanel().addGraphic( flashlightGraphic, 100D );
        PhetVector phetvector = getResistorCenter();
        pointAt( phetvector );
        ModelElement modelelement = new ModelElement() {

            public void stepInTime( double d ) {
                if( timeSinceFire > timeBetweenFires && lightOn ) {
                    firePhoton();
                    timeSinceFire = 0.0D;
                }
                else {
                    timeSinceFire += d;
                }
            }

        };
        getModel().addModelElement( modelelement );
        getModel().addModelElement( new ModelElement() {

            public void stepInTime( double d ) {
                PhetVector phetvector1 = getResistorCenter();
                for( int i = 0; i < photons.size(); i++ ) {
                    Photon photon = (Photon)photons.get( i );
                    photon.stepInTime( d );
                    double d1 = photon.getPosition().getSubtractedInstance( phetvector1 ).getMagnitude();
                    if( d1 <= photon.getSpeed() * d ) {
                        photons.remove( i );
                        Graphic graphic = (Graphic)photonGraphicTable.get( photon );
                        getApparatusPanel().removeGraphic( graphic );
                        model.photonHit();
                    }
                }

            }

        } );
    }

    private PhetVector getResistorCenter() {
        PhetVector phetvector = circuit.getResistor().getStartPosition();
        PhetVector phetvector1 = circuit.getResistor().getEndPosition();
        PhetVector phetvector2 = phetvector1.getSubtractedInstance( phetvector ).getScaledInstance( 0.5D );
        PhetVector phetvector3 = phetvector.getAddedInstance( phetvector2 );
        return phetvector3;
    }

    public void pointAt( PhetVector phetvector ) {
        PhetVector phetvector1 = light.getPosition();
        PhetVector phetvector2 = phetvector.getSubtractedInstance( phetvector1 ).getNormalizedInstance();
        double d = phetvector2.getAngle();
        double d1 = Math.toRadians( 126D );
        double d2 = -d + d1;
        light.setAngle( d2 );
    }

    private void addCableGraphic() {
        java.awt.geom.Rectangle2D.Double double1 = model.getBandSet().getBounds();
        final GeneralPath cablePath = new GeneralPath();
        cablePath.moveTo( (float)double1.getX() + (float)double1.getWidth() / 2.0F, (float)double1.getY() );
        MacroCircuit macrocircuit = model.getCircuit();
        PhetVector phetvector = macrocircuit.getResistor().getLocation( macrocircuit.getResistor().getLength() / 2D );
        PhetVector phetvector1 = new PhetVector( double1.getX() + 0.29999999999999999D, double1.getY() - 0.20000000000000001D );
        PhetVector phetvector2 = phetvector.getSubtractedInstance( 0.0D, 0.10000000000000001D );
        cablePath.curveTo( (float)phetvector1.getX(), (float)phetvector1.getY(), (float)phetvector2.getX(), (float)phetvector2.getY(), (float)phetvector.getX(), (float)phetvector.getY() );
        final ShapeGraphic curveShape = new ShapeGraphic( cablePath, Color.black, new BasicStroke( 2.0F ) );
        transform.addTransformListener( new TransformListener() {

            public void transformChanged( ModelViewTransform2D modelviewtransform2d ) {
                java.awt.Shape shape = transform.toAffineTransform().createTransformedShape( cablePath );
                curveShape.setShape( shape );
            }

        } );
        getApparatusPanel().addGraphic( curveShape, 3D );
    }

    private void relayoutBatterySpinner() {
        Point point = transform.modelToView( circuit.getBattery().getEndPosition() );
        JSpinner jspinner = batterySpinner.getSpinner();
        jspinner.setBounds( point.x, point.y + jspinner.getPreferredSize().height, jspinner.getPreferredSize().width, jspinner.getPreferredSize().height );
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

    public static void main( String args[] )
            throws IOException {
        SwingTimerClock swingtimerclock = new SwingTimerClock( 1.0D, 30, true );
        final MacroModule module = new MacroModule( swingtimerclock );
        PhetApplication phetapplication = new PhetApplication( new ApplicationDescriptor( "Semiconductors!", "Apply current to a conductor, insulator or photoconductor.", "0.2", new FrameCenterer( 100, 100 ) ), module, swingtimerclock );
        phetapplication.startApplication( module );
        swingtimerclock.addClockTickListener( new ClockTickListener() {

            public void clockTicked( AbstractClock abstractclock, double d ) {
                module.getApparatusPanel().repaint();
            }

        } );
        module.getApparatusPanel().invalidate();
        module.getApparatusPanel().validate();
        module.getApparatusPanel().repaint();
        ClockTickListener clockticklistener = new ClockTickListener() {

            public void clockTicked( AbstractClock abstractclock, double d ) {
                if( abstractclock.getRunningTime() > abstractclock.getDt() * 20D ) {
                    module.getApparatusPanel().invalidate();
                    module.getApparatusPanel().revalidate();
                    module.getApparatusPanel().repaint();
                    System.out.println( "Revalidating." );
                    abstractclock.removeClockTickListener( this );
                }
            }

        };
        swingtimerclock.addClockTickListener( clockticklistener );
        phetapplication.getApplicationView().getBasicPhetPanel().setBackground( new Color( 245, 245, 255 ) );
        edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer apparatuspanelcontainer = phetapplication.getApplicationView().getApparatusPanelContainer();
        if( apparatuspanelcontainer instanceof SingleApparatusPanelContainer ) {
            SingleApparatusPanelContainer singleapparatuspanelcontainer = (SingleApparatusPanelContainer)apparatuspanelcontainer;
            singleapparatuspanelcontainer.getComponent().setLayout( new AspectRatioLayout( module.getApparatusPanel(), 10, 10, 0.75D ) );
            phetapplication.getApplicationView().getBasicPhetPanel().invalidate();
            phetapplication.getApplicationView().getBasicPhetPanel().validate();
            phetapplication.getApplicationView().getBasicPhetPanel().repaint();
        }
    }

    public void setBandSet( DefaultBandSet defaultbandset ) {
        DefaultBandSet defaultbandset1 = model.getBandSet();
        BandSetGraphic bandsetgraphic = (BandSetGraphic)bandSetGraphicTable.get( defaultbandset1 );
        if( bandsetgraphic != null ) {
            getApparatusPanel().removeGraphic( bandsetgraphic );
        }
        BandSetGraphic bandsetgraphic1 = getBandSetGraphic( defaultbandset );
        defaultbandset.initParticles();
        getApparatusPanel().addGraphic( bandsetgraphic1 );
        model.setBandSet( defaultbandset );
        model.doVoltageChanged();
    }

    private BandSetGraphic getBandSetGraphic( DefaultBandSet defaultbandset ) {
        BandSetGraphic bandsetgraphic = (BandSetGraphic)bandSetGraphicTable.get( defaultbandset );
        if( bandsetgraphic == null ) {
            bandsetgraphic = new BandSetGraphic( transform, defaultbandset.getBounds(), defaultbandset );
            defaultbandset.addBandParticleObserver( bandsetgraphic );
            bandSetGraphicTable.put( defaultbandset, bandsetgraphic );
        }
        return bandsetgraphic;
    }

    public void setConductor() {
        setBandSet( model.getConductor() );
        disableFlashlight();
        circuitGraphic.getResistorGraphic().setFillPaint( new Color( 210, 210, 210 ) );
        circuitGraphic.getResistorGraphic().setOutlinePaint( new Color( 210, 210, 210 ) );
    }

    public void setInsulator() {
        setBandSet( model.getInsulator() );
        macroControlPanel.disableFlashlight();
        circuitGraphic.getResistorGraphic().setFillPaint( Color.orange );
        circuitGraphic.getResistorGraphic().setOutlinePaint( Color.orange );
        disableFlashlight();
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
        PhetVector phetvector = getResistorCenter();
        Photon photon = new Photon();
        photon.setPosition( light.getPosition() );
        double d = 0.01D;
        PhetVector phetvector1 = phetvector.getSubtractedInstance( photon.getPosition() ).getInstanceForMagnitude( d );
        photon.setVelocity( phetvector1 );
        PhotonArrowGraphic photonarrowgraphic = new PhotonArrowGraphic( photon, transform );
        getApparatusPanel().addGraphic( photonarrowgraphic, 3D );
        photons.add( photon );
        photonGraphicTable.put( photon, photonarrowgraphic );
    }

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
    private AbstractClock clock;
    private double timeBetweenFires;
    private ArrayList photons;
    private MacroControlPanel macroControlPanel;
    private FlashlightGraphic flashlightGraphic;
    double timeSinceFire;
    Hashtable photonGraphicTable;


}
