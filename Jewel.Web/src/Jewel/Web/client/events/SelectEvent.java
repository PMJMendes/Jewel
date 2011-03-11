package Jewel.Web.client.events;

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

	private String mstrResult;

	public SelectEvent(String pstrResult)
	{
		mstrResult = pstrResult;
	}

	public String getResult()
	{
		return mstrResult;
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
