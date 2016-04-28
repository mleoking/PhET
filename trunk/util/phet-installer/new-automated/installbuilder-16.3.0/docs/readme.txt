
==========================================================================
                          RELEASE NOTES FOR
                Bitrock InstallBuilder Multiplatform
==========================================================================

  This Release Notes document is for Bitrock InstallBuilder Multiplatform. This
  Document contains late-breaking information about the product. Please 
  refer to the user guide for more information.

  * World-wide web                http://www.bitrock.com/

  * Technical Support             support@bitrock.com

  * Sales                         sales@bitrock.com

  Should you have any question about BitRock Installer, please don't
  hesitate to contact us at: info@bitrock.com
  
==========================================================================
                                 Overview
==========================================================================

  BitRock InstallBuilder allows you to create easy to use Linux and Windows
  installers that can be run in GUI, text and unattended modes. Having
  the right installer for your product can help you :

  * Sell your software: A complicated, error-prone installation process
  can hurt your sales. BitRock installers "just work" providing a great
  end-user experience that helps sell your programs. 

  * Reduce development time: BitRock InstallBuilder simplifies the
  process of creating installation packages so you can spend time
  developing your product and not building installers.

  * Lower support costs: BitRock installers simplify and automate many
  aspects of the installation process, reducing support costs and
  end-user frustration

  Check our web page: http://www.bitrock.com or subscribe to our
  newsletter if you want to be updated with the lattest news of our
  products.

   
==========================================================================
                        Installation Instructions
==========================================================================

  Please consult the BitRock InstallBuilder user and reference guides for
  step-to-step instructions on installation and usage. The guides are
  included as part of the installation in the docs/ directory.

==========================================================================
                            Technical Support
==========================================================================

  Upon purchase and registration of a Bitrock InstallBuilder software
  license you are entitled to maintenance and support services.
  Bitrock Technical Support is available by e-mail and from our website.

  For technical support for BitRock InstallerBuilder, go to:
  http://bitrock.com/support_installbuilder_overview.html


==========================================================================
                               ChangeLog
==========================================================================

Version 16.3.0 (2016-03-31)
* New <queryWMI> action to allow querying WMI from InstallBuilder
* New <osxApplicationBundleName> and <osxApplicationBundleVersion> properties for Java launchers
* Improved <infoParameter> look and feel when used inside a <parameterGroup> in Qt mode
* Improved performance of uninstallation and unpacking process in upgrade mode
* Options files are now also picked up from the same directory containing the .app bundle on OS X
* Fixed icons not working properly for CLI-based Java launcher on Windows
* Fixed <launchBrowser> not properly working on some Windows environments
* Fixed builder recent files not correctly saved with paths including non-ascii symbols

Version 15.10.0 (2015-10-15)
* Fixed HTTPS downloads sometimes not working correctly when proxy is enabled
* Fixed builder proxy preferences dialog not always properly saving its data
* Fixed issues with OS X signing in some Linux environments
* Fixed ${required_diskspace} in CD-ROM mode
* Improved license page look and feel on OS X 10.11 when running in Qt mode
* Improved <autodetectJava> action to use the full detected version when ordering its results
* Fixed installers throwing an unknown error when trying to enable/disable components inside a component group from command line
* Fixed component selection page losing focus when enabling components

Version 15.1.0 (2015-04-15)
* Allow signing Windows and OS X installers on Windows, Linux and OS X
* Support DMG with custom backgrounds creation on Windows, Linux and OS X
* Improved LZMA/LZMA-ultra compression and decompression speed
* New compression algorithms LZHAM and LZHAM-ultra
* Fixed splash screen not properly refreshed on Fedora 21
* Improve <copyFile> action to merge directories on destination
* Allow specifying specific products in <antivirusTest> rule
* Allow searching in the Builder tree
* Fixed Builder GUI popups not being big enough to accommodate all settings in some cases
* Fixed Qt mode glitches in OS X 10.10
* Fixed LZMA-Ultra compression error when building in directories with spaces

Version 9.5.5 (2015-01-23)
* Fixed installers failing on some RHEL/CentOS 7 environments when running in text mode
* Fixed slide images not always being properly refreshed on xwindow mode

Version 9.5.4 (2015-01-15)
* Improved parameters wrapping on gtk mode
* Added new installer_http_code built-in variable to retrieve the result of HTTP actions
* Improved performance unpacking folders with a large number of files
* Reduced time spent in files to pack calculation at build time
* Reduced TLS initialization time on Windows
* Added <logonType> to <addScheduledTask> action
* Added RHEL 7 platform detection
* Improved <copyFile> action to allow overwriting directories on destination
* Allow configuring the <username> for which <addUnixDesktopStartUpItem> will add the startup item
* Allow configuring look and feel behaviour of unselected options in <choiceParameterGroup> parameters
* Improved component selection page to properly scale with wide installers
* Fixed issues loading some PNG images on CentOS 7
* Fixed gtk mode failing to start on RHEL/CentOS 7
* Fixed encoding errors in environment-manipulation actions
* Fixed Qt installers not being properly set on top of other windows when launched on some OS X environments

Version 9.5.3 (2014-11-06)
* Fixed installer window not being properly centered on Windows when running in Qt mode and using large left side images
* Improved "Abort, Retry, Ignore" dialog to properly translate its buttons 
* Removed extra debug messages printed on Windows
* Updated documentation

Version 9.5.2 (2014-11-03)
* Added TLS v1.1 and v1.2 support
* Improved performances of GUI mode when unpacking large amount of small files
* Added Major and Minor version to Windows ARP Menu information
* Fixed <showPasswordQuestion> dialog failing on some environments
* Improved CPU detection

Version 9.5.1 (2014-10-15)
* Enabled TLSv1 support in HTTPS actions
* Improved builder tool look and feel on Windows when using HiDPI configurations
* New <getFileInfo> action
* Improved multiline-text wrapping on 'win32' and 'osx' modes
* Improved <addDirectoryToPath> to fallback to 'user' scope on Windows in case of error
* Improved <rpmSpecFileTemplate> setting to support variables
* Fixed OS X installer generation failing on some environments because of permission denied errors
* Fixed 'installer_command_line_arguments' built-in variable not properly calculated on some Windows environments
* Fixed 'osxsigner' tool not properly signing installers with <requireInstallationByRootUser> enabled
* Fixed RPM installers not properly cleaned up after uninstallation

Version 9.5.0 (2014-09-29)
* Added support for OS X codesign v2
* Allow generating installers larger than 2GB on Windows, Linux and OS X
* Make XML Editor popups ask for confirmation when closed with unsaved changes
* Allow configuring the LZMA-Ultra compressor block size through the <lzmaUltraBlockSize> setting
* New <resourceLimitTest> rule to allow checking for resource limits on Unix systems
* Fixed ampersand not properly shown in the final page using Qt mode on Windows
* Allow Java launchers to be built at build time
* Improved how the "Fonts" directory location is calculated on Windows
* Added <disallowStartIfOnBatteries> and <executionTimeLimit> to <addScheduledTask> action
* Improved <windowsServiceTest> to also detect "Stop Pending" status as running.
* Added <enableFallbackTempDirectory> to allow providing a fallback temporary directory
* Dropped OS X 10.2 support
* Fixed XML Editor popup not providing details in its error when providing incorrect code
* Fixed <labelParameter> not properly wrapping its text in GTK mode
* Fixed "Add Shortcut" Builder dialog throwing an error when closing the popup
* Fixed LZMA-Ultra compression not properly detecting duplicated files when packing in some environments
* Fixed InstallBuilder installer and AutoUpdate not including PPC runtimes

Version 9.0.2 (2014-06-10)
* Improved Gtk mode to properly run in systems without the recently deprecated Pangox library
* Fixed error when displaying some combinations of multiline text labels in qt mode
* Fixed error when providing incorrect values for the <width> project setting
* Fixed language selection dialog not being focused on OS X when running in Qt mode
* Fixed Java Launcher creation failing in some environments when using downloadable components
* Fixed AutoUpdate build when disabling osx-ppc runtime inclusion
* Fixed Builder Preferences menu failing because of denied permissions in some Windows environments
* Fixed RPM installation hanging on Alt linux

Version 9.0.1 (2014-05-14)
* Fixed Windows single-platform licenses not being properly validated by the Builder
* Fixed Builder "Recent Projects" menu entry not listing results on Windows
* Fixed <showStringQuestion> and <showPasswordQuestion> not properly working on osx mode
* Fixed <type> tag of <getWindowsFileVersionInfo> actions not properly handling the "codepage" type
* Properly handle 301/302 redirects for URLs containing ports in <httpGet> and <httpPost> actions

