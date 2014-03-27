package Jewel.Web.client;

import Jewel.Web.client.events.*;
import Jewel.Web.interfaces.*;
import Jewel.Web.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class DynaFormActive
	extends DynaForm
	implements ActionEvent.HasEvent
{
	private DynaFormActiveServiceAsync formSvc;

	private int mlngOrder;

	public DynaFormActive(int plngOrder)
	{
		mlngOrder = plngOrder;
	}

	public void InitForm(String pstrFormID, String pstrNameSpace, String[] parrData, boolean pbUseDefaults)
	{
		AsyncCallback<FormActionObj[]> callback = new AsyncCallback<FormActionObj[]>()
        {
			public void onSuccess(FormActionObj[] result)
			{
				if (result != null)
				{
					mpnOuter.add(BuildActions(result));
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

        super.InitForm(pstrFormID, pstrNameSpace, parrData, pbUseDefaults);
		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().GetActions(mstrFormID, callback);
	}

	private DynaFormActiveServiceAsync getService()
	{
		if ( formSvc == null )
			formSvc = GWT.create(DynaFormActiveService.class);

		return formSvc;
	}

	private HorizontalPanel BuildActions(FormActionObj[] parrActions)
	{
		HorizontalPanel lpnActions;
		int i;
		IDButton lbtnAux;

		lpnActions = new HorizontalPanel();
		lpnActions.setStylePrimaryName("jewel-DynaForm-ActionBar");

		for ( i = 0; i < parrActions.length; i++ )
		{
			lbtnAux = new IDButton(i);
			lbtnAux.setText(parrActions[i].mstrCaption);
			lpnActions.add(lbtnAux);

			lbtnAux.addClickHandler(new ClickHandler()
			{
				public void onClick(ClickEvent event)
		        {
					doAction(((IDButton)event.getSource()).getID());
		        }
		     });
		}

		return lpnActions;
	}

	private void doAction(int plngID)
	{
		AsyncCallback<FormActionResponse> callback = new AsyncCallback<FormActionResponse>()
        {
			public void onSuccess(FormActionResponse result)
			{
				if ( result != null )
				{
					mrefEventMgr.fireEvent(new JErrorEvent(result.mstrResult));
					if (result.marrData != null)
						SetData(result.marrData);
				}
				else
				{
					Jewel_Web.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				mrefEventMgr.fireEvent(new JErrorEvent(ex.getMessage()));
			}
        };

		if ( mlngOrder >= 0 )
		{
			mrefEventMgr.fireEvent(new ActionEvent(mlngOrder, plngID));
			return;
		}

		mrefEventMgr.fireEvent(new JErrorEvent(null));
        getService().DoAction(plngID, mstrFormID, mstrNameSpace, GetData(), callback);
	}

	public HandlerRegistration addActionHandler(ActionEvent.Handler handler)
	{
		return mrefEventMgr.addHandler(ActionEvent.TYPE, handler);
	}
}
