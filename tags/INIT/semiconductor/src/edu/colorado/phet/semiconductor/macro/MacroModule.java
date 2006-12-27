package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.apparatuspanelcontainment.SingleApparatusPanelContainer;
import edu.colorado.phet.common.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.graphics.transforms.TransformListener;
import edu.colorado.phet.common.view.util.AspectRatioLayout;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.graphics.HashedImageLoader;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.semiconductor.common.NotYetWrittenException;
import edu.colorado.phet.semiconductor.common.OneShotAction;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuit;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticle;
import edu.colorado.phet.semiconductor.macro.circuit.particles.WireParticleGraphic;
import edu.colorado.phet.semiconductor.macro.doping.DopantPanel;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSet;
import edu.colorado.phet.semiconductor.macro.energy.bands.EnergyTextGraphic;
import edu.colorado.phet.semiconductor.macro.energyprobe.Lead;
import edu.colorado.phet.semiconductor.macro.energyprobe.LeadGraphic;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jan 15, 2004
 * Time: 12:59:07 PM
 * Copyright (c) Jan 15, 2004 by Sam Reid
 */
public class MacroModule extends Module {
    double minVolts = 0;
    double maxVolts = 2;
    private MacroSystem model;
    MacroCircuit circuit;
    private MacroCircuitGraphic circuitGraphic;
    private ModelViewTransform2D transform;
    private static final double RESISTOR_LAYER = 2;
    Stroke initstroke = new BasicStroke( 1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER );

    private BatterySpinner batterySpinner;
    private Hashtable bandSetGraphicTable = new Hashtable();
    private AbstractClock clock;
    private MacroControlPanel macroControlPanel;
    private DopantPanel dopantPanel;
    ImageLoader imageLoader = new HashedImageLoader();
    private double imageHeightModelCoords;

    public MacroModule( AbstractClock clock ) throws IOException {
        super( "Semiconductors!" );
        this.clock = clock;
        this.transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 1, 1 ), new Rectangle( 0, 0, 600, 600 ) );

        setApparatusPanel( new ApparatusPanel() );
        getApparatusPanel().setBackground( new Color( 230, 220, 255 ) );
        getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
        macroControlPanel = new MacroControlPanel( this );
        setControlPanel( macroControlPanel );
        setModel( new BaseModel( clock ) );
        addViewBoundsListener();

        imageHeightModelCoords = transform.viewToModelDifferentialX( MacroCircuitGraphic.getParticleImage().getWidth() );

//        model = new BandSetSystem(minVolts, maxVolts, imageHeightModelCoords);
//        diodeModel = new DiodeSystem(minVolts, maxVolts, imageHeightModelCoords);
        this.circuit = model.getCircuit();
        circuitGraphic = new MacroCircuitGraphic( circuit, transform );
        for( int i = 0; i < circuitGraphic.numWireGraphics(); i++ ) {
            getApparatusPanel().addGraphic( circuitGraphic.wireGraphicAt( i ), 0 );
        }
        getApparatusPanel().addGraphic( circuitGraphic.getBatteryGraphic(), 2 );
        getApparatusPanel().addGraphic( circuitGraphic.getResistorGraphic(), RESISTOR_LAYER );

        getModel().addModelElement( model );

        double dx = .05;
        double length = circuit.getLength();
        int numParticles = (int)( length / dx + 1 );
        double particleX = 0;
        for( int i = 0; i < numParticles; i++ ) {
            WireParticle p = new WireParticle( particleX, circuit );
            getModel().addModelElement( p );
            model.particles.add( p );
            Graphic wireParticleGraphic = new WireParticleGraphic( p, transform, MacroCircuitGraphic.getParticleImage() );
            getApparatusPanel().addGraphic( wireParticleGraphic, 3 );
            particleX += dx;
        }

        batterySpinner = new BatterySpinner( circuit.getBattery() );
        getApparatusPanel().setLayout( null );
        transform.addTransformListener( new TransformListener() {
            public void transformChanged( ModelViewTransform2D modelViewTransform2D ) {
                relayoutBatterySpinner();
            }
        } );

        getApparatusPanel().add( batterySpinner.getSpinner() );

