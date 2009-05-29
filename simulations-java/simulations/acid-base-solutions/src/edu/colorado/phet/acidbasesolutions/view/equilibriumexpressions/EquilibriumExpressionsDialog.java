package edu.colorado.phet.acidbasesolutions.view.equilibriumexpressions;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.umd.cs.piccolo.nodes.PText;


public class EquilibriumExpressionsDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 500, 150 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    private final PhetPCanvas topCanvas, bottomCanvas;
    private final EquilibriumExpressionNode topExpressionNode;
    private final WaterEquilibriumExpressionNode waterExpressionNode;
    
    // dev controls
    private final PText topCanvasSizeNode, bottomCanvasSizeNode;
    
    public EquilibriumExpressionsDialog( Frame owner, AqueousSolution solution ) {
        this( owner, solution, PhetApplication.getInstance().isDeveloperControlsEnabled() );
    }
    
    public EquilibriumExpressionsDialog( Frame owner, AqueousSolution solution, boolean dev ) {
        super( owner, ABSStrings.TITLE_EQUILIBRIUM_EXPRESSIONS );
        setResizable( dev );
        
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
        topExpressionNode = new EquilibriumExpressionNode( solution );
        topCanvas.getLayer().addChild( topExpressionNode );
        topCanvasSizeNode = new PText();
        topCanvasSizeNode.setOffset( 5, 5 );
        if ( dev ) {
            topCanvas.getLayer().addChild( topCanvasSizeNode );
        }
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomCanvas.setBackground( ABSConstants.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        waterExpressionNode = new WaterEquilibriumExpressionNode( solution );
        bottomCanvas.getLayer().addChild( waterExpressionNode );
        bottomCanvasSizeNode = new PText();
        bottomCanvasSizeNode.setOffset( 5, 5 );
        if ( dev ) {
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
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        handleScaleEnable();
    }
    
    public boolean isScalingEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void handleScaleEnable() {
        topExpressionNode.setScaleEnabled( scaleOnRadioButton.isSelected() );
        waterExpressionNode.setScaleEnabled( scaleOnRadioButton.isSelected() );
    }
    
    private void updateTopLayout() {
        double xOffset = ( topCanvas.getWidth() - topExpressionNode.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( topCanvas.getHeight() - topExpressionNode.getFullBoundsReference().getHeight() ) / 2;
        topExpressionNode.setOffset( xOffset, yOffset );
        Dimension canvasSize = topCanvas.getSize();
        topCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
    
    private void updateBottomLayout() {
        double xOffset = ( bottomCanvas.getWidth() - waterExpressionNode.getFullBoundsReference().getWidth() ) / 2;
        double yOffset = 20 + ( bottomCanvas.getHeight() - waterExpressionNode.getFullBoundsReference().getHeight() ) / 2;
        waterExpressionNode.setOffset( xOffset, yOffset );
        Dimension canvasSize = bottomCanvas.getSize();
        bottomCanvasSizeNode.setText( "canvas size: " + canvasSize.width + "x" + canvasSize.height );
    }
}
