// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.rutherfordscattering.RSResources;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * LegendPanel identifies each of the icon images used in this sim.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LegendPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Various things that I was asked to remove from the legend...
    private static final boolean SHOW_ALPHA_PARTICLES = true;

    private static final Color TEXT_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors data
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     */
    public LegendPanel( double iconScale, Font titleFont, Font labelFont, Border border ) {
        super();

        ElectronNode electronNode = new ElectronNode();
        electronNode.scale( iconScale );
        JLabel electronImage = toJLabel( electronNode );
        JLabel electronText = new JLabel( RSResources.getString( "string.electron" ) );
        electronText.setFont( labelFont );
        electronText.setForeground( TEXT_COLOR );
        
        ProtonNode protonNode = new ProtonNode();
        protonNode.scale( iconScale );
        JLabel protonImage = toJLabel( protonNode );
        JLabel protonText = new JLabel( RSResources.getString( "string.proton" ) );
        protonText.setFont( labelFont );
        protonText.setForeground( TEXT_COLOR );
        
        NeutronNode neutronNode = new NeutronNode();
        neutronNode.scale( iconScale );
        JLabel neutronImage = toJLabel( neutronNode );
        JLabel neutronText = new JLabel( RSResources.getString( "string.neutron" ) );
        neutronText.setFont( labelFont );
        neutronText.setForeground( TEXT_COLOR );
        
        PImage alphaParticleNode = new PImage( AlphaParticleNode.createImage() );
        alphaParticleNode.scale( iconScale );
        JLabel alphaParticleImage = toJLabel( alphaParticleNode );
        JLabel alphaParticleText = new JLabel( RSResources.getString( "string.alphaParticle" ) );
        alphaParticleText.setFont( labelFont );
        alphaParticleText.setForeground( TEXT_COLOR );

        // Border
        TitledBorder titledBorder = new TitledBorder( RSResources.getString( "string.legend" ) );
        titledBorder.setTitleFont( titleFont );
        titledBorder.setBorder( border );
        setBorder( titledBorder );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int col = 0;
        layout.addComponent( electronImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( electronText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
        layout.addComponent( protonImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( protonText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
        layout.addComponent( neutronImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( neutronText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
        if ( SHOW_ALPHA_PARTICLES ) {
            layout.addComponent( alphaParticleImage, row, col++, 1, 1, GridBagConstraints.CENTER );
            layout.addComponent( alphaParticleText, row++, col++, 1, 1, GridBagConstraints.WEST );
            col = 0;
        }
    }

    /*
     * Converts a PNode to a JLabel.
     */
    private JLabel toJLabel( PNode node ) {
        Icon icon = new ImageIcon( node.toImage() );
        return new JLabel( icon );
    }
}
