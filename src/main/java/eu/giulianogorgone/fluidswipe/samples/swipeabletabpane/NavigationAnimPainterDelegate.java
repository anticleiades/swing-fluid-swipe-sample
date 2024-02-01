/*
 * Copyright 2024 Giuliano Gorgone
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package eu.giulianogorgone.fluidswipe.samples.swipeabletabpane;

import eu.giulianogorgone.fluidswipe.components.AnimPainterDelegate;
import eu.giulianogorgone.fluidswipe.components.NavigationSwipeAnimSupport;
import eu.giulianogorgone.fluidswipe.event.FluidSwipeEvent;

import javax.swing.*;
import java.awt.*;

/**
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 *
 * @author Giuliano Gorgone (anticleiades)
 */

// this implementation reflects macOS native applications behavior, notably Safari, Xcode and so on.
public class NavigationAnimPainterDelegate<T extends JComponent & Navigable> implements AnimPainterDelegate {
    protected static final int DEFAULT_SHADOW_WIDTH = 8;

    protected Image swipingPage;
    protected Image beneathPage;
    protected Image backgroundImage;

    protected Rectangle pageBounds;

    protected T component;
    protected NavigationSwipeAnimSupport navigationAnimSupport;

    protected double xSwipingPage;
    protected FluidSwipeEvent.Direction swipeBeganDirection;

    public NavigationAnimPainterDelegate(T component, NavigationSwipeAnimSupport navigationAnimSupport) {
        this.component = component;
        this.navigationAnimSupport = navigationAnimSupport;
    }


    @Override
    public void fluidSwipeBegan(FluidSwipeEvent e) {
        this.swipeBeganDirection = e.getLogicalGestureDirection();
        final boolean forward = this.swipeBeganDirection == FluidSwipeEvent.Direction.RIGHT_TO_LEFT;
        this.pageBounds = this.navigationAnimSupport.getPageBounds();
        this.swipingPage = forward ? this.navigationAnimSupport.getDestinationPage(e) : this.navigationAnimSupport.getPageToNavFrom(e);
        this.beneathPage = forward ? this.navigationAnimSupport.getPageToNavFrom(e) : this.navigationAnimSupport.getDestinationPage(e);
        this.backgroundImage = this.navigationAnimSupport.getBackgroundImage(e);
        this.xSwipingPage = forward ? this.pageBounds.width : 0.0;
    }

    @Override
    public void fluidSwipeEnded(FluidSwipeEvent e) {
        this.beneathPage.flush();
        this.swipingPage.flush();
        this.backgroundImage.flush();

        this.beneathPage = null;
        this.swipingPage = null;
        this.backgroundImage = null;
        this.pageBounds = null;
        this.navigationAnimSupport = null;

        // not calling repaintImmediately() but repaint() might avoid flickering
        this.component.repaint();
        this.component = null;
    }

    @Override
    public void fluidSwipeProgressed(FluidSwipeEvent e) {
        if (this.pageBounds != null) {
            if (this.swipeBeganDirection == FluidSwipeEvent.Direction.RIGHT_TO_LEFT) {
                this.xSwipingPage = this.pageBounds.getWidth() - (e.getGestureAmount() * this.pageBounds.getWidth());
            } else {
                this.xSwipingPage = e.getGestureAmount() * this.pageBounds.getWidth();
            }
        }
        this.repaintImmediately();
    }

    protected void repaintImmediately() {
        this.component.paintImmediately(0, 0, this.component.getWidth(), this.component.getHeight());
    }

    @Override
    public void paint(Graphics gfx) {
        // isActive() ensures that used fields are not null
        Graphics2D g = (Graphics2D) gfx.create();
        g.drawImage(this.backgroundImage, 0, 0, null);

        g.clipRect(this.pageBounds.x, this.pageBounds.y, (int) this.pageBounds.getWidth(), (int) this.pageBounds.getHeight());
        g.translate(this.pageBounds.x, this.pageBounds.y);

        g.drawImage(this.beneathPage, 0, 0, null);

        g.translate(this.xSwipingPage, 0);
        g.drawImage(this.swipingPage, 0, 0, null);

        g.translate(-DEFAULT_SHADOW_WIDTH, 0);
        this.drawShadow(g);
    }

    protected void drawShadow(Graphics2D gfx) {
        // pageBounds is not null when this method is called; see isActive()
        int height = this.pageBounds.height;
        gfx.setColor(Color.black);
        for (int x = 0; x < DEFAULT_SHADOW_WIDTH; x++) {
            float alpha = Math.min(1 << x, 100) / 100.0F;
            gfx.setComposite(AlphaComposite.SrcOver.derive(alpha));
            gfx.drawLine(x, 0, x, height);
        }
    }

    @Override
    public boolean isActive() {
        return this.backgroundImage != null && this.pageBounds != null && this.beneathPage != null && this.swipingPage != null;
    }
}
