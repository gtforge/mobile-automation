package il.co.topq.mobile.server.impl;

import il.co.topq.mobile.server.interfaces.IDataCallback;
import il.co.topq.mobile.server.interfaces.IExecutorService;
import il.co.topq.mobile.server.interfaces.IInstrumentationLauncher;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * this service is in charge of capturing data from the tcp server 
 * and transferring it to the command executor
 * @author tal ben shabtay
 *
 */
public class ExecutorService extends Service {
	
	private static final String TAG = "ExecutorService";
	private IDataCallback commandExecutor;
	private IInstrumentationLauncher instrumentationLauncher;
	private TcpServer tcpServer;
	
	private IExecutorService.Stub apiEndPoint = new IExecutorService.Stub() {		
		
		
		public String getLastExecutorID() {
			return tcpServer.getLastExecutorID();
		}
		
		/**
		 * registers a command executor to the service
		 */
		@Override
		public void registerExecutor(String executorID,IDataCallback executor) {
			Log.d(TAG,"Registering Executor : "+executor + " to ID : "+executorID);
			commandExecutor = executor;
			tcpServer.registerDataExecutor(executorID,commandExecutor);
		}
		
		/**
		 * registers an instrumentation launcher to the service
		 */
		@Override
		public void registerInstrumenationLauncher(IInstrumentationLauncher iInstrumentationLauncher) {
			Log.d(TAG,"Registering Instrumentation Launcher : "+iInstrumentationLauncher);
			instrumentationLauncher = iInstrumentationLauncher;
			tcpServer.registerInstrumentationLauncher(instrumentationLauncher);
		}
		
		/**
		 * starts the tcp server communication with the input port
		 * @param serverPort the server port
		 */
		@Override
		public void startServerCommunication(int serverPort) {
			if (tcpServer == null) {
				tcpServer = TcpServer.getInstance(serverPort);
				
				tcpServer.startServerCommunication();
			}
			else {
				tcpServer.setNewPort(serverPort);
			}
		}
		
	};
	
	/**
	 * returns a stub of this service
	 */
	@Override
	public IBinder onBind(Intent intent) {
		if (ExecutorService.class.getName().equals(intent.getAction())) {
		    Log.d(TAG, "Bound by intent " + intent);
		    return this.apiEndPoint;
		} 
		else {
		    return null;
		}
	}

	/**
	 * create the service
	 */
	@Override
	public void onCreate() {		
		super.onCreate();
		Log.i(TAG, "Is created");
	}
	
}
