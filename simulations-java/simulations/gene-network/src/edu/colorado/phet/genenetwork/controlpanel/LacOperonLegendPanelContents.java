/* Copyright 2009, University of Colorado */

package edu.colorado.phet.genenetwork.controlpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform2D;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.genenetwork.model.Cap;
import edu.colorado.phet.genenetwork.model.Galactose;
import edu.colorado.phet.genenetwork.model.Glucose;
import edu.colorado.phet.genenetwork.model.LacI;
import edu.colorado.phet.genenetwork.model.LacZ;
import edu.colorado.phet.genenetwork.model.RnaPolymerase;
import edu.colorado.phet.genetwork.view.SimpleModelElementNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays a legend, a.k.a. a key, for a set of ions.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class LacOperonLegendPanelContents extends JPanel {
        
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
    
    public LacOperonLegendPanelContents() {
    	
    	// TODO: i18n
        // Add the border around the legend.
        // Set the layout.
        setLayout( new GridBagLayout() );

        // Add the images and labels for each model element depicted.
        int row = 0;
    	SimpleModelElementNode simpleElementNode = null;
    	String labelText = null;

		labelText = "Lactose";
       	addLegendItem2( createLactoseNode(), 20, labelText, row );
       	row++;

		simpleElementNode = new SimpleModelElementNode(new LacI(), MVT);
		labelText = "LacI";
       	addLegendItem2( simpleElementNode, 30, labelText, row );
       	row++;

		simpleElementNode = new SimpleModelElementNode(new Cap(), MVT);
		labelText = "CAP";
       	addLegendItem2( simpleElementNode, 25, labelText, row );
       	row++;

       	simpleElementNode = new SimpleModelElementNode(new RnaPolymerase(), MVT);
		labelText = "RNA Polymerase";
       	addLegendItem2( simpleElementNode, 40, labelText, row );
       	row++;
       	
		simpleElementNode = new SimpleModelElementNode(new LacZ(), MVT);
		labelText = "LacZ";
       	addLegendItem2( simpleElementNode, 35, labelText, row );
       	row++;
    }
    
    private void addLegendItem2( PNode node, int width, String label, int row ) {
    	int height = (int)Math.round((double)width/node.getFullBounds().getWidth() * node.getFullBounds().getHeight());
    	Image image = node.toImage(width, height, new Color(0, 0, 0, 0));
        ImageIcon icon = new ImageIcon(image);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = row;
        constraints.ipadx = 10;
        constraints.ipady = 10;
        add(new JLabel(icon), constraints);
        constraints.anchor = GridBagConstraints.WEST;
        constraints.ipadx = 0;
        constraints.gridx = 1;
        JLabel textualLabel = new JLabel( label );
        textualLabel.setFont(LABEL_FONT);
        add(textualLabel, constraints);
    }
    
    /**
     * Generate an image of Lactose.  This is necessary because Lactose exists
     * as a combination of two simple model elements in the model, so there is
     * no model element that can be created easily as can be done for the
     * simple model elements.
     */
    private PNode createLactoseNode(){
    	PNode lactoseNode = new PNode();
    	PNode glucoseNode = new SimpleModelElementNode(new Glucose(), MVT);
    	glucoseNode.setOffset(-glucoseNode.getFullBoundsReference().width / 2, 0);
    	lactoseNode.addChild(glucoseNode);
    	PNode galactoseNode = new SimpleModelElementNode(new Galactose(), MVT);
    	galactoseNode.setOffset(galactoseNode.getFullBoundsReference().width / 2, 0);
    	lactoseNode.addChild(galactoseNode);
    	return lactoseNode;
    }

    public static void main(String[] args) {
        JFrame frame =new JFrame();
        frame.setContentPane(new LacOperonLegendPanelContents());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
