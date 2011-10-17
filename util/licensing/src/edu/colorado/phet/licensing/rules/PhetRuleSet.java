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
                new NameEndsWith( ".mol2" ),//File Created by: Spartan '08 Export for molecule polarity
                new NameEndsWith( ".pdb" ),//Protein data base file such as sucrose.pdb used in sugar-and-salt-solutions for showing sugar in 3d

                //suppress reporting duplicates
                new LicenseStartsWith( "same as" ),

                //compatible licenses
                new LicenseStartsWith( "PUBLIC DOMAIN" ),
                new LicenseStartsWith( "NASA" ),//Nasa is okay as long as we give attribution, see http://www.nasa.gov/audience/formedia/features/MP_Photo_Guidelines.html
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
                new LicenseStartsWith( "Custom Apple license" ),
                new LicenseStartsWith( "New BSD License" ),
                new LicenseStartsWith( "MIT License" ),
                new LicenseStartsWith( "Apache Software License 2.0" ),
                new LicenseStartsWith( "Apache" ),
                new LicenseStartsWith( "zlib" ),

                //allowed clip art sources
//microsoft clip art no longer approved for usage, see Unfuddle #1059
                new SourceStartsWith( "clker.com" ),//open source clip art site, see #1080
                new SourceStartsWith( "java" ),//see http://java.sun.com/developer/techDocs/hi/repository/
                new SourceStartsWith( "Wikimedia Commons" ),
                new SourceStartsWith( "Open Clip Art Library" ),
                new SourceStartsWith( "Open Clip Art" ),
                new SourceStartsWith( "Open Source Clip Art" ),
                new SourceStartsWith( "homemade image created by Oliver Nix" ),
                new SourceStartsWith( "created by ON" )
//                new NotMicrosoftClipArt()
        } );
    }
}
