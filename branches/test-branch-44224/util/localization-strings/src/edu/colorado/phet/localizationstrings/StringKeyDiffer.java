package edu.colorado.phet.localizationstrings;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Properties;

/**
 * Provides a diff between key sets of 2 localization string files.
 * For example, this was used to identify whether submitted translations to the old the-ramp are compatible enough with the new version to be kept.
 */
public class StringKeyDiffer {
    
    public static void main(String[] args) throws IOException {
        if ( args.length != 2 ) {
            System.out.println( "usage: " + StringKeyDiffer.class.getName() +  " file1.properties file2.properties" );
            System.exit( 1 );
        }
        new StringKeyDiffer().diff(new File(args[0]), new File(args[1]));
    }

    private void diff(File af, File bf) throws IOException {
        Properties a = new Properties();
        a.load(new FileInputStream(af));

        Properties b = new Properties();
        b.load(new FileInputStream(bf));

        HashSet<Object> shared = new HashSet<Object>();
        for (Object o : a.keySet()) {
            if (b.containsKey(o)) shared.add(o);
        }

        HashSet<Object> aOnly = new HashSet<Object>();
        for (Object o : a.keySet()) {
            if (!b.containsKey(o)) aOnly.add(o);
        }

        HashSet<Object> bOnly = new HashSet<Object>();
        for (Object o : b.keySet()) {
            if (!a.containsKey(o)) bOnly.add(o);
        }

        System.out.println("a.size= "+a.size());
        System.out.println("b.size="+b.size());
        System.out.println("Shared keys: " + show(shared));
        System.out.println("aOnly = " + show(aOnly));
        System.out.println("bOnly = " + show(bOnly));
    }

    private String show(HashSet<Object> bOnly) {
        String s = ("\n"+bOnly.size()+" items\n");
        for (Object o : bOnly) {
            s+=o+"\n";
        }
        return s;
    }
}
