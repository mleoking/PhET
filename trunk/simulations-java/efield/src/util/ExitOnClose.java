// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ExitOnClose extends WindowAdapter
{

    public ExitOnClose()
    {
    }

    public void windowClosing(WindowEvent windowevent)
    {
        System.exit(0);
    }
}
