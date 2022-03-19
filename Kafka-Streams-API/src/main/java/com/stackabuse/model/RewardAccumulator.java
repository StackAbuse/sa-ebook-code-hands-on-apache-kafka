package com.stackabuse.model;

public class RewardAccumulator {

    private final String customerName;
    private final double purchaseTotal;
    private int totalRewardPoints;
    private final int currentRewardPoints;

    private RewardAccumulator(String customerName, double purchaseTotal, int rewardPoints) {
        this.customerName = customerName;
        this.purchaseTotal = purchaseTotal;
        this.currentRewardPoints = rewardPoints;
        this.totalRewardPoints = rewardPoints;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getPurchaseTotal() {
        return purchaseTotal;
    }

    public int getTotalRewardPoints() {
        return totalRewardPoints;
    }

    public void addRewardPoints(int previousTotalPoints) {
        this.totalRewardPoints += previousTotalPoints;
    }

    @Override
    public String toString() {
        return "RewardAccumulator{" +
                "customerName='" + customerName + '\'' +
                ", purchaseTotal=" + purchaseTotal +
                ", totalRewardPoints=" + totalRewardPoints +
                ", currentRewardPoints=" + currentRewardPoints +
                '}';
    }

    public static Builder builder(Transaction transaction){return new Builder(transaction);}

    public static final class Builder {
        private final String customerName;
        private final double purchaseTotal;
        private final int rewardPoints;

        private Builder(Transaction transaction){
            this.customerName = transaction.getLastName() + "," + transaction.getFirstName();
            this.purchaseTotal = transaction.getPrice() * transaction.getQuantity();
            this.rewardPoints = (int) purchaseTotal;
        }


        public RewardAccumulator build(){
            return new RewardAccumulator(customerName, purchaseTotal, rewardPoints);
        }

    }
}
