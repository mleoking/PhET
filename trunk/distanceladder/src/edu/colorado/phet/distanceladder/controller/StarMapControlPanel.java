/**
 * Class: StarMapControlPanel
 * Class: edu.colorado.phet.distanceladder.controller
 * User: Ron LeMaster
 * Date: Apr 12, 2004
 * Time: 2:43:58 PM
 */
package edu.colorado.phet.distanceladder.controller;

import edu.colorado.phet.common.view.util.GraphicsUtil;
import edu.colorado.phet.distanceladder.view.StarshipCoordsGraphic;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;

public class StarMapControlPanel extends JPanel {
    private JButton starshipCoordsBtn;
    private StarMapModule module;

    public StarMapControlPanel( final StarMapModule module ) {
        super( new GridBagLayout() );
        this.module = module;

        int rowIdx = 0;
        try {
            GraphicsUtil.addGridBagComponent( this, new LegendPanel(),
                                              0, rowIdx++,
                                              1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
            GraphicsUtil.addGridBagComponent( this, new ControlPanel(),
                                              0, rowIdx,
                                              1, 1,
                                              GridBagConstraints.NONE, GridBagConstraints.CENTER );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }
    }

    private class ControlPanel extends JPanel {
        String showString = "<html>Show Starship<br>Coordinates</html>";
        String hideString = "<html>Hide Starship<br>Coordinates</html>";

        public ControlPanel() {
            // Create controls
            starshipCoordsBtn = new JButton( new AbstractAction( showString ) {
                private boolean coordsOn = false;

                public void actionPerformed( ActionEvent e ) {
                    coordsOn = !coordsOn;
                    starshipCoordsBtn.setText( coordsOn ? hideString : showString );
                    module.setStarshipCoordsEnabled( coordsOn );
                }
            } );

            // Lay out panel
            setLayout( new GridBagLayout() );
            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            this.setBorder( BorderFactory.createTitledBorder( baseBorder, "Controls" ) );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, starshipCoordsBtn,
                                                  0, rowIdx++,
                                                  1, 1,
                                                  GridBagConstraints.HORIZONTAL,
                                                  GridBagConstraints.CENTER );
            }
            catch( AWTException e ) {
                e.printStackTrace();
            }
        }
    }

    private class LegendPanel extends JPanel {
        public LegendPanel() {
            setLayout( new GridBagLayout() );

            BufferedImage redLineBI = new BufferedImage( 20, 30, BufferedImage.TYPE_INT_ARGB );
            Graphics2D g = (Graphics2D)redLineBI.getGraphics();
            g.setColor( StarshipCoordsGraphic.refLineColor );
            g.drawLine( 0, redLineBI.getHeight() / 2, redLineBI.getWidth(), redLineBI.getHeight() / 2 );
            ImageIcon redLineImg = new ImageIcon( redLineBI );

            BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
            this.setBorder( BorderFactory.createTitledBorder( baseBorder, "Legend" ) );
            int rowIdx = 0;
            try {
                GraphicsUtil.addGridBagComponent( this, new JLabel( "<html>Direction of<br>starship</html>",
                                                                    redLineImg, SwingConstants.LEFT ),
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
