/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.view.ApparatusPanel2;
import edu.colorado.phet.common.view.GraphicsSetup;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Iterator;


public class PotentialProfilePanel extends ApparatusPanel2 {
//public class PotentialProfilePanel extends TxApparatusPanel {

    private static Color axisColor = new Color( 100, 100, 100 );
    private static Stroke axisStroke = new BasicStroke( 1f );
    private static Color backgroundColor = new Color( 255, 255, 255 );
    private static String xAxisLabel = SimStrings.get( "PotentialProfilePanel.XAxisLabel" );
    private static String yAxisLabel = SimStrings.get( "PotentialProfilePanel.YAxisLabel" );
    private static Font axisLabelFont;
    private static float ghostAlpha = 1f;
    //    private static float ghostAlpha = 0.6f;
    private static double profileLayer = 10;
    private static double nucleusLayer = 20;
    private static AffineTransform atx = new AffineTransform();
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
    private boolean init = false;
    private Rectangle orgBounds;

    /**
     * @param clock
     */
    public PotentialProfilePanel( AbstractClock clock ) {
        super( clock );
        this.setBackground( backgroundColor );

        addComponentListener( new ComponentAdapter() {
            public void componentResized( ComponentEvent e ) {
//                if( !init ) {
                orgBounds = new Rectangle( getBounds() );
                origin = new Point2D.Double( getWidth() / 2, getHeight() * 0.8 );
                //            origin = new Point2D.Double( 250, 250 );
                profileTx.setToTranslation( origin.getX(),
                                            origin.getY() );
                System.out.println( "profileTx = " + profileTx );
                init = true;
//                }
            }

            public void componentShown( ComponentEvent e ) {
            }
        } );
    }

    /**
     * todo: does this need to be synchronized anymore?
     *
     * @param graphics
     */
    protected synchronized void paintComponent( Graphics graphics ) {

        Graphics2D g2 = (Graphics2D)graphics;
        GraphicsState gs = new GraphicsState( g2 );

//        // Set the transform that will center the origin
        g2.transform( profileTx );

        // Draw everything that isn't special to this panel. This includes the
        // profiles themselves
        GraphicsUtil.setAlpha( g2, 1 );
        g2.setColor( backgroundColor );
//        GraphicsState gs2 = new GraphicsState( g2 );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
        super.paintComponent( g2 );
//        gs2.restoreGraphics();

        // Set the transform that will center the origin
//        g2.transform( profileTx );


        // Draw axes
        AffineTransform gTx = getGraphicTx();
//        g2.transform( gTx );
        drawAxes( g2 );

        // Draw nuclei
        Iterator nucleusIt = profileNucleusMap.keySet().iterator();
        while( nucleusIt.hasNext() ) {
            Nucleus nucleus = (Nucleus)nucleusIt.next();
            PhetGraphic ng = (PhetGraphic)profileNucleusMap.get( nucleus );
            AffineTransform orgTx = g2.getTransform();
            AffineTransform nucleusTx = new AffineTransform();
//            nucleusTx.concatenate( profileTx );
//            nucleusTx.translate( 0, -nucleus.getPotential() );
//            nucleusTx.scale( 0.5, 0.5 );
//            nucleusTx.translate( nucleus.getPosition().getX(), -nucleus.getPosition().getY() );
//            g2.transform( nucleusTx );
            ng.setLocation( 0, 0 );
//            ng.setLocation( (int)nucleus.getPosition().getX(), (int)-nucleus.getPosition().getY() );
            ng.paint( g2 );
//            g2.setTransform( orgTx );
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
//            g2.transform( profileTx );
            double dy = -( (AlphaParticle)alphaParticleGraphic.getNucleus() ).getPotential();
            alphaParticleGraphic.setLocation( (int)d, (int)dy );
            alphaParticleGraphic.paint( g2 );
//            alphaParticleGraphic.paint( g2, (int)d, (int)dy );
//            GraphicsUtil.setAlpha( g2, 1 );
//            g2.setTransform( orgTx );
        }

        gs.restoreGraphics();
    }

    /**
     * Draw the axes
     *
     * @param g2
     */
    private void drawAxes( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );
        AffineTransform orgTx = g2.getTransform();
        int arrowOffset = 20;

//        g2.transform( profileTx );
        g2.setColor( axisColor );
        g2.setStroke( axisStroke );

        int xAxisMin = -this.getWidth() / 2 + arrowOffset;
        int xAxisMax = this.getWidth() / 2 - arrowOffset;

