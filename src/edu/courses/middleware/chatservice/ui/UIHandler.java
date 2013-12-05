package edu.courses.middleware.chatservice.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import edu.courses.middleware.chatservice.entities.IChatRoom;

/**
 * The UIHandler class represents the command line user interface exposed by a
 * ChatProvider.
 * 
 * @author Jonas Michel
 * 
 */
public class UIHandler extends Thread {

	/** The command line reader. */
	protected BufferedReader br;

	/** We make callbacks on the delegate. */
	private UIHandlerDelegate delegate;

	/** Holds a map of commands and their explanations. */
	protected Map<String, String> commands;

	/** ChatProvider user commands. */
	public static final String HELP_COMMAND = "h";
	public static final String QUIT_COMMAND = "q";
	public static final String REGISTER_COMMAND = "n";
	public static final String DEREGISTER_COMMAND = "d";
	public static final String GET_INFO_COMMAND = "i";
	public static final String GET_ROOMS_COMMAND = "r";
	public static final String GET_HOSTED_ROOMS_COMMAND = "m";
	public static final String GET_JOINED_ROOMS_COMMAND = "p";
	public static final String JOIN_COMMAND = "j";
	public static final String TALK_COMMAND = "t";
	public static final String LEAVE_COMMAND = "l";

	public UIHandler(UIHandlerDelegate delegate) {
		super();

		br = new BufferedReader(new InputStreamReader(System.in));
		this.delegate = delegate;

		commands = new HashMap<String, String>();
		commands.put(HELP_COMMAND, "Display this help message");
		commands.put(QUIT_COMMAND, "Quit");

		initCommands();
	}

