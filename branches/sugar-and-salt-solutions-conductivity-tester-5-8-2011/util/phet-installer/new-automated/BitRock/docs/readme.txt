==========================================================================
                          RELEASE NOTES FOR
                Bitrock InstallBuilder Multiplatform 5.1.1
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

  Please consult the BitRock InstallBuilder user guide for detailed 
  step-to-step instructions on installation and usage. This guide can be
  accessed online at http://www.bitrock.com and is included as part of the
  installation in the docs/ directory.

==========================================================================
                            Technical Support
==========================================================================

  Upon purchase and registration of a Bitrock InstallBuilder software
  license you are entitled to maintenance and support services.
  Bitrock Technical Support is available by e-mail and from our website.

  For technical support for BitRock InstallerBuilder, go to:
  http://www.bitrock.com/support/


==========================================================================
                               ChangeLog
==========================================================================
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

  Copyright (c) 2003-2007 Bitrock SL. All Rights Reserved.
 
  BitRock InstallBuilder uses a number of third party components, which
  have separate license agreements that can be found in the docs/
  directory of your installation.
  In the spirit of Open Source, we support these projects and contribute
  back to their development whenever possible.
 

==========================================================================
                           End of RELEASE NOTES
==========================================================================

