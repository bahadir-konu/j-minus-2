package com.jminus.ide;

import com.vaadin.ui.VerticalLayout;

/**
 * Author: Bahadir Konu (bah.konu@gmail.com) 
 */
public class MediatorVerticalLayout extends VerticalLayout {

    @Override
    public JMinusApplication getApplication() {
        return (JMinusApplication) super.getApplication();
    }

    public void fire(UIEvent event) {
        getApplication().getMainWindow().fireEvent(event);
    }

}
