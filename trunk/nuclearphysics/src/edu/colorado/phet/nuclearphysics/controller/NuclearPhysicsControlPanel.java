/**
 * Class: NuclearPhysicsControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.PhetControlPanel;
import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.Nucleus;
import edu.colorado.phet.nuclearphysics.model.Uranium235;
import edu.colorado.phet.nuclearphysics.view.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class NuclearPhysicsControlPanel extends PhetControlPanel {
    private NuclearPhysicsModule module;
    private int rowIdx = 0;
    private JPanel mainPanel;

    public NuclearPhysicsControlPanel( NuclearPhysicsModule module ) {
        super( module );
        this.module = module;
        setLayout( new BorderLayout() );
        add( new LegendPanel(), BorderLayout.NORTH );
        mainPanel = new JPanel( new GridBagLayout() );
        add( mainPanel, BorderLayout.CENTER );
    }

    protected NuclearPhysicsModule getModule() {
        return module;
    }

    public void addPanelElement( JPanel panel ) {
        try {
            GraphicsUtil.addGridBagComponent( mainPanel, panel,
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    //
    // Inner classes
    //
    private class LegendPanel extends JPanel {
        public LegendPanel() {
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
            this.setBorder( BorderFactory.createTitledBorder( baseBorder, "Legend" ) );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Neutron", neutronImg, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Proton", protonImg, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "Alpha Particle", alphaParticleImg, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "<html>Uranium<sup><font size=-1> 235</font><sup></html>", u235Icon, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "<html>Uranium<sup><font size=-1> 238</font><sup></html>", u238Icon, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
                GraphicsUtil.addGridBagComponent( this, new JLabel( "<html>Uranium<sup><font size=-1> 239</font><sup></html>", u239Icon, SwingConstants.LEFT ),
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.WEST );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
