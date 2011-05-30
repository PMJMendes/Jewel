package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.IJewelMobileCtl;
import Jewel.Mobile.client.Jewel_Mobile;
import Jewel.Mobile.client.popups.FilePopup;
import Jewel.Mobile.interfaces.FileService;
import Jewel.Mobile.interfaces.FileServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;

public class FileCtl
	extends Composite
	implements IJewelMobileCtl
{
	private FileServiceAsync fileSvc;

	private String mstrValue;

	private TextBox mtxtDisplay;
	private Button mbtnAttach;
	private Button mbtnView;
	private Button mbtnClear;
	private FilePopup mdlgPopup;

	public FileCtl()
	{
		HorizontalPanel louter;

		mstrValue = null;

		louter = new HorizontalPanel();
		louter.setStylePrimaryName("formControl mFileXfer");

		mtxtDisplay = new TextBox();
		mtxtDisplay.setReadOnly(true);
		mtxtDisplay.setStylePrimaryName("mFileXfer-Display");
		louter.add(mtxtDisplay);
		mtxtDisplay.getElement().getParentElement().setClassName("mFileXfer-Display-Wrapper");

		mbtnView = new Button();
		mbtnView.setText("?");
		mbtnView.setEnabled(false);
		mbtnView.setStylePrimaryName("mFileXfer-View");
		louter.add(mbtnView);
		mbtnView.getElement().getParentElement().setClassName("mFileXfer-View-Wrapper");
		mbtnAttach = new Button();
		mbtnAttach.setText("+");
		mbtnAttach.setStylePrimaryName("mFileXfer-Attach");
		louter.add(mbtnAttach);
		mbtnAttach.getElement().getParentElement().setClassName("mFileXfer-Attach-Wrapper");
		mbtnClear = new Button();
		mbtnClear.setText("x");
		mbtnClear.setEnabled(false);
		mbtnClear.setStylePrimaryName("mFileXfer-Clear");
		louter.add(mbtnClear);
		mbtnClear.getElement().getParentElement().setClassName("mFileXfer-Clear-Wrapper");

		initWidget(louter);

		mbtnAttach.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				DoPopup();
	        }
	     });
		mbtnView.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				Window.open(GWT.getModuleBaseURL() + "file?fileref=" + mstrValue, null, null);
	        }
	     });
		mbtnClear.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				setJValue(null);
	        }
	     });
	}

	private FileServiceAsync getService()
	{
		if ( fileSvc == null )
			fileSvc = GWT.create(FileService.class);

		return fileSvc;
	}

	public void DiscardValue()
	{
		AsyncCallback<String> callback = new AsyncCallback<String>()
        {
			public void onSuccess(String result)
			{
				if (result != null)
				{
				}
				else
				{
					Jewel_Mobile.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				Jewel_Mobile.getReference().showError(ex.getMessage());
			}
        };

        if ( mstrValue == null )
        	return;

    	getService().Discard(mstrValue, callback);
	}

	private void internalSetValue(String pstrValue)
	{
		if ( (mstrValue != null) && (!mstrValue.equals(pstrValue)) )
			DiscardValue();
		mstrValue = pstrValue;
	}

	public String getJValue()
	{
		if (mstrValue == null)
			return null;

		return mstrValue + "!" + mtxtDisplay.getText();
	}

	public void setJValue(String pstrValue)
	{
		String[] larrAux;

		if ( pstrValue == null )
		{
			mbtnView.setEnabled(false);
			mbtnClear.setEnabled(false);
			internalSetValue(null);
			mtxtDisplay.setText(null);
			return;
		}

		larrAux = pstrValue.split("!", 2);

		mbtnView.setEnabled(true);
		mbtnClear.setEnabled(true);
		internalSetValue(larrAux[0]);
		mtxtDisplay.setText(larrAux[1]);
	}

	private void DoPopup()
	{
		if ( mdlgPopup == null )
			mdlgPopup = new FilePopup(this);

		mdlgPopup.SetKey(mstrValue);
		mdlgPopup.show();
	}
}
