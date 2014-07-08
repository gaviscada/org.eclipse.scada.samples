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

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

public class Application implements IApplication
{

    private SampleSetup setup;

    @Override
    public Object start ( final IApplicationContext context ) throws Exception
    {
        this.setup = new SampleSetup ();
        this.setup.run ();
        return null;
    }

    @Override
    public void stop ()
    {
        try
        {
            this.setup.dispose ();
        }
        catch ( final Exception e )
        {
            throw new RuntimeException ( e );
        }
    }

}
