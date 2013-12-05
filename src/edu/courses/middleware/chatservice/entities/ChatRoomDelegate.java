package edu.courses.middleware.chatservice.entities;

/**
 * The ChatRoom's delegate interface.
 * 
 * @author Jonas Michel
 *
 */
public interface ChatRoomDelegate {

	/**
	 * Called when a chat room closes.
	 * 
	 * @param room
	 *            the chat room.
	 */
	public void close(IChatRoom room);

	/**
	 * Called when a chat room detects a client that has died unexpectedly.
	 * 
	 * @param client
	 *            a dead chat client.
	 */
	public void cleanup(IChatClient client);
}
