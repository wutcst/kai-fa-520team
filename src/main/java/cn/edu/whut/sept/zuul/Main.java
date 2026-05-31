package cn.edu.whut.sept.zuul;

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
     * 创建Game对象并调用其play方法启动游戏。
     *
     * @param args 命令行参数（本程序中未使用）
     */
    public static void main(String[] args) {
        Game game = new Game();  // 创建游戏对象
        game.play();  // 启动游戏
    }
}