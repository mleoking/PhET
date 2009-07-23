/* Copyright 2009, University of Colorado */

package edu.colorado.phet.nuclearphysics.module.betadecay;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.nuclearphysics.NuclearPhysicsStrings;
import edu.colorado.phet.nuclearphysics.view.AntineutrinoNode;
import edu.colorado.phet.nuclearphysics.view.ElectronNode;
import edu.colorado.phet.nuclearphysics.view.NeutronNode;
import edu.colorado.phet.nuclearphysics.view.ProtonNode;
import edu.umd.cs.piccolo.PNode;


/**
 * This class displays the legend for the Beta Decay tabs.  It simply 
 * displays information and doesn't control anything, so it does not include
 * much in the way of interactive behavior.
 *
 * @author John Blanco
 */
public class BetaDecayLegendPanel extends JPanel {
        
    //------------------------------------------------------------------------
    // Class Data
    //------------------------------------------------------------------------
    
    // Amount to scale up the particle nodes to make them look reasonable.
    private static final double PARTICLE_SCALE_FACTOR = 8;
    
    //------------------------------------------------------------------------
    // Instance Data
    //------------------------------------------------------------------------
    
    private int _numRows = 0;
    
    //------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------
    
    public BetaDecayLegendPanel(){
    	
        // Add the border around the legend.
        BevelBorder baseBorder = (BevelBorder)BorderFactory.createRaisedBevelBorder();
        TitledBorder titledBorder = BorderFactory.createTitledBorder( baseBorder,
                NuclearPhysicsStrings.LEGEND_BORDER_LABEL,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new PhetFont( Font.BOLD, 14 ),
                Color.GRAY );
        
        setBorder( titledBorder );
        
        // Set the layout.
        setLayout( new GridBagLayout() );

        // Add the images and labels that comprise the legend.
        
        PNode neutron = new NeutronNode();
        neutron.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( neutron.toImage(), NuclearPhysicsStrings.NEUTRON_LEGEND_LABEL ); 
        PNode proton = new ProtonNode();
        proton.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( proton.toImage(), NuclearPhysicsStrings.PROTON_LEGEND_LABEL ); 
        PNode electron = new ElectronNode();
        electron.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( electron.toImage(), NuclearPhysicsStrings.ELECTRON_LEGEND_LABEL );
        PNode antineutrino = new AntineutrinoNode();
        antineutrino.scale( PARTICLE_SCALE_FACTOR );
        addLegendItem( antineutrino.toImage(), NuclearPhysicsStrings.ANTINEUTRINO_LEGEND_LABEL );
        
    }
    
    /**
     * This method adds simple legend items, i.e. those that only include an
     * image and a label, to the legend.
     */
    private void addLegendItem( Image im, String label ) {
        GridBagConstraints constraints = new GridBagConstraints();
        
        ImageIcon icon = new ImageIcon(im);
        
        // Add the image.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridy = _numRows;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.ipady = 10;
        add( new JLabel(icon), constraints );
        
        // Add the label.
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 1;
        constraints.gridy = _numRows;
        add(new JLabel( label ), constraints);
        
        // Increment to the next row.
        _numRows++;
    }
}
