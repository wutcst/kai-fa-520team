package cn.edu.whut.sept.zuul.Command;

/**
 * 该类表示用户输入的命令。
 * 将用户输入封装为命令词和参数两部分，便于后续处理。
 *
 * @author Michael Kölling and David J. Barnes
 * @version 1.0
 */
public class Command {
    private String commandWord;  // 命令词，如"go"、"help"、"quit"
    private String secondWord;   // 命令参数，如方向、物品名称等

    /**
     * 创建命令对象。
     *
     * @param firstWord  命令词，如果为null表示未知命令
     * @param secondWord 命令参数，可能为null表示无参数
     */
    public Command(String firstWord, String secondWord) {
        commandWord = firstWord;
        this.secondWord = secondWord;
    }

    /**
     * 获取命令词。
     *
     * @return 命令词，可能为null（表示未知命令）
     */
    public String getCommandWord() {
        return commandWord;
    }

    /**
     * 获取命令参数。
     *
     * @return 命令参数，可能为null（表示无参数）
     */
    public String getSecondWord() {
        return secondWord;
    }

    /**
     * 判断是否为未知命令。
     * 通过检查commandWord是否为null来判断。
     *
     * @return 如果命令词为null则返回true，表示未知命令；否则返回false
     */
    public boolean isUnknown() {
        return (commandWord == null);
    }

    /**
     * 判断命令是否包含参数。
     * 通过检查secondWord是否为null来判断。
     *
     * @return 如果参数不为null则返回true，表示命令包含参数；否则返回false
     */
    public boolean hasSecondWord() {
        return (secondWord != null);
    }
}