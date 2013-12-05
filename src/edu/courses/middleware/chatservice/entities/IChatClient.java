package edu.courses.middleware.chatservice.entities;

import java.rmi.RemoteException;

/**
 * This is the remote interface exposed by ChatClients in addition to the
 * IEntity interface.
 * 
 * @author Jonas Michel
 * 
 */
public interface IChatClient extends IEntity {

	/**
	 * Delivers a chat room message to the chat client.
	 * 
	 * @param room
	 *            the chat room name.
	 * @param sender
	 *            the sender's screen name.
	 * @param message
	 *            the chat message.
	 * @throws RemoteException
	 */
	public void deliver(String room, String sender, String message)
			throws RemoteException;

	/**
	 * Notifies a chat client that a chat room has closed.
	 * 
	 * @param room
	 *            the chat room name.
	 * @throws RemoteException
	 */
	public void closed(String room) throws RemoteException;
}
