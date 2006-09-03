/* Copyright 2004, Sam Reid */
package edu.colorado.phet.movingman;


import edu.colorado.phet.common.view.help.HelpItem3;
import edu.colorado.phet.common.view.help.PhetGraphicTarget;

/**
 * User: Sam Reid
 * Date: May 9, 2005
 * Time: 8:50:59 AM
 * Copyright (c) May 9, 2005 by Sam Reid
 */

public class MMHelpSuite {
    private HelpItem3 dragTheMan;
    private MovingManModule module;
    private HelpItem3 dragSlider;
    private HelpItem3 typeValue;
    private HelpItem3 dragCursor;

    /*
     * 1.	Drag the man.
2.	adjust slider
3.	set a value for position
4.	drag the cursor.

     */
    public MMHelpSuite( MovingManModule module ) {
        this.module = module;
        init();

        setHelpEnabled( false );
    }

    private void init() {
        dragTheMan = new HelpItem3( module.getMovingManApparatusPanel(), new PhetGraphicTarget.Left( module.getManGraphic() ), 50, 0, "Drag the Man" );
        dragSlider = new HelpItem3( module.getMovingManApparatusPanel(), new PhetGraphicTarget.Right( module.getMovingManApparatusPanel().getPlotSet().getPositionPlot().getChartSlider() ), -30, 0, "Drag the Slider" );
        typeValue = new HelpItem3( module.getMovingManApparatusPanel(), module.getMovingManApparatusPanel().getPlotSet().getPositionPlotSuite().getTextBoxGraphic(), -30, 0, "Type a Value" );
        dragCursor = new HelpItem3( module.getMovingManApparatusPanel(), module.getMovingManApparatusPanel().getPlotSet().getPositionPlotSuite().getPlotDevice().getCursor(), 0, 30, "Drag the Cursor" );
    }

    public void init( MovingManModule movingManModule ) {
        movingManModule.getMovingManApparatusPanel().addGraphic( dragTheMan, Double.POSITIVE_INFINITY );
        movingManModule.getMovingManApparatusPanel().addGraphic( dragSlider, Double.POSITIVE_INFINITY );
        movingManModule.getMovingManApparatusPanel().addGraphic( typeValue, Double.POSITIVE_INFINITY );
        movingManModule.getMovingManApparatusPanel().addGraphic( dragCursor, Double.POSITIVE_INFINITY );
    }

    public void setHelpEnabled( boolean h ) {
        dragTheMan.setHelpEnabled( h );
        dragSlider.setHelpEnabled( h );
        typeValue.setHelpEnabled( h );
        dragCursor.setHelpEnabled( h );
    }
}
