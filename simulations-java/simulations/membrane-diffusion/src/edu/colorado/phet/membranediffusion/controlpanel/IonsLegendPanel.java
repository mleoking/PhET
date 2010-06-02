/* Copyright 2008, University of Colorado */

package edu.colorado.phet.membranediffusion.controlpanel;

import java.awt.Color;
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
import edu.colorado.phet.membranediffusion.MembraneDiffusionConstants;
import edu.colorado.phet.membranediffusion.model.PotassiumIon;
import edu.colorado.phet.membranediffusion.model.SodiumIon;
import edu.colorado.phet.membranediffusion.view.ParticleNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays a legend, a.k.a. a key, for a set of ions.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class IonsLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	// The model-view transforms below are used to make nodes that usually
	// reside on the canvas be of an appropriate size for inclusion on the
	// control panel.
	private static final ModelViewTransform2D PARTICLE_MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-8, -8, 16, 16));

    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public IonsLegendPanel() {
        
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
        		// TODO: i18n
                "Ions",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                MembraneDiffusionConstants.CONTROL_PANEL_TITLE_FONT,
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridBagLayout() );

        // Add the images and labels for the ions.
        int row = 0;
   		PNode imageNode = new ParticleNode(new SodiumIon(), PARTICLE_MVT);
   		// TODO: i18n.
   		addLegendItem( imageNode.toImage(), "Sodium Ion (Na+)", row++ );
        		
   		imageNode = new ParticleNode(new PotassiumIon(), PARTICLE_MVT);
   		// TODO: i18n.
   		addLegendItem( imageNode.toImage(), "Potassium Ion (K+)", row++ );
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
        textualLabel.setFont(MembraneDiffusionConstants.CONTROL_PANEL_CONTROL_FONT);
        add(textualLabel, constraints);
    }
}
