package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.DirectoryWalker;

public class CreateLaunchConfigs {
	public static void main(String[] args) throws Exception {
		String path = "../org.eclipse.scada.eclipsemagazin.master";
		new CreateLaunchConfigs().run(path);
	}

	private void run(String path) throws IOException {
		List<File> profiles = new ProfileWalker().gather(new File(path));
		System.out.println(profiles);
	}
}
