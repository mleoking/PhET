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

import org.apache.tools.ant.*;
import proguard.*;

import java.io.IOException;
import java.util.*;

/**
 * This Task allows to define a ProGuard configuration from Ant.
 *
 * @author Eric Lafortune
 */
public class ConfigurationTask extends Task
{
    protected final Configuration configuration = new Configuration();


    /**
     * Adds the contents of this configuration task to the given configuration.
     * @param configuration the configuration to be extended.
     */
    public void appendTo(Configuration configuration)
    {
        // Append all of these configuration entries to the given configuration.
        configuration.programJars = extendClassPath(configuration.programJars,
                                                    this.configuration.programJars);

        configuration.libraryJars = extendClassPath(configuration.libraryJars,
                                                    this.configuration.libraryJars);

        configuration.keep = extendClassSpecifications(configuration.keep,
                                                       this.configuration.keep);

        configuration.whyAreYouKeeping = extendClassSpecifications(configuration.whyAreYouKeeping,
                                                                   this.configuration.whyAreYouKeeping);

        configuration.assumeNoSideEffects = extendClassSpecifications(configuration.assumeNoSideEffects,
                                                                      this.configuration.assumeNoSideEffects);

        configuration.keepAttributes = extendList(configuration.keepAttributes,
                                                  this.configuration.keepAttributes);
    }


    // Ant task nested elements.

    public void addConfiguredInjar(ClassPathElement classPathElement)
    {
        configuration.programJars = extendClassPath(configuration.programJars,
                                                    classPathElement,
                                                    false);
    }


    public void addConfiguredOutjar(ClassPathElement classPathElement)
    {
        configuration.programJars = extendClassPath(configuration.programJars,
                                                    classPathElement,
                                                    true);
    }


    public void addConfiguredLibraryjar(ClassPathElement classPathElement)
    {
        configuration.libraryJars = extendClassPath(configuration.libraryJars,
                                                    classPathElement,
                                                    false);
    }


    public void addConfiguredKeep(KeepSpecificationElement keepSpecificationElement)
    {
        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      true,
                                                      false);
    }


    public void addConfiguredKeepclassmembers(KeepSpecificationElement keepSpecificationElement)
    {
        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      false,
                                                      false);
    }


    public void addConfiguredKeepclasseswithmembers(KeepSpecificationElement keepSpecificationElement)
    {
        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      true,
                                                      true);
    }


    public void addConfiguredKeepnames(KeepSpecificationElement keepSpecificationElement)
    {
        // Set the shrinking flag, based on the name (backward compatibility).
        keepSpecificationElement.setAllowshrinking(true);

        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      true,
                                                      false);
    }


    public void addConfiguredKeepclassmembernames(KeepSpecificationElement keepSpecificationElement)
    {
        // Set the shrinking flag, based on the name (backward compatibility).
        keepSpecificationElement.setAllowshrinking(true);

        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      false,
                                                      false);
    }


    public void addConfiguredKeepclasseswithmembernames(KeepSpecificationElement keepSpecificationElement)
    {
        // Set the shrinking flag, based on the name (backward compatibility).
        keepSpecificationElement.setAllowshrinking(true);

        configuration.keep = extendKeepSpecifications(configuration.keep,
                                                      keepSpecificationElement,
                                                      true,
                                                      true);
    }


    public void addConfiguredWhyareyoukeeping(ClassSpecificationElement classSpecificationElement)
    {
        configuration.whyAreYouKeeping = extendClassSpecifications(configuration.whyAreYouKeeping,
                                                                   classSpecificationElement);
    }


    public void addConfiguredAssumenosideeffects(ClassSpecificationElement classSpecificationElement)
    {
        configuration.assumeNoSideEffects = extendClassSpecifications(configuration.assumeNoSideEffects,
                                                                      classSpecificationElement);
    }


    public void addConfiguredKeepattribute(KeepAttributeElement keepAttributeElement)
    {
        configuration.keepAttributes = extendAttributes(configuration.keepAttributes,
                                                        keepAttributeElement);
    }


    public void addConfiguredAdaptResourceFileNames(FilterElement filterElement)
    {
        configuration.adaptResourceFileNames = extendFilter(configuration.adaptResourceFileNames,
                                                            filterElement);
    }


    public void addConfiguredAdaptResourceFileContents(FilterElement filterElement)
    {
        configuration.adaptResourceFileContents = extendFilter(configuration.adaptResourceFileContents,
                                                               filterElement);
    }


    public void addConfiguredConfiguration(ConfigurationElement configurationElement)
    {
        configurationElement.appendTo(configuration);
    }


    // Implementations for Task.

    public void addText(String text) throws BuildException
    {
        try
        {
            String arg = getProject().replaceProperties(text);

            ConfigurationParser parser = new ConfigurationParser(new String[] { arg },
                                                                 getProject().getBaseDir());

            try
            {
                parser.parse(configuration);
            }
            catch (ParseException ex)
            {
                throw new BuildException(ex.getMessage());
            }
            finally
            {
                parser.close();
            }
        }
        catch (IOException ex)
        {
            throw new BuildException(ex.getMessage());
        }
    }


    // Small utility methods.

    private ClassPath extendClassPath(ClassPath        classPath,
                                      ClassPathElement classPathElement,
                                      boolean          output)
    {
        if (classPath == null)
        {
            classPath = new ClassPath();
        }

        classPathElement.appendClassPathEntriesTo(classPath,
                                                  output);

        return classPath;
    }


    private ClassPath extendClassPath(ClassPath classPath,
                                      ClassPath additionalClassPath)
    {
        if (additionalClassPath != null)
        {
            if (classPath == null)
            {
                classPath = new ClassPath();
            }

            classPath.addAll(additionalClassPath);
        }

        return classPath;
    }


    private List extendKeepSpecifications(List                     keepSpecifications,
                                          KeepSpecificationElement keepSpecificationElement,
                                          boolean                  markClasses,
                                          boolean                  markClassesConditionally)
    {
        if (keepSpecifications == null)
        {
            keepSpecifications = new ArrayList();
        }

        keepSpecificationElement.appendTo(keepSpecifications,
                                          markClasses,
                                          markClassesConditionally);

        return keepSpecifications;
    }


    private List extendClassSpecifications(List                      classSpecifications,
                                           ClassSpecificationElement classSpecificationElement)
    {
        if (classSpecifications == null)
        {
            classSpecifications = new ArrayList();
        }

        classSpecificationElement.appendTo(classSpecifications);

        return classSpecifications;
    }


    private List extendClassSpecifications(List classSpecifications,
                                           List additionalClassSpecifications)
    {
        if (additionalClassSpecifications != null)
        {
            if (classSpecifications == null)
            {
                classSpecifications = new ArrayList();
            }

            classSpecifications.addAll(additionalClassSpecifications);
        }

        return classSpecifications;
    }


    private List extendAttributes(List                 attributes,
                                  KeepAttributeElement keepAttributeElement)
    {
        if (attributes == null)
        {
            attributes = new ArrayList();
        }

        keepAttributeElement.appendTo(attributes);

        return attributes;
    }


    private List extendFilter(List          filter,
                              FilterElement filterElement)
    {
        if (filter == null)
        {
            filter = new ArrayList();
        }

        filterElement.appendTo(filter);

        return filter;
    }


    private List extendList(List list,
                            List additionalList)
    {
        if (additionalList != null)
        {
            if (list == null)
            {
                list = new ArrayList();
            }

            list.addAll(additionalList);
        }

        return list;
    }
}
