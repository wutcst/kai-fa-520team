package cn.edu.whut.sept.zuul;

/**
 * 物品类，表示游戏中的物品。
 * 每个物品有名称、描述和重量。
 */
public class Item {
    private String name;
    private String description;
    private double weight;

    /**
     * 创建物品对象。
     * @param name 物品名称
     * @param description 物品描述
     * @param weight 物品重量
     */
    public Item(String name, String description, double weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    /**
     * 获取物品名称。
     * @return 物品名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取物品描述。
     * @return 物品描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 获取物品重量。
     * @return 物品重量
     */
    public double getWeight() {
        return weight;
    }

    /**
     * 获取物品的字符串表示。
     * @return 物品的详细描述
     */
    @Override
    public String toString() {
        return name + " (" + description + "，重量：" + String.format("%.1f", weight) + ")";
    }
}