package il.co.topq.mobile.client.interfaces;

import il.co.topq.mobile.common.client.enums.HardwareButtons;
import il.co.topq.mobile.common.datamodel.CommandResponse;


/**
 * 
 * @author Bortman Limor,Tal ben shabtay
 * this interface is the API that will be exposed to the clients.
 * the features in this API has the ability to operate functions
 * upon an android application
 *
 */
public interface MobileClientInterface {
	
	public enum EDGE {
		TOP, BOTTOM
	}
	

	/**
	 * NOTE ! THIS METHOD MUST BE THE FIRST COMMAND BEFORE ANY OTHER COMMAND the
	 * launch method will launch the instrumentation of the application
	 * 
	 * @param launcherActivityClass
	 *            the full class name with the package of the main launcher
	 *            activity
	 * @return return status of the operation
	 * @throws Exception
	 */

	public abstract CommandResponse launch(String launcherActivityClass) throws Exception;
	
	/**
	 * will get the executor id
	 * @return the executor id
	 */
	public String getExecutorID();
	
	/**
 	* will set the executor id
 	* @param executorID the current unique executor id
 	*/
	public void setExecutorID(String executorID);
	
	/**
	 * NOTE ! THIS METHOD MUST BE THE FIRST COMMAND BEFORE ANY OTHER COMMAND
	 * the launch method will launch the instrumentation of the application
	 * @param launcherActivityClass the full class name with the package of the main launcher activity
	 * @param executorFullClassName the full name of the executor class that handles this app 
	 * @return return status of the operation
	 * @throws Exception
	 */
	public CommandResponse launch(String launcherActivityClass,String executorFullClassName) throws Exception;
	
	/**
	 * will get the text of the input text view index
	 * 
	 * @param index
	 *            the index of the text view on the screen
	 * @return the text of the text view
	 * @throws Exception
	 */
	public abstract CommandResponse getTextView(int index) throws Exception;

	/**
	 * will get the id of the text view with the input text
	 * 
	 * @param text
	 *            the text that is in the text view
	 * @return id of the text view
	 * @throws Exception
	 */
	public abstract CommandResponse getTextViewIndex(String text) throws Exception;

	/**
	 * will get a list of all the texts of the current display text views
	 * 
	 * @return list of all the text views current displayed
	 * @throws Exception
	 */
	public abstract CommandResponse getCurrentTextViews() throws Exception;

	/**
	 * will get the text of the view by the input index
	 * 
	 * @param index
	 *            the index of the view in the screen
	 * @return the text of the input index text view
	 * @throws Exception
	 */
	public abstract CommandResponse getText(int index) throws Exception;
	
	public abstract CommandResponse launchServerEnviroment(String  serverName) throws Exception;
	

	/**
	 * will click on the menu item with the input text
	 * 
	 * @param item
	 *            the text of the item in the menu
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnMenuItem(String item) throws Exception;

	/**
	 * will click on any view with the input index
	 * 
	 * @param index
	 *            the index of the view on the screen
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnView(int index) throws Exception;

	public abstract CommandResponse verifyViewExistsByDescription(String description, boolean click, boolean startsWith, boolean clickInSpecificPosition, float x, float y) throws Exception;


	/**
	 * will click on button with the input index
	 * 
	 * @param index
	 *            the index of the button on the screen
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnButton(int index) throws Exception;

	

	/**
	 * will click on button that includes the input text
	 * 
	 * @param text
	 *            the text on the button
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnButtonWithText(String text) throws Exception;

	/**
	 * will click on the text with the input text
	 * 
	 * @param text
	 *            the text to click on
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnText(String text) throws Exception;

	public abstract CommandResponse waitForActivity(String activityName, int timeout) throws Exception;

	/**
	 * will click on the single input char
	 * 
	 * @param key
	 *            ascii number of the char wanted to click
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse sendKey(int key) throws Exception;

	/**
	 * simulate a click on an hardware button
	 * 
	 * @param button
	 *            an enum of the possible hardware buttons
	 * @return return status of the operation
	 * @throws Exception
	 */
	public abstract CommandResponse clickOnHardwareButton(HardwareButtons button) throws Exception;

	/**
	 * will close the connection to the server
	 * 
	 * @throws Exception
	 */
	public abstract CommandResponse getViews() throws Exception;

	public abstract CommandResponse swipeLeft() throws Exception;

	public abstract CommandResponse swipeRight() throws Exception;

	public abstract CommandResponse clickOnImageButton(int index) throws Exception;

	public abstract CommandResponse clickOnImage(int index) throws Exception;

	public abstract CommandResponse scrollDown() throws Exception;

