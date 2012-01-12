// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.advancedacidbasesolutions.view.equilibriumexpressions;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.advancedacidbasesolutions.AABSColors;
import edu.colorado.phet.advancedacidbasesolutions.AABSStrings;
import edu.colorado.phet.advancedacidbasesolutions.control.EquationScalingControl;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.advancedacidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.piccolophet.PhetPCanvas;
import edu.colorado.phet.common.piccolophet.util.PNodeLayoutUtils;
import edu.umd.cs.piccolo.PCanvas;

/**
 * Dialog that displays the solute equilibrium expressions (K=).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class KExpressionDialog extends PaintImmediateDialog {
    
    private static final Dimension TOP_CANVAS_SIZE = new Dimension( 500, 130 );
    
    private final AqueousSolution solution;
    private final SolutionListener solutionListener;
    private final EquationScalingControl scalingControl;
    private final PhetPCanvas canvas;
    private AbstractEquilibriumExpressionNode soluteNode;
    
    public KExpressionDialog( Frame owner, final AqueousSolution solution ) {
        this( owner, solution, true /* showWaterExpression */ );
    }
    
    public KExpressionDialog( Frame owner, final AqueousSolution solution, boolean showWaterExpression ) {
        super( owner );
        setTitle( AABSStrings.TITLE_EQUILIBRIUM_EXPRESSION );
        setResizable( false );
        
        this.solution = solution;
        
        this.solutionListener = new SolutionListener() {
            
            public void soluteChanged() {
                handleSoluteChanged();
            }

            public void concentrationChanged() {
                handleConcentrationOrStrengthChanged();
            }

            public void strengthChanged() {
                handleConcentrationOrStrengthChanged();
            }
        };
        this.solution.addSolutionListener( solutionListener );
        
        // scaling on/off
        scalingControl = new EquationScalingControl();
        scalingControl.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( soluteNode != null ) {
                    soluteNode.setScalingEnabled( scalingControl.isScalingEnabled(), true /* animated */ );
                }
            }
        } );
        
        // canvas
        canvas = new PhetPCanvas() {
            protected void updateLayout() {
                super.updateLayout();
                updateCanvasLayout();
            }
        };
        canvas.setPreferredSize( TOP_CANVAS_SIZE );
        canvas.setBackground( AABSColors.EQUILIBRIUM_EXPRESSIONS_BACKGROUND );
        
        // solute expression, will be set based on solution
        soluteNode = null;
        
        // layout
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout( new BoxLayout( canvasPanel, BoxLayout.Y_AXIS ) );
        canvasPanel.add( canvas );
        JPanel userPanel = new JPanel( new BorderLayout() );
        userPanel.setBackground( AABSColors.COLOR_PANEL_BACKGROUND );
        userPanel.add( scalingControl, BorderLayout.NORTH );
        userPanel.add( canvasPanel, BorderLayout.CENTER );
        JPanel mainPanel = new JPanel( new BorderLayout() );
        mainPanel.add( userPanel, BorderLayout.CENTER );
        
        handleSoluteChanged();
            
        getContentPane().add( mainPanel, BorderLayout.CENTER );
        pack();
    }
    
    private void cleanup() {
        solution.removeSolutionListener( solutionListener );
    }
    
    public void dispose() {
        cleanup();
        super.dispose();
    }
    
    private void handleSoluteChanged() {
        
        // remove any existing solute expression
        if ( soluteNode != null ) {
            canvas.getLayer().removeChild( soluteNode );
            soluteNode = null;
        }
        
        // create the proper type of solute expression
        if ( solution.isAcidic() ) {
            soluteNode = new AcidEquilibriumExpressionNode( solution );
        }
        else if ( solution.isBasic() ) {
            soluteNode = new BaseEquilibriumExpressionNode( solution );
        }
        
        // add the new solute expression
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( isScalingEnabled(), false /* animated */ );
            canvas.getLayer().addChild( soluteNode );
            updateCanvasLayout();
        }
    }
    
    private void handleConcentrationOrStrengthChanged() {
        if ( soluteNode != null ) {
            soluteNode.update();
        }
    }
    
    public void setScalingEnabled( boolean enabled ) {
        scalingControl.setScalingEnabled( enabled );
        if ( soluteNode != null ) {
            soluteNode.setScalingEnabled( enabled, false /* animated */ );
        }
    }
    
    public boolean isScalingEnabled() {
        return scalingControl.isScalingEnabled();
    }
    
    private void updateCanvasLayout() {
        centerExpression( soluteNode, canvas );
    }
    
    private static void centerExpression( AbstractEquilibriumExpressionNode node, PCanvas canvas ) {
        if ( node != null ) {
            final boolean isScalingEnabled = node.isScalingEnabled();
            node.setScalingEnabled( false /* enabled */, false /* animated */ ); // do the layout with scaling off
            double xOffset = ( canvas.getWidth() - node.getFullBoundsReference().getWidth() ) / 2;
            double yOffset = ( ( canvas.getHeight() - node.getFullBoundsReference().getHeight() ) / 2 ) - PNodeLayoutUtils.getOriginYOffset( node );
            node.setOffset( xOffset, yOffset );
            node.setScalingEnabled( isScalingEnabled, false /* animated */ ); // restore scaling
        }
    }
}
