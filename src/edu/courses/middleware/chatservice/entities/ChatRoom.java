package edu.courses.middleware.chatservice.entities;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A ChatRoom extends the chat service's Entity class and implements the remote
 * IChatRoom interface. ChatProviders may create and register any number of
 * ChatRooms. A ChatRoom is hosted by the ChatProvider that creates it. When a
 * ChatProvider creates a ChatRoom its ChatClient is automatically added as the
 * room's first participant. When all ChatClients have left a ChatRoom it
 * automatically shuts down and deregisters itself from the chat service.
 * 
 * @author Jonas Michel
 * 
 */
public class ChatRoom extends Entity implements IChatRoom {
	private static final long serialVersionUID = 6150046694998603404L;

	/** The chat room's name. */
	private String name;

	/**
	 * Whether or not the chat room is currently open. This prevents chat room
	 * shutdown from being interrupted.
	 */
	private boolean open;
	
	/** The chat room's creation time. */
	private long created;
	
	/** The number of chat messages exchanged in the chat room. */
	private int messageCount;

	/** We make callbacks on the delegate. */
	private ChatRoomDelegate delegate;

	/** Holds the chat room's participants. */
	private Set<IChatClient> clients;

	public ChatRoom(String name, ChatRoomDelegate delegate)
			throws RemoteException {
		super();

		// configure the RMI security manager
		System.setSecurityManager(new RMISecurityManager());

		this.name = name;
		open = true;
		created = System.currentTimeMillis();
		messageCount = 0;

		this.delegate = delegate;

		clients = new HashSet<IChatClient>();
	}

	/* IEntity Interface Implementation */

	@Override
	public String getName() throws RemoteException {
		return name;
	}

	@Override
	public Map<String, String> getInfo() throws RemoteException {
		Map<String, String> info = new HashMap<String, String>();

		// populate the info map
		info.put("Name", name);
		info.put("Created", new Date(created).toString());
		info.put("# Messages", Integer.toString(messageCount));
		info.put(
				"Time Open",
				Long.toString((System.currentTimeMillis() - created) / 1000 / 60)
						+ " minutes");
		synchronized (clients) {
			info.put("# Participants", Integer.toString(clients.size()));
		}

		return info;
	}

	/* IChatRoom Interface Implementation */

	@Override
	public boolean join(IChatClient client) throws RemoteException {
		synchronized (clients) {
			if (clients.contains(client) || !open)
				return false;

			clients.add(client);
		}

		// alert participants that a new client has joined
		talk(client, "(joined)");

		return true;
	}

	@Override
	public boolean talk(IChatClient client, String message)
			throws RemoteException {
		Set<IChatClient> clientsCopy = null;
		synchronized (clients) {
			clientsCopy = new HashSet<IChatClient>(clients);
		}

		String sender = null;
		try {
			sender = client.getName();
		} catch (RemoteException e) {
			sender = "someone";
		}
		for (IChatClient c : clientsCopy) {
			try {
				c.deliver(name, sender, message);
			} catch (RemoteException e) {
				// the client is unreachable
				leave(c);

				// cleanup this client who has died unexpectedly
				delegate.cleanup(c);
			}
		}

		messageCount++;
		return true;
	}

	@Override
	public boolean leave(IChatClient client) throws RemoteException {
		synchronized (clients) {
			if (!clients.contains(client))
				return false;

			clients.remove(client);

			if (clients.size() == 0) {
				open = false;
				delegate.close(this);
			}
		}

		// alert participants that the client has left
		talk(client, "(left)");

		return true;
	}

	@Override
	public void empty() throws RemoteException {
		open = false;

		// notify participating chat clients that the chat room is closing
		Set<IChatClient> clientsCopy = null;
		synchronized (clients) {
			clientsCopy = new HashSet<IChatClient>(clients);
		}

		for (IChatClient c : clientsCopy) {
			try {
				c.closed(name);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
