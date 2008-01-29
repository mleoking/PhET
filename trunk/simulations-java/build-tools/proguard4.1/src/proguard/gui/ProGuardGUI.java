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
package proguard.gui;

import proguard.*;
import proguard.classfile.util.ClassUtil;
import proguard.gui.splash.*;
import proguard.util.ListUtil;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;


/**
 * GUI for configuring and executing ProGuard and ReTrace.
 *
 * @author Eric Lafortune
 */
public class ProGuardGUI extends JFrame
{
    private static final String NO_SPLASH_OPTION = "-nosplash";

    private static final String TITLE_IMAGE_FILE          = "vtitle.gif";
    private static final String BOILERPLATE_CONFIGURATION = "boilerplate.pro";
    private static final String DEFAULT_CONFIGURATION     = "default.pro";

    private static final String KEEP_ATTRIBUTE_DEFAULT               = "Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,LocalVariable*Table,*Annotation*,Synthetic,EnclosingMethod";
    private static final String SOURCE_FILE_ATTRIBUTE_DEFAULT        = "SourceFile";
    private static final String ADAPT_RESOURCE_FILE_NAMES_DEFAULT    = "**.properties";
    private static final String ADAPT_RESOURCE_FILE_CONTENTS_DEFAULT = "**.properties,META-INF/MANIFEST.MF";

