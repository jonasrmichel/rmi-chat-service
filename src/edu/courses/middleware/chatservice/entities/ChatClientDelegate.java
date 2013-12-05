package edu.courses.middleware.chatservice.entities;

import java.util.Map;

/**
 * The ChatClient's delegate interface.
 * 
 * @author Jonas Michel
 * 
 */
public interface ChatClientDelegate {

	/**
	 * Called to retrieve the provider's currently registered chat rooms.
	 * 
	 * @return a map of chat rooms.
	 */
	public Map<String, IChatRoom> getHostedChatRooms();

	/**
	 * Called to chat rooms the chat provider is currently participating in.
	 * 
	 * @return a map of chat rooms.
	 */
	public Map<String, IChatRoom> getJoinedChatRooms();

	/**
	 * Called when a chat message is delivered to the chat client.
	 * 
	 * @param room
	 *            the chat room name.
	 * @param sender
	 *            the sender's screen name.
	 * @param message
	 *            the chat message.
	 */
	public void deliver(String room, String sender, String message);

	/**
	 * Called when a chat room closes.
	 * 
	 * @param name
	 *            the chat room that closed.
	 */
	public void closed(String name);
}
