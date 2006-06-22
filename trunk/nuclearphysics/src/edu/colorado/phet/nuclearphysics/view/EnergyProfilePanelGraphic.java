/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.nuclearphysics.view;

import edu.colorado.phet.coreadditions.TxGraphic;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.common.view.util.GraphicsState;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.common.view.phetgraphics.PhetTextGraphic2;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.AlphaParticle;
import edu.colorado.phet.nuclearphysics.model.EnergyProfile;
import edu.colorado.phet.nuclearphysics.model.NuclearModelElement;

import java.awt.*;
import java.awt.geom.*;
import java.util.HashMap;
import java.util.Iterator;

/**
 * PotentialProfilePanel
 * <p/>
 * Presents a panel, in the ApparatusPanel, that shows a graph of the potential energy
 * an alpha particle has when it is in a nucleus, and when it decays from a nucleus.
 * <p/>
 * This class takes a ProfileType in its constructor that tells if it is going to show
 * an old-style potential energy curve, or the new-style square sided well profile with
 * a total energy line on it.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

public class EnergyProfilePanelGraphic extends CompositePhetGraphic {

    //----------------------------------------------------------------
    // Class fields and methods
    //----------------------------------------------------------------

    private static Color axisColor = new Color( 100, 100, 100 );
    private static Stroke axisStroke = new BasicStroke( 1f );
    private static Color backgroundColor = new Color( 255, 255, 255 );
    private static String xAxisLabel = SimStrings.get( "PotentialProfilePanel.XAxisLabel" );
    private static String yAxisLabel = SimStrings.get( "PotentialProfilePanel.YAxisLabel" );
    private static Font axisLabelFont;
    private static float ghostAlpha = 1f;
    private static double profileLayer = 10;
    private static double nucleusLayer = 20;
    private static AffineTransform atx = new AffineTransform();

    static {
        String family = "SansSerif";
        int style = Font.BOLD;
        int size = 12;
        EnergyProfilePanelGraphic.axisLabelFont = new Font( family, style, size );
    }

    private static GeneralPath arrowhead = new GeneralPath();

    static {
        EnergyProfilePanelGraphic.arrowhead.moveTo( 0, 0 );
        EnergyProfilePanelGraphic.arrowhead.lineTo( 5, 10 );
        EnergyProfilePanelGraphic.arrowhead.lineTo( -5, 10 );
        EnergyProfilePanelGraphic.arrowhead.closePath();
    }


    public static AffineTransform scaleInPlaceTx( double scale, double x, double y ) {
        EnergyProfilePanelGraphic.atx.setToIdentity();
        EnergyProfilePanelGraphic.atx.translate( x, y );
        EnergyProfilePanelGraphic.atx.scale( scale, scale );
        EnergyProfilePanelGraphic.atx.translate( -x, -y );
        return EnergyProfilePanelGraphic.atx;
    }

    public static AffineTransform rotateInPlace( double theta, double x, double y ) {
        EnergyProfilePanelGraphic.atx.setToIdentity();
        EnergyProfilePanelGraphic.atx.translate( x, y );
        EnergyProfilePanelGraphic.atx.rotate( theta );
        EnergyProfilePanelGraphic.atx.translate( -x, -y );
        return EnergyProfilePanelGraphic.atx;
    }

    //----------------------------------------------------------------
    // Instance fields and methods
    //----------------------------------------------------------------

    private PhetTextGraphic2 title;
    // Maps potential profiles to their graphics
    private HashMap potentialProfileMap = new HashMap();
    // Maps potential profiles to the nucleus graphics associated with them
    private HashMap profileNucleusMap = new HashMap();
    private Point2D origin = new Point2D.Double();
    private Line2D.Double xAxis = new Line2D.Double();
    private Line2D.Double yAxis = new Line2D.Double();
    private AffineTransform profileTx = new AffineTransform();
    private HashMap wellParticles = new HashMap();
    private boolean init = false;
    private Rectangle orgBounds;
    private EnergyProfileGraphic.ProfileType profileType;

    private double width = 800;
    private double height = 300;

    /**
     * Sole constructor
     *
     * @param component
     */
    public EnergyProfilePanelGraphic( Component component, EnergyProfileGraphic.ProfileType profileType ) {
        super( component );
        this.profileType = profileType;

        RoundRectangle2D border = new RoundRectangle2D.Double( 0, 0, width, height, 30, 30 );
        PhetShapeGraphic borderGraphic = new PhetShapeGraphic( component,
                                                               border,
                                                               EnergyProfilePanelGraphic.backgroundColor,
                                                               new BasicStroke( 10 ),
                                                               Color.gray );
        addGraphic( borderGraphic, 0 );
        setClip( border );
    }

    private void setOrgFields() {
        if( !init ) {
            orgBounds = new Rectangle( getBounds() );
            profileTx.setToTranslation( origin.getX(),
                                        origin.getY() );
            init = true;
        }
    }

    public void paint( Graphics2D g2 ) {
        GraphicsState gs = new GraphicsState( g2 );

        // Draw everything that isn't special to this panel. This includes the
        // profiles themselves
        {
            AffineTransform orgTx = g2.getTransform();
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setColor( EnergyProfilePanelGraphic.backgroundColor );
            GraphicsState gs2 = new GraphicsState( g2 );
            g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC );
            super.paint( g2 );
            g2.setTransform( orgTx );

            // Draw axes
            if( profileType == EnergyProfileGraphic.TOTAL_ENERGY ) {
                origin.setLocation( getWidth() / 2, getHeight() / 4 );
            }
            else if( profileType == EnergyProfileGraphic.POTENTIAL_ENERGY ) {
                origin.setLocation( getWidth() / 2, getHeight() * 3 / 4 );
            }
            g2.translate( origin.getX(), origin.getY() );
            drawAxes( g2 );

            gs2.restoreGraphics();

            drawLegend( g2 );
        }

