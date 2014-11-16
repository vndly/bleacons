package com.mauriciotogneri.bluetooth.connection.exceptions;

public class ConnectionException extends Exception
{
	private static final long serialVersionUID = 4812409626602352577L;
	
	public ConnectionException(Throwable throwable)
	{
		super(throwable);
	}
}