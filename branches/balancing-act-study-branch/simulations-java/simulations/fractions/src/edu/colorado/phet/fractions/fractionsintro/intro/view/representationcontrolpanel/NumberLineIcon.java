// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionsintro.intro.view.representationcontrolpanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.fractions.fractionsintro.intro.view.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * Shows the icon on the representation control panel for the number line.
 *
 * @author Sam Reid
 */
public class NumberLineIcon extends RichPNode implements RepresentationIcon {
    public NumberLineIcon( final SettableProperty<Representation> chosenRepresentation ) {

        double dx = 5 * 10;
        addChild( new PhetPPath( new Line2D.Double( 0, 0, dx, 0 ) ) );
        {
            final PhetPPath zeroLine = new PhetPPath( new Line2D.Double( 0 * dx, -10, 0 * dx, 10 ), new BasicStroke( 1 ), Color.black );
            addChild( zeroLine );
            addChild( new PhetPText( "0", new PhetFont( 10 ) ) {{
                setOffset( zeroLine.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, zeroLine.getFullBounds().getMaxY() );
            }} );
        }

        final PhetPPath oneLine = new PhetPPath( new Line2D.Double( 1 * dx, -10, 1 * dx, 10 ), new BasicStroke( 1 ), Color.black );
        addChild( oneLine );

        addChild( new PhetPText( "1", new PhetFont( 10 ) ) {{
            setOffset( oneLine.getFullBounds().getCenterX() - getFullBounds().getWidth() / 2, oneLine.getFullBounds().getMaxY() );
        }} );

        final PhetPPath child = new PhetPPath( getFullBounds(), new Color( 0, 0, 0, 0 ) );
        addChild( child );
        child.moveToBack();

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mouseReleased( PInputEvent event ) {
                chosenRepresentation.set( Representation.NUMBER_LINE );
            }
        } );
        scale( 1.2 );
    }

    public PNode getNode() {
        return this;
    }

    public Representation getRepresentation() {
        return Representation.NUMBER_LINE;
    }
}