package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class ProfileWalker extends DirectoryWalker<File> {
	
	public List<File> gather(File startDirectory) throws IOException {
		List<File> results = new ArrayList<File>();
		walk(startDirectory, results);
		return results;
	}

	@Override
	protected void handleFile(File file, int depth, Collection<File> results)
			throws IOException {
		if (file.getName().endsWith(".profile.xml") && file.getAbsolutePath().contains("output")) {
			results.add(file);
		}
	}
}
