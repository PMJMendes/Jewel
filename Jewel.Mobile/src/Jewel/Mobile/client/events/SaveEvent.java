package Jewel.Mobile.client.events;

import Jewel.Mobile.shared.DataObject;

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
		public HandlerRegistration addSaveHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	private DataObject mobjResult;

	public SaveEvent(DataObject pstrResult)
	{
		mobjResult = pstrResult;
	}

	public DataObject getData()
	{
		return mobjResult;
	}

	protected void dispatch(Handler handler)
	{
		handler.onSave(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
