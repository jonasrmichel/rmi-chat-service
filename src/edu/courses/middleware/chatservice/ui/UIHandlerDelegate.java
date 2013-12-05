package edu.courses.middleware.chatservice.ui;

import java.util.Map;

import edu.courses.middleware.chatservice.entities.IChatRoom;

/**
 * The UIHandler's delegate interface.
 * 
 * @author Jonas Michel
 * 
 */
public interface UIHandlerDelegate {

	/**
	 * Called to shutdown the ChatProvider, closing and releasing all chat
	 * service resources and entities.
	 */
	public void quit();

	/**
	 * Called to register a chat client entity with the provided screen name.
	 * 
	 * @param name
	 *            the desired chat client screen name.
	 * @return true if successful, false otherwise.
	 */
	public boolean registerChatClient(String name);

	/**
	 * Called to register a chat room entity with the provided name.
	 * 
	 * @param name
	 *            the desired chat room name.
	 * @return true if successful, false otherwise.
	 */
	public boolean registerChatRoom(String name);

	/**
	 * Called to deregister a chat room entity with the provided name.
	 * 
	 * @param name
	 *            the chat room's name.
	 * @return true if successful, false otherwise.
	 */
	public boolean deregisterChatRoom(String name);

	/**
	 * Called to retrieve information about a specific registered entity.
	 * 
	 * @param name
	 *            the registered entity's name.
	 * @return the entity's info map.
	 */
	public Map<String, String> getEntityInfo(String name);

	/**
	 * Called to retrieve the chat service's currently registered chat rooms.
	 * 
	 * @return the chat service's chat rooms.
	 */
	public Map<String, IChatRoom> getChatRooms();

	/**
	 * Called to obtain the ChatProvider's locally hosted chat rooms.
	 * 
	 * @return the chat provider's locally hosted chat rooms.
	 */
	public Map<String, IChatRoom> getHostedChatRooms();

	/**
	 * Called to obtain the chat rooms that have been joined by the
	 * ChatProvider's ChatClient.
	 * 
	 * @return the ChatProvider's ChatClient's joined chat rooms.
	 */
	public Map<String, IChatRoom> getJoinedChatRooms();

	/**
	 * Called to join a chat room.
	 * 
	 * @param name
	 *            a chat room's name.
	 * @return true if successful, false otherwise.
	 */
	public boolean joinChatRoom(String name);

	/**
	 * Called to send a message in a chat room.
	 * 
	 * @param name
	 *            the name of the chat room.
	 * @param message
	 *            the chat message's content.
	 * @return true if successful, false otherwise.
	 */
	public boolean talkChatRoom(String name, String message);

	/**
	 * Called to leave a chat room that has been joined.
	 * 
	 * @param name
	 *            the name of the chat room to leave.
	 * @return true if sucessful, false otherwise.
	 */
	public boolean leaveChatRoom(String name);

}
