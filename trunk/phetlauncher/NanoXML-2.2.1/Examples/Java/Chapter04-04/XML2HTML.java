/* XML2HTML.java                                                   NanoXML/Java
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

import net.n3.nanoxml.*;
import java.util.*;

public class XML2HTML
{

    public static void main(String[] param)
        throws Exception
    {
        IXMLBuilder builder = new DocumentBuilder();
        IXMLParser parser = XMLParserFactory.createDefaultXMLParser();
        parser.setBuilder(builder);
        IXMLReader reader = StdXMLReader.fileReader(param[0]);
        parser.setReader(reader);
        Chapter chapter = (Chapter) parser.parse();
        System.out.println("<!DOCTYPE HTML PUBLIC '-//W3C//DTD HTML 4.01//EN' 'http://www.w3.org/TR/html4/strict.dtd'>");
        System.out.print("<HTML><HEAD><TITLE>");
        System.out.print(chapter.getTitle());
        System.out.println("</TITLE></HEAD><BODY>");
        System.out.print("<H1>");
        System.out.print(chapter.getTitle());
        System.out.println("</H1>");
        Enumeration enum = chapter.getParagraphs();
        while (enum.hasMoreElements()) {
            Paragraph para = (Paragraph) enum.nextElement();
            System.out.print("<P>");
            System.out.print(para.getContent());
            System.out.println("</P>");
        }
        System.out.println("</BODY></HTML>");
    }

}

