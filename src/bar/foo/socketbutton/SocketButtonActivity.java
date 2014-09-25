package bar.foo.socketbutton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//=============================================================================
//=============================================================================
public class SocketButtonActivity extends Activity implements DialogInterface.OnClickListener, OnLongClickListener {

	static final int CONFIG_DIALOG_ID = 1;
	static final int MESSAGE_DIALOG_ID = 2;
	
	private String address;
	private int port;	
	private int luanchedDialog;	
	private Button buttonPressed;
	
	//--------------------------------------------------------------------		
    /** Called when the activity is first created. */
	//--------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        address = "127.0.0.1";
        port = 80;
        
        luanchedDialog = android.R.id.empty;
        
        buttonPressed = null;
        
        // Set up buttons
        Button b;
        b = (Button) findViewById(R.id.button1);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button2);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button3);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button4);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button5);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button6);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button7);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button8);
        b.setOnLongClickListener(this);
        b = (Button) findViewById(R.id.button9);
        b.setOnLongClickListener(this);
    
    }
    
	//--------------------------------------------------------------------
    /** Called after onRestoreInstanceState(Bundle), onRestart(), or onPause(),
     *  for your activity to start interacting with the user.    */
	//--------------------------------------------------------------------	
    @Override
    public void onResume() {   
    	super.onResume();
    	updateInfo();
    }

	//--------------------------------------------------------------------
	//--------------------------------------------------------------------	
    private void updateInfo() {
    	String msg;
    	
    	msg = String.format("Address: %s", address);
    	TextView tv = (TextView)findViewById(R.id.tv_address);
    	tv.setText(msg);
    	
    	msg = String.format("Port: %d", port);
    	tv = (TextView)findViewById(R.id.tv_port);
    	tv.setText(msg);    	
    }
    
	//--------------------------------------------------------------------
    /** Callback registered with each button in main.xml via android:onClick=""
     *  This will be called whenever a button is pressed.  */
	//--------------------------------------------------------------------
    public void buttonDispatch(View v) {   	
    	if (null==v) return;
    	
    	sendMessage(((Button)v).getText().toString());
    }
    
	//--------------------------------------------------------------------
    /** Send a message over a socket connection. */
	//--------------------------------------------------------------------
    public void sendMessage(String message) {
    	try {
    		Socket s = new Socket(address, port);
			PrintWriter pw = new PrintWriter(s.getOutputStream(),true);
			pw.write(message);
			pw.close();
			s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
    }
    
	//--------------------------------------------------------------------
    /** Callback registered with Config... button in main.xml 
     *  via android:onClick="" */
	//--------------------------------------------------------------------
    public void configNetworkViaDialog(View v) {
    	showDialog(CONFIG_DIALOG_ID);  // launch dialog for network config	
    }
    
	//--------------------------------------------------------------------
    /** Callback for creating dialogs. */
	//--------------------------------------------------------------------	
    @Override
    protected Dialog onCreateDialog(int id) {
    	super.onCreateDialog(id);

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	View view;
    	
    	switch (id) {
    	case CONFIG_DIALOG_ID:
    		view =  getLayoutInflater().inflate(R.layout.config_dialog, null);	
    		break;
    	case MESSAGE_DIALOG_ID:
    		view =  getLayoutInflater().inflate(R.layout.message_dialog, null);
    		break;
		default:
			return null;
    	}
    	
    	builder.setView(view);
    	builder.setPositiveButton(android.R.string.ok, this);
    	builder.setNegativeButton(android.R.string.cancel, this);
		
    	return builder.create();
    }    
    
	//--------------------------------------------------------------------
    /** Prepare dialogs before they are shown */
	//--------------------------------------------------------------------	
    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {    
    	super.onPrepareDialog(id, dialog);
    	
    	EditText et;
    	
    	switch(id) {
    	case CONFIG_DIALOG_ID:
    		luanchedDialog = CONFIG_DIALOG_ID;
	    	et = (EditText)((AlertDialog)dialog).findViewById(R.id.config_dialog_et_address);
	    	et.setText(address);	    	
	    	et = (EditText) ((AlertDialog)dialog).findViewById(R.id.config_dialog_et_port);
	    	et.setText(Integer.toString(port)); 
	    	break;
    	case MESSAGE_DIALOG_ID:
    		luanchedDialog = MESSAGE_DIALOG_ID;
    		et = (EditText)((AlertDialog)dialog).findViewById(R.id.et_message_dialog);
    		et.setText(buttonPressed.getText());
    		break;
		default:
			luanchedDialog = android.R.id.empty;	
    	}
    }

	//--------------------------------------------------------------------
    /** Required implementation for DialogInterface.OnClickListener 
     *  This method will be invoked when a button in the dialog is clicked. 
     */
	//--------------------------------------------------------------------
	@Override
	public void onClick(DialogInterface dialog, int whichButton) {
		
		EditText et;
		
		switch (whichButton) {
		case DialogInterface.BUTTON_NEGATIVE:
			// do nothing
			break;
		case DialogInterface.BUTTON_POSITIVE:
			switch(luanchedDialog) {
			case CONFIG_DIALOG_ID:
				et = (EditText) ((AlertDialog)dialog).findViewById(R.id.config_dialog_et_address);
				address = et.getText().toString();
				et = (EditText) ((AlertDialog)dialog).findViewById(R.id.config_dialog_et_port);
				port = Integer.parseInt(et.getText().toString());
				break;
			case MESSAGE_DIALOG_ID:
				et = (EditText) ((AlertDialog)dialog).findViewById(R.id.et_message_dialog);
				if (buttonPressed != null) {
					buttonPressed.setText(et.getText().toString());
				}
				break;
			}

			break;			
		}
		
		updateInfo();
		removeDialog(luanchedDialog);	
	}

	//--------------------------------------------------------------------
	/** Required implementation for OnLongClickListener
	 *  Called when a view has been clicked and held.
	 */
	//--------------------------------------------------------------------
	@Override
	public boolean onLongClick(View v) {		
		buttonPressed = (Button) v;
		showDialog(MESSAGE_DIALOG_ID);	// launch the dialog that gets the text string	 
		return false;
	}
}