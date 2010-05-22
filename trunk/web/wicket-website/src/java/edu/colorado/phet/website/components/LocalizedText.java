package edu.colorado.phet.website.components;

import org.apache.log4j.Logger;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.parser.XmlTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import edu.colorado.phet.website.util.StringUtils;

/**
 * A block of unescaped text that looks up a translated string, and that can be passed MessageFormat-like arguments.
 */
public class LocalizedText extends WebComponent {

    private Object[] args = null;

    private static final Logger logger = Logger.getLogger( LocalizedText.class.getName() );

    /**
     * Create a block of unescaped text with the specified Wicket ID and translatable string key
     *
     * @param id  Wicket ID
     * @param key Translatable string key
     */
    public LocalizedText( String id, String key ) {
        this( id, new Model( key ) );
    }

    /**
     * Create a block of unescaped text with the specified Wicket ID, translatable string key and a list of
     * MessageFormat arguments
     *
     * @param id   Wicket ID
     * @param key  Translatable string key
     * @param args MessageFormat arguments
     */
    public LocalizedText( String id, String key, Object[] args ) {
        this( id, new Model( key ) );
        this.args = args;
    }

    public LocalizedText( String id, IModel model ) {
        super( id, model );
    }

    /*---------------------------------------------------------------------------*
    * implementation
    *----------------------------------------------------------------------------*/

    protected void onComponentTagBody( final MarkupStream markupStream, final ComponentTag openTag ) {
        String key = getModelObjectAsString();
        String body = getLocalizer().getString( key, this );
        if ( args != null ) {
            try {
                body = StringUtils.messageFormat( body, args, getLocale() );
            }
            catch( RuntimeException e ) {
                logger.warn( "message-format error" );
                body = "*error*";
            }
        }
        logger.debug( "body:\n" + body + "\n" );
        replaceComponentTagBody( markupStream, openTag, body );
    }

    protected void onComponentTag( ComponentTag tag ) {
        super.onComponentTag( tag );
        tag.setType( XmlTag.OPEN );
    }
}
