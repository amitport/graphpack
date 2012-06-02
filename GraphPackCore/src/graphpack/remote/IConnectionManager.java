/*******************************************************************************
 * Copyright 2012 Amit Portnoy
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package graphpack.remote;

import graphpack.IService;

/**
 * this interface is a part of the communication SPI </br>
 * its methods are responsible for making an {@link graphpack.IService service} available remotely and for remotely connecting to other services <br/>
 * any implementation of this interface should transfer incoming requests to a corresponding {@link Gateway}
 * @author amitport </br>
 */
public interface IConnectionManager {
	/**
	 * makes an {@link graphpack.IService service} available remotely </br>
	 * the service can be made unavailable using {@link IConnectionManager#unexport()}
	 * @param serviceName the name of the service
	 * @param service the service itself
	 */
	 void export(String serviceName, IService service);
	 /**
	  * cancel the availability of the service that was exported using {@link IConnectionManager#export(String, IService)}
	  */
	 void unexport();
	 /**
	  * connects to a remote {@link graphpack.IService service}
	  * @param sourceService the service from which the connection is initiated 
	  * @param sourceClient the client that initiates the connection
	  * @param targetService the name of the target service
	  * @return a proxy that represents the remote service. the proxy's method calls returns the same results as if the corresponding method was called on {@link Gateway} 
	  */
	 IService connect(String sourceService, String sourceClient, String targetService);
}
