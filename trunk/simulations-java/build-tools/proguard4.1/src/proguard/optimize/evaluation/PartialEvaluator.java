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
package proguard.optimize.evaluation;

import proguard.classfile.*;
import proguard.classfile.attribute.*;
import proguard.classfile.attribute.visitor.*;
import proguard.classfile.constant.ClassConstant;
import proguard.classfile.instruction.*;
import proguard.classfile.util.*;
import proguard.classfile.visitor.ClassPrinter;
import proguard.evaluation.*;
import proguard.evaluation.value.*;
import proguard.optimize.peephole.BranchTargetFinder;

/**
 * This AttributeVisitor performs partial evaluation on the code attributes
 * that it visits.
 *
 * @author Eric Lafortune
 */
public class PartialEvaluator
extends      SimplifiedVisitor
implements   AttributeVisitor,
             ExceptionInfoVisitor
{
    //*
    private static final boolean DEBUG         = false;
    private static final boolean DEBUG_RESULTS = false;
    /*/
    private static boolean DEBUG         = true;
    private static boolean DEBUG_RESULTS = true;
    //*/

    private static final int MAXIMUM_EVALUATION_COUNT = 5;

    public static final int NONE            = -2;
    public static final int AT_METHOD_ENTRY = -1;
    public static final int AT_CATCH_ENTRY  = -1;

    private final ValueFactory   valueFactory;
    private final InvocationUnit invocationUnit;
    private final boolean        evaluateAllCode;

    private InstructionOffsetValue[] varProducerValues     = new InstructionOffsetValue[ClassConstants.TYPICAL_CODE_LENGTH];
    private InstructionOffsetValue[] stackProducerValues   = new InstructionOffsetValue[ClassConstants.TYPICAL_CODE_LENGTH];
    private InstructionOffsetValue[] branchOriginValues    = new InstructionOffsetValue[ClassConstants.TYPICAL_CODE_LENGTH];
    private InstructionOffsetValue[] branchTargetValues    = new InstructionOffsetValue[ClassConstants.TYPICAL_CODE_LENGTH];
    private TracedVariables[]        variablesBefore       = new TracedVariables[ClassConstants.TYPICAL_CODE_LENGTH];
    private TracedStack[]            stacksBefore          = new TracedStack[ClassConstants.TYPICAL_CODE_LENGTH];
    private TracedVariables[]        variablesAfter        = new TracedVariables[ClassConstants.TYPICAL_CODE_LENGTH];
    private TracedStack[]            stacksAfter           = new TracedStack[ClassConstants.TYPICAL_CODE_LENGTH];
    private boolean[]                generalizedContexts   = new boolean[ClassConstants.TYPICAL_CODE_LENGTH];
    private int[]                    evaluationCounts      = new int[ClassConstants.TYPICAL_CODE_LENGTH];
    private int[]                    initializedVariables  = new int[ClassConstants.TYPICAL_CODE_LENGTH];
    private boolean                  evaluateExceptions;

    private final BasicBranchUnit    branchUnit;
    private final BranchTargetFinder branchTargetFinder = new BranchTargetFinder();
//    private ClassCleaner       classCleaner       = new ClassCleaner();


    /**
     * Creates a new PartialEvaluator.
     */
    public PartialEvaluator()
    {
        this(new ValueFactory(), new BasicInvocationUnit(), true);
    }


    /**
     * Creates a new PartialEvaluator.
     * @param valueFactory    the value factory that will create all values
     *                        during evaluation.
     * @param invocationUnit  the invocation unit that will handle all
     *                        communication with other fields and methods.
     * @param evaluateAllCode a flag that specifies whether all branch targets
     *                        and exception 'catch' blocks should be evaluated,
     *                        even if they are unreachable.
     */
    public PartialEvaluator(ValueFactory   valueFactory,
                            InvocationUnit invocationUnit,
                            boolean        evaluateAllCode)
    {
        this.valueFactory    = valueFactory;
        this.invocationUnit  = invocationUnit;
        this.evaluateAllCode = evaluateAllCode;

        this.branchUnit = evaluateAllCode ?
            new BasicBranchUnit() :
            new TracedBranchUnit();
    }


    // Implementations for AttributeVisitor.

    public void visitAnyAttribute(Clazz clazz, Attribute attribute) {}


    public void visitCodeAttribute(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
//        DEBUG = DEBUG_RESULTS =
//            clazz.getName().equals("abc/Def") &&
//            method.getName(clazz).equals("abc");

        // TODO: Remove this when the partial evaluator has stabilized.
        // Catch any unexpected exceptions from the actual visiting method.
        try
        {
            // Process the code.
            visitCodeAttribute0(clazz, method, codeAttribute);
        }
        catch (RuntimeException ex)
        {
            System.err.println("Unexpected error while performing partial evaluation:");
            System.err.println("  Class       = ["+clazz.getName()+"]");
            System.err.println("  Method      = ["+method.getName(clazz)+method.getDescriptor(clazz)+"]");
            System.err.println("  Exception   = ["+ex.getClass().getName()+"] ("+ex.getMessage()+")");

            if (DEBUG)
            {
                method.accept(clazz, new ClassPrinter());
            }

            throw ex;
        }
    }


    public void visitCodeAttribute0(Clazz clazz, Method method, CodeAttribute codeAttribute)
    {
        // Evaluate the instructions, starting at the entry point.
        if (DEBUG)
        {
            System.out.println();
            System.out.println("Partial evaluation: "+clazz.getName()+"."+method.getName(clazz)+method.getDescriptor(clazz));
            System.out.println("  Max locals = "+codeAttribute.u2maxLocals);
            System.out.println("  Max stack  = "+codeAttribute.u2maxStack);
        }

        // Reuse the existing variables and stack objects, ensuring the right size.
        TracedVariables variables = new TracedVariables(codeAttribute.u2maxLocals);
        TracedStack     stack     = new TracedStack(codeAttribute.u2maxStack);

        // Initialize the reusable arrays and variables.
        initializeVariables(clazz, method, codeAttribute, variables, stack);

        // Find all instruction offsets,...
        codeAttribute.accept(clazz, method, branchTargetFinder);

        // Start executing the first instruction block.
        evaluateInstructionBlock(clazz,
                                 method,
                                 codeAttribute,
                                 variables,
                                 stack,
                                 0);

        // Evaluate the exception catch blocks, until their entry variables
        // have stabilized.
        do
        {
            // Reset the flag to stop evaluating.
            evaluateExceptions = false;

            // Evaluate all relevant exception catch blocks once.
            codeAttribute.exceptionsAccept(clazz, method, this);
        }
        while (evaluateExceptions);

        if (DEBUG_RESULTS)
        {
            System.out.println("Evaluation results:");

            int offset = 0;
            do
            {
                if (isBranchOrExceptionTarget(offset))
                {
                    System.out.println("Branch target:");
                    if (isTraced(offset))
                    {
                        System.out.println("  Vars:  "+variablesBefore[offset]);
                        System.out.println("  Stack: "+stacksBefore[offset]);
                    }
                }

                Instruction instruction = InstructionFactory.create(codeAttribute.code,
                                                                    offset);
                System.out.println(instruction.toString(offset));

                if (isTraced(offset))
                {
                    InstructionOffsetValue varProducerOffsets = varProducerOffsets(offset);
                    if (varProducerOffsets.instructionOffsetCount() > 0)
                    {
                        System.out.println("     has overall been using information from instructions setting vars: "+varProducerOffsets);
                    }

                    InstructionOffsetValue stackProducerOffsets = stackProducerOffsets(offset);
                    if (stackProducerOffsets.instructionOffsetCount() > 0)
                    {
                        System.out.println("     has overall been using information from instructions setting stack: "+stackProducerOffsets);
                    }

                    int initializationOffset = branchTargetFinder.initializationOffset(offset);
                    if (initializationOffset != NONE)
                    {
                        System.out.println("     is to be initialized at ["+initializationOffset+"]");
                    }

                    InstructionOffsetValue branchTargets = branchTargets(offset);
                    if (branchTargets != null)
                    {
                        System.out.println("     has overall been branching to "+branchTargets);
                    }

                    System.out.println("  Vars:  "+variablesAfter[offset]);
                    System.out.println("  Stack: "+stacksAfter[offset]);
                }

                offset += instruction.length(offset);
            }
            while (offset < codeAttribute.u4codeLength);
        }
    }


    /**
     * Returns whether a block of instructions is ever used.
     */
    public boolean isTraced(int startOffset, int endOffset)
    {
        for (int index = startOffset; index < endOffset; index++)
        {
            if (isTraced(index))
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns whether the instruction at the given offset has ever been
     * executed during the partial evaluation.
     */
    public boolean isTraced(int instructionOffset)
    {
        return evaluationCounts[instructionOffset] > 0;
    }


    /**
     * Returns whether the instruction at the given offset is the target of a
     * branch instruction or an exception.
     */
    public boolean isBranchOrExceptionTarget(int instructionOffset)
    {
        return branchTargetFinder.isBranchTarget(instructionOffset) ||
               branchTargetFinder.isExceptionHandler(instructionOffset);
    }


    /**
     * Returns whether the instruction at the given offset is part of a
     * subroutine.
     */
    public boolean isSubroutine(int instructionOffset)
    {
        return branchTargetFinder.isSubroutine(instructionOffset);
    }


    /**
     * Returns whether the subroutine at the given offset is ever returning
     * by means of a regular 'ret' instruction.
     */
    public boolean isSubroutineReturning(int instructionOffset)
    {
        return branchTargetFinder.isSubroutineReturning(instructionOffset);
    }


    /**
     * Returns the offset after the subroutine that starts at the given
     * offset.
     */
    public int subroutineEnd(int instructionOffset)
    {
        return branchTargetFinder.subroutineEnd(instructionOffset);
    }


    /**
     * Returns the instruction offset at which the object instance that is
     * created at the given 'new' instruction offset is initialized, or
     * <code>NONE</code> if it is not being created.
     */
    public int initializationOffset(int instructionOffset)
    {
        return branchTargetFinder.initializationOffset(instructionOffset);
    }


    /**
     * Returns whether the method is an instance initializer.
     */
    public boolean isInitializer()
    {
        return branchTargetFinder.isInitializer();
    }


    /**
     * Returns the instruction offset at which this initializer is calling
     * the "super" or "this" initializer method, or <code>NONE</code> if it is
     * not an initializer.
     */
    public int superInitializationOffset()
    {
        return branchTargetFinder.superInitializationOffset();
    }


    /**
     * Returns the offset of the 'new' instruction that corresponds to the
     * invocation of the instance initializer at the given offset, or
     * <code>AT_METHOD_ENTRY</code> if the invocation is calling the "super" or
     * "this" initializer method, , or <code>NONE</code> if it is not a 'new'
     * instruction.
     */
    public int creationOffset(int offset)
    {
        return branchTargetFinder.creationOffset(offset);
    }


    /**
     * Returns the variables before execution of the instruction at the given
     * offset.
     */
    public TracedVariables getVariablesBefore(int instructionOffset)
    {
        return variablesBefore[instructionOffset];
    }


    /**
     * Returns the variables after execution of the instruction at the given
     * offset.
     */
    public TracedVariables getVariablesAfter(int instructionOffset)
    {
        return variablesAfter[instructionOffset];
    }


    /**
     * Returns the instruction offsets that set the variable that is being
     * used at the given instruction offset.
     */
    public InstructionOffsetValue varProducerOffsets(int instructionOffset)
    {
        return varProducerValues[instructionOffset];
    }


    /**
     * Returns the stack before execution of the instruction at the given
     * offset.
     */
    public TracedStack getStackBefore(int instructionOffset)
    {
        return stacksBefore[instructionOffset];
    }


    /**
     * Returns the stack after execution of the instruction at the given
     * offset.
     */
    public TracedStack getStackAfter(int instructionOffset)
    {
        return stacksAfter[instructionOffset];
    }


    /**
     * Returns the instruction offsets that set the stack entries that are being
     * used at the given instruction offset.
     */
    public InstructionOffsetValue stackProducerOffsets(int instructionOffset)
    {
        return stackProducerValues[instructionOffset];
    }


    /**
     * Returns the instruction offsets that branch to the given instruction
     * offset.
     */
    public InstructionOffsetValue branchOrigins(int instructionOffset)
    {
        return branchOriginValues[instructionOffset];
    }


    /**
     * Returns the instruction offsets to which the given instruction offset
     * branches.
     */
    public InstructionOffsetValue branchTargets(int instructionOffset)
    {
        return branchTargetValues[instructionOffset];
    }


    /**
     * Returns the variable that is initialized at the given instruction offset,
     * or <code>NONE</code> if no variable was initialized.
     */
    public int initializedVariable(int instructionOffset)
    {
        return initializedVariables[instructionOffset];
    }


    // Implementations for ExceptionInfoVisitor.

    public void visitExceptionInfo(Clazz clazz, Method method, CodeAttribute codeAttribute, ExceptionInfo exceptionInfo)
    {
        int startPC = exceptionInfo.u2startPC;
        int endPC   = exceptionInfo.u2endPC;

        // Do we have to evaluate this exception catch block?
        if (isTraced(startPC, endPC))
        {
            int handlerPC = exceptionInfo.u2handlerPC;
            int catchType = exceptionInfo.u2catchType;

            if (DEBUG) System.out.println("Partial evaluation of exception ["+startPC +","+endPC +"] -> ["+handlerPC+"]:");

            // Reuse the existing variables and stack objects, ensuring the
            // right size.
            TracedVariables variables = new TracedVariables(codeAttribute.u2maxLocals);
            TracedStack     stack     = new TracedStack(codeAttribute.u2maxStack);

            // Initialize the trace values.
            Value storeValue = new InstructionOffsetValue(AT_CATCH_ENTRY);
            variables.setProducerValue(storeValue);
            stack.setProducerValue(storeValue);

            // Initialize the variables by generalizing the variables of the
            // try block. Make sure to include the results of the last
            // instruction for preverification.
            generalizeVariables(startPC,
                                endPC,
                                evaluateAllCode,
                                variables);

            // Initialize the the stack.
            //stack.push(valueFactory.createReference((ClassConstant)((ProgramClass)clazz).getConstant(exceptionInfo.u2catchType), false));
            String catchClassName = catchType != 0 ?
                 clazz.getClassName(catchType) :
                 ClassConstants.INTERNAL_NAME_JAVA_LANG_THROWABLE;

            Clazz catchClass = catchType != 0 ?
                ((ClassConstant)((ProgramClass)clazz).getConstant(catchType)).referencedClass :
                null;

            stack.push(valueFactory.createReferenceValue(catchClassName,
                                                         catchClass,
                                                         false));

            int evaluationCount = evaluationCounts[handlerPC];

            // Evaluate the instructions, starting at the entry point.
            evaluateInstructionBlock(clazz,
                                     method,
                                     codeAttribute,
                                     variables,
                                     stack,
                                     handlerPC);

            // Remember to evaluate all exception handlers once more.
            if (!evaluateExceptions)
            {
                evaluateExceptions = evaluationCount < evaluationCounts[handlerPC];
            }
        }
        else if (evaluateAllCode)
        {
            if (DEBUG) System.out.println("No information for partial evaluation of exception ["+startPC +","+endPC +"] -> ["+exceptionInfo.u2handlerPC+"] yet");

            // We don't have any information on the try block yet, but we do
            // have to evaluate the exception handler.
            // Remember to evaluate all exception handlers once more.
            evaluateExceptions = true;
        }
        else
        {
            if (DEBUG) System.out.println("No information for partial evaluation of exception ["+startPC +","+endPC +"] -> ["+exceptionInfo.u2handlerPC+"]");
        }
    }


    // Utility methods to evaluate instruction blocks.

    /**
     * Pushes block of instructions to be executed on the given stack.
     */
    private void pushInstructionBlock(TracedVariables variables,
                                      TracedStack     stack,
                                      int             startOffset,
                                      java.util.Stack instructionBlockStack)
    {
        instructionBlockStack.push(new MyInstructionBlock(variables,
                                                          stack,
                                                          startOffset));
    }


    /**
     * Evaluates a block of instructions, starting at the given offset and ending
     * at a branch instruction, a return instruction, or a throw instruction.
     */
    private void evaluateInstructionBlock(Clazz           clazz,
                                          Method          method,
                                          CodeAttribute   codeAttribute,
                                          TracedVariables variables,
                                          TracedStack     stack,
                                          int             startOffset)
    {
        // We're now defining out own stack instead of more elegant recursion,
        // in order to avoid stack overflow errors in the VM.
        java.util.Stack instructionBlockStack = new java.util.Stack();

        // Execute the initial instruction block.
        evaluateInstructionBlock(clazz,
                                 method,
                                 codeAttribute,
                                 variables,
                                 stack,
                                 startOffset,
                                 instructionBlockStack);

        // Execute all resulting instruction blocks on the execution stack.
        while (!instructionBlockStack.empty())
        {
            MyInstructionBlock instructionBlock =
                (MyInstructionBlock)instructionBlockStack.pop();

            evaluateInstructionBlock(clazz,
                                     method,
                                     codeAttribute,
                                     instructionBlock.variables,
                                     instructionBlock.stack,
                                     instructionBlock.startOffset,
                                     instructionBlockStack);
        }
    }


    /**
     * Evaluates a block of instructions, starting at the given offset and ending
     * at a branch instruction, a return instruction, or a throw instruction.
     * Instruction blocks that are to be evaluated as a result are pushed on
     * the given stack.
     */
    private void evaluateInstructionBlock(Clazz            clazz,
                                          Method           method,
                                          CodeAttribute    codeAttribute,
                                          TracedVariables  variables,
                                          TracedStack      stack,
                                          int              startOffset,
                                          java.util.Stack  instructionBlockStack)
    {
        byte[] code = codeAttribute.code;

        if (DEBUG)
        {
             System.out.println("Instruction block starting at ["+startOffset+"] in "+
                                ClassUtil.externalFullMethodDescription(clazz.getName(),
                                                                        0,
                                                                        method.getName(clazz),
                                                                        method.getDescriptor(clazz)));
             System.out.println("Init vars:  "+variables);
             System.out.println("Init stack: "+stack);
        }

        Processor processor = new Processor(variables, stack, valueFactory, branchUnit, invocationUnit);

        int instructionOffset = startOffset;

        int maxOffset = startOffset;

        // Evaluate the subsequent instructions.
        while (true)
        {
            if (maxOffset < instructionOffset)
            {
                maxOffset = instructionOffset;
            }

            // Maintain a generalized local variable frame and stack at this
            // instruction offset, before execution.
            int evaluationCount = evaluationCounts[instructionOffset];
            if (evaluationCount == 0)
            {
                // First time we're passing by this instruction.
                if (variablesBefore[instructionOffset] == null)
                {
                    // There's not even a context at this index yet.
                    variablesBefore[instructionOffset] = new TracedVariables(variables);
                    stacksBefore[instructionOffset]    = new TracedStack(stack);
                }
                else
                {
                    // Reuse the context objects at this index.
                    variablesBefore[instructionOffset].initialize(variables);
                    stacksBefore[instructionOffset].copy(stack);
                }

                // We'll execute in the generalized context, because it is
                // the same as the current context.
                generalizedContexts[instructionOffset] = true;
            }
            else
            {
                // Merge in the current context.
                boolean variablesChanged = variablesBefore[instructionOffset].generalize(variables, true);
                boolean stackChanged     = stacksBefore[instructionOffset].generalize(stack);

                // Bail out if the current context is the same as last time.
                if (!variablesChanged &&
                    !stackChanged     &&
                    generalizedContexts[instructionOffset])
                {
                    if (DEBUG) System.out.println("Repeated variables, stack, and branch targets");

                    break;
                }

                // See if this instruction has been evaluated an excessive number
                // of times.
                if (evaluationCount >= MAXIMUM_EVALUATION_COUNT)
                {
                    if (DEBUG) System.out.println("Generalizing current context after "+evaluationCount+" evaluations");

                    // Continue, but generalize the current context.
                    // Note that the most recent variable values have to remain
                    // last in the generalizations, for the sake of the ret
                    // instruction.
                    variables.generalize(variablesBefore[instructionOffset], false);
                    stack.generalize(stacksBefore[instructionOffset]);

                    // We'll execute in the generalized context.
                    generalizedContexts[instructionOffset] = true;
                }
                else
                {
                    // We'll execute in the current context.
                    generalizedContexts[instructionOffset] = false;
                }
            }

            // We'll evaluate this instruction.
            evaluationCounts[instructionOffset]++;

            // Remember this instruction's offset with any stored value.
            Value storeValue = new InstructionOffsetValue(instructionOffset);
            variables.setProducerValue(storeValue);
            stack.setProducerValue(storeValue);

            // Reset the trace value.
            InstructionOffsetValue traceValue = InstructionOffsetValue.EMPTY_VALUE;
            variables.setCollectedProducerValue(traceValue);
            stack.setCollectedProducerValue(traceValue);

            // Reset the initialization flag.
            variables.resetInitialization();

            // Note that the instruction is only volatile.
            Instruction instruction = InstructionFactory.create(code, instructionOffset);

            // By default, the next instruction will be the one after this
            // instruction.
            int nextInstructionOffset = instructionOffset +
                                        instruction.length(instructionOffset);
            InstructionOffsetValue nextInstructionOffsetValue = new InstructionOffsetValue(nextInstructionOffset);
            branchUnit.resetCalled();
            branchUnit.setTraceBranchTargets(nextInstructionOffsetValue);

            if (DEBUG)
            {
                System.out.println(instruction.toString(instructionOffset));
            }

            try
            {
                // Process the instruction. The processor may modify the
                // variables and the stack, and it may call the branch unit
                // and the invocation unit.
                instruction.accept(clazz,
                                   method,
                                   codeAttribute,
                                   instructionOffset,
                                   processor);
            }
            catch (RuntimeException ex)
            {
                System.err.println("Unexpected error while evaluating instruction:");
                System.err.println("  Class       = ["+clazz.getName()+"]");
                System.err.println("  Method      = ["+method.getName(clazz)+method.getDescriptor(clazz)+"]");
                System.err.println("  Instruction = "+instruction.toString(instructionOffset));
                System.err.println("  Exception   = ["+ex.getClass().getName()+"] ("+ex.getMessage()+")");

                throw ex;
            }

            // Collect the offsets of the instructions whose results were used.
            InstructionOffsetValue variablesTraceValue = variables.getCollectedProducerValue().instructionOffsetValue();
            InstructionOffsetValue stackTraceValue     = stack.getCollectedProducerValue().instructionOffsetValue();
            varProducerValues[instructionOffset] =
                varProducerValues[instructionOffset].generalize(variablesTraceValue).instructionOffsetValue();
            stackProducerValues[instructionOffset] =
                stackProducerValues[instructionOffset].generalize(stackTraceValue).instructionOffsetValue();
            initializedVariables[instructionOffset] = variables.getInitializationIndex();

            // Collect the branch targets from the branch unit.
            InstructionOffsetValue branchTargets = branchUnit.getTraceBranchTargets();
            int branchTargetCount = branchTargets.instructionOffsetCount();

            // Stop tracing.
            variables.setCollectedProducerValue(traceValue);
            stack.setCollectedProducerValue(traceValue);
            branchUnit.setTraceBranchTargets(traceValue);

            if (DEBUG)
            {
                if (variablesTraceValue.instructionOffsetCount() > 0)
                {
                    System.out.println("     has used information from instructions setting vars: "+variablesTraceValue);
                }
                if (stackTraceValue.instructionOffsetCount() > 0)
                {
                    System.out.println("     has used information from instructions setting stack: "+stackTraceValue);
                }
                if (branchUnit.wasCalled())
                {
                    System.out.println("     is branching to "+branchTargets);
                }

                if (varProducerValues[instructionOffset].instructionOffsetCount() > 0)
                {
                    System.out.println("     has up till now been using information from instructions setting vars: "+varProducerValues[instructionOffset]);
                }
                if (stackProducerValues[instructionOffset].instructionOffsetCount() > 0)
                {
                    System.out.println("     has up till now been using information from instructions setting stack: "+stackProducerValues[instructionOffset]);
                }
                if (branchTargetValues[instructionOffset] != null)
                {
                    System.out.println("     has up till now been branching to "+branchTargetValues[instructionOffset]);
                }

                System.out.println(" Vars:  "+variables);
                System.out.println(" Stack: "+stack);
            }

            // Maintain a generalized local variable frame and stack at this
            // instruction offset, after execution.
            if (evaluationCount == 0)
            {
                // First time we're passing by this instruction.
                if (variablesAfter[instructionOffset] == null)
                {
                    // There's not even a context at this index yet.
                    variablesAfter[instructionOffset] = new TracedVariables(variables);
                    stacksAfter[instructionOffset]    = new TracedStack(stack);
                }
                else
                {
                    // Reuse the context objects at this index.
                    variablesAfter[instructionOffset].initialize(variables);
                    stacksAfter[instructionOffset].copy(stack);
                }
            }
            else
            {
                // Merge in the current context.
                variablesAfter[instructionOffset].generalize(variables, true);
                stacksAfter[instructionOffset].generalize(stack);
            }

            // Did the branch unit get called?
            if (branchUnit.wasCalled())
            {
                // Accumulate the branch targets at this offset.
                branchTargetValues[instructionOffset] = branchTargetValues[instructionOffset] == null ?
                    branchTargets :
                    branchTargetValues[instructionOffset].generalize(branchTargets).instructionOffsetValue();

                // Are there no branch targets at all?
                if (branchTargetCount == 0)
                {
                    // Exit from this code block.
                    break;
                }

                // Accumulate the branch origins at the branch target offsets.
                InstructionOffsetValue instructionOffsetValue = new InstructionOffsetValue(instructionOffset);
                for (int index = 0; index < branchTargetCount; index++)
                {
                    int branchTarget = branchTargets.instructionOffset(index);
                    branchOriginValues[branchTarget] = branchOriginValues[branchTarget] == null ?
                        instructionOffsetValue:
                        branchOriginValues[branchTarget].generalize(instructionOffsetValue).instructionOffsetValue();
                }

                // Are there multiple branch targets?
                if (branchTargetCount > 1)
                {
                    // Push them on the execution stack and exit from this block.
                    for (int index = 0; index < branchTargetCount; index++)
                    {
                        if (DEBUG) System.out.println("Alternative branch #"+index+" out of "+branchTargetCount+", from ["+instructionOffset+"] to ["+branchTargets.instructionOffset(index)+"]");

                        pushInstructionBlock(new TracedVariables(variables),
                                             new TracedStack(stack),
                                             branchTargets.instructionOffset(index),
                                             instructionBlockStack);
                    }

                    break;
                }

                if (DEBUG) System.out.println("Definite branch from ["+instructionOffset+"] to ["+branchTargets.instructionOffset(0)+"]");
            }

            // Just continue with the next instruction.
            instructionOffset = branchTargets.instructionOffset(0);

            // Clear the context of a subroutine before entering it, in order
            // to avoid context conflicts across different invocations.
            if (instruction.opcode == InstructionConstants.OP_JSR ||
                instruction.opcode == InstructionConstants.OP_JSR_W)
            {
                // A subroutine has a single entry point and a single exit point,
                // so we can easily loop over its instructions.
                int subroutineEnd = branchTargetFinder.subroutineEnd(instructionOffset);

                if (DEBUG) System.out.println("Clearing context of subroutine from "+instructionOffset+" to "+subroutineEnd);

                for (int offset = instructionOffset; offset < subroutineEnd; offset++)
                {
                    if (branchTargetFinder.isInstruction(offset))
                    {
                        evaluationCounts[offset] = 0;
                    }
                }

                evaluateInstructionBlock(clazz,
                                         method,
                                         codeAttribute,
                                         new TracedVariables(variables),
                                         new TracedStack(stack),
                                         instructionOffset);

                if (DEBUG) System.out.println("Evaluating exceptions of subroutine from "+instructionOffset+" to "+subroutineEnd);

                // Evaluate all relevant exception catch blocks once.
                codeAttribute.exceptionsAccept(clazz,
                                               method,
                                               instructionOffset,
                                               subroutineEnd,
                                               this);

                if (DEBUG) System.out.println("Ending subroutine from "+instructionOffset+" to "+subroutineEnd);

                break;
            }
        }

        if (DEBUG) System.out.println("Ending processing of instruction block starting at ["+startOffset+"]");
    }


    // Small utility methods.

    /**
     * Initializes the data structures for the variables, stack, etc.
     */
    private void initializeVariables(Clazz           clazz,
                                     Method          method,
                                     CodeAttribute   codeAttribute,
                                     TracedVariables variables,
                                     TracedStack     stack)
    {
        int codeLength = codeAttribute.u4codeLength;

        // Create new arrays for storing information at each instruction offset.
        if (variablesAfter.length < codeLength)
        {
            // Create new arrays.
            varProducerValues    = new InstructionOffsetValue[codeLength];
            stackProducerValues  = new InstructionOffsetValue[codeLength];
            branchOriginValues   = new InstructionOffsetValue[codeLength];
            branchTargetValues   = new InstructionOffsetValue[codeLength];
            variablesBefore      = new TracedVariables[codeLength];
            stacksBefore         = new TracedStack[codeLength];
            variablesAfter       = new TracedVariables[codeLength];
            stacksAfter          = new TracedStack[codeLength];
            generalizedContexts  = new boolean[codeLength];
            evaluationCounts     = new int[codeLength];
            initializedVariables = new int[codeLength];

            // Reset the arrays.
            for (int index = 0; index < codeLength; index++)
            {
                varProducerValues[index]    = InstructionOffsetValue.EMPTY_VALUE;
                stackProducerValues[index]  = InstructionOffsetValue.EMPTY_VALUE;
                initializedVariables[index] = NONE;
            }
        }
        else
        {
            // Reset the arrays.
            for (int index = 0; index < codeLength; index++)
            {
                varProducerValues[index]    = InstructionOffsetValue.EMPTY_VALUE;
                stackProducerValues[index]  = InstructionOffsetValue.EMPTY_VALUE;
                branchOriginValues[index]   = null;
                branchTargetValues[index]   = null;
                generalizedContexts[index]  = false;
                evaluationCounts[index]     = 0;
                initializedVariables[index] = NONE;

                if (variablesBefore[index] != null)
                {
                    variablesBefore[index].reset(codeAttribute.u2maxLocals);
                }

                if (stacksBefore[index] != null)
                {
                    stacksBefore[index].reset(codeAttribute.u2maxStack);
                }

                if (variablesAfter[index] != null)
                {
                    variablesAfter[index].reset(codeAttribute.u2maxLocals);
                }

                if (stacksAfter[index] != null)
                {
                    stacksAfter[index].reset(codeAttribute.u2maxStack);
                }
            }
        }

        // Create the method parameters.
        TracedVariables parameters = new TracedVariables(codeAttribute.u2maxLocals);

        // Remember this instruction's offset with any stored value.
        Value storeValue = new InstructionOffsetValue(AT_METHOD_ENTRY);
        parameters.setProducerValue(storeValue);

        // Reset the trace value.
        InstructionOffsetValue traceValue = InstructionOffsetValue.EMPTY_VALUE;
        parameters.setCollectedProducerValue(traceValue);

        // Initialize the method parameters.
        invocationUnit.enterMethod(clazz, method, parameters);

        if (DEBUG)
        {
            System.out.println("  Params: "+parameters);
        }

        // Initialize the variables with the parameters.
        variables.initialize(parameters);

        // Set the store value of each parameter variable.
        InstructionOffsetValue atMethodEntry = new InstructionOffsetValue(AT_METHOD_ENTRY);

        for (int index = 0; index < parameters.size(); index++)
        {
            variables.setProducerValue(index, atMethodEntry);
        }
    }


    /**
     * Generalize the local variable frames of a block of instructions.
     */
    private void generalizeVariables(int             startOffset,
                                     int             endOffset,
                                     boolean         includeAfterLastInstruction,
                                     TracedVariables generalizedVariables)
    {
        boolean first     = true;
        int     lastIndex = -1;

        // Generalize the variables before each of the instructions in the block.
        for (int index = startOffset; index < endOffset; index++)
        {
            if (isTraced(index))
            {
                TracedVariables tracedVariables = variablesBefore[index];

                if (first)
                {
                    // Initialize the variables with the first traced local
                    // variable frame.
                    generalizedVariables.initialize(tracedVariables);

                    first = false;
                }
                else
                {
                    // Generalize the variables with the traced local variable
                    // frame. We can't use the return value, because local
                    // generalization can be different a couple of times,
                    // with the global generalization being the same.
                    generalizedVariables.generalize(tracedVariables, false);
                }

                lastIndex = index;
            }
        }

        // Generalize the variables after the last instruction in the block,
        // if required.
        if (includeAfterLastInstruction &&
            lastIndex >= 0)
        {
            TracedVariables tracedVariables = variablesAfter[lastIndex];

            if (first)
            {
                // Initialize the variables with the local variable frame.
                generalizedVariables.initialize(tracedVariables);
            }
            else
            {
                // Generalize the variables with the local variable frame.
                generalizedVariables.generalize(tracedVariables, false);
            }
        }

        // Just clear the variables if there aren't any traced instructions
        // in the block.
        if (first)
        {
            generalizedVariables.reset(generalizedVariables.size());
        }
    }


    private static class MyInstructionBlock
    {
        private TracedVariables variables;
        private TracedStack     stack;
        private int             startOffset;


        private MyInstructionBlock(TracedVariables variables,
                                   TracedStack     stack,
                                   int             startOffset)
        {
            this.variables   = variables;
            this.stack       = stack;
            this.startOffset = startOffset;
        }
    }
}
