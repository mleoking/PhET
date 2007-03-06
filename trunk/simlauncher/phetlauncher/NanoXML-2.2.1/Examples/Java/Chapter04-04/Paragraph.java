/* Paragraph.java                                                  NanoXML/Java
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
import java.io.*;

public class Paragraph
    extends DocumentElement
{

    public static final int LEFT = 0;
    public static final int CENTER = 1;
    public static final int RIGHT = 2;

    private static final Hashtable alignments;

    static {
        alignments = new Hashtable();
        alignments.put("left", new Integer(LEFT));
        alignments.put("center", new Integer(CENTER));
        alignments.put("right", new Integer(RIGHT));
    }

    public String getContent()
    {
        return this.attrs.getProperty("#PCDATA");
    }

    public int getAlignment()
    {
        String str = this.attrs.getProperty("align");
        Integer align = (Integer) alignments.get(str);
        return align.intValue();
    }
}
