package org.owasp.appsensor;

import java.text.ParseException;
import java.util.Observer;

import org.owasp.appsensor.configuration.server.ServerConfiguration;
import org.owasp.appsensor.configuration.server.ServerConfigurationReader;
import org.owasp.appsensor.configuration.server.StaxServerConfigurationReader;

/**
 * AppSensor locator class is provided to make it easy to gain access to the 
 * AppSensor classes in use. Use the set methods to override the reference 
 * implementations with instances of any custom implementations.  Alternatively, 
 * These configurations are set in the appsensor-server-config.xml file.
 * 
 * @author John Melton (jtmelton@gmail.com) http://www.jtmelton.com/
 */
public class AppSensorServer extends ObjectFactory {
	
	private static ServerConfigurationReader configurationReader;
	
	private static ServerConfiguration configuration;
	
	private static EventStore eventStore;
	
	private static AttackStore attackStore;
	
	private static ResponseStore responseStore;
	
	private static AnalysisEngine eventAnalysisEngine;
	
	private static AnalysisEngine attackAnalysisEngine;
	
	private static AnalysisEngine responseAnalysisEngine;
	
	private static ResponseHandler responseHandler;
	
	public static AppSensorServer getInstance() {
		if(configurationReader == null) {
			//load default configuration reader
			configurationReader = new StaxServerConfigurationReader();
		}
		
		if(configuration == null) {
			try {
				configuration = configurationReader.read();
				
				initialize();
			} catch(ParseException pe) {
				throw new RuntimeException(pe);
			}
		}
		
		return SingletonHolder.instance;
	}
	
	public static AppSensorServer getInstance(ServerConfigurationReader overriddenConfigurationReader) {
		configurationReader = overriddenConfigurationReader;
		
		try {
			configuration = configurationReader.read();
			
			initialize();
		} catch(ParseException pe) {
			throw new RuntimeException(pe);
		}
		
		return SingletonHolder.instance;
	}
	
	private static final class SingletonHolder {
		static final AppSensorServer instance = new AppSensorServer();
	}
	
	private static void initialize() {
		eventStore = null;
		attackStore = null;
		responseStore = null;
		
		//load up observer configurations on static load
		for(String observer : configuration.getEventStoreObserverImplementations()) {
			SingletonHolder.instance.getEventStore().addObserver((Observer)make(observer, "EventStoreObserver"));
		}
		
		for(String observer : configuration.getAttackStoreObserverImplementations()) {
			SingletonHolder.instance.getAttackStore().addObserver((Observer)make(observer, "AttackStoreObserver"));
		}
		
		for(String observer : configuration.getResponseStoreObserverImplementations()) {
			SingletonHolder.instance.getResponseStore().addObserver((Observer)make(observer, "ResponseStoreObserver"));
		}
	}
	
	//singleton
	private AppSensorServer() { }
	
//	/**
//	 * call this to load your own config reader post-initialization
//	 * @param configurationReader your own custom config reader
//	 */
//	public void setServerConfigurationReader(ServerConfigurationReader configurationReader) {
//		AppSensorServer.configurationReader = configurationReader;
//	}
//	
//	/**
//	 * Call this after calling setServerConfigurationReader() to reload the configuration
//	 * @throws ParseException
//	 */
//	public void reloadConfiguration() throws ParseException {
//		configuration = configurationReader.read();
//		
//		initialize();
//	}
//	
	/**
	 * Accessor for ServerConfiguration object
	 * @return ServerConfiguration object
	 */
	public ServerConfiguration getConfiguration() {
		return configuration;
	}
	
	public void setConfiguration(ServerConfiguration updatedConfiguration) {
		configuration = updatedConfiguration;
	}
	
	/**
	 * Accessor for Event AnalysisEngine object
	 * @return Event AnalysisEngine object
	 */
	public AnalysisEngine getEventAnalysisEngine() {
		if (eventAnalysisEngine == null) {
			eventAnalysisEngine = make(getConfiguration().getEventAnalysisEngineImplementation(), "EventAnalysisEngine");
		}
		
		return eventAnalysisEngine;
	}
	
	/**
	 * Accessor for Attack AnalysisEngine object
	 * @return Attack AnalysisEngine object
	 */
	public AnalysisEngine getAttackAnalysisEngine() {
		if (attackAnalysisEngine == null) {
			attackAnalysisEngine = make(getConfiguration().getAttackAnalysisEngineImplementation(), "AttackAnalysisEngine");
		}
		
		return attackAnalysisEngine;
	}
	
	/**
	 * Accessor for Response AnalysisEngine object
	 * @return Response AnalysisEngine object
	 */
	public AnalysisEngine getResponseAnalysisEngine() {
		if (responseAnalysisEngine == null) {
			responseAnalysisEngine = make(getConfiguration().getResponseAnalysisEngineImplementation(), "ResponseAnalysisEngine");
		}
		
		return responseAnalysisEngine;
	}
	
	/**
	 * Accessor for EventStore object
	 * @return EventStore object
	 */
	public EventStore getEventStore() {
		if (eventStore == null) {
			eventStore = make(getConfiguration().getEventStoreImplementation(), "EventStore");
		}
		
		return eventStore; 
	}
	
	/**
	 * Accessor for AttackStore object
	 * @return AttackStore object
	 */
	public AttackStore getAttackStore() {
		if (attackStore == null) {
			attackStore = make(getConfiguration().getAttackStoreImplementation(), "AttackStore");
		}
		
		return attackStore;
	}
	
	/**
	 * Accessor for ResponseStore object
	 * @return ResponseStore object
	 */
	public ResponseStore getResponseStore() {
		if (responseStore == null) {
			responseStore = make(getConfiguration().getResponseStoreImplementation(), "ResponseStore");
		}
		
		return responseStore;
	}
	
	/**
	 * Accessor for Logger object
	 * @return Logger object
	 */
	public Logger getLogger() {
		return make(getConfiguration().getLoggerImplementation(), "Logger");
	}
	
	/**
	 * Accessor for ReponseHandler object. 
	 * @return ResponseHandler object
	 */
	public ResponseHandler getResponseHandler() {
		if (responseHandler == null) {
			responseHandler = make(getConfiguration().getResponseHandlerImplementation(), "ResponseHandler");
		}
		
		return responseHandler;
	}
	
}