	@Override
	public void run() {
		// register the user's chat client
		boolean registered = false;
		while (!registered) {
			try {
				// prompt the user for a screen name
				System.out.print("Enter a screen name: ");
				String name = br.readLine().trim();

				// attempt to register the chat client
				registered = delegate.registerChatClient(name);

				if (!registered)
					System.out.println("Sorry, the screen name [" + name
							+ "] is already in use");

				else
					System.out.println("Welcome " + name);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// main run loop
		while (true) {
			try {
				System.out.print("Enter command: ");

				String command = br.readLine();

				synchronized (this) {
					if (command.equals("") || command.equals(" ")) {
						// do nothing, the user hit enter to clear alerts
					} else if (!commands.containsKey(command)) {
						System.out.println("Sorry, " + command
								+ " is not a valid command");
						showHelp();

					} else if (command.equals(HELP_COMMAND)) {
						showHelp();

					} else if (command.equals(QUIT_COMMAND)) {
						delegate.quit();

					} else {
						process(command);

					}

					this.notify();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Fills the commands map.
	 */
	public void initCommands() {
		commands.put(REGISTER_COMMAND, "Register a new chat room");
		commands.put(DEREGISTER_COMMAND, "Deregister one of your chat rooms");
		commands.put(GET_INFO_COMMAND, "Get info about a registered entity");
		commands.put(GET_ROOMS_COMMAND, "View all registered chat rooms");
		commands.put(GET_HOSTED_ROOMS_COMMAND, "View your hosted chat rooms");
		commands.put(GET_JOINED_ROOMS_COMMAND, "View your joined chat rooms");
		commands.put(JOIN_COMMAND, "Join a chat room");
		commands.put(TALK_COMMAND, "Send a chat message");
		commands.put(LEAVE_COMMAND, "Leave a chat room");
	}

	/**
	 * Displays a delivered chat message in the UI.
	 * 
	 * @param message
	 *            a chat message.
	 */
	public void deliver(ChatMessage message) {
		System.out.println(); // make some space
		System.out.println("\t" + message.toChatString());
	}

	/**
	 * Processes validated user input.
	 * 
	 * @param command
	 *            a valid user command.
	 */
	public void process(String command) {
		if (command.equals(REGISTER_COMMAND)) {
			doRegister();

		} else if (command.equals(DEREGISTER_COMMAND)) {
			doDeregister();

		} else if (command.equals(GET_INFO_COMMAND)) {
			doGetInfo();

		} else if (command.equals(GET_ROOMS_COMMAND)) {
			doGetChatRooms();

		} else if (command.equals(GET_HOSTED_ROOMS_COMMAND)) {
			doGetHostedChatRooms();

		} else if (command.equals(GET_JOINED_ROOMS_COMMAND)) {
			doGetJoinedChatRooms();

		} else if (command.equals(JOIN_COMMAND)) {
			doJoin();

		} else if (command.equals(TALK_COMMAND)) {
			doTalk();

		} else if (command.equals(LEAVE_COMMAND)) {
			doLeave();

		} else {
			showHelp();
		}
	}

	/**
	 * Performs chat service entity registration.
	 */
	public void doRegister() {
		try {
			System.out.print("Enter desired chat room name: ");

			String name = br.readLine().trim();

			if (delegate.registerChatRoom(name))
				System.out.println("Chat room [" + name
						+ "] successfully registered.");

			else
				System.out
						.println("Sorry, a chat room with that name already exists.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Performs chat service entity deregistration.
	 */
	public void doDeregister() {
		if (delegate.getHostedChatRooms().size() == 0) {
			System.out.println("You are not hosting any chat rooms");
			return;
		}

		try {
			System.out.print("Enter chat room name: ");
			String name = br.readLine().trim();

			if (delegate.deregisterChatRoom(name))
				System.out.println("Chat room [" + name
						+ "] successfully deregistered");

			else
				System.out.println("Unable to deregister chat room [" + name
						+ "]");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves and displays information about a specific chat service entity.
	 */
	public void doGetInfo() {
		try {
			System.out.print("Enter entity name: ");
			String name = br.readLine().trim();

			Map<String, String> info = delegate.getEntityInfo(name);

			if (info == null) {
				System.out
						.println("There is no registered entity with that name");
				return;
			}

			System.out.println(info.toString());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves and displays a list of the chat service's registered chat
	 * rooms.
	 */
	public void doGetChatRooms() {
		Map<String, IChatRoom> chatRooms = delegate.getChatRooms();

		if (chatRooms.size() == 0) {
			System.out.println("There are no registered chat rooms");
			return;
		}

		System.out.println("Registered chat rooms:");
		System.out.println("[(*) indicates you are hosting the chat room]");
		System.out.println("[(**) indicates you have joined the chat room]");
		for (String chatRoom : chatRooms.keySet()) {
			System.out.print("\t" + chatRoom);

			if (delegate.getHostedChatRooms().containsKey(chatRoom))
				System.out.print(" (*)");

			if (delegate.getJoinedChatRooms().containsKey(chatRoom))
				System.out.print(" (**)");

			System.out.println();
		}
	}

	/**
	 * Displays the ChatProvider's locally hosted chat rooms.
	 */
	public void doGetHostedChatRooms() {
		Map<String, IChatRoom> chatRooms = delegate.getHostedChatRooms();

		if (chatRooms.size() == 0) {
			System.out.println("You are not hosting any chat rooms");
			return;
		}

		System.out.println("Your hosted chat rooms:");
		for (String chatRoom : chatRooms.keySet())
			System.out.println("\t" + chatRoom);
	}

	/**
	 * Displays the chat rooms that the ChatProvider's ChatClient has joined.
	 */
	public void doGetJoinedChatRooms() {
		Map<String, IChatRoom> chatRooms = delegate.getJoinedChatRooms();

		if (chatRooms.size() == 0) {
			System.out.println("You have not joined any chat rooms");
			return;
		}

		System.out.println("Your joined chat rooms:");
		for (String chatRoom : chatRooms.keySet())
			System.out.println("\t" + chatRoom);
	}

	/**
	 * Performs the necessary steps for joining a chat room.
	 */
	public void doJoin() {
		try {
			System.out.print("Enter chat room name: ");
			String name = br.readLine().trim();

			if (delegate.joinChatRoom(name))
				System.out.println("Successfully joined chat room [" + name
						+ "]");

			else
				System.out.println("Unable to join chat room [" + name + "]");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Performs the necessary steps for talking in a chat room.
	 */
	public void doTalk() {
		try {
			System.out.print("Enter chat room name: ");
			String name = br.readLine().trim();

			System.out.print("Enter chat message: ");
			String message = br.readLine().trim();

			if (!delegate.talkChatRoom(name, message))
				System.out
						.println("You have not joined a chat room with the name ["
								+ name + "]");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Performs the necessary steps for leaving a chat room.
	 */
	public void doLeave() {
		try {
			System.out.print("Enter chat room name: ");
			String name = br.readLine().trim();

			if (delegate.leaveChatRoom(name))
				System.out
						.println("Successfully left chat room [" + name + "]");

			else
				System.out.println("Unable to leave chat room [" + name + "]");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Displays the CLI's commands.
	 */
	public void showHelp() {
		StringBuilder sb = new StringBuilder();
		sb.append(System.getProperty("line.separator"));
		sb.append("ChatProvider help: you may enter one of the following commands"
				+ System.getProperty("line.separator"));

		for (Map.Entry<String, String> entry : commands.entrySet()) {
			sb.append("\t" + entry.getKey() + "\t" + entry.getValue()
					+ System.getProperty("line.separator"));
		}

		System.out.println(sb.toString());
	}

}
