/* XMLWriter.java                                                  NanoXML/Java
 *
 * $Revision$
 * $Date$
 * $Name$
 *
 * This file is part of NanoXML 2 for Java.
 * Copyright (C) 2001 Marc De Scheemaecker, All Rights Reserved.
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

import java.io.*;
import net.n3.nanoxml.*;


public class MyBuilder
    implements IXMLBuilder
{

    public void startBuilding(String systemID,
                              int    lineNr)
    {
        System.out.println("Document started");
    }

    public void newProcessingInstruction(String target,
                                         Reader reader)
    {
        System.out.println("New PI with target " + target);
    }

    public void startElement(String name,
                             String nsPrefix,
                             String nsSystemID,
                             String systemID,
                             int    lineNr)
    {
        System.out.println("Element started: " + name);
    }

    public void endElement(String name,
                           String nsPrefix,
                           String nsSystemID)
    {
        System.out.println("Element ended: " + name);
    }

    public void addAttribute(String key,
                             String nsPrefix,
                             String nsSystemID,
                             String value,
                             String type)
    {
        System.out.println("    " + key + ": " + type + " = " + value);
    }

    public void elementAttributesProcessed(String name,
                                           String nsPrefix,
                                           String nsSystemID)
    {
        // nothing to do
    }
    
    public void addPCData(Reader reader,
                          String systemID,
                          int    lineNr)
    {
        System.out.println("#PCDATA");
    }

    public Object getResult()
    {
        return null;
    }

}