Version 9.0.0 (2014-05-06)
* Added new compression algorithm LZMA-ultra to allow higher compression ration and faster decompression
* Added official support for OS X 10.9 and improved Windows 8 support
* Added support for Windows 8.1 and Windows Server 2012 R2
* Improved support for Ubuntu 14.04
* Added floating licenses support
* Added Retina Display support on OS X in qt mode
* New <ruleDefinition> feature to allow creating user defined rules
* Added support for HTML licenses in all flavors for Windows, Linux and OS X
* Added support for HTML licenses in all platforms
* Reduced memory consumed when unpacking files
* Reduced memory consumed by downloadable components
* Reduced memory consumed by update installers
* Reduced installation time in GUI mode, specially on Windows
* Reduced uninstaller startup time on Windows
* Fixed signing issues on 10.9 when enabling <requireInstallationByRootUser>
* New ${installbuilder_output_directory} and ${installbuilder_output_filename} built-in variables to allow retrieving the output directory and output installer filename at build time
* New <yamlFileSet> and <yamlFileGet> actions
* New <createOsxBundleZip> tag to allow compressing generated OS X installers as ZIP files
* New <sha256> action
* New <getTotalDiskSpace> action to allow retrieving the total disk space
* New <getProcessUsingPort> action to retrieve the name and PID of the application using a given port
* Added support for IPv6 in <hostValidation> rule
* New ${machine_swap_memory} built-in variable to allow retrieving the amount of swap memory in the system
* Improved license pages look and feel on OS X
* Improved Java launchers creation to avoid making the installer unresponsive while serializing them
* Improved <autodetectJava> action to recognize OpenJDK versions on FreeBSD and JRE 7 on OS X
* Allow language files to support UCN escape sequences
* Added Thai and Latvian languages
* Improved Chinese, Czech, Norwegian, Danish and Japanese translations
* Allow running installers with the debugger enabled directly from the builder
* Improved win32 mode to make the installers automatically adapt to different DPI settings
* Improved <platformTest> rule to support 'linux-x86' as a valid platform
* Improved <substitute> action to support backreferences
* Improved <parameterGroup> parameter to allow grouping <licenseParameter> pages
* Allow editing custom action instances in the builder GUI
* Allow <removeFilesFromUninstaller> action to also remove links
* Allow folders to configure whether to have its files added to the uninstaller or not
* Allow downloadable components feature to reuse already downloaded files
* Improved <autodetectDotNetFramework> action to recognize .NET framework 4.5
* Improved <iniFileGet> to ignore malformed lines
* Allow Java launchers to read and override its values from a .properties file
* Allow disabling pinning shortcuts to Start elements on Windows 7 / Windows 8
* Added linux-s390x as an independent build platform
* Make the builder warn the user and ask for confirmation if unsaved changes are not properly applied when closing a project
* Allow "Select All" keyboard bindings in all text widgets
* Improved <allowAddRemoveComponents> mode to perform a full uninstallation when no more visible components remain installed
* Make start menu shortcut creation fall back to use the user's directory when it has no permissions to write in the global scope
* Make installer serialize the ARP Menu registry keys under the HKEY_CURRENT_USER root key when the scope is set to user
* Deprecated Windows 2000 and ME platforms
* Set uninstaller.dat file permissions on Unix so only the owner can modify it
* POTENTIAL INCOMPATIBILITY: Changed <osxPlatforms> default value so only osx-intel support is included by default
* POTENTIAL INCOMPATIBILITY: Added ".git" and ".hg" to <filesToIgnoreWhenPacking> tag
* Fixed <xmlFileSet> not properly preserving the source file encoding
* Fixed Java launchers not being properly cleaned up at uninstallation on OS X
* Fixed file permissions issues when adding environment variables when running as root on OS X
* Fixed xwindow mode and builder crashing on Ubuntu 14.04 32bit
* Fixed <dos2unix> and <unix2dos> actions not properly preserving file encodings
* Fixed language selection dialog not alphabetically ordering its languages
* Fixed enter key binding on some OS X environments
* Fixed upgrade mode failing in some environments when upgrading installations built by old InstallBuilder versions
* Fixed builder creating corrupted binaries in some environments when enabling encryption
* Fixed downloadable components not being properly cleaned up if the uninstaller creation is disabled
* Fixed <choiceParameterGroup> wrapping issues in qt mode
* Fixed <choiceParameter> not allowing ampersand (&) characters in its options text
* Fixed <uninstallationLogFile> setting not being taken into account when uninstalling individual components
* Fixed <fileExists> rule failing in some Windows environments
* Fixed <installationScope> setting not being honoured when writing registry keys
* Fixed <saveRelativePath> setting not being applied to <licenseParameter> pages
* Fixed ${installer_interactivity} built-in variable not being defined at uninstallation time 
* Fixed slow redraw of <licenseParameter> pages when enabling fixed fonts
* Fixed error trying to load projects with strange characters in its filename
* Fixed long component selection pages being cut off on Windows and OS X
* Fixed some window grabbing issues in some environments when using the builder
* Fixed win32, osx and xwindow modes not being properly centered when using large window heights 
* Fixed XML comments format not properly preserved by the builder

Version 8.6.0 (2013-06-26)
* Improved text wrapping on Gtk mode
* Improved <showFileUnpackingProgress> to also work at uninstallation time
* Added Serbian and Lithuanian languages
* Improved required_diskspace variable to also work at build time
* Reduced delay when calculating the files to unpack
* Improved <addDirectoryToPath> to support spaces in the path
* Improved <antiVirusTest> rule
* Improved download progress meter reporting
* Improved Chinese language autodetection
* Allow Java launchers to be executed in the background
* Added new <removeUninstallationLogFile> setting
* Allow configuring JRE/JDK selection order for Java launchers
* Made builder ignore missing images if they do not belong to the platform being built
* Made <substitute> action ignore directories
* Improved OS X system language detection when running as Administrator
* Improved builder error reporting when packing distribution files with empty <origin>
* Improved InstallBuilder XML errors reporting
* Improved detection of HTTP idle connections 
* Allow configuring whether or not component groups should appear expanded
* Allow reusing already downloaded components if their checksum matches
* Fixed errors when downloading components bigger than 2GB in some environments
* Fixed component groups not properly displaying Unicode characters in Qt mode
* Fixed symbolic link creation failing in some environments
* Fixed temporary images not being properly deleted on OS X
* Fixed default application bundle icon not being properly displayed on OS X 10.5
* Fixed RPM and DEB packages failing to install in some environments
* Fixed GUI glitch in file and directory parameters in Windows 2008 SP2 x64 when enabling 64bit mode
* Fixed incorrect progress message when enabling <showFileUnpackingProgress> at uninstallation time
* Fixed <fileTest> rule default logic

Version 8.5.2 (2013-02-06)
* Fixed language selection dialog location for multiple monitors on Windows
* Fixed archives created with <zip> action creating directory records showing as files in Windows Explorer
* Added detection of missing distribution file/directory in CD-ROM mode
* Fixed support for HTTP chunked encoding transfers in InstallBuilder
* Added handling of HTTP error codes for autoupdate
* Improved detection of Brazilian Portuguese language on OS X
* Improved unpacking and removal of read-only files or directories
* Improved handling of archives with incorrect file/directory mode for <unzip> action

Version 8.5.1 (2012-10-22)
* Updated documentation
* Improved Windows 8 and Windows 2012 compatibility
* Improved handling of errors in Windows services actions
* Improved Java autodetection in OS X
* Improved <addUser> and <deleteUser> actions to support Windows domains
* Make AutoUpdate iterate over the list of mirrors in case of error when running in unattended mode
* Added high resolution icon for OS X installers compatible with Retina Display
* Improved <linkParameter> look and feel
* Improved <createSymLinks> action
* Fixed uninstallation not being aborted when closing the uninstaller window in some scenarios
* Fixed <choiceParameter> page wrapping on Gtk mode
* Fixed choice selection widget crashing in some Windows environments
* Fixed uninstaller creation failing in some Windows scenarios when calling the installer using a relative symbolic links

Version 8.5.0 (2012-08-05)
* OS X Mountain Lion support
* Added support for OS X code signing
* Improved performance when building downloadable components
* Reduced uninstaller creation time on Solaris
* Fixed <showQuestionDialog> dialog failing in some environments
* Updated documentation
* Fixed required_diskspace built-in variable not properly calculated when enabling <allowAddRemoveComponents>
* Fixed Qt Installers are not properly centered on Windows
* Prevent the uninstaller from aborting when trying to close the window
* Fixed <booleanParameterGroup> and <choiceParameterGroup> explanation text wrapping
* Fixed component description label not properly wrapping on some environments
* Fixed --onlyprojectfiles command line flag not working when using downloadable components

Version 8.3.0 (2012-07-13)
* Disabled the debugger in platforms other than Windows, Linux and OS X
* Single platform Linux edition can now build both x86 and x64 binaries
* Improved Java launchers to support generating console applications
* Improved LZMA support on Linux x64
* Improved xwindow mode to support the <wmImage> setting
* Added French translation to the AutoUpdate tool
* Added text mode support when using minimalWithDialogs UI mode
* Improved platform_name built-in variable to allow identifying CDROM builds programmatically  
* Updated Linux Distributions detection and added Amazon Linux distribution to the list of platforms
* Updated documentation
* New installer_error_code and installer_error_code_original built-in variables
* Fixed <compareText> rule not properly working in some scenarios
* Fixed some files being packed twice on Windows
* Fixed <deleteOnExit> not properly working in some Windows scenarios
* Fixed uninstaller not properly finding its data file in some scenarios
* Fixed <preInstallationActionList> not triggering the <intallationAbortedActionList> in case of error
* Properly set the installation log permissions when configuring the <defaultUnixOwner> and <defaultUnixGroup> settings
* Fixed <createOSXService> action not properly configuring its scope
* Fixed components not allowing programmatic selection at some points of the installation when enabling <allowAddRemoveComponents>

Version 8.2.0 (2012-04-24)
* New feature: Password protected encryption of installer payload
* Added support for ksh shell in environment variable actions
* Improved <autodetectJava> action to allow configuring the bitness (32bit/64bit) of the target Java runtime
* Improved <renameFile> action to support wildcards
* Improved installer images initialization on OS X
* Improved ini file actions to support both hashes and semicolons as comment characters
* Fixed OS X installers prompting twice for privilege elevation when enabling <requireInstallationByRootUser>
* Fixed 32 bit installers crashing in Linux Mint x64 when running in gtk mode
* Fixed system language not being properly detected in some Unix environments
* Fixed GUI buid log not allowing text selection on OS X
* Fixed unattendedmodeui setting not properly displaying its default value in the help menu
* Fixed <showProgressDialog> failing at uninstallation time in some environments
* Fixed required parameters being mandatory even if its parent parameter is not selected
* Fixed nested uninstaller calls not properly working on Windows

Version 8.1.0 (2012-03-05)
* Updated documentation
* Reduced memory consumed by big installers
* Improved Builder GUI drag and drop when using non-default DPI settings
* Improved startup time in qt mode when using a big number of directory parameters
* Improved Qt mode on OS X to use the native buttons order
* Fixed OS X installers failing in some environments when displaying the final page
* Fix crash when failing to delete locked DLLs
* Fixed installers failing when displaying some component groups configurations
* Fixed Windows shortcuts cache not being properly refreshed on uninstallation 
* Fixed downloadable components not properly verifying the download checksum in some environments
* Fixed <kill> action not properly working when invoked inside loop actions
* Fixed AutoUpdate not properly normalizing the update download location
* Fixed <userTest> failing in some Windows 64bits environments when <windows64bitMode> is enabled
* Fixed some actions not properly executed in the <onErrorActionList>
* Fixed installer not being deleted in some environments when <deleteOnExit> is enabled

Version 8.0.2 (2012-01-24)
* Improved example projects and made them easily accessible through the builder GUI
* Added HTTPS support for Linux, Windows and OS X for <httpGet> and <httpPost> actions as well as for the autoupdater
* Improved downloadable components to allow resuming failed downloads
* Added new <runAsAdmin> tag to <addScheduledTask> action
* Improved error handling when none of the configured allowed display modes can be initialized
* Improved built-in pages to display runtime changes of the <fullName> property
* Fixed HTTP-related actions failing in some Windows 64bit environments
* Fixed <componentTest> rule not properly checking child components 
* Fixed Qt mode not properly displaying right-to-left languages
* Fixed Gtk custom style not properly wrapping some parameter pages text
* Fixed <runProgram> action failing in some Windows environments when passing arguments containing % characters
* Fixed installer failing when launched in some partially broken HP-UX environments
* Fixed incorrect encoding in Italian language
* Fixed <choiceParameter> crashing when providing multiple options with the same text
* Fixed InstallBuilder's User Guide link in start menu on Windows
* Fixed Gtk progress bar writing warnings to console in some environments
* Fixed <labelParameter> not being properly vertically aligned

