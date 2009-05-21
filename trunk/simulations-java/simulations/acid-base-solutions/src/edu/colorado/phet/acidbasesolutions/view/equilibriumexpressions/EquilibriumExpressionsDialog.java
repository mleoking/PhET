package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    private static final boolean DEV = true;//XXX delete this
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 500, 150 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private AqueousSolution solution;
    private final SolutionListener solutionListener;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PhetPCanvas topCanvas, bottomCanvas;
    private AbstractEquilibriumExpressionNode topExpression;
    private final WaterEquilibriumExpressionNode waterExpression;
    
    // dev controls
    private final PText topCanvasSizeNode, bottomCanvasSizeNode;
    
    public EquilibriumExpressionsDialog( Frame owner, AqueousSolution solution ) {
        super( owner, ABSStrings.TITLE_EQUILIBRIUM_EXPRESSIONS );
        setResizable( true ); //XXX should be false
        
        solutionListener = new SolutionListener() {

            public void soluteChanged() {
                updateSolute();
            }
            
            public void concentrationChanged() {
                updateScales();
            }

            public void strengthChanged() {
                updateScale();
            }
        };
        
        this.solution = solution;
        this.solution.addSolutionListener( solutionListener );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateScale();
            }
        };
        scaleOnRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOnRadioButton.setSelected( true );
        JPanel scaleOnOffPanel = new JPanel( new GridBagLayout() );
        scaleOnOffPanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = GridBagConstraints.RELATIVE;
        scaleOnOffPanel.add( scaleOnOffLabel );
        scaleOnOffPanel.add( scaleOnRadioButton );
        scaleOnOffPanel.add( scaleOffRadioButton );
        
        // top canvas
        topCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                updateTopLayout();
            }
        };
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topCanvasSizeNode = new PText();
        topCanvasSizeNode.setOffset( 5, 5 );
        if ( DEV ) {
            topCanvas.getLayer().addChild( topCanvasSizeNode );
        }
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        waterExpression = new WaterEquilibriumExpressionNode();
        bottomCanvas.getLayer().addChild( waterExpression );
        bottomCanvasSizeNode = new PText();
        bottomCanvasSizeNode.setOffset( 5, 5 );
        if ( DEV ) {
            bottomCanvas.getLayer().addChild( bottomCanvasSizeNode );
        }
        
        // layout
        JPanel canvasPanel = new JPanel( new GridLayout( 0, 1 ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
            
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
        updateSolute();
    }
    
    private void updateSolute() {
        //TODO
        updateTopLayout();
        updateScales();
    }
    
    private void updateScales() {
        
    }
    
    private void updateTopLayout() {
        if ( topExpression != null ) {
            double xOffset = ( topCanvas.getWidth() - topExpression.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = 20 + ( topCanvas.getHeight() - topExpression.getFullBoundsReference().getHeight() ) / 2;
            topExpression.setOffset( xOffset, yOffset );
            Dimension canvasSize = topCanvas.getSize();
            topCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
        }
    }
    
    private void updateBottomLayout() {
        double xOffset = ( bottomCanvas.getWidth() - waterExpression.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( bottomCanvas.getHeight() - waterExpression.getFullBoundsReference().getHeight() ) / 2;
        waterExpression.setOffset( xOffset, yOffset );
        Dimension canvasSize = bottomCanvas.getSize();
        bottomCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private void updateScale() {
        final boolean enabled = scaleOnRadioButton.isSelected();
        if ( enabled ) {
           //TODO
        }
        else {
            if ( topExpression != null ) {
                topExpression.setScaleAll( 1 );
            }
            waterExpression.setScaleAll( 1 );
        }
    }
}
