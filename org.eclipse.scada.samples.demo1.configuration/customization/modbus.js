if ( item.getName () ==  "ES.DEMO.ARDUINO001.LIGHT.V" )
{
	MODBUS.doExport ( properties.get("modbus.port"), 1, 0, MODBUS.UNSIGNED_INT_16.scaled ( 10.0 ) );
	MODBUS.doExport ( properties.get("modbus.port"), 1, 6, MODBUS.DOUBLE );
}
