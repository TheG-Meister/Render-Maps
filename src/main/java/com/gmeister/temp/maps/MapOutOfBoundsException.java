package com.gmeister.temp.maps;

public class MapOutOfBoundsException extends RuntimeException
{
	
	private static final long serialVersionUID = 7514819415074435362L;
	
	public MapOutOfBoundsException()
	{}
	
	public MapOutOfBoundsException(String message)
	{ super(message); }
	
}