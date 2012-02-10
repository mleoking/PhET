// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.intro.tests;

import lombok.Data;
import lombok.ToString;

@Data public class DataExample {
    public final String name;
    public final int age;
    public final double score;
    public final String[] tags;

    @ToString(includeFieldNames = true)
    @Data(staticConstructor = "of")
    public static class Exercise<T> {
        private final String name;
        private final T value;
    }
}
