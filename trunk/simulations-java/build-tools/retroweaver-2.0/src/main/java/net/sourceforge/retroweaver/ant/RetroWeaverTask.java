package net.sourceforge.retroweaver.ant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.retroweaver.RefVerifier;
import net.sourceforge.retroweaver.RetroWeaver;
import net.sourceforge.retroweaver.event.VerifierListener;
import net.sourceforge.retroweaver.event.WeaveListener;
import net.sourceforge.retroweaver.translator.NameSpace;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * An Ant task for running RetroWeaver on a set of class files.
 */
public class RetroWeaverTask extends Task {

	////////////////////////////////////////////////////////////////////////////////
	//	Constants and variables.

	/**
	 * The destination directory for processd classes, or <code>null</code> for in place
	 * processing.
	 */
	private File itsDestDir;

	/**
	 * Indicates if an error should cause the script to fail. Default to <code>true</code>.
	 */
	private boolean itsFailOnError = true;

	/**
	 * The set of files to be weaved.
	 */
	private final List<FileSet> itsFileSets = new ArrayList<FileSet>();

	private final List<DirSet> itsDirSets = new ArrayList<DirSet>();

	private String inputJar;

	private String outputJar;

	/**
	 * Indicates if classes should only be processed if their current version differ from the target version. Initially <code>true</code>.
	 */
	private boolean itsLazy = true;

	/**
	 * Indicates whether the generic signatures should be stripped. Default to <code>false</code>.
	 */
	private boolean stripSignatures;

	/**
	 * Indicates whether the custom retroweaver attributes should be stripped. Default to <code>false</code>.
	 */
	private boolean stripAttributes;

	/**
	 * Indicates if each processed class should be logged. Initially set to <code>false</code>.
	 */
	private boolean itsVerbose = false;

	/**
	 * The classpath to use to verify the weaved result
	 */
	private Path verifyClasspath;

	private boolean verify = true;

	/**
	 * The class file version number.
	 */
	private int itsVersion = 48;

	/**
	 * The class file version number.
	 */
	private static final Map<String, Integer> itsVersionMap = new HashMap<String, Integer>();

