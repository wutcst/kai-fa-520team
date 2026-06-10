package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.gui.GameFrame;

import javax.swing.*;

/**
 * "World-of-Zuul"应用程序的主类。
 * 包含程序的main方法，是应用程序的入口点。
 * 该类创建Game对象并启动游戏。
 *
 * @author Michael Kölling and David J. Barnes
 * @version 1.0
 */
public class Main {

    /**
     * 应用程序的主入口方法。
     * 默认启动 GUI；如果传入 --console 参数则使用原控制台模式。
     *
     * @param args 命令行参数（本程序中未使用）
     */
    public static void main(String[] args) {
        boolean consoleMode = false;
        if (args != null) {
            for (String arg : args) {
                if ("--console".equalsIgnoreCase(arg)) {
                    consoleMode = true;
                    break;
                }
            }
        }

        if (consoleMode) {
            Game game = new Game();
            game.play();
            return;
        }

        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame(new Game());
            frame.setVisible(true);
        });
    }
}