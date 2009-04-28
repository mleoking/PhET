/* Copyright 2009, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view.graph;

import java.awt.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSImages;
import edu.colorado.phet.acidbasesolutions.ABSSymbols;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.SwingLayoutNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * X-axis below the Concentration bar graph that shows molecules icons and labels.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ConcentrationXAxisNode extends SwingLayoutNode {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final Font FONT = new PhetFont( 18 );

    //----------------------------------------------------------------------------
    // Member data
    //----------------------------------------------------------------------------
    
    private final IconNode _iconLHS, _iconRHS;
    private final LabelNode _labelLHS, _labelRHS;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public ConcentrationXAxisNode() {
        super();
        setPickable( false );
        setChildrenPickable( false );
        
        // icons
        _iconLHS = new IconNode( ABSImages.HA_MOLECULE );
        _iconRHS = new IconNode( ABSImages.A_MINUS_MOLECULE );
        IconNode iconH3O = new IconNode( ABSImages.H3O_PLUS_MOLECULE );
        IconNode iconOH = new IconNode( ABSImages.OH_MINUS_MOLECULE );
        IconNode iconH2O = new IconNode( ABSImages.H2O_MOLECULE );
        PNode[] icons = { _iconLHS, _iconRHS, iconH3O, iconOH, iconH2O };
        
        // labels
        _labelLHS = new LabelNode( ABSSymbols.HA );
        _labelRHS = new LabelNode( ABSSymbols.A_MINUS );
        LabelNode labelH3O = new LabelNode( ABSSymbols.H3O_PLUS );
        LabelNode labelOH = new LabelNode( ABSSymbols.OH_MINUS );
        LabelNode labelH2O = new LabelNode( ABSSymbols.H2O );
        PNode[] labels = { _labelLHS, _labelRHS, labelH3O, labelOH, labelH2O };
        
        // render order
        addChild( _iconLHS );
        addChild( _iconRHS );
        addChild( iconH3O );
        addChild( iconOH );
        addChild( iconH2O );
        addChild( _labelLHS );
        addChild( _labelRHS );
        addChild( labelH3O );
        addChild( labelOH );
        addChild( labelH2O );
        
        // layout
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets( 2, 2, 2, 2 ); // top,left,bottom,right
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = GridBagConstraints.RELATIVE; // column
        constraints.gridy = 0; // next row
        for ( int i = 0; i < icons.length; i++ ) {
            addChild( icons[i], constraints );
        }
        constraints.gridy++; // next row
        for ( int i = 0; i < labels.length; i++ ) {
            addChild( labels[i], constraints );
        }
    }
    
    public void setMoleculeLHS( Image image, String text ) {
        _iconLHS.setImage( image );
        _labelLHS.setText( text );
    }
    
    public void setMoleculeRHS( Image image, String text ) {
        _iconRHS.setImage( image );
        _labelRHS.setText( text );
    }
    
    /*
     * Labels used in this view.
     */
    private static class LabelNode extends PComposite {
        
        private HTMLNode htmlNode;
        
        public LabelNode( String text ) {
            super();
            htmlNode = new HTMLNode( HTMLUtils.toHTMLString( text ) );
            htmlNode.setFont( FONT );
            addChild( htmlNode );
        }
        
        public void setText( String text ) {
            htmlNode.setHTML( HTMLUtils.toHTMLString( text ) );
        }
    }
    
    /*
     * Icons used in this view.
     */
    private static class IconNode extends PComposite {
        
        private PImage imageNode;
        
        public IconNode( Image image ) {
            super();
            imageNode = new PImage( image );
            addChild( imageNode );
            scale( 0.25 );//TODO scale image files
        }
        
        public void setImage( Image image ) {
            imageNode.setImage( image );
        }
    }
    
    public static void main( String[] args ) {
        
        Dimension canvasSize = new Dimension( 800, 600 );
        PhetPCanvas canvas = new PhetPCanvas( canvasSize );
        canvas.setPreferredSize( canvasSize );
        canvas.setBackground( Color.LIGHT_GRAY );
        
        ConcentrationXAxisNode node = new ConcentrationXAxisNode();
        canvas.getLayer().addChild( node );
        node.setOffset( 100, 100 );
        
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );
        
        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
}
}
