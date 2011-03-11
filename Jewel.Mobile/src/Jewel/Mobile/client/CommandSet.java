package Jewel.Mobile.client;

import Jewel.Mobile.client.events.*;
import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class CommandSet
	extends Composite
	implements ActionEvent.HasEvent
{
	private CommandSetServiceAsync cmdSvc;

	private FlowPanel mtblPanel;

	private HandlerManager mrefEventMgr;

	public CommandSet()
	{
		mtblPanel = new FlowPanel();
		mtblPanel.setStylePrimaryName("commandSet");

		initWidget(mtblPanel);

		mrefEventMgr = new HandlerManager(this);
	}

	public void InitSet(String pstrFormID)
	{
		AsyncCallback<CommandObj[]> callback = new AsyncCallback<CommandObj[]>()
        {
			public void onSuccess(CommandObj[] result)
			{
				if (result != null)
				{
					BuildSet(result);
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

        getService().GetCommands(pstrFormID, callback);
	}

	private CommandSetServiceAsync getService()
	{
		if ( cmdSvc == null )
			cmdSvc = GWT.create(CommandSetService.class);

		return cmdSvc;
	}

	private void BuildSet(CommandObj[] parrActions)
	{
		IDButton lbtnAux;
		int i;

		for ( i = 0; i < parrActions.length; i++ )
		{
			lbtnAux = new IDButton(i);
			lbtnAux.setText(parrActions[i].mstrCaption);
			lbtnAux.setStylePrimaryName("commandSet-Command");
			mtblPanel.add(lbtnAux);

			lbtnAux.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
		        {
					mrefEventMgr.fireEvent(new ActionEvent(-1, ((IDButton)event.getSource()).getID()));
		        }
		     });
		}
	}

	public HandlerRegistration addActionHandler(ActionEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(ActionEvent.TYPE, handler);
	}
}
