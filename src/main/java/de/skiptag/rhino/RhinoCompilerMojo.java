package de.skiptag.rhino;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Goal which compiles all files in a given directory to Java class files in the
 * projects output directory
 * 
 * A lot of code is copied from {@link org.mozilla.javascript.tools.jsc.Main}
 * and adapted to fit Maven.
 * 
 * @goal compile
 * 
 * @phase compile
 */
public class RhinoCompilerMojo extends AbstractMojo {
	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${basedir}/src/main/js"
	 * @required
	 */
	private File sourceDirectory;

	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.outputDirectory}"
	 * @required
	 */
	private File outputDirectory;

	/**
	 * Character Encoding to use
	 * 
	 * @parameter expression="${project.build.sourceEncoding}"
	 * @optional
	 */
	private String characterEncoding = "UTF-8";

	/**
	 * Specifies the language version to compile with. The string versionNumber
	 * must be one of 100, 110, 120, 130, 140, 150, 160, or 170. See JavaScript
	 * Language Versions(https://developer.mozilla.org/en/Rhino/Overview#
	 * JavaScript_Language_Versions) for more information on language versions.
	 * 
	 * @parameter
	 * @optional
	 */
	private int languageVersion = -1;

	/**
	 * Optimizes at level optLevel, which must be an integer between -1 and 9.
	 * See Optimization (https://developer.mozilla.org/en/Rhino/Optimization)
	 * for more details. If optLevel is greater than zero, -debug may not be
	 * specified.
	 * 
	 * @parameter
	 * @optional
	 */
	private int optLevel = -1;

	/**
	 * Does not save the source in the class file. Functions and scripts
	 * compiled this way cannot be decompiled. This option can be used to avoid
	 * distributing source or simply to save space in the resulting class file.
	 * 
	 * @parameter
	 * @optional
	 */
	private boolean nosource = false;

	/**
	 * Specifies that debug information should be generated. May not be combined
	 * with optimization at an optLevel greater than zero.
	 * 
	 * @parameter
	 * @optional
	 */
	private boolean debug = true;

	/**
	 * TODO What is this Parameter doing
	 * 
	 * @parameter
	 * @optional
	 */
	private boolean observeInstructionCount = true;

	/**
	 * Specify the class name used for main method implementation. The class
	 * must have a method matching public static void main(Script sc, String[]
	 * args).
	 * 
	 * @parameter
	 * @optional
	 */
	private String mainMethodClass = null;

	/**
	 * Specifies that a java class extending the Java class java-class-name
	 * should be generated from the incoming JavaScript source file. Each global
	 * function in the source file is made a method of the generated class,
	 * overriding any methods in the base class by the same name.
	 * 
	 * @parameter
	 * @optional
	 */
	private String superclass;

	/**
	 * Specifies that a java class implementing the Java interface
	 * java-intf-name should be generated from the incoming JavaScript source
	 * file. Each global function in the source file is made a method of the
	 * generated class, implementing any methods in the interface by the same
	 * name.
	 * 
	 * @parameter
	 * @optional
	 */
	private List<String> interfaces = Lists.newArrayList();

	// Internal Members

	private RhinoCompilerErrorReporter reporter;
	private CompilerEnvirons compilerEnv;
	private ClassCompiler compiler;

	public void execute() throws MojoExecutionException {
		File f = outputDirectory;

		if (!f.exists()) {
			f.mkdirs();
		}

		reporter = new RhinoCompilerErrorReporter(getLog());
		compilerEnv = new CompilerEnvirons();
		compilerEnv.setErrorReporter(reporter);
		compiler = new ClassCompiler(compilerEnv);
		processOptions();
		processSource(sourceDirectory);

	}

	/**
	 * Parse arguments.
	 * 
	 */
	public void processOptions() {
		compilerEnv.setGenerateDebugInfo(false); // default to no symbols
		if (languageVersion != -1) {
			compilerEnv.setLanguageVersion(languageVersion);
		}
		if (optLevel != -1) {
			compilerEnv.setOptimizationLevel(optLevel);
		}
		compilerEnv.setGeneratingSource(nosource);
		compilerEnv.setGenerateDebugInfo(debug);
		compilerEnv.setGenerateObserverCount(observeInstructionCount);
		if (mainMethodClass != null) {
			compiler.setMainMethodClass(mainMethodClass);
		}
		if (superclass != null) {
			Class<?> superClass;
			try {
				superClass = Class.forName(superclass);
				compiler.setTargetExtends(superClass);
			} catch (ClassNotFoundException e) {
				getLog().error(e);
			}
		}
		if (!interfaces.isEmpty()) {
			List<Class<?>> list = new ArrayList<Class<?>>();
			for (String interfaceName : interfaces) {
				try {
					list.add(Class.forName(interfaceName));
				} catch (ClassNotFoundException e) {
					getLog().error(e);
				}
			}
			Class<?>[] implementsClasses = list.toArray(new Class<?>[list.size()]);
			compiler.setTargetImplements(implementsClasses);
		}
	}

	/**
	 * Compile JavaScript source.
	 * 
	 */
	public void processSource(File srcDir) {
		checkState(srcDir.isDirectory());
		for (File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				processSource(file);
			} else {
				processSourceFile(file);
			}
		}
	}

	private void processSourceFile(File file) {
		String source = readSource(file);
		if (source == null)
			return;

		String name = file.getName();
		String nojs = name.substring(0, name.length() - 3);
		String mainClassName = getClassName(nojs);

		String relativePath = sourceDirectory.toURI().relativize(file.getParentFile().toURI()).toString();

		String targetPackage = relativePath.replaceAll("/", ".");
		if (targetPackage.length() != 0) {
			mainClassName = targetPackage + "." + mainClassName;
		}
		mainClassName = mainClassName.replaceAll("\\.\\.", ".");
		getLog().info("compile " + name + " to " + mainClassName);
		Object[] compiled = compiler.compileToClassFiles(source, file.getName(), 1, mainClassName);
		if (compiled == null || compiled.length == 0) {
			return;
		}

		for (int j = 0; j != compiled.length; j += 2) {
			String className = (String) compiled[j];
			byte[] bytes = (byte[]) compiled[j + 1];
			File outfile = getOutputFile(outputDirectory, className);
			try {
				FileOutputStream os = new FileOutputStream(outfile);
				try {
					os.write(bytes);
				} finally {
					os.close();
				}
			} catch (IOException ioe) {
				addFormatedError(ioe.toString());
			}
		}
	}

	private String readSource(File f) {
		String absPath = f.getAbsolutePath();
		if (!f.isFile()) {
			addError("msg.jsfile.not.found", absPath);
			return null;
		}
		try {
			return (String) SourceReader.readFileOrUrl(absPath, true, characterEncoding);
		} catch (FileNotFoundException ex) {
			addError("msg.couldnt.open", absPath);
		} catch (IOException ioe) {
			addFormatedError(ioe.toString());
		}
		return null;
	}

	private File getOutputFile(File parentDir, String className) {
		String path = className.replace('.', File.separatorChar);
		path = path.concat(".class");
		File f = new File(parentDir, path);
		String dirPath = f.getParent();
		if (dirPath != null) {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
		return f;
	}

	/**
	 * Verify that class file names are legal Java identifiers. Substitute
	 * illegal characters with underscores, and prepend the name with an
	 * underscore if the file name does not begin with a JavaLetter.
	 */

	String getClassName(String name) {
		char[] s = new char[name.length() + 1];
		char c;
		int j = 0;

		if (!Character.isJavaIdentifierStart(name.charAt(0))) {
			s[j++] = '_';
		}
		for (int i = 0; i < name.length(); i++, j++) {
			c = name.charAt(i);
			if (Character.isJavaIdentifierPart(c)) {
				s[j] = c;
			} else {
				s[j] = '_';
			}
		}
		return new String(s).trim();
	}

	private void addError(String messageId, String arg) {
		String msg;
		if (arg == null) {
			msg = ToolErrorReporter.getMessage(messageId);
		} else {
			msg = ToolErrorReporter.getMessage(messageId, arg);
		}
		addFormatedError(msg);
	}

	private void addFormatedError(String message) {
		reporter.error(message, null, -1, null, -1);
	}
}
