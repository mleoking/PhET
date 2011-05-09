/* Copyright 2009, University of Colorado */
package edu.colorado.phet.licensing.rules;

import edu.colorado.phet.buildtools.util.LicenseInfo;
import edu.colorado.phet.licensing.ResourceAnnotation;

/**
 * Base class for rule sets.
 *
 * @author Sam Reid
 */
public class AbstractRuleSet {

    private final AbstractRule[] rule;

    public AbstractRuleSet( AbstractRule[] rule ) {
        this.rule = rule;
    }

    /**
     * True if any rule matches.
     */
    public boolean matches( ResourceAnnotation annotation ) {
        for ( int i = 0; i < rule.length; i++ ) {
            AbstractRule abstractRule = rule[i];
            if ( abstractRule.matches( annotation ) ) {
                return true;
            }
        }
        return false;
    }

    /**
     * True if any rule matches.
     */
    public boolean matches( LicenseInfo info ) {
        for ( int i = 0; i < rule.length; i++ ) {
            AbstractRule abstractRule = rule[i];
            if ( abstractRule.matches( info ) ) {
                return true;
            }
        }
        return false;
    }
}