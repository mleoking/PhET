package edu.colorado.phet.densityjava;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: Owner
 * Date: May 13, 2009
 * Time: 2:48:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class NativeUtil {
    public static void main(String[] args) {
        File dir=new File("C:\\workingcopy\\phet\\svn\\trunk\\simulations-java\\contrib\\jmonkeyengine\\lib\\natives");
        File[]f=dir.listFiles();
        for (int i = 0; i < f.length; i++) {
            File file = f[i];
            System.out.print(file.getName()+", ");
        }
        System.out.println("");
    }
}