	public abstract CommandResponse isTextVisible(String text) throws Exception;

	public abstract CommandResponse scrollDownUntilTextIsVisible(String text) throws Exception;

	public abstract CommandResponse getCurrentActivity() throws Exception;

	public abstract CommandResponse clickOnActionBarItem(int index) throws Exception;

	public abstract CommandResponse clickOnScreen(float x, float y, boolean relative) throws Exception;

	public abstract CommandResponse drag(float fromX, float toX, float fromY, float toY, int steps, boolean relative) throws Exception;

	public abstract CommandResponse setOrientation(int orientation) throws Exception;

	public abstract CommandResponse scrollToEdge(EDGE edge) throws Exception;

	public abstract CommandResponse clickInList(int index) throws Exception;

	public abstract CommandResponse clickInList(int index1, int index2) throws Exception;

	public abstract CommandResponse getAllVisibleIds() throws Exception;

	public abstract CommandResponse click(String expression) throws Exception;

	public abstract CommandResponse isViewVisibleByViewId(int viewId) throws Exception;

	public abstract CommandResponse isEditTextVisibleById(int editTextId) throws Exception;

	public abstract CommandResponse getCurrentActivityName() throws Exception;

	public abstract CommandResponse closeActivity(String activityName) throws Exception;

	public abstract CommandResponse isViewVisibleByViewName(String viewName) throws Exception;

	public abstract CommandResponse clickInControlByIndex(String controlName, int indexToClickOn) throws Exception;

	public abstract CommandResponse clickOnMenuItemById(int id) throws Exception;

	public abstract void closeApplication() throws Exception;

	public abstract CommandResponse clickOnViewByIndex(int index) throws Exception;

	public abstract CommandResponse ScrollLeft() throws Exception;

	public abstract CommandResponse ScrollRight() throws Exception;

	public abstract CommandResponse ScrollUP() throws Exception;

	public abstract CommandResponse clickOnRadioButtonByIndex(int radioButtonIndex) throws Exception;

	public abstract CommandResponse clickOnImageByIndex(int imageIndex) throws Exception;

	public abstract CommandResponse clickOnActionBarHomeButton() throws Exception;

	public abstract CommandResponse showMenuOptions() throws Exception;

	public abstract byte[] pull(String fileName) throws Exception;

	public abstract CommandResponse push(byte[] data, String newlocalFileName) throws Exception;

	public abstract CommandResponse setPreferanceCompleteRideCounter() throws Exception;

	public abstract CommandResponse launch() throws Exception;

	public abstract CommandResponse setPreferanceInUserApp(String SettingFileName, String KeyPreferanceNameToBeChange) throws Exception;
	public CommandResponse enterText(int viewId, String text) throws Exception ;
	public CommandResponse enterTextByIndex(int index, String text) throws Exception ;
	public CommandResponse clearEditTextByIndex(int index) throws Exception ;
	public CommandResponse clearEditText(int viewId) throws Exception ;
	public CommandResponse setText( String text) throws Exception ;
	public CommandResponse deleteAppData() throws Exception ;
	public CommandResponse getDeviceName() throws Exception;
	public CommandResponse waitForText(String text) throws Exception;
	public CommandResponse countItemsInList(int viewId) throws Exception ;
	public CommandResponse closeSpecificApplication(String packageName) throws Exception ;
	public CommandResponse launchServerEnviromentWeb(String server, String packageName) throws Exception;
	public CommandResponse getTable(String language, String requestedValue) throws Exception;
	public CommandResponse getIndexListItemByText(String searchedTextInList,int viewId) throws Exception;
	public CommandResponse setPreferanceFirstPromoInvite() throws Exception;
	public CommandResponse  initWebElementAndExecuteMethode(String locatorType , String locator,  String methodNameStr ,String TextToInsert)throws Exception;
	public CommandResponse isRadioBtnIscheckedByText(String RBtnText) throws Exception ;
	public CommandResponse isCheckBoxIscheckedByText(String CheckBtnText) throws Exception ;
	public CommandResponse isCheckBoxIscheckedByIndex(int CheckIndex) throws Exception ;
	public CommandResponse isRadioButtoncheckedByIndex(int RBtn) throws Exception ;
	public void closeConnection() throws Exception;
	public CommandResponse getTextById(int id) throws Exception;
	public CommandResponse validateIfTextIsFound(String textToSearch) throws Exception;
	public CommandResponse getListItemContentByText(String searchText, int columnToSearchIn, int targetColumnToPull) throws Exception;
	CommandResponse validateIfTextIsFound(int viewid, String textToSearch)
			throws Exception;	
}
