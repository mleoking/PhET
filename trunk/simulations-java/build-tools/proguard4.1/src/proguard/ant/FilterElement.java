/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.ant;

import org.apache.tools.ant.types.DataType;
import proguard.util.ListUtil;

import java.util.List;

/**
 * This DataType represents a name filter in Ant.
 *
 * @author Eric Lafortune
 */
public class FilterElement extends DataType
{
    private String filter;


    /**
     * Adds the contents of this element to the given name filter.
     * @param filter the list of attributes to be extended.
     */
    public void appendTo(List filter)
    {
        // Get the referenced element, or else this one.
        FilterElement filterElement = isReference() ?
            (FilterElement)getCheckedRef(this.getClass(),
                                         this.getClass().getName()) :
            this;

        String filterString = filterElement.filter;

        if (filterString == null)
        {
            // Clear the filter to keep all names.
            filter.clear();
        }
        else
        {
            // Append the filter.
            filter.addAll(ListUtil.commaSeparatedList(filterString));
        }
    }


    // Ant task attributes.

    public void setFilter(String filter)
    {
        this.filter = filter;
    }
}
