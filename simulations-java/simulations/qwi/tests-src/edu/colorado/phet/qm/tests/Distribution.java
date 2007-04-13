package edu.colorado.phet.qm.tests;

import java.io.Serializable;
import java.util.*;

class UnnormalizableDistributionException extends Exception {
    public UnnormalizableDistributionException( String s ) {
        super( s );
    }
}