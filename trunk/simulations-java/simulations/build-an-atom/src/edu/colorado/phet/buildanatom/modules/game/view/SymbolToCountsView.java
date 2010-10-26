package edu.colorado.phet.buildanatom.modules.game.view;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.model.BuildAnAtomClock;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToCountsProblem;
import edu.colorado.phet.buildanatom.modules.game.model.Problem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * @author Sam Reid
 */
public class SymbolToCountsView extends ProblemView {

    private final ProblemDescriptionNode description = new ProblemDescriptionNode( "How many particles?" );//todo i18n

    private final SymbolIndicatorNode symbolIndicatorNode;
    private final MultiEntryPanel multiEntryPanel;

    public SymbolToCountsView( BuildAnAtomGameModel model, GameCanvas canvas, SymbolToCountsProblem howManyParticlesProblem ) {
        super( model, canvas, howManyParticlesProblem );
        symbolIndicatorNode = new SymbolIndicatorNode( howManyParticlesProblem.getAnswer().toAtom(getClock()) );
        symbolIndicatorNode.scale( 2.25 );
        symbolIndicatorNode.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width / 4 - symbolIndicatorNode.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        multiEntryPanel = new MultiEntryPanel(  );
        multiEntryPanel.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width *3/ 4 - multiEntryPanel.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - multiEntryPanel.getFullBounds().getHeight() / 2 );

        description.centerAbove(multiEntryPanel);
    }

    public static class MultiEntryPanel extends PNode{
        private final EntryPanel protonEntryPanel;
        private final EntryPanel neutronEntryPanel;
        private final EntryPanel electronEntryPanel;

        public Property<Integer> protonGuess = new Property<Integer>(0);
        public Property<Integer> neutronGuess = new Property<Integer>(0);
        public Property<Integer> electronGuess = new Property<Integer>(0);

        public MultiEntryPanel(  ) {
            protonEntryPanel = new EntryPanel( "Protons:", protonGuess );
            addChild( protonEntryPanel );
            neutronEntryPanel = new EntryPanel( "Neutrons:", neutronGuess );
            addChild( neutronEntryPanel );
            electronEntryPanel = new EntryPanel( "Electrons:", electronGuess );
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

        public AtomValue getGuess() {
            return new AtomValue( protonGuess.getValue(), neutronGuess.getValue(), electronGuess.getValue() );
        }

        public void displayAnswer( AtomValue answer ) {
            protonGuess.setValue( answer.getProtons() );
            electronGuess.setValue( answer.getElectrons() );
            neutronGuess.setValue( answer.getNeutrons() );
        }
    }

    @Override
    protected AtomValue getGuess() {
        return multiEntryPanel.getGuess();
    }

    @Override
    protected void displayAnswer( AtomValue answer ) {
        multiEntryPanel.displayAnswer(answer);
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        multiEntryPanel.setPickable( guessEditable );
        multiEntryPanel.setChildrenPickable( guessEditable );
    }

    public static class EntryPanel extends PNode {
        private final PText label;
        private final PSwing spinnerPSwing;

        public EntryPanel( String name, final Property<Integer> property ) {
            label = new PText( name ) {{
                setFont( new PhetFont( 30 ) );
            }};
            addChild( label );
            final JSpinner spinner = new JSpinner( new SpinnerNumberModel( 0, 0, 30, 1 ) ){{
                    addChangeListener( new ChangeListener() {
                        public void stateChanged( ChangeEvent e ) {
                            property.setValue( (Integer) getValue() );
                        }
                    } );
                  }};
            property.addObserver( new SimpleObserver() {
                public void update() {
                    spinner.setValue( property.getValue() );
                }
            } );
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
