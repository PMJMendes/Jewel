package Jewel.Web.client;

import Jewel.Web.client.events.JErrorEvent;
import Jewel.Web.shared.TreeResponse;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Main
	extends Composite
{
	private Label mlblError;
	private HorizontalPanel mpanelHeader;
	private SimplePanel mpanelLeft;
	private SimplePanel mpanelRight;
	private TreeNav mnav;
	private ClosableTabPanel mtabs;

	public Main(String pstrUser)
	{
		VerticalPanel louter;
		HorizontalPanel linner;
		Image limg;
		Label llbl;

		louter = new VerticalPanel();
		louter.setStylePrimaryName("mainWrapper");

		mpanelHeader = new HorizontalPanel();
		mpanelHeader.setStylePrimaryName("mainHeader");
		limg = new Image("images/logo.png");
		mpanelHeader.add(limg);
		limg.getElement().getParentElement().setClassName("headerLogo");
		llbl = new Label("AGIR - Aplicação de Gestão de Intervenções e Reparações");
		mpanelHeader.add(llbl);
		llbl.getElement().getParentElement().setClassName("headerText");

		louter.add(mpanelHeader);

		mlblError = new Label(" ");
		mlblError.setStylePrimaryName("messageLine");
		louter.add(mlblError);

		linner = new HorizontalPanel();
		linner.setStylePrimaryName("mainContainer");
		mpanelLeft = new SimplePanel();
		mpanelLeft.setStylePrimaryName("leftContainer");
		mnav = new TreeNav(this, pstrUser);
		mpanelLeft.add(mnav);
		linner.add(mpanelLeft);
		mpanelLeft.getElement().getParentElement().setClassName("leftContainerWrapper");
		mpanelRight = new SimplePanel();
		mpanelRight.setStylePrimaryName("rightContainer");
		mtabs = new ClosableTabPanel();
		mtabs.addStyleName("normalbk");
		mpanelRight.add(mtabs);
		linner.add(mpanelRight);
		mpanelRight.getElement().getParentElement().setClassName("rightContainerWrapper");
		louter.add(linner);

		initWidget(louter);
		
		mnav.addJErrorHandler(new JErrorEvent.Handler()
		{
			public void onJError(JErrorEvent event)
			{
				SetError(event.getError());
			}
		});
		Window.addWindowClosingHandler(new ClosingHandler()
		{
			public void onWindowClosing(ClosingEvent event)
			{
				mtabs.clear();
			}
		});
	}

	public void DoResize()
	{
		InnerResize();

		Window.addResizeHandler(new ResizeHandler()
		{
			public void onResize(ResizeEvent event)
			{
				InnerResize();
			}
		});
	}

	public void AddPanel(TreeResponse pobjResp)
	{
		DynaFormActive lrefForm;
		DynaSearch lrefSearch;
		DynaReport lrefReport;
		HTML lrefAux;

		switch ( pobjResp.mlngType )
		{
		case TreeResponse.FORM:
			lrefForm = new DynaFormActive(-1);
			lrefForm.addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					SetError(event.getError());
				}
			});
			lrefForm.InitForm(pobjResp.mstrID, pobjResp.mstrNSpace, null);
			mtabs.add(lrefForm, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefForm));
			break;

		case TreeResponse.SEARCH:
			lrefSearch = new DynaSearch();
			lrefSearch.addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					SetError(event.getError());
				}
			});
			lrefSearch.InitSearch(pobjResp.mstrID, pobjResp.mstrNSpace, null, null, null);
			mtabs.add(lrefSearch, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefSearch));
			break;

		case TreeResponse.REPORT:
			lrefReport = new DynaReport();
			lrefReport.addJErrorHandler(new JErrorEvent.Handler()
			{
				public void onJError(JErrorEvent event)
				{
					SetError(event.getError());
				}
			});
			lrefReport.InitReport(pobjResp.mstrID, pobjResp.mstrNSpace);
			mtabs.add(lrefReport, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefReport));
			break;

		default:
			lrefAux = new ClosableEmpty();
			mtabs.add(lrefAux, new ClosableTab(pobjResp.mstrTitle, mtabs, lrefAux));
		}
	}
	
	private void SetError(String pstrError)
	{
		if ( (pstrError == null) || (pstrError.equals("")) ){
			mlblError.setStylePrimaryName("messageLine");
			mlblError.setText(" ");
		}
		else{
			mlblError.setStylePrimaryName("messageLine-active");
			mlblError.setText(pstrError);
		}
	}

	private void InnerResize()
	{
		int i, j;
		
		i = Window.getClientHeight() - mlblError.getOffsetHeight() - mpanelHeader.getOffsetHeight();
		i = ( i < 35 ? 35 : i);
		mpanelLeft.setHeight(i + "px");
		mpanelRight.setHeight(i + "px");

		i = Window.getClientWidth();
		j = (int)(0.2 * i);
		mpanelLeft.setWidth(j + "px");
		mpanelRight.setWidth(i - j + "px");
	}
}
