package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class InitEvent
	extends GwtEvent<InitEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onInit(InitEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addInitHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onInit(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
