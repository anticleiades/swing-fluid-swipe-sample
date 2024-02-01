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

import eu.giulianogorgone.fluidswipe.FluidSwipe;
import eu.giulianogorgone.fluidswipe.components.AnimPainterDelegate;
import eu.giulianogorgone.fluidswipe.components.FluidSwipeVetoer;
import eu.giulianogorgone.fluidswipe.components.NavigationSwipeAnimSupport;
import eu.giulianogorgone.fluidswipe.event.FluidSwipeEvent;
import eu.giulianogorgone.fluidswipe.event.FluidSwipeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 *
 * @author Giuliano Gorgone (anticleiades)
 */

// this implementation reflects AppKit behavior, notably Safari, Xcode and so on.
public class SwipeableTabbedPane extends JTabbedPane implements NavigationSwipeAnimSupport, FluidSwipeListener, FluidSwipeVetoer, Navigable {
    private AnimPainterDelegate swipeAnimationPainterDelegate;

    public SwipeableTabbedPane() {
        super();
        this.configFluidSwipe();
    }

    protected final void configFluidSwipe() {
        FluidSwipe.addListenerTo(this, this);
    }

    @Override
    public AnimPainterDelegate getFluidSwipeAnimationPainterDelegate() {
        return this.swipeAnimationPainterDelegate;
    }

    @Override
    public Rectangle getPageBounds() {
        final Component selectedComponent = getSelectedComponent();
        return selectedComponent != null ? selectedComponent.getBounds() : null;
    }

    @Override
    public Image getBackgroundImage(FluidSwipeEvent e) {
        var img = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB);
        final var co = img.createGraphics();

        Rectangle clipRect = co.getClipBounds();
        int clipX, clipY, clipW, clipH;
        if (clipRect == null) {
            clipX = clipY = 0;
            clipW = getWidth();
            clipH = getHeight();
        } else {
            clipX = clipRect.x;
            clipY = clipRect.y;
            clipW = clipRect.width;
            clipH = clipRect.height;
        }
        co.setClip(clipX, clipY, clipW, clipH);

        super.paintComponent(co);
        super.paintBorder(co);
        return img;
    }

    protected final boolean isTabIndexValid(int tabIndex) {
        return 0 <= tabIndex && tabIndex < this.getTabCount();
    }

    @Override
    public void fluidSwipeProgressed(final FluidSwipeEvent e) {
        this.swipeAnimationPainterDelegate.fluidSwipeProgressed(e);
    }

    @Override
    public Image getPageToNavFrom(final FluidSwipeEvent e) {
        // called only when navigation in target direction is possible
        return createComponentImage(this.getComponentAt(e.getLogicalGestureDirection() == FluidSwipeEvent.Direction.LEFT_TO_RIGHT ?
                this.getSelectedIndex() + 1 : this.getSelectedIndex() - 1));
    }

    @Override
    public Image getDestinationPage(final FluidSwipeEvent e) {
        return createComponentImage(this.getSelectedComponent());
    }

    @Override
    public void fluidSwipeBegan(final FluidSwipeEvent e) {
        this.swipeAnimationPainterDelegate = new NavigationAnimPainterDelegate<>(this, this);
        this.performSwipeAction(e.getLogicalGestureDirection());
        this.swipeAnimationPainterDelegate.fluidSwipeBegan(e);
    }

    @Override
    public void fluidSwipeEnded(final FluidSwipeEvent e) {
        this.swipeAnimationPainterDelegate.fluidSwipeEnded(e);
        if (e.getGestureState() != FluidSwipeEvent.State.SUCCESS) {
            this.performSwipeAction(e.getLogicalGestureDirection().opposite()); // undo the previously performed action
        }
        this.swipeAnimationPainterDelegate = null;
    }

    private void performSwipeAction(FluidSwipeEvent.Direction contentDirection) {
        if (contentDirection == FluidSwipeEvent.Direction.LEFT_TO_RIGHT)
            this.navigateBack();
        else this.navigateForward();
    }

    @Override
    public void navigateBack() {
        this.setSelectedIndex(this.getSelectedIndex() - 1);
    }

    @Override
    public void navigateForward() {
        this.setSelectedIndex(this.getSelectedIndex() + 1);
    }

    @Override
    public boolean canNavigateBack() {
        return this.isTabIndexValid(this.getSelectedIndex() - 1);
    }

    @Override
    public boolean canNavigateForward() {
        return this.isTabIndexValid(this.getSelectedIndex() + 1);
    }

    @Override
    public void paint(Graphics g) {
        if (this.swipeAnimationDelegateCanPaint()) {
            this.swipeAnimationPainterDelegate.paint(g);
        } else {
            super.paint(g);
        }
    }

    @Override
    public boolean permitFluidSwipeGesture(final FluidSwipeEvent e) {
        if (e.getLogicalGestureDirection() == FluidSwipeEvent.Direction.LEFT_TO_RIGHT)
            return this.canNavigateBack();
        else return this.canNavigateForward();
    }

    private static Image createComponentImage(Component component) {
        if (component == null) return null;
        BufferedImage img = new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_ARGB);
        component.paint(img.getGraphics());
        return img;
    }
}
