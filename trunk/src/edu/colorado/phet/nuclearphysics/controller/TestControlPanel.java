/**
 * Class: TestControlPanel
 * Class: edu.colorado.phet.nuclearphysics.controller
 * User: Ron LeMaster
 * Date: Mar 1, 2004
 * Time: 10:50:03 AM
 */
package edu.colorado.phet.nuclearphysics.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.coreadditions.ModelSlider;
import edu.colorado.phet.nuclearphysics.model.PotentialProfile;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

public class TestControlPanel extends JPanel {
    private NuclearPhysicsModule module;

    public TestControlPanel( NuclearPhysicsModule module ) {
        this.module = module;
        setLayout( new GridBagLayout() );
//        int rowIdx = 0;
//        try {
//            GraphicsUtil.addGridBagComponent( this, new PotentialControlPanel( module.getPotentialProfile() ),
//                                              0, rowIdx++,
//                                              1, 1,
//                                              GridBagConstraints.NONE,
//                                              GridBagConstraints.CENTER );
//        }
//        catch( AWTException e ) {
//            e.printStackTrace();
//        }
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


//            JSpinner spinner = new JSpinner();
//            spinner.setUI( new BasicSpinnerUI() {
//                protected Component createPreviousButton() {
//                    JButton b = new JButton("PREV");
//                    Font f = new Font( "SansSerif", Font.BOLD, 18);
//                    b.setFont( f );
//                    return b;
//                }
//                protected Component createNextButton() {
//                    JButton b = new JButton("NEXT");
//                    Font f = new Font( "SansSerif", Font.BOLD, 18);
//                    b.setFont( f );
//                    return b;
//                }
//            });
//            spinner.setPreferredSize( new Dimension( 100, 200 ));


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
//                    GraphicsUtil.addGridBagComponent( this, spinner,
//                                                      1, rowIdx++,
//                                                      1, 1,
//                                                      GridBagConstraints.NONE,
//                                                      GridBagConstraints.CENTER );
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

}
