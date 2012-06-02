package graphpack.local.persistence.JDO3;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import com.google.inject.Inject;

public class JDOManager {
	
	public PersistenceManager pm;
	
	@Inject
	public JDOManager(){	
		pm = JDOHelper.getPersistenceManagerFactory().
					getPersistenceManager();
	}
	
}
