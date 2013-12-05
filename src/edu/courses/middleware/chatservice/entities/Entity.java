package edu.courses.middleware.chatservice.entities;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This class must be extended to define components that may be registered in
 * the chat service.
 * 
 * @author Jonas Michel
 * 
 */
public abstract class Entity extends UnicastRemoteObject implements IEntity {
	private static final long serialVersionUID = 7604250362221746391L;

	protected Entity() throws RemoteException {
		super();
	}

}
