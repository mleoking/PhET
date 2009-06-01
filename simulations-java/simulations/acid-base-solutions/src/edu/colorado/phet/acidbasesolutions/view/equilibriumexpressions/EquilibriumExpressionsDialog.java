package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionAdapter;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 525, 150 );
    private static final Dimension BOTTOM_CANVAS_SIZE = new Dimension( 500, 120 );
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PhetPCanvas topCanvas, bottomCanvas;
    private final EquilibriumExpressionNode topExpressionNode;
    private final WaterEquilibriumExpressionNode waterExpressionNode;
    
    public EquilibriumExpressionsDialog( Frame owner, AqueousSolution solution ) {
        super( owner );
        setTitle( ABSStrings.TITLE_EQUILIBRIUM_EXPRESSIONS );
        setResizable( false );
        
        this.solution = solution;
        this.solutionListener = new SolutionAdapter() {
            public void soluteChanged() {
                updateTopLayout();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleScaleEnable();
            }
        };
        scaleOnRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_ON );
        scaleOnRadioButton.addActionListener( scaleOnOffActionListener );
        scaleOffRadioButton = new JRadioButton( ABSStrings.RADIO_BUTTON_EQUATION_SCALING_OFF );
        scaleOffRadioButton.addActionListener( scaleOnOffActionListener );
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add( scaleOffRadioButton );
        buttonGroup.add( scaleOnRadioButton );
        scaleOffRadioButton.setSelected( true );
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
                super.updateLayout();
                updateTopLayout();
            }
        };
        topCanvas.setPreferredSize( TOP_CANVAS_SIZE );
        topCanvas.setBackground( ABSConstants.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        
        // top expression
        topExpressionNode = new EquilibriumExpressionNode( solution );
        topCanvas.getLayer().addChild( topExpressionNode );
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomCanvas.setBackground( ABSConstants.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        
        // bottom expression
        waterExpressionNode = new WaterEquilibriumExpressionNode( solution );
        bottomCanvas.getLayer().addChild( waterExpressionNode );
        
        // layout
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout( new BoxLayout( canvasPanel, BoxLayout.Y_AXIS ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
            
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    public void dispose() {
        solution.removeSolutionListener( solutionListener );
        super.dispose();
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        handleScaleEnable();
    }
    
    public boolean isScalingEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void handleScaleEnable() {
        topExpressionNode.setScaleEnabled( isScalingEnabled() );
        waterExpressionNode.setScaleEnabled( isScalingEnabled() );
    }
    
    private void updateTopLayout() {
        topExpressionNode.setScaleEnabled( false ); // do the layout with scaling off
        double xOffset = ( topCanvas.getWidth() - topExpressionNode.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = ( ( topCanvas.getHeight() - topExpressionNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( topExpressionNode );
        topExpressionNode.setOffset( xOffset, yOffset );
        topExpressionNode.setScaleEnabled( isScalingEnabled() ); // restore scaling
    }
    
    private void updateBottomLayout() {
        waterExpressionNode.setScaleEnabled( false ); // do the layout with scaling off
        double xOffset = ( bottomCanvas.getWidth() - waterExpressionNode.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = ( ( bottomCanvas.getHeight() - waterExpressionNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( waterExpressionNode );
        waterExpressionNode.setOffset( xOffset, yOffset );
        waterExpressionNode.setScaleEnabled( isScalingEnabled() ); // restore scaling
    }
}
