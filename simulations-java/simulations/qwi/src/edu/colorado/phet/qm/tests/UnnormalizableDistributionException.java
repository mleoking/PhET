package edu.colorado.phet.qm.tests;

import java.io.Serializable;
import java.util.*;

public class UnnormalizableDistributionException extends Exception {
    public UnnormalizableDistributionException( String s ) {
        super( s );
    }
}