//        addLight();

        addCableGraphic();
//        setConductor();
        setPhotoconductor();
        Rectangle2D.Double bounds = model.getBandSetBounds();
        PhetVector textLocation = new PhetVector( bounds.x, bounds.y );
        EnergyTextGraphic etg = new EnergyTextGraphic( transform, textLocation );
        getApparatusPanel().addGraphic( etg, 1000 );

        getApparatusPanel().addGraphic( dopantPanel, 100000 );

    }   //end constructor

    double timeSinceFire = Double.MAX_VALUE;

//    private void addLight() throws IOException {
//        timeBetweenFires = clock.getDt() * 3;
//        light = new Flashlight(.8, .1, 0);
//        ImageLoader loady = new ImageLoader();
//        BufferedImage lightImage = loady.loadImage("images/light.gif");
//        flashlightGraphic = new FlashlightGraphic(light, lightImage, transform);
//        getApparatusPanel().addGraphic(flashlightGraphic, 100);
//
//        final PhetVector dst = getResistorCenter();
//
//        pointAt(dst);
//        ModelElement flashLightElement = new ModelElement() {
//            public void stepInTime(double dt) {
//                if (timeSinceFire > timeBetweenFires && lightOn) {
//                    firePhoton();
//                    timeSinceFire = 0;
//                } else {
//                    timeSinceFire += dt;
//                }
//            }
//        };
//        ModelElement photonMover = new ModelElement() {
//            public void stepInTime(double dt) {
//                PhetVector finalLoc = getResistorCenter();
//                for (int i = 0; i < photons.size(); i++) {
//                    Photon photon = (Photon) photons.get(i);
//                    photon.stepInTime(dt);
//                    double dist = photon.getPosition().getSubtractedInstance(finalLoc).getMagnitude();
//                    if (dist <= photon.getSpeed() * dt) {
//                        //hit target.
//                        //get absorbed.
//                        photons.remove(i);
//                        Graphic g = (Graphic) photonGraphicTable.get(photon);
//                        getApparatusPanel().removeGraphic(g);
//                        model.photonHit();
//                    }
//                }
//            }
//        };
//        getModel().addModelElement(flashLightElement);
//        getModel().addModelElement(photonMover);
//    }

    private PhetVector getResistorCenter() {

        PhetVector start = circuit.getResistor().getStartPosition();
        PhetVector end = circuit.getResistor().getEndPosition();
        PhetVector dv = end.getSubtractedInstance( start ).getScaledInstance( .5 );

        final PhetVector dst = start.getAddedInstance( dv );
        return dst;
    }

