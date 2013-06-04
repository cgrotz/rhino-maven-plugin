package de.skiptag.rhino;

import org.apache.maven.plugin.logging.Log;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class RhinoCompilerErrorReporter implements ErrorReporter {

	private Log log;

	public RhinoCompilerErrorReporter(Log log) {
		this.log = log;
	}

	public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
		log.warn(message + "(Sourcename:" + sourceName + ", line:" + line + ", " + lineSource + ", lineOffset:"
				+ lineOffset);
	}

	public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
		log.error(message + "(Sourcename:" + sourceName + ", line:" + line + ", " + lineSource + ", lineOffset:"
				+ lineOffset);
	}

	public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource,
			int lineOffset) {
		EvaluatorException exp = new EvaluatorException(message, sourceName, line, lineSource, lineOffset);
		return exp;
	}
}
