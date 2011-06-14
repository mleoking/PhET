// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildanatom.modules.game.view;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.buildanatom.BuildAnAtomDefaults;
import edu.colorado.phet.buildanatom.BuildAnAtomStrings;
import edu.colorado.phet.buildanatom.model.ImmutableAtom;
import edu.colorado.phet.buildanatom.modules.game.model.BuildAnAtomGameModel;
import edu.colorado.phet.buildanatom.modules.game.model.SymbolToCountsProblem;
import edu.colorado.phet.buildanatom.view.SymbolIndicatorNode;
import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.umd.cs.piccolo.PNode;

/**
 * Game problem in which the user is shown the symbol view of an atom and is asked to fill in the proton/neutron/electron counts.
 * @author Sam Reid
 */
public class SymbolToCountsView extends ProblemView {

    private final ProblemDescriptionNode description = new ProblemDescriptionNode( BuildAnAtomStrings.GAME_HOW_MANY_PARTICLES );

    private final SymbolIndicatorNode symbolIndicatorNode;
    private final MultiEntryPanel multiEntryPanel;

    public SymbolToCountsView( BuildAnAtomGameModel model, BuildAnAtomGameCanvas canvas, SymbolToCountsProblem howManyParticlesProblem ) {
        super( model, canvas, howManyParticlesProblem );
        symbolIndicatorNode = new SymbolIndicatorNode( howManyParticlesProblem.getAnswer().toAtom(getClock()), true);
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

        // For this problem view, the check button is enabled right away.
        enableCheckButton();
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

        public ImmutableAtom getGuess() {
            return new ImmutableAtom( protonGuess.get(), neutronGuess.get(), electronGuess.get() );
        }

        public void displayAnswer( ImmutableAtom answer ) {
            protonGuess.set( answer.getNumProtons() );
            electronGuess.set( answer.getNumElectrons() );
            neutronGuess.set( answer.getNumNeutrons() );
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
    protected ImmutableAtom getGuess() {
        return multiEntryPanel.getGuess();
    }

    @Override
    protected void displayAnswer( ImmutableAtom answer ) {
        multiEntryPanel.displayAnswer(answer);
    }

    @Override
    protected void setGuessEditable( boolean guessEditable ) {
        multiEntryPanel.setPickable( guessEditable );
        multiEntryPanel.setChildrenPickable( guessEditable );
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
