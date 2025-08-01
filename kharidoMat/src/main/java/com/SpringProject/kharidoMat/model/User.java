package com.SpringProject.kharidoMat.model;

import java.util.HashSet;
import java.util.Set;

import com.SpringProject.kharidoMat.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String fullName;

    @Column(unique = true)
    private String email;
    
    @Column(nullable = true)
    private String password;

    private String studentId;
    
    private boolean verified = false;          
    
    @Column
    private String phone;

    @Column
    private String prn;

    @Column
    private String academicYear;
    
    @Enumerated(EnumType.STRING)
    private Role role;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    
    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    
    public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}


	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPrn() {
		return prn;
	}

	public void setPrn(String prn) {
		this.prn = prn;
	}

	public String getAcademicYear() {
		return academicYear;
	}

	public void setAcademicYear(String academicYear) {
		this.academicYear = academicYear;
	}




	@ManyToMany
    @JoinTable(name = "user_wishlist",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "item_id"))
    @JsonIgnoreProperties("user")
    private Set<Item> wishlist = new HashSet<>();

    public Set<Item> getWishlist() {
        return wishlist;
    }

    public void setWishlist(Set<Item> wishlist) {
        this.wishlist = wishlist;
    }

    
}
