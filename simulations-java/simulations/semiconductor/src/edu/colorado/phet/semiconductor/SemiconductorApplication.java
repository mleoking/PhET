package edu.colorado.phet.semiconductor;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.PhetLookAndFeel;
import edu.colorado.phet.semiconductor.macro.*;
import edu.colorado.phet.semiconductor.macro.circuit.CircuitSection;
import edu.colorado.phet.semiconductor.macro.circuit.MacroCircuitGraphic;
import edu.colorado.phet.semiconductor.macro.circuit.battery.BatterySpinner;
import edu.colorado.phet.semiconductor.macro.doping.DopantGraphic;
import edu.colorado.phet.semiconductor.macro.doping.DopantPanel;
import edu.colorado.phet.semiconductor.macro.doping.DopantType;
import edu.colorado.phet.semiconductor.macro.energy.EnergySection;
import edu.colorado.phet.semiconductor.macro.energy.ParticleAction;
import edu.colorado.phet.semiconductor.macro.energy.ParticleActionApplicator;
import edu.colorado.phet.semiconductor.macro.energy.bands.Band;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandParticle;
import edu.colorado.phet.semiconductor.macro.energy.bands.BandSetGraphic;
import edu.colorado.phet.semiconductor.macro.energy.states.MoveToCell;
import edu.colorado.phet.semiconductor.macro.energyprobe.Cable;
import edu.colorado.phet.semiconductor.macro.energyprobe.CableGraphic;
import edu.colorado.phet.semiconductor.macro.energyprobe.Lead;
import edu.colorado.phet.semiconductor.macro.energyprobe.LeadGraphic;
import edu.colorado.phet.semiconductor.util.math.DoubleSeries;
import edu.colorado.phet.semiconductor.util.math.PhetVector;
import edu.colorado.phet.semiconductor.phetcommon.model.BaseModel;
import edu.colorado.phet.semiconductor.phetcommon.model.ModelElement;
import edu.colorado.phet.semiconductor.phetcommon.model.clock.ClockTickListener;
import edu.colorado.phet.semiconductor.phetcommon.model.clock.SwingTimerClock;
import edu.colorado.phet.semiconductor.phetcommon.view.ApparatusPanel;
import edu.colorado.phet.semiconductor.phetcommon.view.BasicGraphicsSetup;
import edu.colorado.phet.semiconductor.phetcommon.view.CompositeInteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.DefaultInteractiveGraphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.Graphic;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.bounds.Boundary;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.mousecontrols.Translatable;
import edu.colorado.phet.semiconductor.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.semiconductor.phetcommon.view.util.framesetup.FrameSetup;
import edu.colorado.phet.semiconductor.phetcommon.view.util.graphics.HashedImageLoader;
import edu.colorado.phet.semiconductor.phetcommon.view.util.graphics.ImageLoader;

/**
 * User: Sam Reid
 * Date: Feb 7, 2004
 * Time: 7:11:36 PM
 */
public class SemiconductorApplication implements Graphic {
    // Localization
    public static final String localizedStringsPath = "semiconductor/localization/semiconductor-strings";
    private static final String VERSION = PhetApplicationConfig.getVersion( "semiconductor" ).formatForTitleBar();

    CircuitSection circuitSection;
    EnergySection energySection;
    ModelViewTransform2D transform;
    private BufferedImage particleImage;
    public static final HashedImageLoader imageLoader = new HashedImageLoader();
    private DopantPanel dopantPanel;
    private ArrayList cableGraphics = new ArrayList();
    private Magnet magnet;
    private MagnetGraphic magnetGraphic;
    private ApparatusPanel apparatusPanel;
    private BaseModel bm;
    private DiodeControlPanel dcp;