	/**
	 * Initialize the version map.
	 */
	static {
		itsVersionMap.put("1.2", 46);
		itsVersionMap.put("1.3", 47);
		itsVersionMap.put("1.4", 48);
		itsVersionMap.put("1.5", 49);
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Property accessors and mutators.

	/**
	 * Set the destination directory for processed classes. Unless specified the classes
	 * are processed in place.
	 * @param pDir The destination directory. 
	 */
	public void setDestDir(File pDir) {
		if (!pDir.isDirectory()) {
			throw new BuildException(
					"The destination directory doesn't exist: " + pDir,
					getLocation());
		}

		itsDestDir = pDir;
	}

	/**
	 * Specify if an error should cause the script to fail. Default to <code>true</code>.
	 *
	 * @param pFailOnError <code>true</code> to fail, <code>false</code> to keep going.
	 */
	public void setFailOnError(boolean pFailOnError) {
		itsFailOnError = pFailOnError;
	}

	/**
	 * Add a set of files to be weaved.
	 * @param pSet The fileset.
	 */
	public void addFileSet(FileSet pFileSet) {
		itsFileSets.add(pFileSet);
	}

	public void addDirSet(DirSet pFileSet) {
		itsDirSets.add(pFileSet);
	}

	/**
	 * Specify if classes should only be processed if their current version differ from the target version. Initially <code>true</code>.
	 * @param pLazy <code>true</code> for lazy processing.
	 */
	public void setLazy(boolean pLazy) {
		itsLazy = pLazy;
	}

	/**
	 * Set the source directory containing classes to process. This is a shortcut to
	 * using an embedded fileset with the specified base directory and which includes
	 * all class files.
	 * @param pDir The directory. 
	 */
	public void setSrcDir(File pDir) {
		FileSet fileSet = new FileSet();
		fileSet.setDir(pDir);
		fileSet.setIncludes("**/*.class");

		addFileSet(fileSet);
	}

	/**
	 * Specify if each processed class should be logged. Initially set to <code>false</code>.
	 * @param pVerbose <code>true</code> for verbose processing.
	 */
	public void setVerbose(boolean pVerbose) {
		itsVerbose = pVerbose;
	}

	/**
	 * Set the target class file version. Initially set to "1.4".
	 * @param target The JDK target version, e&nbsp;g "1.3". 
	 */
	public void setTarget(String target) {
		Integer v = itsVersionMap.get(target);
		if (v == null) {
			throw new BuildException("Unknown target: " + target, getLocation());
		}
		itsVersion = v;
	}

	/**
	 * Set the classpath to be used for verification.
	 * Retroweaver will report any references to fields/methods/classes which don't appear
	 * on refClassPath.
	 * @param classpath an Ant Path object containing the compilation classpath.
	 */
	public void setClasspath(Path classpath) {
		if (verifyClasspath == null) {
			verifyClasspath = classpath;
		} else {
			verifyClasspath.append(classpath);
		}
	}

	/**
	 * Gets the classpath to be used for verification.
	 * @return the class path
	 public Path getClasspath() {
	 return verifyClasspath;
	 }

	 /**
	 * Adds a path to the classpath.
	 * @return a class path to be configured
	 */
	public Path createClasspath() {
		if (verifyClasspath == null) {
			verifyClasspath = new Path(getProject());
		}
		return verifyClasspath.createPath();
	}

	/**
	 * Adds a reference to a classpath defined elsewhere.
	 * @param r a reference to a classpath
	 */
	public void setClasspathRef(Reference r) {
		createClasspath().setRefid(r);
	}

	/**
	 * Turn off verification if desired
	 * @return is verification enabled?
	 */
	public void setVerify(boolean newVerify) {
		verify = newVerify;
	}

	/**
	 * Turn off verification if desired
	 * @return is verification enabled?
	 */
	public boolean doVerify() {
		return verify;
	}

	/** NameSpace in translator package is immutable. Temporary values are
	 * stored using this Namespace class.
	 */
	public static final class Namespace {
		private String from;
		private String to;
		public String getFrom() { return from; }
		public void setFrom(String from) { this.from = from; }
		public String getTo() { return to; }
		public void setTo(String to) { this.to = to; }		
	}

	private List<Namespace> namespaces = new ArrayList<Namespace>();

	public Namespace createNameSpace() {
		Namespace n = new Namespace();
		namespaces.add(n);
		return n;
	}

	////////////////////////////////////////////////////////////////////////////////
	//	Operations.

	/**
	 * Run the RetroWeaver task.
	 * @throws BuildException If a build exception occurs.
	 */
	public void execute() throws BuildException {

		for (DirSet set : itsDirSets) {
			File baseDir = set.getDir(getProject());
			DirectoryScanner scanner = set.getDirectoryScanner(getProject());

			// create a non recursive file set for each included directory
			for (String fileName : scanner.getIncludedDirectories()) {
				FileSet fileSet = new FileSet();
				fileSet.setDir(new File(baseDir, fileName));
				fileSet.setIncludes("*.class");
				addFileSet(fileSet);
			}
		}

		//	Check arguments.

		boolean hasFileSet = !itsFileSets.isEmpty() || !itsDirSets.isEmpty();

		if (inputJar != null) {
			if (outputJar == null) {
				throw new BuildException("'outputjar' must be set.");
			}
			if (hasFileSet) {
				throw new BuildException(
						"'inputjar' is incompatible with filesets and dirsets");
			}
		} else if (!hasFileSet) {
			throw new BuildException(
					"Either attribute 'srcdir' or 'inputjar' must be used or atleast one fileset or dirset must be embedded.",
					getLocation());
		}
		//	Create and configure the weaver.

		RetroWeaver weaver = new RetroWeaver(itsVersion);
		weaver.setLazy(itsLazy);
		weaver.setStripSignatures(stripSignatures);
		weaver.setStripAttributes(stripAttributes);
		
		// Name space conversion
		List<NameSpace> l = new ArrayList<NameSpace>();
		for(Namespace n: namespaces) {
			l.add(new NameSpace(n.getFrom(), n.getTo()));
		}
		weaver.addNameSpaces(l);

		//	Set up a listener.
		weaver.setListener(new WeaveListener() {
			public void weavingStarted(String msg) {
				getProject().log(RetroWeaverTask.this, msg, Project.MSG_INFO);
			}

			public void weavingCompleted(String msg) {
				getProject().log(RetroWeaverTask.this, msg, Project.MSG_INFO);
			}

			public void weavingError(String msg) {
				getProject().log(RetroWeaverTask.this, msg, Project.MSG_ERR);
				throw new ExitStatusException("weaving error", 1);
			}

			public void weavingPath(String pPath) {
				if (itsVerbose) {
					getProject().log(RetroWeaverTask.this, "Weaving " + pPath,
							Project.MSG_INFO);
				}
			}
		});

		if (verifyClasspath != null && doVerify()) {

			List<String> refPath = new ArrayList<String>();

			for (String pathItem : verifyClasspath.list()) {
				refPath.add(pathItem);
			}
			if (itsDestDir != null) {
				refPath.add(itsDestDir.getPath());
			}

			RefVerifier rv = new RefVerifier(itsVersion, new EmptyVisitor(), refPath, new VerifierListener() {
				public void verifyPathStarted(String msg) {
					getProject().log(RetroWeaverTask.this, msg,
							Project.MSG_INFO);
				}

				public void verifyClassStarted(String msg) {
					if (itsVerbose) {
						getProject().log(RetroWeaverTask.this, msg, Project.MSG_INFO);
					}
				}

				public void acceptWarning(String msg) {
					getProject().log(RetroWeaverTask.this, msg, Project.MSG_WARN);
				}

				public void displaySummary(int warningCount) {
					String msg = "Verification complete, " + warningCount
							+ " warning(s).";
					getProject().log(RetroWeaverTask.this, msg,
							Project.MSG_WARN);

					if (itsFailOnError) {
						throw new ExitStatusException(Integer
								.toString(warningCount)
								+ " warning(s)", 1);
					}
				}
			});
			weaver.setVerifier(rv);
		}

		try {
			if (inputJar != null) {
				weaver.weaveJarFile(inputJar, outputJar);
			} else {
				//	Weave the files in the filesets.

				//	Process each fileset.
				String[][] fileSets = new String[itsFileSets.size()][];
				File[] baseDirs = new File[itsFileSets.size()];
				int i = 0;
				for (FileSet fileSet : itsFileSets) {
					//	Create a directory scanner for the fileset.
					File baseDir = fileSet.getDir(getProject());
					DirectoryScanner scanner = fileSet
							.getDirectoryScanner(getProject());
					fileSets[i] = scanner.getIncludedFiles();
					baseDirs[i++] = baseDir;
				}

				weaver.weave(baseDirs, fileSets, itsDestDir);
			}
		} catch (BuildException ex) {
			throw ex;
		} catch (Exception ex) {
			// unexpected exception
			throw new BuildException(ex, getLocation());
		}
	}

	/**
	 * @return Returns the inputJar.
	 */
	public String getInputJar() {
		return inputJar;
	}

	/**
	 * @param inputJar The inputJar to set.
	 */
	public void setInputJar(String inputJar) {
		this.inputJar = inputJar;
	}

	/**
	 * @return Returns the outputJar.
	 */
	public String getOutputJar() {
		return outputJar;
	}

	/**
	 * @param outputJar The outputJar to set.
	 */
	public void setOutputJar(String outputJar) {
		this.outputJar = outputJar;
	}

	/**
	 * @param stripSignatures The stripSignatures to set.
	 */
	public void setStripSignatures(boolean stripSignatures) {
		this.stripSignatures = stripSignatures;
	}

	/**
	 * @param stripAttributes The stripAttributes to set
	 */
	public void setStripAttributes(boolean stripAttributes) {
		this.stripAttributes = stripAttributes;
	}
}
