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

public class CreateLaunchConfigs
{

    private static final List<String> defaultBundles = new ArrayList<String> ();
    static
    {
        defaultBundles.add ( "org.eclipse.equinox.console" );
        defaultBundles.add ( "org.apache.felix.gogo.shell" );
        defaultBundles.add ( "ch.qos.logback.core" );
    }

    public static void main ( final String[] args ) throws Exception
    {
        final Path path = Paths.get ( "..", "org.eclipse.scada.eclipsemagazin.master" );
        new CreateLaunchConfigs ().run ( path );
    }

    private void run ( final Path path ) throws IOException, JDOMException
    {
        final List<Path> profiles = new ProfileWalker ().gather ( path );
        for ( final Path file : profiles )
        {
            createLauncher ( file, path );
        }
    }

    @SuppressWarnings ( "unchecked" )
    private void createLauncher ( final Path file, final Path path ) throws JDOMException,
            IOException
    {
        final StringBuilder ws_bundles = new StringBuilder ();
        final StringBuilder target_bundles = new StringBuilder ();
        Document doc = new SAXBuilder ().build ( file.toAbsolutePath ().toFile () );
        for ( final Element el : (List<Element>)doc.getRootElement ().getChildren () )
        {
            if ( el.getName ().equals ( "start" ) )
            {
                final String s = el.getValue ();
                if ( s.startsWith ( "org.eclipse.scada" ) )
                {
                    ws_bundles.append ( s );
                    ws_bundles.append ( "@default:default," );
                }
                else
                {
                    target_bundles.append ( s );
                    target_bundles.append ( "@default:default," );
                }
            }
        }
        for ( final String s : defaultBundles )
        {
            target_bundles.append ( s );
            target_bundles.append ( "@default:default," );
        }
        String vmargs = "";
        for ( final Element el : (List<Element>)doc.getRootElement ().getChildren () )
        {
            if ( el.getName ().equals ( "property" ) )
            {
                final String key = el.getAttributeValue ( "key" );
                final String value = el.getContent ( 0 ).getValue ();
                if ( key.contains ( "exportUri" ) )
                {
                    vmargs += " -D" + key + "=" + value;
                }
            }
        }

        doc = new SAXBuilder ().build ( "Template.launch" );
        boolean ws_found = false;
        boolean t_found = false;
        for ( final Element el : (List<Element>)doc.getRootElement ().getChildren () )
        {
            if ( el.getName ().equals ( "stringAttribute" )
                    && "workspace_bundles".equals ( el.getAttributeValue ( "key" ) ) )
            {
                el.setAttribute ( "value", ws_bundles.toString () );
                ws_found = true;
            }
            if ( el.getName ().equals ( "stringAttribute" )
                    && "target_bundles".equals ( el.getAttributeValue ( "key" ) ) )
            {
                el.setAttribute ( "value", target_bundles.toString () );
                t_found = true;
            }
        }
        if ( !ws_found )
        {
            final Element el = new Element ( "stringAttribute" );
            el.setAttribute ( "key", "workspace_bundles" );
            el.setAttribute ( "value", ws_bundles.toString () );
            doc.getRootElement ().getChildren ().add ( el );
        }
        if ( !t_found )
        {
            final Element el = new Element ( "stringAttribute" );
            el.setAttribute ( "key", "target_bundles" );
            el.setAttribute ( "value", target_bundles.toString () );
            doc.getRootElement ().getChildren ().add ( el );
        }
        final String service = file.getName ( file.getNameCount () - 2 ).toString ();
        final String node = file.getName ( file.getNameCount () - 3 ).toString ();
        final Path lf = Paths.get ( path.toString (), service + " on " + node
                + ".launch" );

        vmargs += " -Dorg.eclipse.scada.ca.file.provisionJsonUrl="
                + Paths.get ( file.getParent ().toAbsolutePath ().toString (),
                        "data.json" ).toUri ();
        vmargs += " -Dorg.eclipse.scada.ca.file.root="
                + new File ( path.toString () ).getCanonicalPath () + File.separator
                + "_config_" + service;
        vmargs += " -Dlogback.configurationFile="
                + new File ( path.toString () ).getCanonicalPath () + File.separator
                + "logback.xml";
        vmargs += " -Dorg.eclipse.scada.sec.provider.plain.property.data="
                + "admin:admin12:|interconnect:interconnect12:";
        for ( final Element el : (List<Element>)doc.getRootElement ().getChildren () )
        {
            if ( el.getName ().equals ( "stringAttribute" )
                    && "org.eclipse.jdt.launching.VM_ARGUMENTS".equals ( el
                            .getAttributeValue ( "key" ) ) )
            {
                el.setAttribute ( "value", el.getAttributeValue ( "value" ) + vmargs );
            }
        }
        new XMLOutputter ().output ( doc, new FileWriter ( lf.toFile () ) );
    }
}