    public SemiconductorApplication( SwingTimerClock clock ) throws IOException {
        transform = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 10, 10 ), new Rectangle( 0, 0, 1, 1 ) );


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
                                       transform, imageLoader.loadImage( "semiconductor/images/particle-green-med.gif" ),
                                       imageLoader.loadImage( "semiconductor/images/particle-red-med.gif" ), new Rectangle2D.Double( 8.5, 1, 1, 3 ) );
        dopantPanel.addDopantDropListener( circuitSection );
        circuitSection.addDopantChangeListener( energySection );

        getApparatusPanel().addGraphic( dopantPanel, 2 );

        clock.addClockTickListener( new ClockTickListener() {
            public void clockTicked( double v ) {
                getApparatusPanel().repaint();
            }
        } );

        getApparatusPanel().addComponentListener( new Relayout() );
        getApparatusPanel().addGraphic( this );

        ColumnDebugGraphic cdg = new ColumnDebugGraphic( energySection, transform );
        getApparatusPanel().addGraphic( cdg, 10000 );

//        BufferedImage magnetImage = new ImageLoader().loadImage( "semiconductor/images/magnet.gif" );
        BufferedImage magnetImage = new ImageLoader().loadImage( "semiconductor/images/gate2.gif" );

        double magnetWidth = 1;
        double magScale = magnetWidth / magnetImage.getWidth(); //takes pixels to model.
        double magnetHeight = magnetImage.getHeight() * magScale;
        Rectangle2D.Double rect = new Rectangle2D.Double( 0, 0, magnetWidth, magnetHeight );
        magnet = new Magnet( rect );
        magnetGraphic = new MagnetGraphic( magnet, transform, magnetImage );

        DefaultInteractiveGraphic dig = new DefaultInteractiveGraphic( magnetGraphic, new Boundary() {
            public boolean contains( int x, int y ) {
                Shape trf = transform.createTransformedShape( magnet.getBounds() );
                return trf.contains( x, y );
            }
        } );
        addGraphic( dig, 0 );
        dig.addCursorHandBehavior();
        dig.addTranslationBehavior( new Translatable() {
            public void translate( double dx, double dy ) {
                Point2D.Double out = transform.viewToModelDifferential( (int) dx, (int) dy );
                Rectangle2D.Double allowed = transform.getModelBounds();
                Shape trans = magnet.getTranslatedShape( out.x, out.y );
                if ( allowed.contains( trans.getBounds2D() ) ) {
                    magnet.translate( out.x, out.y );
                    getApparatusPanel().repaint();
                }
            }
        } );
        ModelElement goToMagnet = new ModelElement() {
            public void stepInTime( double dt ) {
                if ( magnetGraphic.isVisible() ) {
                    if ( energySection.numBandSets() == 3 ) {
                        Band mid = energySection.bandSetAt( 1 ).getTopBand();
                        final Band con = energySection.bandSetAt( 1 ).getConductionBand();
                        boolean in = false;
//                        if( mid.getRegion().toRectangle().intersects( magnet.getBounds() ) ) {
//                            in = true;
//                        }
                        if ( con.getRegion().toRectangle().intersects( magnet.getBounds() ) ) {
                            in = true;
                        }
                        if ( in ) {
                            ParticleAction pa = new ParticleAction() {
                                public void apply( BandParticle particle ) {
                                    if ( particle.getBand() == con ||
                                         ( particle.getBand() == energySection.bandSetAt( 1 ).getValenceBand()
                                           && particle.getEnergyLevel().getDistanceFromBottomLevelInBand() >= DopantType.P.getNumFilledLevels() )
                                         && !particle.isExcited() ) {
                                        particle.setState( new GoToMagnet( magnet, particle.getEnergyCell() ) );
                                    }
                                }
                            };
                            ParticleActionApplicator paa = new ParticleActionApplicator( energySection );
                            paa.addParticleAction( pa );
                            paa.stepInTime( dt );
                            energySection.bandSetAt( 1 ).trickDopantType( DopantType.P );
                        }
                    }
                }
            }
        };
        addModelElement( goToMagnet );

        DiodeControlPanel dcp = new DiodeControlPanel( this );
        setControlPanel( dcp );
    }

    private void setControlPanel( DiodeControlPanel dcp ) {
        this.dcp = dcp;
    }

    private void addModelElement( ModelElement modelElement ) {
        bm.addModelElement( modelElement );
    }

    private void addGraphic( Graphic dig, int layer ) {
    }

    private BaseModel getModel() {
        return bm;
    }

    private void setModel( BaseModel bm ) {
        this.bm = bm;
    }

    private void setApparatusPanel( ApparatusPanel ap ) {
        this.apparatusPanel = ap;
    }

    public MagnetGraphic getMagnetGraphic() {
        return magnetGraphic;
    }

    public ApparatusPanel getApparatusPanel() {
        return apparatusPanel;
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
        for ( int i = 0; i < circuitSection.numDopantSlots() && i < energySection.numBandSets(); i++ ) {
            try {
                addCable( circuitSection.dopantSlotAt( i ).getModelCenter(), i );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }

    public void paint( Graphics2D g ) {
        for ( int i = 0; i < cableGraphics.size(); i++ ) {
            Graphic gr = (Graphic) cableGraphics.get( i );
            gr.paint( g );
        }
    }

    private void addCable( PhetVector tip, int bandIndex ) throws IOException {
        Lead lead = new Lead( tip );
        BandSetGraphic bsg = energySection.bandSetGraphicAt( bandIndex );
        Cable c = new Cable( lead, bsg.getViewportBottomCenter() );

        LeadGraphic lg = new LeadGraphic( transform, lead, imageLoader.loadImage( "semiconductor/images/probeRed.gif" ) );
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
//            frame.setSize( Toolkit.getDefaultToolkit().getScreenSize().width - 300, Toolkit.getDefaultToolkit().getScreenSize().height - 300 );
            frame.setSize( 944, 706 );
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

    public void releaseGate() {
        for ( int i = 0; i < energySection.numParticles(); i++ ) {
            BandParticle bp = energySection.particleAt( i );
            if ( bp.getState() instanceof GoToMagnet ) {
                GoToMagnet gtm = (GoToMagnet) bp.getState();
                bp.setState( new MoveToCell( bp, gtm.getFrom(), .2 ) );
                bp.setExcited( false );
            }
        }
        if ( energySection.numBandSets() == 3 && energySection.bandSetAt( 1 ).getDopantType() == DopantType.P ) {
            energySection.bandSetAt( 1 ).trickDopantType( DopantType.N );
        }
    }

    public static class SemiconductorApplicationConfig extends PhetApplicationConfig {

        public SemiconductorApplicationConfig( String[] commandLineArgs ) {
            super( commandLineArgs, new edu.colorado.phet.common.phetcommon.view.util.FrameSetup.CenteredWithInsets( 100, 100 ), SemiconductorResources.getResourceLoader() );
            PhetLookAndFeel feel = new PhetLookAndFeel();
            feel.setBackgroundColor( new Color( 245, 245, 255 ) );
            setLookAndFeel( feel );
            setApplicationConstructor( new ApplicationConstructor() {
                public edu.colorado.phet.common.phetcommon.application.PhetApplication getApplication( PhetApplicationConfig config ) {
                    return new SemiconductorPhetApplication( config );
                }
            } );
        }

    }

    private static class SemiconductorPhetApplication extends edu.colorado.phet.common.phetcommon.application.PhetApplication {
        protected SemiconductorPhetApplication( PhetApplicationConfig config ) {
            super( config );
            addModule( new SemiconductorModule( config ) );
        }

    }

    private static class SemiconductorModule extends edu.colorado.phet.common.phetcommon.application.Module {
        public SemiconductorModule( PhetApplicationConfig config ) {
            super( "name", new ConstantDtClock( 30, 1 ) );
            try {
                final SemiconductorApplication ca = new SemiconductorApplication( new SwingTimerClock( 1, 45, true ) );
                setSimulationPanel( ca.getApparatusPanel() );
                setControlPanel( ca.dcp );
                getClock().addClockListener( new ClockAdapter() {
                    public void clockTicked( ClockEvent clockEvent ) {
                        ca.clockTicked( clockEvent );
                        getSimulationPanel().repaint();
                    }
                } );
            }
            catch( IOException e ) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
    }

    private void clockTicked( ClockEvent clockEvent ) {
        bm.clockTicked( clockEvent.getSimulationTimeChange() );
    }

    public static void main( final String[] args ) throws IOException, UnsupportedLookAndFeelException {
        new SemiconductorApplicationConfig( args ).launchSim();
    }

}
