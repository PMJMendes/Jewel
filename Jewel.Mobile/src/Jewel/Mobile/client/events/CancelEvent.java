package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class CancelEvent
	extends GwtEvent<CancelEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onCancel(CancelEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addCancelHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onCancel(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
