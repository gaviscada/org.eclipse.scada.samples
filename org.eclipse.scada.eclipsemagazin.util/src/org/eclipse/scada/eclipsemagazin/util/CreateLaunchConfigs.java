package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class CreateLaunchConfigs {
	public static void main(String[] args) throws Exception {
		String path = "../org.eclipse.scada.eclipsemagazin.master";
		new CreateLaunchConfigs().run(path);
	}

	private void run(String path) throws IOException, JDOMException {
		List<File> profiles = new ProfileWalker().gather(new File(path));
		for (File file : profiles) {
			createLauncher(file);
		}
	}

	private void createLauncher(File file) throws JDOMException, IOException {
		StringBuilder sb = new StringBuilder();
		Document doc = new SAXBuilder().build(file.getAbsolutePath());
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("start")) {
				String s = el.getValue();
				sb.append(s);
				sb.append("@default:default,");
			}
		}
		doc = new SAXBuilder().build("Template.launch");
		boolean found = false;
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("stringAttribute")
					&& "workspace_bundles".equals(el.getAttributeValue("key"))) {
				el.setAttribute("value", sb.toString());
				found = true;
			}
		}
		if (!found) {
			Element el = new Element("stringAttribute");
			el.setAttribute("key", "workspace_bundles");
			el.setAttribute("value", sb.toString());
			doc.getRootElement().getChildren().add(el);
		}
		String[] elements = file.getCanonicalPath().split(File.separator);
		String service = elements[elements.length - 2];
		String node = elements[elements.length - 3];
		File lf = new File(file.getParentFile().getCanonicalPath(), service
				+ " on " + node + ".launch");
		new XMLOutputter().output(doc, new FileWriter(lf));
	}
}
