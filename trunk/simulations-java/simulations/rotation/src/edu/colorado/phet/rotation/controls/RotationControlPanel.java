package edu.colorado.phet.rotation.controls;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.AngleUnitModel;
import edu.colorado.phet.rotation.controls.RulerButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:51:51 AM
 */

public class RotationControlPanel extends JPanel {
    public RotationControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel, VectorViewModel vectorViewModel, JFrame parentFrame, final RotationBody ladybug, final RotationBody beetle, AbstractRotationModule module, AngleUnitModel angleUnitModel ) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        SymbolKeyButton symbolKey = new SymbolKeyButton( parentFrame );
        ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );

        VerticalLayoutPanel box = new VerticalLayoutPanel();
        RulerButton rulerButton = new RulerButton( rulerNode );
        box.add( symbolKey );

        final JCheckBox beetleGraph = new JCheckBox( "Show Beetle Graph", true );
        beetleGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beetle.setDisplayGraph( beetleGraph.isSelected() );
            }
        } );


        final JCheckBox ladybugGraph = new JCheckBox( "Show Ladybug Graph", true );
        ladybugGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ladybug.setDisplayGraph( ladybugGraph.isSelected() );
            }
        } );

        box.add( ladybugGraph );
        box.add( beetleGraph );

        final AngleUnitsSelectionControl angleUnitsSelectionControl = new AngleUnitsSelectionControl( angleUnitModel );
        box.add( angleUnitsSelectionControl );

        ResetButton resetButton = new ResetButton( module );
        box.add( resetButton );

        add( graphSelectionControl, getConstraints( 0, 0 ) );
        add( box, getConstraints( 2, 0 ) );
        add( rulerButton, getConstraints( 2, 1 ) );
        add( showVectorsControl, getConstraints( 0, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }
}
