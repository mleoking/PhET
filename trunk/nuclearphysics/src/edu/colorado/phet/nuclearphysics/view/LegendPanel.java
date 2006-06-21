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
    public static final LegendItem Po210 = new LegendItem();
    public static final LegendItem Pb206 = new LegendItem();

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

        Nucleus po210 = new Polonium210( new Point2D.Double(), null );
        ImageIcon po210Icon = createIcon( po210 );

        Nucleus pb206 = new Lead206( new Point2D.Double() );
        ImageIcon pb206Icon = createIcon( pb206 );

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
            if( allModelClasses || modelClasses.contains( Po210 ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Polonium210Label" ), po210Icon, SwingConstants.LEFT ),
                                                 0, rowIdx++,
                                                 1, 1,
                                                 GridBagConstraints.HORIZONTAL,
                                                 GridBagConstraints.WEST );
            }
            if( allModelClasses || modelClasses.contains( Pb206 ) ) {
                GridBagUtil.addGridBagComponent( this, new JLabel( SimStrings.get( "NuclearPhysicsControlPanel.Lead206Label" ), pb206Icon, SwingConstants.LEFT ),
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

    private ImageIcon createIcon( Nucleus nucleus ) {
        nucleus.setPosition( nucleus.getRadius(), nucleus.getRadius() );
        NucleusGraphic nucleusGraphic = new NucleusGraphicFactory().create( nucleus );
        BufferedImage nucleusImage = new BufferedImage( (int)nucleus.getRadius(), (int)nucleus.getRadius(), BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D)nucleusImage.getGraphics();
        g2.transform( AffineTransform.getScaleInstance( 0.5, 0.5 ) );
        nucleusGraphic.paint( g2 );
        return new ImageIcon( nucleusImage );
    }


}