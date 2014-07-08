/*******************************************************************************
 * Copyright (c) 2014 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.scada.examples.modbus.exporter;

import org.eclipse.scada.core.ConnectionInformation;
import org.eclipse.scada.da.server.exporter.modbus.ModbusExport;
import org.eclipse.scada.da.server.exporter.modbus.StaticModbusExport;
import org.eclipse.scada.da.server.exporter.modbus.StaticModbusExport.Builder;
import org.eclipse.scada.da.server.exporter.modbus.io.UnsignedShortIntegerType;
import org.eclipse.scada.da.server.ngp.Exporter;

public class SampleSetup
{
    private final SampleHive hive;

    private final Exporter ngpExporter;

    private boolean running = false;

    private final ModbusExport modbusExporter;

    public SampleSetup () throws Exception
    {
        this.hive = new SampleHive ();
        this.hive.start ();

        this.ngpExporter = new Exporter ( this.hive, ConnectionInformation.fromURI ( "da:ngp://0.0.0.0:2199" ) );
        this.ngpExporter.start ();

        final Builder builder = new StaticModbusExport.Builder ( this.hive );

        builder.addExport ( "mem1", 0, UnsignedShortIntegerType.INSTANCE );

        this.modbusExporter = builder.build ();
    }

    public void dispose () throws Exception
    {
        synchronized ( this )
        {
            if ( !this.running )
            {
                performStop ();
                return;
            }
            this.running = false;
            notifyAll ();
        }
    }

    private void performStop () throws Exception
    {
        this.ngpExporter.stop ();
        this.hive.stop ();
        this.modbusExporter.dispose ();
    }

    public void run () throws Exception
    {
        synchronized ( this )
        {
            this.running = true;
            try
            {
                while ( this.running )
                {
                    wait ();
                }
            }
            finally
            {
                this.running = false;
                performStop ();
            }
        }
    }
}
