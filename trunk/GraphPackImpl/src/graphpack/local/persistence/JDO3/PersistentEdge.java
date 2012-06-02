package graphpack.local.persistence.JDO3;

import graphpack.Edge.Payload;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class PersistentEdge {
	@PrimaryKey
    @Persistent(valueStrategy=IdGeneratorStrategy.INCREMENT)
    public long num;
	
	public String srcServiceName;
	public String srcClientName;
	public String srcNodeName;
	
	public String trgServiceName;
	public String trgClientName;
	public String trgNodeName;
	/** must implement {@link PersistenceCapable} */
	public Payload payload;
	
	public PersistentEdge(String srcServiceName, String srcClientName,
			String srcNodeName, String trgServiceName, String trgClientName,
			String trgNodeName, Payload payload) {
		this.srcServiceName = srcServiceName;
		this.srcClientName = srcClientName;
		this.srcNodeName = srcNodeName;
		this.trgServiceName = trgServiceName;
		this.trgClientName = trgClientName;
		this.trgNodeName = trgNodeName;
		this.payload = payload;
	}
	
}
