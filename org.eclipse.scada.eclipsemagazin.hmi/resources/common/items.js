function prefixed ( localPart ) {
	if ( localPart === undefined || localPart == null )
		return controller.getProperty("prefix");
	else
		return controller.getProperty("prefix") + "." + localPart;
}