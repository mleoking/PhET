package edu.colorado.phet.buildanatom.modules.game.view;

import java.awt.*;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.modules.game.model.AtomValue;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToCountsProblem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.Property;
import edu.colorado.phet.common.phetcommon.util.Function0;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * @author Sam Reid
 */
public class SymbolToCountsView extends ProblemView {

    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_HOW_MANY_PARTICLES );

    private final SymbolIndicatorNode symbolIndicatorNode;
    private final MultiEntryPanel multiEntryPanel;

    public SymbolToCountsView( BuildAnAtomGameModel model, GameCanvas canvas, SymbolToCountsProblem howManyParticlesProblem ) {
        super( model, canvas, howManyParticlesProblem );
        symbolIndicatorNode = new SymbolIndicatorNode( howManyParticlesProblem.getAnswer().toAtom(getClock()), true );
        symbolIndicatorNode.scale( 2.25 );
        symbolIndicatorNode.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width / 4 - symbolIndicatorNode.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - symbolIndicatorNode.getFullBounds().getHeight() / 2 );

        multiEntryPanel = new MultiEntryPanel(  );
        multiEntryPanel.setOffset( BuildAnAtomDefaults.STAGE_SIZE.width *3/ 4 - multiEntryPanel.getFullBounds().getWidth() / 2, BuildAnAtomDefaults.STAGE_SIZE.height / 2 - multiEntryPanel.getFullBounds().getHeight() / 2 );
        multiEntryPanel.addChangeListener(new ChangeListener(){
            public void stateChanged( ChangeEvent e ) {
                enableCheckButton();
            }
        });

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
            protonEntryPanel = new EntryPanel( BuildAnAtomStrings.PROTONS_READOUT, protonGuess );
            addChild( protonEntryPanel );
            neutronEntryPanel = new EntryPanel( BuildAnAtomStrings.NEUTRONS_READOUT, neutronGuess );
            addChild( neutronEntryPanel );
            electronEntryPanel = new EntryPanel( BuildAnAtomStrings.ELECTRONS_READOUT, electronGuess );
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
            protonEntryPanel.setEditable( false );
            neutronEntryPanel.setEditable( false );
            electronEntryPanel.setEditable(false);
        }

        public void addChangeListener( final ChangeListener changeListener ) {
            final SimpleObserver observer = new SimpleObserver() {
                public void update() {
                    changeListener.stateChanged( null );
                }
            };
            protonGuess.addObserver( observer, false );
            neutronGuess.addObserver( observer, false );
            electronGuess.addObserver( observer, false );
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
        private final static Font FONT = new PhetFont( 30 );
        private final PText label;
        private final ValueNode valueNode;
        private final Property<Boolean> editable;

        public EntryPanel( String name, final Property<Integer> property ) {
            label = new PText( name ) {{
                setFont( FONT );
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
            editable = new Property<Boolean>( true );
            valueNode = new ValueNode( property, 0, 30, 1, editable, ValueNode.DEFAULT_NUMBER_FORMAT, new Function0.Constant<Color>( Color.black ) );
            valueNode.setScale( label.getFullBoundsReference().height / valueNode.getFullBoundsReference().height * 0.9 );
            addChild( valueNode );
            valueNode.setOffset( 0, label.getFullBounds().getHeight() / 2 - valueNode.getFullBounds().getHeight() / 2 );
        }

        public double getLabelWidth() {
            return label.getFullBounds().getWidth();
        }

        public void setSpinnerX( double x ) {
            valueNode.setOffset( x, valueNode.getOffset().getY() );
        }

        public void setEditable( boolean b ) {
            editable.setValue( b );
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
