package graphpack.local.persistence.JDO3;

import graphpack.local.Node;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class PresistentNode {
	
	@PrimaryKey
	String key;
	
	public String serviceName;
	public String clientName;
	public String nodeName;

	public PresistentNode(String nodeName, Node node) {
		this.key = node.serviceName + "." + node.clientName + "." + nodeName;
		
		this.serviceName = node.serviceName;
		this.clientName = node.clientName;
		this.nodeName = nodeName;
	}

}
