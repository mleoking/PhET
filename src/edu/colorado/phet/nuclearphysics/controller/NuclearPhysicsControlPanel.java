/**
 * Class: NuclearPhysicsControlPanel
 * Package: edu.colorado.phet.nuclearphysics.controller
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.ModelSlider;
import edu.colorado.phet.nuclearphysics.model.NuclearParticle;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;
import edu.colorado.phet.nuclearphysics.view.NeutronGraphic;
import edu.colorado.phet.nuclearphysics.view.ProtonGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class NuclearPhysicsControlPanel extends JPanel {
    private NuclearPhysicsModule module;

    public NuclearPhysicsControlPanel( NuclearPhysicsModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new LegendPanel(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new PotentialControlPanel( module.getPotentialProfile() ),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE,
                                              GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new TestPanel(),
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

            this.setBorder( BorderFactory.createTitledBorder( "Legend" ) );
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


    private class PotentialControlPanel extends JPanel {

        public PotentialControlPanel( PotentialProfile potentialProfile ) {

            // Create the controls
            final ModelSlider maxHeightSlider = new ModelSlider( "Height",
                                                                 10,
                                                                 potentialProfile.getMaxPotential(),
                                                                 potentialProfile.getMaxPotential() / 2 );
            maxHeightSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setProfileMaxHeight( maxHeightSlider.getModelValue() );
                }
            } );
            module.setProfileMaxHeight( maxHeightSlider.getModelValue() );

            final ModelSlider wellDepthSlider = new ModelSlider( "Well Depth",
                                                                 0,
                                                                 potentialProfile.getMaxPotential() * 2,
                                                                 20 );
            wellDepthSlider.addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    module.setProfileWellDepth( wellDepthSlider.getModelValue() );
                }
            } );
            module.setProfileWellDepth( wellDepthSlider.getModelValue() );

//            final ModelSlider profileWidthSlider = new ModelSlider( "Width",
//                                                                    100, 300, 200 );
//            profileWidthSlider.addChangeListener( new ChangeListener() {
//                public void stateChanged( ChangeEvent e ) {
//                    module.setProfileWidth( profileWidthSlider.getModelValue() );
//                }
//            } );
//            module.setProfileWidth( profileWidthSlider.getModelValue() );

            // Lay out the panel
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Potential Profile" );
            this.setBorder( titledBorder );
            setLayout( new GridBagLayout() );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, maxHeightSlider,
                                                  1, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
                GraphicsUtil.addGridBagComponent( this, wellDepthSlider,
                                                  1, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
//                GraphicsUtil.addGridBagComponent( this, profileWidthSlider,
//                                                  1, rowIdx++,
//                                                  1, 1,
//                                                  GridBagConstraints.NONE,
//                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

    private class TestPanel extends JPanel {

        TestPanel() {
            JButton decayBtn = new JButton( "Decay" );
            decayBtn.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.testDecay();
                }
            } );


            // Lay out the panel
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            Border titledBorder = BorderFactory.createTitledBorder( baseBorder, "Test" );
            this.setBorder( titledBorder );
            setLayout( new GridBagLayout() );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, decayBtn,
                                                  1, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.NONE,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }
}
