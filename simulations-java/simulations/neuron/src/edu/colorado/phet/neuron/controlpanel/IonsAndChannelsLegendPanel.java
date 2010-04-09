/* Copyright 2008, University of Colorado */

package edu.colorado.phet.neuron.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.neuron.model.PotassiumGatedChannel;
import edu.colorado.phet.neuron.model.PotassiumIon;
import edu.colorado.phet.neuron.model.PotassiumLeakageChannel;
import edu.colorado.phet.neuron.model.SodiumDualGatedChannel;
import edu.colorado.phet.neuron.model.SodiumIon;
import edu.colorado.phet.neuron.model.SodiumLeakageChannel;
import edu.colorado.phet.neuron.view.MembraneChannelNode;
import edu.colorado.phet.neuron.view.ParticleNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays a legend, a.k.a. a key, for a set of ions and membrane
 * channels.  It simply displays information and doesn't control anything, so
 * it does not include much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class IonsAndChannelsLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final Font LABEL_FONT = new PhetFont(14);
	
	// The model-view transforms below are used to make nodes that usually
	// reside on the canvas be of an appropriate size for inclusion on the
	// control panel.
	private static final ModelViewTransform2D PARTICLE_MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-8, -8, 16, 16));

	private static final ModelViewTransform2D CHANNEL_MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-6, -6, 12, 12));
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public IonsAndChannelsLegendPanel() {
        
    	// TODO: i18n
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                "Legend",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridBagLayout() );

        // Add the images and labels for the ions.
        // TODO: i18n.
        int row = 0;
   		PNode imageNode = new ParticleNode(new SodiumIon(), PARTICLE_MVT);
   		addLegendItem( imageNode.toImage(), "Sodium Ion (Na+)", row++ );
        		
   		imageNode = new ParticleNode(new PotassiumIon(), PARTICLE_MVT);
   		addLegendItem( imageNode.toImage(), "Potassium Ion (K+)", row++ );

   		imageNode = new MembraneChannelNode(new SodiumDualGatedChannel(), CHANNEL_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		addLegendItem( imageNode.toImage(), "Sodium Gated Channel", row++ );

   		imageNode = new MembraneChannelNode(new PotassiumGatedChannel(), CHANNEL_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		addLegendItem( imageNode.toImage(), "Potassium Gated Channel", row++ );

   		imageNode = new MembraneChannelNode(new SodiumLeakageChannel(), CHANNEL_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		addLegendItem( imageNode.toImage(), "Sodium Leak Channel", row++ );

   		imageNode = new MembraneChannelNode(new PotassiumLeakageChannel(), CHANNEL_MVT);
   		imageNode.rotate(-Math.PI / 2);
   		addLegendItem( imageNode.toImage(), "Potassium Leak Channel", row++ );
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     */
    private void addLegendItem( Image im, String label, int row ) {
        ImageIcon icon = new ImageIcon(im);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.ipadx = 30;
        constraints.ipady = 10;
        add(new JLabel(icon), constraints);
        constraints.ipadx = 0;
        constraints.gridx = 1;
        JLabel textualLabel = new JLabel( label );
        textualLabel.setFont(LABEL_FONT);
        add(textualLabel, constraints);
    }
}
