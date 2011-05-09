// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.modesexample.malley;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.Resettable;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common.phetcommon.view.ControlPanel;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;

/**
 * Control panel for the Rectangles module.
 * Part of this panel updates when the mode changes.
 * This is solely for demonstration purposes; we could also have an entirely different control panel
 * for each mode, and the control panels would be swapped by RectangleModule.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class RectanglesControlPanel extends ControlPanel {

    private final GridPanel sizePanel;

    public RectanglesControlPanel( final Property<RectanglesMode> currentMode, ArrayList<RectanglesMode> modes, Resettable resettable ) {

        // Mode selector
        addControlFullWidth( new ModeSelectionControl( currentMode, modes ) );

        // Size controls for rectangles, updates when mode changes
        sizePanel = new GridPanel();
        sizePanel.setGridX( 0 ); //vertical
        addControlFullWidth( sizePanel );
        currentMode.addObserver( new SimpleObserver() {
            public void update() {
                updateSizeControls( currentMode.getValue().model.getRectangles() );
            }
        } );

        // Reset All button
        addResetAllButton( resettable );
    }

    /*
     * Delete any existing SizeControls, and add new controls for rectangles.
     */
    private void updateSizeControls( ArrayList<Rectangle> rectangles ) {
        //TODO in a production app, we should first iterate over sizePanel children and call SizeControl.cleanup to remove property observers.
        sizePanel.removeAll();
        int i = 1;
        for ( Rectangle rectangle : rectangles ) {
            String title = rectangle.displayName + " size";
            sizePanel.add( new SizeControl( title, rectangle.width, rectangle.height ) );
            i++;
        }
    }
}
