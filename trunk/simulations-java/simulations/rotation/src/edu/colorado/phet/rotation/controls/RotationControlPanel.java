package edu.colorado.phet.rotation.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.piccolophet.nodes.RulerNode;
import edu.colorado.phet.rotation.AbstractRotationModule;
import edu.colorado.phet.rotation.RotationStrings;
import edu.colorado.phet.rotation.model.AngleUnitModel;
import edu.colorado.phet.rotation.model.RotationBody;
import edu.colorado.phet.rotation.model.RotationPlatform;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:51:51 AM
 */

public class RotationControlPanel extends JPanel implements Resettable {
    private SymbolKeyButton symbolKeyButton;

    public RotationControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel,
                                 VectorViewModel vectorViewModel, JFrame parentFrame, final RotationBody ladybug,
                                 final RotationBody beetle, AbstractRotationModule module, AngleUnitModel angleUnitModel, final RotationPlatform platform ) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        symbolKeyButton = new SymbolKeyButton( parentFrame );

        VerticalLayoutPanel box = new VerticalLayoutPanel();
        box.add( symbolKeyButton );

        final JCheckBox beetleGraph = new JCheckBox( RotationStrings.getString( "show.beetle.graph" ), beetle.getDisplayGraph() );
        beetleGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beetle.setDisplayGraph( beetleGraph.isSelected() );
            }
        } );

        final JCheckBox ladybugGraph = new JCheckBox( RotationStrings.getString( "show.ladybug.graph" ), ladybug.getDisplayGraph() );
        ladybugGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ladybug.setDisplayGraph( ladybugGraph.isSelected() );
            }
        } );

        final JCheckBox platformGraph = new JCheckBox( RotationStrings.getString( "show.platform.graph" ), platform.getDisplayGraph() );
        platformGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                platform.setDisplayGraph( platformGraph.isSelected() );
            }
        } );

        box.add( platformGraph );
        box.add( ladybugGraph );
        box.add( beetleGraph );

        box.add( new AngleUnitsSelectionControl( angleUnitModel ) );
        box.add( new ResetButton( module ) );

        add( graphSelectionControl, getConstraints( 0, 0 ) );
        add( box, getConstraints( 2, 0 ) );
        add( new RulerButton( rulerNode ), getConstraints( 2, 1 ) );
        add( new ShowVectorsControl( vectorViewModel ), getConstraints( 0, 1 ) );
    }

    private GridBagConstraints getConstraints( int gridX, int gridY ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }

    public void reset() {
        symbolKeyButton.reset();
    }
}
