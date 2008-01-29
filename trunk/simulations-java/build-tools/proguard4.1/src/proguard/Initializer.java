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
package proguard;

import proguard.classfile.ClassPool;
import proguard.classfile.attribute.visitor.AllAttributeVisitor;
import proguard.classfile.instruction.visitor.AllInstructionVisitor;
import proguard.classfile.util.*;
import proguard.classfile.visitor.*;
import proguard.util.*;

import java.io.IOException;
import java.util.*;

/**
 * This class initializes class pools.
 *
 * @author Eric Lafortune
 */
public class Initializer
{
    private final Configuration configuration;


    /**
     * Creates a new Initializer to initialize classes according to the given
     * configuration.
     */
    public Initializer(Configuration configuration)
    {
        this.configuration = configuration;
    }


    /**
     * Initializes the classes in the given program class pool and library class
     * pool, performs some basic checks, and shrinks the library class pool.
     */
    public void execute(ClassPool programClassPool,
                        ClassPool libraryClassPool) throws IOException
    {
        int originalLibraryClassPoolSize = libraryClassPool.size();

        // Initialize the superclass hierarchy for program classes.
        WarningPrinter hierarchyWarningPrinter = configuration.warn ?
            new WarningPrinter(System.err) :
            null;

        programClassPool.classesAccept(
            new ClassSuperHierarchyInitializer(programClassPool,
                                               libraryClassPool,
                                               hierarchyWarningPrinter));

        // Initialize the superclass hierarchy for library classes.
        libraryClassPool.classesAccept(
            new ClassSuperHierarchyInitializer(programClassPool,
                                               libraryClassPool,
                                               null));

        // Initialize the Class.forName and .class references.
        WarningPrinter classForNameNotePrinter = configuration.note ?
            new WarningPrinter(System.out) :
            null;

        programClassPool.classesAccept(
            new AllMethodVisitor(
            new AllAttributeVisitor(
            new AllInstructionVisitor(
            new DynamicClassReferenceInitializer(programClassPool,
                                                 libraryClassPool,
                                                 classForNameNotePrinter,
                                                 createClassNoteExceptionMatcher(configuration.keep))))));

        // Initialize the class references of program class members and attributes.
        WarningPrinter referenceWarningPrinter = configuration.warn ?
            new WarningPrinter(System.err) :
            null;

        programClassPool.classesAccept(
            new ClassReferenceInitializer(programClassPool,
                                          libraryClassPool,
                                          referenceWarningPrinter));

        // Initialize the Class.get[Declared]{Field,Method} references.
        WarningPrinter getMemberNotePrinter = configuration.note ?
            new WarningPrinter(System.out) :
            null;

        programClassPool.classesAccept(
            new AllMethodVisitor(
            new AllAttributeVisitor(
            new AllInstructionVisitor(
            new DynamicMemberReferenceInitializer(programClassPool,
                                                  libraryClassPool,
                                                  getMemberNotePrinter,
                                                  createClassMemberNoteExceptionMatcher(configuration.keep, true),
                                                  createClassMemberNoteExceptionMatcher(configuration.keep, false))))));

        // Print various notes, if specified.
        WarningPrinter fullyQualifiedClassNameNotePrinter = configuration.note ?
            new WarningPrinter(System.out) :
            null;

        WarningPrinter descriptorKeepNotePrinter = configuration.note ?
            new WarningPrinter(System.out) :
            null;

        if (fullyQualifiedClassNameNotePrinter != null)
        {
            new FullyQualifiedClassNameChecker(programClassPool,
                                               libraryClassPool,
                                               fullyQualifiedClassNameNotePrinter).checkClassSpecifications(configuration.keep);
        }

        if (descriptorKeepNotePrinter != null)
        {
            new DescriptorKeepChecker(programClassPool,
                                      libraryClassPool,
                                      descriptorKeepNotePrinter).checkClassSpecifications(configuration.keep);
        }

        // Reconstruct a library class pool with only those library classes
        // whose hierarchies are referenced by the program classes. We can't do
        // this if we later have to come up with the obfuscated class member
        // names that are globally unique.
        if (configuration.useUniqueClassMemberNames)
        {
            // Initialize the class references of library class members.
            libraryClassPool.classesAccept(
                new ClassReferenceInitializer(programClassPool,
                                              libraryClassPool,
                                              null));
        }
        else
        {
            ClassPool referencedLibraryClassPool = new ClassPool();

            // Collect the library classes that are referenced by program
            // classes.
            programClassPool.classesAccept(
                new ReferencedClassVisitor(
                new LibraryClassFilter(
                new ClassHierarchyTraveler(true, true, true, false,
                new LibraryClassFilter(
                new ClassPoolFiller(referencedLibraryClassPool))))));

            // Initialize the class references of library class members.
            referencedLibraryClassPool.classesAccept(
                new ClassReferenceInitializer(programClassPool,
                                              libraryClassPool,
                                              null));

            // Reset the library class pool.
            libraryClassPool.clear();

            // Copy the library classes that are referenced directly by program
            // classes and the library classes that are referenced by referenced
            // library classes.
            referencedLibraryClassPool.classesAccept(
                new MultiClassVisitor(new ClassVisitor[]
                {
                    new ClassHierarchyTraveler(true, true, true, false,
                    new LibraryClassFilter(
                    new ClassPoolFiller(libraryClassPool))),

                    new ReferencedClassVisitor(
                    new LibraryClassFilter(
                    new ClassHierarchyTraveler(true, true, true, false,
                    new LibraryClassFilter(
                    new ClassPoolFiller(libraryClassPool)))))
                }));
        }

        // Initialize the subclass hierarchies.
        programClassPool.classesAccept(new ClassSubHierarchyInitializer());
        libraryClassPool.classesAccept(new ClassSubHierarchyInitializer());

        // Share strings between the classes, to reduce heap memory usage.
        programClassPool.classesAccept(new StringSharer());
        libraryClassPool.classesAccept(new StringSharer());

        // Print out a summary of the notes, if necessary.
        if (fullyQualifiedClassNameNotePrinter != null)
        {
            int fullyQualifiedNoteCount = fullyQualifiedClassNameNotePrinter.getWarningCount();
            if (fullyQualifiedNoteCount > 0)
            {
                System.out.println("Note: there were " + fullyQualifiedNoteCount +
                                   " references to unknown classes.");
                System.out.println("      You should check your configuration for typos.");
            }
        }

        if (descriptorKeepNotePrinter != null)
        {
            int descriptorNoteCount = descriptorKeepNotePrinter.getWarningCount();
            if (descriptorNoteCount > 0)
            {
                System.out.println("Note: there were " + descriptorNoteCount +
                                   " unkept descriptor classes in kept class members.");
                System.out.println("      You should consider explicitly keeping the mentioned classes");
                System.out.println("      (using '-keep').");
            }
        }

        if (classForNameNotePrinter != null)
        {
            int classForNameNoteCount = classForNameNotePrinter.getWarningCount();
            if (classForNameNoteCount > 0)
            {
                System.out.println("Note: there were " + classForNameNoteCount +
                                   " class casts of dynamically created class instances.");
                System.out.println("      You might consider explicitly keeping the mentioned classes and/or");
                System.out.println("      their implementations (using '-keep').");
            }
        }

        if (getMemberNotePrinter != null)
        {
            int getmemberNoteCount = getMemberNotePrinter.getWarningCount();
            if (getmemberNoteCount > 0)
            {
                System.out.println("Note: there were " + getmemberNoteCount +
                                   " accesses to class members by means of introspection.");
                System.out.println("      You should consider explicitly keeping the mentioned class members");
                System.out.println("      (using '-keep' or '-keepclassmembers').");
            }
        }

        // Print out a summary of the warnings, if necessary.
        if (hierarchyWarningPrinter != null &&
            referenceWarningPrinter != null)
        {
            int hierarchyWarningCount = hierarchyWarningPrinter.getWarningCount();
            if (hierarchyWarningCount > 0)
            {
                System.err.println("Warning: there were " + hierarchyWarningCount +
                                   " unresolved references to superclasses or interfaces.");
                System.err.println("         You may need to specify additional library jars (using '-libraryjars'),");
                System.err.println("         or perhaps the '-dontskipnonpubliclibraryclasses' option.");
            }

            int referenceWarningCount = referenceWarningPrinter.getWarningCount();
            if (referenceWarningCount > 0)
            {
                System.err.println("Warning: there were " + referenceWarningCount +
                                   " unresolved references to program class members.");
                System.err.println("         Your input classes appear to be inconsistent.");
                System.err.println("         You may need to recompile them and try again.");
                System.err.println("         Alternatively, you may have to specify the options ");
                System.err.println("         '-dontskipnonpubliclibraryclasses' and/or");
                System.err.println("         '-dontskipnonpubliclibraryclassmembers'.");
            }

            if ((hierarchyWarningCount > 0 ||
                 referenceWarningCount > 0) &&
                !configuration.ignoreWarnings)
            {
                throw new IOException("Please correct the above warnings first.");
            }
        }

        // Discard unused library classes.
        if (configuration.verbose)
        {
            System.out.println("Ignoring unused library classes...");
            System.out.println("  Original number of library classes: " + originalLibraryClassPoolSize);
            System.out.println("  Final number of library classes:    " + libraryClassPool.size());
        }
    }


