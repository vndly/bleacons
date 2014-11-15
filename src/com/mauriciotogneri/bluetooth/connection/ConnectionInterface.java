package com.mauriciotogneri.bluetooth.connection;

public interface ConnectionInterface
{
	void onReceive(byte[] message);
	
	void onConnect();
	
	void onDisconnect();
}