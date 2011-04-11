package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class OkEvent
	extends GwtEvent<OkEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onOk(OkEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addOkHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onOk(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
