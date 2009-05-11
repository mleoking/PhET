package edu.colorado.phet.acidbasesolutions.control;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;


public class SolutionControlsNode extends PhetPNode {
    
    //XXX move to ABSConstants
    private static final DoubleRange STRENGTH_WEAK_RANGE = new DoubleRange( 10E-10, 1 );
    private static final DoubleRange STRENGTH_STRONG_RANGE = new DoubleRange( 20, 10E7 );
    private static final DoubleRange CONCENTRATION_RANGE = new DoubleRange( 10E-3, 1 );
    
    private static final double Y_SPACING = 10;
    
    private final ConcentrationControlNode concentrationControlNode;
    private final StrengthSliderNode strengthSliderNode;

    public SolutionControlsNode() {
        super();
        
        concentrationControlNode = new ConcentrationControlNode( CONCENTRATION_RANGE.getMin(), CONCENTRATION_RANGE.getMax() );
        addChild( concentrationControlNode );
        
        strengthSliderNode = new StrengthSliderNode( STRENGTH_WEAK_RANGE, STRENGTH_STRONG_RANGE );
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
