package edu.colorado.phet.semiconductor.macro;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.common.math.DoubleSeries;
import edu.colorado.phet.common.math.PhetVector;
import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.common.model.clock.SwingTimerClock;
import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.ApplicationDescriptor;
import edu.colorado.phet.common.view.BasicGraphicsSetup;
import edu.colorado.phet.common.view.CompositeInteractiveGraphic;
import edu.colorado.phet.common.view.apparatuspanelcontainment.ApparatusPanelContainer;
import edu.colorado.phet.common.view.apparatuspanelcontainment.SingleApparatusPanelContainer;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.view.util.AspectRatioLayout;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.graphics.HashedImageLoader;
import edu.colorado.phet.semiconductor.macro.circuit.CircuitSection;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.doping.DopantGraphic;
import edu.colorado.phet.semiconductor.macro.doping.DopantPanel;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSetGraphic;
import edu.colorado.phet.semiconductor.macro.energyprobe.Cable;
import edu.colorado.phet.semiconductor.macro.energyprobe.CableGraphic;
import edu.colorado.phet.semiconductor.macro.energyprobe.Lead;
import edu.colorado.phet.semiconductor.macro.energyprobe.LeadGraphic;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSpinnerUI;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:11:36 PM
 * Copyright (c) Feb 7, 2004 by Sam Reid
 */
public class SemiconductorModule extends Module implements Graphic {
    CircuitSection circuitSection;
    EnergySection energySection;
    ModelViewTransform2D transform;
    private BufferedImage particleImage;
    public static final HashedImageLoader imageLoader = new HashedImageLoader();
    private DopantPanel dopantPanel;
    private ArrayList cableGraphics = new ArrayList();
    private Stroke stroke = new BasicStroke( 1 );

    public SemiconductorModule( SwingTimerClock clock ) throws IOException {
        super( "Diodes" );
        transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle( 0, 0, 1, 1 ) );

        DiodeControlPanel dcp = new DiodeControlPanel( this );
        setControlPanel( dcp );
        ApparatusPanel ap = new ApparatusPanel();
        setApparatusPanel( ap );
        getApparatusPanel().setBackground( new Color( 230, 220, 255 ) );
        getApparatusPanel().addGraphicsSetup( new BasicGraphicsSetup() );
        BaseModel bm = new BaseModel( clock );
        setModel( bm );
        int NUM_REGIONS = 2;
        circuitSection = new CircuitSection( this, transform, 6, 5, 3.5, 4, NUM_REGIONS );
        BatterySpinner bs = circuitSection.getBatterySpinner();
        JButton clearDopantButton = circuitSection.getClearDopantButton();
        getApparatusPanel().add( clearDopantButton );
        getApparatusPanel().add( bs.getSpinner() );
        getModel().addModelElement( circuitSection );
        getApparatusPanel().addGraphic( circuitSection );
        particleImage = MacroCircuitGraphic.getParticleImage();
        double particleWidth = transform.viewToModelDifferentialX( particleImage.getWidth() );
        Rectangle2D.Double energySectionBounds = new Rectangle2D.Double( .1, .8, 6, 8.5 );
        energySection = new EnergySection( transform, particleWidth, circuitSection.getCircuit().getBattery(), circuitSection, energySectionBounds );
        circuitSection.getCircuit().getBattery().addBatteryListener( energySection );

        //sync the macro with the micro.
        final DoubleSeries ds = new DoubleSeries( 20 );
        ModelElement me = new ModelElement() {
            public void stepInTime( double dt ) {
                double avg = energySection.getAverageParticleDX() / dt * 2;
                ds.add( avg );
                avg = ds.average();
//                double leaving=energySection.getNumParticlesLeavingRight()-energySection.getNumParticlesLeavingLeft();
//                avg+=leaving;
//                System.out.println("leaving = " + leaving+", avg="+avg);
                circuitSection.setConductionAllowed( true );
                circuitSection.setMacroSpeed( avg );
            }
        };
        getModel().addModelElement( me );

        getModel().addModelElement( energySection );
        getApparatusPanel().addGraphic( energySection );
        energySection.addConductionListener( circuitSection );
        setupCables();
        dopantPanel = new DopantPanel( getApparatusPanel(),
                                       transform, imageLoader.loadImage( "images/particle-green-med.gif" ),
                                       imageLoader.loadImage( "images/particle-red-med.gif" ), new Rectangle2D.Double( 8.5, 1, 1, 3 ) );
        dopantPanel.addDopantDropListener( circuitSection );
        circuitSection.addDopantChangeListener( energySection );

