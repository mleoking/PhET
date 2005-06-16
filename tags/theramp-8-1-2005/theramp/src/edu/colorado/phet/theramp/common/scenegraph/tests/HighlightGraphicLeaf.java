package edu.colorado.phet.theramp.common.scenegraph.tests;

import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.common.model.clock.ClockTickEvent;
import edu.colorado.phet.common.model.clock.ClockTickListener;
import edu.colorado.phet.theramp.common.scenegraph.AbstractGraphic;
import edu.colorado.phet.theramp.common.scenegraph.OutlineGraphic;

import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Sam Reid
 * Date: Jun 8, 2005
 * Time: 12:08:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class HighlightGraphicLeaf extends OutlineGraphic implements ClockTickListener {
    private AbstractGraphic child;
    private AbstractClock clock;

    public HighlightGraphicLeaf( AbstractGraphic child, AbstractClock clock, Color color, Stroke stroke ) {
        super( new Rectangle(), stroke );
        this.clock = clock;
        setColor( color );
        this.child = child;
        clock.addClockTickListener( this );
        setIgnoreMouse( true );
        setAntialias( true );
    }

    public void clockTicked( ClockTickEvent event ) {
        Shape b = child.getBoundsIn( getParent() );
        //todo only works if my transform is IDentity
        setShape( b );
    }
}
