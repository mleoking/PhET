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
import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
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
    //    private static RevertableGraphicsSetup nucleusGraphicsSetup = new RevertableGraphicsSetup() {
    //        private Composite orgComposite;
    //
    //        public void setup( Graphics2D graphics ) {
    //            orgComposite = graphics.getComposite();
    //            GraphicsUtil.setAlpha( graphics, 0.5 );
    //        }
    //
    //        public void revert( Graphics2D graphics ) {
    //            graphics.setComposite( orgComposite );
    //        }
    //    };
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

    private static GeneralPath arrowhead = new GeneralPath();

    static {
        arrowhead.moveTo( 0, 0 );
        arrowhead.lineTo( 5, 10 );
        arrowhead.lineTo( -5, 10 );
        arrowhead.closePath();
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
    // Maps potential profiles to their graphics
    private HashMap potentialProfileMap = new HashMap();
    // Maps potential profiles to the nucleus graphics associated with them
    private HashMap profileNucleusMap = new HashMap();
    private Point2D.Double origin;
    private Point2D.Double strLoc = new Point2D.Double();
    private Line2D.Double xAxis = new Line2D.Double();
    private Line2D.Double yAxis = new Line2D.Double();
    private AffineTransform profileTx = new AffineTransform();
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

        // Draw everything that isn't special to this panel. This includes the
        // profiles themselves
        GraphicsUtil.setAlpha( g2, 1 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

        // Draw axes
        drawAxes( g2 );

        // Draw nuclei
        Iterator nucleusIt = profileNucleusMap.keySet().iterator();
        while( nucleusIt.hasNext() ) {
            Nucleus nucleus = (Nucleus)nucleusIt.next();
            Graphic ng = (Graphic)profileNucleusMap.get( nucleus );
            AffineTransform orgTx = g2.getTransform();
            AffineTransform nucleusTx = new AffineTransform();
            nucleusTx.concatenate( profileTx );
            nucleusTx.translate( 0, -nucleus.getPotential() );
            nucleusTx.scale( 0.5, 0.5 );
            nucleusTx.translate( nucleus.getPosition().getX(), -nucleus.getPosition().getY() );
            g2.transform( nucleusTx );
            ng.paint( g2 );
            g2.setTransform( orgTx );
        }

        // Draw "ghost" alpha particles in the potential well
        Iterator wellParticlesIt = wellParticles.keySet().iterator();
        while( wellParticlesIt.hasNext() ) {
            AlphaParticleGraphic alphaParticleGraphic = (AlphaParticleGraphic)wellParticlesIt.next();
            double xStat = alphaParticleGraphic.getNucleus().getPosition().getX();
            double yStat = alphaParticleGraphic.getNucleus().getPosition().getY();
            double d = ( Math.sqrt( xStat * xStat + yStat * yStat ) ) * ( xStat > 0 ? 1 : -1 );
            GraphicsUtil.setAlpha( g2, ghostAlpha );
            AffineTransform orgTx = g2.getTransform();
            g2.transform( profileTx );
            double dy = -( (AlphaParticle)alphaParticleGraphic.getNucleus() ).getPotential();
            alphaParticleGraphic.paint( g2, (int)d, (int)dy );
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setTransform( orgTx );
        }

        GraphicsUtil.setAlpha( g2, 1 );

    }

    private void drawAxes( Graphics2D g2 ) {
        AffineTransform orgTx = g2.getTransform();
        int arrowOffset = 20;

        g2.setColor( axisColor );
        g2.setStroke( axisStroke );

        int xAxisMin = -this.getWidth() / 2 + arrowOffset;
        int xAxisMax = this.getWidth() / 2 - arrowOffset;
        double yRat = profileTx.getTranslateY() / this.getHeight();

        int yAxisMin = -(int)( this.getHeight() * yRat ) + arrowOffset;
        int yAxisMax = yAxisMin + this.getHeight() - 2 * arrowOffset;

        xAxis.setLine( xAxisMin, 0, xAxisMax, 0 );
        yAxis.setLine( 0, yAxisMin, 0, yAxisMax );
        g2.transform( profileTx );
        g2.draw( xAxis );
        g2.draw( yAxis );
        AffineTransform tempTx = g2.getTransform();
        g2.transform( AffineTransform.getTranslateInstance( xAxisMax, 0 ) );
        g2.transform( AffineTransform.getRotateInstance( Math.PI / 2 ) );
        g2.fill( arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( xAxisMin, 0 ) );
        g2.transform( AffineTransform.getRotateInstance( -Math.PI / 2 ) );
        g2.fill( arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( 0, yAxisMin ) );
        //        g2.transform( AffineTransform.getRotateInstance( 0 );
        g2.fill( arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( 0, yAxisMax ) );
        g2.transform( AffineTransform.getRotateInstance( Math.PI ) );
        g2.fill( arrowhead );

        g2.setTransform( orgTx );

        // Draw labels
        g2.setFont( axisLabelFont );
        g2.setColor( Color.black );
        FontMetrics fm = g2.getFontMetrics();

        // todo: replace the 250 in the next line with something better
        strLoc.setLocation( origin.getX(), profileTx.getTranslateY() - 220 );
        AffineTransform strTx = rotateInPlace( -Math.PI / 2, strLoc.getX(), strLoc.getY() );
        g2.transform( strTx );
        g2.drawString( yAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
        g2.setTransform( orgTx );
        strLoc.setLocation( this.getWidth() / 2 + 10,
                            //        strLoc.setLocation( this.getWidth() - fm.stringWidth( xAxisLabel ) - 10,
                            profileTx.getTranslateY() + fm.getHeight() );
        g2.drawString( xAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
    }

    // todo: replace calls to this with call to addPotentialProfile
    public synchronized void addNucleus( Nucleus nucleus ) {
        addPotentialProfile( nucleus );
    }

    public synchronized void removeNucleus( Nucleus nucleus ) {
        removePotentialProfile( nucleus.getPotentialProfile() );
    }

    public void addPotentialProfile( Nucleus nucleus ) {
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( nucleus );
        nucleus.getPotentialProfile().addObserver( ppg );
        ppg.setOrigin( new Point2D.Double( 0, 0 ) );
        potentialProfileMap.put( nucleus.getPotentialProfile(), ppg );
        TxGraphic txg = new TxGraphic( ppg, profileTx );
        addGraphic( txg, nucleusLayer );
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

    public void removeAllGraphics() {
        profileNucleusMap.clear();
        potentialProfileMap.clear();
        wellParticles.clear();
        super.removeAllGraphics();
    }

    public void addOriginCenteredGraphic( Graphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, profileTx );
        this.addGraphic( txg );
    }

    public void addNucleusGraphic( Nucleus nucleus ) {
        NucleusGraphic ng = new NucleusGraphic( nucleus );
        profileNucleusMap.put( nucleus, ng );
    }

    public void removeNucleusGraphic( Nucleus nucleus ) {
        profileNucleusMap.remove( nucleus );
    }

    /**
     * Adds a nucleus and its potential profile to the panel. The color
     * parameter specifies the color in which the profile is to be drawn.
     * If it is null, no profile is drawn.
     *
     * @param nucleus
     * @param color
     */
    public void addNucleus( Nucleus nucleus, Color color ) {
        this.addNucleus( nucleus );
        if( color == null ) {
            removePotentialProfile( nucleus.getPotentialProfile() );
        }
        else {
            PotentialProfileGraphic ppg = (PotentialProfileGraphic)potentialProfileMap.get( nucleus.getPotentialProfile() );
            ppg.setColor( color );
        }
    }

}
