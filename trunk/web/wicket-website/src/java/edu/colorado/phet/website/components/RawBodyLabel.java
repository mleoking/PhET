package edu.colorado.phet.website.components;

import org.apache.wicket.model.IModel;

/**
 * Inserts raw text without escaping or the original surrounding tags.
 * <p/>
 * Example HTML: <span wicket:id="test">Test Label</span>
 * Example Java: add( new RawBodyLabel( "test", "Test 1 &amp; 2" ) );
 * <p/>
 * Example HTML result: Test 1 &amp; 2
 */
public class RawBodyLabel extends RawLabel {
    public RawBodyLabel( String id ) {
        super( id );
        setRenderBodyOnly( true );
    }

    public RawBodyLabel( String id, String label ) {
        super( id, label );
        setRenderBodyOnly( true );
    }

    public RawBodyLabel( String id, IModel model ) {
        super( id, model );
        setRenderBodyOnly( true );
    }
}
