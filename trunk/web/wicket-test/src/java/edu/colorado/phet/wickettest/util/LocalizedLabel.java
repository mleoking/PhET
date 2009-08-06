package edu.colorado.phet.wickettest.util;

import java.text.MessageFormat;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class LocalizedLabel extends WebComponent {

    private Object[] args = null;

    public LocalizedLabel( String id ) {
        super( id );
    }

    public LocalizedLabel( String id, String text ) {
        this( id, new Model( text ) );
    }

    public LocalizedLabel( String id, String text, Object[] args ) {
        this( id, new Model( text ) );
        this.args = args;
    }

    public LocalizedLabel( String id, IModel model ) {
        super( id, model );
    }

    protected void onComponentTagBody( final MarkupStream markupStream, final ComponentTag openTag ) {
        String key = getModelObjectAsString();
        String body = getLocalizer().getString( key, this );
        if ( args != null ) {
            try {
                body = MessageFormat.format( body, args );
            }
            catch( RuntimeException e ) {
                System.out.println( "message-format error" );
                body = "*error*";
            }
        }
        System.out.println( "body:\n" + body + "\n" );
        replaceComponentTagBody( markupStream, openTag, body );
    }

    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        tag.setType( XmlTag.OPEN );
    }
}
