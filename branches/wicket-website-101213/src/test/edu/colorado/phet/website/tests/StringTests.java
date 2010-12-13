package edu.colorado.phet.website.tests;

import static org.junit.Assert.assertTrue;
import org.junit.Test;

import edu.colorado.phet.website.util.StringUtils;

public class StringTests {
    @Test
    public void testStringMessageFormat() {
        String ax = "Apostrophe's {0} Test";
        String ay = StringUtils.messageFormat( ax, new Object[]{"Easy"} );
        assertTrue( ay, ay.equals( "Apostrophe's Easy Test" ) );

        String bx = "Double '' {0}";
        String by = StringUtils.messageFormat( bx, new Object[]{"Test"} );
        assertTrue( by, by.equals( "Double '' Test" ) );

        String cx = "Tests for escaping '{' {0} '}'";
        String cy = StringUtils.messageFormat( cx, new Object[]{"and"} );
        assertTrue( cy, cy.equals( "Tests for escaping { and }" ) );
    }
}
