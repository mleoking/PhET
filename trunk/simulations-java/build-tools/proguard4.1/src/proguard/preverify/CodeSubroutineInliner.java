/*
 * ProGuard -- shrinking, optimization, obfuscation, and preverification
 *             of Java bytecode.
 *
 * Copyright (c) 2002-2007 Eric Lafortune (eric@graphics.cornell.edu)
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package proguard.preverify;

import proguard.classfile.*;
import proguard.classfile.attribute.*;
import proguard.classfile.attribute.visitor.*;
import proguard.classfile.editor.CodeAttributeComposer;
import proguard.classfile.instruction.*;
import proguard.classfile.instruction.visitor.InstructionVisitor;
import proguard.classfile.util.SimplifiedVisitor;
import proguard.classfile.visitor.ExceptionExcludedOffsetFilter;
import proguard.optimize.peephole.BranchTargetFinder;

/**
 * This AttributeVisitor inlines local subroutines (jsr/ret) in the code
 * attributes that it visits.
 *
 * @author Eric Lafortune
 */
public class CodeSubroutineInliner
extends      SimplifiedVisitor
implements   AttributeVisitor,
             InstructionVisitor,
             ExceptionInfoVisitor
{
    //*
    private static final boolean DEBUG = false;
    /*/
    private static       boolean DEBUG = true;
    //*/


    private final BranchTargetFinder    branchTargetFinder    = new BranchTargetFinder();
    private final CodeAttributeComposer codeAttributeComposer = new CodeAttributeComposer();

    private boolean              inlinedAny;
    private ExceptionInfoVisitor subroutineExceptionInliner;


    // Implementations for AttributeVisitor.

    public void visitAnyAttribute(Clazz clazz, Attribute attribute) {}


    public void visitCodeAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
//        DEBUG =
//            clazz.getName().equals("abc/Def") &&
//            method.getName(clazz).equals("abc");

        branchTargetFinder.visitCodeAttribute(clazz, method, codeAttribute);

        // Don't bother if there aren't any subroutines anyway.
        if (!containsSubroutines(codeAttribute))
        {
            return;
        }

        inlinedAny                 = false;
        subroutineExceptionInliner = this;
        codeAttributeComposer.reset();

        // Append the body of the code.
        copyCode(clazz, method, codeAttribute);

        // Update the code attribute if any code has been inlined.
        if (inlinedAny)
        {
            codeAttributeComposer.visitCodeAttribute(clazz, method, codeAttribute);
        }
    }


    /**
     * Returns whether the given code attribute contains any subroutines.
     */
    private boolean containsSubroutines(CodeAttribute codeAttribute)
    {
        for (int offset = 0; offset < codeAttribute.u4codeLength; offset++)
        {
            if (branchTargetFinder.isSubroutineInvocation(offset))
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Appends the code of the given code attribute.
     */
    private void copyCode(Clazz         clazz,
                          Method        method,
                          CodeAttribute codeAttribute)
    {
        if (DEBUG)
        {
            System.out.println("SubroutineInliner: processing ["+clazz.getName()+"."+method.getName(clazz)+method.getDescriptor(clazz)+"]");
        }

        codeAttributeComposer.beginCodeFragment(codeAttribute.u4codeLength);

        // Copy the non-subroutine instructions.
        int offset  = 0;
        while (offset < codeAttribute.u4codeLength)
        {
            Instruction instruction = InstructionFactory.create(codeAttribute.code, offset);
            int instructionLength = instruction.length(offset);

            // Is this returning subroutine?
            if (branchTargetFinder.isSubroutine(offset) &&
                branchTargetFinder.isSubroutineReturning(offset))
            {
                // Skip the subroutine.
                if (DEBUG)
                {
                    System.out.println("Skipping subroutine at ["+offset+"]");
                }

                // Append a label at this offset instead.
                codeAttributeComposer.appendLabel(offset);
            }
            else
            {
                // Copy the instruction, inlining any subroutine call recursively.
                instruction.accept(clazz, method, codeAttribute, offset, this);
            }

            offset += instructionLength;
        }

        // Copy the exceptions. Note that exceptions with empty try blocks
        // are automatically removed.
        codeAttribute.exceptionsAccept(clazz, method, this);

        if (DEBUG)
        {
            System.out.println("Appending label after code at ["+offset+"]");
        }

        // Append a label just after the code.
        codeAttributeComposer.appendLabel(codeAttribute.u4codeLength);

        codeAttributeComposer.endCodeFragment();
    }


    /**
     * Appends the specified subroutine.
     */
    private void inlineSubroutine(Clazz         clazz,
                                  Method        method,
                                  CodeAttribute codeAttribute,
                                  int           subroutineInvocationOffset,
                                  int           subroutineStart)
    {
        int subroutineEnd = branchTargetFinder.subroutineEnd(subroutineStart);

        if (DEBUG)
        {
            System.out.println("Inlining subroutine ["+subroutineStart+" -> "+subroutineEnd+"] at ["+subroutineInvocationOffset+"]");
        }

        // Don't go inlining exceptions that are already applicable to this
        // subroutine invocation.
        ExceptionInfoVisitor oldSubroutineExceptionInliner =
            subroutineExceptionInliner;

        subroutineExceptionInliner =
            new ExceptionExcludedOffsetFilter(subroutineInvocationOffset,
                                              subroutineExceptionInliner);

        codeAttributeComposer.beginCodeFragment(codeAttribute.u4codeLength);

        // Copy the subroutine instructions, inlining any subroutine calls
        // recursively.
        codeAttribute.instructionsAccept(clazz,
                                         method,
                                         subroutineStart,
                                         subroutineEnd,
                                         this);

        if (DEBUG)
        {
            System.out.println("Appending label after inlined subroutine at ["+subroutineEnd+"]");
        }

        // Append a label just after the code.
        codeAttributeComposer.appendLabel(subroutineEnd);

        // We can again inline exceptions that are applicable to this
        // subroutine invocation.
        subroutineExceptionInliner = oldSubroutineExceptionInliner;

        // Inline the subroutine exceptions.
        codeAttribute.exceptionsAccept(clazz,
                                       method,
                                       subroutineStart,
                                       subroutineEnd,
                                       subroutineExceptionInliner);

        codeAttributeComposer.endCodeFragment();

        inlinedAny = true;
    }


    // Implementations for InstructionVisitor.

    public void visitAnyInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, Instruction instruction)
    {
        // Append the instruction.
        codeAttributeComposer.appendInstruction(offset, instruction.shrink());
    }


    public void visitVariableInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, VariableInstruction variableInstruction)
    {
        byte opcode = variableInstruction.opcode;
        if (opcode == InstructionConstants.OP_RET)
        {
            // Is the return instruction the last instruction of the subroutine?
            if (branchTargetFinder.subroutineEnd(offset) == offset + variableInstruction.length(offset))
            {
                if (DEBUG)
                {
                    System.out.println("Replacing subroutine return at ["+offset+"] by a label");
                }

                // Append a label at this offset instead of the subroutine return.
                codeAttributeComposer.appendLabel(offset);
            }
            else
            {
                if (DEBUG)
                {
                    System.out.println("Replacing subroutine return at ["+offset+"] by a simple branch");
                }

                // Replace the instruction by a branch.
                Instruction replacementInstruction =
                    new BranchInstruction(InstructionConstants.OP_GOTO,
                                          branchTargetFinder.subroutineEnd(offset) - offset).shrink();

                codeAttributeComposer.appendInstruction(offset, replacementInstruction);
            }
        }
        else if (branchTargetFinder.isSubroutineStart(offset))
        {
            if (DEBUG)
            {
                System.out.println("Replacing first subroutine instruction at ["+offset+"] by a label");
            }

            // Append a label at this offset instead of saving the subroutine
            // return address.
            codeAttributeComposer.appendLabel(offset);
        }
        else
        {
            // Append the instruction.
            codeAttributeComposer.appendInstruction(offset, variableInstruction);
        }
    }


    public void visitBranchInstruction(Clazz clazz, Method method, CodeAttribute codeAttribute, int offset, BranchInstruction branchInstruction)
    {
        byte opcode = branchInstruction.opcode;
        if (opcode == InstructionConstants.OP_JSR ||
            opcode == InstructionConstants.OP_JSR_W)
        {
            int branchOffset = branchInstruction.branchOffset;
            int branchTarget = offset + branchOffset;

            // Is the subroutine ever returning?
            if (branchTargetFinder.isSubroutineReturning(branchTarget))
            {
                // Append a label at this offset instead of the subroutine invocation.
                codeAttributeComposer.appendLabel(offset);

                // Inline the invoked subroutine.
                inlineSubroutine(clazz,
                                 method,
                                 codeAttribute,
                                 offset,
                                 branchTarget);
            }
            else
            {
                if (DEBUG)
                {
                    System.out.println("Replacing subroutine invocation at ["+offset+"] by a simple branch");
                }

                // Replace the subroutine invocation by a simple branch.
                Instruction replacementInstruction =
                    new BranchInstruction(InstructionConstants.OP_GOTO,
                                          branchOffset).shrink();

                codeAttributeComposer.appendInstruction(offset, replacementInstruction);

                inlinedAny = true;
            }
        }
        else
        {
            // Append the instruction.
            codeAttributeComposer.appendInstruction(offset, branchInstruction);
        }
    }


    // Implementations for ExceptionInfoVisitor.

    public void visitExceptionInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, ExceptionInfo exceptionInfo)
    {
        int startPC   = exceptionInfo.u2startPC;
        int endPC     = exceptionInfo.u2endPC;
        int handlerPC = exceptionInfo.u2handlerPC;
        int catchType = exceptionInfo.u2catchType;

        // Exclude any subroutine invocations that jump out of the try block,
        // by adding a try block before (and later on, after) each invocation.
        int offset = startPC;
        while (offset < endPC)
        {
            Instruction instruction = InstructionFactory.create(codeAttribute.code, offset);
            int instructionLength = instruction.length(offset);

            // Is it a subroutine invocation?
            if (branchTargetFinder.isSubroutineInvocation(offset) &&
                !exceptionInfo.isApplicable(offset + ((BranchInstruction)instruction).branchOffset))
            {
                // Append a try block that ends before the subroutine invocation.
                codeAttributeComposer.appendException(new ExceptionInfo(startPC,
                                                                        offset,
                                                                        handlerPC,
                                                                        catchType));

                // The next try block will start after the subroutine invocation.
                startPC = offset + instructionLength;
            }

            offset += instructionLength;
        }

        // Append the exception. Note that exceptions with empty try blocks
        // are automatically ignored.
        codeAttributeComposer.appendException(new ExceptionInfo(startPC,
                                                                endPC,
                                                                handlerPC,
                                                                catchType));

        // TODO: While inlining a subroutine, its exception handler code may have to be inlined too.
    }
}
