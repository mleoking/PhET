// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.colorado.phet.common.piccolophet.nodes.photon.PhotonNode;
import edu.colorado.phet.hydrogenatom.HAConstants;
import edu.colorado.phet.hydrogenatom.HAResources;
import edu.colorado.phet.hydrogenatom.view.particle.*;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * LegendPanel identifies each of the icon images used in this sim.
 * Convenience classes are provided for displaying the legend as 
 * a PNode or a JDialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class LegendPanel extends JPanel {

    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // Various things that I was asked to remove from the legend...
    private static final boolean SHOW_UV_IR_PHOTONS = false;
    private static final boolean SHOW_VISIBLE_PHOTONS = false;
    private static final boolean SHOW_ALPHA_PARTICLES = false;

    private static final Color PANEL_COLOR = Color.BLACK;
    private static final Color LABEL_COLOR = Color.WHITE;

    private static final int FONT_STYLE = HAConstants.DEFAULT_FONT_STYLE;
    private static final int DEFAULT_FONT_SIZE = 18;
    private static final String FONT_SIZE_RESOURCE = "legend.font.size";

    //----------------------------------------------------------------------------
    // Constructors data
    //----------------------------------------------------------------------------

    /**
     * Constructor.
     */
    public LegendPanel() {
        super();

        int fontSize = HAResources.getInt( FONT_SIZE_RESOURCE, DEFAULT_FONT_SIZE );
        Font font = new PhetFont( FONT_STYLE, fontSize );

        JLabel photonImage = toJLabel( PhotonNode.createImage( HAConstants.PHOTON_ICON_WAVELENGTH, HAPhotonNode.DIAMETER ) );
        JLabel photonText = new JLabel( HAResources.getString( "label.photon" ) );
        photonText.setFont( font );
        photonText.setForeground( LABEL_COLOR );

        JLabel uvPhotonImage = toJLabel( PhotonNode.createImage( VisibleColor.MIN_WAVELENGTH - 1, HAPhotonNode.DIAMETER ) );
        JLabel uvPhotonText = new JLabel( HAResources.getString( "label.uvPhoton" ) );
        uvPhotonText.setFont( font );
        uvPhotonText.setForeground( LABEL_COLOR );

        JLabel irPhotonImage = toJLabel( PhotonNode.createImage( VisibleColor.MAX_WAVELENGTH + 1, HAPhotonNode.DIAMETER ) );
        JLabel irPhotonText = new JLabel( HAResources.getString( "label.irPhoton" ) );
        irPhotonText.setFont( font );
        irPhotonText.setForeground( LABEL_COLOR );

        JLabel alphaParticleImage = toJLabel( AlphaParticleNode.createImage() );
        JLabel alphaParticleText = new JLabel( HAResources.getString( "label.alphaParticle" ) );
        alphaParticleText.setFont( font );
        alphaParticleText.setForeground( LABEL_COLOR );

        JLabel neutronImage = toJLabel( new NeutronNode() );
        JLabel neutronText = new JLabel( HAResources.getString( "label.neutron" ) );
        neutronText.setFont( font );
        neutronText.setForeground( LABEL_COLOR );

        JLabel protonImage = toJLabel( new ProtonNode() );
        JLabel protonText = new JLabel( HAResources.getString( "label.proton" ) );
        protonText.setFont( font );
        protonText.setForeground( LABEL_COLOR );

        JLabel electronImage = toJLabel( new ElectronNode() );
        JLabel electronText = new JLabel( HAResources.getString( "label.electron" ) );
        electronText.setFont( font );
        electronText.setForeground( LABEL_COLOR );

        // Border
        Border border = null;
        {
            EmptyBorder emptyBorder = new EmptyBorder( new Insets( 5, 5, 5, 5 ) );
            LineBorder lineBorder = new LineBorder( LABEL_COLOR, 1 );
            TitledBorder titledBorder = new TitledBorder( lineBorder, HAResources.getString( "label.legend" ) );
            titledBorder.setTitleFont( font );
            titledBorder.setTitleColor( LABEL_COLOR );
            border = new CompoundBorder( emptyBorder, titledBorder );
        }
        setBorder( border );

        // Layout
        setBackground( PANEL_COLOR );
        EasyGridBagLayout layout = new EasyGridBagLayout( this );
        layout.setInsets( new Insets( 8, 8, 8, 8 ) ); // top,left,bottom,right
        setLayout( layout );
        int row = 0;
        int col = 0;
        if ( SHOW_VISIBLE_PHOTONS ) {
            layout.addComponent( photonImage, row, col++, 1, 1, GridBagConstraints.CENTER );
            layout.addComponent( photonText, row++, col++, 1, 1, GridBagConstraints.WEST );
            col = 0;
        }
        if ( SHOW_UV_IR_PHOTONS ) {
            layout.addComponent( uvPhotonImage, row, col++, 1, 1, GridBagConstraints.CENTER );
            layout.addComponent( uvPhotonText, row++, col++, 1, 1, GridBagConstraints.WEST );
            col = 0;
            layout.addComponent( irPhotonImage, row, col++, 1, 1, GridBagConstraints.CENTER );
            layout.addComponent( irPhotonText, row++, col++, 1, 1, GridBagConstraints.WEST );
            col = 0;
        }
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

    //----------------------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------------------

    /**
     * LegendNode displays the legend as a Piccolo node.
     */
    public static class LegendNode extends PhetPNode {

        public LegendNode() {
            super();
            setPickable( false );
            setChildrenPickable( false );
            LegendPanel panel = new LegendPanel();
            PSwing pswing = new PSwing( panel );
            addChild( pswing );
        }
    }

    /**
     * LegendDialog displays the legend in a dialog.
     */
    public static class LegendDialog extends PaintImmediateDialog {

        public LegendDialog( Frame owner ) {
            super( owner );
            createUI( owner );
        }

        private void createUI( Frame parent ) {

            JPanel legendPanel = new LegendPanel();

            JPanel actionsPanel = createActionsPanel();
            JPanel bottomPanel = new JPanel( new BorderLayout() );
            bottomPanel.add( new JSeparator(), BorderLayout.NORTH );
            bottomPanel.add( actionsPanel, BorderLayout.CENTER );

            JPanel mainPanel = new JPanel( new BorderLayout() );
            mainPanel.setBorder( new EmptyBorder( 10, 10, 0, 10 ) );
            mainPanel.add( legendPanel, BorderLayout.CENTER );
            mainPanel.add( bottomPanel, BorderLayout.SOUTH );

            getContentPane().add( mainPanel );
            pack();
            this.setResizable( false );
            this.setLocationRelativeTo( parent );
        }

        private JPanel createActionsPanel() {
            JButton closeButton = new JButton( HAResources.getString( "button.close" ) );
            closeButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent event ) {
                    dispose();
                }
            } );

            JPanel innerPanel = new JPanel( new GridLayout( 1, 1, 0, 0 ) );
            innerPanel.add( closeButton );

            JPanel actionPanel = new JPanel( new FlowLayout() );
            actionPanel.add( innerPanel );

            return actionPanel;
        }
    }
}
