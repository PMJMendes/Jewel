package Jewel.Mobile.client.events;

import Jewel.Mobile.shared.*;

import com.google.gwt.event.shared.*;

public class SelectEvent
	extends GwtEvent<SelectEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onSelect(SelectEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addSelectHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	private DataObject mobjResult;

	public SelectEvent(DataObject pstrResult)
	{
		mobjResult = pstrResult;
	}

	public DataObject getResult()
	{
		return mobjResult;
	}

	protected void dispatch(Handler handler)
	{
		handler.onSelect(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
