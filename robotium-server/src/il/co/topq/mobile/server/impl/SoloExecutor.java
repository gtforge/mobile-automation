package il.co.topq.mobile.server.impl;

import il.co.topq.mobile.common.datamodel.CommandRequest;
import il.co.topq.mobile.common.datamodel.CommandResponse;
import il.co.topq.mobile.common.server.utils.JsonParser;
import il.co.topq.mobile.server.common.enum_c.ELocatorType;
import il.co.topq.mobile.server.common.enum_c.SoloWebViewMethod;
import il.co.topq.mobile.server.impl.SoloUtils.AXIS;
import il.co.topq.mobile.server.interfaces.ISoloProvider;
import il.co.topq.mobile.viewexpr.ViewExpressionException;
import il.co.topq.mobile.viewexpr.ViewExpressionInterperter;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SlidingDrawer;
import android.widget.TableLayout;
import android.widget.TextView;

import com.robotium.solo.By;
import com.robotium.solo.Solo;
import com.robotium.solo.WebElement;

/**
 * 
 * @author tal ben shabtay,limor bortman executes the client command with the solo interface
 */
public class SoloExecutor {

	private static final String COMPLETE_ORDERS = "COMPLETE_ORDERS";
	private static final String FIRST_PROMO_INVITE = "FIRST_PROMO_INVITE";
	private static final String SETTINGS_FILE_NAME = "app_pref.dat";
	private static final String TAG = "SoloExecutor";
	private Instrumentation instrumentation;
	private Solo solo;
	private final ISoloProvider soloProvider;
	private static final int RIGHT = 22;
	private static final int LEFT = 21;
	private static final int DOWN = 19;
	private static final int UP = 19;
	private Map<Character, Integer> keys;

	/**
	 * http://developer.android.com/reference/android/view/KeyEvent.html
	 */
	private void initializeKeyMap() {
		keys = new HashMap<Character, Integer>();
		keys.put('A', KeyEvent.KEYCODE_A);
		keys.put('B', KeyEvent.KEYCODE_B);
		keys.put('C', KeyEvent.KEYCODE_C);
		keys.put('D', KeyEvent.KEYCODE_D);
		keys.put('E', KeyEvent.KEYCODE_E);
		keys.put('F', KeyEvent.KEYCODE_F);
		keys.put('G', KeyEvent.KEYCODE_G);
		keys.put('H', KeyEvent.KEYCODE_H);
		keys.put('I', KeyEvent.KEYCODE_I);
		keys.put('J', KeyEvent.KEYCODE_J);
		keys.put('K', KeyEvent.KEYCODE_K);
		keys.put('L', KeyEvent.KEYCODE_L);
		keys.put('M', KeyEvent.KEYCODE_M);
		keys.put('N', KeyEvent.KEYCODE_N);
		keys.put('O', KeyEvent.KEYCODE_O);
		keys.put('P', KeyEvent.KEYCODE_P);
		keys.put('Q', KeyEvent.KEYCODE_Q);
		keys.put('R', KeyEvent.KEYCODE_R);
		keys.put('S', KeyEvent.KEYCODE_S);
		keys.put('T', KeyEvent.KEYCODE_T);
		keys.put('U', KeyEvent.KEYCODE_U);
		keys.put('V', KeyEvent.KEYCODE_V);
		keys.put('W', KeyEvent.KEYCODE_W);
		keys.put('X', KeyEvent.KEYCODE_X);
		keys.put('Y', KeyEvent.KEYCODE_Y);
		keys.put('Z', KeyEvent.KEYCODE_Z);

		keys.put('a', KeyEvent.KEYCODE_A);
		keys.put('b', KeyEvent.KEYCODE_B);
		keys.put('c', KeyEvent.KEYCODE_C);
		keys.put('d', KeyEvent.KEYCODE_D);
		keys.put('e', KeyEvent.KEYCODE_E);
		keys.put('f', KeyEvent.KEYCODE_F);
		keys.put('g', KeyEvent.KEYCODE_G);
		keys.put('h', KeyEvent.KEYCODE_H);
		keys.put('i', KeyEvent.KEYCODE_I);
		keys.put('j', KeyEvent.KEYCODE_J);
		keys.put('k', KeyEvent.KEYCODE_K);
		keys.put('l', KeyEvent.KEYCODE_L);
		keys.put('m', KeyEvent.KEYCODE_M);
		keys.put('n', KeyEvent.KEYCODE_N);
		keys.put('o', KeyEvent.KEYCODE_O);
		keys.put('p', KeyEvent.KEYCODE_P);
		keys.put('q', KeyEvent.KEYCODE_Q);
		keys.put('r', KeyEvent.KEYCODE_R);
		keys.put('s', KeyEvent.KEYCODE_S);
		keys.put('t', KeyEvent.KEYCODE_T);
		keys.put('u', KeyEvent.KEYCODE_U);
		keys.put('v', KeyEvent.KEYCODE_V);
		keys.put('w', KeyEvent.KEYCODE_W);
		keys.put('x', KeyEvent.KEYCODE_X);
		keys.put('y', KeyEvent.KEYCODE_Y);
		keys.put('z', KeyEvent.KEYCODE_Z);

		keys.put('1', KeyEvent.KEYCODE_1);
		keys.put('2', KeyEvent.KEYCODE_2);
		keys.put('3', KeyEvent.KEYCODE_3);
		keys.put('4', KeyEvent.KEYCODE_4);
		keys.put('5', KeyEvent.KEYCODE_5);
		keys.put('6', KeyEvent.KEYCODE_6);
		keys.put('7', KeyEvent.KEYCODE_7);
		keys.put('8', KeyEvent.KEYCODE_8);
		keys.put('9', KeyEvent.KEYCODE_9);
		keys.put('0', KeyEvent.KEYCODE_0);
		keys.put(',', KeyEvent.KEYCODE_COMMA);

		keys.put('@', KeyEvent.KEYCODE_AT);
		keys.put('.', KeyEvent.KEYCODE_PERIOD);
		keys.put(' ', KeyEvent.KEYCODE_SPACE);
		keys.put('*', KeyEvent.KEYCODE_ENTER);// for Next or done
		keys.put('-', KeyEvent.KEYCODE_CTRL_RIGHT);// like tab 
	}

	/**
	 * creates a solo executor
	 * 
	 * @param soloProvider
	 *            an interface that provides a solo object
	 * @param instrumentation
	 *            the instrumentation of the AUT
	 */
	public SoloExecutor(final ISoloProvider soloProvider, Instrumentation instrumentation) {
		super();
		this.soloProvider = soloProvider;
		this.instrumentation = instrumentation;
	}

