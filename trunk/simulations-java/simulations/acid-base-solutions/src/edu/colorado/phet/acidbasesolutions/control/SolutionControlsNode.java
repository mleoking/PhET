package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class SolutionControlsNode extends PhetPNode {
    
    private static final double Y_SPACING = 10;
    
    private final ConcentrationControlNode concentrationControlNode;
    private final StrengthSliderNode strengthSliderNode;

    public SolutionControlsNode() {
        super();
        
        concentrationControlNode = new ConcentrationControlNode( ABSConstants.CONCENTRATION_RANGE );
        addChild( concentrationControlNode );
        
        strengthSliderNode = new StrengthSliderNode( ABSConstants.WEAK_STRENGTH_RANGE, ABSConstants.STRONG_STRENGTH_RANGE );
        addChild( strengthSliderNode );
        
        // layout
        double xOffset, yOffset;
        
        xOffset = concentrationControlNode.getXOffset() - concentrationControlNode.getFullBoundsReference().getX();
        yOffset = concentrationControlNode.getYOffset() - concentrationControlNode.getFullBoundsReference().getY();
        concentrationControlNode.setOffset( xOffset, yOffset );
        xOffset = strengthSliderNode.getXOffset() - strengthSliderNode.getFullBoundsReference().getX();
        yOffset = concentrationControlNode.getFullBoundsReference().getMaxY() + Y_SPACING + ( strengthSliderNode.getYOffset() - strengthSliderNode.getFullBoundsReference().getY() );
        strengthSliderNode.setOffset( xOffset, yOffset );
    }
    
    // test
    public static void main( String[] args ) {

        Dimension canvasSize = new Dimension( 600, 300 );
        PhetPCanvas canvas = new PhetPCanvas();
        canvas.setPreferredSize( canvasSize );

        SolutionControlsNode controlsNode = new SolutionControlsNode();
        canvas.getLayer().addChild( controlsNode );
        controlsNode.setOffset( 100, 100 );

        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( canvas, BorderLayout.CENTER );

        JFrame frame = new JFrame();
        frame.setContentPane( panel );
        frame.pack();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
    }
}
