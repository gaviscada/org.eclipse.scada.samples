Generate configuration
=========================

You need the [Eclipse SCADA Configurator][1] installed. 

Create configuration
-----------------------

Right click on the "default.recipe" file and select "Eclipse SCADA Configuration" -> "Run Recipe"

This will create (or delete and re-create) the "output" folder containing all configuration artifcats.

Run the master server
-----------------------

This project will create a master server instance for the node `localhost`. In order to run the instance:

* Ensure that the target platform in the file `EclipseSCADA012.target` is active
* Right click on the file `output/localhost/master/master.profile.xml` and use "Run as" -> "Run Equinox Application Profile"

[1]: http://marketplace.eclipse.org/content/eclipse-scada-configurator "Eclipse SCADA Configurator"
