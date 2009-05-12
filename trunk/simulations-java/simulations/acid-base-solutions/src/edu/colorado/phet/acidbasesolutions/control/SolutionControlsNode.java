package edu.colorado.phet.acidbasesolutions.control;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.PhetPNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolox.pswing.PSwing;


public class SolutionControlsNode extends PhetPNode {
    
    private static final double X_SPACING = 3;
    private static final double Y_SPACING = 10;
    private static final Font LABEL_FONT = new PhetFont( 14 );
    private static final Color LABEL_COLOR = Color.BLACK;
    private static final double BACKGROUND_MARGIN = 10;
    private static final double BACKGROUND_CORNER_RADIUS = 25;
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 220 );
    private static final Color BACKGROUND_STROKE_COLOR = Color.BLACK;
    private static final Stroke BACKGROUND_STROKE = new BasicStroke( 1f );
    private static final Color SEPARATOR_COLOR = BACKGROUND_COLOR.darker();
    private static final Stroke SEPARATOR_STROKE = new BasicStroke( 1f );
    
    private final SoluteComboBox soluteComboBox;
    private final ConcentrationControlNode concentrationControlNode;
    private final StrengthSliderNode strengthSliderNode;

    public SolutionControlsNode() {
        super();
        
        PNode soluteLabel = new LabelNode( ABSStrings.LABEL_SOLUTE );
        addChild( soluteLabel );
        
        soluteComboBox = new SoluteComboBox();
        PSwing soluteComboBoxWrapper = new PSwing( soluteComboBox );
        addChild( soluteComboBoxWrapper );
        
        PNode concentrationLabel = new LabelNode( ABSStrings.LABEL_CONCENTRATION );
        addChild( concentrationLabel );
        
        concentrationControlNode = new ConcentrationControlNode( ABSConstants.CONCENTRATION_RANGE );
        addChild( concentrationControlNode );
        
        PNode strengthLabel = new LabelNode( ABSStrings.LABEL_STRENGTH );
        addChild( strengthLabel );
        
        strengthSliderNode = new StrengthSliderNode( ABSConstants.WEAK_STRENGTH_RANGE, ABSConstants.STRONG_STRENGTH_RANGE );
        addChild( strengthSliderNode );
        
        // layout
        double xOffset = 0;
        double yOffset = 0;
        soluteLabel.setOffset( xOffset, yOffset );
        xOffset = soluteLabel.getFullBoundsReference().getMaxX() + X_SPACING;
        yOffset = soluteLabel.getFullBoundsReference().getCenterY() - ( soluteComboBoxWrapper.getFullBoundsReference().getHeight() / 2 );
        soluteComboBoxWrapper.setOffset( xOffset, yOffset );
        xOffset = soluteLabel.getXOffset();
        yOffset = soluteComboBoxWrapper.getFullBoundsReference().getMaxY() + Y_SPACING;
        concentrationLabel.setOffset( xOffset, yOffset );
        xOffset = concentrationLabel.getXOffset() + 15;
        yOffset = concentrationLabel.getFullBoundsReference().getMaxY() - ( concentrationControlNode.getFullBoundsReference().getY() - concentrationControlNode.getYOffset() ) + 5;
        concentrationControlNode.setOffset( xOffset, yOffset );
        xOffset = concentrationLabel.getXOffset();
        yOffset = concentrationControlNode.getFullBoundsReference().getMaxY() + Y_SPACING;
        strengthLabel.setOffset( xOffset, yOffset );
        xOffset = strengthLabel.getXOffset() + 15;
        yOffset = strengthLabel.getFullBoundsReference().getMaxY() - ( strengthSliderNode.getFullBoundsReference().getY() - strengthSliderNode.getYOffset() );
        strengthSliderNode.setOffset( xOffset, yOffset );
        
        // separator
        final double sepWidth = this.getFullBoundsReference().getWidth();
        PNode sepNode1 = new SeparatorNode( sepWidth );
        addChild( sepNode1 );
        xOffset = 0;
        yOffset = concentrationLabel.getFullBoundsReference().getMinY() - 3;
        sepNode1.setOffset( xOffset, yOffset );
        
        // separator
        PNode sepNode2 = new SeparatorNode( sepWidth );
        addChild( sepNode2 );
        xOffset = 0;
        yOffset = strengthLabel.getFullBoundsReference().getMinY() - 3;
        sepNode2.setOffset( xOffset, yOffset );
        
        // put a background behind the entire panel
        PNode backgroundNode = new BackgroundNode( this );
        addChild( 0, backgroundNode );
    }
    
    private static class LabelNode extends PText {
        
        public LabelNode( String text ) {
            super( text );
            setPickable( false );
            setFont( LABEL_FONT );
            setTextPaint( LABEL_COLOR );
        }
    }
    
    private static class BackgroundNode extends PPath {
        public BackgroundNode( PNode node ) {
            super();
            setPickable( false );
            final double m = BACKGROUND_MARGIN;
            final double r = BACKGROUND_CORNER_RADIUS;
            PBounds b = node.getFullBounds();
            setPathTo( new RoundRectangle2D.Double( b.getX() - m, b.getY() - m, b.getWidth() + ( 2 * m ), b.getHeight() + ( 2 * m ), r, r ) );
            setStroke( BACKGROUND_STROKE );
            setStrokePaint( BACKGROUND_STROKE_COLOR );
            setPaint( BACKGROUND_COLOR );
        }
    }
    
    private static class SeparatorNode extends PPath {
        public SeparatorNode( double length ) {
            super( new Line2D.Double( 0, 0, length, 0 ) );
            setPickable( false );
            setStrokePaint( SEPARATOR_COLOR );
            setStroke( SEPARATOR_STROKE );
        }
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
