/* DumpXML_Lite.java                                               NanoXML/Lite
 *
 * $Revision$
 * $Date$
 * $Name$
 *
 * This file is part of NanoXML 2 Lite.
 * Copyright (C) 2000-2002 Marc De Scheemaecker, All Rights Reserved.
 *
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from the
 * use of this software.
 *
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it
 * freely, subject to the following restrictions:
 *
 *  1. The origin of this software must not be misrepresented; you must not
 *     claim that you wrote the original software. If you use this software in
 *     a product, an acknowledgment in the product documentation would be
 *     appreciated but is not required.
 *
 *  2. Altered source versions must be plainly marked as such, and must not be
 *     misrepresented as being the original software.
 *
 *  3. This notice may not be removed or altered from any source distribution.
 */

import nanoxml.*;
import java.util.*;
import java.io.*;


public class DumpXML_Lite
{

    public static void main(String args[])
        throws Exception
    {
        if (args.length == 0) {
            System.err.println("Usage: java DumpXML_Lite file.xml");
            Runtime.getRuntime().exit(1);
        }
        
        Hashtable entities = new Hashtable();
        entities.put("foo", "bar");
        XMLElement xml = new XMLElement(entities, false, false);
        FileReader reader = new FileReader(args[0]);
        xml.parseFromReader(reader);
        System.out.println(xml.toString());
    }

}
