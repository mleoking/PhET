package edu.colorado.phet.licensing;

import java.io.File;

public class Config {
    public static File getTrunkPath(String[] args) {
        if (args.length == 0) {
            System.err.println("The path to trunk must be specified as args[0]");
            System.exit(1);
        }
        File trunkPath = new File(args[0]);
        if (!trunkPath.exists()) {
            System.err.println(trunkPath + " does not exist.");
            System.exit(1);
        } else if (!trunkPath.isDirectory()) {
            System.err.println(trunkPath + " is not a directory.");
            System.exit(1);
        }
        return trunkPath;
    }
}
