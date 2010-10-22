package edu.colorado.phet.buildanatom.modules.game.view;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.HowManyParticlesProblem;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class HowManyParticlesProblemView extends ProblemView {

    private final PText description = new PText( "How many particles?" ) {{
        setFont( new PhetFont( 20, true ) );
        setOffset( BuildAnAtomDefaults.STAGE_SIZE.width - getFullBounds().getWidth() - 200, 30 );
    }};

    private final SymbolIndicatorNode symbolIndicatorNode;
    private final MultiEntryPanel multiEntryPanel;

    public HowManyParticlesProblemView( BuildAnAtomGameModel model, GameCanvas canvas, HowManyParticlesProblem howManyParticlesProblem ) {
        super( model, canvas, howManyParticlesProblem );
        symbolIndicatorNode = new SymbolIndicatorNode( howManyParticlesProblem.getAtom() );
        symbolIndicatorNode.scale( 2.25 );
        symbolIndicatorNode.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width / 4 - symbolIndicatorNode.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        multiEntryPanel = new MultiEntryPanel( howManyParticlesProblem );
        multiEntryPanel.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width *3/ 4 - multiEntryPanel.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - multiEntryPanel.getFullBounds().getHeight() / 2 );
    }

    public static class MultiEntryPanel extends PNode{
        public MultiEntryPanel( final Problem problem) {
            final EntryPanel protonEntryPanel = new EntryPanel( "Protons:", new ValueSetter() {
                public void setValue( int value ) {
                    problem.setGuessedProtons( value );
                }
            } );
            addChild( protonEntryPanel );
            final EntryPanel neutronEntryPanel = new EntryPanel( "Neutrons:", new ValueSetter() {
                public void setValue( int value ) {
                    problem.setGuessedNeutrons( value );
                }
            } );
            addChild( neutronEntryPanel );
            final EntryPanel electronEntryPanel = new EntryPanel( "Electrons:", new ValueSetter() {
                public void setValue( int value ) {
                    problem.setGuessedElectrons( value );
                }
            } );
            addChild( electronEntryPanel );
            double maxLabelWidth = MathUtil.max( new double[] { protonEntryPanel.getLabelWidth(), neutronEntryPanel.getLabelWidth(), electronEntryPanel.getLabelWidth() } );
            int distanceBetweenSpinnerAndLabel = 20;
            final double spinnerX = maxLabelWidth + distanceBetweenSpinnerAndLabel;
            protonEntryPanel.setSpinnerX( spinnerX );
            neutronEntryPanel.setSpinnerX( spinnerX );
            electronEntryPanel.setSpinnerX( spinnerX );

            final double verticalSpacing = 20;
            protonEntryPanel.setOffset( 0,0);
            neutronEntryPanel.setOffset( protonEntryPanel.getFullBounds().getX(), protonEntryPanel.getFullBounds().getMaxY() + verticalSpacing );
            electronEntryPanel.setOffset( neutronEntryPanel.getFullBounds().getX(), neutronEntryPanel.getFullBounds().getMaxY() + verticalSpacing );
        }
    }

    public interface ValueSetter {
        public void setValue(int value);
    }

    public static class EntryPanel extends PNode {
        private final PText label;
        private final PSwing spinnerPSwing;

        public EntryPanel( String name, final ValueSetter valueSetter ) {
            label = new PText( name ) {{
                setFont( new PhetFont( 20, true ) );
            }};
            addChild( label );
            JSpinner spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            valueSetter.setValue( (Integer) getValue() );
                        }
                    } );
                  }};
            spinnerPSwing = new PSwing( spinner );
            spinnerPSwing.scale( 2 );
            addChild( spinnerPSwing );
            spinnerPSwing.setOffset( 0, label.getFullBounds().getHeight() / 2 - spinnerPSwing.getFullBounds().getHeight() / 2 );
        }

        public double getLabelWidth() {
            return label.getFullBounds().getWidth();
        }

        public void setSpinnerX( double x ) {
            spinnerPSwing.setOffset( x, spinnerPSwing.getOffset().getY() );
        }
    }

    @Override
    public void init() {
        super.init();
        addChild( description );
        addChild( symbolIndicatorNode );
        addChild( multiEntryPanel );
    }

    @Override
    public void teardown() {
        super.teardown();
        removeChild( description );
        removeChild( symbolIndicatorNode );
        removeChild( multiEntryPanel );
    }

}
