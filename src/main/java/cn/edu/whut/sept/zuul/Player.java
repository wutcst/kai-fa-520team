package cn.edu.whut.sept.zuul;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Player {
    private String name;
    private Room currentRoom;
    private List<Item> inventory;
    private double maxWeight;
    private double currentWeight;

    public Player(String name, Room startingRoom) {
        this.name = name;
        this.currentRoom = startingRoom;
        this.inventory = new ArrayList<>();
        this.maxWeight = 10.0; // 初始最大负重10kg
        this.currentWeight = 0.0;
    }

    // 添加缺失的 getMaxWeight() 方法
    public double getMaxWeight() {
        return maxWeight;
    }

    // 添加 getCurrentWeight() 方法，因为 LookCommand 也需要它
    public double getCurrentWeight() {
        return currentWeight;
    }

    /**
     * 尝试拾取物品并更新负重。该方法不负责任何输出，调用者应负责提示信息的显示。
     * @param item 要拾取的物品
     * @return 如果拾取成功返回true（并更新inventory与currentWeight），否则返回false
     */
    public boolean takeItem(Item item) {
        if (currentWeight + item.getWeight() > maxWeight) {
            return false;
        }

        inventory.add(item);
        currentWeight += item.getWeight();
        return true;
    }

    /**
     * 从背包中丢弃指定名称的物品。
     * 该方法只修改玩家状态并返回被丢弃的物品对象，调用者负责输出提示信息。
     * @param itemName 要丢弃的物品名称
     * @return 被丢弃的物品对象；如果没有该物品则返回null
     */
    public Item dropItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            if (item.getName().equalsIgnoreCase(itemName)) {
                inventory.remove(i);
                currentWeight -= item.getWeight();
                return item;
            }
        }
        return null;
    }

    /**
     * 输出背包信息到标准输出（展示性方法）。保留此方法以兼容现有命令逻辑。
     */
    public void showInventory() {
        System.out.println("=== " + name + "的背包 ===");
        System.out.println("负重：" + String.format("%.1f", currentWeight) +
                "/" + String.format("%.1f", maxWeight));

        if (inventory.isEmpty()) {
            System.out.println("背包是空的");
        } else {
            System.out.println("物品列表：");
            for (Item item : inventory) {
                System.out.println("  - " + item);
            }
        }
    }

    // Getter和Setter
    public String getName() { return name; }
    // 新增：允许在运行时修改玩家名称（用于 GUI 取名功能）
    public void setName(String name) { this.name = name; }
    public Room getCurrentRoom() { return currentRoom; }
    public void setCurrentRoom(Room room) { this.currentRoom = room; }
    public List<Item> getInventory() { return inventory; }
    public void setMaxWeight(double maxWeight) { this.maxWeight = maxWeight; }

    // 新增：设置当前负重，用于载入存档后重建状态
    public void setCurrentWeight(double currentWeight) { this.currentWeight = currentWeight; }

    // 添加 hasItem 方法，供其他命令使用
    public boolean hasItem(String itemName) {
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    // 添加 eatCookie 方法（不输出，返回是否成功）
    public boolean eatCookie() {
        // 查找魔法饼干
        Item cookieToRemove = null;

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase("cookie")) {
                cookieToRemove = item;
                break;
            }
        }

        if (cookieToRemove == null) {
            return false;
        }

        // 移除饼干并增加负重
        inventory.remove(cookieToRemove);
        currentWeight -= cookieToRemove.getWeight();
        maxWeight += 5.0; // 固定增加5kg负重
        return true;
    }
}