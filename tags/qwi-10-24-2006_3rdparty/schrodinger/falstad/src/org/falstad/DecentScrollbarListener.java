/* Copyright 2004, Sam Reid */
package org.falstad;

/**
 * User: Sam Reid
 * Date: Feb 9, 2006
 * Time: 9:13:30 PM
 * Copyright (c) Feb 9, 2006 by Sam Reid
 */
interface DecentScrollbarListener {
    abstract void scrollbarValueChanged( DecentScrollbar ds );

    abstract void scrollbarFinished( DecentScrollbar dc );
} // this is a scrollbar that notifies us when the user is _done_ fiddling // with the value.
