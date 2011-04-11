package Jewel.Mobile.client;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;

public class SingleForm
	extends Composite
	implements ClosableContent
{
	private String mstrFormID;
	private String mstrNameSpace;

	private EditorForm mfrmData;

	public SingleForm(String pstrText)
	{
		VerticalPanel louter;
		ClosableHeader lheader;

		louter = new VerticalPanel();
		louter.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		louter.setStylePrimaryName("singleForm");

		lheader = new ClosableHeader(pstrText);
		louter.add(lheader);
		lheader.getElement().getParentElement().setClassName("singleForm-Header-Wrapper");

		mfrmData = new EditorForm();
		louter.add(mfrmData);
		mfrmData.getElement().getParentElement().setClassName("singleForm-Form-Wrapper");

		initWidget(louter);

		lheader.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
			{
				Jewel_Mobile.getReference().setMenuScreen(Menu.GetRoot());
	        }
	    });
	}

	public void InitForm(String pstrFormID, String pstrNameSpace)
	{
		mstrFormID = pstrFormID;
		mstrNameSpace = pstrNameSpace;

		mfrmData.InitForm(mstrFormID, mstrNameSpace, null, -1);
	}

	public void DoClose()
	{
		mfrmData.DoClose();
	}
}
