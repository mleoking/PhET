package edu.colorado.phet.buildtools.html5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.Pair;

public class FontAwesomeToScenery {
    public static void main( String[] args ) throws IOException {
        String svg = FileUtils.loadFileAsString( new File( "C:\\workingcopy\\phet\\svn-1.7\\trunk\\simulations-html\\build-tools\\data\\font-awesome.txt" ) );

        String map = ".icon-glass:before                { content: \"\\f000\"; }\n" +
                     ".icon-music:before                { content: \"\\f001\"; }\n" +
                     ".icon-search:before               { content: \"\\f002\"; }\n" +
                     ".icon-envelope:before             { content: \"\\f003\"; }\n" +
                     ".icon-heart:before                { content: \"\\f004\"; }\n" +
                     ".icon-star:before                 { content: \"\\f005\"; }\n" +
                     ".icon-star-empty:before           { content: \"\\f006\"; }\n" +
                     ".icon-user:before                 { content: \"\\f007\"; }\n" +
                     ".icon-film:before                 { content: \"\\f008\"; }\n" +
                     ".icon-th-large:before             { content: \"\\f009\"; }\n" +
                     ".icon-th:before                   { content: \"\\f00a\"; }\n" +
                     ".icon-th-list:before              { content: \"\\f00b\"; }\n" +
                     ".icon-ok:before                   { content: \"\\f00c\"; }\n" +
                     ".icon-remove:before               { content: \"\\f00d\"; }\n" +
                     ".icon-zoom-in:before              { content: \"\\f00e\"; }\n" +
                     "\n" +
                     ".icon-zoom-out:before             { content: \"\\f010\"; }\n" +
                     ".icon-off:before                  { content: \"\\f011\"; }\n" +
                     ".icon-signal:before               { content: \"\\f012\"; }\n" +
                     ".icon-cog:before                  { content: \"\\f013\"; }\n" +
                     ".icon-trash:before                { content: \"\\f014\"; }\n" +
                     ".icon-home:before                 { content: \"\\f015\"; }\n" +
                     ".icon-file:before                 { content: \"\\f016\"; }\n" +
                     ".icon-time:before                 { content: \"\\f017\"; }\n" +
                     ".icon-road:before                 { content: \"\\f018\"; }\n" +
                     ".icon-download-alt:before         { content: \"\\f019\"; }\n" +
                     ".icon-download:before             { content: \"\\f01a\"; }\n" +
                     ".icon-upload:before               { content: \"\\f01b\"; }\n" +
                     ".icon-inbox:before                { content: \"\\f01c\"; }\n" +
                     ".icon-play-circle:before          { content: \"\\f01d\"; }\n" +
                     ".icon-repeat:before               { content: \"\\f01e\"; }\n" +
                     "\n" +
                     "/* \\f020 doesn't work in Safari. all shifted one down */\n" +
                     ".icon-refresh:before              { content: \"\\f021\"; }\n" +
                     ".icon-list-alt:before             { content: \"\\f022\"; }\n" +
                     ".icon-lock:before                 { content: \"\\f023\"; }\n" +
                     ".icon-flag:before                 { content: \"\\f024\"; }\n" +
                     ".icon-headphones:before           { content: \"\\f025\"; }\n" +
                     ".icon-volume-off:before           { content: \"\\f026\"; }\n" +
                     ".icon-volume-down:before          { content: \"\\f027\"; }\n" +
                     ".icon-volume-up:before            { content: \"\\f028\"; }\n" +
                     ".icon-qrcode:before               { content: \"\\f029\"; }\n" +
                     ".icon-barcode:before              { content: \"\\f02a\"; }\n" +
                     ".icon-tag:before                  { content: \"\\f02b\"; }\n" +
                     ".icon-tags:before                 { content: \"\\f02c\"; }\n" +
                     ".icon-book:before                 { content: \"\\f02d\"; }\n" +
                     ".icon-bookmark:before             { content: \"\\f02e\"; }\n" +
                     ".icon-print:before                { content: \"\\f02f\"; }\n" +
                     "\n" +
                     ".icon-camera:before               { content: \"\\f030\"; }\n" +
                     ".icon-font:before                 { content: \"\\f031\"; }\n" +
                     ".icon-bold:before                 { content: \"\\f032\"; }\n" +
                     ".icon-italic:before               { content: \"\\f033\"; }\n" +
                     ".icon-text-height:before          { content: \"\\f034\"; }\n" +
                     ".icon-text-width:before           { content: \"\\f035\"; }\n" +
                     ".icon-align-left:before           { content: \"\\f036\"; }\n" +
                     ".icon-align-center:before         { content: \"\\f037\"; }\n" +
                     ".icon-align-right:before          { content: \"\\f038\"; }\n" +
                     ".icon-align-justify:before        { content: \"\\f039\"; }\n" +
                     ".icon-list:before                 { content: \"\\f03a\"; }\n" +
                     ".icon-indent-left:before          { content: \"\\f03b\"; }\n" +
                     ".icon-indent-right:before         { content: \"\\f03c\"; }\n" +
                     ".icon-facetime-video:before       { content: \"\\f03d\"; }\n" +
                     ".icon-picture:before              { content: \"\\f03e\"; }\n" +
                     "\n" +
                     ".icon-pencil:before               { content: \"\\f040\"; }\n" +
                     ".icon-map-marker:before           { content: \"\\f041\"; }\n" +
                     ".icon-adjust:before               { content: \"\\f042\"; }\n" +
                     ".icon-tint:before                 { content: \"\\f043\"; }\n" +
                     ".icon-edit:before                 { content: \"\\f044\"; }\n" +
                     ".icon-share:before                { content: \"\\f045\"; }\n" +
                     ".icon-check:before                { content: \"\\f046\"; }\n" +
                     ".icon-move:before                 { content: \"\\f047\"; }\n" +
                     ".icon-step-backward:before        { content: \"\\f048\"; }\n" +
                     ".icon-fast-backward:before        { content: \"\\f049\"; }\n" +
                     ".icon-backward:before             { content: \"\\f04a\"; }\n" +
                     ".icon-play:before                 { content: \"\\f04b\"; }\n" +
                     ".icon-pause:before                { content: \"\\f04c\"; }\n" +
                     ".icon-stop:before                 { content: \"\\f04d\"; }\n" +
                     ".icon-forward:before              { content: \"\\f04e\"; }\n" +
                     "\n" +
                     ".icon-fast-forward:before         { content: \"\\f050\"; }\n" +
                     ".icon-step-forward:before         { content: \"\\f051\"; }\n" +
                     ".icon-eject:before                { content: \"\\f052\"; }\n" +
                     ".icon-chevron-left:before         { content: \"\\f053\"; }\n" +
                     ".icon-chevron-right:before        { content: \"\\f054\"; }\n" +
                     ".icon-plus-sign:before            { content: \"\\f055\"; }\n" +
                     ".icon-minus-sign:before           { content: \"\\f056\"; }\n" +
                     ".icon-remove-sign:before          { content: \"\\f057\"; }\n" +
                     ".icon-ok-sign:before              { content: \"\\f058\"; }\n" +
                     ".icon-question-sign:before        { content: \"\\f059\"; }\n" +
                     ".icon-info-sign:before            { content: \"\\f05a\"; }\n" +
                     ".icon-screenshot:before           { content: \"\\f05b\"; }\n" +
                     ".icon-remove-circle:before        { content: \"\\f05c\"; }\n" +
                     ".icon-ok-circle:before            { content: \"\\f05d\"; }\n" +
                     ".icon-ban-circle:before           { content: \"\\f05e\"; }\n" +
                     "\n" +
                     ".icon-arrow-left:before           { content: \"\\f060\"; }\n" +
                     ".icon-arrow-right:before          { content: \"\\f061\"; }\n" +
                     ".icon-arrow-up:before             { content: \"\\f062\"; }\n" +
                     ".icon-arrow-down:before           { content: \"\\f063\"; }\n" +
                     ".icon-share-alt:before            { content: \"\\f064\"; }\n" +
                     ".icon-resize-full:before          { content: \"\\f065\"; }\n" +
                     ".icon-resize-small:before         { content: \"\\f066\"; }\n" +
                     ".icon-plus:before                 { content: \"\\f067\"; }\n" +
                     ".icon-minus:before                { content: \"\\f068\"; }\n" +
                     ".icon-asterisk:before             { content: \"\\f069\"; }\n" +
                     ".icon-exclamation-sign:before     { content: \"\\f06a\"; }\n" +
                     ".icon-gift:before                 { content: \"\\f06b\"; }\n" +
                     ".icon-leaf:before                 { content: \"\\f06c\"; }\n" +
                     ".icon-fire:before                 { content: \"\\f06d\"; }\n" +
                     ".icon-eye-open:before             { content: \"\\f06e\"; }\n" +
                     "\n" +
                     ".icon-eye-close:before            { content: \"\\f070\"; }\n" +
                     ".icon-warning-sign:before         { content: \"\\f071\"; }\n" +
                     ".icon-plane:before                { content: \"\\f072\"; }\n" +
                     ".icon-calendar:before             { content: \"\\f073\"; }\n" +
                     ".icon-random:before               { content: \"\\f074\"; }\n" +
                     ".icon-comment:before              { content: \"\\f075\"; }\n" +
                     ".icon-magnet:before               { content: \"\\f076\"; }\n" +
                     ".icon-chevron-up:before           { content: \"\\f077\"; }\n" +
                     ".icon-chevron-down:before         { content: \"\\f078\"; }\n" +
                     ".icon-retweet:before              { content: \"\\f079\"; }\n" +
                     ".icon-shopping-cart:before        { content: \"\\f07a\"; }\n" +
                     ".icon-folder-close:before         { content: \"\\f07b\"; }\n" +
                     ".icon-folder-open:before          { content: \"\\f07c\"; }\n" +
                     ".icon-resize-vertical:before      { content: \"\\f07d\"; }\n" +
                     ".icon-resize-horizontal:before    { content: \"\\f07e\"; }\n" +
                     "\n" +
                     ".icon-bar-chart:before            { content: \"\\f080\"; }\n" +
                     ".icon-twitter-sign:before         { content: \"\\f081\"; }\n" +
                     ".icon-facebook-sign:before        { content: \"\\f082\"; }\n" +
                     ".icon-camera-retro:before         { content: \"\\f083\"; }\n" +
                     ".icon-key:before                  { content: \"\\f084\"; }\n" +
                     ".icon-cogs:before                 { content: \"\\f085\"; }\n" +
                     ".icon-comments:before             { content: \"\\f086\"; }\n" +
                     ".icon-thumbs-up:before            { content: \"\\f087\"; }\n" +
                     ".icon-thumbs-down:before          { content: \"\\f088\"; }\n" +
                     ".icon-star-half:before            { content: \"\\f089\"; }\n" +
                     ".icon-heart-empty:before          { content: \"\\f08a\"; }\n" +
                     ".icon-signout:before              { content: \"\\f08b\"; }\n" +
                     ".icon-linkedin-sign:before        { content: \"\\f08c\"; }\n" +
                     ".icon-pushpin:before              { content: \"\\f08d\"; }\n" +
                     ".icon-external-link:before        { content: \"\\f08e\"; }\n" +
                     "\n" +
                     ".icon-signin:before               { content: \"\\f090\"; }\n" +
                     ".icon-trophy:before               { content: \"\\f091\"; }\n" +
                     ".icon-github-sign:before          { content: \"\\f092\"; }\n" +
                     ".icon-upload-alt:before           { content: \"\\f093\"; }\n" +
                     ".icon-lemon:before                { content: \"\\f094\"; }\n" +
                     ".icon-phone:before                { content: \"\\f095\"; }\n" +
                     ".icon-check-empty:before          { content: \"\\f096\"; }\n" +
                     ".icon-bookmark-empty:before       { content: \"\\f097\"; }\n" +
                     ".icon-phone-sign:before           { content: \"\\f098\"; }\n" +
                     ".icon-twitter:before              { content: \"\\f099\"; }\n" +
                     ".icon-facebook:before             { content: \"\\f09a\"; }\n" +
                     ".icon-github:before               { content: \"\\f09b\"; }\n" +
                     ".icon-unlock:before               { content: \"\\f09c\"; }\n" +
                     ".icon-credit-card:before          { content: \"\\f09d\"; }\n" +
                     ".icon-rss:before                  { content: \"\\f09e\"; }\n" +
                     "\n" +
                     ".icon-hdd:before                  { content: \"\\f0a0\"; }\n" +
                     ".icon-bullhorn:before             { content: \"\\f0a1\"; }\n" +
                     ".icon-bell:before                 { content: \"\\f0a2\"; }\n" +
                     ".icon-certificate:before          { content: \"\\f0a3\"; }\n" +
                     ".icon-hand-right:before           { content: \"\\f0a4\"; }\n" +
                     ".icon-hand-left:before            { content: \"\\f0a5\"; }\n" +
                     ".icon-hand-up:before              { content: \"\\f0a6\"; }\n" +
                     ".icon-hand-down:before            { content: \"\\f0a7\"; }\n" +
                     ".icon-circle-arrow-left:before    { content: \"\\f0a8\"; }\n" +
                     ".icon-circle-arrow-right:before   { content: \"\\f0a9\"; }\n" +
                     ".icon-circle-arrow-up:before      { content: \"\\f0aa\"; }\n" +
                     ".icon-circle-arrow-down:before    { content: \"\\f0ab\"; }\n" +
                     ".icon-globe:before                { content: \"\\f0ac\"; }\n" +
                     ".icon-wrench:before               { content: \"\\f0ad\"; }\n" +
                     ".icon-tasks:before                { content: \"\\f0ae\"; }\n" +
                     "\n" +
                     ".icon-filter:before               { content: \"\\f0b0\"; }\n" +
                     ".icon-briefcase:before            { content: \"\\f0b1\"; }\n" +
                     ".icon-fullscreen:before           { content: \"\\f0b2\"; }\n" +
                     "\n" +
                     ".icon-group:before                { content: \"\\f0c0\"; }\n" +
                     ".icon-link:before                 { content: \"\\f0c1\"; }\n" +
                     ".icon-cloud:before                { content: \"\\f0c2\"; }\n" +
                     ".icon-beaker:before               { content: \"\\f0c3\"; }\n" +
                     ".icon-cut:before                  { content: \"\\f0c4\"; }\n" +
                     ".icon-copy:before                 { content: \"\\f0c5\"; }\n" +
                     ".icon-paper-clip:before           { content: \"\\f0c6\"; }\n" +
                     ".icon-save:before                 { content: \"\\f0c7\"; }\n" +
                     ".icon-sign-blank:before           { content: \"\\f0c8\"; }\n" +
                     ".icon-reorder:before              { content: \"\\f0c9\"; }\n" +
                     ".icon-list-ul:before              { content: \"\\f0ca\"; }\n" +
                     ".icon-list-ol:before              { content: \"\\f0cb\"; }\n" +
                     ".icon-strikethrough:before        { content: \"\\f0cc\"; }\n" +
                     ".icon-underline:before            { content: \"\\f0cd\"; }\n" +
                     ".icon-table:before                { content: \"\\f0ce\"; }\n" +
                     "\n" +
                     ".icon-magic:before                { content: \"\\f0d0\"; }\n" +
                     ".icon-truck:before                { content: \"\\f0d1\"; }\n" +
                     ".icon-pinterest:before            { content: \"\\f0d2\"; }\n" +
                     ".icon-pinterest-sign:before       { content: \"\\f0d3\"; }\n" +
                     ".icon-google-plus-sign:before     { content: \"\\f0d4\"; }\n" +
                     ".icon-google-plus:before          { content: \"\\f0d5\"; }\n" +
                     ".icon-money:before                { content: \"\\f0d6\"; }\n" +
                     ".icon-caret-down:before           { content: \"\\f0d7\"; }\n" +
                     ".icon-caret-up:before             { content: \"\\f0d8\"; }\n" +
                     ".icon-caret-left:before           { content: \"\\f0d9\"; }\n" +
                     ".icon-caret-right:before          { content: \"\\f0da\"; }\n" +
                     ".icon-columns:before              { content: \"\\f0db\"; }\n" +
                     ".icon-sort:before                 { content: \"\\f0dc\"; }\n" +
                     ".icon-sort-down:before            { content: \"\\f0dd\"; }\n" +
                     ".icon-sort-up:before              { content: \"\\f0de\"; }\n" +
                     "\n" +
                     ".icon-envelope-alt:before         { content: \"\\f0e0\"; }\n" +
                     ".icon-linkedin:before             { content: \"\\f0e1\"; }\n" +
                     ".icon-undo:before                 { content: \"\\f0e2\"; }\n" +
                     ".icon-legal:before                { content: \"\\f0e3\"; }\n" +
                     ".icon-dashboard:before            { content: \"\\f0e4\"; }\n" +
                     ".icon-comment-alt:before          { content: \"\\f0e5\"; }\n" +
                     ".icon-comments-alt:before         { content: \"\\f0e6\"; }\n" +
                     ".icon-bolt:before                 { content: \"\\f0e7\"; }\n" +
                     ".icon-sitemap:before              { content: \"\\f0e8\"; }\n" +
                     ".icon-umbrella:before             { content: \"\\f0e9\"; }\n" +
                     ".icon-paste:before                { content: \"\\f0ea\"; }\n" +
                     ".icon-lightbulb:before            { content: \"\\f0eb\"; }\n" +
                     ".icon-exchange:before             { content: \"\\f0ec\"; }\n" +
                     ".icon-cloud-download:before       { content: \"\\f0ed\"; }\n" +
                     ".icon-cloud-upload:before         { content: \"\\f0ee\"; }\n" +
                     "\n" +
                     ".icon-user-md:before              { content: \"\\f0f0\"; }\n" +
                     ".icon-stethoscope:before          { content: \"\\f0f1\"; }\n" +
                     ".icon-suitcase:before             { content: \"\\f0f2\"; }\n" +
                     ".icon-bell-alt:before             { content: \"\\f0f3\"; }\n" +
                     ".icon-coffee:before               { content: \"\\f0f4\"; }\n" +
                     ".icon-food:before                 { content: \"\\f0f5\"; }\n" +
                     ".icon-file-alt:before             { content: \"\\f0f6\"; }\n" +
                     ".icon-building:before             { content: \"\\f0f7\"; }\n" +
                     ".icon-hospital:before             { content: \"\\f0f8\"; }\n" +
                     ".icon-ambulance:before            { content: \"\\f0f9\"; }\n" +
                     ".icon-medkit:before               { content: \"\\f0fa\"; }\n" +
                     ".icon-fighter-jet:before          { content: \"\\f0fb\"; }\n" +
                     ".icon-beer:before                 { content: \"\\f0fc\"; }\n" +
                     ".icon-h-sign:before               { content: \"\\f0fd\"; }\n" +
                     ".icon-plus-sign-alt:before        { content: \"\\f0fe\"; }\n" +
                     "\n" +
                     ".icon-double-angle-left:before    { content: \"\\f100\"; }\n" +
                     ".icon-double-angle-right:before   { content: \"\\f101\"; }\n" +
                     ".icon-double-angle-up:before      { content: \"\\f102\"; }\n" +
                     ".icon-double-angle-down:before    { content: \"\\f103\"; }\n" +
                     ".icon-angle-left:before           { content: \"\\f104\"; }\n" +
                     ".icon-angle-right:before          { content: \"\\f105\"; }\n" +
                     ".icon-angle-up:before             { content: \"\\f106\"; }\n" +
                     ".icon-angle-down:before           { content: \"\\f107\"; }\n" +
                     ".icon-desktop:before              { content: \"\\f108\"; }\n" +
                     ".icon-laptop:before               { content: \"\\f109\"; }\n" +
                     ".icon-tablet:before               { content: \"\\f10a\"; }\n" +
                     ".icon-mobile-phone:before         { content: \"\\f10b\"; }\n" +
                     ".icon-circle-blank:before         { content: \"\\f10c\"; }\n" +
                     ".icon-quote-left:before           { content: \"\\f10d\"; }\n" +
                     ".icon-quote-right:before          { content: \"\\f10e\"; }\n" +
                     "\n" +
                     ".icon-spinner:before              { content: \"\\f110\"; }\n" +
                     ".icon-circle:before               { content: \"\\f111\"; }\n" +
                     ".icon-reply:before                { content: \"\\f112\"; }\n" +
                     ".icon-github-alt:before           { content: \"\\f113\"; }\n" +
                     ".icon-folder-close-alt:before     { content: \"\\f114\"; }\n" +
                     ".icon-folder-open-alt:before      { content: \"\\f115\"; }";

        ArrayList<Pair<String, String>> output = new ArrayList<Pair<String, String>>();
        StringTokenizer a = new StringTokenizer( svg, "\n" );
        while ( a.hasMoreTokens() ) {
            String token = a.nextToken().trim();
            if ( token.length() > 0 ) {
                String key = token.substring( token.indexOf( 'f' ) + 1, token.indexOf( ';' ) );
                int start = token.indexOf( "d=" ) + 3;
                int end = token.lastIndexOf( '"' );
                String pathDeclaration = token.substring( start, end ).trim();

                StringTokenizer b = new StringTokenizer( map, "\n" );
                while ( b.hasMoreTokens() ) {
                    String bToken = b.nextToken().trim();
                    if ( bToken.indexOf( key ) >= 0 ) {
                        String name = bToken.substring( 1, bToken.indexOf( ':' ) );
                        output.add( new Pair<String, String>( name, pathDeclaration ) );
                        break;
                    }
                }
//                output += subString + "\n";

            }
        }
        String objectLiteral = "{";
        for ( int i = 0; i < output.size(); i++ ) {
            Pair<String, String> stringStringPair = output.get( i );
            System.out.println( "stringStringPair = " + stringStringPair );
            objectLiteral += stringStringPair._1.replace( '-', '_' ) + ": \"" + stringStringPair._2 + "\"";
            if ( i < output.size() - 1 ) {
                objectLiteral += ",\n";
            }
            else {
                objectLiteral += "\n";
            }
        }
        objectLiteral += "}";

        System.out.println( "objectLiteral = \n" + objectLiteral );

    }
}
