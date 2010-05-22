package edu.colorado.phet.website.test;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.PageParameters;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.templates.PhetPage;

public class PreventXSSTest extends PhetPage {
    public PreventXSSTest( PageParameters parameters ) {
        super( parameters );

        //addTitle( new ResourceModel( "style.title" ) );
        addTitle( "XSS Prevention Testing &amp; Other Things" );

        WebMarkupContainer container = new WebMarkupContainer( "container" );
        add( container );
        container.add( new AttributeAppender( "attrA", true, new Model( "\" style=\"background-image: expression(alert('XSS'))\" place=\"" ), "" ) );
        container.add( new AttributeModifier( "attrB", true, new Model( "\" style=\"background-image: expression(alert('XSS'))\" place=\"" ) ) );

        container.add( new Label( "label", "<script>alert('XSS')</script>&amp;<b>Test</b>" ) );
    }

    @Override
    public String getStyle( String key ) {
        if ( key.equals( "style.xatr" ) ) {
            return "\" style=\"background-image: expression(alert('XSS'))\" place=\"";
        }
        if ( key.equals( "style.mess" ) ) {
            return "<script>alert('XSS')</script>&amp;<b>Test</b>";
        }
        if ( key.equals( "style.title" ) ) {
            return "XSS Prevention Testing &amp; Other things";
        }
        return super.getStyle( key );
    }
}
