package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class EditorForm
	extends Composite
	implements ClosableContent, ActionEvent.HasEvent
{
	private EditorFormServiceAsync formSvc;

	private String mstrFormID;
	private String mstrNameSpace;
	private int mlngEventOrder;

	private SimpleForm mfrmData;
	private CommandSet msetCommands;

	private HandlerManager mrefEventMgr;

	public EditorForm()
	{
		VerticalPanel louter;

		louter = new VerticalPanel();
		louter.setStylePrimaryName("editorForm");

		mfrmData = new SimpleForm();
		louter.add(mfrmData);
		mfrmData.getElement().getParentElement().setClassName("editorForm-Form-Wrapper");

		msetCommands = new CommandSet();
		louter.add(msetCommands);
		msetCommands.getElement().getParentElement().setClassName("editorForm-Commands-Wrapper");

		initWidget(louter);

		msetCommands.addActionHandler(new ActionEvent.Handler()
		{
			public void onAction(ActionEvent event)
			{
				if ( mlngEventOrder < 0 )
					DoCommand(event.GetAction());
				else
					mrefEventMgr.fireEvent(new ActionEvent(mlngEventOrder, event.GetAction()));
			}
		});

		mrefEventMgr = new HandlerManager(this);
	}

	private EditorFormServiceAsync getService()
	{
		if ( formSvc == null )
			formSvc = GWT.create(EditorFormService.class);
	
		return formSvc;
	}

	public void InitForm(String pstrFormID, String pstrNameSpace, String[] parrData, int plngEventOrder)
	{
		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;
		mlngEventOrder = plngEventOrder;

		mfrmData.InitForm(mstrFormID, mstrNameSpace, parrData);
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

	public String[] GetData()
	{
		return mfrmData.GetData();
	}

	public void SetData(String[] parrData)
	{
		mfrmData.SetData(parrData);
	}

	public void DoClose()
	{
		mfrmData.DoClose();
	}

	public HandlerRegistration addActionHandler(ActionEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(ActionEvent.TYPE, handler);
	}
}
