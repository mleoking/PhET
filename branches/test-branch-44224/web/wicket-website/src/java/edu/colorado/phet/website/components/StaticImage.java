package edu.colorado.phet.website.components;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.model.IModel;

/**
 * Handles an image with src and alt attributes. wicket:id should be placed upon the img tag itself, and then
 * the existing (or nonexisting) attributes will be overwritten.
 */
public class StaticImage extends WebComponent {

    private String url = null;
    private String alt = null;
    private IModel altModel = null;

    /**
     * Create an image with a standard URL for a src attribute.
     *
     * @param id  The Wicket ID for the image
     * @param src The src attribute (URL)
     * @param alt The alt text for accessibility
     */
    public StaticImage( String id, String src, String alt ) {
        super( id );
        this.url = src;
        this.alt = alt;
    }

    /**
     * Create an image with a model for an alt attribute.
     *
     * @param id       The Wicket ID for the image
     * @param src      The src attribute (URL)
     * @param alt      Should be null. Exists to differentiate between overloaded constructors
     * @param altModel The alt model. Will be converted to a string for accessibility
     */
    public StaticImage( String id, String src, String alt, IModel altModel ) {
        super( id );
        this.url = src;
        this.alt = alt;
        this.altModel = altModel;
    }

    /*---------------------------------------------------------------------------*
    * implementation
    *----------------------------------------------------------------------------*/

    protected void onComponentTag( ComponentTag tag ) {
        checkComponentTag( tag, "img" );
        super.onComponentTag( tag );
        tag.put( "src", url );
        if ( alt != null ) {
            tag.put( "alt", alt );
        }
        if ( altModel != null ) {
            tag.put( "alt", altModel.getObject().toString() );
        }
    }

    @Override
    protected boolean getStatelessHint() {
        return true;
    }
}