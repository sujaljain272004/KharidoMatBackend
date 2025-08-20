package com.SpringProject.kharidoMat.dto;

public class ReturnRequestDTO {
	private boolean accepted;
	private String notes; // For when the return is rejected due to damage

	// Getters and Setters
	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}
}