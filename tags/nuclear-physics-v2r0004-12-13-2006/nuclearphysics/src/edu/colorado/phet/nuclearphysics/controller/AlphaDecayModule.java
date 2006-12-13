/**
 * Class: AlphaDecayModule
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 11:58:03 AM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.model.clock.IClock;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.ClockControlPanel;
import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.nuclearphysics.Config;
import edu.colorado.phet.nuclearphysics.model.*;
import edu.colorado.phet.nuclearphysics.view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.*;
import java.io.IOException;

public class AlphaDecayModule extends ProfiledNucleusModule implements DecayListener, PreDecayListener {

    private AlphaDecayPhysicalPanel physicalPanel;
    private Ellipse2D.Double alphaRing;
    private Line2D.Double leaderLine1;
    private Line2D.Double leaderLine2;
    private AlphaDecayControlPanel alphaDecayControlPanel;
    private PhetGraphic ringGraphic;
    private PhetGraphic leaderLines;
    private double ringLevel = Config.backgroundGraphicLevel;
    private double leaderLineLevel = 0;

    private float miterLimit = 10f;
    private float[] dashPattern = {10f};
    private float dashPhase = 5f;
    private final Stroke leaderLineStroke = new BasicStroke( 1f, BasicStroke.CAP_BUTT,
                                                             BasicStroke.JOIN_MITER, miterLimit,
                                                             dashPattern, dashPhase );

    /**
     * Constructor
     *
     * @param clock
     */
    public AlphaDecayModule( IClock clock ) {
        super( SimStrings.get( "ModuleTitle.AlphaDecayModule" ), clock, EnergyProfileGraphic.TOTAL_ENERGY );
    }

    /**
     *
     */
    protected void init() {
        super.init();

        setClockControlPanel( new MyClockControlPanel() );

        physicalPanel = new AlphaDecayPhysicalPanel( getClock(), (NuclearPhysicsModel)getModel() );
        super.setPhysicalPanel( physicalPanel );
        addPhysicalPanel( physicalPanel );
        alphaDecayControlPanel = new AlphaDecayControlPanel( this );
        super.addControlPanelElement( alphaDecayControlPanel );
        physicalPanel.setOrigin( new Point2D.Double( 0, -150 ) );

        // Add a listener that will add and remove energy profiles from the energy profile panel
        NuclearPhysicsModel model = (NuclearPhysicsModel)getModel();
        model.addNucleusListener( new NuclearPhysicsModel.NucleusListener() {
            public void nucleusAdded( NuclearPhysicsModel.ChangeEvent event ) {
                if( event.getNucleus() instanceof ProfileableNucleus ) {
                    getEnergyProfilePanel().addEnergyProfile( (ProfileableNucleus)event.getNucleus(), EnergyProfileGraphic.TOTAL_ENERGY );
                }
            }

            public void nucleusRemoved( NuclearPhysicsModel.ChangeEvent event ) {
                if( event.getNucleus() instanceof ProfileableNucleus ) {
                    getEnergyProfilePanel().removeEnergyProfile( ( (ProfileableNucleus)event.getNucleus() ).getEnergyProfile() );
                }
            }
        } );

        // Start the module. This creates the requisite nucleus, alpha particles, etc.
        start();
    }

    protected java.util.List getLegendClasses() {
        LegendPanel.LegendItem[] legendClasses = new LegendPanel.LegendItem[]{
                LegendPanel.NEUTRON,
                LegendPanel.PROTON,
                LegendPanel.ALPHA_PARTICLE,
                LegendPanel.Po210,
                LegendPanel.Pb206
        };
        return Arrays.asList( legendClasses );
    }

    public void start() {

        // todo: combine these calls
        Polonium211 nucleus = new Polonium211( new Point2D.Double( 0, 0 ), (NuclearPhysicsModel)getModel() );
        setNucleus( nucleus );
        addNucleus( nucleus );

        nucleus.addDecayListener( this );

        // Add the nucleus' jumping alpha particles to the model and the panels
        for( int i = 0; i < nucleus.getAlphaParticles().length; i++ ) {
            addAlphaParticle( nucleus.getAlphaParticles()[i], getNucleus() );
        }
        addRingGraphic( getNucleus() );
        alphaDecayControlPanel.startTimer();
    }

    public void stop() {
        Nucleus nucleus = getNucleus();
        getModel().removeModelElement( nucleus );

        ( (NuclearPhysicsModel)getModel() ).removeNuclearParticles();

        getEnergyProfilePanel().removeAllAlphaParticles();
        getEnergyProfilePanel().removeAllPotentialProfiles();
        getEnergyProfilePanel().removeGraphic( leaderLines );
        getPhysicalPanel().removeGraphic( ringGraphic );
        getPhysicalPanel().removeGraphic( leaderLines );

        alphaDecayControlPanel.stopTimer();
    }

    protected void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        // The following line is inserted to try to solve a bug Wendy found, that show's up
        // only on her computer. If she hits the "Reset" button before alpha decay has happened,
        // the new alpha particles appear first at (0,0) on the profile panel, rather than at
        // the bottom of the potential well.
        alphaParticle.stepInTime( 0 );
        this.getModel().addModelElement( alphaParticle );
        physicalPanel.addAlphaParticle( alphaParticle );
        getEnergyProfilePanel().addAlphaParticle( alphaParticle, nucleus );
    }

    private void addRingGraphic( ProfileableNucleus nucleus ) {
        // Add a ring around the nucleus to show where its alpha decay radius is
        setRingAttributes( nucleus );
        final Stroke ringStroke = new BasicStroke( 2f );
        ringGraphic = new PhetGraphic() {
            public void paint( Graphics2D g ) {
                if( alphaRing != null ) {
                    GraphicsUtil.setAntiAliasingOn( g );
                    GraphicsUtil.setAlpha( g, 0.4 );
                    g.setColor( EnergyProfileGraphic.potentialProfileColor );
//                    g.setColor( Color.blue );
                    g.setStroke( ringStroke );
                    g.draw( alphaRing );
                    GraphicsUtil.setAlpha( g, 1 );
                }
            }

            protected Rectangle determineBounds() {
                if( alphaRing != null ) {
                    return alphaRing.getBounds();
                }
                else {
                    return new Rectangle();
                }
            }
        };
        this.getPhysicalPanel().addOriginCenteredGraphic( ringGraphic, ringLevel );

        // Add leader lines from the ring up to the profile
        leaderLines = new PhetGraphic() {
            public void paint( Graphics2D g ) {
                if( leaderLine1 != null && leaderLine2 != null ) {
                    g.setColor( Color.black );
                    g.setStroke( leaderLineStroke );
                    GraphicsUtil.setAlpha( g, 0.4 );
                    g.draw( leaderLine1 );
                    g.draw( leaderLine2 );
                    GraphicsUtil.setAlpha( g, 1 );
                }
            }

            /// Note: possible performance problem
            protected Rectangle determineBounds() {
                return leaderLine1.getBounds().union( leaderLine2.getBounds() );
            }
        };
//        this.getPhysicalPanel().addOriginCenteredGraphic( leaderLines, leaderLineLevel );
        this.getEnergyProfilePanel().addOriginCenteredGraphic( leaderLines );
    }

    private void setRingAttributes( ProfileableNucleus nucleus ) {
        if( nucleus.getEnergyProfile().getAlphaDecayX() > 0 ) {
            double radius = Math.abs( nucleus.getEnergyProfile().getAlphaDecayX() );
            double x = getNucleus().getPosition().getX() - radius;
            double y = getNucleus().getPosition().getY() - radius;
            alphaRing = new Ellipse2D.Double( x, y, radius * 2, radius * 2 );
            leaderLine1 = new Line2D.Double( x, -1000, x, 1000 );
            leaderLine2 = new Line2D.Double( x + radius * 2, -1000, x + radius * 2, 1000 );
        }
        else {
            alphaRing = null;
            leaderLine1 = null;
            leaderLine2 = null;
        }
    }

    public void setRingAttributes() {
        setRingAttributes( getNucleus() );
    }

    private class MyClockControlPanel extends JPanel {
        JButton rewindToDecayBtn;
        AlphaDecaySnapshot snapshot;

        public MyClockControlPanel() {
            addChangeListener( new AlphaDecayModule.ChangeListener() {
                public void decayOccurred( AlphaDecayModule.ChangeEvent event, AlphaDecaySnapshot alphaDecaySnapshot ) {
                    snapshot = alphaDecaySnapshot;
                    rewindToDecayBtn.setEnabled( true );
                }
            } );

            try {
                Image image = ImageLoader.loadBufferedImage( ClockControlPanel.IMAGE_REWIND );
                Icon icon = new ImageIcon( image );
                rewindToDecayBtn = new JButton( SimStrings.get("AlphaDecayControlPanel.Rewind"), icon );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
            rewindToDecayBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if( snapshot != null ) {
                        getClock().pause();
                        snapshot.restore();
                        rewindToDecayBtn.setEnabled( false );
                    }
                }
            } );
            rewindToDecayBtn.setEnabled( false );
            add( new ClockControlPanel( getClock() ) );
            add( rewindToDecayBtn );
        }

    }
    //--------------------------------------------------------------------------------------------------
    // Implementation of DecayListener
    //--------------------------------------------------------------------------------------------------

    public void alphaDecay( AlphaDecayProducts decayProducts, AlphaDecaySnapshot alphaDecaySnapshot ) {

        alphaDecayControlPanel.stopTimer();

        changeListenerProxy.decayOccurred( new ChangeEvent( this ), alphaDecaySnapshot );

        //Remove old nucleus
        getModel().removeModelElement( decayProducts.getParent() );

        // Bind the alpha particles to the daughter nucleus
        Polonium211 u235 = (Polonium211)decayProducts.getParent();
        AlphaParticle[] alphaParticles = u235.getAlphaParticles();
        for( int i = 0; i < alphaParticles.length; i++ ) {
            AlphaParticle alphaParticle = alphaParticles[i];
            alphaParticle.setNucleus( decayProducts.getDaughter() );
        }

        // Add the daughter nucleus
        getModel().addModelElement( decayProducts.getDaughter() );

        // Set the size of the alpha decay threshold ring and the positions of the
        // leader lines
        setRingAttributes( decayProducts.getDaughter() );

        // Make a bang!
        Kaboom kaboom = new Kaboom( new Point2D.Double(), 25, 300, getPhysicalPanel(), getModel() );
        getPhysicalPanel().addGraphic( kaboom );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of abstract methods
    //--------------------------------------------------------------------------------------------------

    protected String getPotentialEnergyLegend() {
        return SimStrings.get( "PotentialProfilePanel.legend.AlphaParticlePotentialEnergy" );
    }

    //--------------------------------------------------------------------------------------------------
    // Implementation of PredecayListener
    //--------------------------------------------------------------------------------------------------

    public void alphaDecayOnNextTimeStep() {
//        changeListenerProxy.preDecayOccured( new ChangeEvent( this ) );
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------
    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }
    }

    public interface ChangeListener extends EventListener {
        void decayOccurred( ChangeEvent event, AlphaDecaySnapshot alphaDecaySnapshot );
    }
}