Version 8.0.1 (2011-11-30)
* Added new installer_command_line_arguments built-in variable
* Added new regular expressions section to the documentation
* Allow <showChoiceQuestion> to be used in the <preBuildActionList>
* Prevent <addUnixService> from failing because of warnings when adding the services
* Improved slide show images alignment on OS X and Windows in qt mode
* Fixed debugger failing in some environments when editing the executed action list
* Fixed debugger crashing when right clicking in the Variables Editor root node
* Fixed builder crashing when editing <substitute> actions and enabling advanced syntax mode
* Fixed builder crashing when building projects on some solaris-intel environments
* Fixed machine_ipaddr built-in variable not being properly defined on OS X Lion
* Fixed Gtk mode crashing in directory selection in some partially broken environments

Version 8.0.0 (2011-11-09)
* Implemented built-in debugger
* Improved component system to allow tree component selection
* Improved component system to allow downloading components from a remote server at runtime
* Improved component system to allow adding and removing components from an installation
* New <booleanParameterGroup> to allow enabling/disabling a set of child parameters dynamically 
* New <choiceParameterGroup> to allow exclusively enabling a parameter from a set of childs dynamically 
* Updated documentation
* Improved quickbuild performance in GUI mode
* Added new dotnet_framework_type built-in variable
* Improved all languages translations
* Improved AutoUpdate to support redirects
* Fixed slide show alignment on qt mode
* Improved GUI to allow hiding advanced settings
* Prevent components configured with canBeEdited=0 from being configured from the command line
* Make evaluation version message more clear on qt and gtk modes
* Improved linux_distribution_version autodetection on CentOS
* Fixed <showStringQuestion> dialog not being properly centered on OS X,
* Fixed Java launchers throwing an unknown error message when the launched application returned with a non-zero exit code

Version 7.2.6 (2011-10-18)
* Updated documentation
* Fixed qt mode text widget not properly wrapping its content on Windows
* Added support for tcsh shell in environment variable actions
* Fixed <platformTest> rule not properly recognizing osx-intel type on some 64bit environments
* Improved Italian translation
* Improved .password suffix to support nested variables
* Speed up uninstaller creation on Windows
* Now aborting the installation when canceling the language selection also triggers the <installationAbortedActionList>
* Improved AutoUpdate to support the <removeLogFile> feature
* Fixed builder crashing in some Windows environments when browsing for image files
* Fixed OS X bundles launcher script encoding not being properly configured
* Fixed Java launchers not being properly created on OS X
* Fixed <globalVariables> action not properly handling parameters

Version 7.2.5 (2011-09-05)
* Updated documentation
* Improved Dutch language support
* Fixed language files not being properly loaded when including a BOM
* Improved <addUser> action to properly deal with SELinux
* Fixed packed folders not being deleted when using multiple hierarchy <destination> tags.
* Improved Installer.RebootRequired string to allow variables
* Fixed custom uninstaller directory not being deleted in some environments

Version 7.2.4 (2011-08-09)
* Updated documentation
* Allow executing .cmd scripts on Windows
* Added contextual menu to GUI entry widgets to allow restoring the original value being edited
* Fixed installers not deleting the current file being unpacked when canceling the installation in some environments
* Improved <project> properties editor dialog to validate user input
* Fixed <httpGet> and <httpPost> actions failing in some Windows environments because of the download file being locked
* Improved registry actions to work with <foreach> and <while> actions

Version 7.2.3 (2011-07-29)
* Improved Italian, Czech and Russian language support
* Added support for HTTP redirects in <httpPost> and <httpGet> actions
* New --verbose command line flag added to command line builder
* Added new <endOfLineConversion> tag to file manipulation actions to allow configuring the EOL handling
* New <fileIsLocked> rule
* Improved performance of <unpackDirectory> action
* Improved AutoUpdate to allow configuring its output directory and filename
* Improved built-in pages language strings to support variables modified at any point at runtime
* Improved widget alignment in nested parameter groups in qt mode
* Fixed <addScheduledTask> action failing in some environments
* Dropped Windows 98 support
* Added examples for all actions to the documentation
* Improved deb generation to allow providing custom conffiles files
* Improved <autodetectJava> action to properly work on OS X 10.7
* Fixed <shutdown> action failing in some environments
* Fixed installers failing on OS X with some thirdparty system fonts configuration
* Improved the file packing filters to support semicolon separated patterns
* Improved command line builder to support displaying text mode popups when building
* Improved GUI builder to accept the information provided through the --setvars flag
* Added new built-in variable installbuilder_ui 
* Allow Arabic language to be selectable through the GUI builder
* Allow providing short version of registry root keys to registry actions
* Allow launching scrips from directories containing "&" characters
* Improved final page text wrapping
* Fixed some environment variable actions now properly working in some Windows environments

Version 7.2.2 (2011-06-20)
* Updated documentation
* New Search dialog for GUI Builder XML editor
* Disabled "Save" and "Save As" menu entries in GUI Builder when no project is loaded
* Fixed <addEnvironmentVariable> action not properly registering variables in some scenarios.
* Skipped mirror selection page in AutoUpdate tool when just one option is available
* New <pathManipulation> action to create absolute paths
* Fixed <showPasswordQuestion> not saving the password when accepting the dialog by pressing enter
* Improved <firewallTest> rule to also include default Windows firewall
* Improved Russian language support
* New <createWindowsARPEntry> project setting to allow disabling the creation of the Add Remove Programs entry on windows
* Improved <addScheduledTask> action to support latest Windows versions
* Added Support for shiftjis encoding on HP-UX in text mode

Version 7.2.1 (2011-06-03)
* Improved Albanian, Bulgarian, Croatian, Danish, Finnish, Argentine Spanish, Slovak, Swedish and Turkish language support
* Updated documentation
* Speed up installer startup time when being launched from a network drive
* New <addWindowsAccountRights> and <removeWindowsAccountRights> actions
* Improved administrator user check in HP-UX platform 
* Improved <abortOnError> and <showMessageOnError> action settings to support variables
* Improved minimalWithDialogs mode look and feel
* Fixed text not being correctly wrapped in some <choiceParameters> in osx and win32 modes
* Improved environment actions on Unix
* Fixed <passwordParameter> entries misaligned in some languages
* Fixed GUI "Files" tree not being updated when manually deleting <distributionFile> elements from the XML editor
* Fixed <globalVariables> action not properly handling case insensitivity

Version 7.2.0 (2011-05-18)
* Improved packing filters to allow excluding files to pack at any depth of the directory hierarchy
* Added Albanian, Bulgarian, Croatian, Danish, Finnish, Argentine Spanish, Slovak, Swedish and Turkish language support
* Allow modifying the exit code in the <installationAbortedActionList>
* Allow disabling Xft fonts support in xwindow mode to reduce installers size
* Improved language autodetection on Windows
* Updated documentation
* Fixed 32bit installers crashing in Ubuntu 11.04 64bit when running in gtk mode
* Allow executing .cmd scripts on Windows
* Added new variable installation_aborted_by_user
* Removed --enable-components, --disable-components and --optionfile options from uninstaller help menu
* Improved <showProgressDialog> refresh rate to allow properly displaying quick actions progress text
* Fixed <launchBrowser> throwing warnings to console in some corrupt Linux environments
* Fixed default installation language not being properly resolved in uninstaller help menu
* Fixed error in GUI builder when using file dialogs in some scenarios
* Fixed scope issue in custom actions
* Replaced missing occurrences of "Ok" text in buttons to all uppercase "OK"  per UI guidelines 
* Fixed <enableLinuxLegacySupport> setting failing in some environments 
* Fixed some graphic glitches in osx mode

Version 7.1.1 (2011-05-05)
* Fixed <unzip> action failing in some environments
* Fixed InstallBuilder license registration dialog failing on OS X
* Improved look and feel of <showProgressDialog> on OS X 

Version 7.1.0 (2011-04-27)
* Fixed installer startup failing in some scenarios when being launched from a symbolic link
* Fixed <processTest> rule failing in some environments
* Fixed installation log containing unnecessary empty new lines when using disabling the file unpacking process
* Fixed comboboxes not being properly displayed in Windows Vista and 7 in win32 mode
* Improved look and feel of the Builder and installers in OS X and Windows
* Improved <getWindowsACL> to properly handle invalid SID provided as username 
* New <languageSelectionStyle> project property to configure the style of the language selection dialog
* Improved reported errors when loading invalid XML files
* Updated documentation
* Fixed uninstaller not being removed on HP-UX

Version 7.0.5 (2011-04-18)
* New split HTML version of the documentation
* Significantly improved <unzip> action performance
* Improved installers to allow variables in most of the project tags
* Improved GUI to accept variables in choice and boolean-like properties
* New <osxBundlesAreFiles> property added configure how <fileParameter> and <directoryParameter> should validate bundles in OS X
* Allow <linkParameters> to receive keyboard focus on Qt mode
* Improved OS X bundle launcher script to support projects with UTF-8 characters in their full name
* Improved Polish translation
* Fixed <windowsResourceProductVersion> not properly working
* Removed outdated syntax from demo projects
* Updated command line builder icon on Windows
* Fixed <globalVariables> action not properly handling project references
* Show more verbose errors in <break> and <continue> actions when invoked outside loop
* Fixed XML comments not being preserved in the GUI editor in some scenarios
* Fixed installers failing on HP-UX, AIX and FreeBSD 4 in some system language configurations

Version 7.0.4 (2011-03-31)
* Updated documentation
* Added <selectionOrder> to the <autodetectJava> action to allow specifying the order of the detected Java versions
* Added <checkFreeDiskSpace> rule
* Added <clearWindowsACL> action
* Added <osxUninstallerApplicationBundleIcon> to configure uninstaller icon on OS X
* Added <osxPlatforms> project tag to configure the OS X runtimes to build
* Improved Italian, French, Norwegian, Korean and Spanish translations
* Included English language file in the installers as a reference
* Improved build performance in Solaris, FreeBSD, AIX, HP-UX and IRIX
* Fixed <httpPost> action failing when providing an empty <queryParameterList>
* Fixed main progress bar not properly configured in some scenarios
* Fixed <portTest> rule throwing an error when providing a malformed port
* Changed "Ok" buttons text to all uppercase "OK"  per UI guidelines
* Fixed registered estimated size on Windows 7 not being properly calculated
* Fixed RPM database registration not honoring files removed from uninstaller
* Fixed environment variables not being properly registered in some OS X terminal shells
* Fixed rollback functionality not properly handling the original drive

Version 7.0.3 (2011-03-07)
* Added Czech language support
* Added Norwegian language support
* Updated documentation
* New <globalVariables> action to allow custom actions to modify project level variables
* Reduced installer startup time on OS X when running in qt mode
* Provided a default value for installer-language command line flag in the help menu
* Improved <writeFile> action to accept variables in the <encoding> tag
* Improved <run> and <show> action properties to accept variables
* Added support for Bourne shell (sh) in actions related to environment variables
* Fixed installer refreshing issue when quickly pushing 'next' button multiple times on Windows
* Fixed installer not properly loading images in some environments
* Properly handle standard streams redirection on Windows Java launchers
* Fixed AutoUpdate <requireInstallationByRootUser> property not properly working on OS X
* Improved regular installer registration with RPM database
* Fixed qt mode post-uninstallation popups not displaying any icon on OS X
* Fixed GUI XML editor not properly preserving comments

