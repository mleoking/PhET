package edu.colorado.phet.rotation;

import edu.colorado.phet.rotation.controls.GraphSelectionControl;
import edu.colorado.phet.rotation.controls.ShowVectorsControl;
import edu.colorado.phet.rotation.controls.SymbolKey;
import edu.colorado.phet.rotation.controls.VectorViewModel;
import edu.colorado.phet.rotation.graphs.GraphSetModel;
import edu.colorado.phet.rotation.graphs.RotationGraphSet;

import javax.swing.*;
import java.awt.*;

/**
 * User: Sam Reid
 * Date: Jan 9, 2007
 * Time: 7:51:51 AM
 * Copyright (c) Jan 9, 2007 by Sam Reid
 */

public class RotationControlPanel extends JPanel {
    public RotationControlPanel( RotationGraphSet rotationGraphSet, GraphSetModel graphSetModel, VectorViewModel vectorViewModel ) {
        super( new GridBagLayout() );
        GraphSelectionControl graphSelectionControl = new GraphSelectionControl( rotationGraphSet, graphSetModel );
        SymbolKey symbolKey = new SymbolKey();
        ShowVectorsControl showVectorsControl = new ShowVectorsControl( vectorViewModel );

        add( graphSelectionControl, getConstraints( 0, 0 ) );
//        add( Box.createRigidArea( new Dimension( 50, 10 ) ), getConstraints( 1, 0 ) );
        add( symbolKey, getConstraints( 2, 0 ) );
        add( showVectorsControl, getConstraints( 0, 1 ) );

    }

    private GridBagConstraints getConstraints( int gridX, int gridY ) {
        return new GridBagConstraints( gridX, gridY, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets( 10, 10, 10, 10 ), 0, 0 );
    }
}
