package Jewel.Web.client.popups;

import Jewel.Web.client.controls.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.*;

public class FilePopup
	extends DialogBox
{
	private Label mlblError;
	private FormPanel mfrmMain;
	private FileUpload mfupMain;
	private Button mbtnOk;
	private Button mbtnCancel;

	private FileCtl mrefOwner;

	public FilePopup(FileCtl prefOwner)
	{
		super(false, true);

		mrefOwner = prefOwner;

		VerticalPanel lvert;
		HorizontalPanel lhorz;

		setStylePrimaryName("jewel-FilePopup");

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("jewel-FilePopup-Main");

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("messageLine");
		lvert.add(mlblError);

		mfrmMain = new FormPanel();
		mfrmMain.setStylePrimaryName("jewel-FilePopup-Form");
		mfrmMain.setEncoding(FormPanel.ENCODING_MULTIPART);
		mfrmMain.setMethod(FormPanel.METHOD_POST);
		mfrmMain.setAction(GWT.getModuleBaseURL() + "file");
		lvert.add(mfrmMain);
		mfrmMain.getElement().getParentElement().addClassName("jewel-FilePopup-Form-Wrapper");

		mfupMain = new FileUpload();
		mfupMain.setStylePrimaryName("jewel-Control jewel-FilePopup-Upload");
		mfrmMain.setWidget(mfupMain);

		lhorz = new HorizontalPanel();
		lhorz.setStylePrimaryName("jewel-FilePopup-Buttonbar");
		mbtnOk = new Button();
		mbtnOk.setText("Ok");
		lhorz.add(mbtnOk);
		mbtnOk.getElement().getParentElement().addClassName("jewel-FilePopup-Ok-Wrapper");
		mbtnCancel = new Button();
		mbtnCancel.setText("Cancel");
		lhorz.add(mbtnCancel);
		mbtnCancel.getElement().getParentElement().addClassName("jewel-FilePopup-Cancel-Wrapper");
		lvert.add(lhorz);

		setWidget(lvert);

		mfrmMain.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				String lstrResults;

				mbtnOk.setEnabled(true);
				mbtnCancel.setEnabled(true);

				lstrResults = event.getResults();
				if ( lstrResults.startsWith("!") )
				{
					SetError(lstrResults.substring(1));
					return;
				}

				mrefOwner.setJValue(lstrResults);
				hide();
			}
		});
		mbtnOk.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				mbtnOk.setEnabled(false);
				mbtnCancel.setEnabled(false);
				SetError(null);
				mfrmMain.submit();
	        }
	    });
		mbtnCancel.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				hide();
	        }
		});
	}

	public void SetKey(String pstrKey)
	{
		if ( pstrKey == null )
			mfupMain.setName("none");
		else
			mfupMain.setName(pstrKey);
	}
	
	private void SetError(String pstrError)
	{
		if ( (pstrError == null) || (pstrError.equals("")) )
			mlblError.setText(" ");
		else
			mlblError.setText(pstrError);
	}
}
