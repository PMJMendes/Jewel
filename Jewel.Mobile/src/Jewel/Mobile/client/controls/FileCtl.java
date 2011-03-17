package Jewel.Mobile.client.controls;

import Jewel.Mobile.client.*;
import Jewel.Mobile.client.popups.*;
import Jewel.Mobile.interfaces.*;

import com.google.gwt.core.client.*;
import com.google.gwt.dom.client.Style.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

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

		mbtnAttach = new Button();
		mbtnAttach.setText("Attach");
		mbtnAttach.addStyleName("mFileXfer-Attach");
		louter.add(mbtnAttach);
		mbtnView = new Button();
		mbtnView.setText("View");
		mbtnView.setEnabled(false);
		mbtnView.addStyleName("mFileXfer-View");
		louter.add(mbtnView);
		mbtnClear = new Button();
		mbtnClear.setText("Clear");
		mbtnClear.setEnabled(false);
		mbtnClear.addStyleName("mFileXfer-Clear");
		louter.add(mbtnClear);

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

	public void setWidth(int plngWidth)
	{
		getElement().getStyle().setWidth(plngWidth-15, Unit.PX);
		mtxtDisplay.getElement().getStyle().setWidth(plngWidth-175, Unit.PX);
	}

	private void DoPopup()
	{
		if ( mdlgPopup == null )
		{
			mdlgPopup = new FilePopup(this);
			mdlgPopup.setText("File Upload");
			mdlgPopup.center();
		}

		mdlgPopup.SetKey(mstrValue);
		mdlgPopup.show();
	}
}
