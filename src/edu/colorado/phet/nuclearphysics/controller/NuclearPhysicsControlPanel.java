/**
 * Class: NuclearPhysicsControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.ProtonGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.image.BufferedImage;

public class NuclearPhysicsControlPanel extends JPanel {
    private NuclearPhysicsModule module;
    private int rowIdx = 0;

    public NuclearPhysicsControlPanel( NuclearPhysicsModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
        try {
            GraphicsUtil.addGridBagComponent( this, new LegendPanel(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    protected NuclearPhysicsModule getModule() {
        return module;
    }

    public void addPanelElement( JPanel panel ) {
        try {
            GraphicsUtil.addGridBagComponent( this, panel,
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
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