//        g2.setColor( Color.red );
//        g2.drawArc( -3, -3, 6,6, 0, 360 );
//        g2.setColor( Color.green );
//        g2.drawArc( (int)origin.getX() - 3, (int)origin.getY() -3, 6,6, 0, 360 );

        // Draw nuclei
        Iterator nucleusIt = profileNucleusMap.keySet().iterator();
        while( nucleusIt.hasNext() ) {
            Nucleus nucleus = (Nucleus)nucleusIt.next();
            PhetGraphic ng = (PhetGraphic)profileNucleusMap.get( nucleus );
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
            GraphicsUtil.setAlpha( g2, EnergyProfilePanelGraphic.ghostAlpha );
            AffineTransform orgTx = g2.getTransform();
            g2.transform( profileTx );
            double dy = -( ( (AlphaParticle)alphaParticleGraphic.getNucleus() ).getParentNucleusTotalEnergy() );
            alphaParticleGraphic.paint( g2, (int)d, (int)dy );
            GraphicsUtil.setAlpha( g2, 1 );
            g2.setTransform( orgTx );
        }

        gs.restoreGraphics();
    }

    private void drawAxes( Graphics2D g2 ) {
        // Make sure key objects are initialized
        setOrgFields();

        GraphicsState gs = new GraphicsState( g2 );
        AffineTransform orgTx = g2.getTransform();

        int arrowOffset = 20;

        g2.setColor( EnergyProfilePanelGraphic.axisColor );
        g2.setStroke( EnergyProfilePanelGraphic.axisStroke );

        int xAxisMin = -this.getWidth() / 2 + arrowOffset;
        int xAxisMax = this.getWidth() / 2 - arrowOffset;

        int yAxisMin = -(int)( profileTx.getTranslateY() ) + arrowOffset;
        int yAxisMax = (int)orgBounds.getHeight() - (int)profileTx.getTranslateY() - 2 * arrowOffset;

        int yAxisXLoc = -350;
        xAxis.setLine( xAxisMin, 0, xAxisMax, 0 );
        yAxis.setLine( yAxisXLoc, yAxisMin, yAxisXLoc, yAxisMax );
        g2.draw( xAxis );
        g2.draw( yAxis );
        AffineTransform tempTx = g2.getTransform();
        g2.transform( AffineTransform.getTranslateInstance( xAxisMax, 0 ) );
        g2.transform( AffineTransform.getRotateInstance( Math.PI / 2 ) );
        g2.fill( EnergyProfilePanelGraphic.arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( xAxisMin, 0 ) );
        g2.transform( AffineTransform.getRotateInstance( -Math.PI / 2 ) );
        g2.fill( EnergyProfilePanelGraphic.arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( yAxisXLoc, yAxisMin ) );
        g2.fill( EnergyProfilePanelGraphic.arrowhead );
        g2.setTransform( tempTx );
        g2.transform( AffineTransform.getTranslateInstance( yAxisXLoc, yAxisMax ) );
        g2.transform( AffineTransform.getRotateInstance( Math.PI ) );
        g2.fill( EnergyProfilePanelGraphic.arrowhead );

        // Tick marks
        {
            g2.setTransform( orgTx );
            int tickSpacing = 40;
            int tickHeight = 10;

            // x axis ticks
            for( int x = yAxisXLoc; x < xAxisMax; x += tickSpacing ) {
                g2.drawLine( x, -( tickHeight / 2 ), x, tickHeight / 2 );
            }

            // x axis ticks
            for( int x = yAxisXLoc; x < xAxisMax; x += tickSpacing ) {
                g2.drawLine( x, -( tickHeight / 2 ), x, tickHeight / 2 );
            }

            // y axis ticks
            for( int y = 0; y < yAxisMax; y += tickSpacing ) {
                g2.drawLine( yAxisXLoc - ( tickHeight / 2 ), y, yAxisXLoc + ( tickHeight / 2 ), y );
            }
        }

        // Draw labels
        {
            g2.setFont( EnergyProfilePanelGraphic.axisLabelFont );
            g2.setColor( Color.black );

            // y axis
            AffineTransform strTx = EnergyProfilePanelGraphic.rotateInPlace( -Math.PI / 2, 0, 0 );
            g2.transform( strTx );
            Rectangle2D yAxisLabelBounds = GraphicsUtil.getStringBounds( EnergyProfilePanelGraphic.yAxisLabel, g2 );
            g2.drawString( EnergyProfilePanelGraphic.yAxisLabel, -(int)( yAxisLabelBounds.getWidth() + 10 ), -10 + yAxisXLoc );
            g2.setTransform( orgTx );

            // x axis
            Rectangle2D xAxisLabelBounds = GraphicsUtil.getStringBounds( EnergyProfilePanelGraphic.xAxisLabel, g2 );
            g2.drawString( EnergyProfilePanelGraphic.xAxisLabel, (int)( -yAxisXLoc - xAxisLabelBounds.getWidth() ),
                           (int)xAxisLabelBounds.getHeight() + 5 );
        }

        gs.restoreGraphics();
    }

    private void drawLegend( Graphics2D g2 ) {

        GraphicsState gs = new GraphicsState( g2 );

        int legendWidth = 180;
        int legendHeight = 60;
        Insets insetsFromPanel = new Insets( 0, 0, 100, 20);
        Stroke borderStroke = new BasicStroke( 3 );
        Point legendLoc = new Point( getWidth() - legendWidth - insetsFromPanel.right, getHeight() - legendHeight - insetsFromPanel.bottom );
        g2.setStroke( borderStroke );
        g2.setColor( Color.gray );
        g2.drawRoundRect( legendLoc.x, legendLoc.y, legendWidth, legendHeight, 15, 15 );

        g2.translate( legendLoc.x, legendLoc.y );
        Insets insets = new Insets( 20, 20, 0, 0 );
        {
            g2.setColor( EnergyProfileGraphic.potentialProfileColor );
            g2.setStroke( EnergyProfileGraphic.potentialProfileStroke );
            g2.drawLine( insets.left, insets.top, insets.left + 30, insets.top );
            g2.setColor( Color.black );
            Rectangle2D stringBounds = GraphicsUtil.getStringBounds( SimStrings.get( "PotentialProfilePanel.legend.PotentialEnergy" ), g2 );
            g2.drawString( SimStrings.get( "PotentialProfilePanel.legend.PotentialEnergy" ),
                           insets.left + 30 + insets.left, insets.top + (int)stringBounds.getHeight() / 3 );
        }

        {
            g2.setColor( EnergyProfileGraphic.totalEnergyColor );
            g2.setStroke( EnergyProfileGraphic.totalEnergyStroke );
            g2.drawLine( insets.left, insets.top * 2, insets.left + 30, insets.top * 2 );
            g2.setColor( Color.black );
            Rectangle2D stringBounds = GraphicsUtil.getStringBounds( SimStrings.get( "PotentialProfilePanel.legend.TotalEnergy" ), g2 );
            g2.drawString( SimStrings.get( "PotentialProfilePanel.legend.TotalEnergy" ), insets.left + 30 + insets.left, insets.top * 2 + (int)stringBounds.getHeight() / 3 );
        }


        gs.restoreGraphics();
    }

    /**
     * Adds the energy profile to the panel, and sets the origin for the graphics on the panel
     *
     * @param nucleus
     */
    public void addEnergyProfile( Nucleus nucleus, EnergyProfileGraphic.ProfileType profileType ) {
        EnergyProfileGraphic ppg = new EnergyProfileGraphic( this.getComponent(), nucleus, profileType );
        TxGraphic txg = new TxGraphic( ppg, profileTx );
        potentialProfileMap.put( nucleus.getEnergyProfile(), txg );
        addGraphic( txg, EnergyProfilePanelGraphic.nucleusLayer );
    }

    public void removeEnergyProfile( EnergyProfile potentialProfile ) {
        PhetGraphic ppg = (PhetGraphic)potentialProfileMap.get( potentialProfile );
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

    public void addAlphaParticle( final AlphaParticle alphaParticle, Nucleus nucleus ) {
        // Add an alpha particle for the specified nucleus
        final AlphaParticleGraphic alphaParticleGraphic = new AlphaParticleGraphic( alphaParticle );
        wellParticles.put( alphaParticleGraphic, nucleus );
        alphaParticle.addListener( new NuclearModelElement.Listener() {
            public void leavingSystem( NuclearModelElement nme ) {
                EnergyProfilePanelGraphic.this.removeGraphic( alphaParticleGraphic );
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
//        super.removeAllGraphics();
        PhetGraphic[] graphics = super.getGraphics();
        for( int i = 0; i < graphics.length; i++ ) {
            PhetGraphic graphic = graphics[i];
            removeGraphic( graphic );
        }
    }

    public void addOriginCenteredGraphic( PhetGraphic graphic ) {
        TxGraphic txg = new TxGraphic( graphic, profileTx );
        this.addGraphic( txg );
    }

    public void addNucleusGraphic( final Nucleus nucleus ) {
        final NucleusGraphic ng = new NucleusGraphic( nucleus );
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
        this.addEnergyProfile( nucleus, profileType );
    }

}
