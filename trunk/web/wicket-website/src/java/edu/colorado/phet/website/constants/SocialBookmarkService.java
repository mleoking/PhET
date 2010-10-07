package edu.colorado.phet.website.constants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

import edu.colorado.phet.website.util.links.RawLinker;

/**
 * Model of a social bookmarking service (like sharing for Facebook, tweeting links, or the more classic delicious /
 * digg models). They have two main things: an icon and a way to get a link to bookmark a specific URL.
 */
public abstract class SocialBookmarkService {
    /**
     * @return Path to image icon. Relative from server root. Starts with slash
     */
    public abstract String getIconPath();

    /**
     * Get the URL to use for bookmarking.
     *
     * @param relativeUrl Relative to server root, starts with slash
     * @param title       The default title for the bookmarking
     * @return Absolute URL
     * @throws UnsupportedEncodingException
     */
    public abstract String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException;

    public RawLinker getLinker( String relativeUrl, String title ) {
        try {
            return new RawLinker( getShareUrl( relativeUrl, title ) );
        }
        catch ( UnsupportedEncodingException e ) {
            throw new RuntimeException( e );
        }
    }

    public static String doubleEncode( String str ) throws UnsupportedEncodingException {
        return URLEncoder.encode( URLEncoder.encode( str, "UTF-8" ), "UTF-8" );
    }

    public static final SocialBookmarkService FACEBOOK = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/facebook.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "http://www.facebook.com/sharer.php?u=http%3A%2F%2Fphet.colorado.edu" + URLEncoder.encode( relativeUrl, "UTF-8" ) + "&t=" + URLEncoder.encode( title, "UTF-8" );
        }
    };

    public static final SocialBookmarkService TWITTER = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/twitter.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "https://twitter.com/share?url=http%3A%2F%2Fphet.colorado.edu" + URLEncoder.encode( relativeUrl, "UTF-8" ) + "&text=" + URLEncoder.encode( title, "UTF-8" );
        }
    };

    public static final SocialBookmarkService STUMBLE_UPON = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/stumbleupon.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "http://www.stumbleupon.com/submit?url=http%3A%2F%2Fphet.colorado.edu" + URLEncoder.encode( relativeUrl, "UTF-8" ) + "&title=" + URLEncoder.encode( title, "UTF-8" );
        }
    };

    public static final SocialBookmarkService DIGG = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/digg.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "http://digg.com/submit?phase=2&url=http%3A%2F%2Fphet.colorado.edu" + URLEncoder.encode( relativeUrl, "UTF-8" ) + "&title=" + URLEncoder.encode( title, "UTF-8" );
        }
    };

    public static final SocialBookmarkService REDDIT = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/reddit.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "http://www.reddit.com/login?dest=%2Fsubmit%3Furl%3Dhttp%3A%2F%2Fhttp%253A%252F%252Fphet.colorado.edu" + doubleEncode( relativeUrl ) + "%26title%3D" + doubleEncode( title );
        }
    };

    public static final SocialBookmarkService DELICIOUS = new SocialBookmarkService() {
        @Override
        public String getIconPath() {
            return "/images/icons/social/32/delicious.png";
        }

        @Override
        public String getShareUrl( String relativeUrl, String title ) throws UnsupportedEncodingException {
            return "https://secure.delicious.com/login?jump=http%3A%2F%2Fwww.delicious.com%2Fsave%3Furl%3Dhttp%253A%252F%252Fphet.colorado.edu" + doubleEncode( relativeUrl ) + "%26title%3D" + doubleEncode( title );
        }
    };

    public static final List<SocialBookmarkService> SERVICES = new LinkedList<SocialBookmarkService>();

    static {
        SERVICES.add( FACEBOOK );
        SERVICES.add( TWITTER );
        SERVICES.add( STUMBLE_UPON );
        SERVICES.add( DIGG );
        SERVICES.add( REDDIT );
        SERVICES.add( DELICIOUS );
    }
}
