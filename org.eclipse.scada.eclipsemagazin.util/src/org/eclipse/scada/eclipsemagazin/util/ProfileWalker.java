package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class ProfileWalker extends DirectoryWalker<Path> {
	
	public List<Path> gather(Path startDirectory) throws IOException {
		List<Path> results = new ArrayList<Path>();
		walk(startDirectory.toFile(), results);
		return results;
	}

	@Override
	protected void handleFile(File file, int depth, Collection<Path> results)
			throws IOException {
		if (file.toString().endsWith(".profile.xml") && file.getAbsolutePath().toString().contains("output")) {
			results.add(file.toPath());
		}
	}
}
