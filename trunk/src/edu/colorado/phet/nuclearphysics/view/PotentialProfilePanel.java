// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanel
 * Class: edu.colorado.phet.nuclearphysics.view
 * User: Ron LeMaster
 * Date: Feb 28, 2004
 * Time: 6:03:01 AM
 */
// todo: Apply a model transform to this panel, so we don't have to
// work in view coordinates all the time.

/**
 * Class: PotentialProfilePanel
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
import edu.colorado.phet.common.view.graphics.ShapeGraphic;
import edu.colorado.phet.coreadditions.AlphaSetter;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.DecayNucleus;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
            AlphaSetter.set( graphics, 0.5 );
        }

        public void revert( Graphics2D graphics ) {
            graphics.setComposite( orgComposite );
        }
    };
    private static GraphicsSetup decayProductGraphicsSetup = new GraphicsSetup() {
        public void setup( Graphics2D graphics ) {
            AlphaSetter.set( graphics, 0.8 );
        }
    };
    private Rectangle2D.Double nucleusBackground;
    private Line2D.Double dividingLine;

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
    private PotentialProfile potentialProfile;
    private Point2D.Double origin;
    private Point2D.Double strLoc = new Point2D.Double();
    private Line2D.Double xAxis = new Line2D.Double();
    private Line2D.Double yAxis = new Line2D.Double();
    private AffineTransform profileTx = new AffineTransform();
    private AffineTransform originTx = new AffineTransform();
    private AffineTransform wellTx = new AffineTransform();
    private ArrayList wellParticles = new ArrayList();

    public PotentialProfilePanel( PotentialProfile potentialProfile ) {
        origin = new Point2D.Double( 250, 250 );
        this.setBackground( backgroundColor );
        this.potentialProfile = potentialProfile;
        profileGraphic = new PotentialProfileGraphic( potentialProfile );

        nucleusBackground = new Rectangle2D.Double();
        this.addGraphic( new ShapeGraphic( nucleusBackground, new Color( 255, 255, 220 ) ), 0 );
        dividingLine = new Line2D.Double();
        this.addGraphic( new ShapeGraphic( dividingLine, Color.black, new BasicStroke( 1f ) ), 0.1 );
    }

    private synchronized void addNucleusGraphic( NucleusGraphic nucleusGraphic ) {
        this.addGraphic( nucleusGraphic, nucleusLayer, originTx, nucleusGraphicsSetup );
    }

    public synchronized void addOriginCenteredGraphic( Graphic graphic ) {
        this.addGraphic( graphic, originTx );
    }

    public PotentialProfile getPotentialProfile() {
        return potentialProfile;
    }

    protected synchronized void paintComponent( Graphics graphics ) {
        Graphics2D g2 = (Graphics2D)graphics;

        // Center the profile in the panel
        origin.setLocation( this.getWidth() / 2, this.getHeight() * 1 / 4 );
        originTx.setToTranslation( origin.getX(), origin.getY() );
        profileTx.setToTranslation( origin.getX(),
                                    origin.getY() + 600 );
        wellTx.setToTranslation( origin.getX(), origin.getY() + 600 - potentialProfile.getWellPotential() - AlphaParticle.RADIUS );

        // Paint the background
        nucleusBackground.setRect( this.getX(), this.getY(), this.getWidth(), this.getHeight() * 2 / 5 );
        dividingLine.setLine( 0, this.getHeight() * 2 / 5, this.getWidth(), this.getHeight() * 2 / 5 );

        // Draw everything that isn't special to this panel
        AlphaSetter.set( g2, 1 );
        g2.setColor( backgroundColor );
        super.paintComponent( g2 );

//        Color orgColor = g2.getColor();
//        g2.setColor( new Color( 255, 255, 220 ) );
//        g2.draw( nucleusBackground );
//        g2.setColor( orgColor );


        // Draw axes
        drawAxes( g2 );

        // Draw "ghost" alpah particles in the potential well
//        for( int i = 0; i < 0; i++ ) {
        for( int i = 0; i < wellParticles.size(); i++ ) {
            NucleusGraphic nucleusGraphic = (NucleusGraphic)wellParticles.get( i );
            double xStat = nucleusGraphic.getNucleus().getLocation().getX();
            double yStat = nucleusGraphic.getNucleus().getLocation().getY();
            double d = ( Math.sqrt( xStat * xStat + yStat * yStat ) ) * ( xStat > 0 ? 1 : -1 );
            AlphaSetter.set( g2, ghostAlpha );
            AffineTransform orgTx = g2.getTransform();
            g2.transform( wellTx );
            nucleusGraphic.paint( g2, (int)d, 0 );
            AlphaSetter.set( g2, 1 );
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
            AlphaSetter.set( g2, ghostAlpha );
            decayGraphic.paint( g2, (int)( x ), -(int)y );
            AlphaSetter.set( g2, 1 );
            g2.setTransform( orgTx );
            g2.transform( originTx );
            decayGraphic.paint( g2, (int)nucleus.getLocation().getX(), (int)nucleus.getLocation().getY() );
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

    public synchronized void setNucleus( Nucleus nucleus ) {
        this.addNucleusGraphic( new NucleusGraphic( nucleus ) );
        setPotentialProfile( nucleus.getPotentialProfile() );
    }

    public void setPotentialProfile( PotentialProfile potentialProfile ) {
        this.potentialProfile = potentialProfile;
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( potentialProfile );
        ppg.setOrigin( new Point2D.Double( 0, 0 ) );
        addGraphic( ppg, nucleusLayer, profileTx );
    }

    public synchronized void addDecayProduct( Nucleus decayNucleus ) {
        this.decayGraphic = new NucleusGraphic( decayNucleus );
    }

    public synchronized void addAlphaParticle( AlphaParticle alphaParticle ) {
        NucleusGraphic graphic = new NucleusGraphic( alphaParticle );
        this.addOriginCenteredGraphic( graphic );

        // Add a graphic up in the potential well for the graphic
        NucleusGraphic wellGraphic = new NucleusGraphic( alphaParticle );
        wellParticles.add( wellGraphic );
    }

    public void clear() {
        this.decayGraphic = null;
        this.removeAllGraphics();
    }
}
