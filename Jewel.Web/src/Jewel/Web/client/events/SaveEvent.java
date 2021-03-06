package Jewel.Web.client.events;

import com.google.gwt.event.shared.*;

public class SaveEvent
	extends GwtEvent<SaveEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onSave(SaveEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addSaveRowHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	protected void dispatch(Handler handler)
	{
		handler.onSave(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
