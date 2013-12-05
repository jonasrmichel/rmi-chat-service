package edu.courses.middleware.chatservice;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import edu.courses.middleware.chatservice.entities.ChatClient;
import edu.courses.middleware.chatservice.entities.ChatClientDelegate;
import edu.courses.middleware.chatservice.entities.ChatRoom;
import edu.courses.middleware.chatservice.entities.ChatRoomDelegate;
import edu.courses.middleware.chatservice.entities.IChatClient;
import edu.courses.middleware.chatservice.entities.IChatRoom;
import edu.courses.middleware.chatservice.ui.ChatMessage;
import edu.courses.middleware.chatservice.ui.UIHandler;
import edu.courses.middleware.chatservice.ui.UIHandlerDelegate;

/**
 * A ChatProvider creates and hosts chat service entities (chat clients, chat
 * rooms) and provides a command line interface to the user.
 * 
 * A ChatProvider interacts with a ChatRegistry instance through a remote
 * IChatRegistry handle obtained from the Java RMI registry. Note that a
 * ChatProvider takes as an input argument the hostname of the machine on which
 * the ChatRegistry is running.
 * 
 * @author Jonas Michel
 * 
 */
public class ChatProvider implements UIHandlerDelegate, ChatRoomDelegate,
		ChatClientDelegate {
	/** The UI handler. */
	private UIHandler uiHandler;

	/** The chat service's registry. */
	private IChatRegistry chatRegistry;

	/** The chat provider's representative chat client. */
	private IChatClient chatClient;

	/** Holds the chat rooms hosted by the chat provider. */
	private Map<String, IChatRoom> hostedChatRooms;

	/** Holds the chat rooms joined by the chat provider. */
	private Map<String, IChatRoom> joinedChatRooms;

	public ChatProvider(String host) {
		try {
			// locate the remote RMI registry
			Registry registry = LocateRegistry.getRegistry(host);

			// obtain a chat service registry stub
			chatRegistry = (IChatRegistry) registry
					.lookup(ChatRegistry.CHAT_REGISTRY_URL);

			hostedChatRooms = new ConcurrentHashMap<String, IChatRoom>();
			joinedChatRooms = new ConcurrentHashMap<String, IChatRoom>();

			// kick off the UI handler
			uiHandler = new UIHandler(this);
			uiHandler.start();

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * A ChatProvider uses this method to leave and proactively deregister a
	 * non-responsive chat room.
	 * 
	 * @param name
	 *            the name of the non-responsive chat room entity.
	 */
	private void cleanupDeadJoinedChatRoom(String name) {
		System.out.println("This chat room [" + name
				+ "] appears to have closed unexpectedly");
		System.out
				.println("Closing and leaving the chat room (sorry for the inconvenience)");

		try {
			// deregister the chat room
			chatRegistry.deregister(joinedChatRooms.get(name));
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// leave the chat room
		joinedChatRooms.remove(name);
	}

	/* UIHandlerDelegate Interface Implementation */

	@Override
	public void quit() {
		try {
			// leave all joined chat rooms
			for (Map.Entry<String, IChatRoom> entry : joinedChatRooms
					.entrySet()) {
				entry.getValue().leave(chatClient);
			}

			// close all hosted chat rooms
			for (Map.Entry<String, IChatRoom> entry : hostedChatRooms
					.entrySet()) {
				entry.getValue().empty();
				chatRegistry.deregister(entry.getValue());
				hostedChatRooms.remove(entry.getKey());
			}

			// deregister our chat client
			chatRegistry.deregister(chatClient);

			System.out.println("Goodbye");
			System.exit(0);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

	}

	@Override
	public boolean registerChatClient(String name) {
		try {
			// create the chat client
			chatClient = new ChatClient(name, this);

			// attempt to register the chat client with the chat registry
			return chatRegistry.register(chatClient);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean registerChatRoom(String name) {
		try {
			// create a new chat room
			IChatRoom chatRoom = new ChatRoom(name, this);

			// attempt to register it with the chat service's registry
			boolean success = chatRegistry.register(chatRoom);

			if (!success)
				return false;

			// keep track that we're hosting this chat room
			hostedChatRooms.put(name, chatRoom);

			// add ourselves as the first chat room participant
			return joinChatRoom(name);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean deregisterChatRoom(String name) {
		if (!hostedChatRooms.containsKey(name))
			return false;

		try {
			IChatRoom chatRoom = hostedChatRooms.get(name);

			// leave the chat room if necessary
			if (joinedChatRooms.containsKey(name)) {
				chatRoom.leave(chatClient);
				joinedChatRooms.remove(name);
			}

			// notify all participants that the chat room is closing
			chatRoom.empty();

			// deregister the chat room from the chat service
			boolean success = chatRegistry
					.deregister(hostedChatRooms.get(name));

			if (!success)
				return false;

			// keep track that we've deregistered this chat room
			hostedChatRooms.remove(name);

			return true;

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Map<String, String> getEntityInfo(String name) {
		try {
			return chatRegistry.getInfo(name);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, IChatRoom> getChatRooms() {
		try {
			return chatRegistry.getChatRooms();

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Map<String, IChatRoom> getHostedChatRooms() {
		return hostedChatRooms;
	}

	@Override
	public Map<String, IChatRoom> getJoinedChatRooms() {
		return joinedChatRooms;
	}

	@Override
	public boolean joinChatRoom(String name) {
		try {
			// retrieve the remote chat room
			IChatRoom chatRoom = chatRegistry.getChatRooms().get(name);

			if (chatRoom == null)
				return false;

			// attempt to join the chat room
			boolean success = chatRoom.join(chatClient);

			if (!success)
				return false;

			// keep track that we've joined this chat room
			joinedChatRooms.put(name, chatRoom);

			return true;

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean talkChatRoom(String name, String message) {
		try {
			if (!joinedChatRooms.containsKey(name))
				return false;

			return joinedChatRooms.get(name).talk(chatClient, message);

		} catch (RemoteException e) {
			// the chat room has died unexpectedly, clean it up
			cleanupDeadJoinedChatRoom(name);

			return true;
		}
	}

	@Override
	public boolean leaveChatRoom(String name) {
		try {
			if (!joinedChatRooms.containsKey(name))
				return false;

			boolean success = joinedChatRooms.get(name).leave(chatClient);

			if (!success)
				return false;

			joinedChatRooms.remove(name);

			return true;

		} catch (RemoteException e) {
			// the chat room has died unexpectedly, clean it up
			cleanupDeadJoinedChatRoom(name);

			return true;
		}
	}

	/* ChatRoomDelegate Interface Implementation */

	@Override
	public void close(IChatRoom room) {
		try {
			// note: the chat room is already empty at this point
			// (that's why this method is begin called)
			chatRegistry.deregister(room);

			hostedChatRooms.remove(room.getName());

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void cleanup(IChatClient client) {
		try {
			chatRegistry.deregister(client);

		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* ChatClientDelegate Interface Implementation */

	@Override
	public void deliver(String room, String sender, String message) {
		uiHandler.deliver(new ChatMessage(room, sender, message));
	}

	@Override
	public void closed(String name) {
		// alert the user that the chat room has closed
		uiHandler.deliver(new ChatMessage(name, "admin",
				"(this chat room has been closed)"));

		// remove this chat room from our map of joined rooms
		joinedChatRooms.remove(name);
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Please provide the hostname of the machine "
					+ "running the ChatRegistry (e.g., localhost)");
			System.exit(1);
		}

		new ChatProvider(args[0]);
	}
}
