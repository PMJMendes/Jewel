package Jewel.Mobile.client;

import Jewel.Mobile.client.controls.IDButton;
import Jewel.Mobile.client.events.ActionEvent;
import Jewel.Mobile.interfaces.CommandSetService;
import Jewel.Mobile.interfaces.CommandSetServiceAsync;
import Jewel.Mobile.shared.CommandObj;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

public class CommandSet
	extends Composite
	implements ActionEvent.HasEvent
{
	private CommandSetServiceAsync cmdSvc;

	private boolean mbInit;
	private boolean mbEnabled;

	private FlowPanel mtblPanel;
	private IDButton[] marrButtons;

	private HandlerManager mrefEventMgr;

	public CommandSet()
	{
		mbInit = false;
		mbEnabled = false;

		mtblPanel = new FlowPanel();
		mtblPanel.setStylePrimaryName("commandSet");

		initWidget(mtblPanel);

		mrefEventMgr = new HandlerManager(this);
	}

	private CommandSetServiceAsync getService()
	{
		if ( cmdSvc == null )
			cmdSvc = GWT.create(CommandSetService.class);

		return cmdSvc;
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
					mbInit = true;
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
	
	void setEnabled(boolean enabled)
	{
		int i;

		mbEnabled = enabled;

		if ( mbInit )
			for ( i = 0; i < marrButtons.length; i++ )
				marrButtons[i].setEnabled(enabled);
	}

	private void BuildSet(CommandObj[] parrActions)
	{
		int i;

		marrButtons = new IDButton[parrActions.length];

		for ( i = 0; i < parrActions.length; i++ )
		{
			marrButtons[i] = new IDButton(i);
			marrButtons[i].setText(parrActions[i].mstrCaption);
			marrButtons[i].setEnabled(mbEnabled);
			marrButtons[i].setStylePrimaryName("commandSet-Command");
			mtblPanel.add(marrButtons[i]);

			marrButtons[i].addClickHandler(new ClickHandler()
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
	
	public boolean hasActions()
	{
		boolean rvalue = false;
		
		if ( marrButtons != null ) {
			rvalue = marrButtons.length > 0;
		}
		return rvalue;
	}
}
