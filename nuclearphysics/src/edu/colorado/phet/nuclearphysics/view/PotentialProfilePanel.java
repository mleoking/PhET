/**
 * Class: PotentialProfilePanel
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
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
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
    private static float ghostAlpha = 1f;
//    private static float ghostAlpha = 0.6f;
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
        profileTx.setToTranslation( origin.getX(),
                                    origin.getY() );

        // Set up all the well transforms. These are based on the x location of
        // the nucleus on which each is based
        // todo: keeping multiple wellTxs may be unnecessary now that the transform
        // does not move alpha particles up to the well
        Iterator wellTxIt = wellTxs.keySet().iterator();
        while( wellTxIt.hasNext() ) {
            Nucleus nucleus = (Nucleus)wellTxIt.next();
            AffineTransform wellTx = (AffineTransform)wellTxs.get( nucleus );
            wellTx.setToIdentity();
            double x = origin.getX() - nucleus.getLocation().getX();
            double y = origin.getY() - AlphaParticle.RADIUS;
            wellTx.translate( x, y );
        }

        // Draw everything that isn't special to this panel
        GraphicsUtil.setAlpha( g2, 1 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        // Draw axes
        drawAxes( g2 );

        // Draw "ghost" alpha particles in the potential well
        Iterator wellParticlesIt = wellParticles.keySet().iterator();
        while( wellParticlesIt.hasNext() ) {
            AlphaParticleGraphic alphaParticleGraphic = (AlphaParticleGraphic)wellParticlesIt.next();
            double xStat = alphaParticleGraphic.getNucleus().getLocation().getX();
            double yStat = alphaParticleGraphic.getNucleus().getLocation().getY();
            double d = ( Math.sqrt( xStat * xStat + yStat * yStat ) ) * ( xStat > 0 ? 1 : -1 );
            GraphicsUtil.setAlpha( g2, ghostAlpha );
            AffineTransform orgTx = g2.getTransform();
            Nucleus nucleus = (Nucleus)wellParticles.get( alphaParticleGraphic );
            g2.transform( (AffineTransform)wellTxs.get( nucleus ) );
            double dy = -( (AlphaParticle)alphaParticleGraphic.getNucleus() ).getPotential();
            alphaParticleGraphic.paint( g2, (int)d, (int)dy );
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setTransform( orgTx );
        }

        GraphicsUtil.setAlpha( g2, 1 );

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

        // todo: replace the 250 in the next line with something better
        strLoc.setLocation( origin.getX(), profileTx.getTranslateY() - 250 );
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
        AffineTransform wellTx = new AffineTransform();
        wellTx.setToIdentity();
        wellTxs.put( nucleus, new AffineTransform() );
    }

    public synchronized void removeNucleus( Nucleus nucleus ) {
        removePotentialProfile( nucleus.getPotentialProfile() );
        wellTxs.remove( nucleus );
    }

    public void addPotentialProfile( Nucleus nucleus ) {
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( nucleus );
        nucleus.getPotentialProfile().addObserver( ppg );
        ppg.setOrigin( new Point2D.Double( 0, 0 ) );
        potentialProfileMap.put( nucleus.getPotentialProfile(), ppg );
        addGraphic( ppg, nucleusLayer, profileTx );
    }

    public void removePotentialProfile( PotentialProfile potentialProfile ) {
        PotentialProfileGraphic ppg = (PotentialProfileGraphic)potentialProfileMap.get( potentialProfile );
        removeGraphic( ppg );
        potentialProfileMap.remove( potentialProfile );
    }

    public void removeAllPotentialProfiles() {
        Iterator it = potentialProfileMap.values().iterator();
        while( it.hasNext() ) {
            PotentialProfileGraphic ppg = (PotentialProfileGraphic)it.next();
            this.removeGraphic( ppg );
        }
        potentialProfileMap.clear();
    }

    public synchronized void addAlphaParticle( AlphaParticle alphaParticle, Nucleus nucleus ) {
        // Add an alpha particle for the specified nucleus
        AlphaParticleGraphic alphaParticleGraphic = new AlphaParticleGraphic( alphaParticle );
        wellParticles.put( alphaParticleGraphic, nucleus );
    }

    public void removeAllAlphaParticles() {
        wellParticles.clear();
    }

    public void clear() {
        this.removeAllGraphics();
    }
}
