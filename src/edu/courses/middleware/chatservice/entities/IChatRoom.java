package edu.courses.middleware.chatservice.entities;

import java.rmi.RemoteException;

/**
 * This is the remote interface exposed by ChatRooms in addition to the IEntity
 * interface.
 * 
 * @author Jonas Michel
 * 
 */
public interface IChatRoom extends IEntity {

	/**
	 * Allows a new chat client to join the chat room.
	 * 
	 * @param client
	 *            the chat client that wishes to join the chat room.
	 * @return true if the chat client successfully joined the chat room.
	 * @throws RemoteException
	 */
	public boolean join(IChatClient client) throws RemoteException;

	/**
	 * Allows a joined client to send a message to the chat room's participants.
	 * 
	 * @param client
	 *            the chat client sending the message.
	 * @param message
	 *            the message to send.
	 * @return true if the message was successfully delivered to the chat room's
	 *         participants.
	 * @throws RemoteException
	 */
	public boolean talk(IChatClient client, String message)
			throws RemoteException;

	/**
	 * Allows a joined chat client to leave the chat room.
	 * 
	 * @param client
	 *            the chat client that wishes to leave the chat room.
	 * @return true if the chat client successfully left the chat room.
	 * @throws RemoteException
	 */
	public boolean leave(IChatClient client) throws RemoteException;

	/**
	 * Called to gracefully close the chat room.
	 */
	public void empty() throws RemoteException;
}
