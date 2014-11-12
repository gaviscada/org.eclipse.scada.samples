
var addr = properties.getProperty ( "iec.informationAddress" );
if ( addr != null )
{
	IEC60870.doExportFloat ( "1-1/" + addr );
}