	/**
	 * executes the command with the solo
	 * 
	 * @param data
	 *            json of command request object
	 * @return json of a command response object
	 * @throws Exception
	 */
	public String execute(final String data) throws Exception {
		// ScriptParser parser;
		// JSONObject result = new JSONObject();
		// parser = new ScriptParser(data);
		CommandRequest request = JsonParser.fromJson(data, CommandRequest.class);
		CommandResponse response = new CommandResponse();
		// for (CommandParser command : parser.getCommands()) {
		if (!this.soloProvider.getLastExecutorID().equals(request.getExecutorID())) {
			this.soloProvider.syncActivity();
		}
		String commandStr = request.getCommand();
		
//		
//		String[] params = null;
//		
//		
//		Method method = this.getClass().getMethod(commandStr, null);
//		CommandResponse response;
//		if(request.getParams()!=null){
//			method = this.getClass().getMethod(commandStr, null);
//			response = (CommandResponse) method.invoke(this, request.getParams());
//		}
//		else{
//			method = this.getClass().getMethod(commandStr, null);
//			response = (CommandResponse) method.invoke(this);
//		}
		
		
		
		if (commandStr.equals("scrollToEdge")) {
			response = scrollToEdge(request.getParams());
		} else if (commandStr.equals("setPortraitOrientation")) {
			response = setOrientation(Solo.PORTRAIT, request.getParams());
		} else if (commandStr.equals("setLandscapeOrientation")) {
			response = setOrientation(Solo.LANDSCAPE, request.getParams());
		} else if (commandStr.equals("drag")) {
			response = drag(request.getParams());
		} else if (commandStr.equals("clickOnScreen")) {
			response = clickOnScreen(request.getParams());
		} else if (commandStr.equals("clickOnActionBarItem")) {
			response = clickOnActionBarItem(request.getParams());
		} else if (commandStr.equals("getCurrentActivity")) {
			response = getCurrentActivity(request.getParams());
		} else if (commandStr.equals("scrollDownUntilTextIsVisible")) {
			response = scrollDownUntilTextIsVisible(request.getParams());
		} else if (commandStr.equals("scrollDownSafetly")) {
			response = scrollDown(request.getParams());
		} else if (commandStr.equals("isTextVisible")) {
			response = isTextVisible(request.getParams());
		} else if (commandStr.equals("waitForText")) {
			response = waitForText(request.getParams());			
		} else if (commandStr.equals("clickOnImageButton")) {
			response = clickOnImagButton(request.getParams());
		} else if (commandStr.equals("clickOnImage")) {
			response = clickOnImag(request.getParams());
		} else if (commandStr.equals("swipeRight")) {
			response = swipeRight(request.getParams());
		} else if (commandStr.equals("swipeLeft")) {
			response = swipeLeft(request.getParams());
		} else if (commandStr.equals("enterText")) {
			response = enterText(request.getParams());
		} else if (commandStr.equals("setText")) {
			response = setText(request.getParams());
		} else if (commandStr.equals("enterTextByIndex")) {
			response = enterTextByIndex(request.getParams());
		} else if (commandStr.equals("isButtonVisible")) {
			response = isButtonVisible(request.getParams());
		} else if (commandStr.equals("clickInControlByIndex")) {
			response = clickInControlByIndex(request.getParams());
		} else if (commandStr.equals("isViewVisibleByViewName")) {
			response = isViewVisibleByViewName(request.getParams());
		} else if (commandStr.equals("isViewVisibleByViewId")) {
			response = isViewVisibleByViewId(request.getParams());
		} else if (commandStr.equals("clickOnButton")) {
			response = clickOnButton(request.getParams());
		} else if (commandStr.equals("launch")) {
			response = launch();
		} else if (commandStr.equals("clearEditText")) {
			response = clearEditText(request.getParams());
		} else if (commandStr.equals("clearEditTextByIndex")) {
			response = clearEditTextByIndex(request.getParams());
		} else if (commandStr.equals("clickOnButtonWithText")) {
			response = clickOnButtonWithText(request.getParams());
		} else if (commandStr.equals("clickOnView")) {
			response = clickOnView(request.getParams());
		} else if (commandStr.equals("verifyViewExistsByDescription")) {
			response = verifyViewExistsByDescription(request.getParams());
		} else if (commandStr.equals("clickOnText")) {
			response = clickOnText(request.getParams());
		} else if (commandStr.equals("sendKey")) {
			response = sendKey(request.getParams());
		} else if (commandStr.equals("clickOnMenuItem")) {
			response = clickOnMenuItem(request.getParams());
		} else if (commandStr.equals("getViews")) {
			response = getViews(request.getParams());
		} else if (commandStr.equals("getText")) {
			response = getText(request.getParams());
		} else if (commandStr.equals("getTextById")) {
			response = getTextById(request.getParams());
		} else if (commandStr.equals("getTextViewIndex")) {
			response = getTextViewIndex(request.getParams());
		} else if (commandStr.equals("getTextView")) {
			response = getTextView(request.getParams());
		} else if (commandStr.equals("getCurrentTextViews")) {
			response = getCurrentTextViews(request.getParams());
		} else if (commandStr.equals("clickOnHardware")) {
			response = clickOnHardware(request.getParams());
		} else if (commandStr.equals("createFileInServer")) {
			response = createFileInServer(request.getParams());
		} else if (commandStr.equals("activateIntent")) {
			response = activateIntent(request.getParams());
		} else if (commandStr.equals("waitForActivity")) {
			response = waitForActivity(request.getParams());
		} else if (commandStr.equals("clickInList")) {
			response = clickInList(request.getParams());
		} else if (commandStr.equals("getAllVisibleIds")) {
			response = getAllVisibleIds();
		} else if (commandStr.equals("click")) {
			response = click(request.getParams());
		} else if (commandStr.equals("closeActivity")) {
			response = closeActivity(request.getParams());
		} else if (commandStr.equals("clickOnMenuItemById")) {
			response = clickOnMenuItemById(request.getParams());
		} else if (commandStr.equals("closeApplication")) {
			response = closeApplication();
		} else if (commandStr.equals("clickOnViewByIndex")) {
			response = clickOnViewByIndex(request.getParams());
		} else if (commandStr.equals("scrollLeft")) {
			response = scrollLeft();
		} else if (commandStr.equals("scrollRight")) {
			response = scrollRight();
		} else if (commandStr.equals("scrollUp")) {
			response = scrollUp();
		} else if (commandStr.equals("scrollDown")) {
			response = scrollDown();
		} else if (commandStr.equals("clickOnRadioButton")) {
			response = clickOnRadioButton(request.getParams());
		} else if (commandStr.equals("isRadioBtnIscheckedByText")) {
			response = isRadioBtnIscheckedByText(request.getParams());
		} else if (commandStr.equals("isRadioButtoncheckedByIndex")) {
			response = isRadioButtoncheckedByIndex(request.getParams());
		} else if (commandStr.equals("isCheckBoxIscheckedByText")) {
			response = isCheckBoxIscheckedByText(request.getParams());
		} else if (commandStr.equals("isCheckBoxIscheckedByIndex")) {
			response = isCheckBoxIscheckedByIndex(request.getParams());
		} else if (commandStr.equals("clickOnActionBarHomeButton")) {
			response = clickOnActionBarHomeButton();
		} else if (commandStr.equals("showMenuOptions")) {
			response = showMenuOptions();
		} else if (commandStr.equals("deleteAppData")) {
			response = deleteAppData();			
		} else if (commandStr.equals("pull")) {
			response = pull(request.getParams());
		} else if (commandStr.equals("initWebElementAndExecuteMethode")) {
			response = initWebElementAndExecuteMethode(request.getParams());
		} else if (commandStr.equals("push")) {
			response = createFileInServer(request.getParams());
		} else if (commandStr.equals("setPreferanceCompleteRideCounter")) {
			response = setPreferanceCompleteRideCounter();
		} else if (commandStr.equals("setPreferanceFirstPromoInvite")) {
			response = setPreferanceFirstPromoInvite();
		} else if (commandStr.equals("setPreferanceInUserApp")) {
			response = setPreferanceInUserApp(request.getParams());
		} else if (commandStr.equals("launchServerEnviroment")) {
			response = launchServerEnviroment(request.getParams());
		} else if (commandStr.equals("getDeviceName")) {
			response = getDeviceName();
		} else if (commandStr.equals("countItemsInList")) {
			response = countItemsInList(request.getParams());
		} else if (commandStr.equals("closeSpecificApplication")) {
			response = closeSpecificApplication(request.getParams());
		} else if (commandStr.equals("launchServerEnviromentWeb")) {
			response = launchServerEnviromentWeb(request.getParams());
		} else if (commandStr.equals("getTable")) {
			response = getTable(request.getParams());
		} else if (commandStr.equals("getIndexListItemByText")) {
			response = getIndexListItemByText(request.getParams());
		} else if (commandStr.equals("validateIfTextIsFound")) {
			response = validateIfTextIsFound(request.getParams());
		} else if (commandStr.equals("getListItemContentByText")) {
			response = getListItemContentByText(request.getParams());
		}
		
		
		else{
			Log.e(TAG, "ERROR - Didn't find the method: " + request.getCommand() + request.getParams() + " in this class - SoloExecutor!");
		}
		
		response.setOriginalCommand(request.getCommand());
		response.setParams(request.getParams());
		String result = JsonParser.toJson(response);
		Log.i(TAG, "The Result is:" + result);
		return result;
	}
	
	public CommandResponse getDeviceName() {
		  CommandResponse result = new CommandResponse();
		  String command = "the command \"getDeviceName\": ";
		  result.setSucceeded(true);
		  
		  String manufacturer = Build.MANUFACTURER;
		  String model = Build.MODEL;
		  
		  if (model.startsWith(manufacturer)) {
			  command += capitalize(model);
			  result.setResponse(command);
		  } else {
			  command += capitalize(manufacturer) + " " + model;
			  result.setResponse(command);
		  }
		  
		  String[] array = new String[2];
		  array[0] = capitalize(manufacturer);
		  array[1] = model;
		  result.setReturnedValues(array);
			
		  return result;
	}

	private String capitalize(String s) {
	  if (s == null || s.length() == 0) {
	    return "";
	  }
	  char first = s.charAt(0);
	  if (Character.isUpperCase(first)) {
	    return s;
	  } else {
	    return Character.toUpperCase(first) + s.substring(1);
	  }
	} 
		
	private CommandResponse click(String[] params) {
		CommandResponse result = new CommandResponse();
		result.setOriginalCommand("click");
		final String expression = params[0];
		Log.i(TAG, "About to click on " + expression);
		try {
			List<View> views = getViews(expression);
			if (views.size() == 0) {
				result.setResponse("No views found to match expression " + expression);
				result.setSucceeded(false);
				return result;
			}
			for (View view : views) {
				solo.clickOnView(view);
			}
			result.setResponse("Clicked on " + views.size() + " views");
			result.setSucceeded(true);
		} catch (ViewExpressionException e) {
			result.setSucceeded(false);
			result.setResponse("Failed evalauating expression " + expression);
		}
		return result;
	}

	private List<View> getViews(String expression) throws ViewExpressionException {
		ViewExpressionInterperter interperter = new ViewExpressionInterperter(solo.getViews().get(0).getRootView());
		return interperter.evaluate(expression);
	}

