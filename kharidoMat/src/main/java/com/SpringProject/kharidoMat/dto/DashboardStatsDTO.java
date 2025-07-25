package com.SpringProject.kharidoMat.dto;

public class DashboardStatsDTO {
	private int totalBookingsMade;
	private double totalAmountSpent;
	private int totalListingsOwned;
	private double totalAmountEarned;
	private int totalWishlistItems;
	public int getTotalBookingsMade() {
		return totalBookingsMade;
	}
	public void setTotalBookingsMade(int totalBookingsMade) {
		this.totalBookingsMade = totalBookingsMade;
	}
	public double getTotalAmountSpent() {
		return totalAmountSpent;
	}
	public void setTotalAmountSpent(double totalAmountSpent) {
		this.totalAmountSpent = totalAmountSpent;
	}
	public int getTotalListingsOwned() {
		return totalListingsOwned;
	}
	public void setTotalListingsOwned(int totalListingsOwned) {
		this.totalListingsOwned = totalListingsOwned;
	}
	public double getTotalAmountEarned() {
		return totalAmountEarned;
	}
	public void setTotalAmountEarned(double totalAmountEarned) {
		this.totalAmountEarned = totalAmountEarned;
	}
	public int getTotalWishlistItems() {
		return totalWishlistItems;
	}
	public void setTotalWishlistItems(int totalWishlistItems) {
		this.totalWishlistItems = totalWishlistItems;
	}
	public DashboardStatsDTO(int totalBookingsMade, double totalAmountSpent, int totalListingsOwned,
			double totalAmountEarned, int totalWishlistItems) {
		super();
		this.totalBookingsMade = totalBookingsMade;
		this.totalAmountSpent = totalAmountSpent;
		this.totalListingsOwned = totalListingsOwned;
		this.totalAmountEarned = totalAmountEarned;
		this.totalWishlistItems = totalWishlistItems;
	}
	public DashboardStatsDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
