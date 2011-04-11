package Jewel.Mobile.client;

import Jewel.Mobile.interfaces.*;
import Jewel.Mobile.shared.*;

import com.google.gwt.core.client.*;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.rpc.*;
import com.google.gwt.user.client.ui.*;

public class Menu
	extends Composite
{
	private static Menu gmnuRoot = null;
	private static Menu gmnuSysOps = null;

	public static Menu GetRoot(String pstrUser)
	{
		if ( gmnuRoot == null )
			gmnuRoot = new Menu(pstrUser);

		return gmnuRoot;
	}

	public static Menu GetRoot()
	{
		return gmnuRoot;
	}
	
	public static void DropRoot()
	{
		gmnuRoot = null;
		gmnuSysOps = null;
	}

	public static Menu GetSysOps()
	{
		if ( gmnuSysOps == null )
			gmnuSysOps = new MenuSysOps();

		return gmnuSysOps;
	}

	private MenuServiceAsync treeSvc;

	private MenuItem mitmParent;
	private String mstrTmp;
	private VerticalPanel mvert;

	public Menu(MenuNodeObj[] parrDef, MenuItem pitmParent)
	{
		if ( parrDef == null )
			return;

		InitPanel();

		mitmParent = pitmParent;
		BuildItems(parrDef, "Back");
	}
	
	protected Menu(String pstrUser)
	{
		AsyncCallback<MenuNodeObj[]> callback = new AsyncCallback<MenuNodeObj[]>()
        {
			public void onSuccess(MenuNodeObj[] result)
			{
				if (result != null)
				{
					BuildItems(result, mstrTmp + ": Options");
				}
				else
				{
					Jewel_Mobile.getReference().setLoginScreen();
				}
			}

			public void onFailure(Throwable ex)
			{
				while ( ex.getCause() != null )
					ex = ex.getCause();

				Jewel_Mobile.getReference().showError(ex.getMessage());
			}
        };

        if ( pstrUser == null )
        	return;

        InitPanel();

		mitmParent = null;
		mstrTmp = pstrUser;
        getService().GetNodes(callback);
	}

	private MenuServiceAsync getService()
	{
		if ( treeSvc == null )
			treeSvc = GWT.create(MenuService.class);
		
		return treeSvc;
	}
	
	private void InitPanel()
	{
		mvert = new VerticalPanel();
		mvert.setStylePrimaryName("menu");
		mvert.setSpacing(5);
		initWidget(mvert);
	}

	private void BuildItems(MenuNodeObj[] parrDef, String pstrLabel)
	{
		MenuItem litm;
		Button lbBack;
		int i;

		for ( i = 0; i < parrDef.length; i++ )
		{
			litm = new MenuItem(parrDef[i], this);
			mvert.add(litm);
			litm.getElement().getParentElement().setClassName("menu-Item-Wrapper");
			
		}

		lbBack = new Button();
		lbBack.setText(pstrLabel);
		lbBack.setStylePrimaryName("menu-Item");
		mvert.add(lbBack);
		lbBack.getElement().getParentElement().setClassName("menu-Item-Wrapper");

		lbBack.addClickHandler(new ClickHandler()
		{
			public void onClick(ClickEvent event)
	        {
				if ( mitmParent == null )
		        	Jewel_Mobile.getReference().setMenuScreen(GetSysOps());
				else
					Jewel_Mobile.getReference().setMenuScreen(mitmParent.GetOwner());
	        }
	     });
	}
}
