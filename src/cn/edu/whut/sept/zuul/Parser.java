package cn.edu.whut.sept.zuul;

import cn.edu.whut.sept.zuul.Command.Command;
import cn.edu.whut.sept.zuul.Command.CommandWords;

import java.util.Scanner;

/**
 * 命令解析器。
 * 负责从用户输入中解析命令，将用户输入的文本转换为Command对象。
 */
public class Parser {
    private CommandWords commands;
    private Scanner reader;

    /**
     * 创建解析器对象。
     * 初始化命令字典和用户输入读取器。
     */
    public Parser() {
        commands = new CommandWords();
        reader = new Scanner(System.in);
    }

    /**
     * 获取用户输入的命令。
     * @return 解析后的Command对象
     */
    public Command getCommand() {
        String inputLine;
        String word1 = null;
        String word2 = null;

        System.out.print("> ");

        inputLine = reader.nextLine();

        // 分词
        Scanner tokenizer = new Scanner(inputLine);
        if (tokenizer.hasNext()) {
            word1 = tokenizer.next().toLowerCase();
            if (tokenizer.hasNext()) {
                word2 = tokenizer.next().toLowerCase();
                // 支持多单词物品名（如"magic cookie"）
                while (tokenizer.hasNext()) {
                    word2 += " " + tokenizer.next().toLowerCase();
                }
            }
        }

        tokenizer.close();

        if (commands.isCommand(word1)) {
            return new Command(word1, word2);
        } else {
            return new Command(null, word2);
        }
    }

    /**
     * 显示所有可用的命令。
     */
    public void showCommands() {
        commands.showAll();
    }

    /**
     * 获取输入读取器。
     * @return Scanner对象
     */
    public Scanner getReader() {
        return reader;
    }
}