Version 7.0.2 (2011-01-27)
* Updated documentation
* Allow the <scope> tag of the <addEnvironmentVariable> action to contain variables
* Provide more verbose error when the builder does not have permissions to write in the output directory
* Improved look and feel of the "custom" <style>
* Improved wrapping of <choiceParamter> text when using radiobuttons style in gtk mode
* Added new built-in variable machine_cpu_count
* Improved <unpackFile> action performance
* Added <requestedExecutionLevel> tag to the Autoupdate tool to allow configuring its execution level
* Improved quickbuild in GUI mode to pack differences in packed files in addition to project changes
* Added machine_cpu_speed and machine_total_memory built-in variables to OS X platform
* Fixed popup XML editor failing in some scenarios
* Fixed gtk buttons not being properly localized after the language selection when using <overrideGtkButtonText>
* Fixed <enableLinuxLegacySupport> setting failing with the new compression system
* Fixed builder crashing on some OS X environments when using Asian languages
* Fixed command line quickbuild not properly working in some environments with the new compression system
* Fixed <getFreeDiskSpace> reporting incorrect data in some OS X environments
* Fixed <addEnvironmentVariable> not properly working when used in the <foreach> action

Version 7.0.1 (2010-12-14)
* Fixed XML Editor failing to load some projects
* Improved XML editor performance when editing big XML projects
* Remove incorrect validation in <distributionFile> and <distributionDirectory> elements

Version 7.0.0 (2010-12-12)
* Ability to create custom actions to reuse code snippets
* New <createJavaLaunchers> action to create Java launchers
* New in depth documentation
* Allow downloading ready to use JREs from the GUI builder
* Added built-in variables inspector to the builder
* Added search functionality to builder actions dialog
* New built-in XML editor in the GUI builder
* Improved <windowsAccountTest> to work on Windows domains
* Improved continuous progress popup layout
* Fixed <waitForPort> action not properly working on some Windows 64bit environments
* Display component selection default description in Qt and Gtk modes
* Localize OS X translucent menu bar
* Fixed Korean and Chinese languages not being properly autodetected on some Windows environments
* Fixed ${machine_ipaddr} not being properly resolved in some Windows 64bit environments
* Asynchronously check for updates in the builder
* Making permissions errors when building more verbose
* Moved language selection dialog after the <initializationActionList>
* Make sure the log contains all the information after an error
* Improved packing method to improve packing and unpacking speed and reduce installers size and memory consume
* Fixed multidisk cdrom installers not retrying asking the first disk after a failed try
* POTENTIAL INCOMPATIBILITY: Changed Unix installer extension from .bin to .run to make installers recognized as executable in some environments such as latest versions of Ubuntu
* Fixed <createBackupFile> destination field validation
* Fixed <getFreeDiskSpace> failing on OS X if the disk volume tested contains spaces

Version 6.5.6 (2010-10-27)
* Improve administrator user check in Solaris platform
* Added Xft font support in Solaris
* Added <windowsAccountTest> rule
* Fixed dialog buttons not being localized in qt mode
* <autodetectDotNetFramework> is now accessible through the GUI
* Allow created OS X environment variables to be accessible from GUI applications
* Fixed Autoupdate tool not properly launching the downloaded installers in some OS X scenarios
* Fixed key bindings not properly working on xwindow, osx and win32 modes when selecting some languages
* java_autodetected and dotnet_autodetected are now set to 0 if the detection fails
* Fixed UI becoming not responsive when using <unpackDirectory> to unpack big directories
* Fixed <choiceParameter> not properly representing its value in some UI modes when using variables
* Support zh_HK as valid language code
* Fixed errors when trying to overwrite hidden files on Windows
* Now in_empty and is_not_empty rules conditions in <fileTest> rule now take into account hidden files

Version 6.5.5 (2010-10-04)
* New <registryFind> action
* New options is_type and is_not_type to registryTest rule
* Implemented new <osxApplicationBundleIcon> project property to configure the icon on OS X
* Added Java Web Start binary to the list of autodetected targets in <autodetectJava> action
* Upgrade Autoupdate tool to support high color depth icons
* Allow viewing XML code of elements from the GUI
* Fixed <compareText> description in the GUI
* Fixed popups sometimes not working when being resized in Solaris
* Modified position of "Created with an evaluation version" text in unregistered installers
* Fixed Chinese and Brazillian language autodetection on Unix 
* Fixed wrapping in welcome and installation finished pages in Qt installers on Windows and OS X
* Fixed integer validation in <exit> action
* Fixed GUI context menu on OS X
* Improved page wrapping in Gtk custom style
* Properly deal with incorrect paths when creating the rollback directory
* Fixed Suse version autodetection failing in some environments

Version 6.5.4 (2010-09-17)
* Implemented drag and drop of nodes in the GUI builder
* Make folders' <actionList>  accessible through the GUI
* Added OS X service manipulation actions <createOSXService>, <deleteOSXService>, <startOSXService> and <stopOSXService>
* Added <osxServiceTest> rule to check OS X services state
* Fixed XML load errors displaying an incorrect line number in some unicode files
* Implemented new <xmlFileCommentElement> action
* Improved language autodetection in OS X
* Added <ordering> tag in <choiceParameter> to specify the display order of the elements
* Added is_windows_admin_account, is_windows_user_account, is_windows_guest_account to <userTest> rule
* Properly wrap <booleanParameter> descriptions when used inside a <parameterGroup> on xwindow, win32 and osx modes
* Improved error when loading an incorrect project to display line number and file
* Wrap all text mode pages
* Allow resolving environment variables with parentheses
* Make <zip> action recursive
* Fixed InstallBuilder RELAX NG schema
* Improved keyboard navigation on xwindow mode
* Fixed incorrect font used in Chinese and Korean languages on Windows
* Fixed <showQuestionString> failing on OS X
* Make sure all actions defining variables resolve variables in the variable name
* Fixed deb/rpm helper binaries permissions
* Fixed <iniFileGet> action requiring write permissions over the target .ini file
* Properly handled errors retrieving available disk space
* Clean unnecessary helper binaries when upgrading Debian packages
* Added monthly date type in <addScheduledTask> action
* Make Autoupdate tool to also look for the license in user directory on Windows

Version 6.5.3 (2010-08-24)
* Added Korean language support
* Now any icon resolution is accepted for Windows installers
* Fixed OS X installers failing when requiring Admininstrator privileges and containing invalid characters in its <fullName>
* Make keyboard focus visible on xwindow mode
* Fixed uninstaller not being properly created in some scenarios when launched with a symbolic link
* Added support for symbolic links in unzip action
* Allow component names to contain underscores
* Fix integers validation failing dealing with large integers
* Allow <permissions> tag in <changePermissions> action to contain variables

Version 6.5.2 (2010-08-13)
* Properly localize --help menu
* Fixed GUI Builder failing when adding <throwError> actions

Version 6.5.1 (2010-08-12)
* Updated Brazilian Portugese language
* Fixed dialogs not being correctly centered on Windows in qt mode
* Fixed --version and --help Windows popups not being displayed
* Fixed minimal unattended ui mode not being correctly detected on Solaris
* Fixed Japanese and Chinese languages not being correctly displayed in xwindow mode

Version 6.5.0 (2010-08-05)
* New <consoleWrite> action
* New mechanism to escape variable references
* Allow configuring Autoupdate tool proxy through the configuration file
* New <antivirusTest> and <firewallTest> rules
* Added --help menu to the builder
* Added --license command line flag to the builder to provide an alternative license file
* Added --debugtrace command line flag to the builder
* Added --project command line flag to specify a project to load in the GUI builder
* Improved unattendedModeUI mode to support gtk and qt mode
* Added <delay> tag to <stopWindowsService> action
* New windows-x86  platform type check added to <platformTest> rule
* Fixed refresh issue that affected download rate in Autoupdate Tool, specially on OS X
* New <runAsAdmin> tag added to shortcuts to mark Windows shortcuts to run as Administrator
* Improved validation of boolean and integer type settings
* Added OS detection support for RHEL 6
* Use default Unix permissions in <unzip> action running on Unix when unpacking files created on Windows
* Added <htmlText> tag to <showText> action and <htmlValue> to <infoParameter> to display HTML text in Qt mode
* Fixed "Test Run" button on GUI builder not correctly launching generated Windows installers in UAC environments
* Added <title> tag to <showQuestion>, <showInfo> and <showWarning> actions
* Added <ruleList> tag to <startMenuFolder> elements
* New windows_os_uac_enabled built-in variable to check the state of the UAC on Windows
* New warnings to report command line flags that are provided multiple times
* Properly deal with multiple installation mode command line flags
* Fixed <zip> action failing in some scenarios when packing directories
* Enforce executable permissions in Unix shortcuts
* Added file and line number to duplicated tags warnings
* Fixed tcsh not being recognized as a valid shell by environment actions on Unix
* Added <askForConfirmationOnUninstall> tag to disable the built-in confirmation popup on uninstallation
* Fixed uninstaller not being deleted on unattended mode on Windows in some scenarios
* Fixed <showProgressDialog> title not being configurable in some UI modes
* Include <negate> state in rule description in GUI builder
* Added double click binding in "Files" section in the GUI builder
* Fixed <httpGet>/<httpPost> and <setWindowsACL> actions being affected by the state of the Wow64 filesystem redirection
* Fixed crash on GUI builder when the <platforms> tag of folder contains incorrect values
* Clean /opt/bitrock directory after deb/rpm uninstallation
* Fixed installer failing on Windows when TEMP environment variable containing multiple directories
* Fixed creation of symbolic links on OS X failing when target is an existing link to a non empty directory

Version 6.4.0 (2010-06-11)
* Added support for nested variables
* Now all variable references are case insensitive
* New <creacteShortcuts> action
* Prevent GUI builder from accepting incorrect values in its dialogs
* Added new <while> and <break> actions
* New <autodetectDotNetFramework> action
* New <locate> action
* Added new <zip> action
* Added matchHiddenFiles flag to <touchFile>, <copyFile> and <deleteFile> actions
* Added OS X support for user and group manipulation actions
* Added new built-in variable windows_os_flavor
* Allow <foreach> and <if> actions to be accessed through the GUI
* Fixed Qt installer abnormaly resizing in "custom" mode
* Fixed patterns in folders' inclusion filters not resolved when using backslashes
* Allow <showProgressDialog> to display a download progress bar when containing a single <httpGet> child action
* Normalize all relative paths to reference project directory in Autoupdate tool
* New validation of duplicated project tags
* Disabled RPM registration in platforms with non-native RPM support
* Increase size of builder project properties dialog
* Allow empty user and password to be provided in <addScheduledTask> action
* New <runOnlyIfLoggedOn> tag in <addScheduledTask> action
* Disable language selection dialog when a value is provided through command line
* Allow InstallBuilder license to be saved to and loaded from user personal directory on Windows
* Added Japanese string for Installer.DirectorySelection built-in string
* Fixed glitch in button states after installation page
* Allow task name with spaces in <addScheduledTask> action

