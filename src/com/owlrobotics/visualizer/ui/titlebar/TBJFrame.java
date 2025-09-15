package com.owlrobotics.visualizer.ui.titlebar;

import com.owlrobotics.visualizer.util.XYLayout;
import com.owlrobotics.visualizer.ui.titlebar.controls.TBCloseButton;
import com.owlrobotics.visualizer.ui.titlebar.controls.TBDragButton;
import com.owlrobotics.visualizer.ui.titlebar.controls.TBMinimizeButton;
import com.owlrobotics.visualizer.ui.titlebar.controls.TBRestoreButton;
import com.owlrobotics.visualizer.ui.titlebar.icon.TBIconPanel;
import com.owlrobotics.visualizer.ui.titlebar.menu.TBMenuBar;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;
import com.owlrobotics.visualizer.ui.titlebar.win.WindowFrameType;

import javax.swing.*;
import java.awt.*;

public class TBJFrame extends JFrame {

    final WindowFrameType windowFrameType;
    private JPanel titleBarPane, customContentContainer, frameContentPane, iconContainer, controlContainer;
    private TBIconPanel iconPanel;
    private TBRestoreButton restoreButton;
    private TBMinimizeButton minimizeButton;
    private TBCloseButton closeButton;
    private TBDragButton dragButton;
    private Theme theme;
    private TBMenuBar menuBar;

    public TBJFrame(String title, WindowFrameType windowFrameType, Theme theme, int logoSize, boolean resizable){
        super(title);
        this.theme = theme;
        this.windowFrameType = windowFrameType;
        setLayout(new XYLayout());
        setResizable(resizable);
        setUndecorated(true);

        // Creating the default content pane back.
        frameContentPane = new JPanel();
        frameContentPane.setLayout(new BorderLayout());
        frameContentPane.setOpaque(false);

        // Adding the title bar to the frame.
        if (windowFrameType == WindowFrameType.NONE){
            CustomDecorationParameters.setTitleBarHeight(0);
        } else {
            titleBarPane = new JPanel();
            titleBarPane.setBackground(theme.getTitleBarColorBackground);
            titleBarPane.setLayout(new BorderLayout());

            iconContainer = new JPanel();
            iconContainer.setOpaque(false);
            iconContainer.add(iconPanel = new TBIconPanel(null, logoSize));


            controlContainer = new JPanel();
            controlContainer.setOpaque(false);
            if(windowFrameType == WindowFrameType.NORMAL){
                controlContainer.setLayout(new GridLayout(1, 0, 0, 0));
                controlContainer.add(minimizeButton = new TBMinimizeButton(this));
                if (isResizable()) {
                    controlContainer.add(restoreButton = new TBRestoreButton(this));
                }
                controlContainer.add(closeButton = new TBCloseButton(this));
            } else if(windowFrameType == WindowFrameType.TOOL){
                controlContainer.setLayout(new GridLayout(1, 1, -1, 0));
                controlContainer.add(new TBCloseButton(this));
            }
        }

        titleBarPane.add(controlContainer, BorderLayout.EAST);

        customContentContainer = new JPanel();
        customContentContainer.setLayout(new FlowLayout(FlowLayout.LEADING,0,0));
        customContentContainer.setOpaque(false);
        customContentContainer.add(iconContainer);
        customContentContainer.add(menuBar = new TBMenuBar(this));

        titleBarPane.add(customContentContainer, BorderLayout.WEST);
        titleBarPane.add(dragButton = new TBDragButton(this));
        frameContentPane.add(titleBarPane, BorderLayout.NORTH);

        JPanel clientContentPane = new JPanel();
        clientContentPane.setLayout(new FlowLayout());
        clientContentPane.setOpaque(false);
        frameContentPane.add(clientContentPane);

        setContentPane(frameContentPane);

        CustomDecorationParameters.setControlBoxWidth(controlContainer.getWidth() + 7);
    }

    public void setTitleBarIcon(Image image){
        iconPanel.setIcon(image);
    }

    public void setTaskBarIcon(Image image) {
        setIconImage(image);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    @Override
    public void pack() {
        super.pack();
        CustomDecorationParameters.setExtraLeftReservedWidth(customContentContainer.getWidth());
    }

    /**
     * @return the panel containing the icon of the title bar.
     *         This panel is nested inside the iconContainer panel
     */
    public TBIconPanel getIconPanel(){
        return this.iconPanel;
    }


    /**
     * @return the area that is used to add any components such as JMenuBar or JButton for instance.
     *          This zone is located right next to the icon container.
     */
    public JPanel getCustomAreaPanel(){
        return this.customContentContainer;
    }

    /**
     * @return the JPanel containing the control button(s), which are the minimize, restore and close buttons.
     *          This component is located on the right of the title bar ny default.
     */
    public JPanel getControlContainer(){
        return this.controlContainer;
    }

    /**
     * @return the JPanel representing the title bar itself.
     *          All the other components of the bar sit on this JPanel.
     */
    public JPanel getTitleBarPane(){
        return this.titleBarPane;
    }

    /**
     * @return The theme of the title bar/frame.
     */
    public Theme getTheme(){
        return this.theme;
    }

    /**
     * @return the instance of the button that closes the window.
     *          This button should not be null.
     */
    public TBCloseButton getCloseButton(){
        return this.closeButton;
    }

    /**
     * @return the instance of the minimize button.
     *          This button can be null if the type of window does not support it.
     */
    public TBMinimizeButton getMinimizeButton(){
        return this.minimizeButton;
    }

    /**
     * @return the instance of the restore button.
     *          This button can be null if the type of the window does not support it.
     */
    public TBRestoreButton getRestoreButton(){
        return this.restoreButton;
    }

}
