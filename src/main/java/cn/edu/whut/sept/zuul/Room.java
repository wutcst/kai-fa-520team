package cn.edu.whut.sept.zuul;

import java.util.HashMap;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * 该类表示游戏中的一个房间。
 * 每个房间有描述信息、出口和物品。
 * 玩家可以在房间之间移动并收集物品。
 *
 * @author Michael Kölling and David J. Barnes
 * @version 2.0 (添加物品系统)
 */
public class Room {
    private String description;
    private HashMap<String, Room> exits;
    private List<Item> items;

    /**
     * 创建房间对象。
     * @param description 房间的描述信息
     */
    public Room(String description) {
        this.description = description;
        exits = new HashMap<>();
        items = new ArrayList<>();
    }

    /**
     * 设置房间的出口。
     * @param direction 出口方向
     * @param neighbor 相邻的房间对象
     */
    public void setExit(String direction, Room neighbor) {
        exits.put(direction, neighbor);
    }

    /**
     * 获取房间的简短描述。
     * @return 房间的描述字符串
     */
    public String getShortDescription() {
        return description;
    }

    /**
     * 获取房间的完整描述。
     * 包括房间描述和所有可用的出口方向。
     * @return 包含房间描述和出口信息的字符串
     */
    public String getLongDescription() {
        String longDesc = "You are " + description + ".\n" + getExitString();

        // 添加物品信息
        if (!items.isEmpty()) {
            longDesc += "\n你可以看到:";
            for (Item item : items) {
                longDesc += "\n  - " + item.getName() + " (" + item.getDescription() +
                        "，重量：" + String.format("%.1f", item.getWeight()) + ")";
            }
        }

        return longDesc;
    }

    /**
     * 获取房间的所有出口方向。
     * @return 包含所有出口方向的字符串
     */
    private String getExitString() {
        String returnString = "Exits:";
        Set<String> keys = exits.keySet();

        for(String exit : keys) {
            returnString += " " + exit;
        }
        return returnString;
    }

    /**
     * 获取指定方向的出口房间。
     * @param direction 要查询的方向
     * @return 指定方向的相邻房间，如果该方向无出口则返回null
     */
    public Room getExit(String direction) {
        return exits.get(direction);
    }

    /**
     * 向房间添加物品。
     * @param item 要添加的物品
     */
    public void addItem(Item item) {
        items.add(item);
    }

    public Item removeItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            if (item.getName().equalsIgnoreCase(itemName)) {
                return items.remove(i);
            }
        }
        return null;
    }

    /**
     * 获取房间内的所有物品。
     * @return 物品列表
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * 检查房间是否有特定物品。
     * @param itemName 物品名称
     * @return 如果有该物品则返回true，否则返回false
     */
    public boolean hasItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }
}