Version 6.3.1 (2010-05-04)
* New <sha1> action
* New <md4> action
* New <addDirectoriesToUninstaller> action
* Added <password> tag to <addUser> action
* Improved <httpProxyInit> to autodetect system proxy settings
* Improved RPM integration
* Improved Qt look and feel on Windows and OS X
* Validate user provided input for parmeters while creating them.
* Added Windows support for user and group manipulation actions
* Fixed <registryDelete> failing to delete keys on Windows 64bit
* Documented windows_os_service_pack
* Prevent fileTest rule from failing when testing a non-existent link
* Fixed autoupdate returning update available when check_for_updates was set to '0'
* Fixed Windows 7 not being recognized when Windows 7 Logo Toolkit is installed
* Fixed symbolic links not being properly packed when included as <distributionFile>
* Fixed variables not being resolved in some Autoupdate project tags.
* Fixed error parsing Autoupdate projects containing utf-8 BOM
* Built-in registry keys are now created in the 64bit view when <windows64bitMode> is enabled
* Fixed Windows 2008 autodetection failing in some environments

Version 6.3.0 (2010-02-23)
* Now Windows installers set the default installation language from a previous installation when updating
* Reduced memory consumption when unpacking large single-file installers
* Multiple improvements in CDROM installers
* Allow HTML licenses file in Qt mode
* New installer_builder_version and installer_builder_timestamp built-in variables
* License parameters now accept multiple localized licenses
* New <httpProxyInit> action to configure proxy to use with http actions
* New <xmlFileGet> and <xmlFileSet> actions
* New <addScheduledTask> and <deleteScheduledTask> Windows actions
* New <getWindowsFileVersionInfo> action
* New <foreach> and <if> actions
* New .password suffix to mask passwords in log messages
* Support for osx-intel and osx-pcc in platform test
* Fixed proxy configuration in autoupdate failing in some environments
* Fixed autoupdate failing on OS X when using minimalWithDialog ui mode
* Properly set default output and project directory on Windows 7
* Prevent downloaded installers to by launched in text mode by the autoupdater
* New tag to match hidden files on Windows
* Fixed <installerFilename> property being empty during build time
* FreeBSD 8 support
* Improve RPM removal process
* Fixed <singleInstanceCheck> failing on Windows when using <windows64bitMode>
* Fixed unpacking errors in some environments

Version 6.2.7 (2010-01-22)
* File selection crashes if third-party Explorer extensions change locale
* Fixed unpacking error in some Windows environments due to antivirus software locking files
* New <useMSDOSPath> property in <runProgram> action to configure whether to use or not 8.3 format in the <program> path
* Added encoding property in <addTextToFile> action and <fileContentTest> rule
* New <unix2dos> action
* Allow file type filters in <fileParameter> dialogs for xwindow, osx and win32 modes
* Include InstallBuilder version by default in the generated installers version info

Version 6.2.6 (2009-12-21)
* New <showStringQuestion> dialog
* New <getWindowsACL> action to retrieve ACLs on Windows
* New <changeExecutableResources> action
* Added project property <replaceLockedFilesOnReboot> to mark locked files on Windows to be renamed instead of prompting the end-user to abort/retry/ignore
* Fixed build-time memory errors produced by the usage of <requestedExecutionLevel> with big executables
* Allow Autoupdate strings to be localized
* Added <excludeFiles> and <includeFiles> tags to <deleteFile> action
* Actions that modify the environment variables and PATH can now be applied to a particular user (Unix only)
* Fixed <onErrorActionList> masking the the original error
* Fixed .desktop files on Unix not correctly working on some scenarios
* Avoid locked files error when unpacking on Unix
* Use SID for user default value on <setWindowsACL> action
* Added is_empty and is_not_empty tests to <registryTest> rule
* Fixed registry actions not correctly resolving variables in their errors
* Fixed Autoupdate tool not modifying Windows executable resources
* Fixed some minor bugs in Autoupdate tool
* Improved windows-x64 rule
* Allow uninstaller icon to be customized
* Fixed <rollbackBackupDirectory> not being deleted after a successful rollback
* Fixed <fileParameter> validation failing in some environments
* Fixed software version registry key not being updated after an upgrade
* Give executable permissions to serialized .desktop files on Unix

Version 6.2.5 (2009-11-09)
* Allow file filtering in folders
* Fix glitch in action popup on OS X builder
* New <setWindowsACL> action to modify Windows permissions
* New <restoreRollback> action
* New <windowsARPRegistryPrefix> project property
* Fixed "Administrator privileges required" string not shown in all languages
* Fixed <findFile> action always following symbolic links
* Improved text entry field in GUI builder

Version 6.2.4 (2009-10-05)
* Improved builder tool on OS X x86

Version 6.2.3 (2009-10-02)
* Improved builder tool on OS X x86
* Added new windows_os_service_pack built-in variable
* Allow <addTextToFile> action to also insert text at the beginning
* Allow autoupdate to run and install installers in unattended mode
* New <deleteLockedFilesOnReboot> action.
* Fixed <kill> action not accepting paths as arguments on Windows 64bits
* Fixed <autodetectJava> action not correctly detecting 64bit Java installations on Windows
* Fix OS X installers Info.plist not correctly created when non-ASCII characters were used in the installer name
* Open file button fails on OS X if initial directory does not exist.
* addLibraryToPath action does not resolve variables in its path tag
* addLibraryToPath not working correctly on Linux x64 and OS X
* platformTest can now distinguish between hpux-ia64 and hpux-parisc
* addDirectoryToPath not working correctly on OS X
* launchBrowser action fails in some environments

Version 6.2.2 (2009-09-04)
* Fixed <showText>  and Readme file not correctly resized on Windows
* New <compareVersions> rule
* Added additional information to autoupdate guide
* Expanded entry fields on builder GUI
* Fixed Unix services related actions not working correctly on some distributions

Version 6.2.1 (2009-08-26)
* Added Windows 2008 R2 platform to platformTest rule
* Add SME linux to linux_distribution built-in variables
* Allow deleting nodes on OS X from the GUI
* Added windows64bitMode project property to allow installer behave as 64bit applications on Windows
* Fixed OSX version of the autoupdate not properly launching the downloaded installer
* Added windows64bitMode project property to allow installers to behave as 64bit applications on Windows
* Fixed installation step of installers launched by the autoupdate being skipped in some environments
* New if/else action
* Added <height> and <width> properties to showText dialog action
* Allow Windows UAC level to be changed at built-time with <requestedExecutionLevel> property
* Validation of parameters' leftImage file at build time
* Component selection page resizable for Qt
* Added installer_ui_detail built-in variable to get the detailed installation mode
* Added aditional tests to componentTest rule
* Fixed platformTest type=windows-x64 not working
* Removed alpha channel from leftSide image to prevent some crashes on Solaris
* Fixed rules not being evaluated for onErrorActionLists

Version 6.2.0 (2009-07-20)
* Added <debugLevel> project property and --debugLevel flag to control the verbosity level in installation log file
* Added new <postUninstallerCreationActionList>
* Added windows-xp, windows-2003,windows-2008 and windows-7 to <platformTest> rule
* Fixed uninstaller failing because of missing DLL in some scenarios
* Fixed  <runProgram>'s <customErrorMessage> not being used in the installationLog
* Fixed stringTest ascii and digit types.
* Added Hebrew and Greek language support.
* Fixed Linux installers unable to launch from a directory containing non-ASCII characters
* Added osx_version and osx_major_version built-in variables.
* Fixed choiceParameter combobox style not correctly working when using variables in its 'value' and 'default' tags

Version 6.1.3 (2009-07-05)
* Added "hidden" and "readonly" attributes to <changeOSXAttributes> action
* Relative installation paths not always working in interactive mode
* Added <changeWindowsAttributes> action
* Fixed showQuestion dialogs not working when <overrideGtkButtonText> enabled
* Display warnings on project load when incorrect choice parameters 
* Fixed error loading a project with no components.
* Enabled optional timestamp in logMessage action.
* Fixed uninstaller on OSX when installing as root.
* Consider directories containing .DS_Store file as empty
* Added autoupdate support for OS X

Version 6.1.2 (2009-06-19)
* Fixed autoupdate customization utility

Version 6.1.1 (2009-06-17)
* Show the default components to install and allowed components in the help menu
* Add support for excluding files in many actions
* Fix upgrade installers on OS X not modifying uninstaller
* Fix deleteEnvironmentVariable not working correctly on Unix systems
* New negate option for all rules, allows negating any rule
* Fix builds from shared drives on Windows and VMWare mapped drives
* File readable test failed in some shared drives under Windows
* addDirectoryToPath action did not honor insertAt on Windows systems
* Fixed resizing of component selection screen in some GUI modes
* New unattended mode ui command line option
* Allow component selection page to resize horizontally
* Fix sometimes uninstaller failing when files where in use
* Support Windows x64 test in platformTest
* Fix linux-x64 not having GUI builder mode
* Fixed runAs failing when arguments contain quotes

Version 6.1.0 (2009-05-30)
* Support for IBM iSeries, OS/400  Use existing AIX target.
* Builder tool GUI mode is only available now for Linux x86/x64, Windows and OSX. Rest of platforms can still use command line build tool
* Fixed crashes when current Linux distribution version could not be determined
* Resizable component selection for X-window mode
* Added Windows 2008 and Windows 7 platforms to  windows_os_* built-in variables
* Fixed paths always being relativized when saving project using the GUI
* Fixed symbolic links not being correctly registered in the uninstaller log
* New <showChoiceQuestion> dialog
* Fixed "Open Project.." in GUI not opening last used directory
* Fixed <showPasswordQuestion> dialog in Qt
* Added a delay option to <shutdown> action
* Added suport for UTF-8 encoding on .desktop files
* Added new <getSymLinkTarget>
* Added is_symlink and is_symlink conditions to fileTest rule
* <runAs> property in <runProgram> action no supports scripts with spaces.
* <wowMode> in Windows registry related actions and rules now admits variables
* Now the GUI allows nested startMenuFolders
* Fixed installed shortcuts on common Desktop not being refreshed properly on Windows Vista

Version 6.0.3 (2009-04-27)
* Fix error in Unix systems when the HOME environment variable was not set
* Fix autoupdate failing to download updates when a file already existed
* The installer now returns an error code if an error occurs in 
postInstallationActionList even if the error does not stop the installer
* A new action list, preShowHelpActionList allows the content of the 
--help output to be dynamically modified
* Fix choice parameter combobox not supporting a default value that contains 
variable references
* New variables machine_total_memory and machine_cpu_speed
* LC_ALL takes precedence over LANG when determining system language in 
Unix systems
* Fix distro detection on RedHat derivatives like Red Flag Linux or 
Scientific Linux
* Fix validation action lists been run twice for parameter groups on Qt mode

