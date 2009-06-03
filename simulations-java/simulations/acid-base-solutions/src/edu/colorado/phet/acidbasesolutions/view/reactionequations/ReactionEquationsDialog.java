package edu.colorado.phet.acidbasesolutions.view.reactionequations;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.acidbasesolutions.ABSStrings;
import edu.colorado.phet.acidbasesolutions.model.*;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionAdapter;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.acidbasesolutions.util.PNodeUtils;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;


public class ReactionEquationsDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 650, 175 );
    private static final Dimension BOTTOM_CANVAS_SIZE = TOP_CANVAS_SIZE;
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    
    private final PhetPCanvas topCanvas, bottomCanvas;
    private final AcidReactionEquationNode acidNode;
    private final BaseReactionEquationNode baseNode;
    private final WaterReactionEquationNode waterNode;
    private final JRadioButton scaleOnRadioButton, scaleOffRadioButton;
    
    public ReactionEquationsDialog( Frame owner, final AqueousSolution solution ) {
        super( owner, ABSStrings.TITLE_REACTION_EQUATIONS );
        setResizable( true );
        
        this.solution = solution;
        this.solutionListener = new SolutionAdapter() {
            public void soluteChanged() {
                updateVisibility();
                updateTopLayout();
                updateBottomLayout();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scale on/off
        JLabel scaleOnOffLabel = new JLabel( ABSStrings.LABEL_EQUATION_SCALING );
        ActionListener scaleOnOffActionListener = new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleScalingEnabled();
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
        topCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
        
        // acid equation
        acidNode = new AcidReactionEquationNode( solution );
        topCanvas.getLayer().addChild( acidNode );
        
        // base equation
        baseNode = new BaseReactionEquationNode( solution );
        topCanvas.getLayer().addChild( baseNode );
        
        // bottom canvas
        bottomCanvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateBottomLayout();
            }
        };
        bottomCanvas.setPreferredSize( BOTTOM_CANVAS_SIZE );
        bottomCanvas.setBackground( ABSConstants.REACTION_EQUATIONS_BACKGROUND );
        
        // water equation
        waterNode = new WaterReactionEquationNode( solution );
        bottomCanvas.getLayer().addChild( waterNode );
        
        // layout
        JPanel canvasPanel = new JPanel( new GridLayout( 0, 1 ) );
        canvasPanel.add( topCanvas );
        canvasPanel.add( bottomCanvas);
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.add( scaleOnOffPanel, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
            
        updateVisibility();
        
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private void cleanup() {
        solution.removeSolutionListener( solutionListener );
        acidNode.cleanup();
        baseNode.cleanup();
        waterNode.cleanup();
    }
    
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scaleOnRadioButton.setSelected( enabled );
        handleScalingEnabled();
    }
    
    public boolean isScalingEnabled() {
        return scaleOnRadioButton.isSelected();
    }
    
    private void handleScalingEnabled() {
        acidNode.setScaleEnabled( isScalingEnabled() );
        baseNode.setScaleEnabled( isScalingEnabled() );
        waterNode.setScaleEnabled( isScalingEnabled() );
    }
    
    private void updateVisibility() {
        Solute solute = solution.getSolute();
        acidNode.setVisible( !solution.isPureWater() && ( solute instanceof Acid ) );
        baseNode.setVisible( !solution.isPureWater() && ( solute instanceof Base ) );
    }
    
    private void updateTopLayout() {
        
        double xOffset, yOffset;
        
        // acid expression
        acidNode.setScaleEnabled( false ); // do the layout with scaling off
        xOffset = ( ( topCanvas.getWidth() - acidNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeUtils.getOriginXOffset( acidNode );
        yOffset = ( ( topCanvas.getHeight() - acidNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( acidNode );
        acidNode.setOffset( xOffset, yOffset );
        acidNode.setScaleEnabled( isScalingEnabled() ); // restore scaling
        
        // base expression
        baseNode.setScaleEnabled( false ); // do the layout with scaling off
        xOffset = ( ( topCanvas.getWidth() - baseNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeUtils.getOriginXOffset( baseNode );
        yOffset = ( ( topCanvas.getHeight() - baseNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( baseNode );
        baseNode.setOffset( xOffset, yOffset );
        baseNode.setScaleEnabled( isScalingEnabled() ); // restore scaling
    }
    
    private void updateBottomLayout() {
        
        // water expression
        waterNode.setScaleEnabled( false ); // do the layout with scaling off
        double xOffset = ( ( bottomCanvas.getWidth() - waterNode.getFullBoundsReference().getWidth() ) / 2 ) - PNodeUtils.getOriginXOffset( waterNode );
        double yOffset = ( ( bottomCanvas.getHeight() - waterNode.getFullBoundsReference().getHeight() ) / 2 ) - PNodeUtils.getOriginYOffset( waterNode );
        waterNode.setOffset( xOffset, yOffset );
        waterNode.setScaleEnabled( isScalingEnabled() ); // restore scaling
    }
}
