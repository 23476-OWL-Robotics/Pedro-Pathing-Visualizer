package com.owlrobotics.visualizer.ui.titlebar.menu;

import com.owlrobotics.visualizer.FieldPanel;
import com.owlrobotics.visualizer.ui.theme.Theme;
import com.owlrobotics.visualizer.ui.titlebar.TBJFrame;
import com.owlrobotics.visualizer.ui.titlebar.win.CustomDecorationParameters;
import com.owlrobotics.visualizer.util.enums.Backgrounds;
import com.owlrobotics.visualizer.util.enums.PlaneOrigin;

import javax.swing.*;
import javax.swing.plaf.basic.BasicMenuUI;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.io.IOException;

public class TBViewMenu extends TBControlMenu {

    public TBViewMenu(TBJFrame frame) {
        super(frame);
        setBorder(BorderFactory.createEmptyBorder(5,2,5,2));
        setBackground(frame.getTheme().getTitleBarColorBackground);
        setForeground(frame.getTheme().getTextColor);
        setFont(new Font ("SansSerif", Font.PLAIN, 14));

        UIManager.put("MenuItem.selectionBackground", Theme.menuSelectedBackgroundColor);
        UIManager.put("MenuItem.selectionForeground", Theme.textColor);

        getPopupMenu().setUI(new BasicPopupMenuUI());
        getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
        getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

        setText("View");

        add(new FieldImage());
        add(new FieldRotation());
        add(new FieldOrigin());
    }

    @Override
    public void draw(Graphics2D g, Point p1, Point p2) {
        g.setColor(frame.getTheme().getTextColor);

        g.setStroke(new BasicStroke(1.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString(getText(), 5, (CustomDecorationParameters.getTitleBarHeight() / 2) + 7);
    }

    class FieldImage extends JMenu {
         public FieldImage() {
             setBackground(frame.getTheme().getTitleBarColorBackground);
             setForeground(frame.getTheme().getTextColor);
             setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
             UIManager.put("Menu.selectionBackground", Theme.menuSelectedBackgroundColor);
             UIManager.put("Menu.selectionForeground", Theme.textColor);
             UIManager.put("Menu.submenuPopupOffsetX", 2);
             setUI(new BasicMenuUI());

             getPopupMenu().setUI(new BasicPopupMenuUI());
             getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
             getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

             setFont(new Font ("SansSerif", Font.PLAIN, 14));
             setText("Field Image");

             add(new PowerPlay());
             add(new CenterStage());
             add(new IntoTheDeep());
             add(new Decode());
         }
    }

    class PowerPlay extends JMenu {

        public PowerPlay() {
            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("PowerPlay");

            add(new PowerPlayDark());
            add(new PowerPlayLight());
        }

        class PowerPlayDark extends JMenuItem {

            public PowerPlayDark() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("PowerPlay Dark");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.PowerPlay_Dark
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
        class PowerPlayLight extends JMenuItem {

            public PowerPlayLight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("PowerPlay Light");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.PowerPlay_Light
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    class CenterStage extends JMenu {

        public CenterStage() {
            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("CenterStage");

            add(new CenterStageDark());
            add(new CenterStageLight());
        }

        class CenterStageDark extends JMenuItem {

            public CenterStageDark() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("CenterStage Dark");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.CenterStage_DARK
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
        class CenterStageLight extends JMenuItem {

            public CenterStageLight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("CenterStage Light");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.CenterStage_Light
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    class IntoTheDeep extends JMenu {

        public IntoTheDeep() {
            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("Into The Deep");

            add(new IntoTheDeepDark());
            add(new IntoTheDeepLight());
        }

        class IntoTheDeepDark extends JMenuItem {

            public IntoTheDeepDark() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Into the Deep Dark");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.IntoTheDeep_DARK
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
        class IntoTheDeepLight extends JMenuItem {

            public IntoTheDeepLight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Into the Deep Light");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.IntoTheDeep_LIGHT
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }
    class Decode extends JMenu {

        public Decode() {
            setBackground(frame.getTheme().getTitleBarColorBackground);
            setForeground(frame.getTheme().getTextColor);
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
            setUI(new BasicMenuUI());

            getPopupMenu().setUI(new BasicPopupMenuUI());
            getPopupMenu().setBorder(BorderFactory.createLineBorder(Theme.menuBorderColor, 2));
            getPopupMenu().setBackground(frame.getTheme().getMenuBackgroundColor);

            setFont(new Font ("SansSerif", Font.PLAIN, 14));
            setText("Decode");

            add(new DecodeDark());
            add(new DecodeLight());
        }

        class DecodeDark extends JMenuItem {

            public DecodeDark() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Decode Dark");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.Decode_DARK
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
        class DecodeLight extends JMenuItem {

            public DecodeLight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Decode Light");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    try {
                        fieldPanel.fieldImg = Backgrounds.Decode_LIGHT
                                .getImage()
                                .getScaledInstance(fieldPanel.fieldSize, fieldPanel.fieldSize, Image.SCALE_SMOOTH);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        }
    }

    class FieldRotation extends JMenu {

        public FieldRotation() {

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
            setText("Field Rotation");

            add(new Rotation0());
            add(new Rotation90());
            add(new Rotation180());
            add(new Rotation270());
        }

        class Rotation0 extends JMenuItem {

            public Rotation0() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("0 Degrees");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.fieldRotation = Math.toRadians(0);
                });
            }
        }
        class Rotation90 extends JMenuItem {

            public Rotation90() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("90 Degrees");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.fieldRotation = Math.toRadians(90);
                });
            }
        }
        class Rotation180 extends JMenuItem {

            public Rotation180() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("180 Degrees");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.fieldRotation = Math.toRadians(180);
                });
            }
        }
        class Rotation270 extends JMenuItem {

            public Rotation270() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("270 Degrees");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.fieldRotation = Math.toRadians(270);
                });
            }
        }
    }

    class FieldOrigin extends JMenu {

        public FieldOrigin() {

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
            setText("Field Origin");

            add(new Center());
            add(new BottomLeft());
            add(new BottomRight());
            add(new TopLeft());
            add(new TopRight());
        }

        class Center extends JMenuItem {

            public Center() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Center");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.setOrigin(PlaneOrigin.CENTER);
                });
            }
        }
        class BottomLeft extends JMenuItem {

            public BottomLeft() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Bottom Left");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.setOrigin(PlaneOrigin.BOTTOM_LEFT);
                });
            }
        }
        class BottomRight extends JMenuItem {

            public BottomRight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Bottom Right");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.setOrigin(PlaneOrigin.BOTTOM_RIGHT);
                });
            }
        }
        class TopLeft extends JMenuItem {

            public TopLeft() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Top Left");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.setOrigin(PlaneOrigin.TOP_LEFT);
                });
            }
        }
        class TopRight extends JMenuItem {

            public TopRight() {
                setBackground(frame.getTheme().getMenuBackgroundColor);
                setForeground(frame.getTheme().getTextColor);
                setBorder(BorderFactory.createEmptyBorder());

                setFont(new Font ("SansSerif", Font.PLAIN, 14));
                setText("Top Right");

                addActionListener(e -> {
                    FieldPanel fieldPanel = (FieldPanel) frame.getContentPane().getComponent(2).getComponentAt(20, 20);
                    fieldPanel.setOrigin(PlaneOrigin.TOP_RIGHT);
                });
            }
        }
    }
}