Version 6.0.2 (2009-03-18)
* Added support for FreeBSD 7 x86/x64
* Fix locale autodetection on OS X
* Building Windows autoupdate from Linux was broken
* GTK mode not initialized when path had spaces
* addEnvironmentVariable breaks if the variable is already defined
* Fix crash in builder when folder added to wrong element

Version 6.0.1 (2009-03-13)
* Add support for OpenJDK in autodetect Java action
* Fix unzip action causing the InstallBuilder GUI to crash
* Fix allowedLanguages functionality showing empty language selection window
* Fix substitute action modifying binary files even if a match was not present

Version 6.0 (2009-03-05)
* Autoupdate functionality available for Windows and Linux
* Quickbuild functionality now can do incremental builds and only repackage new
or changed files
* Support for right to left languages in Qt mode, in particular Arabic is
now included
* Enhanced keyboard navigaton on Qt mode
* New <shouldPackRuleList> rule for components, folders and shortcuts,
  allows specifying at build time whether to pack or not the given element
* New <mathExpression> action, allows to perform basic arithmetic
  calculations
* New <wow64FsRedirection> action, allows configuring the file system
  redirection behavior on Windows 64-bit systems
* New <registryTest> rule, allows checking whether a key or value exists
  on the Windows registry
* New <windowsSoftwareRegistryPrefix> property, allows customizing the
  application entry under HKEY_LOCAL_MACHINE\Software
* Ability to change the version information of generated Windows
  installers. This is the information that appears when you position the
  mouse over the icon.
* Custom style installers were not showing left image with parameter
  groups
* Fix typo in Dutch localization	
* Fix addUnixDesktopStartUpItem not working on latest Ubuntu 8.10
* Fix InstallBuilder problems as admin when running on Mac OS X Snow Leopard
* Using runProgram with runAs option will now bring a graphical prompt on
Linux systems with gksu or kdesu available when the installer is not
already running with admin privileges
* External included files will now be serialized to separate files on disk when
using the GUI
* New java_autodetected variable. Set to 0 unless autodetectJava action is
successful and sets it to 1.
* Fix issue with builder CLI on Windows not working properly
* New .escape_backslashes suffix for escaping backslashes in values. Useful
when dealing with Java properties
* New shutdown action to reboot the machine on Windows
* Fix addDirectoryToPath not working in previous version of InstallBuilder

Version 6.0pre2 (2009-01-02)
* Fix an error on GTK choice parameter which was causing the installer to
  halt
* Linux legacy installers do no longer have GTK graphic mode available
  (they directly fall back to xwindow mode). This is required due to
  incompatibilities with GTK releases in recent distributions.

Version 6.0pre1 (2008-12-31)
* New Advanced Builder Editing mode, allow to access most of the
  InstallBuilder functionality through the Builder Graphical Interface
* Fix GTK segmentation fault on Linux installers on recent Linux
  distributions
* Do not support by default distributions with GLIBC versions previous
  to 2.3.2. That means the installer will work for example with Red Hat
  9 but not earlier distributions. For most customers, this will have no
  impact (Red Hat 9 was released in 2003 and reached end-of-life in
  2004) but if you need to support earlier glibc versions you can do so
  by setting <enableLinuxLegacySupport>1</enableLinuxLegacySupport> in
  your project.
* Enhanced support for Windows installers running under Wine
* Enhanced keyboard navigation for Qt installers
* Fixed issue with transparent images on Solaris Intel that could cause
  the installer to exit with an X_GetImage error
* New <shouldPackRuleList> rule for components, folders and shortcuts,
  allows specifying at build time whether to pack or not the give element
* New <enableTimestamp> action. By default, installers store a
  timestamp of when they were created. The timestamp will be displayed
  when executing the installer with --version.  However, this will mean
  that packing the same set of files at different points in time will
  create installers that are slightly different (and will have a
  different md5 sum, for example). If this is important to you, you can
  now set enableTimestamp to 0, allowing you to create completely
  identical installers as long as the files being packed are the same.
* New <mathExpression> action, allows to perform basic arithmetic
  calculations in the XML
* New <wow64FsRedirection> action, allows configuring the file system
  redirection behavior on Windows 64-bit systems
* New <registryTest> rule, allows checking whether a key or value exists
  on the Windows registry
* New <windowsSoftwareRegistryPrefix> property, allows customizing the
  application entry under HKEY_LOCAL_MACHINE\Software
* Installers now ignore packing .DS_Store files on OS X
* Fix temporary images folder creation on Qt installers
* Password parameters are now reset if validation fails
* Ability to change the version information of generated Windows
  installers. This is the information that appears when you position the
  mouse over the icon.
* Custom style installers were not showing left image with parameter
  groups
* Fix uninstaller generation error when debug trace was enabled on big
  installers
* <stringTest> now handles correctly empty strings
* <portTest> now handles correctly an unspecified port
* Fix updating the installdir parameter from the keyboard on Qt installers
* Fix <linkParameter> on Qt mode, it was not accepting installer variables

Version 5.4.15 (2009-02-13)
* Fix GTK segmentation fault on Linux installers on recent Linux distributions. 
* New enableLinuxLegacySupport project property. You will now need to manually enable this setting if you need to support Linux versions older than 2.3 (older than Red Hat 9)
* Add --version info to the windows right-click menu properties
* Included progressText for all actions
* Added console command line builder to Windows
* Added support for HTTP basic auth (username & password) in httpGet and httpPost actions
* Do not create a file to save a request result if no file was specified in httpGet
* New registryTest rule to check for the existence of registry keys
* Enable or disable automatic filesystem redirection on Windows x64
* New shouldPackRuleList rule for components, folders and shortcuts
* Disable storage of installer creation time, to allow having binary-identical installers if built from the same files
* New installer variables to easily retrieve Linux Distribution data based on lsb_release: linux_distribution_id,linux_distribution_codename, linux_distribution_release, linux_distribution_description
* Force defaultLanguage to be in the list of allowed languages even when allowLanguageSelection is disabled
* Let the user customize the "Uninstallation did not complete successfully" string
* Let the user overwrite readonly files on Windows during installation by automatically changing the permissions
* Password field is reset to empty in all modes in case a validation fails
* Language selection window shows language in native spelling
* Fixed error with registerWithPackageDatabase on Fedora Core 10
* Fixed showProgressDialog in text mode overwriting the progressbar
* Fixed portTest throwing internal error when port is empty
* Fixed stringTest not working correctly with empty strings
* Fixed X Windows error on Solaris Intel 10
* Fixed uninstaller breaking due to installationLog initialization
* Fixed the issue with Qt installers creating the temporary images directory on CWD
* Fixed Qt installers not able to use enter/return key to navigate
* Fixed the issue with Installers that should not pack .DS_Store files on OS X
* Fixed Parameter group not showing left image
* Fixed GTK choice parameter freezeing the installer on some circumstances
* Fixed registryDelete that should silently ignore deleting non-existing keys
* Fixed Tk password parameter retype field not accepting enter key to show next page

Version 5.4.14 (2008-11-07)
* Installers are now able to register Windows file extensions at the user
  scope
* New actions 'encodeBase64' and 'decodeBase64' allow to encode/decode any
  string using base64
* New 'componentTest' rule, allow to check the presence of a component on
  the current project
* Link creation now supports abort/retry/ignore
* Fixed issue in OS X case-sensitive HFS+ file systems
* Fix 'showProgressDialog' action for Linux 64-bit platforms
* POTENTIAL INCOMPATIBILITY:  Japanese language code is now 'ja' instead
  of the current 'jp'. Most customers will not be affected because 'jp'
  is also recognized automatically, but there may be an issue is you
  explicitly use variable 'installation_language_code' in you code.
* Fixed issue with 'registryGetKey' and 'registryGetMatch' not being
  case-insensitive in certain cases
* Fix missing text messages on OS X root installations
* Fix OS X firewall warning being thrown when running the installers
* New 'uninstallationLogFile' project property, allow to configure the
  path for the log file during uninstallation.
* Installers now show a meaningful error message when the specified
  encoding is incorrect or not supported
* New convenience 'isTrue' and 'isFalse' rules

Version 5.4.13 (2008-09-18)
* Significant performance improvement for Windows installers when a large
  number of small files are being installed
* Cancel button is now disabled during uninstallation
* Installers now preserve the working directory along its execution life
* Improved error message for corrupted installers on Qt Linux

Version 5.4.12 (2008-09-10)
* Windows userTest action now supports Windows domains
* New unattended UI mode minimalWithDialogs
* Action processTest now supports process names with spaces
* Windows shortcuts now support the comment property
* Fix an error when the path contained spaces in the file selection dialog
  on GTK mode
* Better display text-mode infoParameter pages
* Improved uninstaller startup time when containing large number of files

Version 5.4.11 (2008-08-08)
* Fixes incorrect LZMA decompression issue when installing a 32-bit
  installer on a 64-bit machine in 32-bit compatibility mode
* Project XML files now support Latin and Unicode encodings
* New 'debianCustomPackageDirectory' property, allow using customized DEB
  scripts
* Action 'createWindowsService' now allow configuring runas information
* Components can now specify custom language files
* Enhanced Desktop path retrieval on Windows and Linux
* Fixes visual error on GTK mode with 'infoParameter' inside a
  'parameterGroup'
* New 'urlDecode' action, converts to regular text an URL-encoded string
* Builder application now supports '--version' command line switch
* Improved error handling when a folder destination path is empty
* Language, Readme and License file paths can now contain installer
  variables at build time

Version 5.4.10 (2008-06-27)
* New built-in unzip action to uncompress external ZIP files
* Fixed word wrapping for the showInfo, showWarning, etc dialogs in Qt mode
* New encoding property for the substitute action, allows performing
  substitutions on files with non-ASCII encodings
* Fixed error when trying to perform LZMA compression on unsupported
  platforms (linux-x64)
* New exitCode property for the exit action
* New variable build_project_directory, contains the location of
  project XML file during build time
* Fix error on Solaris Intel installers when the variable HOME is not set
* Improved associateWindowsFileExtension, now it is able to update
  friendlyName property
* New action <removeWindowsFileAssociation>, allows unregistering a
  file extension on Windows
* New windowsIconIndex property for shortcuts, allow specifying the
  icon index for Windows shortcuts

Version 5.4.9 (2008-06-13)
* New overwritePolicy property controls the installer behavior when
  overwriting files. Possible values are onlyIfNewer, always, never.
* DEB packages now support the –purge option for uninstallation
* Fixed segmentation fault on Fedora Core 9 with GTK mode
* Fixed removeChoiceOption action, it was generating an error when the
  "options" property was set
