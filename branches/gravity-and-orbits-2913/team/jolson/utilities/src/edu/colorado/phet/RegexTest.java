package edu.colorado.phet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
    public static void main( String[] args ) {
        String[] testStrings = new String[]{
                "\"testing.string\"",
                "\"some debug output\" and more",
                "\"Another fancy string!\".toLowerCase",
                ", \"should catch this\".bogus, ",
                "doesn't have quotes",
                "\"mystring\".translate",
                "\"another string 123\".literal",
                "methodCall(\"my first string\".translate, 123 ,456, \"should catch this\".bogus, \"another string\", \"my second string\".literal)",
                "evilMethod(\"my \\\"first\\\" string\".translate, 123 ,456, \"should catch \\\" this\".bogus, \"another string\", \"my second string\".literal)",
                "evenEviler(\"boo: \\\"evil string\\\".literal\\\\\" + \"unmatched \\\" (escaped)\".translate, \"it should match this string\" + x, \"should not match this one\".literal)"
        };
        //Pattern pattern = Pattern.compile( "(\"[^\"]*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        //Pattern pattern = Pattern.compile( "(?:[^\"]*\"[^\"]*\")*?[^\"]*(\"[^\"]*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        String insideStringToken = "(?:[^\"\\\\]|(?:\\\\.))"; //"(?:(?:[^\"\\\\])|(?:\\\\[^\"])|(?:\\\\\"))";
        //Pattern pattern = Pattern.compile( "(?:[^\"]*\"(?:[^\"\\\\]|(?:\\\\.))*\")*?[^\"]*(\"(?:[^\"\\\\]|(?:\\\\.))*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        //Pattern pattern = Pattern.compile( "^(?:[^\"]*\"(?:[^\"\\\\]|(?:\\\\.))*\")*?[^\"]*(\"(?:[^\"\\\\]|(?:\\\\.))*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        Pattern pattern = Pattern.compile( "^(?:[^\"\\r\\n]*\"(?:[^\"\\\\]|(?:\\\\.))*\")*?[^\"\\r\\n]*(\"(?:[^\"\\\\\\r\\n]|(?:\\\\.))*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))[^\\r\\n]*$" );
        //Pattern pattern = Pattern.compile( "(?:[^\"\\r\\n]*\"(?:[^\"\\\\]|(?:\\\\.))*\")*?[^\"\\r\\n]*(\"(?:[^\"\\\\]|(?:\\\\.))*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        //Pattern pattern = Pattern.compile( "(?:(?:(?:[^\"\\\\])|(?:\\\\[^\"])|(?:\\\\\"))*?\"(?:[^\"]|\\\\\")*\")*?(?:[^\"]|\\\\\")*?(\"(?:(?:[^\"\\\\])|(?:\\\\[^\"])|(?:\\\\\"))*\")(?:$|(?:(?!\\.translate)(?!\\.literal)))" );
        for ( String testString : testStrings ) {
            System.out.println( "Testing: " + testString );
            Matcher matcher = pattern.matcher( testString );
            boolean found = false;
            while ( matcher.find() ) {
                System.out.println( "\t" + matcher.group( 1 ) );
                found = true;
            }
            if ( !found ) {
                System.out.println( "None" );
            }
        }
    }
}
