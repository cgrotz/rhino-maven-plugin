package de.skiptag.rhino;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

public class RhinoCompilerClassLoader {

    private URLClassLoader newLoader;

    public RhinoCompilerClassLoader(MavenProject project)
	    throws DependencyResolutionRequiredException, MalformedURLException {
	List runtimeClasspathElements = project.getRuntimeClasspathElements();
	URL[] runtimeUrls = new URL[runtimeClasspathElements.size()];
	for (int i = 0; i < runtimeClasspathElements.size(); i++) {
	    String element = (String) runtimeClasspathElements.get(i);
	    runtimeUrls[i] = new File(element).toURI().toURL();
	}
	newLoader = new URLClassLoader(runtimeUrls, Thread.currentThread()
		.getContextClassLoader());
    }

    public Class<?> forName(String name) throws ClassNotFoundException {
	return newLoader.loadClass(name);
    }

}
