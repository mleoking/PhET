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
import edu.colorado.phet.coreadditions.GridBagUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;

import java.util.List;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 * LegendPanel
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class LegendPanel extends JPanel {

    public static class LegendItem {
        private LegendItem(){};
    }
    public static final LegendItem ALPHA_PARTICLE = new LegendItem();
    public static final LegendItem NEUTRON = new LegendItem();
    public static final LegendItem PROTON = new LegendItem();
    public static final LegendItem U235 = new LegendItem();
    public static final LegendItem U238 = new LegendItem();
    public static final LegendItem U239 = new LegendItem();

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
        u235.setPosition( u235.getRadius(), u235.getRadius() );
        Uranium235Graphic u235G = new Uranium235Graphic( u235 );
        BufferedImage u235Img = new BufferedImage( (int)u235.getRadius(), (int)u235.getRadius(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D gu235 = (Graphics2D)u235Img.getGraphics();
        gu235.transform( AffineTransform.getScaleInstance( 0.5, 0.5 ) );
        u235G.paint( gu235 );
        ImageIcon u235Icon = new ImageIcon( u235Img );

        Nucleus u238 = new Uranium235( new Point2D.Double(), null );
        u238.setPosition( u238.getRadius(), u238.getRadius() );
        Uranium238Graphic u238G = new Uranium238Graphic( u238 );
        BufferedImage u238Img = new BufferedImage( (int)u238.getRadius(), (int)u238.getRadius(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D gu238 = (Graphics2D)u238Img.getGraphics();
        gu238.transform( AffineTransform.getScaleInstance( 0.5, 0.5 ) );
        u238G.paint( gu238 );
        ImageIcon u238Icon = new ImageIcon( u238Img );

        Nucleus u239 = new Uranium235( new Point2D.Double(), null );
        u239.setPosition( u239.getRadius(), u239.getRadius() );
        Uranium239Graphic u239G = new Uranium239Graphic( u239 );
        BufferedImage u239Img = new BufferedImage( (int)u239.getRadius(), (int)u239.getRadius(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D gu239 = (Graphics2D)u239Img.getGraphics();
        gu239.transform( AffineTransform.getScaleInstance( 0.5, 0.5 ) );
        u239G.paint( gu239 );
        ImageIcon u239Icon = new ImageIcon( u239Img );

        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        this.setBorder( BorderFactory.createTitledBorder( baseBorder, SimStrings.get( "NuclearPhysicsControlPanel.LegendBorder" ) ) );
        int rowIdx = 0;
        try {
            if( allModelClasses || modelClasses.contains( NEUTRON ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.NeutronLabel" ), neutronImg, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( PROTON ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.ProtonLabel" ), protonImg, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( ALPHA_PARTICLE ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.AlphaParticleLabel" ), alphaParticleImg, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( U235 ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium235Label" ), u235Icon, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( U238 ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium238Label" ), u238Icon, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( U239 ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Uranium239Label" ), u239Icon, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }
}