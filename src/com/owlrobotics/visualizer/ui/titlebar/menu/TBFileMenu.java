package com.owlrobotics.visualizer.ui.titlebar.menu;

import com.owlrobotics.visualizer.ControlPanel;
import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;
import com.owlrobotics.visualizer.util.conversion.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TBFileMenu extends TBControlMenu {

    public TBFileMenu(TBJFrame frame) {
        super(frame);
        setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
        setBackground(frame.getTheme().getTitleBarColorBackground);
        setForeground(frame.getTheme().getTextColor);
        setFont(new Font ("SansSerif", Font.PLAIN, 14));

        getPopupMenu().setUI(new BasicPopupMenuUI());
        getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
        getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

        UIManager.put("Separator.background", Theme.menuBorderColor);
        UIManager.put("Separator.highlight", Theme.menuBorderColor);
        UIManager.put("Separator.shadow", Theme.menuBorderColor);
        UIManager.put("Separator.foreground", Theme.menuBorderColor);

        UIManager.put("MenuItem.selectionBackground", Theme.menuSelectedBackgroundColor);
        UIManager.put("MenuItem.selectionForeground", Theme.textColor);

        setText("File");

        add(new Import());
        add(new Export());
        addSeparator();
        add(new Convert());
    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {
        g.setColor(frame.getTheme().getTextColor);

        g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font ("SansSerif", Font.PLAIN, 14));
        g.drawString(getText(), 5, (CustomDecorationParameters.getTitleBarHeight() / 2) + 7);
    }

    class Import extends JMenuItem {

        public Import() {
            setBackground(frame.getTheme().getMenuBackgroundColor);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

            setFont(new Font ("SansSerif", Font.PLAIN, 14));

            setText("Import .txt");

            addActionListener(e -> {
                FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                ControlPanel controlPanel = (ControlPanel) frame.getContentPane().getComponent(2).getComponentAt(fieldPanel.fieldSize + 40, 20);
                try {
                    new ImportText(fieldPanel, controlPanel);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
        }
    }

    class Export extends JMenu {

        public Export() {

            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            UIManager.put("Menu.selectionBackground", Theme.menuSelectedBackgroundColor);
            UIManager.put("Menu.selectionForeground", Theme.textColor);
            UIManager.put("Menu.submenuPopupOffsetX", 2);
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getTitleBarColorBackground);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("Export As");

            add(new ExportJava());
            add(new ExportText());
        }

        class ExportJava extends JMenuItem {

             public ExportJava() {
                 setBackground(frame.getTheme().getMenuBackgroundColor);
                 setForeground(frame.getTheme().getTextColor);
                 setBorder(BorderFactory.createEmptyBorder());

                 setFont(new Font ("SansSerif", Font.PLAIN, 14));

                 setText("Export As .java");

                 addActionListener(e -> {
                     FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                     try {
                         new ExportToJava(fieldPanel);
                     } catch (IOException ex) {
                         throw new RuntimeException(ex);
                     }
                 });
             }
        }
        class ExportText extends JMenuItem {

            public ExportText() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));

                setText("Export As .txt");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        new ExportToText(fieldPanel);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    class Convert extends JMenu {

        public Convert() {

            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0 ,0, 0, 0));
            UIManager.put("Menu.selectionBackground", Theme.menuSelectedBackgroundColor);
            UIManager.put("Menu.selectionForeground", Theme.textColor);
            UIManager.put("Menu.submenuPopupOffsetX", 2);
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getTitleBarColorBackground);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("Convert To");

            add(new ConvertJava());
            add(new ConvertText());
        }

        class ConvertJava extends JMenuItem {

            public ConvertJava() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));

                setText("Convert To Java");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    new ConvertToJava(fieldPanel);
                });
            }
        }
        class ConvertText extends JMenuItem {

            public ConvertText() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));

                setText("Convert To Text");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    new ConvertToText(fieldPanel);
                });
            }
        }
    }
}
