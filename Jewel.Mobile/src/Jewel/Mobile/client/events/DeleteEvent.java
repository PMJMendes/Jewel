package Jewel.Mobile.client.events;

import com.google.gwt.event.shared.*;

public class DeleteEvent
	extends GwtEvent<DeleteEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onDelete(DeleteEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addDeleteRowHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onDelete(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
