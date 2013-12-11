function initTextValue ()
{
	if ( controller.getProperty ( "bold" ) != null )
		{
			controller.getElement ( "label" ).setFontBold ( true );
		}
		
	controller.getElement ( "label" ).setToolTip ( controller.getProperty ( "tooltip" ) );
	
	controller.registerItem ( "A", controller.getProperty ( "item" ), controller.getProperty("connection"));
	
}

function updateValue ( itemName )
{
	setColor ( controller.getElement ("left" ) );
	setColor ( controller.getElement ("right" ) );
	
	var format = controller.getProperty ( "format", "%.1f" );
	
	var value = data.getPrimaryValue (itemName).asDouble ( null );
	
	var scale = controller.getProperty ("scale" );
	
	if ( scale != null && scale != "" )
		value = value * new Number ( scale );
		
	if ( value == null )
	{
		value = controller.getProperty ( "nullString", "" );
		controller.getElement("label").setText ( value );
	}
	else
	{
		// set text
		controller.getElement("label").setText ( java.lang.String.format ( format, [value] ) );
	}
}

function onClick ()
{
	openItemDetails ( controller.getProperty ( "connection" ), controller.getProperty ("item" ) );
}

function setColor ( element )
{
	// set background
	element.setBackgroundColor ( getBackgroundStyle ( data ) );
}
