// Copyright (C) 1998, James R. Weeks and BioElectroMech.
// Visit BioElectroMech at www.obrador.com.  Email James@obrador.com.

// This software is based in part on the work of the Independent JPEG Group.
// See license.txt for details about the allowed used of this software.
// See IJGreadme.txt for details about the Independent JPEG Group's license.

package com.obrador;

import java.awt.*;
import java.io.*;


public class Jpeg
{
    /************** Main Method ****************/
    /***
    * Jpeg("Imagefile", Quality, "OutFileName")
    * According to JAVA virtual machine, the files which can be read are jpeg, tiff and gif files
    ***/

    public static void StandardUsage()
    {
        System.out.println("JpegEncoder for Java(tm) Version 0.9");
        System.out.println("");
        System.out.println("Program usage: java Jpeg \"InputImage\".\"ext\" Quality [\"OutputFile\"[.jpg]]");
        System.out.println("");
        System.out.println("Where \"InputImage\" is the name of an existing image in the current directory.");
        System.out.println("  (\"InputImage may specify a directory, too.) \"ext\" must be .tif, .gif,");
        System.out.println("  or .jpg.");
        System.out.println("Quality is an integer (0 to 100) that specifies how similar the compressed");
        System.out.println("  image is to \"InputImage.\"  100 is almost exactly like \"InputImage\" and 0 is");
        System.out.println("  most dissimilar.  In most cases, 70 - 80 gives very good results.");
        System.out.println("\"OutputFile\" is an optional argument.  If \"OutputFile\" isn't specified, then");
        System.out.println("  the input file name is adopted.  This program will NOT write over an existing");
        System.out.println("  file.  If a directory is specified for the input image, then \"OutputFile\"");
        System.out.println("  will be written in that directory.  The extension \".jpg\" may automatically be");
        System.out.println("  added.");
        System.out.println("");
        System.out.println("Copyright 1998 BioElectroMech and James R. Weeks.  Portions copyright IJG and");
        System.out.println("  Florian Raemy, LCAV.  See license.txt for details.");
        System.out.println("Visit BioElectroMech at www.obrador.com.  Email James@obrador.com.");
    System.exit(0);
    }

    public static void main(String args[])
    {
                Image image = null;
                FileOutputStream dataOut = null;
                File file, outFile;
                JpegEncoder jpg;
                String string = "";
                int i, Quality = 80;
// Check to see if the input file name has one of the extensions:
//     .tif, .gif, .jpg
// If not, print the standard use info.
                if (args.length < 2)
                        StandardUsage();
                if (!args[0].endsWith(".jpg") && !args[0].endsWith(".tif") && !args[0].endsWith(".gif"))
                        StandardUsage();
// First check to see if there is an OutputFile argument.  If there isn't
// then name the file "InputFile".jpg
// Second check to see if the .jpg extension is on the OutputFile argument.
// If there isn't one, add it.
// Need to check for the existence of the output file.  If it exists already,
// rename the file with a # after the file name, then the .jpg extension.
                if (args.length < 3) {
                        string = args[0].substring(0, args[0].lastIndexOf(".")) + ".jpg";
                }
                else {
                        string = args[2];
                        if (string.endsWith(".tif") || string.endsWith(".gif"))
                                string = string.substring(0, string.lastIndexOf("."));
                        if (!string.endsWith(".jpg"))
                                string = string.concat(".jpg");
                }
                outFile = new File(string);
                i = 1;
                while (outFile.exists()) {
                        outFile = new File(string.substring(0, string.lastIndexOf(".")) + (i++) + ".jpg");
                        if (i > 100)
                                System.exit(0);
                }
                file = new File(args[0]);
                        if (file.exists()) {
                                try {
                                        dataOut = new FileOutputStream(outFile);
                                } catch(IOException e) {}
                                try {
                                        Quality = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                        StandardUsage();
                                }
                                image = Toolkit.getDefaultToolkit().getImage(args[0]);
                                jpg = new JpegEncoder(image, Quality, dataOut);
                                jpg.Compress();
                                try {
                                        dataOut.close();
                                } catch(IOException e) {}
                        }
                        else {
                                System.out.println("I couldn't find " + args[0] + ". Is it in another directory?");
                        }
                System.exit(0);
        }
}