    /**
     * Extracts a list of exceptions of classes for which not to print notes,
     * from the keep configuration.
     */
    private StringMatcher createClassNoteExceptionMatcher(List noteExceptions)
    {
        if (noteExceptions != null)
        {
            List noteExceptionNames = new ArrayList(noteExceptions.size());
            for (int index = 0; index < noteExceptions.size(); index++)
            {
                KeepSpecification keepSpecification = (KeepSpecification)noteExceptions.get(index);
                if (keepSpecification.markClasses)
                {
                    // If the class itself is being kept, it's ok.
                    String className = keepSpecification.className;
                    if (className != null)
                    {
                        noteExceptionNames.add(className);
                    }

                    // If all of its extensions are being kept, it's ok too.
                    String extendsClassName = keepSpecification.extendsClassName;
                    if (extendsClassName != null)
                    {
                        noteExceptionNames.add(extendsClassName);
                    }
                }
            }

            if (noteExceptionNames.size() > 0)
            {
                return new ListParser(new ClassNameParser()).parse(noteExceptionNames);
            }
        }

        return null;
    }


    /**
     * Extracts a list of exceptions of field or method names for which not to
     * print notes, from the keep configuration.
     */
    private StringMatcher createClassMemberNoteExceptionMatcher(List    noteExceptions,
                                                                boolean isField)
    {
        if (noteExceptions != null)
        {
            List noteExceptionNames = new ArrayList();
            for (int index = 0; index < noteExceptions.size(); index++)
            {
                KeepSpecification keepSpecification = (KeepSpecification)noteExceptions.get(index);
                List memberSpecifications = isField ?
                    keepSpecification.fieldSpecifications :
                    keepSpecification.methodSpecifications;

                if (memberSpecifications != null)
                {
                    for (int index2 = 0; index2 < memberSpecifications.size(); index2++)
                    {
                        MemberSpecification memberSpecification =
                            (MemberSpecification)memberSpecifications.get(index2);

                        String memberName = memberSpecification.name;
                        if (memberName != null)
                        {
                            noteExceptionNames.add(memberName);
                        }
                    }
                }
            }

            if (noteExceptionNames.size() > 0)
            {
                return new ListParser(new ClassNameParser()).parse(noteExceptionNames);
            }
        }

        return null;
    }
}
