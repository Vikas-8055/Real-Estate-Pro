package com.realestate.management.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "property_viewings")
public class PropertyViewing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "viewing_date", nullable = false)
    private LocalDateTime viewingDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ViewingStatus status = ViewingStatus.PENDING;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ViewingStatus {
        PENDING,
        APPROVED,
        REJECTED,
        COMPLETED,
        CANCELLED
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public PropertyViewing() {
    }

    public PropertyViewing(Property property, User user, LocalDateTime viewingDate, String message) {
        this.property = property;
        this.user = user;
        this.viewingDate = viewingDate;
        this.message = message;
        this.status = ViewingStatus.PENDING;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Property getProperty() {
        return property;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getViewingDate() {
        return viewingDate;
    }

    public void setViewingDate(LocalDateTime viewingDate) {
        this.viewingDate = viewingDate;
    }

    public ViewingStatus getStatus() {
        return status;
    }

    public void setStatus(ViewingStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}