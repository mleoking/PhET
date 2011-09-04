// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculeshapes.control;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.moleculeshapes.MoleculeShapesConstants;
import edu.colorado.phet.moleculeshapes.MoleculeShapesResources.Strings;
import edu.colorado.phet.moleculeshapes.jme.JmeActionListener;
import edu.colorado.phet.moleculeshapes.jme.JmeUtils;
import edu.colorado.phet.moleculeshapes.model.MoleculeModel;

import static edu.colorado.phet.moleculeshapes.view.MoleculeJMEApplication.showLonePairs;

/**
 * Allows toggling visibility on the lone pairs in the scene
 */
public class ToggleLonePairsButton extends TextButtonNode {

    private final MoleculeModel molecule;

    public ToggleLonePairsButton( MoleculeModel molecule ) {
        super( "<placeholder>", new PhetFont( 14 ), Color.ORANGE );
        this.molecule = molecule;

        // when clicked, toggle the property in the JME thread
        addActionListener( new JmeActionListener( new Runnable() {
            public void run() {
                showLonePairs.set( !showLonePairs.get() );
            }
        } ) );

        // when the property changes, update our state in the Swing thread
        showLonePairs.addObserver( JmeUtils.swingObserver( new Runnable() {
            public void run() {
                updateState();
            }
        } ) );

        // when a molecule group changes, update (since visibility could matter)
        molecule.onGroupChanged.addTarget( JmeUtils.swingTarget( new Runnable() {
            public void run() {
                updateState();
            }
        } ) );

        // sanity check for at the start
        updateState();
    }

    /**
     * Updates the visual state. Call this in the Swing EDT
     */
    private void updateState() {
        // update the text state.
        setText( showLonePairs.get() ? Strings.CONTROL__HIDE_LONE_PAIRS : Strings.CONTROL__SHOW_LONE_PAIRS );

        // center horizontally, and place below the lone pair image
        setOffset(
                ( MoleculeShapesConstants.CONTROL_PANEL_INNER_WIDTH - getFullBounds().getWidth() ) / 2,
                getOffset().getY() );

        // enabled if there are lone pairs to hide
        setEnabled( !molecule.getLonePairs().isEmpty() );
    }
}
