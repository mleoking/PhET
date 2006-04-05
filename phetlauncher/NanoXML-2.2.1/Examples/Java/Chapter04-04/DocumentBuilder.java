/* DocumentBuilder.java                                            NanoXML/Java
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
import java.util.*;
import net.n3.nanoxml.*;


public class DocumentBuilder
    implements IXMLBuilder
{

    private static Hashtable classes;

    private Stack elements;

    private DocumentElement topElement;

    static {
        classes = new Hashtable();
        classes.put("Chapter", Chapter.class);
        classes.put("Paragraph", Paragraph.class);
    }


    public void startBuilding(String systemID,
                              int    lineNr)
    {
        this.elements = new Stack();
        this.topElement = null;
    }

    public void newProcessingInstruction(String target,
                                         Reader reader)
    {
        // nothing to do
    }

    public void startElement(String name,
                             String nsPrefix,
                             String nsSystemID,
                             String systemID,
                             int    lineNr)
    {
        DocumentElement elt = null;
        try {
            Class cls = (Class) classes.get(name);
            elt = (DocumentElement) cls.newInstance();
        } catch (Exception e) {
            // ignore the exception
        }
        this.elements.push(elt);
        if (this.topElement == null) {
            this.topElement = elt;
        }
    }

    public void endElement(String name,
                           String nsPrefix,
                           String nsSystemID)
    {
        DocumentElement child = (DocumentElement) this.elements.pop();
        if (! this.elements.isEmpty()) {
            DocumentElement parent = (DocumentElement) this.elements.peek();
            parent.addChild(child);
        }
    }

    public void addAttribute(String key,
                             String nsPrefix,
                             String nsSystemID,
                             String value,
                             String type)
    {
        DocumentElement child = (DocumentElement) this.elements.peek();
        child.setAttribute(key, value);
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
        throws IOException
    {
        StringBuffer str = new StringBuffer(1024);
        char[] buf = new char[1024];
        for (;;) {
            int size = reader.read(buf);
            if (size < 0) {
                break;
            }
            str.append(buf, 0, size);
        }
        this.addAttribute("#PCDATA", null, null, str.toString(), "CDATA");
    }

    public Object getResult()
    {
        return topElement;
    }

}

