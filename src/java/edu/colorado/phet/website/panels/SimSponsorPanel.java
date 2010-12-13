package edu.colorado.phet.website.panels;

import java.util.Random;

import org.apache.wicket.markup.html.basic.Label;

import edu.colorado.phet.website.components.RawLink;
import edu.colorado.phet.website.components.StaticImage;
import edu.colorado.phet.website.constants.Images;
import edu.colorado.phet.website.util.PageContext;

public class SimSponsorPanel extends PhetPanel {
    public SimSponsorPanel( String id, final PageContext context, Sponsor sponsor ) {
        super( id, context );

        RawLink link = new RawLink( "link", sponsor.getUrl() );
        add( link );
        link.add( new StaticImage( "image", sponsor.getImageUrl(), sponsor.getImageAlt() ) );

        // TODO: i18n
        add( new Label( "before-text", "PhET is supported by" + ( sponsor.needsArticle() ? " the" : "" ) ) );
        add( new Label( "after-text", "and educators like you." ) );
        add( new Label( "thanks", "Thanks!" ) );
    }

    public static abstract class Sponsor {
        public abstract String getUrl();

        public abstract String getImageUrl();

        public abstract String getImageAlt();

        public boolean needsArticle() {
            return true;
        }
    }

    public static Sponsor HEWLETT_FOUNDATION = new Sponsor() {
        public String getUrl() {
            return "http://www.hewlett.org/";
        }

        public String getImageUrl() {
            return Images.LOGO_HEWLETT;
        }

        public String getImageAlt() {
            return "Hewlett Foundation logo";
        }

        @Override
        public boolean needsArticle() {
            return false;
        }
    };

    public static Sponsor NSF = new Sponsor() {
        public String getUrl() {
            return "http://www.nsf.gov/";
        }

        public String getImageUrl() {
            return Images.LOGO_NSF;
        }

        public String getImageAlt() {
            return "NSF logo";
        }
    };

    public static Sponsor KSU = new Sponsor() {
        public String getUrl() {
            return "http://www.ksu.edu.sa/";
        }

        public String getImageUrl() {
            return Images.LOGO_ECSME;
        }

        public String getImageAlt() {
            return "The King Saud (ESCME) Logo";
        }

        @Override
        public boolean needsArticle() {
            return false;
        }
    };

    public static Sponsor ODONNELL_FOUNDATION = new Sponsor() {
        public String getUrl() {
            return "http://www.odf.org/";
        }

        public String getImageUrl() {
            return Images.LOGO_ODONNELL_LARGE;
        }

        public String getImageAlt() {
            return "O'Donnell Foundation logo";
        }
    };

    public static Random random = new Random();

    public static Sponsor[] ActiveSponsors = new Sponsor[] { HEWLETT_FOUNDATION, NSF, KSU, ODONNELL_FOUNDATION };

    public static Sponsor chooseRandomActiveSponsor() {
        return ActiveSponsors[random.nextInt( ActiveSponsors.length )];
    }
}