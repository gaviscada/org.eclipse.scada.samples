package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class CreateLaunchConfigs {

	private static final List<String> defaultBundles = new ArrayList<String>();
	static {
		defaultBundles.add("org.eclipse.equinox.console");
		defaultBundles.add("org.apache.felix.gogo.shell");
		defaultBundles.add("ch.qos.logback.core");
	}

	public static void main(String[] args) throws Exception {
		String path = "../org.eclipse.scada.eclipsemagazin.master";
		new CreateLaunchConfigs().run(path);
	}

	private void run(String path) throws IOException, JDOMException {
		List<File> profiles = new ProfileWalker().gather(new File(path));
		for (File file : profiles) {
			createLauncher(file, path);
		}
	}

	private void createLauncher(File file, String path) throws JDOMException,
			IOException {
		StringBuilder ws_bundles = new StringBuilder();
		StringBuilder target_bundles = new StringBuilder();
		Document doc = new SAXBuilder().build(file.getAbsolutePath());
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("start")) {
				String s = el.getValue();
				if (s.startsWith("org.eclipse.scada")) {
					ws_bundles.append(s);
					ws_bundles.append("@default:default,");
				} else {
					target_bundles.append(s);
					target_bundles.append("@default:default,");
				}
			}
		}
		for (String s : defaultBundles) {
			target_bundles.append(s);
			target_bundles.append("@default:default,");
		}
		doc = new SAXBuilder().build("Template.launch");
		boolean ws_found = false;
		boolean t_found = false;
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("stringAttribute")
					&& "workspace_bundles".equals(el.getAttributeValue("key"))) {
				el.setAttribute("value", ws_bundles.toString());
				ws_found = true;
			}
			if (el.getName().equals("stringAttribute")
					&& "target_bundles".equals(el.getAttributeValue("key"))) {
				el.setAttribute("value", target_bundles.toString());
				t_found = true;
			}
		}
		if (!ws_found) {
			Element el = new Element("stringAttribute");
			el.setAttribute("key", "workspace_bundles");
			el.setAttribute("value", ws_bundles.toString());
			doc.getRootElement().getChildren().add(el);
		}
		if (!t_found) {
			Element el = new Element("stringAttribute");
			el.setAttribute("key", "target_bundles");
			el.setAttribute("value", target_bundles.toString());
			doc.getRootElement().getChildren().add(el);
		}
		String[] elements = file.getCanonicalPath().split(File.separator);
		String service = elements[elements.length - 2];
		String node = elements[elements.length - 3];
		File lf = new File(file.getParentFile().getCanonicalPath(), service
				+ " on " + node + ".launch");

		String vmargs = "";
		vmargs += " -Dorg.eclipse.scada.ca.file.provisionJsonUrl=file://"
				+ file.getParentFile().getCanonicalPath() + File.separator
				+ "data.json";
		vmargs += " -Dorg.eclipse.scada.ca.file.root="
				+ new File(path).getCanonicalPath() + File.separator
				+ "_config";
		vmargs += " -Dlogback.configurationFile="
				+ new File(path).getCanonicalPath() + File.separator
				+ "logback.xml";
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("stringAttribute")
					&& "org.eclipse.jdt.launching.VM_ARGUMENTS".equals(el
							.getAttributeValue("key"))) {
				el.setAttribute("value", el.getAttributeValue("value") + vmargs);
			}
		}
		new XMLOutputter().output(doc, new FileWriter(lf));
	}
}
