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

    public boolean takeItem(Item item) {
        if (currentWeight + item.getWeight() > maxWeight) {
            System.out.println("拿不动了！当前负重：" + String.format("%.1f", currentWeight) +
                    "/" + String.format("%.1f", maxWeight) +
                    "，物品重量：" + String.format("%.1f", item.getWeight()));
            return false;
        }

        inventory.add(item);
        currentWeight += item.getWeight();
        System.out.println("拾取了：" + item.getName());
        System.out.println("当前负重：" + String.format("%.1f", currentWeight) +
                "/" + String.format("%.1f", maxWeight));
        return true;
    }

    public Item dropItem(String itemName) {
        for (int i = 0; i < inventory.size(); i++) {
            Item item = inventory.get(i);
            if (item.getName().equalsIgnoreCase(itemName)) {
                inventory.remove(i);
                currentWeight -= item.getWeight();
                System.out.println("丢弃了：" + item.getName());
                System.out.println("当前负重：" + String.format("%.1f", currentWeight) +
                        "/" + String.format("%.1f", maxWeight));
                return item;
            }
        }
        System.out.println("你没有这个物品：" + itemName);
        return null;
    }

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

    // 添加 eatCookie 方法
    public void eatCookie() {
        // 查找魔法饼干
        boolean hasCookie = false;
        Item cookieToRemove = null;

        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase("cookie")) {
                hasCookie = true;
                cookieToRemove = item;
                break;
            }
        }

        if (!hasCookie) {
            System.out.println("你没有魔法饼干！");
            return;
        }

        // 移除饼干并增加负重
        inventory.remove(cookieToRemove);
        currentWeight -= cookieToRemove.getWeight();
        maxWeight += 5.0; // 固定增加5kg负重
        System.out.println("吃掉了魔法饼干！");
        System.out.println("你的负重能力增加了5kg，现在最大负重：" +
                String.format("%.1f", maxWeight) + "kg");
    }
}