* Fixed an error on OS X, "--help" info was not being shown when
  requireInstallationByRootUser was enabled

Version 5.4.8 (2008-05-29)
* New action getFreePort for selecting a free port from a given range.
* New action addUnixDesktopStartUpItem for executing set a program to be
  executed on a KDE or GNOME session start up
* RPM integration now available on AIX
* Improved the display of progress dialog on Suse 10
* Fixed component selection problem on InstallBuilder for Qt
* Corrected Readme file display on Windows
* Deb package generation is now available for Linux 64-bit
* General action lists can now be set also in components
* Fixed 64-bit RPM packages shipping 32-bit files issue
* Rollback directory can now contain variable substitutions
* Installers now return an appropriate error in all scenarios when an
  installation is canceled

Version 5.4.7 (2008-04-25)
* New showProgressDialog action, allows showing an indeterminate progress
  bar when executing actions
* New Relax-NG schema included with InstallBuilder
* New examples for actions, components and parameters included with
  InstallBuilder
* Ability to generate 64-bit RPM packages from a Linux-x64 machine
* Improved display of slide show images on GTK mode
* Fixed uninstaller creation when defaultUnixOwner and
  requireInstallationByRootUser are set
* Improved feedback to the user when an unknown error is detected during
  the postInstallationActionList
* Fixed an error on machine_ipaddr, it was returning multiple addresses
  on Windows on certain scenarios
* New addChoiceOptionsFromText action allows to obtain a list of options
  from a text source to be inserted into a choice parameter

Version 5.4.6 (2008-04-18)
* Improved file copying speed on UNIX machines
* New command line option --setvars allows to set the installer variable
  values on build time
* x64 bit registry on Windows is now fully supported
* removeFilesFromUninstaller now allows to specify folders
* Prevent going back in page sequence after installation progress screen
  has completed.
* Comments are now being preserved when the XML is edited from the GUI

Version 5.4.5 (2008-04-11)
* Improved left image display on GTK mode
* Fixed lack of focus of the installer's window on OS X 10.3 when run by 
  root
* Installation now continues after an error during a component's 
  postInstallationActionList
* Fixed delay when displaying the readme file on OS X
* Fixed an error on the demo project when executing the post installation
  script on OS X
* Fixed installer failure on Solaris SPARC when LANG environment variable
  set to some values
* Fixed iniFileSet action, now it preserves elements order and generates
  correct content
* Fixed an error when upgrading the uninstaller
* Fixed failure when selecting files tab on builder GUI mode
* Improvement of Portuguese and Brazilian-Portuguese translations
* New variable machine_fqdn that retrieves the Fully Qualified Domain
  Name

Version 5.4.4 (2008-03-21)
* Fixed a bug that caused most installations to be broken on OS X

Version 5.4.3 (2008-03-20)
* New unattendedModeUI property, allows to show a minimal progress bar during
  unattended installations
* Fixed text fallback mode for Qt installers on Unix when DISPLAY is not
  present
* New removeUnixService action, allows removing Unix services from the
  system
* Fixed display of errors on FreeBSD and other Unix versions when
  installation fails in the early stages
* New kill action, allows to kill processes on Windows, based on PID,
  name or path to executable
* Fixed an error that occurs when creating uninstaller on OS X from a
  read-only installer
* Installers now support packing files with Unicode characters on their names
* Most project properties can now be accessed as installer variables
  using "project.propertyName"

Version 5.4.2 (2008-03-07)
* New dirName action, allows to get a path's parent directory
* New unpackDirectory action, allows you extract a directory at any time
  during the installation
* New findFile action, allows to find a file or directory recursively
  from a path
* New removeLogFile property optionally removes the log file at the end of the
  installation
* New installer variables to easily retrieve current username, home directory
  and temp directory: user_home_directory, system_temp_directory and
  system_username
* New productComments, productContact properties for Add/Remove Programs
  on Windows
* CreateWindowsService now allows to configure dependencies on other services
* Fixed a crash in the Builder application when setting multiple
  platforms for a folder
* Fix several issues on unpackFile action
* Uninstaller title window is now configurable
* New Action List postBuildActionList, allows executing actions after
  an installer has been generated
* New createTimeStamp action
* Fixed an error on OS X installers in text mode when
  requireInstallationByRootUser is enabled
* Improvements on portTest rule on OS X, Solaris and FreeBSD to
  account for reuse flag
* substitute action now preserves the encoding of the files
* hostValidation action now also supports Linux
* Fixed choiceParameter not honoring the default property on Qt mode
* Fixed crash in installers running in FreeBSD with KDE installed

Version 5.4.1 (2008-01-22)
* Support for Linux 64-bit in InstallBuilder for Qt (tm)
* Fix crashes on license page in X-window on certain 64-bit Linux platforms

Version 5.4.0 (2008-01-18)
* Fixed an error on Mac OS X where installers were not able to run from
  read-only media
* Improved support for Japanese fonts on Windows
* Fixed validation of user passwords on Windows using the userTest rule
* New --installer-language switch that allows configuring the
  installation language
* Components now can have a pre uninstallation action list
* Fixed an error that prevented linkParameters from displaying

Version 5.3.0 (2007-12-28)
* Improved support for OS X Intel
* Added support for Linux on pSeries
* Builder application incorporates a new feature to automatically check
  for updates on startup
* A single installer now support all versions of OS X back to 10.2.
* Native authentication dialog on OS X for installers that require admin
  privileges
* userTest rule can now check for valid passwords on Windows
* Fix splash screen fade-out effect for OS X
* Fixed an error when showing a parameter group containing a password
  parameter. If a validation exception was raised, the UI would hang in
  Windows and OS X
* New property "installationLogFile" allows customizing the path where
  the log file will be saved if the installation is completed successfully
* Fix getFreeDiskSpace issue on IRIX

Version 5.3.0beta2 (2007-12-22)

Version 5.3.0beta1 (2007-12-03)

Version 5.2.0 (2007-11-16)
* Full Mac OS X 10.5 (Leopard) support
* Fixed a build error for Multiple Platform CDROMs when compress packed
  files setting is on and the contents span several discs
* Enforce that folders, parameters and components must contain a valid
  name element
* New property "windowsExecutableIcon" that allows customizing the
  installer executable icon on Windows
* New property "productDisplayIcon" that allows customizing the icon shown
  on Add/Remove Programs on Windows
* unpackFile action is now able to handle big files (more than 2 GB.)
* Action "startWindowsService" now includes a timeout property
* Fixed an error on the progress bar, now installer variables are properly
  substituted in the progress text
* Detailed error message on Windows when trying to quick build an installer
  executable that is in use
* If the user cancels the installation before any files were installed,
  the uninstallation progress bar is not shown
* New "stringModify" action, that allows different operations on text
* Fixed lack of window decoration in xwindow mode in Beryl (XGL)
* Better text word wrapping for GTK mode

Version 5.2.0beta1 (2007-11-06)

Version 5.1.1 (2007-10-09)
* New singleInstanceCheck rule to check if there is a previous instance of
the installer running
* Fix bug when serializing project file with enableRollback tag
* Fixed issue with InstallBuilder for Qt handling of temporary images on Windows

Version 5.1.0 (2007-10-03)
* Automatic rollback support for files overwritten during installation
* Allow customization of Start Menu / Desktop links for Current User or 
All Users on Windows
* Fix an error for text mode initialization on OS X
* Fix an error related to the Clipboard on Windows Qt mode
* Detect infinite recursion during variable substitution
* Fix an error when displaying 'required_diskspace' as part of a message

Version 5.0 (2007-09-20)
* New rule processTest to test for running processes (Windows, Linux, OSX)
* Use UAC guidelines for default locations for projects/ and output/ on Vista
* Fixed issue with fileExists handling of some Windows paths
* Fixed display issue of Quick builds in text mode
* Fixed issue with removeFilesFromUninstaller
* Improved documentations for actions

Version 5.0beta1 (2007-09-05)
* LZMA compression support on Windows, OS X and Linux x86.
* Improved installer startup speed.
* New action iniFileSet to create and modify .ini files
* New property splashScreenDelay increases the display time of the splash screen
* New getDiskUsage action calculates disk space for files and folders
* Windows installer now shows a graphical popup for error and help messages
* preBuildActionList is now executed in Quick Builds
* readyToInstallActionList is now available in generated RPMs
* Fixed bug on generated DEB packages removal
* Fixed RPM and DEB generation in InstallBuilder for Qt
* Installer variables are now case-insensitive

Version 4.5.3 (2007-07-31)
* Fixed bug in directory selection with Unicode characters on Windows
* Reduced uninstaller creation time on Unix platforms
* Fixed bug in uninstaller creation for upgrade installers
* Improved alignment of label on multi-parameter pages

Version 4.5.2 (2007-07-23)
* Fix error in start menu creation on Windows

Version 4.5.1 (2007-07-20)
* Ability to configure default owner and group for installed files
* New action to calculate md5 value for a given text
* Improved error notification on programs executed by the installer
* Ability to customize the length of values returned by generateRandomValue action
* Improved text mode interface for parameter groups
* Enhanced integration of upgrade installers with Add / Remove Programs on Windows
* Ability to access standard Windows shell folder locations using installer variables

Version 4.5.0 (2007-07-12)
* Improved readFile and writeFile actions to support different encodings.
* Ability to add and remove Fonts on Windows.
* New linkParameter allows display of actionable links
* Fix value of installer_directory variable on OS X
* Fix variable substitution on httpPost actions
* Allow quick reloading of files changed on disk
* Fix packing of files with resource forks on OS X
* Improved logging of actions

Version 4.4.0 (2007-06-13)
* Support for .DEB packaging generation.
* Platform support for FreeBSD 6 - 64 bits.
* New httpPost action, allows passing of parameters
* New 'scope' property for actions addDirectoryToPath and
removeDirectoryFromPath allows to set path only for current user
(Windows only).
* New 'askForConfirmation' property for password parameters allows
not having to verify password.
* Improved visualization for GTK and Win32 modes, as well as
crashes in some specific GTK configurations. 

Version 4.3.2 (2007-05-18)
* Upgrade installers will now track files added/removed and update 
  uninstaller appropriately
* Prevent components from having the same name
* License parameters now correctly handle relative file paths
* Fixed RPM progress meter when packaging file links
* Java autodetection can now specifically require a JDK
* Native dialogs on InstallBuilder for Qt Windows

Version 4.3.1 (2007-05-09)
* Fixed a number of OS X glitches on complex parameter pages.
* New actions addFilesToUninstaller and removeFilesFromUninstaller.
* registryGetMatch now can accept wildcards in the key.

Version 4.3.0 (2007-05-04)
* Improved Vista look and feel
* Quicklaunch shortcuts on Windows
* Russian language support
* Show password questions can now have a 'title' property
* Default language can be set to 'auto' for autodetection
* Fixed localization issues of installer buttons on Windows
* Improved visualization of complex parameter pages

