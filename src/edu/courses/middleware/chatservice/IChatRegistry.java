package edu.courses.middleware.chatservice;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import edu.courses.middleware.chatservice.entities.IChatRoom;
import edu.courses.middleware.chatservice.entities.IEntity;

/**
 * This is the remote interface exposed by the ChatRegistry.
 * 
 * @author Jonas Michel
 * 
 */
public interface IChatRegistry extends Remote {

	/**
	 * Called to request the provided entity be registered in the chat service.
	 * 
	 * @param entity
	 *            an entity to be registered.
	 * @return true if the entity was successfully registered.
	 * @throws RemoteException
	 */
	public boolean register(IEntity entity) throws RemoteException;

	/**
	 * Called to request the provided entity be deregistered from the chat
	 * service.
	 * 
	 * @param entity
	 *            the entity to be deregistered.
	 * @return true if the entity was successfully deregistered.
	 * @throws RemoteException
	 */
	public boolean deregister(IEntity entity) throws RemoteException;

	/**
	 * Returns the information stored about the specified entity.
	 * 
	 * @param name
	 *            the name of a registered entity.
	 * @return a map of entity information.
	 * @throws RemoteException
	 */
	public Map<String, String> getInfo(String name) throws RemoteException;

	/**
	 * Returns the currently registered chat rooms.
	 * 
	 * @return the currently registered chat rooms.
	 * @throws RemoteException
	 */
	public Map<String, IChatRoom> getChatRooms() throws RemoteException;

}
