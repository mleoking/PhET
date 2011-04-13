package edu.colorado.phet.licensing.rules;

/**
 * Indicates annotations that are compatible with PhET licensing.
 * Items matching any of the following will be suppressed from the licensing reports
 *
 * @author Sam Reid
 */
public class PhetRuleSet extends AbstractRuleSet {

    public PhetRuleSet() {
        super( new AbstractRule[] {

                //copyright by PhET
                new SourceStartsWith( "phet" ),
                new AuthorStartsWith( "phet" ),
                new AuthorStartsWith( "pixelzoom" ),

                //data files created by PhET
                new NameEndsWith( ".xml" ),
                new NameEndsWith( ".esp" ),
                new NameEndsWith( ".html" ),
                new NameEndsWith( ".properties" ),
                new NameEndsWith( ".txt" ),
                new NameEndsWith( ".csv" ),

                //suppress reporting duplicates
                new LicenseStartsWith( "same as" ),

                //compatible licenses
                new LicenseStartsWith( "PUBLIC DOMAIN" ),
                new LicenseStartsWith( "Used with permission" ),
                new LicenseStartsWith( "http://creativecommons.org" ),
                new LicenseStartsWith( "Creative Commons, royalty free, public domain" ),

                //source licenses
                new LicenseStartsWith( "Sun Graphics License" ),
                new LicenseStartsWith( "Piccolo2D License" ),
                new LicenseStartsWith( "common public license 1.0" ),
                new LicenseStartsWith( "Sun Binary Code License" ),
                new LicenseStartsWith( "Apache 2.0" ),
                new LicenseStartsWith( "Common Public License" ),
                new LicenseStartsWith( "LGPL" ),
                new LicenseStartsWith( "zlib/libpng license" ),
                new LicenseStartsWith( "Scala License (BSD-Style)" ),

                //allowed clip art sources
//microsoft clip art no longer approved for usage, see Unfuddle #1059
                new SourceStartsWith( "clker.com" ),//open source clip art site, see #1080
                new SourceStartsWith( "java" )//see http://java.sun.com/developer/techDocs/hi/repository/
//                new NotMicrosoftClipArt()
        } );
    }
}
