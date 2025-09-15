package com.owlrobotics.visualizer.ui.titlebar.menu;

import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class TBHelpMenu extends TBControlMenu{

    public TBHelpMenu(TBJFrame frame) {
        super(frame);
        setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
        setBackground(frame.getTheme().getTitleBarColorBackground);
        setForeground(frame.getTheme().getTextColor);
        setFont(new Font ("SansSerif", Font.PLAIN, 14));

        UIManager.put("MenuItem.selectionBackground", Theme.menuSelectedBackgroundColor);
        UIManager.put("MenuItem.selectionForeground", Theme.textColor);

        setText("Help");

        getPopupMenu().setUI(new BasicPopupMenuUI());
        getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
        getPopupMenu().setBackground(frame.getTheme().getTitleBarColorBackground);

        add(new GitHubRepository());
        add(new GitHubIssues());
    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {
        g.setColor(frame.getTheme().getTextColor);

        g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font ("SansSerif", Font.PLAIN, 14));
        g.drawString(getText(), 5, (CustomDecorationParameters.getTitleBarHeight() / 2) + 7);
    }

    class GitHubRepository extends JMenuItem {

        public GitHubRepository() {
            setBackground(frame.getTheme().getMenuBackgroundColor);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder());

            setFont(new Font ("SansSerif", Font.PLAIN, 14));

            setText("GitHub Repository");

            addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/23476-OWL-Robotics/Pedro-Pathing-Visualizer"));
                } catch (IOException | URISyntaxException ex) {
                    System.out.println("Desktop Not Supported");
                    System.out.println("https://github.com/23476-OWL-Robotics/Pedro-Pathing-Visualizer");
                }
            });
        }
    }
    class GitHubIssues extends JMenuItem {

        public GitHubIssues() {
            setBackground(frame.getTheme().getMenuBackgroundColor);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder());

            setFont(new Font ("SansSerif", Font.PLAIN, 14));

            setText("GitHub Issues");

            addActionListener(e -> {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/23476-OWL-Robotics/Pedro-Pathing-Visualizer/issues"));
                } catch (IOException | URISyntaxException ex) {
                    System.out.println("Desktop Not Supported");
                    System.out.println("https://github.com/23476-OWL-Robotics/Pedro-Pathing-Visualizer/issues");
                }
            });
        }
    }
}
