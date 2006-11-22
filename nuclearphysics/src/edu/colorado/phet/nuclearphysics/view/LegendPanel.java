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

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.nuclearphysics.model.*;

import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * LegendPanel
 * <p>
 * This is the class used for the legend for all modules. A module indicates
 * what items it wants to have in the legend by implementing the abstract method
 * List getLegendClasses(), which returns a list of LegendItem instances. LegendItem
 * is an enumeration inner class defined in this class.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LegendPanel extends JPanel {

    public static class LegendItem {
        private LegendItem() {
        }

        ;
    }

    public static final LegendItem ALPHA_PARTICLE = new LegendItem();
    public static final LegendItem NEUTRON = new LegendItem();
    public static final LegendItem PROTON = new LegendItem();
    public static final LegendItem U235 = new LegendItem();
    public static final LegendItem U238 = new LegendItem();
    public static final LegendItem U239 = new LegendItem();
    public static final LegendItem Po210 = new LegendItem();
    public static final LegendItem Pb206 = new LegendItem();
    public static final LegendItem DAUGHTER_NUCLEI = new LegendItem();

    /**
     * @param modelClasses
     */
    public LegendPanel( List modelClasses ) {
        if( modelClasses.isEmpty() ) {
            return;
        }
        boolean allModelClasses = ( modelClasses == null );
        setLayout( new GridBagLayout() );

        BufferedImage neutronBi = new BufferedImage( (int)NuclearParticle.RADIUS * 4, (int)NuclearParticle.RADIUS * 4,
                                                     BufferedImage.TYPE_INT_ARGB );
        Graphics2D gn = (Graphics2D)neutronBi.getGraphics();
        new NeutronGraphic().paint( gn, NuclearParticle.RADIUS, NuclearParticle.RADIUS );
        ImageIcon neutronImg = new ImageIcon( neutronBi );

        BufferedImage protonBi = new BufferedImage( (int)NuclearParticle.RADIUS * 4, (int)NuclearParticle.RADIUS * 4,
                                                    BufferedImage.TYPE_INT_ARGB );
        Graphics2D gp = (Graphics2D)protonBi.getGraphics();
        new ProtonGraphic().paint( gp, NuclearParticle.RADIUS, NuclearParticle.RADIUS );
        ImageIcon protonImg = new ImageIcon( protonBi );

        Icon alphaParticleImg = AlphaParticleGraphic.getIcon();

        Nucleus u235 = new Uranium235( new Point2D.Double(), null );
        ImageIcon u235Icon = createIcon( u235 );

        Nucleus u238 = new Uranium238( new Point2D.Double(), null );
        ImageIcon u238Icon = createIcon( u238 );

        Nucleus u239 = new Uranium239( new Point2D.Double(), null );
        ImageIcon u239Icon = createIcon( u239 );

        Nucleus po210 = new Polonium211( new Point2D.Double(), null );
        ImageIcon po210Icon = createIcon( po210 );

        Nucleus pb206 = new Lead207( new Point2D.Double() );
        ImageIcon pb206Icon = createIcon( pb206 );

        Nucleus ru = new Rubidium( new Point2D.Double() );
        ImageIcon ruIcon = createIcon( ru );

        Nucleus cs = new Cesium( new Point2D.Double() );
        ImageIcon csIcon = createIcon( cs );

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        this.setBorder( BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "NuclearPhysicsControlPanel.LegendBorder" ) ) );
        GridBagConstraints iconGbc = new GridBagConstraints( 0, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.EAST,
                                                             GridBagConstraints.BOTH,
                                                             new Insets( 4, 5, 4, 5 ), 0, 0 );
        GridBagConstraints textGbc = new GridBagConstraints( 1, GridBagConstraints.RELATIVE, 1, 1, 1, 1,
                                                             GridBagConstraints.WEST,
                                                             GridBagConstraints.BOTH,
                                                             new Insets( 4, 5, 4, 5 ), 0, 0 );
        if( allModelClasses || modelClasses.contains( NEUTRON ) ) {
            add( new JLabel( neutronImg, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.NeutronLabel" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( PROTON ) ) {
            add( new JLabel( protonImg, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.ProtonLabel" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( ALPHA_PARTICLE ) ) {
            add( new JLabel( alphaParticleImg, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.AlphaParticleLabel" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( U235 ) ) {
            add( new JLabel( u235Icon, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium235Label" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( U238 ) ) {
            add( new JLabel( u238Icon, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium238Label" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( U239 ) ) {
            add( new JLabel( u239Icon, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium239Label" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( Po210 ) ) {
            add( new JLabel( po210Icon, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Polonium211Label" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( Pb206 ) ) {
            add( new JLabel( pb206Icon, SwingConstants.RIGHT ), iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Lead207Label" ), SwingConstants.LEFT ), textGbc );
        }
        if( allModelClasses || modelClasses.contains( DAUGHTER_NUCLEI ) ) {
            JPanel jp = new JPanel( new GridLayout( 1,2));
            jp.add( new JLabel( ruIcon, SwingConstants.RIGHT ) );
            jp.add( new JLabel( csIcon, SwingConstants.RIGHT ) );
            add( jp, iconGbc );
            add( new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.DaughterNucleiLabel" ), SwingConstants.LEFT ), textGbc );
        }
    }

    private ImageIcon createIcon( Nucleus nucleus ) {
        double scale = 0.5;
        nucleus.setPosition( nucleus.getRadius(), nucleus.getRadius() );
        NucleusGraphic nucleusGraphic = new NucleusGraphicFactory().create( nucleus );
        BufferedImage nucleusImage = new BufferedImage( (int)( ( nucleusGraphic.getImage().getWidth() + 15 ) * scale ),
                                                        (int)( ( nucleusGraphic.getImage().getHeight() + 15 ) * scale ),
                                                        BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D)nucleusImage.getGraphics();
        g2.transform( AffineTransform.getScaleInstance( 0.5, 0.5 ) );
        g2.translate( 10, 10 );
        nucleusGraphic.setTransform( new AffineTransform() );
        nucleusGraphic.paint( g2 );
        return new ImageIcon( nucleusImage );
    }
}