        getApparatusPanel().addGraphic( dopantPanel );

        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( AbstractClock abstractClock, double v ) {
                getApparatusPanel().repaint();
            }
        } );

        getApparatusPanel().addComponentListener( new Relayout() );
        getApparatusPanel().addGraphic( this );

        ColumnDebugGraphic cdg=new ColumnDebugGraphic( energySection ,transform );
        getApparatusPanel().addGraphic( cdg,10000);
    }

    class Relayout extends ComponentAdapter {
        public void componentShown( ComponentEvent e ) {
            relayout();
        }

        public void componentResized( ComponentEvent e ) {
            relayout();
        }
    }

    private void setupCables() {
        cableGraphics.clear();
        for( int i = 0; i < circuitSection.numDopantSlots() && i < energySection.numBandSets(); i++ ) {
            try {
                addCable( circuitSection.dopantSlotAt( i ).getModelCenter(), i );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void paint( Graphics2D g ) {
        for( int i = 0; i < cableGraphics.size(); i++ ) {
            Graphic gr = (Graphic)cableGraphics.get( i );
            gr.paint( g );
        }
    }

    private void addCable( PhetVector tip, int bandIndex ) throws IOException {
        Lead lead = new Lead( tip );
        BandSetGraphic bsg = energySection.bandSetGraphicAt( bandIndex );
        Cable c = new Cable( lead, bsg.getViewportBottomCenter() );

        LeadGraphic lg = new LeadGraphic( transform, lead, imageLoader.loadImage( "images/probeRed.gif" ) );
        CableGraphic cg = new CableGraphic( transform, c, lg );
        CompositeInteractiveGraphic compositeGraphic = new CompositeInteractiveGraphic();
        compositeGraphic.addGraphic( lg );
        compositeGraphic.addGraphic( cg );

        cableGraphics.add( compositeGraphic );
    }

    private void relayout() {
        int w = getApparatusPanel().getWidth();
        int h = getApparatusPanel().getHeight();
        w = Math.max( w, 1 );
        h = Math.max( h, 1 );
        Rectangle rect = new Rectangle( 0, 0, w, h );
        transform.setViewBounds( rect );
//        double particleWidth = transform.viewToModelDifferentialX( particleImage.getWidth() );
//        energySection.setParticleWidth( particleWidth );
    }

    static class TopOfScreen implements FrameSetup {
        public void initialize( JFrame frame ) {
            frame.setLocation( 0, 0 );
            frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - 300, Toolkit.getDefaultToolkit().getScreenSize().height - 300 );
        }
    }

    public static void main( String[] args ) throws IOException, UnsupportedLookAndFeelException {
//        UIManager.setLookAndFeel(new SemiconductorLookAndFeel());
//        FrameSetup fs = new MaxExtentFrameSetup( new FullScreen() );
        FrameSetup fs = new TopOfScreen();
        ApplicationDescriptor ad = new ApplicationDescriptor( "Diodes", "Diodes", "Diodes", fs );
        SwingTimerClock clock = new SwingTimerClock( 1, 45, true );
        SemiconductorModule module = new SemiconductorModule( clock );
        PhetApplication pa = new PhetApplication( ad, module, clock );
        pa.startApplication( module );
        enableAspectRatio( pa, module );
    }

    static class MySpinnerUI extends BasicSpinnerUI {
        protected Component createNextButton() {
            return super.createNextButton();
        }

        protected Component createPreviousButton() {
            return super.createPreviousButton();
        }
    }

    private static void enableAspectRatio( PhetApplication app, Module module ) {
        ApparatusPanelContainer apc = app.getApplicationView().getApparatusPanelContainer();
        if( apc instanceof SingleApparatusPanelContainer ) {
            SingleApparatusPanelContainer sapc = (SingleApparatusPanelContainer)apc;
            sapc.getComponent().setLayout( new AspectRatioLayout( module.getApparatusPanel(), 10, 10, .75 ) );
            app.getApplicationView().getBasicPhetPanel().invalidate();
            app.getApplicationView().getBasicPhetPanel().validate();
            app.getApplicationView().getBasicPhetPanel().repaint();
        }
    }

    public void removeDopantGraphic( DopantGraphic dopant ) {
        dopantPanel.removeDopant( dopant );
    }

    public void setSingleSection() {
        energySection.setSingleSection();
        circuitSection.setSingleSection();
        setupCables();
        relayout();
        getApparatusPanel().repaint();
    }

    public void setDoubleSection() {
        energySection.setDoubleSection();
        circuitSection.setDoubleSection();
        setupCables();
        relayout();
        getApparatusPanel().repaint();
    }

    public void setTripleSection() {
        energySection.setTripleSection();
        circuitSection.setTripleSection();
        setupCables();
        relayout();
        getApparatusPanel().repaint();
    }

}
