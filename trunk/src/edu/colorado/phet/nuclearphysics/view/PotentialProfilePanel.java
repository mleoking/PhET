// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanelOld
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */

/**
 * Class: PotentialProfilePanelOld
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.view.ApparatusPanel;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.RevertableGraphicsSetup;
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.DecayNucleus;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;

public class PotentialProfilePanel extends ApparatusPanel {

    //
    // Statics
    //
    private static Color axisColor = new Color( 100, 100, 100 );
    private static Stroke axisStroke = new BasicStroke( 1f );
    private static Color backgroundColor = new Color( 255, 255, 255 );
    private static String xAxisLabel = "Distance from Nucleus Center";
    private static String yAxisLabel = "Potential Energy";
    private static Font axisLabelFont;
    private static float ghostAlpha = 0.2f;
    private static double profileLayer = 10;
    private static double nucleusLayer = 20;
    private static AffineTransform atx = new AffineTransform();
    private static RevertableGraphicsSetup nucleusGraphicsSetup = new RevertableGraphicsSetup() {
        private Composite orgComposite;

        public void setup( Graphics2D graphics ) {
            orgComposite = graphics.getComposite();
            GraphicsUtil.setAlpha( graphics, 0.5 );
        }

        public void revert( Graphics2D graphics ) {
            graphics.setComposite( orgComposite );
        }
    };
    private static GraphicsSetup decayProductGraphicsSetup = new GraphicsSetup() {
        public void setup( Graphics2D graphics ) {
            GraphicsUtil.setAlpha( graphics, 0.8 );
        }
    };

    static {
        String family = "SansSerif";
        int style = Font.BOLD;
        int size = 12;
        axisLabelFont = new Font( family, style, size );
    }

    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        atx.setToIdentity();
        atx.translate( x, y );
        atx.scale( scale, scale );
        atx.translate( -x, -y );
        return atx;
    }

    public static AffineTransform rotateInPlace( double theta, double x, double y ) {
        atx.setToIdentity();
        atx.translate( x, y );
        atx.rotate( theta );
        atx.translate( -x, -y );
        return atx;
    }

    //
    // Instance fields and methods
    //
    private NucleusGraphic decayGraphic;
    private PotentialProfileGraphic profileGraphic;
    private HashMap potentialProfileMap = new HashMap();
    private Point2D.Double origin;
    private Point2D.Double strLoc = new Point2D.Double();
    private Line2D.Double xAxis = new Line2D.Double();
    private Line2D.Double yAxis = new Line2D.Double();
    private AffineTransform profileTx = new AffineTransform();
    private HashMap wellTxs = new HashMap();
    private HashMap wellParticles = new HashMap();

    public PotentialProfilePanel() {
        origin = new Point2D.Double( 250, 250 );
        this.setBackground( backgroundColor );
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Center the profile in the panel
        origin.setLocation( this.getWidth() / 2, this.getHeight() * 0.8 );
        double scale = 1;
        profileTx.setToTranslation( origin.getX(),
                                    origin.getY() );

        // Set up all the well transforms. These are based on the x location of
        // the nucleus on which each is based
        Iterator wellTxIt = wellTxs.keySet().iterator();
        while( wellTxIt.hasNext() ) {
            Nucleus nucleus = (Nucleus)wellTxIt.next();
            AffineTransform wellTx = (AffineTransform)wellTxs.get( nucleus );
            wellTx.setToIdentity();
            double x = origin.getX() - nucleus.getLocation().getX();
            double y = origin.getY() - nucleus.getPotentialProfile().getWellPotential() - AlphaParticle.RADIUS;
            wellTx.translate( x, y );
        }

        // Draw everything that isn't special to this panel
        GraphicsUtil.setAlpha( g2, 1 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        // Draw axes
        drawAxes( g2 );

        // Draw "ghost" alpah particles in the potential well
        Iterator wellParticlesIt = wellParticles.keySet().iterator();
        while( wellParticlesIt.hasNext() ) {
            AlphaParticleGraphic nucleusGraphic = (AlphaParticleGraphic)wellParticlesIt.next();
            double xStat = nucleusGraphic.getNucleus().getLocation().getX();
            double yStat = nucleusGraphic.getNucleus().getLocation().getY();
            double d = ( Math.sqrt( xStat * xStat + yStat * yStat ) ) * ( xStat > 0 ? 1 : -1 );
            GraphicsUtil.setAlpha( g2, ghostAlpha );
            AffineTransform orgTx = g2.getTransform();
            Nucleus nucleus = (Nucleus)wellParticles.get( nucleusGraphic );
            g2.transform( (AffineTransform)wellTxs.get( nucleus ) );
            nucleusGraphic.paint( g2, (int)d, 0 );
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setTransform( orgTx );
        }

        if( decayGraphic != null ) {
            AffineTransform orgTx = g2.getTransform();
            DecayNucleus nucleus = (DecayNucleus)decayGraphic.getNucleus();
            // Note: -y is needed because we're currently working in view coordinates. The profile is a cubic
            // in view coordinates
            double y = Math.max( nucleus.getPotentialEnergy(), 0 );
            double x = nucleus.getLocation().getX();

            // Draw a ghost coming down the profile first, then the real thing on the x axis
            g2.transform( profileTx );
            GraphicsUtil.setAlpha( g2, ghostAlpha );
            decayGraphic.paint( g2, (int)( x ), -(int)y );
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setTransform( orgTx );
        }
    }

    private void drawAxes( Graphics2D g2 ) {
        AffineTransform orgTx = g2.getTransform();

        g2.setColor( axisColor );
        g2.setStroke( axisStroke );
        xAxis.setLine( -this.getWidth(), 0, this.getWidth(), 0 );
        yAxis.setLine( 0, -this.getHeight(), 0, this.getHeight() );
        g2.transform( profileTx );
        g2.draw( xAxis );
        g2.draw( yAxis );
        g2.setTransform( orgTx );

        // Draw labels
        g2.setFont( axisLabelFont );
        g2.setColor( Color.black );
        FontMetrics fm = g2.getFontMetrics();
        strLoc.setLocation( origin.getX(), profileTx.getTranslateY() );
        AffineTransform strTx = rotateInPlace( -Math.PI / 2, strLoc.getX(), strLoc.getY() );
        g2.transform( strTx );
        g2.drawString( yAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
        g2.setTransform( orgTx );
        strLoc.setLocation( this.getWidth() - fm.stringWidth( xAxisLabel ) - 10,
                            profileTx.getTranslateY() + fm.getHeight() );
        g2.drawString( xAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
    }

    public synchronized void addNucleus( Nucleus nucleus ) {
        addPotentialProfile( nucleus );
//        addPotentialProfile( nucleus.getPotentialProfile() );
        AffineTransform wellTx = new AffineTransform();
        wellTx.setToIdentity();
        wellTxs.put( nucleus, new AffineTransform() );

        // Add leader lines from the ring up to the profile
        final Line2D.Double line1 = new Line2D.Double( -nucleus.getPotentialProfile().getAlphaDecayX(),
                                                       -1000,
                                                       -nucleus.getPotentialProfile().getAlphaDecayX(),
                                                       1000 );
        final Line2D.Double line2 = new Line2D.Double( nucleus.getPotentialProfile().getAlphaDecayX(),
                                                       -1000,
                                                       nucleus.getPotentialProfile().getAlphaDecayX(),
                                                       1000 );
        final Stroke leaderLineStroke = new BasicStroke( 1f );
        Graphic leaderLines = new Graphic() {
            public void paint( Graphics2D g ) {
                g.setColor( Color.black );
                g.setStroke( leaderLineStroke );
                GraphicsUtil.setAlpha( g, 0.3 );
                g.draw( line1 );
                g.draw( line2 );
                GraphicsUtil.setAlpha( g, 1 );
            }
        };
        this.addGraphic( leaderLines, profileTx );
    }

    public void addPotentialProfile( Nucleus nucleus ) {
//    public void addPotentialProfile( PotentialProfile potentialProfile ) {
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( nucleus );
        ppg.setOrigin( new Point2D.Double( 0, 0 ) );
        potentialProfileMap.put( nucleus.getPotentialProfile(), ppg );
//        potentialProfileMap.put( potentialProfile, ppg );
        addGraphic( ppg, nucleusLayer, profileTx );
    }

    public void removePotentialProfile( PotentialProfile potentialProfile ) {
        PotentialProfileGraphic ppg = (PotentialProfileGraphic)potentialProfileMap.get( potentialProfile );
        removeGraphic( ppg );
    }

    public synchronized void addDecayProduct( Nucleus decayNucleus ) {
        this.decayGraphic = new NucleusGraphic( decayNucleus );
    }

    public synchronized void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        // Add an alpha particle for the specified nucleus
        AlphaParticleGraphic alphaParticleGraphic = new AlphaParticleGraphic( alphaParticle );
        wellParticles.put( alphaParticleGraphic, nucleus );
    }

    public void clear() {
        this.decayGraphic = null;
        this.removeAllGraphics();
    }
}
