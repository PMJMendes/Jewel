package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class JChangeEvent
	extends GwtEvent<JChangeEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onJChange(JChangeEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addJChangeHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onJChange(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