    private static final Border BORDER = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);

    static boolean systemOutRedirected;

    private final JFileChooser configurationChooser = new JFileChooser("");
    private final JFileChooser fileChooser          = new JFileChooser("");

    private final SplashPanel splashPanel;

    private final ClassPathPanel programPanel = new ClassPathPanel(this, true);
    private final ClassPathPanel libraryPanel = new ClassPathPanel(this, false);

    private       KeepSpecification[] boilerplateKeep;
    private final JCheckBox[]         boilerplateKeepCheckBoxes;
    private final JTextField[]        boilerplateKeepTextFields;

    private final KeepSpecificationsPanel additionalKeepPanel = new KeepSpecificationsPanel(this, true, false, false, false, false);

    private       KeepSpecification[] boilerplateKeepNames;
    private final JCheckBox[]         boilerplateKeepNamesCheckBoxes;
    private final JTextField[]        boilerplateKeepNamesTextFields;

    private final KeepSpecificationsPanel additionalKeepNamesPanel = new KeepSpecificationsPanel(this, true, false, true, false, false);

    private       ClassSpecification[] boilerplateNoSideEffectMethods;
    private final JCheckBox[]          boilerplateNoSideEffectMethodCheckBoxes;

    private final ClassSpecificationsPanel additionalNoSideEffectsPanel = new ClassSpecificationsPanel(this, false);

    private final ClassSpecificationsPanel whyAreYouKeepingPanel = new ClassSpecificationsPanel(this, false);

    private final JCheckBox shrinkCheckBox     = new JCheckBox(msg("shrink"));
    private final JCheckBox printUsageCheckBox = new JCheckBox(msg("printUsage"));

    private final JCheckBox optimizeCheckBox                = new JCheckBox(msg("optimize"));
    private final JCheckBox allowAccessModificationCheckBox = new JCheckBox(msg("allowAccessModification"));
    private final JLabel    optimizationPassesLabel         = new JLabel(msg("optimizationPasses"));
    private final JSpinner  optimizationPassesSpinner       = new JSpinner(new SpinnerNumberModel(1, 1, 9, 1));

    private final JCheckBox obfuscateCheckBox                 = new JCheckBox(msg("obfuscate"));
    private final JCheckBox printMappingCheckBox              = new JCheckBox(msg("printMapping"));
    private final JCheckBox applyMappingCheckBox              = new JCheckBox(msg("applyMapping"));
    private final JCheckBox obfuscationDictionaryCheckBox     = new JCheckBox(msg("obfuscationDictionary"));
    private final JCheckBox overloadAggressivelyCheckBox      = new JCheckBox(msg("overloadAggressively"));
    private final JCheckBox useUniqueClassMemberNamesCheckBox = new JCheckBox(msg("useUniqueClassMemberNames"));
    private final JCheckBox useMixedCaseClassNamesCheckBox    = new JCheckBox(msg("useMixedCaseClassNames"));
    private final JCheckBox flattenPackageHierarchyCheckBox   = new JCheckBox(msg("flattenPackageHierarchy"));
    private final JCheckBox repackageClassesCheckBox          = new JCheckBox(msg("repackageClasses"));
    private final JCheckBox keepAttributesCheckBox            = new JCheckBox(msg("keepAttributes"));
    private final JCheckBox newSourceFileAttributeCheckBox    = new JCheckBox(msg("renameSourceFileAttribute"));
    private final JCheckBox adaptResourceFileNamesCheckBox    = new JCheckBox(msg("adaptResourceFileNames"));
    private final JCheckBox adaptResourceFileContentsCheckBox = new JCheckBox(msg("adaptResourceFileContents"));

    private final JCheckBox preverifyCheckBox    = new JCheckBox(msg("preverify"));
    private final JCheckBox microEditionCheckBox = new JCheckBox(msg("microEdition"));
    private final JCheckBox targetCheckBox       = new JCheckBox(msg("target"));

    private final JComboBox  targetComboBox = new JComboBox(ListUtil.commaSeparatedList(msg("targets")).toArray());

    private final JCheckBox verboseCheckBox                          = new JCheckBox(msg("verbose"));
    private final JCheckBox ignoreWarningsCheckBox                   = new JCheckBox(msg("ignoreWarnings"));
    private final JCheckBox warnCheckBox                             = new JCheckBox(msg("warn"));
    private final JCheckBox noteCheckBox                             = new JCheckBox(msg("note"));
    private final JCheckBox skipNonPublicLibraryClassesCheckBox      = new JCheckBox(msg("skipNonPublicLibraryClasses"));
    private final JCheckBox skipNonPublicLibraryClassMembersCheckBox = new JCheckBox(msg("skipNonPublicLibraryClassMembers"));
    private final JCheckBox forceProcessingCheckBox                  = new JCheckBox(msg("forceProcessing"));
    private final JCheckBox printSeedsCheckBox                       = new JCheckBox(msg("printSeeds"));
    private final JCheckBox printConfigurationCheckBox               = new JCheckBox(msg("printConfiguration"));
    private final JCheckBox dumpCheckBox                             = new JCheckBox(msg("dump"));

    private final JTextField printUsageTextField                = new JTextField(40);
    private final JTextField printMappingTextField              = new JTextField(40);
    private final JTextField applyMappingTextField              = new JTextField(40);
    private final JTextField obfuscationDictionaryTextField     = new JTextField(40);
    private final JTextField flattenPackageHierarchyTextField   = new JTextField(40);
    private final JTextField repackageClassesTextField          = new JTextField(40);
    private final JTextField keepAttributesTextField            = new JTextField(40);
    private final JTextField newSourceFileAttributeTextField    = new JTextField(40);
    private final JTextField adaptResourceFileNamesTextField    = new JTextField(40);
    private final JTextField adaptResourceFileContentsTextField = new JTextField(40);
    private final JTextField printSeedsTextField                = new JTextField(40);
    private final JTextField printConfigurationTextField        = new JTextField(40);
    private final JTextField dumpTextField                      = new JTextField(40);

    private final JTextArea  consoleTextArea = new JTextArea(msg("processingInfo"), 3, 40);

    private final JCheckBox  reTraceVerboseCheckBox  = new JCheckBox(msg("verbose"));
    private final JTextField reTraceMappingTextField = new JTextField(40);
    private final JTextArea  stackTraceTextArea      = new JTextArea(3, 40);
    private final JTextArea  reTraceTextArea         = new JTextArea(msg("reTraceInfo"), 3, 40);


    /**
     * Creates a new ProGuardGUI.
     */
    public ProGuardGUI()
    {
        setTitle("ProGuard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Create some constraints that can be reused.
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 4, 0, 4);

        GridBagConstraints constraintsStretch = new GridBagConstraints();
        constraintsStretch.fill    = GridBagConstraints.HORIZONTAL;
        constraintsStretch.weightx = 1.0;
        constraintsStretch.anchor  = GridBagConstraints.WEST;
        constraintsStretch.insets  = constraints.insets;

        GridBagConstraints constraintsLast = new GridBagConstraints();
        constraintsLast.gridwidth = GridBagConstraints.REMAINDER;
        constraintsLast.anchor    = GridBagConstraints.WEST;
        constraintsLast.insets    = constraints.insets;

        GridBagConstraints constraintsLastStretch = new GridBagConstraints();
        constraintsLastStretch.gridwidth = GridBagConstraints.REMAINDER;
        constraintsLastStretch.fill      = GridBagConstraints.HORIZONTAL;
        constraintsLastStretch.weightx   = 1.0;
        constraintsLastStretch.anchor    = GridBagConstraints.WEST;
        constraintsLastStretch.insets    = constraints.insets;

        GridBagConstraints splashPanelConstraints = new GridBagConstraints();
        splashPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        splashPanelConstraints.fill      = GridBagConstraints.BOTH;
        splashPanelConstraints.weightx   = 1.0;
        splashPanelConstraints.weighty   = 0.02;
        splashPanelConstraints.anchor    = GridBagConstraints.NORTHWEST;
        //splashPanelConstraints.insets    = constraints.insets;

        GridBagConstraints welcomeTextAreaConstraints = new GridBagConstraints();
        welcomeTextAreaConstraints.gridwidth = GridBagConstraints.REMAINDER;
        welcomeTextAreaConstraints.fill      = GridBagConstraints.NONE;
        welcomeTextAreaConstraints.weightx   = 1.0;
        welcomeTextAreaConstraints.weighty   = 0.01;
        welcomeTextAreaConstraints.anchor    = GridBagConstraints.CENTER;//NORTHWEST;
        welcomeTextAreaConstraints.insets    = new Insets(20, 40, 20, 40);

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        panelConstraints.fill      = GridBagConstraints.HORIZONTAL;
        panelConstraints.weightx   = 1.0;
        panelConstraints.anchor    = GridBagConstraints.NORTHWEST;
        panelConstraints.insets    = constraints.insets;

        GridBagConstraints stretchPanelConstraints = new GridBagConstraints();
        stretchPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        stretchPanelConstraints.fill      = GridBagConstraints.BOTH;
        stretchPanelConstraints.weightx   = 1.0;
        stretchPanelConstraints.weighty   = 1.0;
        stretchPanelConstraints.anchor    = GridBagConstraints.NORTHWEST;
        stretchPanelConstraints.insets    = constraints.insets;

        GridBagConstraints glueConstraints = new GridBagConstraints();
        glueConstraints.fill    = GridBagConstraints.BOTH;
        glueConstraints.weightx = 0.01;
        glueConstraints.weighty = 0.01;
        glueConstraints.anchor  = GridBagConstraints.NORTHWEST;
        glueConstraints.insets  = constraints.insets;

        GridBagConstraints bottomButtonConstraints = new GridBagConstraints();
        bottomButtonConstraints.anchor = GridBagConstraints.SOUTHEAST;
        bottomButtonConstraints.insets = new Insets(2, 2, 4, 6);
        bottomButtonConstraints.ipadx  = 10;
        bottomButtonConstraints.ipady  = 2;

        GridBagConstraints lastBottomButtonConstraints = new GridBagConstraints();
        lastBottomButtonConstraints.gridwidth = GridBagConstraints.REMAINDER;
        lastBottomButtonConstraints.anchor    = GridBagConstraints.SOUTHEAST;
        lastBottomButtonConstraints.insets    = bottomButtonConstraints.insets;
        lastBottomButtonConstraints.ipadx     = bottomButtonConstraints.ipadx;
        lastBottomButtonConstraints.ipady     = bottomButtonConstraints.ipady;

        // Leave room for a growBox on Mac OS X.
        if (System.getProperty("os.name").toLowerCase().startsWith("mac os x"))
        {
            lastBottomButtonConstraints.insets = new Insets(2, 2, 4, 6 + 16);
        }

        GridBagLayout layout = new GridBagLayout();

        configurationChooser.addChoosableFileFilter(
            new ExtensionFileFilter(msg("proExtension"), new String[] { ".pro" }));

        // Create the opening panel.
        Sprite splash =
            new CompositeSprite(new Sprite[]
        {
            new ColorSprite(new ConstantColor(Color.gray),
            new FontSprite(new ConstantFont(new Font("sansserif", Font.BOLD, 90)),
            new TextSprite(new ConstantString("ProGuard"),
                           new ConstantInt(160),
                           new LinearInt(-10, 120, new SmoothTiming(500, 1000))))),

            new ColorSprite(new ConstantColor(Color.white),
            new FontSprite(new ConstantFont(new Font("sansserif", Font.BOLD, 45)),
            new ShadowedSprite(new ConstantInt(3),
                               new ConstantInt(3),
                               new ConstantDouble(0.4),
                               new ConstantInt(1),
                               new CompositeSprite(new Sprite[]
            {
                new TextSprite(new ConstantString(msg("shrinking")),
                               new LinearInt(1000, 60, new SmoothTiming(1000, 2000)),
                               new ConstantInt(70)),
                new TextSprite(new ConstantString(msg("optimization")),
                               new LinearInt(1000, 400, new SmoothTiming(1500, 2500)),
                               new ConstantInt(60)),
                new TextSprite(new ConstantString(msg("obfuscation")),
                               new LinearInt(1000, 10, new SmoothTiming(2000, 3000)),
                               new ConstantInt(145)),
                new TextSprite(new ConstantString(msg("preverification")),
                               new LinearInt(1000, 350, new SmoothTiming(2500, 3500)),
                               new ConstantInt(140)),
                new FontSprite(new ConstantFont(new Font("sansserif", Font.BOLD, 30)),
                new TextSprite(new TypeWriterString(msg("developed"), new LinearTiming(3500, 5500)),
                               new ConstantInt(250),
                               new ConstantInt(200))),
            })))),
        });
        splashPanel = new SplashPanel(splash, 0.5, 5500L);
        splashPanel.setPreferredSize(new Dimension(0, 200));

        JTextArea welcomeTextArea = new JTextArea(msg("proGuardInfo"), 18, 50);
        welcomeTextArea.setOpaque(false);
        welcomeTextArea.setEditable(false);
        welcomeTextArea.setLineWrap(true);
        welcomeTextArea.setWrapStyleWord(true);
        welcomeTextArea.setPreferredSize(new Dimension(0, 0));
        welcomeTextArea.setBorder(new EmptyBorder(20, 20, 20, 20));
        addBorder(welcomeTextArea, "welcome");

        JPanel proGuardPanel = new JPanel(layout);
        proGuardPanel.add(splashPanel,      splashPanelConstraints);
        proGuardPanel.add(welcomeTextArea,  welcomeTextAreaConstraints);

        // Create the input panel.
        // TODO: properly clone the ClassPath objects.
        // This is awkward to implement in the generic ListPanel.addElements(...)
        // method, since the Object.clone() method is not public.
        programPanel.addCopyToPanelButton("moveToLibraries", "moveToLibrariesTip", libraryPanel);
        libraryPanel.addCopyToPanelButton("moveToProgram",   "moveToProgramTip",   programPanel);

        // Collect all buttons of these panels and make sure they are equally
        // sized.
        List panelButtons = new ArrayList();
        panelButtons.addAll(programPanel.getButtons());
        panelButtons.addAll(libraryPanel.getButtons());
        setCommonPreferredSize(panelButtons);

        addBorder(programPanel, "programJars" );
        addBorder(libraryPanel, "libraryJars" );

        JPanel inputOutputPanel = new JPanel(layout);
        inputOutputPanel.add(tip(programPanel, "programJarsTip"), stretchPanelConstraints);
        inputOutputPanel.add(tip(libraryPanel, "libraryJarsTip"), stretchPanelConstraints);

        // Load the boiler plate options.
        loadBoilerplateConfiguration();

        // Create the boiler plate keep panels.
        boilerplateKeepCheckBoxes = new JCheckBox[boilerplateKeep.length];
        boilerplateKeepTextFields = new JTextField[boilerplateKeep.length];

        JButton printUsageBrowseButton   = createBrowseButton(printUsageTextField,
                                                              msg("selectUsageFile"));

        JPanel shrinkingOptionsPanel = new JPanel(layout);
        addBorder(shrinkingOptionsPanel, "options");

        shrinkingOptionsPanel.add(tip(shrinkCheckBox,         "shrinkTip"),       constraintsLastStretch);
        shrinkingOptionsPanel.add(tip(printUsageCheckBox,     "printUsageTip"),   constraints);
        shrinkingOptionsPanel.add(tip(printUsageTextField,    "outputFileTip"),   constraintsStretch);
        shrinkingOptionsPanel.add(tip(printUsageBrowseButton, "selectUsageFile"), constraintsLast);

        JPanel shrinkingPanel = new JPanel(layout);

        shrinkingPanel.add(shrinkingOptionsPanel, panelConstraints);
        addClassSpecifications(extractClassSpecifications(boilerplateKeep),
                               shrinkingPanel,
                               boilerplateKeepCheckBoxes,
                               boilerplateKeepTextFields);

        addBorder(additionalKeepPanel, "keepAdditional");
        shrinkingPanel.add(tip(additionalKeepPanel, "keepAdditionalTip"), stretchPanelConstraints);

        // Create the boiler plate keep names panels.
        boilerplateKeepNamesCheckBoxes = new JCheckBox[boilerplateKeepNames.length];
        boilerplateKeepNamesTextFields = new JTextField[boilerplateKeepNames.length];

        JButton printMappingBrowseButton = createBrowseButton(printMappingTextField,
                                                              msg("selectPrintMappingFile"));
        JButton applyMappingBrowseButton = createBrowseButton(applyMappingTextField,
                                                              msg("selectApplyMappingFile"));
        JButton obfucationDictionaryBrowseButton = createBrowseButton(obfuscationDictionaryTextField,
                                                                      msg("selectObfuscationDictionaryFile"));

        JPanel obfuscationOptionsPanel = new JPanel(layout);
        addBorder(obfuscationOptionsPanel, "options");

        obfuscationOptionsPanel.add(tip(obfuscateCheckBox,                  "obfuscateTip"),                    constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(printMappingCheckBox,               "printMappingTip"),                 constraints);
        obfuscationOptionsPanel.add(tip(printMappingTextField,              "outputFileTip"),                   constraintsStretch);
        obfuscationOptionsPanel.add(tip(printMappingBrowseButton,           "selectPrintMappingFile"),          constraintsLast);
        obfuscationOptionsPanel.add(tip(applyMappingCheckBox,               "applyMappingTip"),                 constraints);
        obfuscationOptionsPanel.add(tip(applyMappingTextField,              "inputFileTip"),                    constraintsStretch);
        obfuscationOptionsPanel.add(tip(applyMappingBrowseButton,           "selectApplyMappingFile"),          constraintsLast);
        obfuscationOptionsPanel.add(tip(obfuscationDictionaryCheckBox,      "obfuscationDictionaryTip"),        constraints);
        obfuscationOptionsPanel.add(tip(obfuscationDictionaryTextField,     "inputFileTip"),                    constraintsStretch);
        obfuscationOptionsPanel.add(tip(obfucationDictionaryBrowseButton,   "selectObfuscationDictionaryFile"), constraintsLast);
        obfuscationOptionsPanel.add(tip(overloadAggressivelyCheckBox,       "overloadAggressivelyTip"),         constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(useUniqueClassMemberNamesCheckBox,  "useUniqueClassMemberNamesTip"),    constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(useMixedCaseClassNamesCheckBox,     "useMixedCaseClassNamesTip"),       constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(flattenPackageHierarchyCheckBox,    "flattenPackageHierarchyTip"),      constraints);
        obfuscationOptionsPanel.add(tip(flattenPackageHierarchyTextField,   "packageTip"),                      constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(repackageClassesCheckBox,           "repackageClassesTip"),             constraints);
        obfuscationOptionsPanel.add(tip(repackageClassesTextField,          "packageTip"),                      constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(keepAttributesCheckBox,             "keepAttributesTip"),               constraints);
        obfuscationOptionsPanel.add(tip(keepAttributesTextField,            "attributesTip"),                   constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(newSourceFileAttributeCheckBox,     "renameSourceFileAttributeTip"),    constraints);
        obfuscationOptionsPanel.add(tip(newSourceFileAttributeTextField,    "sourceFileAttributeTip"),          constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(adaptResourceFileNamesCheckBox,     "adaptResourceFileNamesTip"),       constraints);
        obfuscationOptionsPanel.add(tip(adaptResourceFileNamesTextField,    "fileNameFilterTip"),               constraintsLastStretch);
        obfuscationOptionsPanel.add(tip(adaptResourceFileContentsCheckBox,  "adaptResourceFileContentsTip"),    constraints);
        obfuscationOptionsPanel.add(tip(adaptResourceFileContentsTextField, "fileNameFilterTip"),               constraintsLastStretch);

        JPanel obfuscationPanel = new JPanel(layout);

        obfuscationPanel.add(obfuscationOptionsPanel, panelConstraints);
        addClassSpecifications(extractClassSpecifications(boilerplateKeepNames),
                               obfuscationPanel,
                               boilerplateKeepNamesCheckBoxes,
                               boilerplateKeepNamesTextFields);

        addBorder(additionalKeepNamesPanel, "keepNamesAdditional");
        obfuscationPanel.add(tip(additionalKeepNamesPanel, "keepNamesAdditionalTip"), stretchPanelConstraints);

        // Create the boiler plate "no side effect methods" panels.
        boilerplateNoSideEffectMethodCheckBoxes = new JCheckBox[boilerplateNoSideEffectMethods.length];

        JPanel optimizationOptionsPanel = new JPanel(layout);
        addBorder(optimizationOptionsPanel, "options");

        optimizationOptionsPanel.add(tip(optimizeCheckBox,                "optimizeTip"),                constraintsLastStretch);
        optimizationOptionsPanel.add(tip(allowAccessModificationCheckBox, "allowAccessModificationTip"), constraintsLastStretch);
        optimizationOptionsPanel.add(tip(optimizationPassesLabel,         "optimizationPassesTip"),      constraints);
        optimizationOptionsPanel.add(tip(optimizationPassesSpinner,       "optimizationPassesTip"),      constraintsLast);

        JPanel optimizationPanel = new JPanel(layout);

        optimizationPanel.add(optimizationOptionsPanel, panelConstraints);
        addClassSpecifications(boilerplateNoSideEffectMethods,
                               optimizationPanel,
                               boilerplateNoSideEffectMethodCheckBoxes,
                               null);

        addBorder(additionalNoSideEffectsPanel, "assumeNoSideEffectsAdditional");
        optimizationPanel.add(tip(additionalNoSideEffectsPanel, "assumeNoSideEffectsAdditionalTip"), stretchPanelConstraints);

        // Create the options panel.
        JPanel preverificationOptionsPanel = new JPanel(layout);
        addBorder(preverificationOptionsPanel, "preverificationAndTargeting");

        preverificationOptionsPanel.add(tip(preverifyCheckBox,    "preverifyTip"),    constraintsLastStretch);
        preverificationOptionsPanel.add(tip(microEditionCheckBox, "microEditionTip"), constraintsLastStretch);
        preverificationOptionsPanel.add(tip(targetCheckBox,       "targetTip"),       constraints);
        preverificationOptionsPanel.add(tip(targetComboBox,       "targetTip"),       constraintsLast);

        JButton printSeedsBrowseButton =
            createBrowseButton(printSeedsTextField, msg("selectSeedsFile"));

        JButton printConfigurationBrowseButton =
            createBrowseButton(printConfigurationTextField, msg( "selectConfigurationFile"));

        JButton dumpBrowseButton =
            createBrowseButton(dumpTextField, msg("selectDumpFile"));

        // Select the most recent target by default.
        targetComboBox.setSelectedIndex(targetComboBox.getItemCount() - 1);

        JPanel consistencyPanel = new JPanel(layout);
        addBorder(consistencyPanel, "consistencyAndCorrectness");

        consistencyPanel.add(tip(verboseCheckBox,                          "verboseTip"),                          constraintsLastStretch);
        consistencyPanel.add(tip(noteCheckBox,                             "noteTip"),                             constraintsLastStretch);
        consistencyPanel.add(tip(warnCheckBox,                             "warnTip"),                             constraintsLastStretch);
        consistencyPanel.add(tip(ignoreWarningsCheckBox,                   "ignoreWarningsTip"),                   constraintsLastStretch);
        consistencyPanel.add(tip(skipNonPublicLibraryClassesCheckBox,      "skipNonPublicLibraryClassesTip"),      constraintsLastStretch);
        consistencyPanel.add(tip(skipNonPublicLibraryClassMembersCheckBox, "skipNonPublicLibraryClassMembersTip"), constraintsLastStretch);
        consistencyPanel.add(tip(forceProcessingCheckBox,                  "forceProcessingTip"),                  constraintsLastStretch);
        consistencyPanel.add(tip(printSeedsCheckBox,                       "printSeedsTip"),                       constraints);
        consistencyPanel.add(tip(printSeedsTextField,                      "outputFileTip"),                       constraintsStretch);
        consistencyPanel.add(tip(printSeedsBrowseButton,                   "selectSeedsFile"),                     constraintsLast);
        consistencyPanel.add(tip(printConfigurationCheckBox,               "printConfigurationTip"),               constraints);
        consistencyPanel.add(tip(printConfigurationTextField,              "outputFileTip"),                       constraintsStretch);
        consistencyPanel.add(tip(printConfigurationBrowseButton,           "selectConfigurationFile"),             constraintsLast);
        consistencyPanel.add(tip(dumpCheckBox,                             "dumpTip"),                             constraints);
        consistencyPanel.add(tip(dumpTextField,                            "outputFileTip"),                       constraintsStretch);
        consistencyPanel.add(tip(dumpBrowseButton,                         "selectDumpFile"),                      constraintsLast);

        // Collect all components that are followed by text fields and make
        // sure they are equally sized. That way the text fields start at the
        // same horizontal position.
        setCommonPreferredSize(Arrays.asList(new JComponent[] {
            printMappingCheckBox,
            applyMappingCheckBox,
            flattenPackageHierarchyCheckBox,
            repackageClassesCheckBox,
            newSourceFileAttributeCheckBox,
        }));

        JPanel optionsPanel = new JPanel(layout);

        optionsPanel.add(preverificationOptionsPanel, panelConstraints);
        optionsPanel.add(consistencyPanel,            panelConstraints);

        addBorder(whyAreYouKeepingPanel, "whyAreYouKeeping");
        optionsPanel.add(tip(whyAreYouKeepingPanel, "whyAreYouKeepingTip"), stretchPanelConstraints);

        // Create the process panel.
        consoleTextArea.setOpaque(false);
        consoleTextArea.setEditable(false);
        consoleTextArea.setLineWrap(false);
        consoleTextArea.setWrapStyleWord(false);
        JScrollPane consoleScrollPane = new JScrollPane(consoleTextArea);
        consoleScrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));
        addBorder(consoleScrollPane, "processingConsole");

        JPanel processPanel = new JPanel(layout);
        processPanel.add(consoleScrollPane, stretchPanelConstraints);

        // Create the load, save, and process buttons.
        JButton loadButton = new JButton(msg("loadConfiguration"));
        loadButton.addActionListener(new MyLoadConfigurationActionListener());

        JButton viewButton = new JButton(msg("viewConfiguration"));
        viewButton.addActionListener(new MyViewConfigurationActionListener());

        JButton saveButton = new JButton(msg("saveConfiguration"));
        saveButton.addActionListener(new MySaveConfigurationActionListener());

        JButton processButton = new JButton(msg("process"));
        processButton.addActionListener(new MyProcessActionListener());

        // Create the ReTrace panel.
        JPanel reTraceSettingsPanel = new JPanel(layout);
        addBorder(reTraceSettingsPanel, "reTraceSettings");

        JButton reTraceMappingBrowseButton = createBrowseButton(reTraceMappingTextField,
                                                                msg("selectApplyMappingFile"));

        JLabel reTraceMappingLabel = new JLabel(msg("mappingFile"));
        reTraceMappingLabel.setForeground(reTraceVerboseCheckBox.getForeground());

        reTraceSettingsPanel.add(tip(reTraceVerboseCheckBox,     "verboseTip"),             constraintsLastStretch);
        reTraceSettingsPanel.add(tip(reTraceMappingLabel,        "mappingFileTip"),         constraints);
        reTraceSettingsPanel.add(tip(reTraceMappingTextField,    "inputFileTip"),           constraintsStretch);
        reTraceSettingsPanel.add(tip(reTraceMappingBrowseButton, "selectApplyMappingFile"), constraintsLast);

        stackTraceTextArea.setOpaque(true);
        stackTraceTextArea.setEditable(true);
        stackTraceTextArea.setLineWrap(false);
        stackTraceTextArea.setWrapStyleWord(true);
        JScrollPane stackTraceScrollPane = new JScrollPane(stackTraceTextArea);
        addBorder(stackTraceScrollPane, "obfuscatedStackTrace");

        reTraceTextArea.setOpaque(false);
        reTraceTextArea.setEditable(false);
        reTraceTextArea.setLineWrap(true);
        reTraceTextArea.setWrapStyleWord(true);
        JScrollPane reTraceScrollPane = new JScrollPane(reTraceTextArea);
        reTraceScrollPane.setBorder(new EmptyBorder(1, 1, 1, 1));
        addBorder(reTraceScrollPane, "deobfuscatedStackTrace");

        JPanel reTracePanel = new JPanel(layout);
        reTracePanel.add(reTraceSettingsPanel,                                 panelConstraints);
        reTracePanel.add(tip(stackTraceScrollPane, "obfuscatedStackTraceTip"), panelConstraints);
        reTracePanel.add(reTraceScrollPane,                                    stretchPanelConstraints);

        // Create the load button.
        JButton loadStackTraceButton = new JButton(msg("loadStackTrace"));
        loadStackTraceButton.addActionListener(new MyLoadStackTraceActionListener());

        JButton reTraceButton = new JButton(msg("reTrace"));
        reTraceButton.addActionListener(new MyReTraceActionListener());

        // Create the main tabbed pane.
        TabbedPane tabs = new TabbedPane();
        tabs.add(msg("proGuardTab"),     proGuardPanel);
        tabs.add(msg("inputOutputTab"),  inputOutputPanel);
        tabs.add(msg("shrinkingTab"),    shrinkingPanel);
        tabs.add(msg("obfuscationTab"),  obfuscationPanel);
        tabs.add(msg("optimizationTab"), optimizationPanel);
        tabs.add(msg("informationTab"),  optionsPanel);
        tabs.add(msg("processTab"),      processPanel);
        tabs.add(msg("reTraceTab"),      reTracePanel);
        tabs.addImage(Toolkit.getDefaultToolkit().getImage(
            this.getClass().getResource(TITLE_IMAGE_FILE)));

        // Add the bottom buttons to each panel.
        proGuardPanel     .add(Box.createGlue(),                                      glueConstraints);
        proGuardPanel     .add(tip(loadButton,             "loadConfigurationTip"),   bottomButtonConstraints);
        proGuardPanel     .add(createNextButton(tabs),                                lastBottomButtonConstraints);

        inputOutputPanel  .add(Box.createGlue(),           glueConstraints);
        inputOutputPanel  .add(createPreviousButton(tabs), bottomButtonConstraints);
        inputOutputPanel  .add(createNextButton(tabs),     lastBottomButtonConstraints);

        shrinkingPanel    .add(Box.createGlue(),           glueConstraints);
        shrinkingPanel    .add(createPreviousButton(tabs), bottomButtonConstraints);
        shrinkingPanel    .add(createNextButton(tabs),     lastBottomButtonConstraints);

        obfuscationPanel  .add(Box.createGlue(),           glueConstraints);
        obfuscationPanel  .add(createPreviousButton(tabs), bottomButtonConstraints);
        obfuscationPanel  .add(createNextButton(tabs),     lastBottomButtonConstraints);

        optimizationPanel .add(Box.createGlue(),           glueConstraints);
        optimizationPanel .add(createPreviousButton(tabs), bottomButtonConstraints);
        optimizationPanel .add(createNextButton(tabs),     lastBottomButtonConstraints);

        optionsPanel      .add(Box.createGlue(),           glueConstraints);
        optionsPanel      .add(createPreviousButton(tabs), bottomButtonConstraints);
        optionsPanel      .add(createNextButton(tabs),     lastBottomButtonConstraints);

        processPanel      .add(Box.createGlue(),                                      glueConstraints);
        processPanel      .add(createPreviousButton(tabs),                            bottomButtonConstraints);
        processPanel      .add(tip(viewButton,             "viewConfigurationTip"),   bottomButtonConstraints);
        processPanel      .add(tip(saveButton,             "saveConfigurationTip"),   bottomButtonConstraints);
        processPanel      .add(tip(processButton,          "processTip"),             lastBottomButtonConstraints);

        reTracePanel      .add(Box.createGlue(),                                      glueConstraints);
        reTracePanel      .add(tip(loadStackTraceButton,   "loadStackTraceTip"),      bottomButtonConstraints);
        reTracePanel      .add(tip(reTraceButton,          "reTraceTip"),             lastBottomButtonConstraints);

        // Initialize the GUI settings to reasonable defaults.
        loadConfiguration(this.getClass().getResource(DEFAULT_CONFIGURATION));

        // Add the main tabs to the frame and pack it.
        getContentPane().add(tabs);
    }


    public void startSplash()
    {
        splashPanel.start();
    }


    public void skipSplash()
    {
        splashPanel.stop();
    }


    /**
     * Loads the boilerplate keep class options from the boilerplate file
     * into the boilerplate array.
     */
    private void loadBoilerplateConfiguration()
    {
        try
        {
            // Parse the boilerplate configuration file.
            ConfigurationParser parser = new ConfigurationParser(
                this.getClass().getResource(BOILERPLATE_CONFIGURATION));
            Configuration configuration = new Configuration();

            try
            {
                parser.parse(configuration);

                // We're interested in the keep options.
                boilerplateKeep =
                    extractKeepSpecifications(configuration.keep, false, false);

                // We're interested in the keep options.
                boilerplateKeepNames =
                    extractKeepSpecifications(configuration.keep, true, false);

                // We're interested in the side effects options.
                boilerplateNoSideEffectMethods = new ClassSpecification[configuration.assumeNoSideEffects.size()];
                configuration.assumeNoSideEffects.toArray(boilerplateNoSideEffectMethods);
            }
            finally
            {
                parser.close();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    /**
     * Returns an array containing the ClassSpecifications instances with
     * matching flags.
     */
    private KeepSpecification[] extractKeepSpecifications(List    keepSpecifications,
                                                          boolean allowShrinking,
                                                          boolean allowObfuscation)
    {
        List matches = new ArrayList();

        for (int index = 0; index < keepSpecifications.size(); index++)
        {
            KeepSpecification keepSpecification = (KeepSpecification)keepSpecifications.get(index);
            if (keepSpecification.allowShrinking   == allowShrinking &&
                keepSpecification.allowObfuscation == allowObfuscation)
            {
                 matches.add(keepSpecification);
            }
        }

        KeepSpecification[] matchingKeepSpecifications = new KeepSpecification[matches.size()];
        matches.toArray(matchingKeepSpecifications);

        return matchingKeepSpecifications;
    }


    /**
     * Returns an array containing the ClassSpecification instances of the
     * given array of KeepSpecification instances.
     */
    private ClassSpecification[] extractClassSpecifications(KeepSpecification[] keepSpecifications)
    {
        ClassSpecification[] classSpecifications = new ClassSpecification[keepSpecifications.length];

        for (int index = 0; index < classSpecifications.length; index++)
        {
            classSpecifications[index] = keepSpecifications[index];
        }

        return classSpecifications;
    }


    /**
     * Creates a panel with the given boiler plate class specifications.
     */
    private void addClassSpecifications(ClassSpecification[] boilerplateClassSpecifications,
                                        JPanel               classSpecificationsPanel,
                                        JCheckBox[]          boilerplateCheckBoxes,
                                        JTextField[]         boilerplateTextFields)
    {
        // Create some constraints that can be reused.
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 4, 0, 4);

        GridBagConstraints constraintsLastStretch = new GridBagConstraints();
        constraintsLastStretch.gridwidth = GridBagConstraints.REMAINDER;
        constraintsLastStretch.fill      = GridBagConstraints.HORIZONTAL;
        constraintsLastStretch.weightx   = 1.0;
        constraintsLastStretch.anchor    = GridBagConstraints.WEST;
        constraintsLastStretch.insets    = constraints.insets;

        GridBagConstraints panelConstraints = new GridBagConstraints();
        panelConstraints.gridwidth = GridBagConstraints.REMAINDER;
        panelConstraints.fill      = GridBagConstraints.HORIZONTAL;
        panelConstraints.weightx   = 1.0;
        panelConstraints.anchor    = GridBagConstraints.NORTHWEST;
        panelConstraints.insets    = constraints.insets;

        GridBagLayout layout = new GridBagLayout();

        String lastPanelName = null;
        JPanel keepSubpanel  = null;
        for (int index = 0; index < boilerplateClassSpecifications.length; index++)
        {
            // The panel structure is derived from the comments.
            String comments    = boilerplateClassSpecifications[index].comments;
            int    dashIndex   = comments.indexOf('-');
            int    periodIndex = comments.indexOf('.', dashIndex);
            String panelName   = comments.substring(0, dashIndex).trim();
            String optionName  = comments.substring(dashIndex + 1, periodIndex).replace('_', '.').trim();
            String toolTip     = comments.substring(periodIndex + 1);
            if (keepSubpanel == null || !panelName.equals(lastPanelName))
            {
                // Create a new keep subpanel and add it.
                keepSubpanel = new JPanel(layout);
                keepSubpanel.setBorder(BorderFactory.createTitledBorder(BORDER, panelName));
                classSpecificationsPanel.add(keepSubpanel, panelConstraints);

                lastPanelName = panelName;
            }

            // Add the check box to the subpanel.
            JCheckBox boilerplateCheckBox = new JCheckBox(optionName);
            boilerplateCheckBox.setToolTipText(toolTip);
            boilerplateCheckBoxes[index] = boilerplateCheckBox;
            keepSubpanel.add(boilerplateCheckBox,
                             boilerplateTextFields != null ?
                                 constraints :
                                 constraintsLastStretch);

            if (boilerplateTextFields != null)
            {
                // Add the text field to the subpanel.
                boilerplateTextFields[index] = new JTextField(40);
                keepSubpanel.add(tip(boilerplateTextFields[index], "classNamesTip"), constraintsLastStretch);
            }
        }
    }


    /**
     * Adds a standard border with the title that corresponds to the given key
     * in the GUI resources.
     */
    private void addBorder(JComponent component, String titleKey)
    {
        Border oldBorder = component.getBorder();
        Border newBorder = BorderFactory.createTitledBorder(BORDER, msg(titleKey));

        component.setBorder(oldBorder == null ?
            newBorder :
            new CompoundBorder(newBorder, oldBorder));
    }


    /**
     * Creates a Previous button for the given tabbed pane.
     */
    private JButton createPreviousButton(final TabbedPane tabbedPane)
    {
        JButton browseButton = new JButton(msg("previous"));
        browseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                tabbedPane.previous();
            }
        });

        return browseButton;
    }


    /**
     * Creates a Next button for the given tabbed pane.
     */
    private JButton createNextButton(final TabbedPane tabbedPane)
    {
        JButton browseButton = new JButton(msg("next"));
        browseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                tabbedPane.next();
            }
        });

        return browseButton;
    }


    /**
     * Creates a browse button that opens a file browser for the given text field.
     */
    private JButton createBrowseButton(final JTextField textField,
                                       final String     title)
    {
        JButton browseButton = new JButton(msg("browse"));
        browseButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                fileChooser.setDialogTitle(title);
                fileChooser.setSelectedFile(new File(textField.getText()));

                int returnVal = fileChooser.showDialog(ProGuardGUI.this, msg("ok"));
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    textField.setText(fileChooser.getSelectedFile().getPath());
                }
            }
        });

        return browseButton;
    }


    /**
     * Sets the preferred sizes of the given components to the maximum of their
     * current preferred sizes.
     */
    private void setCommonPreferredSize(List components)
    {
        // Find the maximum preferred size.
        Dimension maximumSize = null;
        for (int index = 0; index < components.size(); index++)
        {
            JComponent component = (JComponent)components.get(index);
            Dimension  size      = component.getPreferredSize();
            if (maximumSize == null ||
                size.getWidth() > maximumSize.getWidth())
            {
                maximumSize = size;
            }
        }

        // Set the size that we found as the preferred size for all components.
        for (int index = 0; index < components.size(); index++)
        {
            JComponent component = (JComponent)components.get(index);
            component.setPreferredSize(maximumSize);
        }
    }


    /**
     * Updates to GUI settings to reflect the given ProGuard configuration.
     */
    private void setProGuardConfiguration(Configuration configuration)
    {
        // Set up the input and output jars and directories.
        programPanel.setClassPath(configuration.programJars);
        libraryPanel.setClassPath(configuration.libraryJars);

        // Set up the boilerplate keep options.
        for (int index = 0; index < boilerplateKeep.length; index++)
        {
            String classNames =
                findMatchingKeepSpecifications(boilerplateKeep[index],
                                               configuration.keep);

            boilerplateKeepCheckBoxes[index].setSelected(classNames != null);
            boilerplateKeepTextFields[index].setText(classNames == null ? "*" : classNames);
        }


        // Set up the boilerplate keep names options.
        for (int index = 0; index < boilerplateKeepNames.length; index++)
        {
            String classNames =
                findMatchingKeepSpecifications(boilerplateKeepNames[index],
                                               configuration.keep);

            boilerplateKeepNamesCheckBoxes[index].setSelected(classNames != null);
            boilerplateKeepNamesTextFields[index].setText(classNames == null ? "*" : classNames);
        }

        // Set up the additional keep options. Note that the matched boilerplate
        // options have been removed from the list.
        additionalKeepPanel.setClassSpecifications(filteredKeepSpecifications(configuration.keep,
                                                                              false));

        // Set up the additional keep options. Note that the matched boilerplate
        // options have been removed from the list.
        additionalKeepNamesPanel.setClassSpecifications(filteredKeepSpecifications(configuration.keep,
                                                                                   true));


        // Set up the boilerplate "no side effect methods" options.
        for (int index = 0; index < boilerplateNoSideEffectMethods.length; index++)
        {
            boolean found =
                findClassSpecification(boilerplateNoSideEffectMethods[index],
                                       configuration.assumeNoSideEffects);

            boilerplateNoSideEffectMethodCheckBoxes[index].setSelected(found);
        }

        // Set up the additional keep options. Note that the matched boilerplate
        // options have been removed from the list.
        additionalNoSideEffectsPanel.setClassSpecifications(configuration.assumeNoSideEffects);

        // Set up the "why are you keeping" options.
        whyAreYouKeepingPanel.setClassSpecifications(configuration.whyAreYouKeeping);

        // Set up the other options.
        shrinkCheckBox                          .setSelected(configuration.shrink);
        printUsageCheckBox                      .setSelected(configuration.printUsage              != null);

        optimizeCheckBox                        .setSelected(configuration.optimize);
        optimizationPassesSpinner.getModel()    .setValue(new Integer(configuration.optimizationPasses));
        allowAccessModificationCheckBox         .setSelected(configuration.allowAccessModification);

        obfuscateCheckBox                       .setSelected(configuration.obfuscate);
        printMappingCheckBox                    .setSelected(configuration.printMapping              != null);
        applyMappingCheckBox                    .setSelected(configuration.applyMapping              != null);
        obfuscationDictionaryCheckBox           .setSelected(configuration.obfuscationDictionary     != null);
        overloadAggressivelyCheckBox            .setSelected(configuration.overloadAggressively);
        useUniqueClassMemberNamesCheckBox       .setSelected(configuration.useUniqueClassMemberNames);
        useMixedCaseClassNamesCheckBox          .setSelected(configuration.useMixedCaseClassNames);
        flattenPackageHierarchyCheckBox         .setSelected(configuration.flattenPackageHierarchy   != null);
        repackageClassesCheckBox                .setSelected(configuration.repackageClasses          != null);
        keepAttributesCheckBox                  .setSelected(configuration.keepAttributes            != null);
        newSourceFileAttributeCheckBox          .setSelected(configuration.newSourceFileAttribute    != null);
        adaptResourceFileNamesCheckBox          .setSelected(configuration.adaptResourceFileNames    != null);
        adaptResourceFileContentsCheckBox       .setSelected(configuration.adaptResourceFileContents != null);

        preverifyCheckBox                       .setSelected(configuration.preverify);
        microEditionCheckBox                    .setSelected(configuration.microEdition);
        targetCheckBox                          .setSelected(configuration.targetClassVersion != 0);

        verboseCheckBox                         .setSelected(configuration.verbose);
        noteCheckBox                            .setSelected(configuration.note);
        warnCheckBox                            .setSelected(configuration.warn);
        ignoreWarningsCheckBox                  .setSelected(configuration.ignoreWarnings);
        skipNonPublicLibraryClassesCheckBox     .setSelected(configuration.skipNonPublicLibraryClasses);
        skipNonPublicLibraryClassMembersCheckBox.setSelected(configuration.skipNonPublicLibraryClassMembers);
        forceProcessingCheckBox                 .setSelected(configuration.lastModified == Long.MAX_VALUE);
        printSeedsCheckBox                      .setSelected(configuration.printSeeds              != null);
        printConfigurationCheckBox              .setSelected(configuration.printConfiguration      != null);
        dumpCheckBox                            .setSelected(configuration.dump                    != null);

        printUsageTextField                     .setText(fileName(configuration.printUsage));
        printMappingTextField                   .setText(fileName(configuration.printMapping));
        applyMappingTextField                   .setText(fileName(configuration.applyMapping));
        obfuscationDictionaryTextField          .setText(fileName(configuration.obfuscationDictionary));
        flattenPackageHierarchyTextField        .setText(configuration.flattenPackageHierarchy);
        repackageClassesTextField               .setText(configuration.repackageClasses);
        keepAttributesTextField                 .setText(configuration.keepAttributes            == null ? KEEP_ATTRIBUTE_DEFAULT               : ListUtil.commaSeparatedString(configuration.keepAttributes));
        newSourceFileAttributeTextField         .setText(configuration.newSourceFileAttribute    == null ? SOURCE_FILE_ATTRIBUTE_DEFAULT        : configuration.newSourceFileAttribute);
        adaptResourceFileNamesTextField         .setText(configuration.adaptResourceFileNames    == null ? ADAPT_RESOURCE_FILE_NAMES_DEFAULT    : ListUtil.commaSeparatedString(configuration.adaptResourceFileNames));
        adaptResourceFileContentsTextField      .setText(configuration.adaptResourceFileContents == null ? ADAPT_RESOURCE_FILE_CONTENTS_DEFAULT : ListUtil.commaSeparatedString(configuration.adaptResourceFileContents));
        printSeedsTextField                     .setText(fileName(configuration.printSeeds));
        printConfigurationTextField             .setText(fileName(configuration.printConfiguration));
        dumpTextField                           .setText(fileName(configuration.dump));

        if (configuration.targetClassVersion != 0)
        {
            targetComboBox.setSelectedItem(ClassUtil.externalClassVersion(configuration.targetClassVersion));
        }
        else
        {
            targetComboBox.setSelectedIndex(targetComboBox.getItemCount() - 1);
        }

        if (configuration.printMapping != null)
        {
            reTraceMappingTextField.setText(fileName(configuration.printMapping));
        }
    }


    /**
     * Returns the ProGuard configuration that reflects the current GUI settings.
     */
    private Configuration getProGuardConfiguration()
    {
        Configuration configuration = new Configuration();

        // Get the input and output jars and directories.
        configuration.programJars = programPanel.getClassPath();
        configuration.libraryJars = libraryPanel.getClassPath();

        List keep = new ArrayList();

        // Collect the additional keep options.
        List additionalKeep = additionalKeepPanel.getClassSpecifications();
        if (additionalKeep != null)
        {
            keep.addAll(additionalKeep);
        }

        // Collect the additional keep names options.
        List additionalKeepNames = additionalKeepNamesPanel.getClassSpecifications();
        if (additionalKeepNames != null)
        {
            keep.addAll(additionalKeepNames);
        }

        // Collect the boilerplate keep options.
        for (int index = 0; index < boilerplateKeep.length; index++)
        {
            if (boilerplateKeepCheckBoxes[index].isSelected())
            {
                keep.add(classSpecification(boilerplateKeep[index],
                                            boilerplateKeepTextFields[index].getText()));
            }
        }

        // Collect the boilerplate keep names options.
        for (int index = 0; index < boilerplateKeepNames.length; index++)
        {
            if (boilerplateKeepNamesCheckBoxes[index].isSelected())
            {
                keep.add(classSpecification(boilerplateKeepNames[index],
                                            boilerplateKeepNamesTextFields[index].getText()));
            }
        }

        // Put the list of keep specifications in the configuration.
        if (keep.size() > 0)
        {
            configuration.keep = keep;
        }


        // Collect the boilerplate "no side effect methods" options.
        List noSideEffectMethods = new ArrayList();

        for (int index = 0; index < boilerplateNoSideEffectMethods.length; index++)
        {
            if (boilerplateNoSideEffectMethodCheckBoxes[index].isSelected())
            {
                noSideEffectMethods.add(boilerplateNoSideEffectMethods[index]);
            }
        }

        // Collect the additional "no side effect methods" options.
        List additionalNoSideEffectOptions = additionalNoSideEffectsPanel.getClassSpecifications();
        if (additionalNoSideEffectOptions != null)
        {
            noSideEffectMethods.addAll(additionalNoSideEffectOptions);
        }

        // Put the list of "no side effect methods" options in the configuration.
        if (noSideEffectMethods.size() > 0)
        {
            configuration.assumeNoSideEffects = noSideEffectMethods;
        }


        // Collect the "why are you keeping" options.
        configuration.whyAreYouKeeping = whyAreYouKeepingPanel.getClassSpecifications();


        // Get the other options.
        configuration.shrink                           = shrinkCheckBox                          .isSelected();
        configuration.printUsage                       = printUsageCheckBox                      .isSelected() ? new File(printUsageTextField                       .getText()) : null;

        configuration.optimize                         = optimizeCheckBox                        .isSelected();
        configuration.optimizationPasses               = ((SpinnerNumberModel)optimizationPassesSpinner.getModel()).getNumber().intValue();
        configuration.allowAccessModification          = allowAccessModificationCheckBox         .isSelected();

        configuration.obfuscate                        = obfuscateCheckBox                       .isSelected();
        configuration.printMapping                     = printMappingCheckBox                    .isSelected() ? new File(printMappingTextField                                .getText()) : null;
        configuration.applyMapping                     = applyMappingCheckBox                    .isSelected() ? new File(applyMappingTextField                                .getText()) : null;
        configuration.obfuscationDictionary            = obfuscationDictionaryCheckBox           .isSelected() ? new File(obfuscationDictionaryTextField                       .getText()) : null;
        configuration.overloadAggressively             = overloadAggressivelyCheckBox            .isSelected();
        configuration.useUniqueClassMemberNames        = useUniqueClassMemberNamesCheckBox       .isSelected();
        configuration.useMixedCaseClassNames           = useMixedCaseClassNamesCheckBox          .isSelected();
        configuration.flattenPackageHierarchy          = flattenPackageHierarchyCheckBox         .isSelected() ? ClassUtil.externalClassName(flattenPackageHierarchyTextField  .getText()) : null;
        configuration.repackageClasses                 = repackageClassesCheckBox                .isSelected() ? ClassUtil.externalClassName(repackageClassesTextField         .getText()) : null;
        configuration.keepAttributes                   = keepAttributesCheckBox                  .isSelected() ? ListUtil.commaSeparatedList(keepAttributesTextField           .getText()) : null;
        configuration.newSourceFileAttribute           = newSourceFileAttributeCheckBox          .isSelected() ? newSourceFileAttributeTextField                               .getText()  : null;
        configuration.adaptResourceFileNames           = adaptResourceFileNamesCheckBox          .isSelected() ? ListUtil.commaSeparatedList(adaptResourceFileNamesTextField   .getText()) : null;
        configuration.adaptResourceFileContents        = adaptResourceFileContentsCheckBox       .isSelected() ? ListUtil.commaSeparatedList(adaptResourceFileContentsTextField.getText()) : null;

        configuration.preverify                        = preverifyCheckBox                       .isSelected();
        configuration.microEdition                     = microEditionCheckBox                    .isSelected();
        configuration.targetClassVersion               = targetCheckBox                          .isSelected() ? ClassUtil.internalClassVersion(targetComboBox.getSelectedItem().toString()) : 0;

        configuration.verbose                          = verboseCheckBox                         .isSelected();
        configuration.note                             = noteCheckBox                            .isSelected();
        configuration.warn                             = warnCheckBox                            .isSelected();
        configuration.ignoreWarnings                   = ignoreWarningsCheckBox                  .isSelected();
        configuration.skipNonPublicLibraryClasses      = skipNonPublicLibraryClassesCheckBox     .isSelected();
        configuration.skipNonPublicLibraryClassMembers = skipNonPublicLibraryClassMembersCheckBox.isSelected();
        configuration.lastModified                     = forceProcessingCheckBox                 .isSelected() ? Long.MAX_VALUE : System.currentTimeMillis();
        configuration.printSeeds                       = printSeedsCheckBox                      .isSelected() ? new File(printSeedsTextField                                .getText()) : null;
        configuration.printConfiguration               = printConfigurationCheckBox              .isSelected() ? new File(printConfigurationTextField                        .getText()) : null;
        configuration.dump                             = dumpCheckBox                            .isSelected() ? new File(dumpTextField                                      .getText()) : null;

        return configuration;
    }


    /**
     * Looks in the given list for a class specification that is identical to
     * the given template. Returns true if it is found, and removes the matching
     * class specification as a side effect.
     */
    private boolean findClassSpecification(ClassSpecification classSpecificationTemplate,
                                           List                classSpecifications)
    {
        if (classSpecifications == null)
        {
            return false;
        }

        for (int index = 0; index < classSpecifications.size(); index++)
        {
            if (classSpecificationTemplate.equals(classSpecifications.get(index)))
            {
                // Remove the matching option as a side effect.
                classSpecifications.remove(index);

                return true;
            }
        }

        return false;
    }


    /**
     * Returns the subset of the given list of keep specifications, with
     * matching shrinking flag.
     */
    private List filteredKeepSpecifications(List    keepSpecifications,
                                            boolean allowShrinking)
    {
        List filteredKeepSpecifications = new ArrayList();

        for (int index = 0; index < keepSpecifications.size(); index++)
        {
            KeepSpecification keepSpecification =
                (KeepSpecification)keepSpecifications.get(index);

            if (keepSpecification.allowShrinking == allowShrinking)
            {
                filteredKeepSpecifications.add(keepSpecification);
            }
        }

        return filteredKeepSpecifications;
    }


    /**
     * Looks in the given list for keep specifications that match the given
     * template. Returns a comma-separated string of class names from
     * matching keep specifications, and removes the matching keep
     * specifications as a side effect.
     */
    private String findMatchingKeepSpecifications(KeepSpecification keepSpecificationTemplate,
                                                  List              keepSpecifications)
    {
        if (keepSpecifications == null)
        {
            return null;
        }

        StringBuffer buffer = null;

        for (int index = 0; index < keepSpecifications.size(); index++)
        {
            KeepSpecification listedKeepSpecification =
                (KeepSpecification)keepSpecifications.get(index);
            String className = listedKeepSpecification.className;
            keepSpecificationTemplate.className = className;
            if (keepSpecificationTemplate.equals(listedKeepSpecification))
            {
                if (buffer == null)
                {
                    buffer = new StringBuffer();
                }
                else
                {
                    buffer.append(',');
                }
                buffer.append(className == null ? "*" : ClassUtil.externalClassName(className));

                // Remove the matching option as a side effect.
                keepSpecifications.remove(index--);
            }
        }

        return buffer == null ? null : buffer.toString();
    }


    /**
     * Returns a class specification or keep specification, based on the given
     * template and the class name to be filled in.
     */
    private ClassSpecification classSpecification(ClassSpecification classSpecificationTemplate,
                                                  String             className)
    {
        // Create a copy of the template.
        ClassSpecification classSpecification =
            (ClassSpecification)classSpecificationTemplate.clone();

        // Set the class name in the copy.
        classSpecification.className =
            className.equals("") ||
            className.equals("*") ?
                null :
                ClassUtil.internalClassName(className);

        // Return the modified copy.
        return classSpecification;
    }


    // Methods and internal classes related to actions.

    /**
     * Loads the given ProGuard configuration into the GUI.
     */
    private void loadConfiguration(File file)
    {
        // Set the default directory and file in the file choosers.
        configurationChooser.setSelectedFile(file.getAbsoluteFile());
        fileChooser.setCurrentDirectory(file.getAbsoluteFile().getParentFile());

        try
        {
            // Parse the configuration file.
            ConfigurationParser parser = new ConfigurationParser(file);
            Configuration configuration = new Configuration();

            try
            {
                parser.parse(configuration);

                // Let the GUI reflect the configuration.
                setProGuardConfiguration(configuration);
            }
            catch (ParseException ex)
            {
                JOptionPane.showMessageDialog(getContentPane(),
                                              msg("cantParseConfigurationFile", file.getPath()),
                                              msg("warning"),
                                              JOptionPane.ERROR_MESSAGE);
            }
            finally
            {
                parser.close();
            }
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(getContentPane(),
                                          msg("cantOpenConfigurationFile", file.getPath()),
                                          msg("warning"),
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Loads the given ProGuard configuration into the GUI.
     */
    private void loadConfiguration(URL url)
    {
        try
        {
            // Parse the configuration file.
            ConfigurationParser parser = new ConfigurationParser(url);
            Configuration configuration = new Configuration();

            try
            {
                parser.parse(configuration);

                // Let the GUI reflect the configuration.
                setProGuardConfiguration(configuration);
            }
            catch (ParseException ex)
            {
                JOptionPane.showMessageDialog(getContentPane(),
                                              msg("cantParseConfigurationFile", url),
                                              msg("warning"),
                                              JOptionPane.ERROR_MESSAGE);
            }
            finally
            {
                parser.close();
            }
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(getContentPane(),
                                          msg("cantOpenConfigurationFile", url),
                                          msg("warning"),
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Saves the current ProGuard configuration to the given file.
     */
    private void saveConfiguration(File file)
    {
        try
        {
            // Save the configuration file.
            ConfigurationWriter writer = new ConfigurationWriter(file);
            writer.write(getProGuardConfiguration());
            writer.close();
        }
        catch (Exception ex)
        {
            JOptionPane.showMessageDialog(getContentPane(),
                                          msg("cantSaveConfigurationFile", file.getPath()),
                                          msg("warning"),
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * Loads the given stack trace into the GUI.
     */
    private void loadStackTrace(String fileName)
    {
        try
        {
            StringBuffer buffer = new StringBuffer(1024);

            Reader reader = new BufferedReader(new FileReader(fileName));
            try
            {
                while (true)
                {
                    int c = reader.read();
                    if (c < 0)
                    {
                        break;
                    }

                    buffer.append(c);
                }
            }
            finally
            {
                reader.close();
            }

            // Put the stack trace in the text area.
            stackTraceTextArea.setText(buffer.toString());
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(getContentPane(),
                                          msg("cantOpenStackTraceFile", fileName),
                                          msg("warning"),
                                          JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * This ActionListener loads a ProGuard configuration file and initializes
     * the GUI accordingly.
     */
    private class MyLoadConfigurationActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            configurationChooser.setDialogTitle(msg("selectConfigurationFile"));

            int returnValue = configurationChooser.showOpenDialog(ProGuardGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                loadConfiguration(configurationChooser.getSelectedFile());
            }
        }
    }


    /**
     * This ActionListener saves a ProGuard configuration file based on the
     * current GUI settings.
     */
    private class MySaveConfigurationActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            configurationChooser.setDialogTitle(msg("saveConfigurationFile"));

            int returnVal = configurationChooser.showSaveDialog(ProGuardGUI.this);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                saveConfiguration(configurationChooser.getSelectedFile());
            }
        }
    }


    /**
     * This ActionListener displays the ProGuard configuration specified by the
     * current GUI settings.
     */
    private class MyViewConfigurationActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // Make sure System.out has not been redirected yet.
            if (!systemOutRedirected)
            {
                consoleTextArea.setText("");

                TextAreaOutputStream outputStream =
                    new TextAreaOutputStream(consoleTextArea);

                try
                {
                    // TODO: write out relative path names and path names with system properties.

                    // Write the configuration.
                    ConfigurationWriter writer = new ConfigurationWriter(outputStream);
                    try
                    {
                        writer.write(getProGuardConfiguration());
                    }
                    finally
                    {
                        writer.close();
                    }
                }
                catch (IOException ex)
                {
                    // This shouldn't happen.
                }

                // Scroll to the top of the configuration.
                consoleTextArea.setCaretPosition(0);
            }
        }
    }


    /**
     * This ActionListener executes ProGuard based on the current GUI settings.
     */
    private class MyProcessActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // Make sure System.out has not been redirected yet.
            if (!systemOutRedirected)
            {
                systemOutRedirected = true;

                // Get the informational configuration file name.
                File configurationFile = configurationChooser.getSelectedFile();
                String configurationFileName = configurationFile != null ?
                    configurationFile.getName() :
                    msg("sampleConfigurationFileName");

                // Create the ProGuard thread.
                Thread proGuardThread =
                    new Thread(new ProGuardRunnable(consoleTextArea,
                                                    getProGuardConfiguration(),
                                                    configurationFileName));

                // Run it.
                proGuardThread.start();
            }
        }
    }


    /**
     * This ActionListener loads an obfuscated stack trace from a file and puts
     * it in the proper text area.
     */
    private class MyLoadStackTraceActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            fileChooser.setDialogTitle(msg("selectStackTraceFile"));
            fileChooser.setSelectedFile(null);

            int returnValue = fileChooser.showOpenDialog(ProGuardGUI.this);
            if (returnValue == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                String fileName = selectedFile.getPath();

                loadStackTrace(fileName);
            }
        }
    }


    /**
     * This ActionListener executes ReTrace based on the current GUI settings.
     */
    private class MyReTraceActionListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            // Make sure System.out has not been redirected yet.
            if (!systemOutRedirected)
            {
                systemOutRedirected = true;

                boolean verbose            = reTraceVerboseCheckBox.isSelected();
                File    retraceMappingFile = new File(reTraceMappingTextField.getText());
                String  stackTrace         = stackTraceTextArea.getText();

                // Create the ReTrace runnable.
                Runnable reTraceRunnable = new ReTraceRunnable(reTraceTextArea,
                                                               verbose,
                                                               retraceMappingFile,
                                                               stackTrace);

                // Run it in this thread, because it won't take long anyway.
                reTraceRunnable.run();
            }
        }
    }


    // Small utility methods.

    /**
     * Returns the file name of the given file, if any.
     */
    private static String fileName(File file)
    {
        return file == null ? "" : file.getAbsolutePath();
    }


    /**
     * Attaches the tool tip from the GUI resources that corresponds to the
     * given key, to the given component.
     */
    private static JComponent tip(JComponent component, String messageKey)
    {
        component.setToolTipText(msg(messageKey));

        return component;
    }


    /**
     * Returns the message from the GUI resources that corresponds to the given
     * key.
     */
    private static String msg(String messageKey)
    {
         return GUIResources.getMessage(messageKey);
    }


    /**
     * Returns the message from the GUI resources that corresponds to the given
     * key and argument.
     */
    private String msg(String messageKey,
                       Object messageArgument)
    {
         return GUIResources.getMessage(messageKey, new Object[] {messageArgument});
    }


    /**
     * The main method for the ProGuard GUI.
     */
    public static void main(final String[] args)
    {
//        SwingUtil.invokeAndWait(new Runnable()
//        {
//            public void run()
            {
                ProGuardGUI gui = new ProGuardGUI();
                gui.pack();

                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                Dimension guiSize    = gui.getSize();
                gui.setLocation((screenSize.width - guiSize.width)   / 2,
                                (screenSize.height - guiSize.height) / 2);
                gui.show();

                // Start the splash animation, unless specified otherwise.
                int argIndex = 0;
                if (argIndex < args.length &&
                    NO_SPLASH_OPTION.startsWith(args[argIndex]))
                {
                    gui.skipSplash();
                    argIndex++;
                }
                else
                {
                    gui.startSplash();
                }

                // Load an initial configuration, if specified.
                if (argIndex < args.length)
                {
                    gui.loadConfiguration(new File(args[argIndex]));
                    argIndex++;
                }

                if (argIndex < args.length)
                {
                    System.out.println(gui.getClass().getName() + ": ignoring extra arguments [" + args[argIndex] + "...]");
                }

            }
//        });
    }
}
