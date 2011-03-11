package Jewel.Web.client.events;

import com.google.gwt.event.shared.*;

public class JErrorEvent
	extends GwtEvent<JErrorEvent.Handler>
{
	public static interface Handler
		extends EventHandler
	{
		void onJError(JErrorEvent event);
	}

	public static interface HasEvent
	{
		public HandlerRegistration addJErrorHandler(Handler handler);
	}

	public static final Type<Handler> TYPE = new Type<Handler>();

	private String mstrError;

	public JErrorEvent(String pstrError)
	{
		mstrError = pstrError;
	}

	public String getError()
	{
		return mstrError;
	}

	protected void dispatch(Handler handler)
	{
		handler.onJError(this);
	}

	public Type<Handler> getAssociatedType()
	{
		return TYPE;
	}
}
