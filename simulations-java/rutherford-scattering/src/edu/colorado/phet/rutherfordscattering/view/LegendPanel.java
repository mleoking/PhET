/* Copyright 2007, University of Colorado */

package edu.colorado.phet.rutherfordscattering.view;

import java.awt.*;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.*;

import com.sun.rsasign.al;

import edu.colorado.phet.common.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.rutherfordscattering.RSConstants;
import edu.umd.cs.piccolo.PNode;

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
    private static final boolean SHOW_ALPHA_PARTICLES = false;

    private static final Font FONT = RSConstants.CONTROL_FONT;
    
    private static final Color TEXT_COLOR = Color.BLACK;
    
    //----------------------------------------------------------------------------
    // Constructors data
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public LegendPanel() {
        super();

        JLabel alphaParticleImage = toJLabel( AlphaParticleNode.createImage() );
        JLabel alphaParticleText = new JLabel( SimStrings.get( "label.alphaParticle" ) );
        alphaParticleText.setFont( FONT );
        alphaParticleText.setForeground( TEXT_COLOR );

        JLabel neutronImage = toJLabel( new NeutronNode() );
        JLabel neutronText = new JLabel( SimStrings.get( "label.neutron" ) );
        neutronText.setFont( FONT );
        neutronText.setForeground( TEXT_COLOR );

        JLabel protonImage = toJLabel( new ProtonNode() );
        JLabel protonText = new JLabel( SimStrings.get( "label.proton" ) );
        protonText.setFont( FONT );
        protonText.setForeground( TEXT_COLOR );

        JLabel electronImage = toJLabel( new ElectronNode() );
        JLabel electronText = new JLabel( SimStrings.get( "label.electron" ) );
        electronText.setFont( FONT );
        electronText.setForeground( TEXT_COLOR );

        // Border
        TitledBorder titledBorder = new TitledBorder( SimStrings.get( "label.legend" ) );
        titledBorder.setTitleFont( FONT );
        setBorder( titledBorder );

        // Layout
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        setLayout( layout );
        int row = 0;
        int col = 0;
        if ( SHOW_ALPHA_PARTICLES ) {
            layout.addComponent( alphaParticleImage, row, col++, 1, 1, GridBagConstraints.CENTER );
            layout.addComponent( alphaParticleText, row++, col++, 1, 1, GridBagConstraints.WEST );
            col = 0;
        }
        layout.addComponent( electronImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( electronText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
        layout.addComponent( protonImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( protonText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
        layout.addComponent( neutronImage, row, col++, 1, 1, GridBagConstraints.CENTER );
        layout.addComponent( neutronText, row++, col++, 1, 1, GridBagConstraints.WEST );
        col = 0;
    }

    /*
     * Converts an Image to a JLabel.
     */
    private JLabel toJLabel( Image image ) {
        Icon icon = new ImageIcon( image );
        return new JLabel( icon );
    }

    /*
     * Converts a PNode to a JLabel.
     */
    private JLabel toJLabel( PNode node ) {
        Icon icon = new ImageIcon( node.toImage() );
        return new JLabel( icon );
    }
}
