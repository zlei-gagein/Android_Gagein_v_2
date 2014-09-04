package com.gagein.component;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gagein.R;
import com.gagein.util.MessageCode;

public class EmptyView extends LinearLayout {

	// member vars
	private TextView lblTitle;
	private TextView lblMessage;
	private Button btnAction;
	
	public Button getBtnAction() {
		return btnAction;
	}

	// constructors
	public EmptyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initSubviews(context);
	}

	public EmptyView(Context context) {
		super(context);
		
		initSubviews(context);
	}

	private void initSubviews(Context context) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View root = inflater.inflate(R.layout.layout_empty,this);	// root should equal to this
		lblTitle = (TextView) root.findViewById(R.id.txtTitle);
		lblMessage = (TextView) root.findViewById(R.id.txtMessage);
		btnAction = (Button) root.findViewById(R.id.btnAction);
	}
	
	public void clearTitle() {
		setTitle(null);
	}
	
	public void clearMessage() {
		setMessage(null);
	}
	
	public void clearAction() {
		setActionTitle(null);
	}
	
	public void setTitle(String aTitle) {
		Boolean isEmpty = TextUtils.isEmpty(aTitle);
		if (isEmpty) {
			lblTitle.setText("");
			lblTitle.setVisibility(View.GONE);
		} else {
			lblTitle.setText(aTitle);
			lblTitle.setVisibility(View.VISIBLE);
		}
	}
	
	public void setMessage(String aMessage) {
		Boolean isEmpty = TextUtils.isEmpty(aMessage);
		if (isEmpty) {
			lblMessage.setText("");
			lblMessage.setVisibility(View.GONE);
		} else {
			lblMessage.setText(aMessage);
			lblMessage.setVisibility(View.VISIBLE);
		}
	}
	
	public void setActionTitle(String aTitle) {
		Boolean isEmpty = TextUtils.isEmpty(aTitle);
		if (!isEmpty) {
			btnAction.setText(aTitle);
			btnAction.setVisibility(View.VISIBLE);
		} else {
			btnAction.setText("");
			btnAction.setVisibility(View.GONE);
		}
	}
	
	public void setActionListener(OnClickListener aListener) {
		btnAction.setOnClickListener(aListener);
	}
	
	public void applyMessageCode(int aMessageCode) {

		String title = MessageCode.titleForCode(aMessageCode);
		setTitle(title);
		
		String message = MessageCode.messageForCode(aMessageCode);
		setMessage(message);
		
		String action = MessageCode.actionForCode(aMessageCode);
		setActionTitle(action);
	}
}


/**

*/