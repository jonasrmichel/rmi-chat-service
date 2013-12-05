package edu.courses.middleware.chatservice;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import edu.courses.middleware.chatservice.entities.IChatRoom;
import edu.courses.middleware.chatservice.entities.IEntity;

/**
 * The ChatRegistry acts as a "meta" registry, an application-specific adapter
 * between ChatProviders and the Java RMI registry.
 * 
 * The ChatRegistry uses a locally running RMI registry. 
 * 
 * @author Jonas Michel
 * 
 */
public class ChatRegistry extends UnicastRemoteObject implements IChatRegistry {
	private static final long serialVersionUID = -4824956501661514974L;

	/** Holds registered chat entities. */
	private Map<String, IEntity> entities;

	/** The chat registry's global URL. */
	public static final String CHAT_REGISTRY_URL = "ChatRegistry";

	public ChatRegistry() throws RemoteException {
		super();

		entities = new HashMap<String, IEntity>();

		// configure the RMI security manager
		System.setSecurityManager(new RMISecurityManager());
		try {
			Naming.rebind(CHAT_REGISTRY_URL, this);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	/* IChatRegistry Interface Implementation */

	@Override
	public boolean register(IEntity entity) throws RemoteException {
		String name = entity.getName();

		System.out.println("Attempting to register entity [" + name + "]");

		if (entities.containsKey(name)) {
			System.out.println("Entity [" + name + "] already exists");

			try {
				// check if this entity is still alive
				entities.get(name).getName();

				System.out.println("Entity [" + name + "] is still alive");

				// yup, it's there
				return false;

			} catch (RemoteException e) {
				// the entity has died, release its resources
				System.out.println("Entity [" + name
						+ "] has died, releasing its resources");

				deregister(entity);
			}
		}

		System.out.println("Attempting to bind entity [" + name + "]");

		try {
			// bind the entity name to the remote entity object
			Naming.rebind(name, entity);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// keep track of the registry in the chat service
		entities.put(name, entity);

		System.out.println("Entity successfully registered [" + name + "]");

		return true;
	}

	@Override
	public boolean deregister(IEntity entity) throws RemoteException {
		String name = null;
		try {
			name = entity.getName();
		} catch (RemoteException e) {
			// the entity has died, reverse lookup the entity name
			for (Map.Entry<String, IEntity> entry : entities.entrySet()) {
				if (!entry.getValue().equals(entity))
					continue;

				name = entry.getKey();
				break;
			}

		}

		System.out.println("Attempting to deregister entity [" + name + "]");

		if (name == null || !entities.containsKey(name)) {
			System.out.println("There is no registered entity [" + name + "]");
			return false;
		}

		System.out.println("Attempting to unbind entity [" + name + "]");

		try {
			// unbind the entity name from the remote enity object
			Naming.unbind(name);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;

		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		// remove the registry from the chat service
		entities.remove(name);

		System.out.println("Entity successfully deregistered [" + name + "]");

		return true;
	}

	@Override
	public Map<String, String> getInfo(String name) throws RemoteException {
		System.out.println("Getting info for entity [" + name + "]");

		if (!entities.containsKey(name))
			return null;

		System.out.println("Returning info for entity [" + name + "]");

		return entities.get(name).getInfo();
	}

	@Override
	public Map<String, IChatRoom> getChatRooms() throws RemoteException {
		Map<String, IChatRoom> chatRooms = new HashMap<String, IChatRoom>();

		System.out.println("Building map of current chat rooms");

		for (IEntity entity : entities.values()) {
			if (!(entity instanceof IChatRoom))
				continue;

			try {
				chatRooms.put(entity.getName(), (IChatRoom) entity);
			} catch (RemoteException e) {
				System.out
						.println("Deregistering a chat room that died unexpectedly");

				deregister(entity);
			}
		}

		System.out.println("Returning list of current chat rooms:");
		System.out.println(chatRooms.toString());

		return chatRooms;
	}

	public static void main(String[] args) {
		try {
			new ChatRegistry();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}