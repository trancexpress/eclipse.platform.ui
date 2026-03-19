package org.eclipse.search.tests.filesearch;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.osgi.service.prefs.BackingStoreException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import org.eclipse.search.core.text.TextSearchEngine;
import org.eclipse.search.core.text.TextSearchMatchAccess;
import org.eclipse.search.core.text.TextSearchRequestor;
import org.eclipse.search.internal.core.SearchCorePlugin;
import org.eclipse.search.internal.core.text.PatternConstructor;
import org.eclipse.search.internal.core.text.TextSearchVisitor;
import org.eclipse.search.tests.ResourceHelper;
import org.eclipse.search.ui.text.FileTextSearchScope;

public class PerformanceSearchTest {

	private static final String FORBIDDEN_FILE_PREFIX= "forbidden_";

	private record TestResult(IFile file, int offset, int length) {
	}

	private static class TestResultCollector extends TextSearchRequestor {

		private final boolean parallel;

		private final List<TestResult> result;

		private TestResultCollector(boolean parallel) {
			this.parallel= parallel;
			if (parallel) {
				result= Collections.synchronizedList(new ArrayList<>());
			} else {
				result= new ArrayList<>();
			}
		}

		private TestResult[] getResults() {
			return result.toArray(new TestResult[result.size()]);
		}

		@Override
		public boolean canRunInParallel() {
			return parallel;
		}

		@Override
		public boolean acceptPatternMatch(TextSearchMatchAccess match) throws CoreException {
			result.add(new TestResult(match.getFile(), match.getMatchOffset(), match.getMatchLength()));
			return true;
		}
	}

	@Test
	public void testSimpleFilesSerial() throws Exception {
		runTest(10_000, 10, true, true);
	}

	private static void runTest(int n, int m, boolean parallel, boolean sessionProperty) throws Exception {
		System.out.println("STARTING TEST n=" + n + " m=" + m + " parallel=" + parallel + " sessionProperty=" + sessionProperty);
		String searchString= "hello";
		String projectName= "performance-test-project";
		try {
			IProject project= prepareProject(n, m, projectName, searchString);
			if (sessionProperty) {
				setForbiddenSearchEnabled(true);
				long s= System.currentTimeMillis();
				IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
				root.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						if (resource.getName().startsWith(FORBIDDEN_FILE_PREFIX)) {
							setForbiddenSearchSessionProperty(resource);
						}
						return true;
					}
				});
				System.out.println("Setting session property took: " + (System.currentTimeMillis() - s) + " ms");
			}
			TestResultCollector collector= new TestResultCollector(parallel);
			runTest(n, m, sessionProperty, project, collector, searchString);
		} finally {
			ResourceHelper.deleteProject(projectName);
			setForbiddenSearchEnabled(false);
		}
		System.out.println("DONE");
	}


	private static void runTest(int n, int m, boolean sessionProperty, IProject project, TestResultCollector collector, String searchString) throws Exception {
		Pattern searchPattern= PatternConstructor.createPattern(searchString, false, true);

		FileTextSearchScope scope= FileTextSearchScope.newSearchScope(new IResource[] {project}, (String[]) null, false);
		long s= System.currentTimeMillis();
		TextSearchEngine.create().search(scope, collector, searchPattern, null);
		System.out.println("Search took: " + (System.currentTimeMillis() - s) + " ms");

		int expectedMatches= m;
		if (!sessionProperty) {
			expectedMatches+= n * m;
		}
		TestResult[] results= collector.getResults();
		assertEquals(expectedMatches, results.length, "Number of total results");

		assertMatches(results, expectedMatches, searchString);
	}

	private static IProject prepareProject(int n, int m, String projectName, String searchString) throws CoreException {
		IProject project= ResourceHelper.createProject(projectName);
		IPath path= new Path("tst");
		for (int i= 0; i < m; ++i) {
			IFolder folder= project.getFolder(path);
			folder.create(true, false, null);
			path= path.append("tst" + i);
			for (int j= 0; j < n; ++j) {
				IFile file= folder.getFile(FORBIDDEN_FILE_PREFIX + "_file_" + i + "_" + j + ".txt");
				file.create(new String("test content " + searchString + " " + i + " " + j + System.lineSeparator()).getBytes(), IResource.FORCE, null);
			}
			IFile matchFile= folder.getFile("test_match_file_" + i + ".txt");
			matchFile.create(new String("prefix " + searchString + " suffix").getBytes(), IResource.FORCE, null);
		}
		return project;
	}

	private static void assertMatches(TestResult[] results, int expectedCount, String string) throws Exception {
		int k= 0;
		for (TestResult curr : results) {
			String content= curr.file.readString();
			k++;
			assertEquals(string, content.substring(curr.offset, curr.offset + curr.length), "Wrong positions");
		}
		assertEquals(expectedCount, k, "Number of results in file");
	}

	private static void setForbiddenSearchSessionProperty(IResource resource) throws CoreException {
		resource.setSessionProperty(TextSearchVisitor.FORBIDDEN_KEY, "true");
	}

	private static void setForbiddenSearchEnabled(boolean value) throws BackingStoreException {
		IEclipsePreferences node= InstanceScope.INSTANCE.getNode(SearchCorePlugin.PLUGIN_ID);
		node.putBoolean("enable_search_forbidden_files", value);
		node.flush();
	}
}