//    public void pointAt(PhetVector dst) {
//        PhetVector init = light.getPosition();
//        PhetVector dir = dst.getSubtractedInstance(init).getNormalizedInstance();
//        double angle = dir.getAngle();
//        double rad = Math.toRadians(126);
//        double totalAngle = -angle + rad;
//        light.setAngle(totalAngle);
//    }

    private void addCableGraphic() throws IOException {
        PhetVector target = circuit.getResistor().getLocation( circuit.getResistor().getLength() / 2 );
        final Lead lead = new Lead( target );
        LeadGraphic leadGraphic = new LeadGraphic( transform, lead,
                                                   imageLoader.loadImage( "images/probeRed.gif" ) );
        DefaultInteractiveGraphic dig = new DefaultInteractiveGraphic( leadGraphic, leadGraphic.getBoundary() );
        dig.addCursorHandBehavior();
        dig.addTranslationBehavior( new Translatable() {
            public void translate( double x, double y ) {
                Point2D.Double out = transform.viewToModelDifferential( (int)x, (int)y );
                double destX = out.x + lead.getTipLocation().getX();
                if( destX >= circuit.getResistor().getStartPosition().getX() && destX <= circuit.getResistor().getEndPosition().getX() ) {
                    lead.translate( out.x, 0 );
                }
            }
        } );
        getApparatusPanel().addGraphic( dig, 30 );
    }

    private void relayoutBatterySpinner() {
        Point viewPtBatt = transform.modelToView( circuit.getBattery().getEndPosition() );
        JSpinner batterySp = this.batterySpinner.getSpinner();
        batterySp.setBounds( viewPtBatt.x, viewPtBatt.y + batterySp.getPreferredSize().height, batterySp.getPreferredSize().width, batterySp.getPreferredSize().height );
        batterySp.repaint();
    }

    private void addViewBoundsListener() {
        getApparatusPanel().addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
                setTransform();
            }

            public void componentShown( ComponentEvent e ) {
                setTransform();
            }
        } );
    }

    void setTransform() {
        Rectangle b = new Rectangle( 0, 0, getApparatusPanel().getWidth(), getApparatusPanel().getHeight() );
        transform.setViewBounds( b );
//        System.out.println("transform = " + transform);
        getApparatusPanel().repaint();
    }

    public static void main( String[] args ) throws IOException {
        AbstractClock clock = new SwingTimerClock( 1, 30, true );

        final MacroModule module = new MacroModule( clock );
        PhetApplication app = new PhetApplication( new ApplicationDescriptor( "Semiconductors!", "Apply current to a conductor, insulator or photoconductor.", "0.2", new FrameCenterer( 100, 100 ) ), module, clock );
        app.startApplication( module );
        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock abstractClock, double v ) {
                module.getApparatusPanel().repaint();
            }
        } );
        module.getApparatusPanel().invalidate();
        module.getApparatusPanel().validate();
        module.getApparatusPanel().repaint();
        OneShotAction validate = new OneShotAction( new Runnable() {
            public void run() {
                module.getApparatusPanel().invalidate();
                module.getApparatusPanel().revalidate();
                module.getApparatusPanel().repaint();
                System.out.println( "Revalidating." );
            }
        }, 30 );
        clock.addClockTickListener( validate );
        app.getApplicationView().getBasicPhetPanel().setBackground( new Color( 245, 245, 255 ) );
        ApparatusPanelContainer apc = app.getApplicationView().getApparatusPanelContainer();
        if( apc instanceof SingleApparatusPanelContainer ) {
            SingleApparatusPanelContainer sapc = (SingleApparatusPanelContainer)apc;
            sapc.getComponent().setLayout( new AspectRatioLayout( module.getApparatusPanel(), 10, 10, .75 ) );
            app.getApplicationView().getBasicPhetPanel().invalidate();
            app.getApplicationView().getBasicPhetPanel().validate();
            app.getApplicationView().getBasicPhetPanel().repaint();
        }
    }

    public void setBandSet( BandSet bandSet ) {
//        BandSet oldSet = model.getBandSet();
//        BandSetGraphic oldGraphic = (BandSetGraphic) bandSetGraphicTable.get(oldSet);
//        if (oldGraphic != null) {
//            getApparatusPanel().removeGraphic(oldGraphic);
//        }
//        BandSetGraphic bandSetGraphic = getBandSetGraphic(bandSet);
//        bandSet.initParticles();
//        getApparatusPanel().addGraphic(bandSetGraphic);
//        model.setBandSet(bandSet);
//        model.doVoltageChanged();
    }

