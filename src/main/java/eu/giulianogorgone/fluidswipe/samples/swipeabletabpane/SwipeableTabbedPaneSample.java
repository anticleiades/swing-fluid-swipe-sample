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
import eu.giulianogorgone.fluidswipe.components.impl.FluidSwipeAwareJScrollPane;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 *
 * @author Giuliano Gorgone (anticleiades)
 */
public final class SwipeableTabbedPaneSample {
    private static final int N_TABS = 5;
    private static final Dimension FRAME_DIMENSION = new Dimension(640, 512);
    private static final String[] SAMPLE_STRINGS = new String[N_TABS];

    private static void loadSamples() {
        for (int i = 0; i < N_TABS; i++) {
            URL resource = SwipeableTabbedPaneSample.class.getResource("tab" + i + ".txt");
            try {
                SAMPLE_STRINGS[i] = Files.readString(Path.of(Objects.requireNonNull(resource).toURI()));
            } catch (final Exception e) {
                SAMPLE_STRINGS[i] = "sample text loading failed for tab " + i;
            }
        }
    }

    private SwipeableTabbedPaneSample() {
        FluidSwipe.startEventMonitoring();
        JFrame frame = new JFrame("SwipeableTabbedPaneSample");
        JTabbedPane tabbedPane = new SwipeableTabbedPane();
        JCheckBox enableGestures = new JCheckBox("fluid-swipe enabled");
        enableGestures.setSelected(true);
        enableGestures.addActionListener((e) -> {
            if (((JCheckBox) e.getSource()).isSelected()) {
                FluidSwipe.startEventMonitoring();
            } else {
                FluidSwipe.stopEventMonitoring();
            }
        });
        frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
        frame.getContentPane().add(enableGestures, BorderLayout.PAGE_END);
        frame.setPreferredSize(FRAME_DIMENSION);
        for (int i = 0; i < N_TABS; i++) {
            JTextPane view = new JTextPane();
            view.getCaret().setBlinkRate(0);
            view.setText(SAMPLE_STRINGS[i]);
            tabbedPane.add("Tab" + i, new FluidSwipeAwareJScrollPane(view));
        }
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(final String[] args) throws InterruptedException, InvocationTargetException {
        loadSamples();
        SwingUtilities.invokeAndWait(SwipeableTabbedPaneSample::new);
    }
}