Version 4.2.0 (2007-04-03)
* Ability to disable splash screen setting with disableSplashScreen property
* Added orientation property to group parameters
* Fix RPM generation in GUI mode
* Added support for Linux IA64, FreeBSD 6 and OpenBSD 3.x
* Add a showPasswordQuestion action
* Ability to modify startMenuGroupname at runtime
* New product URL and help links on Windows add Remove Program
* Fixed problems with Add/Remove Programs uninstallation on Vista
* New readmeFileList and licenseFileList to allow internationalization of
license and readme files
* New onErrorActionList for actions, that gets executed on failure
* Show explanation property for boolean parameters in text mode
* Allow changing ask property in parameters dinamically
* touchFile can take wildcards and has a new option createIfNotExists
* addDirectoryToPath action now has an insertAt option that can specify
where to add the path ('beginning' or 'end')
* Fixed issue with addDirectoryToPath action missing a carriage return when
manipulating csh configuration files
* Fixed certain actions being executed twice in pages with multiple parameters
* Fixed missing toolbar icons in Qt-mode file selection dialog
* Windows 2003 is now recognized as a separate OS
* Textmode pages now display titles


Version 4.1.0 (2007-01-22)
* New action to associate windows file extensions
* New --env option allows passing of environment variables to installers
* Fixed problem with --optionfile on Windows

Version 4.0.0 (2007-01-03)
* Support for RPM generation
* Support for Linux 64-bit in x86_64 architecture
* New parameter group allows displaying multiple parameters per page
* Japanese language support
* Reference documentation now included with installer
* New environment variable installation_language_code
* New action to manipulate choice parameter options
* New rule for testing the content of strings
* Desktop shortcuts on Unix now have an option to run in terminal mode
* Components can now have parameter lists, uninstall action lists
* Fixed GTK frontend crashes related to locale and input method settings
* Fixed compatibility issues with earlier versions of Windows 2000

Version 3.8.2  (2006-11-24)
* New initialization action list, which gets run before command line options
  are parsed
* New InstallBuilder for Qt (tm) front-end

Version 3.8.1  (2006-08-14)
* Choice parameter pages now can include a displayType attribute 
  ('combobox', 'radiobuttons') to control appearance 
* Timestamps are preserved for packed files
* Sanity check on images before packing
* Fix problem with text mode installers not correctly setting cli options
* Fix issue with relative output directory
* Fix issue of uninstaller of size 0 being created in update mode
* Fix issue in add shared library action

Version 3.8.0  (2006-07-28)
* Support for IBM zLinux (s390)
* Port checks no longer trigger third-party firewall software
* CDROM platforms parameter allow selection of target platforms
* Improved shortcut creation on Windows
* New rule for improved platform testing
* Ability to change window manager image for installer on Unix platforms
* New action to register shared DLLs on windows
* New action to delete entries from system path on uninstallation
* New action to write to property files
* Errors in post-installation will be noted, but not abort installation
* Fix focus problem on OSX choice parameter pages
* Fix issue with uninstaller creation when target directory is missing
* Improved reference documentation for actions, rules and parameters

Version 3.7.0  (2006-05-05)

* Parameter names may include now underscores
* Added nocase option to compare text option
* New rule grouping allows creation of complex rule sets
* Fix for secure creation of bitrock_installer.log


Version 3.6.0  (2006-03-22)

* Add user action can now take a home directory argument
* New environment variables my_hostame and my_ipaddr
* Shortcuts can now be conditionally installed using rule lists
* New license page parameter allows multiple license pages
* Fix get free disk space action on Mac OS X systems
* Fix for passing cli options on the Windows platform
* Fix for some start menu folders not removed during uninstallation
* Fixed fonts for license page

Version 3.5.1  (2006-01-20)

* New actions for adding, deleting users and groups on Unix platforms
* New action to wait a specified amount of time
* New rule to match user input on regular expressions
* New rule to check if a program actually exists in the system path
* New rule to check if a certain port is already in use
* The installer now issues a warning when there is already an instance of 
  the installer running

Version 3.5.0  (2006-01-03)

* Support for Irix, Solaris Intel
* Mac OS X versions now include support for 10.2 Jaguar
* Ability to execute actions during install as a different user (if running
  as root)
* Substitute actions now support 'exact' mode in addition to regular
  expressions
* Ability to set default installation mode
* Ability to compress CDROM contents
* Fix command-line quickbuild functionality
* Fix issue when starting installer with SELinux extensions enabled
* Fix issue when starting from directory path containing non-standard charset
* Fix problems with Start Menu Group names when installing multiple versions
over the same location in Windows.

Version 3.0.1  (2005-09-20)

* Fixed issue with <setInstallerVariableFromScriptOutput> adding an extra \n
* New <touchFile> action
* New command line options --enable-components, --disable-components
* New <postInstallationActionList> per components
* Fix issues when creating icons on Windows

Version 3.0  (2005-09-01)

* Mac OS X support
* RPM integration
* Component selection
* Address segfaults on Linux 2.6 when using gtk-qt-engine theme
* Fine-grained platform specification for folders
* Ability to change uninstaller name
* New actions for creating/renaming directories and setting runtime
  environment
* Ability to specify installation options in a separate file
  with --optionfile command line option
* Improved support for Japanese and Chinese
* Improved CDROM support

Version 2.6.1  (2005-07-04)

* Fix Solaris Sparc 10 runtime
* Fix <createBackupFile> action when destination is a directory
* Allow running multiple actions in the final installation page

Version 2.6.0  (2005-06-21)

* Support for HP-UX, AIX, FreeBSD 4.x
* Custom splash screen
* Support for CD-ROM installers that span multiple disks
* New rule : Compare text length.
* Enhanced action: createBackupFile
* Bugfix: Fix Solaris soft link creation.
* Bugfix: Fix problems when executing installer from UNC path on Windows

Version 2.5.0  (2005-05-23)

* New actions: copy/delete/backup files, get free disk space, run console
  program.
* Improved actions: adding environment variable and add directory to path
  now add support CSH/TCSH syntax. All actions now have an <abortOnError>
  property that will specify if an error while executing the action will
  abort the installation process.
* Can specify encoding for Readme, License files
* New environment variables : installer_root_install, installer_ui,
  installer_pathname.
* New installation options : show file unpacking progress, delete
  installer binary on exit, reboot required.
* String, File, Directory parameters now support : <allowEmptyValue>
  property.
* New rule : Compare values.

Version 2.4.1  (2005-04-12)

* New pre uninstallation action list
* New delete environment variable action
* Fix start menu shortcut creation on CDROM installers
* Fix issues when password field values contain special characters
* Fix naming of uninstaller file on Windows
* Fix boolean parameters not being translated on GTK frontend

Version 2.4.0  (2005-03-29)

* Linux PPC support
* FreeBSD support

Version 2.3.0  (2005-03-17)

* Improved startup and runtime speed for complex installers.
* JRE autodetection has option to prompt user for selection.
* New CDROM target platform.
* New rule to test file contents.
* New action to access properties files.
* Fix for saving relative paths on different volumes on Windows.
* Fix Solaris specific bugs in builder tool.

Version 2.2.0  (2005-02-02)

* Support for Solaris Sparc.
* German language support.
* New actions : Java(tm) JRE autodetection, get values from the Windows
registry, show error message to the user, set installer variable from
script output.
* New rules : file tests (exists, is writable, is empty, etc.)
* Ability to prompt for password during installation.
* Ability to detect Linux distribution flavor at runtime.
* Ability to execute actions before and after a page is shown to the user.
* Parameter validation using arbitrary actions.
* Uninstaller now uses correct language for performing uninstallations.

Version 2.1.0  (2004-11-07)

* Define custom installer pages and command line options
* New actions : run program, set installer variables, change file
ownership, create symbolic links.
* Rule-system for conditional evaluation of actions and page display
* Improved error reporting during development.
* Speed improvements in build times for large projects.
* Automatic backup of project files and manual changes autodetection.
* Ability to refer to system environment variables.
* Fix for uninstaller creation when installer is read-only.

Version 2.0  (2004-10-28)

* Improved Windows support
* Built-in actions for changing file permissions, performing substitutions,
  accessing Windows registry, file conversions, etc.
* Customization Start Menu Group name
* Fix for Tooltip on Windows shortcuts
* Fix for console applications on Windows shortcuts

Version 2.0 Beta 1  (2004-10-14)

* Windows support : builder tool, look-and-feel, start menu shortcuts,
add/remove installers from control panel, etc.
* Create URL and Document shortcuts
* Added Polish language support

Version 1.3.0 (2004-09-10)

* Added language support for Italian, French, Portuguese, Traditional
Chinese, Dutch, Valencian, Catalan, Estonian, Slovenian, Romanian,
Welsh. Thanks to the AMSN developers.
* Ability to create Shortcuts for URLs, documents, etc.
* Optionally save paths relative to project file to ease team development
* Added ${installation_langcode} environment variable
* Fixed error when closing README window with window manager button

Version 1.2.2 (2004-08-19)

* Added Brazilian Portuguese language support
* Fallback gracefully with old GTK releases
* Fix encoding problems with translations

Version 1.2.1 (2004-08-05)

* Multiple language support for the installer, bundled Spanish language.
* New uninstallerDirectory XML option to specify uninstaller location.

Version 1.2.0 (2004-07-12)

* Improved look and feel with customizable left image.
* Ability to run script previous to uninstallation.
* Optionally present post installation script result to user.
* Speed up uninstaller creation in projects with a large number of files.

Version 1.1.0 (2004-07-01)

* Improved packing/unpacking of big files (hundreds of MBs).
* Uninstaller program.
* New environment variables ${platform_name} ${platform_exec_suffix}.

Version 1.0.3 (2004-05-01)

* Can now handle symbolic file links and recreate them during installation.
* New wrapLicenseFileText XML option to control wrapping of license text.

Version 1.0.2 (2004-02-27)

* New environment variable ${platform_install_prefix} that contains the
  platform specific default installation prefix. On Unix, this means /opt
  when installing as the root user and the user's home directory when
  installing as a regular user.
* Solved issue with Gnome shortcuts when the provided installation path
  contains a reference to the user home directory using '~'

Version 1.0.1 (2004-02-20)

* Solved unattended mode issues when --prefix not specified.
* Solved GTK2 issues in unstable versions of Gentoo and Debian.

Version 1.0 (2004-02-09) 

* Initial release.


==========================================================================
                                Copyrights
==========================================================================

  Bitrock InstallBuilder Multiplatform

  Copyright (c) 2003-2009 Bitrock SL. All Rights Reserved.
 
  BitRock InstallBuilder uses a number of third party components, which
  have separate license agreements that can be found in the docs/
  directory of your installation.
  In the spirit of Open Source, we support these projects and contribute
  back to their development whenever possible.
 

==========================================================================
                           End of RELEASE NOTES
==========================================================================