//    private BandSetGraphic getBandSetGraphic(BandSet bandSet) {
////        BandSetGraphic bsg = (BandSetGraphic) bandSetGraphicTable.get(bandSet);
////        if (bsg == null) {
////            bsg = new BandSetGraphic(transform, bandSet);
////            bandSet.addBandParticleObserver(bsg);
////            bandSetGraphicTable.put(bandSet, bsg);
////        }
////        return bsg;
//    }

    public void setConductor() {
        throw new NotYetWrittenException();
//        setBandSet(model.getConductor());
//        disableFlashlight();
//        circuitGraphic.getResistorGraphic().setFillPaint(new Color(210, 210, 210));
//        circuitGraphic.getResistorGraphic().setOutlinePaint(new Color(210, 210, 210));
    }

    public void setInsulator() {
        throw new NotYetWrittenException();
//        setBandSet(model.getInsulator());
//        macroControlPanel.disableSemiconductor();
//        circuitGraphic.getResistorGraphic().setFillPaint(Color.orange);
//        circuitGraphic.getResistorGraphic().setOutlinePaint(Color.orange);
//        disableFlashlight();
    }

//    private void disableFlashlight() {
//        macroControlPanel.disableSemiconductor();
//        flashlightGraphic.setVisible(false);
//        setFlashlightOn(false);
//    }

    public void setPhotoconductor() {
        model.removeBandSets();
        Rectangle2D.Double bounds = new Rectangle2D.Double( 0, 0, 1, 1 );
//        SemiconductorBandSet pbs=new SemiconductorBandSet(imageHeightModelCoords, bounds);
//        model.addBandSet(pbs);
        throw new NotYetWrittenException();
//        setBandSet(model.getPhotoconductor());
//        macroControlPanel.enableSemiconductor();
//        circuitGraphic.getResistorGraphic().setFillPaint(Color.yellow);
//        circuitGraphic.getResistorGraphic().setOutlinePaint(Color.yellow);
//        enableFlashlight();
    }

    public void setDiode() {
//        BandSet oldSet = model.getBandSet();
//        JunctionGraphic jg=new JunctionGraphic(transform, diodeModel.getJunction());
//        getApparatusPanel().addGraphic(jg);
//        BandSetGraphic oldGraphic = (BandSetGraphic) bandSetGraphicTable.get(oldSet);
//        if (oldGraphic != null) {
//            getApparatusPanel().removeGraphic(oldGraphic);
//        }
////        BandSetGraphic bandSetGraphic = getBandSetGraphic(bandSet);
////        bandSet.initParticles();
////        getApparatusPanel().addGraphic(bandSetGraphic);
////        model.setBandSet(bandSet);
//        model.doVoltageChanged();
    }

//    private void enableFlashlight() {
//        macroControlPanel.enableSemiconductor();
//        flashlightGraphic.setVisible(true);
//    }

//    public void setFlashlightOn(boolean on) {
//
//        this.lightOn = on;
//        model.getPhotoconductor().setFlashlightOn(on);
//    }

//    public void firePhoton() {
//        PhetVector dst = getResistorCenter();
//        Photon photon = new Photon();
//        photon.setPosition(light.getPosition());
//        double speed = .01;
//        PhetVector velocity = dst.getSubtractedInstance(photon.getPosition()).getInstanceForMagnitude(speed);
//        photon.setVelocity(velocity);
//        PhotonArrowGraphic pag = new PhotonArrowGraphic(photon, transform);
//        getApparatusPanel().addGraphic(pag, RESISTOR_LAYER + 1);
//        photons.add(photon);
//        photonGraphicTable.put(photon, pag);
//    }

    Hashtable photonGraphicTable = new Hashtable();

    public void setDopantPanelVisible( boolean selected ) {
        //unclick flashlight.
//        if (selected)
//            this.macroControlPanel.turnOffLight();
        dopantPanel.setVisible( selected );
    }

    public void setShowPlusses( boolean selected ) {
////        circuitGraphic.setShowPlusses(selected);
//        Collection val = bandSetGraphicTable.values();
//        for (Iterator iterator = val.iterator(); iterator.hasNext();) {
//            BandSetGraphic bandSetGraphic = (BandSetGraphic) iterator.next();
////            bandSetGraphic.setShowPlusses(selected);
//        }
    }

}
