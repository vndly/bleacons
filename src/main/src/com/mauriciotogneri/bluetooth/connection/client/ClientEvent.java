package com.mauriciotogneri.bluetooth.connection.client;

public interface ClientEvent
{
	void onReceive(byte[] message);
	
	void onConnect();
	
	void onErrorConnecting();
	
	void onDisconnect();
}