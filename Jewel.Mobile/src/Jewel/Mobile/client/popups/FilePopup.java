package Jewel.Mobile.client.popups;

import Jewel.Mobile.client.ClosableHeader;
import Jewel.Mobile.client.controls.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.ui.FormPanel.*;

public class FilePopup
	extends DialogBox
{
	private FileCtl mrefOwner;

	private ClosableHeader mheader;
	private Label mlblError;
	private FormPanel mfrmMain;
	private FileUpload mfupMain;
	private Button mbtnOk;

	public FilePopup(FileCtl prefOwner)
	{
		super(false, true);

		mrefOwner = prefOwner;

		VerticalPanel lvert;

		setStylePrimaryName("filePopup");

		setGlassEnabled(true);
		setGlassStyleName("filePopup-Glass");

		lvert = new VerticalPanel();
		lvert.setStylePrimaryName("filePopup-Main");

		mheader = new ClosableHeader("File Upload");
		lvert.add(mheader);

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("filePopup-ErrorLine");
		lvert.add(mlblError);

		mfrmMain = new FormPanel();
		mfrmMain.setStylePrimaryName("filePopup-Form");
		mfrmMain.setEncoding(FormPanel.ENCODING_MULTIPART);
		mfrmMain.setMethod(FormPanel.METHOD_POST);
		mfrmMain.setAction(GWT.getModuleBaseURL() + "file");
		lvert.add(mfrmMain);
		mfrmMain.getElement().getParentElement().setClassName("filePopup-Form-Wrapper");

		mfupMain = new FileUpload();
		mfupMain.setStylePrimaryName("formControl filePopup-Upload");
		mfrmMain.setWidget(mfupMain);

		mbtnOk = new Button();
		mbtnOk.setText("Ok");
		mbtnOk.setStylePrimaryName("filePopup-Ok");
		lvert.add(mbtnOk);
		mbtnOk.getElement().getParentElement().setClassName("filePopup-Ok-Wrapper");

		setWidget(lvert);

		mheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				hide();
	        }
		});
		mfrmMain.addSubmitCompleteHandler(new SubmitCompleteHandler()
		{
			public void onSubmitComplete(SubmitCompleteEvent event)
			{
				String lstrResults;

				mheader.setEnabled(true);
				mbtnOk.setEnabled(true);
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
				mheader.setEnabled(false);
				mbtnOk.setEnabled(false);
				SetError(null);
				mfrmMain.submit();
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
