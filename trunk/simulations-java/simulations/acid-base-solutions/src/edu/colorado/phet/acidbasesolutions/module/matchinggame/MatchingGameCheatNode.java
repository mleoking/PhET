package edu.colorado.phet.acidbasesolutions.module.matchinggame;

import java.text.DecimalFormat;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import edu.colorado.phet.acidbasesolutions.model.AqueousSolution;
import edu.colorado.phet.acidbasesolutions.model.AqueousSolution.SolutionListener;
import edu.umd.cs.piccolox.nodes.PComposite;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Developer-only node that allows us to cheat at the Matching Game,
 * by showing us the concentration and strength of the solutions that 
 * we're trying to match.  This is not localized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MatchingGameCheatNode extends PComposite {
    
    private static final DecimalFormat CONCENTRATION_FORMAT = new DecimalFormat( "0.0000" );
    private static final DecimalFormat STRENGTH_FORMAT = new DecimalFormat( "0.0000" );
    
    private final AqueousSolution solution;
    private final JLabel concentrationLabel, strengthLabel;
    
    public MatchingGameCheatNode( AqueousSolution solution ) {
        super();
        
        this.solution = solution;
        this.solution.addSolutionListener( new SolutionListener() {

            public void concentrationChanged() {
                update();
            }

            public void soluteChanged() {
                update();
            }

            public void strengthChanged() {
                update();
            }
        } );
        
        concentrationLabel = new JLabel();
        strengthLabel = new JLabel();
        
        JPanel panel = new JPanel();
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ) );
        panel.setBorder( new TitledBorder( "cheat (dev)" ) );
        panel.add( concentrationLabel );
        panel.add( strengthLabel );
        
        PSwing pswing = new PSwing( panel );
        addChild( pswing );
        
        update();
    }
    
    private void update() {
        double c = solution.getSolute().getConcentration();
        concentrationLabel.setText( "c = " + CONCENTRATION_FORMAT.format( c ) );
        double k = solution.getSolute().getStrength();
        strengthLabel.setText( "K = " + STRENGTH_FORMAT.format( k ) );
    }

}