	private CommandResponse scrollToEdge(String[] params) {
		String command = "the command \"scroll to edge\": ";
		CommandResponse result = new CommandResponse();
		try {
			if (params[0].equals("top")) {
				while (scrollUpSafetly()) {
				}
				result.setSucceeded(true);
				result.setResponse(command);
				Log.e(TAG, "Scrollup was successfull");
			} else {
				while (scrollDownSafetly()) {

				}
				result.setSucceeded(true);
				result.setResponse(command);
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse setOrientation(int orientation, String[] params) {
		String command = "the command set orientation: ";
		CommandResponse result = new CommandResponse();
		try {
			this.solo.setActivityOrientation(orientation);
			result.setSucceeded(true);
			result.setResponse(command);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse waitForActivity(String[] params) {
		String command = "the command wait for activity: ";
		CommandResponse result = new CommandResponse();
		try {
			if (params.length == 2) {
				result.setSucceeded(solo.waitForActivity(params[0], Integer.parseInt(params[1])));

			} else if (params.length == 1) {
				result.setSucceeded(solo.waitForActivity(params[0]));
			}
			result.setResponse(command + "Wait for activity ended with status " + result.isSucceeded());
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnActionBarItem(String[] params) {
		String command = "the command click on actionbar item: ";
		CommandResponse result = new CommandResponse();
		try {
			solo.clickOnActionBarItem(Integer.parseInt(params[0]));
			result.setSucceeded(true);
			result.setResponse("Unable to tell whether the action suceeded or not");
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse getCurrentActivity(String[] params) {
		String command = "the command get current activity";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = this.solo.getCurrentActivity().getLocalClassName();
			if (response == null || response.equals("")) {
				result.setResponse(command + ",Response: failed to get the activity name");
				result.setSucceeded(false);
			} else {
				result.setOriginalCommand(command + ", Response: Activity name is: " + response);
				result.setResponse(response); // command + ", Response: Activity name is: " +
				result.setSucceeded(true);
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse scrollDown(String[] params) {
		String command = "the command scroll down";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "scrolling down";
			if (scrollDownSafetly()) {
				response += command + ",Response: " + response + " scrolled down successfully";
				result.setSucceeded(true);
				result.setResponse(response);
			} else {
				response += command + ",Response: " + response + " can't scroll down";
				result.setSucceeded(true);
				result.setResponse(response);

			}

		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse isTextVisible(String[] params) {
		String command = "the command is text visible";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "checking if text is visible";
			if (solo.searchText(params[0])) {
				result.setResponse(command + ",Response: " + response + " is visible");
			} else {
				result.setResponse(command + ",Response: " + response + " is not visible");
			}
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}
	
	private CommandResponse waitForText(String[] params) {
		String command = "the command waitForText";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "waiting for text";
			if (solo.waitForText(params[0])) {
				result.setResponse(command + ",Response: " + response + " is visible");
				result.setSucceeded(true);
			} else {
				result.setResponse(command + ",Response: " + response + " is not visible");
				result.setSucceeded(false);
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse scrollDownUntilTextIsVisible(String[] params) {
		String command = "the command scroll down until text is visible";
		CommandResponse result = new CommandResponse();
		String response;
		try {
			response = "Trying to scroll down until the requested text will be visible";
			while (!this.solo.searchText(params[0])) {
				if (!scrollDownSafetly()) {
					break;
				}
			}
			if (this.solo.searchText(params[0])) {
				result.setResponse(command + ",Response: " + response + ": text found");
			} else {
				result.setResponse(command + ",Response: " + response + ": text not found");
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * checks if the view is visible with the input id
	 * 
	 * @param arguments
	 *            id of the view
	 * @return response with the status of the command
	 */
	private CommandResponse isViewVisibleByViewId(String[] arguments) {
		CommandResponse result = new CommandResponse();
		String command = "the command isViewVisible";
		try {
			int viewId = Integer.parseInt(arguments[0]);
			command += "(" + viewId + ")";
			View view = this.solo.getView(viewId);
			if (view != null) {
				if (view.isShown()) {
					result.setResponse("view with ID: " + viewId + " is visible");
					result.setSucceeded(true);
				} else {
					result.setResponse("view with ID: " + viewId + " is not visible");
					result.setSucceeded(false);
				}
			} else {
				result.setResponse("view with ID: " + viewId + " is not found ");
				result.setSucceeded(false);
			}
		} catch (Throwable e) {
			result.setResponse(command + "failed due to " + e.getMessage());
			result.setSucceeded(false);
			Log.d(TAG, result.getResponse());
		}
		return result;
	}

	/**
	 * checks if the view is visible with the input view name
	 * 
	 * @param arguments
	 *            view name
	 * @return response with the status of the command
	 */
	private CommandResponse isViewVisibleByViewName(String[] arguments) {
		CommandResponse result = new CommandResponse();
		String command = "the command isViewVisible";
		try {
			String viewName = arguments[0];
			command += "(" + viewName + ")";
			View view = findViewByName(viewName);
			if (view.isShown()) {
				result.setResponse("view: " + viewName + " is visible");
				result.setSucceeded(true);
			} else {
				result.setResponse("view: " + viewName + " is not visible");
				result.setSucceeded(false);
			}
		} catch (Throwable e) {
			result.setResponse(command + "failed due to " + e.getMessage());
			Log.d(TAG, result.getResponse());
		}
		return result;
	}

	private CommandResponse swipeLeft(String[] parameters) {
		String command = "the command swipe left";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "attempting to swipe to the left";
			String[] dragParams = new String[] { Float.toString(0.9f), Float.toString(0.1f), Float.toString(0.5f), Float.toString(0.5f), Integer.toString(25), "relative" };
			CommandResponse dragResponse = drag(dragParams);
			result.setResponse(command + ",Response: " + response + ", " + dragResponse.getResponse());
			result.setSucceeded(dragResponse.isSucceeded());
			if (!result.isSucceeded()) {
				throw new Exception("Attempt to swipe to the left has failed");
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse swipeRight(String[] parameters) {
		String command = "the command swipe right";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "attempting to swipe to the right";
			String[] dragParams = new String[] { Float.toString(0.1f), Float.toString(0.9f), Float.toString(0.5f), Float.toString(0.5f), Integer.toString(25), "relative" };
			CommandResponse dragResponse = drag(dragParams);
			result.setResponse(command + ",Response: " + response + ", " + dragResponse.getResponse());
			result.setSucceeded(dragResponse.isSucceeded());
			if (!result.isSucceeded()) {
				throw new Exception("Attempt to swipe to the right has failed");
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnImagButton(String[] parameters) {
		String command = "the command click on image button";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "attempting to click on an image button";
			this.solo.clickOnImageButton(Integer.parseInt(parameters[0]));
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse clickOnImag(String[] parameters) {
		String command = "the command click on image";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = "attempting to click on an image button";
			solo.clickOnImage(Integer.parseInt(parameters[0]));
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse activateIntent(String[] arguments) {
		String command = null;
		CommandResponse result = new CommandResponse();
		try {
			command = "the command  activateIntent(" + arguments[0] + " " + arguments[1] + " " + arguments[2] + " " + arguments[3] + " " + arguments[4] + ")";

			/*
			 * if (arguments[0].equals("ACTION_VIEW")) { Intent webIntent = new
			 * Intent(Intent.ACTION_VIEW, Uri.parse(arguments[1])); Log.d(TAG, "Sending intent to "
			 * + solo.getClass().getSimpleName());
			 * solo.getCurrentActivity().startActivityForResult(webIntent, 1); }else if
			 * (arguments[0].equals("com.greenroad.PlayTrip")) {
			 */
			Log.d(TAG, "Sending intent");
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction(arguments[0]);
			for (int i = 1; i < arguments.length; i = i + 2) {
				broadcastIntent.putExtra(/*
										 * solo.getCurrentActivity(). getCallingPackage()+
										 */arguments[i], arguments[i + 1]);
			}
			this.solo.getCurrentActivity().sendBroadcast(broadcastIntent);
			result.setResponse("Activate Intent Succeeded");
			result.setSucceeded(true);
			// }
			/*
			 * else if (arguments[0].equals("ACTION_SEND")) { Intent sendIntent = new Intent();
			 * sendIntent.setAction(arguments[0]); for(int i = 1;i<arguments.length;i=i+2){
			 * Log.i(TAG, "check: " + arguments[i] + " " + arguments[i+1]);
			 * sendIntent.putExtra(solo.getCurrentActivity ().getCallingPackage()+
			 * "."+arguments[i],android.content.Intent.EXTRA_TEXT, arguments[i+1]); }
			 * solo.getCurrentActivity().startActivity(sendIntent); }
			 */
		} catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * gets the text of all the current text views
	 * 
	 * @return response with the status of the command
	 */
	private CommandResponse getCurrentTextViews(String[] arguments) {
		String command = "the command  getCurrentTextViews";
		CommandResponse result = new CommandResponse();
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments[0] + ")";
			List<TextView> textViews = this.solo.getCurrentViews(TextView.class);
			for (int i = 0; i < textViews.size(); i++) {
				response.append(i).append(",").append(textViews.get(i).getText().toString()).append(";");
			}
			result.setResponse(command + ",Response: " + response.toString());
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * will get the text view with the input index
	 * 
	 * @param arguments
	 *            index of the text view
	 * @return command response with the text of the input text view index
	 */
	private CommandResponse getTextView(String[] arguments) {
		String command = "the command  getTextView";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			command += "(" + arguments[0] + ")";
			response = this.solo.getCurrentViews(TextView.class).get(Integer.parseInt(arguments[0])).getText().toString();
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * gets the text of the view by the input index
	 * 
	 * @param arguments
	 *            the index of the view
	 * @return response with the status of the command
	 */
	private CommandResponse getTextViewIndex(String[] arguments) {
		String command = "the command  getTextViewIndex";
		CommandResponse result = new CommandResponse();
		StringBuilder response = new StringBuilder();
		try {
			command += "(" + arguments[0] + ")";
			List<TextView> textViews = this.solo.getCurrentViews(TextView.class);
			for (int i = 0; i < textViews.size(); i++) {
				if (arguments[0].trim().equals(textViews.get(i).getText().toString())) {
					response.append(i).append(";");
				}
			}
			result.setResponse(command + ",Response: " + response.toString());
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * gets the text by index
	 * 
	 * @param arguments
	 *            index
	 * @return response with the status of the command
	 */
	private CommandResponse getText(String[] arguments) {
		String command = "the command  getText";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			command += "(" + arguments[0] + ")";
			response = solo.getText(Integer.parseInt(arguments[0])).getText().toString();
			result.setOriginalCommand(command + ",Response: " + response);
			result.setResponse(response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}
	
	/**
	 * gets the text by id
	 * 
	 * @param arguments
	 *            index
	 * @return response with the status of the command
	 */
	private CommandResponse getTextById(String[] arguments) {
		String command = "the command  getTextById";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			command += "(" + arguments[0] + ")";
			ArrayList<View> views = solo.getViews();
			for (View view : views) {
//					Log.i(TAG, Integer.toString(view.getId()));
//					try {
//						TextView v = (TextView) view;
//						Log.i(TAG, v.getText().toString());
//					} catch (Exception e) {
//						// TODO: handle exception
//					}
				if(view.getId() == Integer.parseInt(arguments[0])){
					TextView v = (TextView) view;
					response = v.getText().toString();
				}
			}
			//response = v.getText().toString();
			result.setOriginalCommand(command + ",Response: " + response);
			result.setResponse(response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}
	
	

	private CommandResponse getViews(String[] arguments) {
		String command = "the command  getViews";
		CommandResponse result = new CommandResponse();
		String response = "";
		try {
			response = solo.getViews().toString();
			result.setResponse(command + ",Response: " + response);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * clicks on a menu item
	 * 
	 * @param arguments
	 *            the item to click on
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnMenuItem(String[] arguments) {
		String command = "the command  clickOnMenuItem";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clickOnMenuItem(arguments[0]);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * sends a single character key click
	 * 
	 * @param arguments
	 *            the char to click on
	 * @return response with the status of the command
	 */
	private CommandResponse sendKey(String[] arguments) {
		String command = "the command  sendKey";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.sendKey(Integer.parseInt(arguments[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * clicks in the input control on the input index
	 * 
	 * @param commandParameters
	 *            [0] the control id , [1] the index of the item to click
	 * @return response with the status of the command
	 * @throws Exception
	 */
	private CommandResponse clickInControlByIndex(String[] commandParameters) throws Exception {
		String command = "The command clickInControlByIndex";
		CommandResponse result = new CommandResponse();
		try {
			int controlId = Integer.parseInt(commandParameters[0]);
			int indexToClickOn = Integer.parseInt(commandParameters[1]);
			command += "(controlId: " + controlId + ")";
			command += "(indexToClickOn: " + indexToClickOn + ")";
			View control = this.solo.getView(controlId);
			if (control != null) {
				if (indexToClickOn < control.getTouchables().size()) {
					clickOnView(control.getTouchables().get(indexToClickOn), false, false);
					result.setResponse(command);
					result.setSucceeded(true);
				} else {
					result.setResponse(command + "failed due to: index to click in control is out of bounds. control touchables: " + control.getTouchables().size());
					result.setSucceeded(false);
				}
			} else {
				result.setResponse(command + "failed due to failed to find control with id: " + controlId);
				result.setSucceeded(false);
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * This method will search the requested view / control by its name in the currentViews <br>
	 * 
	 * @param viewName
	 *            the name of the view
	 * @return response with the status of the command
	 * @throws Exception
	 */
	private View findViewByName(String viewName) throws Exception {
		ArrayList<View> currentViews = this.solo.getCurrentViews();
		for (View view : currentViews) {
			if (view.getClass().getName().contains(viewName)) {
				return view;
			}
		}
		throw new Exception("View : " + viewName + " was not found in current views ");
	}

	/**
	 * click on the view id
	 * 
	 * @param arguments
	 *            id of the view to click
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnView(String[] arguments) {
		String command = "the command  clickOnView";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			View view = solo.getView(Integer.parseInt(arguments[0]));
			clickOnView(view, false, false);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Exception e) {
			result = handleException(command, e);
		}
		return result;
	}

	private CommandResponse verifyViewExistsByDescription(String[] arguments) {
		String command = "the command  verify view exists by description";
		CommandResponse result = new CommandResponse();
		if (arguments == null || arguments.length < 2) {
			result.setResponse("Not all parameters were received");
			result.setSucceeded(false);
		} else {
			try {
				boolean startsWith = false;
				boolean clickInSpecificPosition = false;
				float x, y;
				x = y = 0.0f;
				if (arguments.length > 2) {
					startsWith = Boolean.parseBoolean(arguments[2]);
					clickInSpecificPosition = Boolean.parseBoolean(arguments[3]);
					if (clickInSpecificPosition) {
						x = Float.parseFloat(arguments[4]);
						y = Float.parseFloat(arguments[5]);
					}
				}
				boolean click = arguments[1].equals("true") ? true : false;
				command += "(" + arguments[0] + ", also click: " + (click ? "yes" : "no") + ")";
				for (View view : solo.getViews()) {
					if (view == null || view.getContentDescription() == null) {
						continue;
					}
					if (view.getContentDescription() != null) {
						boolean condition;
						if (startsWith) {
							condition = view.getContentDescription().toString().startsWith(arguments[0]);
						} else {
							condition = view.getContentDescription().equals(arguments[0]);
						}
						if (condition && view.getVisibility() == View.VISIBLE) {
							try {
								if (click) {

									if (clickInSpecificPosition) {
										int[] location = new int[2];
										view.getLocationOnScreen(location);
										int addX = (int) (x * ((float) view.getWidth()));
										int addY = (int) (y * ((float) view.getHeight()));
										this.solo.clickOnScreen(x + addX, y + addY);
									} else {
										clickOnView(view, false, false);
									}

								}
							} catch (Throwable e) {

							}
							String addToCommand = (click ? (": Clicked on view with description: " + arguments[0]) : ("View with description: " + arguments[0] + "exists"));
							result.setResponse(command + addToCommand);
							result.setSucceeded(true);
							return result;
						}
					}
				}
				result.setResponse(command + " :No view with description " + arguments[0] + " found");
				result.setSucceeded(false);
				return result;
			} catch (Exception e) {
				result = handleException(command, e);
			}
		}
		return result;
	}

	private CommandResponse clickInList(String[] arguments) {
		String command = "the command  clickInList(";
		CommandResponse result = new CommandResponse();
		try {
			if (arguments.length == 1) {
				command += "(" + arguments[0] + ")";
				this.solo.clickInList(Integer.parseInt(arguments[0]));
			} else {
				command += "(" + arguments[0] + ", " + arguments[1] + ")";
				this.solo.clickInList(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
			}

			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	public CommandResponse getAllVisibleIds() {
		CommandResponse result = new CommandResponse();
		String response = "";
		for (View view : this.solo.getViews()) {
			try {
				response += view.getContentDescription() + ":" + Integer.toHexString(view.getId()) + ":" + view.getClass().getSimpleName() + "\r\n";
			} catch (Exception e) {
			}
		}
		result.setResponse(response);
		result.setSucceeded(true);
		return result;
	}

	private boolean scrollDownSafetly() {
		int index = 0;
		while (index < 5) {
			if (solo.scrollDown())
				return true;
			index++;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}
		return false;
	}

	private boolean scrollUpSafetly() {
		int index = 0;
		while (index < 5) {
			if (solo.scrollUp())
				return true;
			index++;
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
			}
		}
		return false;
	}

	public View findViewInList(View view, String description) {
		if (view == null)
			return null;
		View result;
		String tag;
		try {
			tag = (String) view.getTag();
		} catch (Throwable t) {
			Log.e(TAG, "error casting");
			tag = "";
		}
		Log.w(TAG, tag + " was found while searching list");
		if (view.getTag() != null && tag.equals(description)) {
			return view;
		} else {
			try {
				int max = ((ViewGroup) view).getChildCount();
				for (int i = 0; i < max; i++) {
					View nextChild = ((ViewGroup) view).getChildAt(i);
					result = findViewInList(nextChild, description);
					if (result != null)
						return result;
				}
			} catch (Exception e) {
			}

		}
		return null;
	}

	// //////////////////////////////////////////////////////////////////////////////////

	/**
	 * click on button with the input text
	 * 
	 * @param arguments
	 *            the text of the button to click
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnButtonWithText(String[] arguments) {
		String command = "the command  clickOnButton";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clickOnButton(arguments[0]);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * clears the text box
	 * 
	 * @param arguments
	 *            the id of the text box to clear
	 * @return response with the status of the command
	 */
	private CommandResponse clearEditTextByIndex(String[] arguments) {
		String command = "the command  clearEditText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			this.solo.clearEditText(Integer.parseInt(arguments[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * clears the text box
	 * 
	 * @param arguments
	 *            the id of the text box to clear
	 * @return response with the status of the command
	 */
	private CommandResponse clearEditText(String[] arguments) {
		String command = "the command  clearEditText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + arguments[0] + ")";
			final View view = solo.getView(Integer.parseInt(arguments[0]));
			if (view instanceof EditText) {
				this.solo.clearEditText((EditText) view);
				result.setResponse(command);
				result.setSucceeded(true);
			} else {
				result.setSucceeded(false);
				result.setResponse("View is instance of " + view.getClass().getSimpleName() + " instead of EditText");
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * checks if button is visible
	 * 
	 * @param arguments
	 *            [0] search button by id or text, [1] the text or id to search the button
	 * @return response with the status of the command
	 */
	private CommandResponse isButtonVisible(String[] arguments) {
		String command = "the command isButtonVisible";
		CommandResponse result = new CommandResponse();
		boolean isVisible = false;
		try {
			String searchButtonByKey = arguments[0];
			command += "(findButtonBy: " + arguments[0] + ")";
			if (searchButtonByKey.equalsIgnoreCase("text")) {
				String searchButtonByTextValue = arguments[1];
				command += "(Value: " + searchButtonByTextValue + ")";
				isVisible = isButtonVisibleByText(searchButtonByTextValue);
			} else if (searchButtonByKey.equalsIgnoreCase("id")) {
				int searchButtonByIntValue = Integer.parseInt(arguments[1]);
				command += "(Value: " + searchButtonByIntValue + ")";
				isVisible = isButtonVisibleById(searchButtonByIntValue);
			}
		} catch (Throwable e) {
			return handleException(command, e);
		}
		if (isVisible) {
			result.setResponse(command + " is visible");
			result.setSucceeded(true);
		} else {
			result.setResponse(command + " is not visible");
			result.setSucceeded(true);
		}
		return result;
	}

	/**
	 * checks if the button with input text is visble
	 * 
	 * @param buttonText
	 *            the text of the button
	 * @return response with the status of the command
	 * @throws Exception
	 */
	private boolean isButtonVisibleByText(String buttonText) throws Exception {
		Button button = this.solo.getButton(buttonText);
		if (button != null) {
			return button.isShown();
		} else {
			throw new Exception("Button with text: " + buttonText + " was not found");
		}
	}

	private CommandResponse getTable(String[] params) {
		CommandResponse result = new CommandResponse();
		String response = "the command getTable";
		String requestedValue = null;
		String language = params[0];
		String buttonText = params[1];
		ArrayList<View> array = solo.getCurrentViews();
		for (View view : array) {
			if(view instanceof android.widget.TableLayout){
				android.widget.TableLayout table = (TableLayout) view;
				Log.i(TAG, Integer.toString(table.getChildCount()));
				for (int i = 0; i < table.getChildCount(); i++) {
					try {
						LinearLayout row = (LinearLayout) table.getChildAt(i);
						for (int j = 0; j < row.getChildCount(); j++) {//run over the textviews in the row
							TextView t = (TextView) row.getChildAt(j);
							if(t.getText().toString().contains(buttonText)){
								if(language.equalsIgnoreCase("HE"))
									requestedValue = ((TextView) row.getChildAt(j-1)).getText().toString();
								else
									requestedValue = ((TextView) row.getChildAt(j+1)).getText().toString();
							}
						}
					} catch (Exception e) {
						if(e!=null)	
							Log.e(TAG, e.getMessage());
					}
				}
			}
		}
		String[] arr = new String[1];
		arr[0] = requestedValue;
		result.setReturnedValues(arr);
		result.setResponse(response);
		result.setSucceeded(true);
		return result;
	}

	/**
	 * checks if the button with input id is visible
	 * 
	 * @param buttonId
	 *            the id of the button
	 * @return response with the status of the command
	 * @throws Exception
	 */
	private boolean isButtonVisibleById(int buttonId) throws Exception {
		ArrayList<Button> currentButtons = this.solo.getCurrentViews(Button.class);
		for (Button button : currentButtons) {
			if (button.getId() == buttonId) {
				if (button.isShown()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * click on button with the input id
	 * 
	 * @param params
	 *            the id of the button
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnButton(String[] params) {
		String command = "the command  clickOnButton";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + ")";
			this.solo.clickOnButton(Integer.parseInt(params[0]));
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			return handleException(command, e);
		}
		return result;
	}

	/**
	 * enter text to the input text box id with the input text
	 * 
	 * @param params
	 *            [0] the id of the text box , [1] the text to enter
	 * @return response with the status of the command
	 */

	private CommandResponse enterText(String[] params) {
		String command = "the command  enterText";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + "," + params[1] + ")";
			final View view = solo.getView(Integer.parseInt(params[0]));
			if (view instanceof EditText) {
				this.solo.enterText((EditText) view, params[1]);
				result.setResponse(command);
				result.setSucceeded(true);
			} else {
				result.setResponse("try to enter text in text view - View is instance of " + view.getClass().getSimpleName() + " instead of EditText");
				result.setSucceeded(false);
			}
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;

	}

	/**
	 * enter text to the input text box id with the input text
	 * 
	 * @param params
	 *            [0] the id of the text box , [1] the text to enter
	 * @return response with the status of the command
	 */

	private CommandResponse setText(String[] params) {
		String command = "the command  setText";
		CommandResponse result = new CommandResponse();
		command += "(" + params[0] + ")";
		try {
			String text = params[0].toString();
			Log.i(TAG, "About to set text: " + text);
			command += "(text: " + text + ")";
			for (Character c : text.toCharArray()) {
				instrumentation.sendKeyDownUpSync(keys.get(c));
			}
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * enter text to the input text box id with the input text
	 * 
	 * @param params
	 *            [0] the id of the text box , [1] the text to enter
	 * @return response with the status of the command
	 */

	private CommandResponse enterTextByIndex(String[] params) {
		String command = "the command  enterText By Index";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + "," + params[1] + ")";
			this.solo.enterText(Integer.parseInt(params[0]), params[1]);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;

	}

	/**
	 * click on the input text
	 * 
	 * @param params
	 *            the text to click on
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnText(String[] params) {
		String command = "the command clickOnText";
		CommandResponse result = new CommandResponse();
		try {
			if (params.length == 1) {
				command += "(" + params[0] + ")";
				solo.clickOnText(params[0]);
			}
			if (params.length == 2) {
				command += "(" + params[0] + "," + params[1] + ")";
				solo.clickOnText(params[0], Integer.parseInt(params[1]));

			}
			if (params.length == 3) {
				command += "(" + params[0] + "," + params[1] + "," + params[2] + ")";
				solo.clickOnText(params[0], Integer.parseInt(params[1]), Boolean.parseBoolean(params[2]));
			}

			result.setResponse(command + " : requested text was found and clicked on");
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;

	}

	/**
	 * 
	 * @param params
	 *            x and y coordinates to click and whether the the coordinates are relative or
	 *            absolute
	 * @return response with the status of the command
	 * 
	 */

	private CommandResponse clickOnScreen(String[] params) {
		String command = "the command click on screen: ";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + "," + params[1] + "," + params[2] + ")";
			boolean relative = params[2].equals("relative");
			float x = Float.parseFloat(params[0]);
			float y = Float.parseFloat(params[1]);
			if (relative) {
				x = SoloUtils.convertRelativeToAbsolute(x, AXIS.X, this.solo);
				y = SoloUtils.convertRelativeToAbsolute(y, AXIS.Y, this.solo);
			}

			this.solo.clickOnScreen(x, y);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * 
	 * @param params
	 *            x and y start coordinates, x and y end coordinates and whether the the coordinates
	 *            are relative or absolute
	 * @return response with the status of the command
	 * 
	 */

	private CommandResponse drag(String[] params) {
		String command = "the command click on screen: ";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + params[0] + "," + params[1] + "," + params[2] + "," + params[3] + "," + params[4] + "," + params[5] + ")";
			int steps = Integer.parseInt(params[4]);
			boolean relative = params[5].equals("relative");
			float x1 = Float.parseFloat(params[0]);
			float x2 = Float.parseFloat(params[1]);
			float y1 = Float.parseFloat(params[2]);
			float y2 = Float.parseFloat(params[3]);
			if (relative) {
				x1 = SoloUtils.convertRelativeToAbsolute(x1, AXIS.X, this.solo);
				x2 = SoloUtils.convertRelativeToAbsolute(x2, AXIS.X, this.solo);
				y1 = SoloUtils.convertRelativeToAbsolute(y1, AXIS.Y, this.solo);
				y2 = SoloUtils.convertRelativeToAbsolute(y2, AXIS.Y, this.solo);
			}
			this.solo.drag(x1, x2, y1, y2, steps);
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * click on hardware button
	 * 
	 * @param keyString
	 *            an hardware button to click
	 * @return response with the status of the command
	 */
	private CommandResponse clickOnHardware(String[] keyString) {
		String command = "the command clickOnHardware";
		CommandResponse result = new CommandResponse();
		try {
			command += "(" + keyString[0] + ")";
			int key = (keyString[0] == "HOME") ? KeyEvent.KEYCODE_HOME : (keyString[0] == "BACK") ? KeyEvent.KEYCODE_BACK : (keyString[0] == "TAB") ? KeyEvent.KEYCODE_CTRL_LEFT:(keyString[0] == "ENTER")? KeyEvent.KEYCODE_ENTER : KeyEvent.KEYCODE_BACK ;
			//int key = (keyString[0] == "BACK") ? KeyEvent.KEYCODE_HOME : KeyEvent.KEYCODE_BACK;
			this.instrumentation.sendKeyDownUpSync(key);
			result.setResponse("click on hardware");
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * NOTE ! THIS METHOD MUST BE THE FIRST COMMAND BEFORE ANY OTHER COMMAND the launch method will
	 * launch the instrumentation of the application
	 * 
	 * @return return status of the operation
	 */
	private CommandResponse launch() {
		Log.i(TAG, "Robotium: About to launch application");
		CommandResponse result = new CommandResponse();
		String command = "the command  launch";
		try {
			this.solo = this.soloProvider.getSolo();
			result.setResponse(command);
			result.setSucceeded(true);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}

	/**
	 * handle an exception
	 * 
	 * @param command
	 *            the command that caused the exception
	 * @param e
	 *            the exception that was thrown
	 * @return response with the status of the command
	 */
	private CommandResponse handleException(final String command, Throwable e) {
		CommandResponse result = new CommandResponse();
		result.setResponse(command + " failed due to " + e.getMessage());
		result.setSucceeded(false);
		Log.e(TAG, result.getResponse());
		return result;
	}

	/**
	 * clicks on a view
	 * 
	 * @param view
	 *            the view to click
	 * @throws Exception
	 */
	private void clickOnView(View view, boolean immediatly, boolean longClick) throws Exception {
		if (view.isShown()) {
			if (longClick) {
				this.solo.clickLongOnView(view);
			} else {
				this.solo.clickOnView(view, immediatly);
			}
		} else {
			throw new Exception("clickOnView FAILED view: " + view.getClass().getSimpleName() + " is not shown");
		}
	}
	
	/**
	 * clicks on a view
	 * 
	 * @param view
	 *            the view to click
	 * @throws Exception
	 */
	private CommandResponse initWebElementAndExecuteMethode(String[] params) throws Exception {
		Log.i(TAG, "Robotium: About to perform action on web element by method initElementAndExecuteMethode()");
		CommandResponse result = new CommandResponse();
		String command = "the initElementAndExecuteMethode  init and Execute: ";
		String locatorType ,  locator,   methodNameStr , TextToInsert;
		try {
			locatorType	 = params[0];
			locator 	 = params[1];
			methodNameStr= params[2];
			TextToInsert = params[3];
			boolean returnForEait = initElementAndExecuteMethode(locatorType ,  locator,   methodNameStr , TextToInsert);
			result.setResponse(command);
			result.setSucceeded(true);
			String[] returnVal = new String [1];
			returnVal[0] =String.valueOf(returnForEait);
			result.setReturnedValues(returnVal);
		} catch (Throwable e) {
			result = handleException(command, e);
		}
		return result;
	}
	
	public boolean  initElementAndExecuteMethode(String locatorType , String locator,  String methodNameStr ,String TextToInsert) throws Exception {
		
		
		WebElement element;
		ELocatorType type = ELocatorType.ID;
		type.initELocatorType(type, locatorType);
		boolean dome =false;
		SoloWebViewMethod methodeName = SoloWebViewMethod.ClickOnWebElement;
		methodeName.initMethodName(methodeName, methodNameStr);
//		for (WebElement webElement : solo.getCurrentWebElements()) {
//			
//		}
		switch (methodeName){
			
			case ClickOnWebElement:
				
			clickOnWebElement(locator, methodNameStr, type);
			break;
		case waitForWebElement:
			
			   return waitForWebElement(locator, methodNameStr, type);
		case clearTextInWebElement:
			clearTextInWebElement(locator, methodNameStr, type);
			break;
		case enterTextInWebElement:
			enterTextInWebElement(locator, methodNameStr, TextToInsert, type);
			break;
		default:
			break;
				
				
		}
		return dome;
		
	}

	private void enterTextInWebElement(String locator, String methodNameStr, String TextToInsert, ELocatorType type) throws Exception {
		switch (type) {
		case ID:
				solo.typeTextInWebElement(By.id(locator),TextToInsert);
			break;
		case NAME:
			solo.enterTextInWebElement(By.name(locator),TextToInsert);
			break;
		case CLASS:
			solo.enterTextInWebElement(By.className(locator),TextToInsert);
			break;
		case CSS:
			solo.enterTextInWebElement(By.cssSelector(locator),TextToInsert);
			break;
		case XPATH:
			solo.typeTextInWebElement(By.xpath(locator),TextToInsert);
			break;
		default:
			Log.e(TAG,"element " + locator +  " in method" + methodNameStr + " wasn't Found");
			throw new Exception();
		}
	}

	private void clearTextInWebElement(String locator, String methodNameStr, ELocatorType type) throws Exception {
		switch (type) {
		case ID:
				solo.clearTextInWebElement(By.id(locator));
			break;
		case NAME:
			solo.clearTextInWebElement(By.name(locator));
			break;
		case CLASS:
			solo.clearTextInWebElement(By.className(locator));
			break;
		case CSS:
			solo.clearTextInWebElement(By.cssSelector(locator));
			break;
		case XPATH:
			solo.clearTextInWebElement(By.xpath(locator));
			break;
		default:
			Log.e(TAG,"element " + locator +  " in method" + methodNameStr + " wasn't Found");
			throw new Exception();
		}
	}

	private boolean  waitForWebElement(String locator, String methodNameStr, ELocatorType type) throws Exception {
		Log.e(TAG,"element " + locator +  " in method" + methodNameStr + " wasn't Found");
		boolean exist=false;
		switch (type) {
		case ID:
			exist= solo.waitForWebElement(By.id(locator));
			break;
		case NAME:
			exist= solo.waitForWebElement(By.name(locator));
			break;
		case CLASS:
			exist= solo.waitForWebElement(By.className(locator));
			break;
		case CSS:
			exist= solo.waitForWebElement(By.cssSelector(locator));
			break;
		case XPATH:
			exist= solo.waitForWebElement(By.xpath(locator));
			break;
		default:
			Log.e(TAG,"element " + locator +  " in method" + methodNameStr + " wasn't Found");
			throw new Exception();
		}
		return exist;
	}

	private void clickOnWebElement(String locator, String methodNameStr, ELocatorType type) throws Exception {
		switch (type) {
		case ID:
				solo.clickOnWebElement(By.id(locator));
			break;
		case NAME:
			solo.clickOnWebElement(By.name(locator));
			break;
		case CLASS:
			solo.clickOnWebElement(By.className(locator));
			break;
		case CSS:
			solo.clickOnWebElement(By.cssSelector(locator));
			break;
		case XPATH:
			solo.clickOnWebElement(By.xpath(locator));
			break;
		default:
			Log.e(TAG,"element " + locator +  " in method" + methodNameStr + " wasn't Found");
			throw new Exception();
		}
	}


	// *********************** updating methods

	@SuppressLint("NewApi")
	private CommandResponse closeActivity(String[] params) {
		try {
			String activityName = params[0];
			CommandResponse response = new CommandResponse();
			response.setOriginalCommand("closeActivity");
			Activity activity = solo.getCurrentActivity();
			if (activity.getClass().getSimpleName().equals(activityName)) {
				activity.finish();
				int currentapiVersion = android.os.Build.VERSION.SDK_INT;
				if (currentapiVersion > android.os.Build.VERSION_CODES.JELLY_BEAN) {
					if (!activity.isDestroyed()) {
						response.setResponse("Failed to destroy activity " + activityName);
						response.setSucceeded(false);
						return response;
					}
				}
			} else {
				response.setSucceeded(false);
				response.setResponse("Current activity is " + activity.getClass().getSimpleName() + " while expecting activity " + activityName);
				return response;
			}
			response.setResponse("Activity " + activityName + " was closed");
			response.setSucceeded(true);
			return response;

		} catch (Exception e) {
			return handleException("closeActivity", e);
		}
	}

	private CommandResponse clickOnMenuItemById(String[] params) {
		CommandResponse response = new CommandResponse();
		int itemId = Integer.valueOf(params[0]);
		try {
			response.setOriginalCommand("clickOnMenuItemById");
			instrumentation.invokeMenuActionSync(solo.getCurrentActivity(), itemId, 0);
			response.setResponse("Success to invokeMenuActionSync and click on menue item - item id  " + itemId);
			response.setSucceeded(true);
		} catch (Throwable e) {
			response.setResponse("Failed to invokeMenuActionSync and click on menue item - item id  " + itemId);
			response.setSucceeded(false);
			return response;
		}
		return response;
	}

	private CommandResponse closeApplication() {
		CommandResponse response = new CommandResponse();
		String command = "the command closeApplication";
		try {
			response.setOriginalCommand("closeApplication");
			Log.i(TAG, "Robotium: About to close application");
			solo.finishOpenedActivities();
		} catch (Exception e) {
			return handleException("Failed to close Application", e);
		}
		response.setResponse(command);
		response.setSucceeded(true);
		return response;
	}
	
	private CommandResponse closeSpecificApplication(String[] params) {
		CommandResponse response = new CommandResponse();
		String command = "the command closeApplication";
		String packageAppName = null;
		try {
			packageAppName = params[0];
			response.setOriginalCommand("closeSpecificApplication");
			Log.i(TAG, "Robotium: About to close Specific application + " + packageAppName);
			solo.finishOpenedActivities();
			android.os.Process.killProcess(android.os.Process.getUidForName(packageAppName));
			
			//List<RunningTaskInfo> runningTasks = solo.getActivityMonitor().//getRunningTask(1);
			
					
			
			
			if(android.os.Process.getUidForName(packageAppName)!=-1){//if closing didn't succeed
				response.setSucceeded(false);
				Log.i(TAG, "Closing didn't succeed for: " + packageAppName);
				return response;
			}
			
		} catch (Exception e) {
			return handleException("Failed to kill " + packageAppName, e);
		}
		response.setResponse(command);
		response.setSucceeded(true);
		return response;
	}

	private CommandResponse clickOnViewByIndex(String[] params) {
		Log.i(TAG, "About to click on view by index");
		CommandResponse response = new CommandResponse();
		String command = "the command clickOnViewByIndex";
		try {
			response.setOriginalCommand("clickOnViewByIndex");
			int viewIndex = Integer.valueOf(params[0]);
			command += "(" + viewIndex + ")";
			solo.clickOnView(solo.getCurrentViews().get(viewIndex));
			response.setResponse("sucesse to run " + command);
			response.setSucceeded(true);
		} catch (Throwable e) {
			return handleException("failed to run " + command, e);
		}
		return response;
	}

	private CommandResponse scrollLeft() {
		CommandResponse response = new CommandResponse();
		String command = "the command Scroll Left";
		try {
			Log.i(TAG, "About to scroll Left ");
			response.setOriginalCommand("scrollLeft");
			solo.scrollToSide(LEFT);//
			response.setResponse("sucesse to run " + command);
			response.setSucceeded(true);
		} catch (Throwable e) {
			return handleException("failed to run " + command, e);
		}

		return response;

	}

	private CommandResponse scrollRight() {
		CommandResponse response = new CommandResponse();
		String command = "the command Scroll Right";
		try {
			Log.i(TAG, "About to scroll Left ");
			response.setOriginalCommand("scrollRight");
			solo.scrollToSide(RIGHT);//
			response.setResponse("sucesse to run " + command);
			response.setSucceeded(true);
		} catch (Throwable e) {
			return handleException("failed to run " + command, e);
		}

		return response;

	}

	private CommandResponse scrollUp() {
		CommandResponse response = new CommandResponse();
		String command = "the command Scroll Up";
		try {
			Log.i(TAG, "About to scroll Up ");
			response.setOriginalCommand("scrollUp");
			solo.scrollToSide(UP);//
			response.setResponse("sucesse to run " + command);
			response.setSucceeded(true);
		} catch (Throwable e) {
			return handleException("failed to run " + command, e);
		}
		return response;

	}

	private CommandResponse scrollDown() {
		CommandResponse response = new CommandResponse();
		String command = "the command Scroll Down";
		try {
			Log.i(TAG, "About to scroll Down ");
			response.setOriginalCommand("scrollDown");
			solo.scrollToSide(DOWN);//
			response.setResponse("sucesse to run " + command);
			response.setSucceeded(true);
		} catch (Throwable e) {
			return handleException("failed to run " + command, e);
		}
		return response;

	}

	/**
	 * This method will click on a radioButton by its index<br>
	 * 
	 * @param arguments
	 * @author eran_g 28.11.12
	 */
	private CommandResponse clickOnRadioButton(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("clickOnRadioButton");
		try {
			int radioButtonIndex = Integer.parseInt(params[0]);
			Log.i(TAG, "Robotium: About to clickOnRadioButton in index: " + radioButtonIndex);
			int counter = 0;
			for (View v : solo.getCurrentViews()) {
				if (v instanceof RadioButton) {
					counter++;
				}
			}
			if (radioButtonIndex <= counter) {
				solo.clickOnRadioButton(radioButtonIndex);
			} else {
				throw new Exception("Radion button index is invalid, found " + counter + " radio buttons and requested index was: " + radioButtonIndex);
			}
			response.setSucceeded(true);
			response.setResponse("Clicked on radio button with index " + radioButtonIndex);
		} catch (Exception e) {
			return handleException("Failed to run command " + response.getOriginalCommand(), e);
		}
		return response;
	}
	
	/**
	 * This method will return  on a radioButton status by its index<br>
	 * 
	 * @param arguments
	 * @author livnat 6.14
	 */
	private CommandResponse isRadioButtoncheckedByIndex(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("isRadioButtonchecked");
		String retParams[] =new String[2];
		try {
			int radioButtonIndex = Integer.parseInt(params[0]);
			Log.i(TAG, "Robotium: About to pull radio btn  checked status in index: " + radioButtonIndex);
			int counter = 0;
			for (View v : solo.getCurrentViews()) {
				if (v instanceof RadioButton) {
					counter++;
				}
			}
			if (radioButtonIndex <= counter) {
				retParams[0]= String.valueOf(solo.isRadioButtonChecked(radioButtonIndex));
				response.setParams(retParams);
			} else {
				throw new Exception("Radion button index is invalid, found " + counter + " radio buttons and requested index was: " + radioButtonIndex);
			}
			response.setSucceeded(true);
			response.setResponse("Clicked on radio button with index " + radioButtonIndex);
		} catch (Exception e) {
			return handleException("Failed to run command " + response.getOriginalCommand(), e);
		}
		return response;
	}
	
	/**
	 * This method will return  on a radioButton status by its index<br>
	 * 
	 * @param arguments
	 * @author livnat 6.14
	 */
	private CommandResponse isRadioBtnIscheckedByText(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("isRadioBtnIscheckedByText");
		String retParams[] =new String[1];
		try {
			String checkButtonText = params[0];
			Log.i(TAG, "Robotium: About to pull radioBtn has  checked status in text: " + checkButtonText);
			
			retParams[0]= String.valueOf(solo.isRadioButtonChecked(checkButtonText));
			response.setParams(retParams);
			
			response.setSucceeded(true);
			response.setResponse("validate if radioBtn status is checked for radioBtn  with text " + checkButtonText);
		} catch (Exception e) {
			return handleException("Failed to run command " + response.getOriginalCommand(), e);
		}
		return response;
	}
	
	/**
	 * This method will return  on a checkBox status by its index<br>
	 * 
	 * @param arguments
	 * @author livnat 6.14
	 */
	private CommandResponse isCheckBoxIscheckedByIndex(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("isCheckBoxIscheckedByIndex");
		String retParams[] =new String[1];
		try {
			int checkButtonIndex = Integer.parseInt(params[0]);
			Log.i(TAG, "Robotium: About to pull checkBox has  checked status in index: " + checkButtonIndex);
			int counter = 0;
			for (View v : solo.getCurrentViews()) {
				if (v instanceof CheckBox) {
					counter++;
				}
			}
			if (checkButtonIndex <= counter) {
				retParams[0]= String.valueOf(solo.isCheckBoxChecked(checkButtonIndex));
				response.setParams(retParams);
			} else {
				throw new Exception("CheckBox button index is invalid, found " + counter + " Check Box  status requested index was: " + checkButtonIndex);
			}
			response.setSucceeded(true);
			response.setResponse("validate if checkobox status is checked for checkoBox  with index " + checkButtonIndex);
		} catch (Exception e) {
			return handleException("Failed to run command " + response.getOriginalCommand(), e);
		}
		return response;
	}
	/**
	 * This method will return  on a checkBox status by its Text<br>
	 * 
	 * @param arguments
	 * @author livnat 6.14
	 */
	private CommandResponse isCheckBoxIscheckedByText(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("isCheckBoxIschecked");
		String retParams[] =new String[1];
		try {
			String checkButtonText = params[0];
			Log.i(TAG, "Robotium: About to pull checkBox has  checked status in text: " + checkButtonText);
			
			retParams[0]= String.valueOf(solo.isCheckBoxChecked(checkButtonText));
			response.setParams(retParams);
			
			response.setSucceeded(true);
			response.setResponse("validate if checkobox status is checked for checkoBox  with text " + checkButtonText);
		} catch (Exception e) {
			return handleException("Failed to run command " + response.getOriginalCommand(), e);
		}
		return response;
	}

	private CommandResponse clickOnActionBarHomeButton() {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("clickOnActionBarHomeButton");
		try {
			solo.clickOnActionBarHomeButton();
			response.setResponse("Clicked on action bar home button");
			response.setSucceeded(true);
		} catch (Exception e) {
			return handleException("Failed ro eun command " + response.getOriginalCommand(), e);
		}
		return response;
	}

	private CommandResponse showMenuOptions() {
		Log.i(TAG, "Robotium: About to show menu options");
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("the command show menu options");
		try {
			solo.sendKey(Solo.MENU);
			response.setSucceeded(true);
			response.setResponse("Showed manu options");
		} catch (Exception e) {
			return handleException("Failed " + response.getOriginalCommand(), e);
		}
		return response;
	}

	private CommandResponse pull(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("pull");
		DataInputStream in = null;
		FileInputStream fstream = null;
		BufferedReader br = null;
		try {
			StringBuilder allText = new StringBuilder();
			fstream = new FileInputStream(params[0]);
			// Get the object of DataInputStream
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String line;
			while ((line = br.readLine()) != null) {
				allText.append(line);
			}
			response.setResponse(allText.toString());
			response.setSucceeded(true);
		} catch (Exception e) {
			return handleException(response.getOriginalCommand(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					return handleException("Failed closing file", e);
				}
			}

		}
		return response;
	}

	@SuppressLint("NewApi")
	private CommandResponse createFileInServer(String[] params) {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("createFileInServer");
		String fileName = params[0];
		String content = params[1];
		boolean append = Boolean.parseBoolean(params[2]);
		FileOutputStream fos = null;
		FileWriter out = null;
		try {
			if (append) {
				byte[] data = Base64.decode(content, Base64.URL_SAFE);
				fos = new FileOutputStream(fileName);
				fos.write(data);
			} else {
				out = new FileWriter(fileName);
				out.write(content);
			}

			Log.d(TAG, "run the command:" + response.getOriginalCommand());
			response.setResponse("Creating file " + fileName);
		} catch (Exception e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					return handleException("Failed closing file", e);
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					return handleException("Failed closing file", e);
				}
			}
		}
		return response;
	}

	private CommandResponse setPreferanceCompleteRideCounter() {
		// update the compte ride parameter in the preferences to be 0 so we won't get any popup for
		// rate the App
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("setPreferanceCompleteRideCounter");
		long valueOfCounter = 0;
		try {
			SharedPreferences sharedPreferences = solo.getCurrentActivity().getApplicationContext().getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
			sharedPreferences.edit().putInt(COMPLETE_ORDERS, 0).commit();
			valueOfCounter = sharedPreferences.getLong(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("setPreferanceCompleteRideCounter + value of counter is " + String.valueOf(valueOfCounter));
		response.setSucceeded(true);
		return response;
	}
	private CommandResponse setPreferanceFirstPromoInvite() {
		// update the compte ride parameter in the preferences to be 0 so we won't get any popup for
		// rate the App
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("setPreferanceFirstPromoInvite");
		long valueOfCounter = 0;
		try {
			SharedPreferences sharedPreferences = solo.getCurrentActivity().getApplicationContext().getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
			sharedPreferences.edit().putBoolean(FIRST_PROMO_INVITE, false).commit();
			valueOfCounter = sharedPreferences.getLong(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);
		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("setPreferanceFirstPromoInvite + value of counter is " + String.valueOf(valueOfCounter));
		response.setSucceeded(true);
		return response;
	}
	
	private CommandResponse deleteAppData() {
		CommandResponse response = new CommandResponse();
		response.setOriginalCommand("deleteAppData");
		try {
			
			Editor editor = solo.getCurrentActivity().getApplicationContext().getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE).edit();
			editor.clear();
			editor.commit();
		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("deleteAppData - delet all app cookies ");
		response.setSucceeded(true);
		return response;
	}

	private CommandResponse setPreferanceInUserApp(String[] params) {
		// update the compte ride parameter in the preferences to be 0 so we won't get any popup for
		// rate the App
		CommandResponse response = new CommandResponse();
		String SettingFileName = params[0];
		String KeyPreferanceNameToBeChange = params[1];
		response.setOriginalCommand("setPreferanceCompleteRideCounter");
		long valueOfCounter = 0;
		try {
			SharedPreferences sharedPreferences = solo.getCurrentActivity().getApplicationContext().getSharedPreferences(SettingFileName, Context.MODE_PRIVATE);
			sharedPreferences.edit().putInt(KeyPreferanceNameToBeChange, 0).commit();
			valueOfCounter = sharedPreferences.getLong(SettingFileName, Context.MODE_PRIVATE);
		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("setPreferanceInUserApp + value of preferance key  is " + String.valueOf(valueOfCounter));
		response.setSucceeded(true);
		return response;
	}

	public CommandResponse launchServerEnviroment(String[] params) {
		CommandResponse response = new CommandResponse();
		Activity lastActivity;
		String server = params[0];
		try {
			response.setOriginalCommand("set server environment" + server);
			Intent intent = new Intent("com.gettaxi.android.OPEN_URL");
			intent.putExtra("DATA", server);
			// TODO: Assert that the last activity is not null and handle the exeception
			lastActivity = solo.getActivityMonitor().getLastActivity();
			if (lastActivity == null || lastActivity.isFinishing()) {
				solo.getCurrentActivity();
				if (lastActivity == null || lastActivity.isFinishing()) {				
					response.setResponse("set the server enviroment to run with server : " + server + "   can't get last activity , the returned value is  : "+ lastActivity );
					response.setSucceeded(false);
				} else{
					lastActivity.sendBroadcast(intent);
				}
				return response;
			} else {
				lastActivity.sendBroadcast(intent);
			}

		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("set the server enviroment to run with  + value of preferance key  is " + server +"last activity , the returned value is  : "+ lastActivity);
		response.setSucceeded(true);
		return response;
	}
	
	public CommandResponse launchServerEnviromentWeb(String[] params) {
		CommandResponse response = new CommandResponse();
		String server = params[0];
		String packageName = params[1];
		try {
			response.setOriginalCommand("set server environment: " + server);
			Intent intent = new Intent("com.gettaxi.android.OPEN_URL");
			intent.putExtra("DATA", server);

			
			Log.d(TAG, "server: " + server);
			Log.d(TAG, "packageName: " + packageName);
			
			// TODO: Assert that the last activity is not null and handle the exeception
			Activity lastActivity = solo.getActivityMonitor().getLastActivity();
			
			Log.d(TAG, "lastActivity: " + lastActivity);
			
			
			Application app = lastActivity.getApplication();
			
			Log.d(TAG, "app: " + app.toString());
			
			
			intent = app.getPackageManager().getLaunchIntentForPackage(packageName);
			
			Log.d(TAG, "intent:" + intent);
			
			
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			app.startActivity(intent);
			
			
			if (lastActivity == null || lastActivity.isFinishing()) {
				solo.getCurrentActivity();
				if (lastActivity == null || lastActivity.isFinishing()) {				
					response.setResponse("set the server enviroment to run with server : " + server + "   can't get last activity , the returned value is  : "+ lastActivity );
					response.setSucceeded(false);
				} else{
					lastActivity.sendBroadcast(intent);
				}
				return response;
			} else {
				lastActivity.sendBroadcast(intent);
			}

		} catch (Throwable e) {
			return handleException("Failed: " + response.getOriginalCommand(), e);
		}
		response.setResponse("set the server environment to run with  + value of preferance key  is " + server);
		response.setSucceeded(true);
		return response;
	}

	private CommandResponse countItemsInList(String[] params) {
		
		String[] array = new String[1];
		CommandResponse result = new CommandResponse();
		String command = "the command \"getListView\": ";
		try{
			final View view = solo.getView(Integer.parseInt(params[0]));
			if (view instanceof ListView) {
				ListView listView = (ListView) view;
				int count = listView.getChildCount();
				array[0] = String.valueOf(count);
				result.setReturnedValues(array);
				result.setResponse(command);
				result.setSucceeded(true);
				return result;
			} else {
				result.setResponse("View is instance of " + view.getClass().getSimpleName() + " instead of ListView");
			}
		} catch (Throwable e) {
			return handleException("Failed: " + result.getOriginalCommand(), e);
		}
		array[0] = String.valueOf(-1);
		result.setSucceeded(true);
		return result;
	}
	
	public CommandResponse getIndexListItemByText(String[] params){
		String[] array = new String[1];
		CommandResponse result = new CommandResponse();
		String command = "the command \"clickOnListItemByText\": ";
		String searchedItemText= params[0]; 
		try{
			int textid= Integer.valueOf(params[1]);
			for (View view : solo.getCurrentViews()) {
				Log.d(TAG, "creating batch of  lists views ");
				if (view instanceof ListView) {
					ListView listView = (ListView) view;
					int count = listView.getChildCount();
					Log.d(TAG, "get childe of lists of views count = " +count);
					for( int i =0 ; i<count ;i++)
						{	// assuming that we have only oe list view on the current main activity 
						Log.d(TAG, "childe index : "+i);
	//					View view2 = ;
						ViewGroup latout= ((ViewGroup) listView.getChildAt(i));
						TextView tv=(TextView) latout.findViewById(textid);
						String   context=tv.getText().toString();
						
	//						String   context= ((TextView) listView.getChildAt(i)).getText().toString();
							
							Log.d(TAG, "context text : " + context.toString());
							if ( context.contains(searchedItemText)){
								Log.d(TAG, "context text contains : " + searchedItemText);
	//							this.solo.clickInList(i);// click on the item in the list 
								array[0] = String.valueOf(i);
								result.setReturnedValues(array);
								result.setResponse(command);
								result.setSucceeded(true); 
								return result;
							}
						}
					}
			}
		} catch (Throwable e) {
			return handleException("Failed: " + result.getOriginalCommand(), e);
		}
		array[0] = String.valueOf(-1);
		result.setReturnedValues(array);
		result.setSucceeded(true);
		result.setResponse("can't find searched text in view the searched text is :  " + searchedItemText + " in method clickOnListItemByText");
		return result;
	}
	public CommandResponse getListItemContentByText(String[] params){
		String[] array = new String[1];
		CommandResponse result = new CommandResponse();
		String command = "the command \"GetTextInListItemByText\": ";
		String searchText = params[0]; //order id in dbx history
		Log.d(TAG, "Search Text: " + searchText);
		try{
			int columnToSearchIn = Integer.valueOf(params[1]);
			Log.d(TAG, "Column To Search In: " + columnToSearchIn);
			int targetColumnToPull = Integer.valueOf(params[2]);
			Log.d(TAG, "Column To Pull from: " + targetColumnToPull);

			for (View view : solo.getCurrentViews()) {
				Log.d(TAG, "creating batch of  lists views ");
				if (view instanceof ListView) {
					ListView listView = (ListView) view;
					int count = listView.getChildCount();
					Log.d(TAG, "get childe of lists of views count = " +count);
					for( int i =0 ; i<count ;i++)
					{	
						Log.d(TAG, "childe index : "+i);
						
						View row= listView.getChildAt(i);
						
						TextView txtDateView=(TextView) row.findViewById(columnToSearchIn);
						if(txtDateView.getText().toString().contains(searchText)){ //Date column in dbx history
							TextView cost =(TextView) row.findViewById(targetColumnToPull);
							array[0] = cost.getText().toString();
							result.setReturnedValues(array);
							result.setResponse(command);
							result.setSucceeded(true); 
							Log.d(TAG, "context text : " + "Success pulling" + cost.getText().toString());
							return result;
						}
						//Log.d(TAG, "context text : " + "");
					}
				}
			}
		} catch (Throwable e) {
			return handleException("Failed: " + result.getOriginalCommand(), e);
		}
		array[0] = "";
		result.setReturnedValues(array);
		result.setSucceeded(true);
		result.setResponse("can't find searched text in view the searched text is :  " + searchText + " in method clickOnListItemByText");
		return result;
	}
	
	public CommandResponse validateIfTextIsFound(String[] params){
		String[] array = new String[1];
		CommandResponse result = new CommandResponse();
		String command = "the command validateIfTextIsFound";
		String searchedItemText = params[0]; 
//		int textid = Integer.valueOf(params[1]);
//		int parentId = Integer.valueOf(params[2]);
		
		//View parent = solo.getView(parentId);
		try {
			for (View view : solo.getCurrentViews()) {
				
				if (view instanceof TextView) {
					TextView tv = (TextView) view;
					String context = tv.getText().toString();
					Log.d(TAG, context);
					if (context.contains(searchedItemText)){
						Log.d(TAG, "context text contains: " + searchedItemText);
						result.setResponse(command);
						array[0] = "true";
						result.setReturnedValues(array);
						result.setSucceeded(true); 
						return result;
					}
				}
			}
		} catch (Throwable e) {
			return handleException("Failed: " + result.getOriginalCommand(), e);
		}
		array[0] = "false";
		result.setReturnedValues(array);
		result.setSucceeded(true);
		result.setResponse("can't find searched text in view the searched text is :  " + searchedItemText + " in method validateIfTextIsFound");
		return result;
	}
}
