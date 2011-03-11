package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class SingleForm
	extends Composite
	implements ClosableContent
{
	private SingleFormServiceAsync formSvc;

	private String mstrFormID;
	private String mstrNameSpace;

	private SimpleForm mfrmData;
	private CommandSet msetCommands;

	public SingleForm(String pstrText)
	{
		VerticalPanel louter;
		HorizontalPanel linner;
		Label llbl;
		Image limg;

		louter = new VerticalPanel();
		louter.setStylePrimaryName("singleForm");

		linner = new HorizontalPanel();
		linner.setStylePrimaryName("singleForm-Header");

		llbl = new Label(pstrText);
		llbl.setStylePrimaryName("singleForm-Label");
		llbl.setWordWrap(false);
		linner.add(llbl);
		llbl.getElement().getParentElement().setClassName("singleForm-LabelWrapper");

		limg = new Image();
		limg.setUrl("images/closebox.png");
		limg.setStylePrimaryName("singleForm-CloseButton");
		linner.add(limg);
		limg.getElement().getParentElement().setClassName("singleForm-CloseButtonWrapper");

		louter.add(linner);
		linner.getElement().getParentElement().setClassName("singleForm-HeaderWrapper");

		mfrmData = new SimpleForm();
		louter.add(mfrmData);
		mfrmData.getElement().getParentElement().setClassName("singleForm-FormWrapper");

		msetCommands = new CommandSet();
		louter.add(msetCommands);
		msetCommands.getElement().getParentElement().setClassName("singleForm-CommandsWrapper");

		initWidget(louter);

		limg.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot());
	        }
	    });
		msetCommands.addActionHandler(new ActionEvent.Handler()
		{
			public void onAction(ActionEvent event)
			{
				DoCommand(event.GetAction());
			}
		});
	}

	private SingleFormServiceAsync getService()
	{
		if ( formSvc == null )
			formSvc = GWT.create(SingleFormService.class);

		return formSvc;
	}

	public void InitForm(String pstrFormID, String pstrNameSpace)
	{
		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;

		mfrmData.InitForm(mstrFormID, mstrNameSpace, null);
		msetCommands.InitSet(mstrFormID);
	}

	private void DoCommand(int plngID)
	{
		AsyncCallback<CommandResponse> callback = new AsyncCallback<CommandResponse>()
        {
			public void onSuccess(CommandResponse result)
			{
				if ( result != null )
				{
					Jewel_Mobile.getReference().showError(result.mstrResult);
					if (result.marrData != null)
						mfrmData.SetData(result.marrData);
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

        getService().DoCommand(plngID, mstrFormID, mstrNameSpace, mfrmData.GetData(), callback);
	}

	public void DoClose()
	{
		mfrmData.DoClose();
	}
}
