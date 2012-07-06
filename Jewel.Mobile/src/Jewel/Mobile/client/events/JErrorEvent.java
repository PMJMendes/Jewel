package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class JErrorEvent
	extends GwtEvent<JErrorEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onError(JErrorEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addErrorHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onError(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
