package graphpack.local.persistence.JDO3;

import graphpack.local.Client;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class PresistentClient {

	@PrimaryKey
	String key;
	public String serviceName;
	public String clientName;
	
	public PresistentClient(String clientName, Client client) {
		this.key = client.serviceName + "." + clientName;
		
		this.serviceName = client.serviceName;
		this.clientName = clientName;
	}

	
}
