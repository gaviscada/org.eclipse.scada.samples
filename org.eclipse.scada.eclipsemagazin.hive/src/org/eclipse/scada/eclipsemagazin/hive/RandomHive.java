package org.eclipse.scada.eclipsemagazin.hive;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.scada.core.Variant;
import org.eclipse.scada.da.server.browser.common.FolderCommon;
import org.eclipse.scada.da.server.common.AttributeMode;
import org.eclipse.scada.da.server.common.DataItemInputCommon;
import org.eclipse.scada.da.server.common.impl.HiveCommon;
import org.eclipse.scada.utils.collection.MapBuilder;
import org.eclipse.scada.utils.concurrent.NamedThreadFactory;

public class RandomHive extends HiveCommon
{

    private final FolderCommon rootFolder;

    private ScheduledExecutorService scheduler = null;

    private final Random random = new Random ();

    public RandomHive ()
    {
        super ();

        // since it is possible to use a custom folder implementation
        // the root folder has to be explicitly set
        this.rootFolder = new FolderCommon ();
        setRootFolder ( this.rootFolder );
    }

    @Override
    public String getHiveId ()
    {
        return "org.eclipse.scada.eclipsemagazin.hive";
    }

    @Override
    protected void performStart () throws Exception
    {
        super.performStart ();

        // the executor is only used for the dataItem itself
        this.scheduler = Executors
                .newSingleThreadScheduledExecutor ( new NamedThreadFactory (
                        "org.eclipse.scada.eclipsemagazin.hive" ) );

        // create the item
        final RandomItem randomItem = new RandomItem ( "random" );
        // register item with hive (initializes lifecycle)
        registerItem ( randomItem );
        // add item to folder
        // the folder name does not have to be the same as the itemId!
        this.rootFolder.add ( "random", randomItem, null );
    }

    @Override
    protected void performStop () throws Exception
    {
        // just shut down scheduler
        if ( this.scheduler != null )
        {
            this.scheduler.shutdownNow ();
        }
        super.performStop ();
    }

    private class RandomItem extends DataItemInputCommon implements Runnable
    {

        public RandomItem ( final String name )
        {
            super ( name );
            // every second call run
            RandomHive.this.scheduler.scheduleAtFixedRate ( this, 0, 1, TimeUnit.SECONDS );
        }

        @Override
        public void run ()
        {
            // just update item with a new random value, also set current
            // timestamp
            updateData (
                    Variant.valueOf ( RandomHive.this.random.nextLong () ),
                    new MapBuilder<String, Variant> ()
                            .put ( "description",
                                    Variant.valueOf ( "a random long value" ) )
                            .put ( "timestamp",
                                    Variant.valueOf ( System.currentTimeMillis () ) )
                            .getMap (), AttributeMode.UPDATE );
        }
    }
}
