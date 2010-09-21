package edu.colorado.phet.rotation.controls;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.motion.graphs.GraphSelectionControl;
import edu.colorado.phet.common.motion.graphs.GraphSetModel;
import edu.colorado.phet.common.motion.graphs.GraphSuiteSet;
import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
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

public class RotationControlPanel extends HorizontalLayoutPanel implements Resettable {
    private SymbolKeyButton symbolKeyButton;

    public RotationControlPanel( RulerNode rulerNode, GraphSuiteSet rotationGraphSet, GraphSetModel graphSetModel,
                                 VectorViewModel vectorViewModel, JFrame parentFrame, final RotationBody ladybug,
                                 final RotationBody beetle, AbstractRotationModule module, AngleUnitModel angleUnitModel, final RotationPlatform platform ) {
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        symbolKeyButton = new SymbolKeyButton( parentFrame );

        VerticalLayoutPanel rightPanel = new VerticalLayoutPanel();
        rightPanel.add( symbolKeyButton );

        final JCheckBox beetleGraph = new JCheckBox( RotationStrings.getString( "controls.show.beetle.graph" ), beetle.getDisplayGraph() );
        beetleGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                beetle.setDisplayGraph( beetleGraph.isSelected() );
            }
        } );
        beetle.addListener( new RotationBody.Adapter() {
            public void displayGraphChanged() {
                beetleGraph.setSelected( beetle.getDisplayGraph() );
            }
        } );
        beetle.addListener( new RotationBody.Adapter() {
            public void platformStateChanged() {
                if ( beetle.isOnPlatform() ) {
                    beetle.setDisplayGraph( true );
                }
            }
        } );

        final JCheckBox ladybugGraph = new JCheckBox( RotationStrings.getString( "controls.show.ladybug.graph" ), ladybug.getDisplayGraph() );
        ladybugGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                ladybug.setDisplayGraph( ladybugGraph.isSelected() );
            }
        } );
        ladybug.addListener( new RotationBody.Adapter() {
            public void displayGraphChanged() {
                ladybugGraph.setSelected( ladybug.getDisplayGraph() );
            }
        } );

        final JCheckBox platformGraph = new JCheckBox( RotationStrings.getString( "controls.show.platform.graph" ), platform.getDisplayGraph() );
        platformGraph.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                platform.setDisplayGraph( platformGraph.isSelected() );
            }
        } );

        rightPanel.add( platformGraph );
        rightPanel.add( ladybugGraph );
        rightPanel.add( beetleGraph );

        final AngleUnitsSelectionControl unitsSelectionControl = new AngleUnitsSelectionControl( angleUnitModel );
        rightPanel.add( unitsSelectionControl );
        rightPanel.add( new ResetButton( module ) );
        rightPanel.add( new RulerButton( rulerNode ) );

        VerticalLayoutPanel leftPanel = new VerticalLayoutPanel();
        leftPanel.add( graphSelectionControl );
        leftPanel.add( Box.createRigidArea( new Dimension( 30, 30 ) ) );
        leftPanel.add( new ShowVectorsControl( vectorViewModel ) );

        add( leftPanel );
        add( rightPanel );
    }

    public void reset() {
        symbolKeyButton.reset();
    }
}
