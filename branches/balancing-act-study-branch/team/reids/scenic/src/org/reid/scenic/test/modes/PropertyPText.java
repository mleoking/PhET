// Copyright 2002-2011, University of Colorado
package org.reid.scenic.test.modes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * PText that can be bound to external properties for text, color and paint.
 *
 * @author Sam Reid
 */
public class PropertyPText extends PText {

    public PropertyPText( String text ) {
        this( text, new PhetFont() );
    }

    public PropertyPText( String text, Font font ) {
        this( text, font, Color.black );
    }

    public PropertyPText( String text, Font font, Paint paint ) {
        this( text, font, new Property<Paint>( paint ) );
    }

    public PropertyPText( String text, Font font, ObservableProperty<Paint> paint ) {
        this( text, new Property<Font>( font ), paint );
    }

    public PropertyPText( String text, ObservableProperty<Font> font, ObservableProperty<Paint> paint ) {
        this( new Property<String>( text ), font, paint );
    }

    public PropertyPText( ObservableProperty<String> text, ObservableProperty<Font> font, ObservableProperty<Paint> paint ) {
        text.addObserver( new VoidFunction1<String>() {
            public void apply( String text ) {
                setText( text );
            }
        } );
        font.addObserver( new VoidFunction1<Font>() {
            public void apply( Font font ) {
                setFont( font );
            }
        } );
        paint.addObserver( new VoidFunction1<Paint>() {
            public void apply( Paint paint ) {
                setTextPaint( paint );
            }
        } );
    }
}
