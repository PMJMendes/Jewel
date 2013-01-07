package Jewel.Web.client.controls;

import Jewel.Web.client.IJewelWebCtl;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FocusWidget;

public class TriStateCheckboxCtl extends FocusWidget implements IJewelWebCtl{

	private static final String UNCHECKED = "images/unchecked.png";
	private static final String CHECKED = "images/checked.png";
	private static final String UNDEFINED = "images/undefined.png";
	
	private final Element btnElement = DOM.createElement("input");
	
	public TriStateCheckboxCtl(){

		DOM.setElementProperty(btnElement, "type", "image");	
		setElement(btnElement);
		setStyleName("jewel-tristate-checkbox");
		
		DOM.setElementAttribute(btnElement, "src", UNDEFINED);		
		
		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final String img = DOM.getElementAttribute(btnElement, "src");
				String newImg;
				if(img.endsWith(UNDEFINED)){
					newImg = CHECKED;
				}else if(img.endsWith(CHECKED)){
					newImg = UNCHECKED;
				}else{
					newImg = UNDEFINED;
				}
				
				DOM.setElementAttribute(btnElement, "src", newImg);
			}
		});
	}
	
	public void setButtonState(final Boolean state) { 
        DOM.setElementAttribute(btnElement, "src", state == null ? UNDEFINED : state.booleanValue() ? CHECKED : UNCHECKED); 
	} 
	
	public String getButtonState(){
		final String img = DOM.getElementAttribute(btnElement, "src");
		
		if (img.endsWith(UNCHECKED)) { 
			return Boolean.FALSE.toString(); 
		} 
		else if (img.endsWith(CHECKED)) { 
			return Boolean.TRUE.toString(); 
		}
		else { 
			return null; 
		}
	}
	
	@Override
	public String getJValue() {
		return getButtonState();
	}

	@Override
	public void setJValue(String pstrValue) {
		String newImg;
		
		if ( pstrValue == null )
		{
			newImg = UNDEFINED;
			DOM.setElementAttribute(btnElement, "src", newImg);
			return;
		}

		if ( pstrValue.equals("1") || pstrValue.equalsIgnoreCase("true") )
		{
			newImg = CHECKED;
			DOM.setElementAttribute(btnElement, "src", newImg);
			return;
		}

		if ( pstrValue.equals("0") || pstrValue.equalsIgnoreCase("false") )
		{
			newImg = UNCHECKED;
			DOM.setElementAttribute(btnElement, "src", newImg);
			return;
		}

		newImg = UNDEFINED;
		DOM.setElementAttribute(btnElement, "src", newImg);
	}

	@Override
	public void setWidth(int plngWidth) {
		getElement().getStyle().setWidth(16, Unit.PX);
	}
}
