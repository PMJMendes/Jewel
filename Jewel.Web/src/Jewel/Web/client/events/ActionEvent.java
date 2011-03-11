package Jewel.Web.client.events;

import com.google.gwt.event.shared.*;

public class ActionEvent
	extends GwtEvent<ActionEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onAction(ActionEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addActionHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	private int mlngOrder;
	private int mlngAction;

	public ActionEvent(int plngOrder, int plngAction)
	{
		mlngOrder = plngOrder;
		mlngAction = plngAction;
	}

	public int GetOrder()
	{
		return mlngOrder;
	}

	public int GetAction()
	{
		return mlngAction;
	}

	protected void dispatch(Handler handler)
	{
		handler.onAction(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
