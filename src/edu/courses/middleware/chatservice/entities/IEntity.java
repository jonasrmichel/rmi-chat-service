package edu.courses.middleware.chatservice.entities;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

/**
 * This is the bare minimum remote interface that must be exposed by chat
 * service Entity objects.
 * 
 * @author Jonas Michel
 * 
 */
public interface IEntity extends Remote {

	/**
	 * Returns the entity's name.
	 * 
	 * @return the entity's name.
	 * @throws RemoteException
	 */
	public String getName() throws RemoteException;

	/**
	 * Returns information about the entity.
	 * 
	 * @return a map of entity information key value pairs.
	 */
	public Map<String, String> getInfo() throws RemoteException;

}