        int yAxisMin = -(int)( profileTx.getTranslateY() ) + arrowOffset;
        int yAxisMax = (int)orgBounds.getHeight() - (int)profileTx.getTranslateY() - 2 * arrowOffset;
//        int yAxisMin = -(int)( profileTx.getTranslateY() ) + arrowOffset;
//        int yAxisMax = (int)orgBounds.getHeight() - (int)profileTx.getTranslateY() - 2 * arrowOffset;


        xAxis.setLine( xAxisMin, 0, xAxisMax, 0 );
        yAxis.setLine( 0, yAxisMin, 0, yAxisMax );
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

        // Draw labels
        g2.setFont( axisLabelFont );
        g2.setColor( Color.black );
        FontMetrics fm = g2.getFontMetrics();

        strLoc.setLocation( profileTx.getTranslateX() + fm.getHeight(), 0 );
        strLoc.setLocation( 10, 120 );
        AffineTransform strTx = rotateInPlace( -Math.PI / 2, strLoc.getX(), strLoc.getY() );
        g2.transform( strTx );
        g2.drawString( yAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );
        g2.setTransform( orgTx );
        strLoc.setLocation( profileTx.getTranslateX() + 10,
                            profileTx.getTranslateY() + fm.getHeight() );
        g2.drawString( xAxisLabel, (int)strLoc.getX(), (int)strLoc.getY() );

        gs.restoreGraphics();
    }

    public void addPotentialProfile( Nucleus nucleus ) {
        PotentialProfileGraphic ppg = new PotentialProfileGraphic( this, nucleus );
        nucleus.getPotentialProfile().addObserver( ppg );
        ppg.setOrigin( new Point2D.Double( 200, 200 ) );
//        ppg.setOrigin( new Point2D.Double( 0, 0 ) );
//        TxGraphic txg = new TxGraphic( ppg, profileTx );
//        potentialProfileMap.put( nucleus.getPotentialProfile(), txg );
        potentialProfileMap.put( nucleus.getPotentialProfile(), ppg );
        addGraphic( ppg, nucleusLayer );
//        addGraphic( txg, nucleusLayer );
    }

    public void removePotentialProfile( PotentialProfile potentialProfile ) {
        PhetGraphic ppg = (PhetGraphic)potentialProfileMap.get( potentialProfile );
//        Graphic ppg = (Graphic)potentialProfileMap.get( potentialProfile );
        removeGraphic( ppg );
        potentialProfileMap.remove( potentialProfile );
    }

    public void removeAllPotentialProfiles() {
        Iterator it = potentialProfileMap.values().iterator();
        while( it.hasNext() ) {
            PhetGraphic ppg = (PhetGraphic)it.next();
            this.removeGraphic( ppg );
        }
        potentialProfileMap.clear();
    }

    /**
     * todo: does this need to be synchronized
     *
     * @param alphaParticle
     * @param nucleus
     */
    public synchronized void addAlphaParticle( final AlphaParticle alphaParticle, Nucleus nucleus ) {
        // Add an alpha particle for the specified nucleus
        final AlphaParticleGraphic alphaParticleGraphic = new AlphaParticleGraphic( this, alphaParticle );
        wellParticles.put( alphaParticleGraphic, nucleus );
        alphaParticle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                PotentialProfilePanel.this.removeGraphic( alphaParticleGraphic );
                alphaParticle.removeListener( this );
                wellParticles.remove( alphaParticleGraphic );
            }
        } );
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

    public void addOriginCenteredGraphic( PhetGraphic graphic ) {
//    public void addOriginCenteredGraphic( Graphic graphic ) {
//        TxGraphic txg = new TxGraphic( graphic, profileTx );
        this.addGraphic( graphic );
//        this.addGraphic( txg );
    }

    public void addNucleusGraphic( final Nucleus nucleus ) {
        final NucleusGraphic ng = new NucleusGraphic( this, nucleus );
        //        TxGraphic txg = new TxGraphic( ng, );
        profileNucleusMap.put( nucleus, ng );
        nucleus.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                profileNucleusMap.remove( nucleus );
            }
        } );
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
        this.addPotentialProfile( nucleus );
        if( color == null ) {
            removePotentialProfile( nucleus.getPotentialProfile() );
        }
        else {
            PotentialProfileGraphic ppg = (PotentialProfileGraphic)potentialProfileMap.get( nucleus.getPotentialProfile() );
            ppg.setColor( color );
        }
    }

}
