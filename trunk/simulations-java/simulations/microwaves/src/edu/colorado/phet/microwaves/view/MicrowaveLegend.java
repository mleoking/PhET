/**
 * Class: MicrowaveLegend
 * Package: edu.colorado.phet.microwave.view
 * Author: Another Guy
 * Date: Dec 1, 2003
 */
package edu.colorado.phet.microwaves.view;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common_microwaves.view.graphics.ModelViewTransform2D;
import edu.colorado.phet.microwaves.model.WaterMolecule;

public class MicrowaveLegend extends JPanel {

    public MicrowaveLegend() {

        // Draw a water molecule
        BufferedImage bImg = new BufferedImage( 30, 30, BufferedImage.TYPE_INT_ARGB );
        Graphics2D g2 = (Graphics2D) bImg.getGraphics();
        WaterMolecule molecule = new WaterMolecule();
        ModelViewTransform2D tx = new ModelViewTransform2D( new Rectangle2D.Double( 0, 0, 60, 60 ),
                                                            new Rectangle( 0, 0, bImg.getWidth(), bImg.getHeight() ) );
        molecule.setLocation( tx.viewToModel( bImg.getWidth() / 2, bImg.getHeight() / 2 ).getX(),
                              tx.viewToModel( bImg.getWidth() / 2, bImg.getHeight() / 2 ).getY() );
        WaterMoleculeGraphic graphic = new WaterMoleculeGraphic( molecule, tx );
        graphic.update( molecule, null );
        g2.setTransform( tx.toAffineTransform() );
        graphic.paint( g2 );
        ImageIcon icon = new ImageIcon( bImg );

        setLayout( new GridBagLayout() );
        this.setBorder( BorderFactory.createTitledBorder( SimStrings.get( "MicrowaveLegend.BorderTitle" ) ) );
        int rowIdx = 0;
        try {
            SwingUtils.addGridBagComponent( this, new JLabel(
                    SimStrings.get( "MicrowaveLegend.WaterMoleculeLabel" ),
                    icon, SwingConstants.LEFT ), 0, rowIdx++, 1, 1,
                                                 GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST );
        }
        catch( AWTException e ) {
            e.printStackTrace();
        }

    }
}
