/* Copyright 2008, University of Colorado */

package edu.colorado.phet.genenetwork.controlpanel;

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
import edu.colorado.phet.genenetwork.model.Cap;
import edu.colorado.phet.genenetwork.model.LacI;
import edu.colorado.phet.genenetwork.model.LacZ;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;
import edu.colorado.phet.genetwork.view.SimpleModelElementNode;


/**
 * This class displays a legend, a.k.a. a key, for a set of ions.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class LacOperonLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
	
	private static final Font LABEL_FONT = new PhetFont(14);
	
	// The model-view transform below is used to make nodes that usually
	// reside on the canvas be of an appropriate size for inclusion on the
	// control panel.
	private static final ModelViewTransform2D MVT = new ModelViewTransform2D(
			new Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0), new Rectangle2D.Double(-6, -6, 12, 12));

    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public LacOperonLegendPanel() {
        
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

        // Add the images and labels for each model element depicted.
        int row = 0;
    	SimpleModelElementNode simpleElementNode = null;
    	String labelText = null;

		simpleElementNode = new SimpleModelElementNode(new LacI(), MVT);
		labelText = "LacI";
       	addLegendItem( simpleElementNode.toImage(30, 30, new Color(0, 0, 0, 0)), labelText, row );
       	row++;

		simpleElementNode = new SimpleModelElementNode(new Cap(), MVT);
		labelText = "CAP";
       	addLegendItem( simpleElementNode.toImage(25, 25, new Color(0, 0, 0, 0)), labelText, row );
       	row++;

       	simpleElementNode = new SimpleModelElementNode(new RnaPolymerase(), MVT);
		labelText = "RNA Polymerase";
       	addLegendItem( simpleElementNode.toImage(40, 40, new Color(0, 0, 0, 0)), labelText, row );
       	row++;
       	
		simpleElementNode = new SimpleModelElementNode(new LacZ(), MVT);
		labelText = "LacZ";
       	addLegendItem( simpleElementNode.toImage(35, 35, new Color(0, 0, 0, 0)), labelText, row );
       	row++;

    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     */
    private void addLegendItem( Image im, String label, int row ) {
        ImageIcon icon = new ImageIcon(im);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.ipadx = 10;
        constraints.ipady = 10;
        add(new JLabel(icon), constraints);
        constraints.ipadx = 0;
        constraints.gridx = 1;
        JLabel textualLabel = new JLabel( label );
        textualLabel.setFont(LABEL_FONT);
        add(textualLabel, constraints);
    }
}
