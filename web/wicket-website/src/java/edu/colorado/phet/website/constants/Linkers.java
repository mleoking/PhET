package edu.colorado.phet.website.constants;

import edu.colorado.phet.website.util.links.RawLinkable;
import edu.colorado.phet.website.util.links.RawLinker;

public class Linkers {
    public static final RawLinkable PHET_TRANSLATION_UTILITY_JAR = new RawLinker( "/files/translation-utility/translation-utility.jar" );
    public static final RawLinkable JAVA_COMMON_STRINGS_JAR = new RawLinker( "/sims/java-common-strings/java-common-strings_en.jar" );
    public static final RawLinkable FLASH_COMMON_STRINGS_JAR = new RawLinker( "/sims/flash-common-strings/flash-common-strings_en.jar" );

    
    public static final class HelpMailer extends RawLinker {

        public HelpMailer() {
            super( WebsiteConstants.HELP_EMAIL );
        }

        public HelpMailer( String subject ) {
            super( WebsiteConstants.HELP_EMAIL + "?subject=" + subject.replace( " ", "%20" ) );
        }
    }
}
