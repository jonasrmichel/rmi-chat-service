package edu.courses.middleware.chatservice.ui;

/**
 * This is a helpful class for managing and displaying messages received in a
 * chat room.
 * 
 * @author Jonas Michel
 * 
 */
public class ChatMessage {
	/** The chat room name. */
	private String room;

	/** The screen name of the chat client who sent this message. */
	private String sender;

	/** The chat message's content. */
	private String message;

	public ChatMessage(String room, String sender, String message) {
		this.room = room;
		this.sender = sender;
		this.message = message;
	}

	/**
	 * Defines what a chat message looks like when displayed in the CLI.
	 * 
	 * @return
	 */
	public String toChatString() {
		return "[" + room + "] " + sender + " says: " + message;
	}

	@Override
	public String toString() {
		return "ChatMessage [room=" + room + ", sender=" + sender
				+ ", message=" + message + "]";
	}

}
