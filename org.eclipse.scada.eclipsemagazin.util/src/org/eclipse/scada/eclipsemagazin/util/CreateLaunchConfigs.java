package org.eclipse.scada.eclipsemagazin.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
		Path path = Paths.get("..", "org.eclipse.scada.eclipsemagazin.master");
		new CreateLaunchConfigs().run(path);
	}

	private void run(Path path) throws IOException, JDOMException {
		List<Path> profiles = new ProfileWalker().gather(path);
		for (Path file : profiles) {
			createLauncher(file, path);
		}
	}

	@SuppressWarnings("unchecked")
	private void createLauncher(Path file, Path path) throws JDOMException,
			IOException {
		StringBuilder ws_bundles = new StringBuilder();
		StringBuilder target_bundles = new StringBuilder();
		Document doc = new SAXBuilder().build(file.toAbsolutePath().toFile());
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
		String vmargs = "";
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("property")) {
				String key = el.getAttributeValue("key");
				String value = el.getContent(0).getValue();
				if (key.contains("exportUri")) {
					vmargs += " -D" + key + "=" + value;
				}
			}
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
		String service = file.getName(file.getNameCount() - 2).toString();
		String node = file.getName(file.getNameCount() - 3).toString();
		Path lf = Paths.get(path.toString(), service + " on " + node
				+ ".launch");

		vmargs += " -Dorg.eclipse.scada.ca.file.provisionJsonUrl="
				+ Paths.get(file.getParent().toAbsolutePath().toString(),
						"data.json").toUri();
		vmargs += " -Dorg.eclipse.scada.ca.file.root="
				+ new File(path.toString()).getCanonicalPath() + File.separator
				+ "_config";
		vmargs += " -Dlogback.configurationFile="
				+ new File(path.toString()).getCanonicalPath() + File.separator
				+ "logback.xml";
		vmargs += " -Dorg.eclipse.scada.sec.provider.plain.property.data="
				+ "admin:admin12:|interconnect:interconnect12:";
		for (Element el : (List<Element>) doc.getRootElement().getChildren()) {
			if (el.getName().equals("stringAttribute")
					&& "org.eclipse.jdt.launching.VM_ARGUMENTS".equals(el
							.getAttributeValue("key"))) {
				el.setAttribute("value", el.getAttributeValue("value") + vmargs);
			}
		}
		new XMLOutputter().output(doc, new FileWriter(lf.toFile()));
	}
}
