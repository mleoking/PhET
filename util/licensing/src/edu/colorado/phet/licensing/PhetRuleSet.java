/*
This file indicates licenses or annotations that are compatible with
PhET licensing.  Items matching the following will be suppressed from the licensing reports

Sam Reid, 2009
*/

package edu.colorado.phet.licensing;

public class PhetRuleSet extends SimInfo.RuleSet {
    public PhetRuleSet() {
        super( new AbstractRule[]{
                new Author( "phet" ),
                new Author( "cmalley" ),
                new Author( "Chris Malley" ),
                new License( "same as" ),//ignore duplicates
                new License( "PUBLIC DOMAIN" ),
                new License( "Used with permission" ),
                new License( "http://creativecommons.org" ),
                new License( "Creative Commons, royalty free, public domain" ),
                new Source( "microsoft" ),//microsoft clip art approved for usage, see Unfuddle #1059
                new Source( "clker.com" ),
                new Source( "java" ),
                new Source( "phet" ),
                new Suffix( ".xml" ),
                new Suffix( ".esp" ),
                new Suffix( ".html" ),
                new Suffix( ".properties" )
        } );
    }
}
