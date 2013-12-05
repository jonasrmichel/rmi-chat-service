package edu.courses.middleware.chatservice.entities;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A ChatClient extends the chat service Entity class and implements the remote
 * IChatClient interface. Each ChatProvider hosts a single registered ChatClient
 * instance. A ChatClient may join and participate in any number of ChatRooms.
 * 
 * @author Jonas Michel
 * 
 */
public class ChatClient extends Entity implements IChatClient {
	private static final long serialVersionUID = -377524500039013920L;

	/** The chat client's screen name. */
	private String name;

	/** The chat client's creation time. */
	private long created;

	/** We make callbacks on the delegate. */
	private ChatClientDelegate delegate;

	public ChatClient(String name, ChatClientDelegate delegate)
			throws RemoteException {
		super();

		// configure the RMI security manager
		System.setSecurityManager(new RMISecurityManager());

		this.name = name;
		created = System.currentTimeMillis();

		this.delegate = delegate;
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
		info.put(
				"Time Online",
				Long.toString((System.currentTimeMillis() - created) / 1000 / 60)
						+ " minutes");
		info.put("# Chat Rooms Hosting",
				Integer.toString(delegate.getHostedChatRooms().size()));
		info.put("# Chat Rooms Joined",
				Integer.toString(delegate.getJoinedChatRooms().size()));

		return info;
	}

	/* IChatClient Interface Implementation */

	@Override
	public void deliver(String room, String sender, String message)
			throws RemoteException {
		delegate.deliver(room, sender, message);
	}

	@Override
	public void closed(String room) throws RemoteException {
		delegate.closed(room);
	}

}
