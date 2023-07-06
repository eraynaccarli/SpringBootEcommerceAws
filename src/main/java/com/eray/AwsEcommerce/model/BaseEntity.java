package com.eray.AwsEcommerce.model;

import org.springframework.cglib.core.Local;
import java.time.LocalDateTime;

public class BaseEntity {
   private LocalDateTime createdDate;
   private LocalDateTime updatedDate;

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
}

